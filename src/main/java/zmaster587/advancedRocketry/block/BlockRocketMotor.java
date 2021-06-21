package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.IRocketEngine;
import zmaster587.libVulpes.block.BlockFullyRotatable;

import javax.annotation.Nonnull;

public class BlockRocketMotor extends BlockFullyRotatable implements IRocketEngine {

	public BlockRocketMotor(Properties mat) {
		super(mat);	
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.DOWN));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

		world.setBlockState(pos, state.with(FACING,Direction.DOWN), 2);
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
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(FACING, Direction.DOWN);
	}
	
	@Override
<<<<<<< HEAD
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return VoxelShapes.empty();
=======
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
		
		world.setBlockState(pos, state.withProperty(FACING, EnumFacing.DOWN));
>>>>>>> origin/feature/nuclearthermalrockets
	}
}
