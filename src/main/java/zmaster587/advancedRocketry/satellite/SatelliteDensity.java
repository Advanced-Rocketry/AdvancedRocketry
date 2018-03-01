package zmaster587.advancedRocketry.satellite;

import zmaster587.advancedRocketry.api.DataStorage;

public class SatelliteDensity extends SatelliteData {

	public SatelliteDensity() {
		data = new DataStorage(DataStorage.DataType.ATMOSPHEREDENSITY);
		data.lockDataType(DataStorage.DataType.ATMOSPHEREDENSITY);
	}
	
	@Override
	public String getName() {
		return "Density Scanner";
	}

	@Override
	public double failureChance() {
		return 0;
	}

}
