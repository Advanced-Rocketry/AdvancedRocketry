package zmaster587.advancedRocketry.api;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import zmaster587.advancedRocketry.AdvancedRocketry;

/**
 * Stores AdvancedRocketry AdvancedRocketry.FLUIDS
 *
 */
public class AdvancedRocketryFluids {
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
	
	
	
	private static final ResourceLocation notFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_still");
	private static final ResourceLocation flowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_flow");
	private static final ResourceLocation lavaNotFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/lava_still");
	private static final ResourceLocation lavaFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/lava_flow");
	
	public static ForgeFlowingFluid.Properties makeOxygenProps()
	{
		return new ForgeFlowingFluid.Properties(oxygenStill, oxygenFlowing, FluidAttributes.builder(notFlowing, flowing).color(0xFF8f94b9).density(800).viscosity(1500)).bucket(() -> AdvancedRocketryItems.itemBucketOxygen).block(() -> AdvancedRocketryBlocks.blockOxygenFluid);
	}
	
	public static RegistryObject<FlowingFluid> oxygenFlowing = AdvancedRocketry.FLUIDS.register("oxygen_flowing", () ->
		new ForgeFlowingFluid.Source(makeOxygenProps())
	);
	
	public static RegistryObject<FlowingFluid> oxygenStill = AdvancedRocketry.FLUIDS.register("oxygen", () ->
		new ForgeFlowingFluid.Source(makeOxygenProps())
	);
	
	public static ForgeFlowingFluid.Properties makeHydrogenProps()
	{
		return new ForgeFlowingFluid.Properties(hydrogenStill, hydrogenFlowing, FluidAttributes.builder(notFlowing, flowing).color(0xFFdbc1c1).density(800).viscosity(1500)).bucket(() -> AdvancedRocketryItems.itemBucketHydrogen).block(() -> AdvancedRocketryBlocks.blockHydrogenFluid);
	}
	
	public static RegistryObject<FlowingFluid> hydrogenFlowing = AdvancedRocketry.FLUIDS.register("hydrogen_flowing", () ->
		new ForgeFlowingFluid.Source(makeHydrogenProps())
	);
	
	public static RegistryObject<FlowingFluid> hydrogenStill = AdvancedRocketry.FLUIDS.register("hydrogen", () ->
		new ForgeFlowingFluid.Source(makeHydrogenProps())
	);
	
	public static ForgeFlowingFluid.Properties makeRocketFuelProps()
	{
		return new ForgeFlowingFluid.Properties(rocketFuelStill, rocketFuelFlowing, FluidAttributes.builder(notFlowing, flowing).color(0xFFe5d884).density(800).viscosity(1500)).bucket(() -> AdvancedRocketryItems.itemBucketRocketFuel);
	}
	
	public static RegistryObject<FlowingFluid> rocketFuelFlowing = AdvancedRocketry.FLUIDS.register("rocket_fuel_flowing", () ->
		new ForgeFlowingFluid.Source(makeRocketFuelProps())
	);
	
	public static RegistryObject<FlowingFluid> rocketFuelStill = AdvancedRocketry.FLUIDS.register("rocket_fuel", () ->
		new ForgeFlowingFluid.Source(makeRocketFuelProps())
	);
	
	public static ForgeFlowingFluid.Properties makeNitrogenProps()
	{
		return new ForgeFlowingFluid.Properties(nitrogenStill, nitrogenFlowing, FluidAttributes.builder(notFlowing, flowing).color(0xFF97a7e7).density(800).viscosity(1500)).bucket(() -> AdvancedRocketryItems.itemBucketNitrogen).block(() -> AdvancedRocketryBlocks.blockNitrogenFluid);
	}
	
	public static RegistryObject<FlowingFluid> nitrogenFlowing = AdvancedRocketry.FLUIDS.register("nitrogen_flowing", () ->
		new ForgeFlowingFluid.Source(makeNitrogenProps())
	);
	
	public static RegistryObject<FlowingFluid> nitrogenStill = AdvancedRocketry.FLUIDS.register("nitrogen", () ->
		new ForgeFlowingFluid.Source(makeNitrogenProps())
	);
	
	
	public static ForgeFlowingFluid.Properties makeEnrichedLavaProps()
	{
		return new ForgeFlowingFluid.Properties(enrichedLavaStill, enrichedlavaFlowing, FluidAttributes.builder(lavaNotFlowing, lavaFlowing).color(0xFFFFFFFF).density(3000).viscosity(6000).temperature(3000)).bucket(() -> AdvancedRocketryItems.itemBucketEnrichedLava).block(() -> AdvancedRocketryBlocks.blockEnrichedLavaFluid);
	}
	
	public static RegistryObject<FlowingFluid> enrichedlavaFlowing = AdvancedRocketry.FLUIDS.register("enriched_lava_flowing", () ->
		new ForgeFlowingFluid.Source(makeRocketFuelProps())
	);
	
	public static RegistryObject<FlowingFluid> enrichedLavaStill = AdvancedRocketry.FLUIDS.register("enriched_lava", () ->
		new ForgeFlowingFluid.Source(makeRocketFuelProps())
	);
}
