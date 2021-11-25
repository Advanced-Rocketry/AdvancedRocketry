package zmaster587.advancedRocketry.block.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.entity.EntityDummy;
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisibleAlphaTexture;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class BlockSolarArrayPanel extends BlockMultiBlockComponentVisibleAlphaTexture {

	private static VoxelShape bb = VoxelShapes.create(0, 0.375, 0, 1, 0.625, 1);

	public BlockSolarArrayPanel(Properties mat) {
		super(mat);
	}
	
	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return bb;
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return bb;
	}
}
