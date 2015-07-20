package zmaster587.advancedRocketry.block.multiblock;

import java.util.List;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockHatch extends BlockMultiblockStructure {

	IIcon output;
	
	public BlockHatch() {
		super();
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
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		output = iconRegister.registerIcon("advancedrocketry:outputHatch");
		blockIcon = iconRegister.registerIcon("advancedrocketry:inputHatch");
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if((meta & 7) == 0) {
			return blockIcon;
		}else {
			return output;
		}
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		if((metadata & 1) == 0)
			return new TileInputHatch(4);
		else if((metadata & 1) == 1)
			return new TileOutputHatch(4);
		
		
		return null;
	}


	
	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int arg1, float arg2, float arg3,
			float arg4) {
		player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.Hatch.ordinal(), world, x, y, z);
		return true;
	}
}
