package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.TileForceFieldProjector;
import zmaster587.libVulpes.block.BlockFullyRotatable;

public class BlockForceFieldProjector extends BlockFullyRotatable {

	public BlockForceFieldProjector(Properties par2Material) {
		super(par2Material);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tile = worldIn.getTileEntity(pos);
		
		if(tile instanceof TileForceFieldProjector)
			((TileForceFieldProjector)tile).destroyField(getFront(state));
		
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileForceFieldProjector();
	}
}
