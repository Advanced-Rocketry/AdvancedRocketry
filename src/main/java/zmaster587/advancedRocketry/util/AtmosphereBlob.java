package zmaster587.advancedRocketry.util;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.util.SealableBlockHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.libVulpes.util.BlockPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AtmosphereBlob extends AreaBlob implements Runnable {

	
	static ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2));
	
	boolean executing;
	BlockPosition blockPos;

	public AtmosphereBlob(IBlobHandler blobHandler) {
		super(blobHandler);
		executing = false;
	}

	@Override
	public void removeBlock(int x, int y, int z) {
		BlockPosition blockPos = new BlockPosition(x, y, z);
		graph.remove(blockPos);
		graph.contains(blockPos);

		for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {

			BlockPosition newBlock = blockPos.getPositionAtOffset(direction.offsetX, direction.offsetY, direction.offsetZ);
			if(graph.contains(newBlock) && !graph.doesPathExist(newBlock, blobHandler.getRootPosition()))
				runEffectOnWorldBlocks(blobHandler.getWorld(), graph.removeAllNodesConnectedTo(newBlock));
		}
	}

	@Override
	public boolean isPositionAllowed(World world, BlockPosition pos) {
		return !SealableBlockHandler.INSTANCE.isBlockSealed(world, pos);
	}

	@Override
	public void addBlock(BlockPosition blockPos) {

		if(blobHandler.canFormBlob()) {

			if(!this.contains(blockPos)) {
				/*if(worker != null) {
					this.blockPos = blockPos;

					worker.interrupt();
				}*/
				//worker = null;
				if(!executing) {
					this.blockPos = blockPos;
					executing = true;
					pool.execute(this);
					//worker = new Thread(this);
					//worker.start();
				}
			}
		}
	}	


	@Override
	public void run() {

		Stack<BlockPosition> stack = new Stack<BlockPosition>();
		stack.push(blockPos);

		final int maxSize = this.getBlobMaxRadius();
		final HashSet<BlockPosition> addableBlocks = new HashSet<BlockPosition>();

		while(!stack.isEmpty()) {
			BlockPosition stackElement = stack.pop();
			addableBlocks.add(stackElement);

			for(ForgeDirection dir2 : ForgeDirection.VALID_DIRECTIONS) {
				BlockPosition searchNextPosition = stackElement.getPositionAtOffset(dir2.offsetX, dir2.offsetY, dir2.offsetZ);
				boolean sealed;
				
				synchronized(SealableBlockHandler.INSTANCE) {
					sealed = SealableBlockHandler.INSTANCE.isBlockSealed(blobHandler.getWorld(), searchNextPosition);
				}
				
				if(!sealed && !graph.contains(searchNextPosition) && !addableBlocks.contains(searchNextPosition)) {
					if(addableBlocks.size() <= maxSize) {
						stack.push(searchNextPosition);
						addableBlocks.add(searchNextPosition);
					}
					else {
						//World world = blobHandler.getWorld();

						//runEffectOnWorldBlocks(world, getLocations());

						clearBlob();
						executing = false;
						return;
					}
				}
			}
		}

		//only one instance can editing this at a time because this will not run again b/c "worker" is not null
		
			for(BlockPosition blockPos2 : addableBlocks) {
				super.addBlock(blockPos2);
			}
		
			executing = false;
	}


	/**
	 * @param world
	 * @param blocks Collection containing affected locations
	 */
	protected void runEffectOnWorldBlocks(World world, Collection<BlockPosition> blocks) {
		for(BlockPosition pos : new LinkedList<BlockPosition>(blocks)) {
			if(world.getBlock(pos.x, pos.y, pos.z) == Blocks.torch) {
				world.setBlock(pos.x, pos.y, pos.z, AdvancedRocketryBlocks.blockUnlitTorch);
			}
		}
	}

	@Override
	public void clearBlob() {
		World world = blobHandler.getWorld();

		runEffectOnWorldBlocks(world, getLocations());

		super.clearBlob();
	}
}
