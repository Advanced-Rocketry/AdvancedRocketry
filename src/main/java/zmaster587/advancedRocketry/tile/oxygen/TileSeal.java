package zmaster587.advancedRocketry.tile.oxygen;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.block.BlockSeal;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileSeal extends TileEntity {

	boolean ticked = false;

	@Override
	public void onChunkUnload() {
		((BlockSeal) AdvancedRocketryBlocks.blockPipeSealer).clearBlob(worldObj, xCoord, yCoord, zCoord);
		ticked = false;
	}
	
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote && !ticked && !isInvalid()) {
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
				((BlockSeal) AdvancedRocketryBlocks.blockPipeSealer).fireCheckAllDirections(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir);
			}
			ticked = true;
		}
	}
}
