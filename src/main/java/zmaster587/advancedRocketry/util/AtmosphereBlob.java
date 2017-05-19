package zmaster587.advancedRocketry.util;

import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AtmosphereBlob extends AreaBlob implements Runnable {


	static ThreadPoolExecutor pool = (Configuration.atmosphereHandleBitMask & 1) == 1 ? new ThreadPoolExecutor(2, 16, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(32)) : null;

	boolean executing;
	HashedBlockPosition blockPos;
	List<AreaBlob> nearbyBlobs;

	public AtmosphereBlob(IBlobHandler blobHandler) {
		super(blobHandler);
		executing = false;
	}

	public int getPressure() {
		return 100;
	}

	/**
	 * Called when a block can no longer be filled with air
	 */
	@Override
	public void removeBlock(HashedBlockPosition blockPos) {

		synchronized (graph) {
			graph.remove(blockPos);

			for(EnumFacing direction : EnumFacing.values()) {

				HashedBlockPosition newBlock = blockPos.getPositionAtOffset(direction);
				if(graph.contains(newBlock) && !graph.doesPathExist(newBlock, blobHandler.getRootPosition()))
					runEffectOnWorldBlocks(blobHandler.getWorldObj(), graph.removeAllNodesConnectedTo(newBlock));
			}
		}
	}

	@Override
	public boolean isPositionAllowed(World world, HashedBlockPosition pos, List<AreaBlob> otherBlobs) {
		for(AreaBlob blob : otherBlobs) {
			if(blob.contains(pos) && blob != this)
				return false;
		}

		return !SealableBlockHandler.INSTANCE.isBlockSealed(world, pos.getBlockPos());
	}

	@Override
	public void addBlock(HashedBlockPosition blockPos, List<AreaBlob> nearbyBlobs) {

		if(blobHandler.canFormBlob()) {

			if(!this.contains(blockPos) && 
					(this.graph.size() == 0 || this.contains(blockPos.getPositionAtOffset(EnumFacing.UP)) || this.contains(blockPos.getPositionAtOffset(EnumFacing.DOWN)) ||
							this.contains(blockPos.getPositionAtOffset(EnumFacing.EAST)) || this.contains(blockPos.getPositionAtOffset(EnumFacing.WEST)) ||
							this.contains(blockPos.getPositionAtOffset(EnumFacing.NORTH)) || this.contains(blockPos.getPositionAtOffset(EnumFacing.SOUTH)))) {
				if(!executing) {
					this.nearbyBlobs = nearbyBlobs;
					this.blockPos = blockPos;
					executing = true;
					if((Configuration.atmosphereHandleBitMask & 1) == 1)
						try {
							pool.execute(this);
						} catch (RejectedExecutionException e) {
							AdvancedRocketry.logger.warn("Atmosphere calculation at " + this.getRootPosition() + " aborted due to oversize queue!");
						}
					else
						this.run();
				}
			}
		}
	}	


	@Override
	public void run() {

		//Nearby Blobs


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

						sealed = !isPositionAllowed(blobHandler.getWorldObj(), searchNextPosition, nearbyBlobs);//SealableBlockHandler.INSTANCE.isBlockSealed(blobHandler.getWorldObj(), searchNextPosition.getBlockPos());


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
		synchronized (graph) {
			for(HashedBlockPosition blockPos2 : addableBlocks) {
				super.addBlock(blockPos2, nearbyBlobs);
			}
		}

		executing = false;
	}


	/**
	 * @param world
	 * @param blocks Collection containing affected locations
	 */
	protected void runEffectOnWorldBlocks(World world, Collection<HashedBlockPosition> blocks) {
		if(!AtmosphereHandler.getOxygenHandler(world.provider.getDimension()).getDefaultAtmosphereType().allowsCombustion()) {

			List<HashedBlockPosition> list;

			synchronized (graph) {
				list = new LinkedList<HashedBlockPosition>(blocks);
			}


			for(HashedBlockPosition pos : list) {
				IBlockState state  = world.getBlockState(pos.getBlockPos());
				if(state.getBlock() == Blocks.TORCH) {
					world.setBlockState(pos.getBlockPos(), AdvancedRocketryBlocks.blockUnlitTorch.getDefaultState().withProperty(BlockTorch.FACING, state.getValue(BlockTorch.FACING)));
				}
				else if(Configuration.torchBlocks.contains(state.getBlock())) {
					EntityItem item = new EntityItem(world, pos.x, pos.y, pos.z, new ItemStack(state.getBlock()));
					world.setBlockToAir(pos.getBlockPos());
					world.spawnEntity(item);
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
