package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.inventory.GuiHandler;
import zmaster587.advancedRocketry.tile.TileAtmosphereDetector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneEmitter extends Block {

	IIcon activeIcon;
	String iconName;
	
	public BlockRedstoneEmitter(Material material,String activeIconName) {
		super(material);
		iconName = activeIconName;
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		player.openGui(AdvancedRocketry.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), world, x, y, z);
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileAtmosphereDetector();
	}
	
	@Override
	public void registerBlockIcons(IIconRegister icon) {
		super.registerBlockIcons(icon);
		
		activeIcon = icon.registerIcon(iconName);
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		return meta == 1 ? activeIcon : super.getIcon(side, meta);
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess world,
			int x, int y, int z, int side) {
		return world.getBlockMetadata(x, y, z) == 1 ? 15 : 0;
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x,
			int y, int z, int p_149709_5_) {
		return world.getBlockMetadata(x, y, z) == 1 ? 15 : 0;
	}
	
	@Override
	public boolean canProvidePower() {
		return true;
	}

}
