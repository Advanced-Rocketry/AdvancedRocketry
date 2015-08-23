package zmaster587.advancedRocketry.satellite;

import zmaster587.advancedRocketry.util.DataStorage;
import zmaster587.advancedRocketry.util.DataStorage.DataType;

public class SatelliteOptical extends SatelliteData {

	public SatelliteOptical() {
		data = new DataStorage(DataStorage.DataType.DISTANCE);
		data.lockDataType(DataType.DISTANCE);
	}
	
	@Override
	public String getName() {
		return "Optical Telescope";
	}

	@Override
	public double failureChance() {
		return 0;
	}
}
