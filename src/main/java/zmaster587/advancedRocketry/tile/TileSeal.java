package zmaster587.advancedRocketry.tile;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.block.BlockSeal;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileSeal extends TileEntity implements ITickable {

	boolean ticked = false;

	@Override
	public void onChunkUnload() {
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
