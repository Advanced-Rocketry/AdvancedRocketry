package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.tile.atmosphere.TileSeal;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

import java.util.HashMap;
import java.util.LinkedList;

public class BlockSeal extends Block {

	private HashMap<HashedBlockPosition,BlobHandler> blobList = new HashMap<HashedBlockPosition,BlobHandler>();
	
	public BlockSeal(Properties materialIn) {
		super(materialIn);
	}
	
	public void clearMap() {
		blobList.clear();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileSeal();
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(worldIn);
		if(atmhandler == null)
			return;
		
		for(Direction dir : Direction.values()) {
			BlobHandler handler = blobList.remove(new HashedBlockPosition(pos.offset(dir)));
			if (handler != null) atmhandler.unregisterBlob(handler);
			
			fireCheckAllDirections(worldIn, pos.offset(dir), dir);
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
	
	public void removeSeal(World worldIn, BlockPos pos) {
		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(worldIn);
		if(atmhandler == null)
			return;
		
		for(Direction dir : Direction.values()) {
			BlobHandler handler = blobList.remove(new HashedBlockPosition(pos.offset(dir)));
			if (handler != null) atmhandler.unregisterBlob(handler);
		}
	}
	
	public void clearBlob(World worldIn, BlockPos pos, BlockState state) {
		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(worldIn);
		if(atmhandler == null)
			return;
		
		for(Direction dir : Direction.values()) {
			BlobHandler handler = blobList.remove(new HashedBlockPosition(pos.offset(dir)));
			if (handler != null) atmhandler.unregisterBlob(handler);
			
			//fireCheckAllDirections(worldIn, pos.offset(dir), dir);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);

		checkCompleteness(worldIn, pos);
		
		for(Direction dir : Direction.values()) {
			fireCheckAllDirections(worldIn, pos.offset(dir), dir);
		}
	}
	
	public void fireCheckAllDirections(World worldIn, BlockPos startBlock, Direction directionFrom) {
		for(Direction dir : Direction.values()) {
			if(directionFrom.getOpposite() != dir)
				fireCheck(worldIn, startBlock.offset(dir));
		}
	}
	
	private void fireCheck(World worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		if(block == this) {
			BlockSeal blockSeal = (BlockSeal)block;
			blockSeal.checkCompleteness(worldIn, pos);
		}
	}

	private boolean checkCompleteness(World worldIn, BlockPos pos) {
		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(worldIn);
		if(atmhandler == null)
			return false;
		
		// check along XY axis
		if(((worldIn.getBlockState(pos.up().west()).getBlock() == this && 
				worldIn.getBlockState(pos.up().east()).getBlock() == this &&
				worldIn.getBlockState(pos.up().up()).getBlock() == this) ||
				
				(worldIn.getBlockState(pos.up().north()).getBlock() == this && 
				worldIn.getBlockState(pos.up().south()).getBlock() == this &&
				worldIn.getBlockState(pos.up().up()).getBlock() == this &&
				!blobList.containsKey(new HashedBlockPosition(pos.up()))))) {
			
			pos = pos.up();
			HashedBlockPosition hashPos = new HashedBlockPosition(pos);
			BlobHandler handler = new BlobHandler(worldIn, pos);
			blobList.put(hashPos, handler);
			
			AreaBlob blob = new AreaBlob(handler);
			blob.addBlock(hashPos, new LinkedList<AreaBlob>());
			atmhandler.registerBlob(handler, pos, blob);
			
			return true;
		}
		
		// check along XZ axis
		if(worldIn.getBlockState(pos.east().north()).getBlock() == this && 
				worldIn.getBlockState(pos.east().south()).getBlock() == this &&
				worldIn.getBlockState(pos.east().east()).getBlock() == this &&
				!blobList.containsKey(new HashedBlockPosition(pos.east()))) {
			
			pos = pos.east();
			HashedBlockPosition hashPos = new HashedBlockPosition(pos);
			BlobHandler handler = new BlobHandler(worldIn, pos);
			blobList.put(hashPos, handler);
			
			AreaBlob blob = new AreaBlob(handler);
			blob.addBlock(hashPos, new LinkedList<AreaBlob>());
			atmhandler.registerBlob(handler, pos, blob);
			return true;
		}
		return false;
	}
	
	private static class BlobHandler implements IBlobHandler {
		
		World world;
		BlockPos pos;
		
		public BlobHandler(World world, BlockPos pos) {
			this.world = world;
			this.pos = pos;
		}
		
		@Override
		public boolean canFormBlob() {
			return true;
		}
		@Override
		public World getWorldObj() {
			return world;
		}

		@Override
		public boolean canBlobsOverlap(HashedBlockPosition blockPosition, AreaBlob blob) {
			return false;
		}

		@Override
		public int getMaxBlobRadius() {
			return 0;
		}

		@Override
		public HashedBlockPosition getRootPosition() {
			return new HashedBlockPosition(pos);
		}
		
		@Override
		public int getTraceDistance() {
			return -1;
		}
	}
}
