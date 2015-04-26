package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.IRocketEngine;
import zmaster587.advancedRocketry.tile.TileModelRender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRocketMotor extends Block implements IRocketEngine {

	public BlockRocketMotor(Material mat) {
		super(mat);
		
		
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileModelRender(TileModelRender.models.ROCKET.ordinal());
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
		return 100;
	}

	@Override
	public int getFuelConsumptionRate(World world, int x, int y, int z) {
		return 10;
	}
}
