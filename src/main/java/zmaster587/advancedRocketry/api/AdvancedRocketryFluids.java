package zmaster587.advancedRocketry.api;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;

/**
 * Stores AdvancedRocketry Fluids
 *
 */
public class AdvancedRocketryFluids {
	public static Fluid fluidOxygen;
	public static Fluid fluidHydrogen;
	public static Fluid fluidRocketFuel;
	public static Fluid fluidNitrogen;
	public static Fluid fluidEnrichedLava;
	private static Set<Fluid> gasses = new HashSet<Fluid>();
	
	// Registers a gas that can be spawned on a gas giant
	public static void registerGasGiantGas(Fluid gas)
	{
		gasses.add(gas);
	}
	
	public static Set<Fluid> getGasGiantGasses()
	{
		return gasses;
	}
	
	public static boolean isGasGiantGasRegistered(ResourceLocation name)
	{
		for(Fluid gas : getGasGiantGasses())
		{
			if(name.equals(gas.getRegistryName()))
				return true;
		}
		return false;
	}
	
	public static boolean isGasGiantGasRegistered(Fluid gasToCheck)
	{
		for(Fluid gas : getGasGiantGasses())
		{
			if(gas == gasToCheck)
				return true;
		}
		return false;
	}
}
