package zmaster587.advancedRocketry.util;

import net.minecraftforge.fluids.Fluid;

public class FluidUtils {

	
	public static boolean areFluidsSameType(Fluid in, Fluid otherFluid) {
		return in != null && otherFluid != null && in.getName().equals(otherFluid.getName());
	}
}
