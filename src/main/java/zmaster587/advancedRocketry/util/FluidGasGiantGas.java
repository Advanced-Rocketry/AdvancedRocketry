package zmaster587.advancedRocketry.util;

import net.minecraft.fluid.Fluid;

public class FluidGasGiantGas {

	private final Fluid fluid;
	private final int minGravity;
	private final int maxGravity;
	private final double chance;

	public FluidGasGiantGas(Fluid fluidInput, int minGravityInput, int maxGravityInput, double chanceInput) {
		fluid = fluidInput;
		minGravity = minGravityInput;
		maxGravity = maxGravityInput;
		chance = chanceInput;

	}

	public Fluid getFluid () {return fluid;}

	public int getMinGravity () {return minGravity;}

	public int getMaxGravity () {return maxGravity;}

	public double getChance () {return chance;}

}
