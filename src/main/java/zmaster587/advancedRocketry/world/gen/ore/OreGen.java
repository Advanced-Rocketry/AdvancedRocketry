package zmaster587.advancedRocketry.world.gen.ore;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.reflect.FieldUtils;

import net.minecraft.item.BlockItem;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;

public class OreGen {

	public static final ConfiguredFeature<?, ?> COPPER_ORE = register("ore_copper", Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241882_a, ((BlockItem)MaterialRegistry.getMaterialFromName("copper").getProduct(AllowedProducts.getProductByName("ORE")).getItem()).getBlock().getDefaultState(), ARConfiguration.getCurrentConfig().copperClumpSize.get() /*clump size*/ )).func_242733_d(128 /*max height */).func_242728_a().func_242731_b(40 /*min height */));
	public static final ConfiguredFeature<?, ?> TIN_ORE = register("ore_tin", Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241882_a, ((BlockItem)MaterialRegistry.getMaterialFromName("tin").getProduct(AllowedProducts.getProductByName("ORE")).getItem()).getBlock().getDefaultState(), ARConfiguration.getCurrentConfig().tinClumpSize.get() /*clump size*/ )).func_242733_d(128 /*max height */).func_242728_a().func_242731_b(20 /*min height */));
	public static final ConfiguredFeature<?, ?> RUTILE_ORE = register("ore_rutile", Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241882_a, ((BlockItem)MaterialRegistry.getMaterialFromName("rutile").getProduct(AllowedProducts.getProductByName("ORE")).getItem()).getBlock().getDefaultState(), ARConfiguration.getCurrentConfig().rutileClumpSize.get() /*clump size*/ )).func_242733_d(80 /*max height */).func_242728_a().func_242731_b(20 /*min height */));
	public static final ConfiguredFeature<?, ?> ALUMINUM_ORE = register("ore_aluminum", Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241882_a, ((BlockItem)MaterialRegistry.getMaterialFromName("aluminum").getProduct(AllowedProducts.getProductByName("ORE")).getItem()).getBlock().getDefaultState(), ARConfiguration.getCurrentConfig().aluminumClumpSize.get() /*clump size*/ )).func_242733_d(80 /*max height */).func_242728_a().func_242731_b(20 /*min height */));
	public static final ConfiguredFeature<?, ?> DILITHIUM_ORE = register("ore_dilithium", Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241882_a, ((BlockItem)MaterialRegistry.getMaterialFromName("dilithium").getProduct(AllowedProducts.getProductByName("ORE")).getItem()).getBlock().getDefaultState(), ARConfiguration.getCurrentConfig().dilithiumClumpSize.get() /*clump size*/ )).func_242733_d(80 /*max height */).func_242728_a().func_242731_b(20 /*min height */));

	private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String p_243968_0_, ConfiguredFeature<FC, ?> p_243968_1_) {
		return Registry.register(WorldGenRegistries.field_243653_e, p_243968_0_, p_243968_1_);
	}

	private static final int UNDERGROUND_ORES = GenerationStage.Decoration.UNDERGROUND_ORES.ordinal();

	public static void injectOreGen()
	{
		final Field features = ObfuscationReflectionHelper.findField(BiomeGenerationSettings.class, "field_242484_f");
		setMutable(features);

		updateAllFeatures(features);
		AdvancedRocketryBiomes.getAllBiomes().forEachRemaining((biome) ->
		{
			if(ARConfiguration.getCurrentConfig().generateCopper.get())
				getOreFeatures(biome).add(() -> COPPER_ORE);
			if(ARConfiguration.getCurrentConfig().generateTin.get())
				getOreFeatures(biome).add(() -> TIN_ORE);
			if(ARConfiguration.getCurrentConfig().generateRutile.get())
				getOreFeatures(biome).add(() -> RUTILE_ORE);
			if(ARConfiguration.getCurrentConfig().generateAluminum.get())
				getOreFeatures(biome).add(() -> ALUMINUM_ORE);
			if(ARConfiguration.getCurrentConfig().generateDilithium.get())
				getOreFeatures(biome).add(() -> DILITHIUM_ORE);
		});
	}

	/** Replace all feature arrays with mutable copies. */
	private static void updateAllFeatures(Field features) {
		for (Biome b : WorldGenRegistries.field_243657_i) {
			final BiomeGenerationSettings settings = b.func_242440_e();
			final List<List<Supplier<ConfiguredFeature<?, ?>>>> current = getValue(features, settings);
			final List<List<Supplier<ConfiguredFeature<?, ?>>>> values = Collections.synchronizedList(new LinkedList<>());
			current.forEach(list -> values.add(new LinkedList<>(list)));
			setValue(features, settings, values);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(Field f, Object instance) {
		try {
			return (T) f.get(instance);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
	public static void setValue(Field f, Object instance, Object value) {
		try {
			f.set(instance, value);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	/** Retrieves the current set of features in the ore phase. */
	private static List<Supplier<ConfiguredFeature<?, ?>>> getOreFeatures(Biome b) {
		final List<List<Supplier<ConfiguredFeature<?, ?>>>> features = b.func_242440_e().func_242498_c();
		while (features.size() <= UNDERGROUND_ORES) {
			features.add(new LinkedList<>());
		}
		return features.get(UNDERGROUND_ORES);
	}

	public static void setMutable(Field f) {
		FieldUtils.removeFinalModifier(f, true);
	}
}
