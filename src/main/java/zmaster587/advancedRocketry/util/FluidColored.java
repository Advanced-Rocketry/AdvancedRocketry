package zmaster587.advancedRocketry.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidColored extends Fluid {

	private static final ResourceLocation notFlowing = new ResourceLocation("advancedrocketry:textures/blocks/fluid/oxygen_still");
	private static final ResourceLocation flowing = new ResourceLocation("advancedrocketry:textures/blocks/fluid/oxygen_flowing");
	
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
