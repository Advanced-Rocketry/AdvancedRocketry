package zmaster587.advancedRocketry.api;

import net.minecraft.util.math.BlockPos;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;

public interface ISpaceObjectManager {
	public ISpaceObject getSpaceStationFromBlockCoords(BlockPos pos);
}
