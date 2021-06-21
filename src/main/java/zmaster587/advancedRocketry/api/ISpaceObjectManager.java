package zmaster587.advancedRocketry.api;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;

public interface ISpaceObjectManager {
	
	@Nullable ISpaceObject getSpaceStationFromBlockCoords(BlockPos pos);
}
