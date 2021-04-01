package zmaster587.advancedRocketry.util;

import net.minecraftforge.fluids.Fluid;

public class FluidGasGiantGas {

	private Fluid fluid;
	private int minGravity;
	private int maxGravity;
	private double chance;

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
