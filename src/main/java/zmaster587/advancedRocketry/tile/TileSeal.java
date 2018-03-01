package zmaster587.advancedRocketry.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.block.BlockSeal;

public class TileSeal extends TileEntity implements ITickable {

	boolean ticked = false;

	@Override
	public void onChunkUnload() {
		((BlockSeal) AdvancedRocketryBlocks.blockPipeSealer).removeSeal(getWorld(), getPos());
		ticked = false;
	}
	
	@Override
	public void update() {
		if(!world.isRemote && !ticked && !isInvalid()) {
			for(EnumFacing dir : EnumFacing.VALUES) {
				((BlockSeal) AdvancedRocketryBlocks.blockPipeSealer).fireCheckAllDirections(getWorld(), pos.offset(dir), dir);
			}
			ticked = true;
		}
	}
}
