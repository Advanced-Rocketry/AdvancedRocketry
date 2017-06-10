package zmaster587.advancedRocketry.capability;

import java.lang.reflect.Field;

import zmaster587.advancedRocketry.AdvancedRocketry;

public class DimensionCompat {

	static Field JEDSpawnID;
	
	static {
		try {
			JEDSpawnID = Class.forName("fi.dy.masa.justenoughdimensions.config.Configs").getDeclaredField("initialSpawnDimensionId");
			AdvancedRocketry.logger.info("JED Found, compat loaded");
		} catch (Exception e) {
			AdvancedRocketry.logger.info("JED compat not loaded");
			JEDSpawnID = null;
		}
	}
	
	public static int getDefaultSpawnDimension() {
		if(JEDSpawnID != null) {
			try {
				return (int) JEDSpawnID.get(null);
			} catch (Exception e) {
				//No nonsense
				return 0;
			} 
		}
		
		return 0;
	}
	
}
