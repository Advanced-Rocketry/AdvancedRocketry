package zmaster587.advancedRocketry.util;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Configuration;
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

	
	static ThreadPoolExecutor pool = (Configuration.atmosphereHandleBitMask & 1) == 1 ? new ThreadPoolExecutor(2, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2)) : null;
	
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
					if((Configuration.atmosphereHandleBitMask & 1) == 1)
						pool.execute(this);
					else
						this.run();
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

		final int maxSize = (Configuration.atmosphereHandleBitMask & 2) != 0 ? (int)(Math.pow(this.getBlobMaxRadius(), 3)*((4f/3f)*Math.PI)) : this.getBlobMaxRadius();
		final HashSet<BlockPosition> addableBlocks = new HashSet<BlockPosition>();

		//Breadth first search; non recursive
		while(!stack.isEmpty()) {
			BlockPosition stackElement = stack.pop();
			addableBlocks.add(stackElement);

			for(ForgeDirection dir2 : ForgeDirection.VALID_DIRECTIONS) {
				BlockPosition searchNextPosition = stackElement.getPositionAtOffset(dir2.offsetX, dir2.offsetY, dir2.offsetZ);

				//Don't path areas we have already scanned
				if(!graph.contains(searchNextPosition) && !addableBlocks.contains(searchNextPosition)) {

					boolean sealed;

					try {

						sealed = SealableBlockHandler.INSTANCE.isBlockSealed(blobHandler.getWorld(), searchNextPosition);
						

						if(!sealed) {
							if(((Configuration.atmosphereHandleBitMask & 2) == 0 && searchNextPosition.getDistance(this.getRootPosition()) <= maxSize) ||
									((Configuration.atmosphereHandleBitMask & 2) != 0 && addableBlocks.size() <= maxSize)) {
								stack.push(searchNextPosition);
								addableBlocks.add(searchNextPosition);
							}
							else {
								//Failed to seal, void
								clearBlob();
								executing = false;
								return;
							}
						}
					} catch (Exception e){
						//Catches errors with additional information
						AdvancedRocketry.logger.info("Error: AtmosphereBlob has failed to form correctly due to an error. \nCurrentBlock: " + stackElement + "\tNextPos: " + searchNextPosition + "\tDir: " + dir2 + "\tStackSize: " + stack.size());
						e.printStackTrace();
						//Failed to seal, void
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
