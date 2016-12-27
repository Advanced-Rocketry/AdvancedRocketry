package zmaster587.advancedRocketry.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidColored extends Fluid {

	private static final ResourceLocation notFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_still");
	private static final ResourceLocation flowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_flow");
	
	int color;
	public FluidColored(String fluidName, int color) {
		super(fluidName, notFlowing, flowing);
		this.color = color;
	}
	
	@Override
	public int getColor() {
		return color;
	}

}
