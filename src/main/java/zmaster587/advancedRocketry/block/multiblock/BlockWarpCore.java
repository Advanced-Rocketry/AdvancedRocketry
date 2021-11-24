package zmaster587.advancedRocketry.block.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

public class BlockWarpCore extends BlockMultiblockMachine {

	public BlockWarpCore(Properties property,
			GuiHandler.guiId guiId) {
		super(property, guiId);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state,
			LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		if(!world.isRemote && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
		
			if(spaceObj instanceof SpaceStationObject)
				((SpaceStationObject)spaceObj).addWarpCore(new HashedBlockPosition(pos));
		}
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if(state.getBlock() != newState.getBlock() && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {

			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObj instanceof SpaceStationObject)
				((SpaceStationObject)spaceObj).removeWarpCore(new HashedBlockPosition(pos));
		}
		
		super.onReplaced(state, world, pos, newState, isMoving);
	}
}
