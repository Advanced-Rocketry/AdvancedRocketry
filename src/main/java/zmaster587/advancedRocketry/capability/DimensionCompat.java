package zmaster587.advancedRocketry.capability;

import zmaster587.advancedRocketry.AdvancedRocketry;

import java.lang.reflect.Field;

public class DimensionCompat {

    static Field JEDSpawnID, JEDEnableOverride;

    static {
        try {
            JEDSpawnID = Class.forName("fi.dy.masa.justenoughdimensions.config.Configs").getDeclaredField("initialSpawnDimensionId");
            JEDEnableOverride = Class.forName("fi.dy.masa.justenoughdimensions.config.Configs").getDeclaredField("enableInitialSpawnDimensionOverride");
            AdvancedRocketry.logger.info("JED Found, compat loaded");
        } catch (Exception e) {
            AdvancedRocketry.logger.info("JED compat not loaded");
            JEDSpawnID = null;
            JEDEnableOverride = null;
        }
    }

    public static int getDefaultSpawnDimension() {
        try {
            if (JEDSpawnID != null && JEDEnableOverride != null && (boolean) JEDEnableOverride.get(null)) {

                return (int) JEDSpawnID.get(null);

            }
        } catch (Exception e) {
            //No nonsense
            return 0;
        }


        return 0;
    }

}
