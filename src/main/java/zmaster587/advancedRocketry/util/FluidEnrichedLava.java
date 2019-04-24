package zmaster587.advancedRocketry.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidEnrichedLava extends Fluid {

	private static final ResourceLocation notFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/lava_still");
	private static final ResourceLocation flowing = new ResourceLocation("advancedrocketry:blocks/fluid/lava_flow");
	
	public FluidEnrichedLava(String fluidName, int color) {
		super(fluidName, notFlowing, flowing);
		this.setColor(color);
	}

}
