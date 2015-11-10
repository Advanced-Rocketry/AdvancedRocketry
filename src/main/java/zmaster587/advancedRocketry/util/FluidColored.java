package zmaster587.advancedRocketry.util;

import net.minecraftforge.fluids.Fluid;

public class FluidColored extends Fluid {

	
	int color;
	public FluidColored(String fluidName, int color) {
		super(fluidName);
		this.color = color;
	}
	
	@Override
	public int getColor() {
		return color;
	}

}
