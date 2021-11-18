package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * InvisLight source
 *
 */
public class BlockLightSource extends Block {

	public BlockLightSource(Properties properties) {
		super(properties);
		properties.setLightLevel(value -> 1);
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		return true;
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return 15;
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}
}