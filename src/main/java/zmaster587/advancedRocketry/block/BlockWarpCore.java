package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockWarpCore extends BlockMultiblockMachine {

	public BlockWarpCore(Class<? extends TileMultiBlock> tileClass,
			int guiId) {
		super(tileClass, guiId);
	}

	@Override
	public void onBlockPlacedBy(World world, int x,
			int y, int z, EntityLivingBase player,
			ItemStack items) {
		super.onBlockPlacedBy(world, x, y, z,
				player, items);
		
		if(!world.isRemote && world.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);
		
			if(spaceObj instanceof SpaceObject)
				((SpaceObject)spaceObj).addWarpCore(new BlockPosition(x,y,z));
		}
	}
	
	@Override
	public void onBlockPreDestroy(World world, int x,
			int y, int z, int oldMeta) {
		super.onBlockPreDestroy(world, x, y, z,
				oldMeta);
		
		if(world.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);
			if(spaceObj instanceof SpaceObject)
				((SpaceObject)spaceObj).removeWarpCore(new BlockPosition(x,y,z));
		}
	}
}
