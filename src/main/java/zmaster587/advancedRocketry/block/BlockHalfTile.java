package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.GuiHandler;

public class BlockHalfTile  extends BlockTile {
	private static VoxelShape bb = VoxelShapes.create(0, 0, 0, 1, .5f, 1);
	public BlockHalfTile(Properties properties, GuiHandler.guiId guiId) {
		super(properties, guiId);
	}

	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		// TODO Auto-generated method stub
		return bb;
	}
}
