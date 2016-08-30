package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.tile.station.TileLandingPad;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLandingPad extends Block {

	public BlockLandingPad(Material mat) {
		super(mat);
	}
	
	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileLandingPad();
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
				((SpaceObject)spaceObj).addLandingPad(x, z);
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
				((SpaceObject)spaceObj).removeLandingPad(x, z);
		}
	}
}
