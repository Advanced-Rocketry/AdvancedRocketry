package zmaster587.advancedRocketry.tile;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetSelector;

public class TileWarpshipNavigator extends TilePlanetSelector {
	
	@Override
	public void onSelectionConfirmed(Object sender) {
		if(this.worldObj.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject spaceStation = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);
			spaceStation.setDestOrbitingBody(container.getSelectedSystem());
		}
	}
}
