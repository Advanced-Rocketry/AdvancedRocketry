package zmaster587.advancedRocketry.backwardCompat;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.dimension.DimensionManager;

public class VersionCompat {
	public static void upgradeDimensionManagerPostLoad(String prevVersion) {
		if(AdvancedRocketry.version.equals(prevVersion) || !AdvancedRocketry.version.contains("-"))
			return;
		
		String version = AdvancedRocketry.version.split("-")[1];
		
		//Upgrade gas giants
		if(version.isEmpty() || version.compareTo("0.9.1") < 1) {
			for(int dimId : DimensionManager.getInstance().getLoadedDimensions()) {
				
				if(dimId >= DimensionManager.GASGIANT_DIMID_OFFSET)
					DimensionManager.getInstance().getDimensionProperties(dimId).setGasGiant();
			}
			DimensionManager.getSol().setName("Sol");
		}
	}
}
