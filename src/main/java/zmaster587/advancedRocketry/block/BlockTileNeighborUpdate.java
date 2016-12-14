package zmaster587.advancedRocketry.block;

import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
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
	public void onNeighborBlockChange(World world, int x, int y, int z,
			Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(tile instanceof IAdjBlockUpdate)
			((IAdjBlockUpdate)tile).onAdjacentBlockUpdated();
	}

}
