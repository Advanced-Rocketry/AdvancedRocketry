package zmaster587.advancedRocketry.tile.atmosphere;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.block.BlockSeal;

public class TileSeal extends TileEntity implements ITickableTileEntity {

	public TileSeal() {
		super(AdvancedRocketryTileEntityType.TILE_SEAL);
	}

	boolean ticked = false;

	@Override
	public void onChunkUnloaded() {
		((BlockSeal) AdvancedRocketryBlocks.blockSeal).removeSeal(getWorld(), getPos());
		ticked = false;
	}
	
	@Override
	public void tick() {
		if(!world.isRemote && !ticked && !isRemoved()) {
			for(Direction dir : Direction.values()) {
				((BlockSeal) AdvancedRocketryBlocks.blockSeal).fireCheckAllDirections(getWorld(), pos.offset(dir), dir);
			}
			ticked = true;
		}
	}
}
