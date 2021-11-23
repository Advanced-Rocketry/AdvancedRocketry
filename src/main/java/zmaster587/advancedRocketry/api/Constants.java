package zmaster587.advancedRocketry.api;

import net.minecraft.util.ResourceLocation;

public class Constants {
	public static final String modId = "advancedrocketry";
	public static final ResourceLocation INVALID_PLANET = new ResourceLocation(Constants.modId, "no_planet"); //min value is used for warp
	public static final ResourceLocation INVALID_STAR = new ResourceLocation(Constants.modId, "no_star"); //min value is used for warp
	public static final long INVALID_SAT = -1; 
	public static final int GENTYPE_ASTEROID = 2;
	public static final String STAR_NAMESPACE = "star";
	public static final String PLANET_NAMESPACE = "planet";
}
