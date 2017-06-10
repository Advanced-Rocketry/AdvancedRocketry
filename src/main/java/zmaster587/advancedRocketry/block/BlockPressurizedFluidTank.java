package zmaster587.advancedRocketry.block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidContainerItem;

public class BlockPressurizedFluidTank extends Block {

	IIcon top;

	public BlockPressurizedFluidTank(Material material) {
		super(material);
		this.setBlockTextureName("advancedrocketry:liquidTank");
		this.setBlockBounds(0.05f, 0, 0.05f, 0.95f, 1, 0.95f);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		this.setBlockBounds(0.05f, 0, 0.05f, 0.95f, 1, 0.95f);
		return true;
	}


	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
			int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}

	@Override
	public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_,
			int p_149749_4_, Block p_149749_5_, int p_149749_6_) {
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x,
			int y, int z, boolean willHarvest) {
		if(!world.isRemote && !player.capabilities.isCreativeMode) {
			TileEntity tile = world.getTileEntity(x,y,z);

			if(tile != null && tile instanceof TileFluidTank) {
				TileFluidTank fluid = ((TileFluidTank)tile);


				ItemStack itemstack = new ItemStack(AdvancedRocketryBlocks.blockPressureTank);
				IFluidContainerItem fluidItem = (IFluidContainerItem) itemstack.getItem();

				fluidItem.fill(itemstack,fluid.drain(ForgeDirection.DOWN, Integer.MAX_VALUE, false), true);

				EntityItem entityitem;

				float f = world.rand.nextFloat() * 0.8F + 0.1F;
				float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
				float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

				itemstack.stackSize = 1;
				entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), 1, 0));
				float f3 = 0.05F;
				entityitem.motionX = (double)((float)world.rand.nextGaussian() * f3);
				entityitem.motionY = (double)((float)world.rand.nextGaussian() * f3 + 0.2F);
				entityitem.motionZ = (double)((float)world.rand.nextGaussian() * f3);

				if (itemstack.hasTagCompound())
				{
					entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
				}
				world.spawnEntityInWorld(entityitem);
			}
		}
		
		return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}

	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int p_149727_6_, float p_149727_7_, float p_149727_8_,
			float p_149727_9_) {


		if(!world.isRemote)
			player.openGui(LibVulpes.instance, guiId.MODULAR.ordinal(), world, x, y, z);
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);

		top = reg.registerIcon("advancedrocketry:machineGeneric");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int dir, int meta) {

		ForgeDirection side = ForgeDirection.getOrientation(dir);
		if(side.offsetY != 0)
			return top;
		return super.getIcon(dir, meta);
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileFluidTank((int) (64000*Math.pow(2,metadata)));
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world,
			int x, int y, int z, int side) {


		if(ForgeDirection.values()[side].offsetY != 0) {
			if(world.getBlock(x, y, z) == this)
				return false;
		}

		return super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public void onNeighborBlockChange(World world, int x,
			int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockNormalCube() {
		return false;
	}
}
