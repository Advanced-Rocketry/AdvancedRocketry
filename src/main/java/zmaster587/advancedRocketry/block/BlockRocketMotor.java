package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.IRocketEngine;
import zmaster587.libVulpes.block.BlockFullyRotatable;

import org.jetbrains.annotations.NotNull;

public class BlockRocketMotor extends BlockFullyRotatable implements IRocketEngine {

	public BlockRocketMotor(Material mat) {
		super(mat);	
		this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.DOWN));
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
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, @NotNull ItemStack stack) {
		
		world.setBlockState(pos, state.withProperty(FACING, EnumFacing.DOWN));
	}
}
