package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.IRocketEngine;
import zmaster587.libVulpes.block.RotatableBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRocketMotor extends RotatableBlock implements IRocketEngine {

	public BlockRocketMotor(Material mat) {
		super(mat);	
	}
	
	 @Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int getThrust(World world, BlockPos pos) {
		return 10;
	}

	@Override
	public int getFuelConsumptionRate(World world, int x, int y, int z) {
		return 1;
	}
}
