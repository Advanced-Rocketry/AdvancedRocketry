package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.inventory.GuiHandler;

public class BlockTileRedstoneEmitter extends BlockTile {

	public BlockTileRedstoneEmitter(Properties properties,
			GuiHandler.guiId guiId) {
		super(properties, guiId);
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.get(STATE) ? 15 : 0;
	}
	
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}
	
	public void setRedstoneState(World world, BlockState state, BlockPos pos, boolean newState) {
		if(world.getBlockState(pos).getBlock() != this)
			return;
		
		world.setBlockState(pos, state.with(STATE, newState));
		world.notifyBlockUpdate(pos, state,  state, 3);
	}
}
