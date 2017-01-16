package zmaster587.advancedRocketry.block;

import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTileNeighborUpdate extends BlockTile {

	/**
	 * @param tileClass must extend IAdjBlockUpdate
	 */
	public BlockTileNeighborUpdate(Class<? extends TileEntity> tileClass,
			int guiId) {
		super(tileClass, guiId);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos,
			Block blockIn) {
		
		super.neighborChanged(state, worldIn, pos, blockIn);
		TileEntity tile = worldIn.getTileEntity(pos);
		
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();
	}
	
	/*@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos,
			BlockPos neighbor) {
		// TODO Auto-generated method stub
		super.onNeighborChange(world, pos, neighbor);
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();
	}*/

}
