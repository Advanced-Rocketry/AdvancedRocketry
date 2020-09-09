package zmaster587.advancedRocketry.api;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraftforge.fluids.Fluid;

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
	
	public static boolean isGasGiantGasRegistered(String name)
	{
		for(Fluid gas : getGasGiantGasses())
		{
			if(name.equals(gas.getName()))
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
