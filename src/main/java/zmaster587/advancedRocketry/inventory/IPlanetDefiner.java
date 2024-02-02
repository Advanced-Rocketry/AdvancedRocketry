package zmaster587.advancedRocketry.inventory;

import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;

public interface IPlanetDefiner {

    boolean isPlanetKnown(IDimensionProperties properties);

    boolean isStarKnown(StellarBody body);
}
