package zmaster587.advancedRocketry.util;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

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
	HashedBlockPosition blockPos;

	public AtmosphereBlob(IBlobHandler blobHandler) {
		super(blobHandler);
		executing = false;
	}

	public int getPressure() {
		return 100;
	}

	@Override
	public void removeBlock(HashedBlockPosition blockPos) {

		graph.remove(blockPos);
		graph.contains(blockPos);

		for(EnumFacing direction : EnumFacing.values()) {

			HashedBlockPosition newBlock = blockPos.getPositionAtOffset(direction);
			if(graph.contains(newBlock) && !graph.doesPathExist(newBlock, blobHandler.getRootPosition()))
				runEffectOnWorldBlocks(blobHandler.getWorldObj(), graph.removeAllNodesConnectedTo(newBlock));
		}
	}

	@Override
	public boolean isPositionAllowed(World world, HashedBlockPosition pos) {
		return !SealableBlockHandler.INSTANCE.isBlockSealed(world, pos.getBlockPos());
	}

	@Override
	public void addBlock(HashedBlockPosition blockPos) {

		if(blobHandler.canFormBlob()) {

			if(!this.contains(blockPos)) {
				if(!executing) {
					this.blockPos = blockPos;
					executing = true;
					if((Configuration.atmosphereHandleBitMask & 1) == 1)
						pool.execute(this);
					else
						this.run();
				}
			}
		}
	}	


	@Override
	public void run() {

		Stack<HashedBlockPosition> stack = new Stack<HashedBlockPosition>();
		stack.push(blockPos);

		final int maxSize = (Configuration.atmosphereHandleBitMask & 2) != 0 ? (int)(Math.pow(this.getBlobMaxRadius(), 3)*((4f/3f)*Math.PI)) : this.getBlobMaxRadius();
		final HashSet<HashedBlockPosition> addableBlocks = new HashSet<HashedBlockPosition>();

		//Breadth first search; non recursive
		while(!stack.isEmpty()) {
			HashedBlockPosition stackElement = stack.pop();
			addableBlocks.add(stackElement);

			for(EnumFacing dir2 : EnumFacing.values()) {
				HashedBlockPosition searchNextPosition = stackElement.getPositionAtOffset(dir2);

				//Don't path areas we have already scanned
				if(!graph.contains(searchNextPosition) && !addableBlocks.contains(searchNextPosition)) {

					boolean sealed;

					try {

						sealed = SealableBlockHandler.INSTANCE.isBlockSealed(blobHandler.getWorldObj(), searchNextPosition.getBlockPos());


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

		for(HashedBlockPosition blockPos2 : addableBlocks) {
			super.addBlock(blockPos2);
		}

		executing = false;
	}


	/**
	 * @param world
	 * @param blocks Collection containing affected locations
	 */
	protected void runEffectOnWorldBlocks(World world, Collection<HashedBlockPosition> blocks) {
		if(!AtmosphereHandler.getOxygenHandler(world.provider.getDimension()).getDefaultAtmosphereType().allowsCombustion()) {
			for(HashedBlockPosition pos : new LinkedList<HashedBlockPosition>(blocks)) {
				IBlockState state  = world.getBlockState(pos.getBlockPos());
				if(state.getBlock() == Blocks.TORCH) {
					world.setBlockState(pos.getBlockPos(), AdvancedRocketryBlocks.blockUnlitTorch.getDefaultState().withProperty(BlockTorch.FACING, state.getValue(BlockTorch.FACING)));
				}
			}
		}
	}

	@Override
	public void clearBlob() {
		World world = blobHandler.getWorldObj();

		runEffectOnWorldBlocks(world, getLocations());

		super.clearBlob();
	}
}
