package zmaster587.advancedRocketry.api;

import net.minecraftforge.fluids.Fluid;
import zmaster587.advancedRocketry.util.FluidGasGiantGas;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores AdvancedRocketry Fluids
 */
public class AdvancedRocketryFluids {
    public static Fluid fluidOxygen;
    public static Fluid fluidHydrogen;
    public static Fluid fluidRocketFuel;
    public static Fluid fluidNitrogen;
    public static Fluid fluidEnrichedLava;
    private static Set<FluidGasGiantGas> gasses = new HashSet<>();

    // Registers a gas that can be spawned on a gas giant
    public static void registerGasGiantGas(Fluid gas, int minGravity, int maxGravity, double chance) {
        gasses.add(new FluidGasGiantGas(gas, minGravity, maxGravity, chance));
    }

    public static Set<FluidGasGiantGas> getGasGiantGasses() {
        return gasses;
    }

    public static boolean isGasGiantGasRegistered(String name) {
        for (FluidGasGiantGas gas : getGasGiantGasses()) {
            if (name.equals(gas.getFluid().getName()))
                return true;
        }
        return false;
    }

    public static boolean isGasGiantGasRegistered(Fluid gasToCheck) {
        for (FluidGasGiantGas gas : getGasGiantGasses()) {
            if (gas.getFluid() == gasToCheck)
                return true;
        }
        return false;
    }
}
