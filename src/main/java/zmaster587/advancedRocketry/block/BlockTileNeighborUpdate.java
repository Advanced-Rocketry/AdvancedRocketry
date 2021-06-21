package zmaster587.advancedRocketry.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.BlockTileComparatorOverride;
import zmaster587.libVulpes.util.IAdjBlockUpdate;

public class BlockTileNeighborUpdate extends BlockTileComparatorOverride {

	/**
	 * @param tileClass must extend IAdjBlockUpdate
	 */
	public BlockTileNeighborUpdate(Class<? extends TileEntity> tileClass, int guiId) {
		super(tileClass, guiId);
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(world, pos, neighbor);
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();
	}

}
