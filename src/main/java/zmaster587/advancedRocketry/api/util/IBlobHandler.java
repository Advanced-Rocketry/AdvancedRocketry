package zmaster587.advancedRocketry.api.util;

import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import zmaster587.libVulpes.util.BlockPosition;

public interface IBlobHandler {
	/**
	 * Called when a block is being removed from a blob
	 * @param pos BlockPosition to remove
	 * /
	public void onBlobRemove(BlockPosition pos);
	
	/**
	 * Called when a block is being added to the blob
	 * @param pos BlockPosition to add
	 
	public void onBlobAdd(BlockPosition pos);*/
	
	/**
	 * @return true if a blob is allowed to form otherwise false
	 */
	public boolean canFormBlob();
	
	public World getWorldObj();
	
	/**
	 * Called when two blobs of the same type overlap
	 * @param blockPosition Position at which the overlap occurs
	 * @param other areaBlob to Overlap
	 * @return true if the two blobs are allowed to overlap, false if not
	 */
	public boolean canBlobsOverlap(BlockPosition blockPosition, AreaBlob blob);
	
	/**
	 * @return the maximum distance an object can maintain a blob
	 */
	public int getMaxBlobRadius();
	
	/**
	 * @return the position to use as root
	 */
	public BlockPosition getRootPosition();
}
