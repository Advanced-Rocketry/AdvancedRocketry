package zmaster587.advancedRocketry.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.libVulpes.util.AdjacencyGraph;
import zmaster587.libVulpes.util.HashedBlockPosition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AreaBlob {
	//Graph containing the acutal area enclosed
	protected final AdjacencyGraph<HashedBlockPosition> graph;
	//Object to call back to when events happen, usually a tileentity
	protected IBlobHandler blobHandler;
	//Data stored by this blob
	private Object data;

	public AreaBlob(@Nonnull IBlobHandler blobHandler) {
		this.blobHandler = blobHandler;
		graph = new AdjacencyGraph<>();
		data = null;
	}

	public void setData(@Nullable Object obj) {
		data = obj;
	}
	
	public boolean isPositionAllowed(World world, HashedBlockPosition pos, List<AreaBlob> otherBlobs) {
		return true;
	}

	@Nullable
	public Object getData() {
		return data;
	}
	
	public int getBlobMaxRadius() {
		return blobHandler.getMaxBlobRadius();
	}
	
	/**
	 * Adds a block position to the blob
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addBlock(int x, int y , int z, @Nonnull List<AreaBlob> otherBlobs) {
		HashedBlockPosition blockPos = new HashedBlockPosition(x, y, z);
		addBlock(blockPos, otherBlobs);
	}
	
	/**
	 * Adds a block to the graph
	 * @param blockPos block to add
	 */
	public void addBlock(@Nonnull HashedBlockPosition blockPos, List<AreaBlob> otherBlobs) {
		if(!graph.contains(blockPos) && blobHandler.canFormBlob()) {
			graph.add(blockPos, getPositionsToAdd(blockPos));
		}
	}
	
	/**
	 * @return the BlockPosition of the root of the blob
	 */
	@Nonnull
	public HashedBlockPosition getRootPosition() {
		return blobHandler.getRootPosition();
	}
	
	/**
	 * Gets adjacent blocks if they exist in the blob
	 * @param blockPos block to find things adjacent to
	 * @return list containing valid adjacent blocks
	 */
	@Nonnull
	protected HashSet<HashedBlockPosition> getPositionsToAdd(@Nonnull HashedBlockPosition blockPos) {
		HashSet<HashedBlockPosition> set = new HashSet<>();
		
		for(EnumFacing direction : EnumFacing.values()) {
			
			HashedBlockPosition offset = blockPos.getPositionAtOffset(direction);
			if(graph.contains(offset))
				set.add(offset);
		}
		
		return set;
	}

	/**
	 * Given a block position returns whether or not it exists in the graph
	 * @return true if the block exists in the blob
	 */
	public boolean contains(@Nonnull HashedBlockPosition position) {
		boolean contains;
		
		synchronized (graph) {
			contains = graph.contains(position);
		}
		return contains;
	}
	
	/**
	 * Given a block position returns whether or not it exists in the graph
	 * @param x
	 * @param y
	 * @param z
	 * @return true if the block exists in the blob
	 */
	public boolean contains(int x, int y, int z) {
		return contains(new HashedBlockPosition(x, y, z));
	}

	/**
	 * Called when this blob is about to overlap another blob
	 * @param otherBlob other blob about to be overlapped
	 * @return true if this blob is allowed to overlap the otherBlob
	 */
	public boolean canBlobsOverlap(int x, int y, int z, AreaBlob otherBlob) {
		return blobHandler.canBlobsOverlap(new HashedBlockPosition(x, y, z), otherBlob);
	}

	/**
	 * Removes the block at the given coords for this blob
	 * @param blockPos
	 */
	public void removeBlock(@Nonnull HashedBlockPosition blockPos) {
		//HashedBlockPosition blockPos = new HashedBlockPosition(x, y, z);
		graph.remove(blockPos);
		
		for(EnumFacing direction : EnumFacing.values()) {

			HashedBlockPosition newBlock = blockPos.getPositionAtOffset(direction);
			if(graph.contains(newBlock) && !graph.doesPathExist(newBlock, blobHandler.getRootPosition()))
				graph.removeAllNodesConnectedTo(newBlock);
		}
		
	}
	
	/**
	 * Removes all nodes from the blob
	 */
	public void clearBlob() {
		graph.clear();
	}
	
	/**
	 * @return a set containing all locations
	 */
	public Set<HashedBlockPosition> getLocations() {
		return graph.getKeys();
	}
	
	/**
	 * @return the number of elements in the blob
	 */
	public int getBlobSize() {
		return graph.size();
	}
}
