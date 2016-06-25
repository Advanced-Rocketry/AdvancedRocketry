package zmaster587.advancedRocketry.block.multiblock;

import java.util.List;
import java.util.Random;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.inventory.GuiHandler;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.data.TileDataBus;
import zmaster587.advancedRocketry.tile.multiblock.TileFluidHatch;
import zmaster587.libVulpes.tile.TilePointer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockHatch extends BlockMultiblockStructure {

	IIcon output, data, satellite, fluidInput, fluidOutput;
	
	private final Random random = new Random();
	
	public BlockHatch(Material material) {
		super(material);
		isBlockContainer = true;
	}
	
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta & 7;
	}

	
	@Override
	public int isProvidingWeakPower(IBlockAccess world,
			int x, int y, int z, int dir) {
		ForgeDirection direction = ForgeDirection.getOrientation(dir);
		boolean isPointer = world.getTileEntity(x - direction.offsetX , y- direction.offsetY, z - direction.offsetZ) instanceof TilePointer;
		if(isPointer)
			isPointer = isPointer && !((TilePointer)world.getTileEntity(x - direction.offsetX , y- direction.offsetY, z- direction.offsetZ)).hasMaster();
		
		
		return !isPointer && (world.getBlockMetadata(x, y, z) & 8) != 0 ? 15 : 0;
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	public void setRedstoneState(World world, int x, int y, int z, boolean state) {
		if(state && (world.getBlockMetadata(x, y, z) & 8) == 0) {
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) | 8, 3);
		}
		else if(!state && (world.getBlockMetadata(x, y, z) & 8) != 0) {
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) & 7, 3);
		}
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		output = iconRegister.registerIcon("advancedrocketry:outputHatch");
		blockIcon = iconRegister.registerIcon("advancedrocketry:inputHatch");
		data = iconRegister.registerIcon("advancedrocketry:dataHatch");
		satellite = iconRegister.registerIcon("advancedrocketry:satelliteBay");
		fluidInput = iconRegister.registerIcon("advancedrocketry:fluidInput");
		fluidOutput = iconRegister.registerIcon("advancedrocketry:fluidOutput");
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if((meta & 7) == 0) {
			return blockIcon;
		}else if((meta & 7) == 1 ) {
			return output;
		}
		else if((meta & 7) == 2 )
			return data;
		else if((meta & 7) == 3 )
			return satellite;
		else if((meta & 7) == 4 )
			return fluidInput;
		else
			return fluidOutput;
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 4));
		list.add(new ItemStack(item, 1, 5));
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		//TODO: multiple sized Hatches
		if((metadata & 7) == 0)
			return new TileInputHatch(4);
		else if((metadata & 7) == 1)
			return new TileOutputHatch(4);
		else if((metadata & 7) == 2)
			return new TileDataBus(1);
		else if((metadata & 7) == 3)
			return new TileSatelliteHatch(1);
		else if((metadata & 7) == 4)
			return new TileFluidHatch(false);	
		else if((metadata & 7) == 5)
			return new TileFluidHatch(true);	
		
		return null;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile != null && tile instanceof IInventory) {
			IInventory inventory = (IInventory)tile;
			for(int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				
				if(stack == null)
					continue;
				
				EntityItem entityitem = new EntityItem(world, x, y, z, stack);
				
				float mult = 0.05F;
				
                entityitem.motionX = (double)((float)this.random.nextGaussian() * mult);
                entityitem.motionY = (double)((float)this.random.nextGaussian() * mult + 0.2F);
                entityitem.motionZ = (double)((float)this.random.nextGaussian() * mult);
                
                world.spawnEntityInWorld(entityitem);
			}
		}
		
		super.breakBlock(world, x, y, z, block, meta);
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
		ForgeDirection direction = ForgeDirection.getOrientation(side);
		boolean isPointer = access.getTileEntity(x - direction.offsetX , y- direction.offsetY, z - direction.offsetZ) instanceof TilePointer;
		if(isPointer)
			isPointer = isPointer && !((TilePointer)access.getTileEntity(x - direction.offsetX , y- direction.offsetY, z- direction.offsetZ)).hasMaster();
		return ( isPointer || access.getBlockMetadata(x - direction.offsetX, y- direction.offsetY, z - direction.offsetZ) < 8);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int arg1, float arg2, float arg3,
			float arg4) {
		
		int meta = world.getBlockMetadata(x, y, z);
		//Handlue gui through modular system
		if((meta & 7) < 6 )
			player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.MODULAR.ordinal(), world, x, y, z);
		
		return true;
	}
}
