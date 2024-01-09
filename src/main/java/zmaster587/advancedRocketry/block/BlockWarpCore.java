package zmaster587.advancedRocketry.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.HashedBlockPosition;

import org.jetbrains.annotations.NotNull;

public class BlockWarpCore extends BlockMultiblockMachine {

	public BlockWarpCore(Class<? extends TileMultiBlock> tileClass,
			int guiId) {
		super(tileClass, guiId);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
			EntityLivingBase placer, @NotNull ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		
		if(!world.isRemote && world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
		
			if(spaceObj instanceof SpaceStationObject)
				((SpaceStationObject)spaceObj).addWarpCore(new HashedBlockPosition(pos));
		}
	}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos,
								 IBlockState state, EntityPlayer player) {
		super.onBlockHarvested(world, pos, state, player);
		
		if(world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObj instanceof SpaceStationObject)
				((SpaceStationObject)spaceObj).removeWarpCore(new HashedBlockPosition(pos));
		}
	}
}
