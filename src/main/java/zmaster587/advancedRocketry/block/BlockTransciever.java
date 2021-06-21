package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.GuiHandler;

public class BlockTransciever extends BlockTile {

<<<<<<< HEAD
	private static VoxelShape bb[] = {VoxelShapes.create(.25, .25, .75, .75, .75, 1),
			VoxelShapes.create(.25, .25, 0, .75, .75, 0.25),
			VoxelShapes.create(.75, .25, .25, 1, .75, .75),
			VoxelShapes.create(0, .25, .25, 0.25, .75, .75)};
=======
	private static AxisAlignedBB[] bb = {new AxisAlignedBB(.25, .25, .75, .75, .75, 1),
		new AxisAlignedBB(.25, .25, 0, .75, .75, 0.25),
		new AxisAlignedBB(.75, .25, .25, 1, .75, .75),
		new AxisAlignedBB(0, .25, .25, 0.25, .75, .75)};
>>>>>>> origin/feature/nuclearthermalrockets
	
	public BlockTransciever(Properties properties, GuiHandler.guiId guiId) {
		super(properties, guiId);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return bb[state.get(FACING).ordinal() - 2];
	}
}
