package zmaster587.advancedRocketry.api;

import zmaster587.advancedRocketry.util.FluidGasGiantGas;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.block.BlockEnrichedLava;
import zmaster587.libVulpes.LibVulpes;

/**
 * Stores AdvancedRocketry AdvancedRocketry.FLUIDS
 *
 */
public class AdvancedRocketryFluids {

	private static Set<FluidGasGiantGas> gasses = new HashSet<>();
	// Registers a gas that can be spawned on a gas giant
	public static void registerGasGiantGas(Fluid gas, int minGravity, int maxGravity, double chance) {
		gasses.add(new FluidGasGiantGas(gas, minGravity, maxGravity, chance));
	}
	
	public static Set<FluidGasGiantGas> getGasGiantGasses()
	{
		return gasses;
	}

	public static boolean isGasGiantGasRegistered(ResourceLocation name) {
		for(FluidGasGiantGas gas : getGasGiantGasses()) {
			if(name.equals(gas.getFluid().getRegistryName()))
				return true;
		}
		return false;
	}

	public static boolean isGasGiantGasRegistered(Fluid gasToCheck) {
		for(FluidGasGiantGas gas : getGasGiantGasses()) {
			if(gas.getFluid() == gasToCheck)
				return true;
		}
		return false;
	}



	private static final ResourceLocation notFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_still");
	private static final ResourceLocation flowing = new ResourceLocation("advancedrocketry:blocks/fluid/oxygen_flow");
	private static final ResourceLocation lavaNotFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/lava_still");
	private static final ResourceLocation lavaFlowing = new ResourceLocation("advancedrocketry:blocks/fluid/lava_flow");

	public static ForgeFlowingFluid.Properties makeOxygenProps() {
		return new ForgeFlowingFluid.Properties(oxygenStill, oxygenFlowing, FluidAttributes.builder(notFlowing, flowing).color(0xFF6CE2FF).density(800).viscosity(1500)).bucket(AdvancedRocketryItems.itemBucketOxygen).block(AdvancedRocketryBlocks.blockOxygenFluid);
	}
	public static RegistryObject<FlowingFluid> oxygenFlowing;
	public static RegistryObject<FlowingFluid> oxygenStill;

	public static ForgeFlowingFluid.Properties makeHydrogenProps() {
		return new ForgeFlowingFluid.Properties(hydrogenStill, hydrogenFlowing, FluidAttributes.builder(notFlowing, flowing).color(0xFFDBC1C1).density(800).viscosity(1500)).bucket(AdvancedRocketryItems.itemBucketHydrogen).block(AdvancedRocketryBlocks.blockHydrogenFluid);
	}
	public static RegistryObject<FlowingFluid> hydrogenFlowing;
	public static RegistryObject<FlowingFluid> hydrogenStill;

	public static ForgeFlowingFluid.Properties makeRocketFuelProps() {
		return new ForgeFlowingFluid.Properties(rocketFuelStill, rocketFuelFlowing, FluidAttributes.builder(notFlowing, flowing).color(0xFFDBF1FF).density(800).viscosity(1500)).bucket(AdvancedRocketryItems.itemBucketRocketFuel);
	}
	public static RegistryObject<FlowingFluid> rocketFuelFlowing;
	public static RegistryObject<FlowingFluid> rocketFuelStill;

	public static ForgeFlowingFluid.Properties makeNitrogenProps() {
		return new ForgeFlowingFluid.Properties(nitrogenStill, nitrogenFlowing, FluidAttributes.builder(notFlowing, flowing).color(0xFFDFE5FE).density(800).viscosity(1500)).bucket(AdvancedRocketryItems.itemBucketNitrogen).block(AdvancedRocketryBlocks.blockNitrogenFluid);
	}
	public static RegistryObject<FlowingFluid> nitrogenFlowing;
	public static RegistryObject<FlowingFluid> nitrogenStill;


	public static ForgeFlowingFluid.Properties makeEnrichedLavaProps() {
		return new ForgeFlowingFluid.Properties(enrichedLavaStill, enrichedLavaFlowing, FluidAttributes.builder(lavaNotFlowing, lavaFlowing).color(0xFFFFFFFF).luminosity(15).density(3000).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA).viscosity(6000).temperature(3000)).bucket(AdvancedRocketryItems.itemBucketEnrichedLava).block(AdvancedRocketryBlocks.blockEnrichedLavaFluid);
	}
	public static RegistryObject<FlowingFluid> enrichedLavaFlowing;
	public static RegistryObject<FlowingFluid> enrichedLavaStill;

	//Fluids
	public static void init() {
		oxygenFlowing = AdvancedRocketry.FLUIDS.register("oxygen_flowing", () -> new ForgeFlowingFluid.Flowing(makeOxygenProps()));
		oxygenStill = AdvancedRocketry.FLUIDS.register("oxygen", () -> new ForgeFlowingFluid.Source(makeOxygenProps()));

		hydrogenFlowing = AdvancedRocketry.FLUIDS.register("hydrogen_flowing", () -> new ForgeFlowingFluid.Flowing(makeHydrogenProps()));
		hydrogenStill = AdvancedRocketry.FLUIDS.register("hydrogen", () -> new ForgeFlowingFluid.Source(makeHydrogenProps()));

		rocketFuelFlowing = AdvancedRocketry.FLUIDS.register("rocket_fuel_flowing", () -> new ForgeFlowingFluid.Flowing(makeRocketFuelProps()));
		rocketFuelStill = AdvancedRocketry.FLUIDS.register("rocket_fuel", () -> new ForgeFlowingFluid.Source(makeRocketFuelProps()));

		nitrogenStill = AdvancedRocketry.FLUIDS.register("nitrogen", () -> new ForgeFlowingFluid.Source(makeNitrogenProps()));
		nitrogenFlowing = AdvancedRocketry.FLUIDS.register("nitrogen_flowing", () -> new ForgeFlowingFluid.Flowing(makeNitrogenProps()));

		enrichedLavaStill = AdvancedRocketry.FLUIDS.register("enriched_lava", () -> new ForgeFlowingFluid.Source(makeEnrichedLavaProps()));
		enrichedLavaFlowing = AdvancedRocketry.FLUIDS.register("enriched_lava_flowing", () -> new ForgeFlowingFluid.Flowing(makeEnrichedLavaProps()));

		AdvancedRocketryBlocks.blockOxygenFluid = AdvancedRocketry.BLOCKS.register("oxygenfluidblock", () -> new FlowingFluidBlock(AdvancedRocketryFluids.oxygenStill, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
		AdvancedRocketryBlocks.blockHydrogenFluid = AdvancedRocketry.BLOCKS.register("hydrogenfluidblock", () -> new FlowingFluidBlock(AdvancedRocketryFluids.hydrogenStill, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
		AdvancedRocketryBlocks.blockFuelFluid = AdvancedRocketry.BLOCKS.register("rocketfuelblock", () -> new FlowingFluidBlock(AdvancedRocketryFluids.rocketFuelStill, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
		AdvancedRocketryBlocks.blockNitrogenFluid = AdvancedRocketry.BLOCKS.register("nitrogenfluidblock", () -> new FlowingFluidBlock(AdvancedRocketryFluids.nitrogenStill, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
		AdvancedRocketryBlocks.blockEnrichedLavaFluid = AdvancedRocketry.BLOCKS.register("enrichedlavablock", () -> new BlockEnrichedLava(AdvancedRocketryFluids.enrichedLavaStill, AbstractBlock.Properties.create(Material.LAVA).tickRandomly().doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops().setLightLevel((p_235470_0_) -> 14)));


		Item.Properties bucketItem = new Item.Properties().group(LibVulpes.tabLibVulpesOres).containerItem(Items.BUCKET).maxStackSize(1);
		AdvancedRocketryItems.itemBucketRocketFuel = AdvancedRocketry.ITEMS.register("bucketrocketfuel", () -> new BucketItem(AdvancedRocketryFluids.rocketFuelStill, bucketItem));
		AdvancedRocketryItems.itemBucketNitrogen = AdvancedRocketry.ITEMS.register("bucketnitrogen", () -> new BucketItem(AdvancedRocketryFluids.nitrogenStill, bucketItem));
		AdvancedRocketryItems.itemBucketHydrogen = AdvancedRocketry.ITEMS.register("buckethydrogen", () -> new BucketItem(AdvancedRocketryFluids.hydrogenStill, bucketItem));
		AdvancedRocketryItems.itemBucketOxygen = AdvancedRocketry.ITEMS.register("bucketoxygen", () -> new BucketItem(AdvancedRocketryFluids.oxygenStill, bucketItem));
		AdvancedRocketryItems.itemBucketEnrichedLava = AdvancedRocketry.ITEMS.register("bucketenrichedlava", () -> new BucketItem(AdvancedRocketryFluids.enrichedLavaStill, bucketItem));
	}
}
