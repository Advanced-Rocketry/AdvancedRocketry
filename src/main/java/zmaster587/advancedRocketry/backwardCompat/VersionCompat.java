package zmaster587.advancedRocketry.backwardCompat;

public class VersionCompat {
	public static void upgradeDimensionManagerPostLoad(String prevVersion) {
		/*if(AdvancedRocketry.version.equals(prevVersion) || AdvancedRocketry.version.startsWith("%"))
			return;
		String version = prevVersion;
		
		if(version.contains("-"))
			version = prevVersion.split("-")[1];

		//Upgrade gas giants
		if(version.isEmpty() || version.compareTo("0.9.1") < 1) {
			for(int dimId : DimensionManager.getInstance().getLoadedDimensions()) {

				if(dimId >= DimensionManager.GASGIANT_DIMID_OFFSET)
					DimensionManager.getInstance().getDimensionProperties(dimId).setGasGiant();
			}
			DimensionManager.getSol().setName("Sol");
		}*/
	}
}
