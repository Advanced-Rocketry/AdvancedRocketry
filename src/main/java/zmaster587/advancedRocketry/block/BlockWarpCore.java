package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.HashedBlockPosition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWarpCore extends BlockMultiblockMachine {

	public BlockWarpCore(Class<? extends TileMultiBlock> tileClass,
			int guiId) {
		super(tileClass, guiId);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
			EntityLivingBase placer, ItemStack stack) {
		// TODO Auto-generated method stub
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		if(!world.isRemote && world.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
		
			if(spaceObj instanceof SpaceObject)
				((SpaceObject)spaceObj).addWarpCore(new HashedBlockPosition(pos));
		}
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World world, BlockPos pos,
			IBlockState state) {
		// TODO Auto-generated method stub
		super.onBlockDestroyedByPlayer(world, pos, state);
		
		if(world.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObj instanceof SpaceObject)
				((SpaceObject)spaceObj).removeWarpCore(new HashedBlockPosition(pos));
		}
	}
}
