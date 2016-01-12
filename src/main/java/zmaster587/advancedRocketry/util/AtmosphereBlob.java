package zmaster587.advancedRocketry.util;

import java.util.HashSet;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.libVulpes.util.BlockPosition;

public class AtmosphereBlob extends AreaBlob {

	public AtmosphereBlob(IBlobHandler blobHandler) {
		super(blobHandler);
	}

	@Override
	public void removeBlock(int x, int y, int z) {
		BlockPosition blockPos = new BlockPosition(x, y, z);
		graph.remove(blockPos);
		graph.contains(blockPos);

		for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {

			BlockPosition newBlock = blockPos.getPositionAtOffset(direction.offsetX, direction.offsetY, direction.offsetZ);
			if(graph.contains(newBlock) && !graph.doesPathExist(newBlock, blobHandler.getRootPosition()))
				graph.removeAllNodesConnectedTo(newBlock);
		}
	}

	@Override
	public boolean isPositionAllowed(World world, BlockPosition pos) {
		return !isBlockSealed(world, pos);
	}

	//TODO: door corners
	public boolean isBlockSealed(World world, BlockPosition pos) {
		Block block = world.getBlock(pos.x, pos.y, pos.z);
		int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);

		return block.isOpaqueCube() || block == Blocks.glass || 
				(block == AdvancedRocketryBlocks.blockAirLock && 
				((meta & 8) == 8 || 
				((meta & 4) >> 2 == (meta & 1) && checkDoorSeal(world, pos.getPositionAtOffset(0, 0, 1), meta) && checkDoorSeal(world, pos.getPositionAtOffset(0, 0, -1), meta)) ||
				(meta & 4) >> 2 != (meta & 1) && checkDoorSeal(world, pos.getPositionAtOffset(1, 0, 0), meta) && checkDoorSeal(world, pos.getPositionAtOffset(-1, 0, 0), meta)));
	}

	private boolean checkDoorSeal(World world, BlockPosition pos, int meta) {
		Block otherBlock = world.getBlock(pos.x, pos.y, pos.z);
		int otherMeta = world.getBlockMetadata(pos.x, pos.y, pos.z);

		return (otherBlock == AdvancedRocketryBlocks.blockAirLock && (otherMeta & 1) == (meta & 1)) || 
				(otherBlock != AdvancedRocketryBlocks.blockAirLock && isBlockSealed(world, pos));
	}

	@Override
	public void addBlock(BlockPosition blockPos) {

		if(blobHandler.canFormBlob()) {

			int maxSize = this.getBlobMaxRadius();

			HashSet<BlockPosition> addableBlocks = new HashSet<BlockPosition>();
			if(!this.contains(blockPos)) {

				Stack<BlockPosition> stack = new Stack<BlockPosition>();
				stack.push(blockPos);


				while(!stack.isEmpty()) {
					BlockPosition stackElement = stack.pop();
					addableBlocks.add(stackElement);

					for(ForgeDirection dir2 : ForgeDirection.VALID_DIRECTIONS) {
						BlockPosition searchNextPosition = stackElement.getPositionAtOffset(dir2.offsetX, dir2.offsetY, dir2.offsetZ);
						
						if(!isBlockSealed(blobHandler.getWorld(), searchNextPosition) && !graph.contains(searchNextPosition) && !addableBlocks.contains(searchNextPosition)) {
							if(searchNextPosition.getDistance(this.getRootPosition()) <= maxSize) {
								stack.push(searchNextPosition);
								addableBlocks.add(searchNextPosition);
							}
							else {
								//TODO: for limits
								clearBlob();
								return;
							}
						}
					}
				}
			}

			for(BlockPosition blockPos2 : addableBlocks) {
				super.addBlock(blockPos2);
			}
		}
	}
}
