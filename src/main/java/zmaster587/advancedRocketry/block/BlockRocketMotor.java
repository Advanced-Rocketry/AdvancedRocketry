package zmaster587.advancedRocketry.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import zmaster587.advancedRocketry.api.IRocketEngine;
import zmaster587.advancedRocketry.tile.TileModelRender;
import zmaster587.advancedRocketry.tile.TileModelRenderRotatable;
import zmaster587.libVulpes.util.ZUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRocketMotor extends BlockRotatableModel implements IRocketEngine {

	public BlockRocketMotor(Material mat) {
		super(mat,TileModelRender.models.ROCKET.ordinal());	
	}
	
	protected BlockRocketMotor(Material mat, int i) {
		super(mat,i);	
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {return false;}

	@Override
	public int getThrust(World world, int x, int y, int z) {
		return 10;
	}

	@Override
	public int getFuelConsumptionRate(World world, int x, int y, int z) {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister reg)
	{
		//Not needed
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta)
	{
		return Blocks.iron_block.getIcon(side, meta);
	}
}
