package zmaster587.advancedRocketry.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import zmaster587.libVulpes.block.BlockTileComparatorOverride;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.util.IAdjBlockUpdate;

public class BlockTileNeighborUpdate extends BlockTileComparatorOverride {

	public BlockTileNeighborUpdate(AbstractBlock.Properties properties,
								   GuiHandler.guiId guiId) {
		super(properties, guiId);
	}
	
	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, world, pos, neighbor);
		TileEntity tile = world.getTileEntity(pos);
		
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();
	}
}
