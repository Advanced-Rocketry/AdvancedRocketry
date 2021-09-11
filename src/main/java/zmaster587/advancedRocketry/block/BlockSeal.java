package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AreaBlob;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.tile.atmosphere.TileSeal;
import zmaster587.libVulpes.util.HashedBlockPosition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.LinkedList;

public class BlockSeal extends Block {

	private HashMap<HashedBlockPosition,BlobHandler> blobList = new HashMap<>();
	
	public BlockSeal(Material materialIn) {
		super(materialIn);
	}
	
	public void clearMap() {
		blobList.clear();
	}
	
	@Override
	public boolean hasTileEntity(@Nullable IBlockState state) {
		return true;
	}
	
	@Override
	@Nonnull
	public TileEntity createTileEntity(@Nullable World world, @Nullable IBlockState state) {
		return new TileSeal();
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		
		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(worldIn.provider.getDimension());
		if(atmhandler == null)
			return;
		
		for(EnumFacing dir : EnumFacing.VALUES) {
			BlobHandler handler = blobList.remove(new HashedBlockPosition(pos.offset(dir)));
			if (handler != null) atmhandler.unregisterBlob(handler);
			
			fireCheckAllDirections(worldIn, pos.offset(dir), dir);
		}
	}
	
	public void removeSeal(@Nonnull World worldIn, @Nonnull BlockPos pos) {
		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(worldIn.provider.getDimension());
		if(atmhandler == null)
			return;
		
		for(EnumFacing dir : EnumFacing.VALUES) {
			BlobHandler handler = blobList.remove(new HashedBlockPosition(pos.offset(dir)));
			if (handler != null) atmhandler.unregisterBlob(handler);
		}
	}
	
	public void clearBlob(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nullable IBlockState state) {
		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(worldIn.provider.getDimension());
		if(atmhandler == null)
			return;
		
		for(EnumFacing dir : EnumFacing.VALUES) {
			BlobHandler handler = blobList.remove(new HashedBlockPosition(pos.offset(dir)));
			if (handler != null) atmhandler.unregisterBlob(handler);
			
			//fireCheckAllDirections(worldIn, pos.offset(dir), dir);
		}
	}

	@Override
	public void onBlockAdded(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nullable IBlockState state) {
		super.onBlockAdded(worldIn, pos, state);

		checkCompleteness(worldIn, pos);
		
		for(EnumFacing dir : EnumFacing.VALUES) {
			fireCheckAllDirections(worldIn, pos.offset(dir), dir);
		}
	}
	
	public void fireCheckAllDirections(@Nonnull World worldIn, @Nonnull BlockPos startBlock, @Nonnull EnumFacing directionFrom) {
		for(EnumFacing dir : EnumFacing.VALUES) {
			if(directionFrom.getOpposite() != dir)
				fireCheck(worldIn, startBlock.offset(dir));
		}
	}
	
	private void fireCheck(@Nonnull World worldIn, @Nonnull BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		if(block == this) {
			BlockSeal blockSeal = (BlockSeal)block;
			blockSeal.checkCompleteness(worldIn, pos);
		}
	}

	private boolean checkCompleteness(@Nonnull World worldIn, @Nonnull BlockPos pos) {
		AtmosphereHandler atmhandler = AtmosphereHandler.getOxygenHandler(worldIn.provider.getDimension());
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
			blob.addBlock(hashPos, new LinkedList<>());
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
			blob.addBlock(hashPos, new LinkedList<>());
			atmhandler.registerBlob(handler, pos, blob);
			return true;
		}
		return false;
	}
	
	private static class BlobHandler implements IBlobHandler {
		
		World world;
		BlockPos pos;
		
		public BlobHandler(@Nonnull World world, @Nonnull BlockPos pos) {
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
		@Nonnull
		public HashedBlockPosition getRootPosition() {
			return new HashedBlockPosition(pos);
		}
		
		@Override
		public int getTraceDistance() {
			return -1;
		}
	}
}
