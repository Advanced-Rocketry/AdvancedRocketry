package zmaster587.advancedRocketry.api;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.BiomeGenerationSettings.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.BiomeAmbience.GrassColorModifier;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.GenerationStage.Carving;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import zmaster587.advancedRocketry.dimension.DimensionProperties.Temps;
import zmaster587.advancedRocketry.world.decoration.StructurePieceCrater;
import zmaster587.advancedRocketry.world.decoration.StructurePieceGeode;
import zmaster587.advancedRocketry.world.ChunkProviderPlanet;
import zmaster587.advancedRocketry.world.ChunkProviderSpace;
import zmaster587.advancedRocketry.world.CustomPlanetBiomeProvider;
import zmaster587.advancedRocketry.world.decoration.MapGenInvertedPillar;
import zmaster587.advancedRocketry.world.decoration.MapGenSwampTree;
import zmaster587.advancedRocketry.world.decoration.StructurePieceVolcano;
import zmaster587.advancedRocketry.world.decoration.StructureVolcano;
import zmaster587.advancedRocketry.world.decoration.StructureCrater;
import zmaster587.advancedRocketry.world.decoration.StructureGeode;
import zmaster587.advancedRocketry.world.gen.WorldGenAlienTree;
import zmaster587.advancedRocketry.world.gen.WorldGenCharredTree;
import zmaster587.advancedRocketry.world.gen.WorldGenLargeCrystal;

/**
 * Stores information relating to the biomes and biome registry of AdvancedRocketry
 */
public class AdvancedRocketryBiomes {

	public static final AdvancedRocketryBiomes instance = new AdvancedRocketryBiomes();
	private List<Biome> registeredBiomes;
	private List<Biome> registeredHighPressureBiomes;
	private List<Biome> registeredSingleBiome;
	private static List<ResourceLocation> blackListedBiomeIds;
	
	
	public static IStructurePieceType STRUCTURE_PIECE_CRATER;
	public static IStructurePieceType STRUCTURE_PIECE_VOLCANO;
	public static IStructurePieceType STRUCTURE_PIECE_GEODE;
	// Biome Providers
	static {
		Registry.register(Registry.BIOME_PROVIDER_CODEC, "planetary", CustomPlanetBiomeProvider.customPlanetCodec);
		Registry.register(Registry.CHUNK_GENERATOR_CODEC, "planetary_noise", ChunkProviderPlanet.planetCodec);
		Registry.register(Registry.CHUNK_GENERATOR_CODEC, "space", ChunkProviderSpace.planetCodec);
		
		
		STRUCTURE_PIECE_CRATER = Registry.register(Registry.STRUCTURE_PIECE, "craterpiece".toLowerCase(Locale.ROOT), StructurePieceCrater::new);
		STRUCTURE_PIECE_VOLCANO = Registry.register(Registry.STRUCTURE_PIECE, "volcanopiece".toLowerCase(Locale.ROOT), StructurePieceVolcano::new);
		STRUCTURE_PIECE_GEODE = Registry.register(Registry.STRUCTURE_PIECE, "geodePiece".toLowerCase(Locale.ROOT), StructurePieceGeode::new);
	}
	
	
	// Surface Builder config
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> MOON_LUNAR_LIGHT_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> MOON_LUNAR_DARK_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> HOTDRY_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> SPACE_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> GRAVEL_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> BASALT_CONFIG;

	private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> registerSurfaceBuilder(String p_244192_0_, ConfiguredSurfaceBuilder<SC> p_244192_1_) {
		return  WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, new ResourceLocation(Constants.modId, p_244192_0_), p_244192_1_);
	}

	public static void configureSurfaceBuilders() {
		MOON_LUNAR_LIGHT_CONFIG = registerSurfaceBuilder("lunargenerationlight", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(AdvancedRocketryBlocks.blockMoonTurf.getDefaultState(), AdvancedRocketryBlocks.blockMoonTurf.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
		MOON_LUNAR_DARK_CONFIG = registerSurfaceBuilder("lunargenerationdark", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(AdvancedRocketryBlocks.blockMoonTurfDark.getDefaultState(), AdvancedRocketryBlocks.blockMoonTurfDark.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
		HOTDRY_CONFIG = registerSurfaceBuilder("hotdrygeneration", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(AdvancedRocketryBlocks.blockOxidizedFerricSand.getDefaultState(), AdvancedRocketryBlocks.blockOxidizedFerricSand.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
		SPACE_CONFIG = registerSurfaceBuilder("spacegeneration", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState())));
		GRAVEL_CONFIG = registerSurfaceBuilder("gravel", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
		BASALT_CONFIG = registerSurfaceBuilder("basalt", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(AdvancedRocketryBlocks.blockBasalt.getDefaultState(), AdvancedRocketryBlocks.blockBasalt.getDefaultState(), AdvancedRocketryBlocks.blockBasalt.getDefaultState())));
	}

	// End surface Builder config

	// Structure features
	public static Structure<ProbabilityConfig> CRATER = new StructureCrater(ProbabilityConfig.CODEC);
	public static Structure<ProbabilityConfig> VOLCANO = new StructureVolcano(ProbabilityConfig.CODEC);
	public static Structure<ProbabilityConfig> GEODE = new StructureGeode(ProbabilityConfig.CODEC);
	public static StructureFeature<ProbabilityConfig, ? extends Structure<ProbabilityConfig>> CONFIGURED_CRATER = CRATER.withConfiguration(new ProbabilityConfig(0.0000001F));
	public static StructureFeature<ProbabilityConfig, ? extends Structure<ProbabilityConfig>> CONFIGURED_VOLCANO = VOLCANO.withConfiguration(new ProbabilityConfig(0.0000001F));
	public static StructureFeature<ProbabilityConfig, ? extends Structure<ProbabilityConfig>> CONFIGURED_GEODE = GEODE.withConfiguration(new ProbabilityConfig(0.0000001F));
	
	public static void registerStructures(RegistryEvent.Register<Structure<?>> evt) {
		evt.getRegistry().register(VOLCANO.setRegistryName("volcano"));
		evt.getRegistry().register(CRATER.setRegistryName("crater"));
		evt.getRegistry().register(GEODE.setRegistryName("geode"));
		
		Structure.NAME_STRUCTURE_BIMAP.put("volcano", VOLCANO);
	    Structure.NAME_STRUCTURE_BIMAP.put("crater", CRATER);
	    Structure.NAME_STRUCTURE_BIMAP.put("geode", GEODE);
	    
	    Field decorationStageField = ObfuscationReflectionHelper.findField(Structure.class, "field_236385_u_");
	    decorationStageField.setAccessible(true);
	    
	    Map<Structure<?>, GenerationStage.Decoration> decoractionStage;
		try {
			decoractionStage = (Map<Structure<?>, GenerationStage.Decoration>)decorationStageField.get(null);
			
		    decoractionStage.put(VOLCANO, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
		    decoractionStage.put(CRATER, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
		    decoractionStage.put(GEODE, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}


		WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, "volcano", CONFIGURED_VOLCANO);
		WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, "crater", CONFIGURED_CRATER);
		WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, "geode", CONFIGURED_GEODE);
	}
	
	   
    // End structure features
	
	// Start Carver config
	public static WorldCarver<ProbabilityConfig> BIG_TREE = new MapGenSwampTree(ProbabilityConfig.CODEC, 256);
	public static WorldCarver<ProbabilityConfig> INVERTED_PILLAR = new MapGenInvertedPillar(ProbabilityConfig.CODEC, 256, Blocks.STONE.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.DIRT.getDefaultState());

	public static ConfiguredCarver<ProbabilityConfig> CONFIGURED_BIG_TREE = configureCarver("big_tree", BIG_TREE.func_242761_a(new ProbabilityConfig(0.02F)));
	public static ConfiguredCarver<ProbabilityConfig> CONFIGURED_INVERTED_PILLAR = configureCarver("inverted_pillar", INVERTED_PILLAR.func_242761_a(new ProbabilityConfig(0.02F)));

	public static void registerCarvers(RegistryEvent.Register<WorldCarver<?>> evt) {
		evt.getRegistry().register(BIG_TREE.setRegistryName("swamp_tree"));
		evt.getRegistry().register(INVERTED_PILLAR.setRegistryName("inverted_pillar"));
		
	}

	private static <WC extends ICarverConfig> ConfiguredCarver<WC> configureCarver(String p_243773_0_, ConfiguredCarver<WC> p_243773_1_) {
		return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_CARVER, new ResourceLocation(Constants.modId, p_243773_0_), p_243773_1_);
	}
	// End Carver config

	// feature config
	public static Feature<NoFeatureConfig> CRYSTAL_FEATURE;
	
	public static ConfiguredFeature<BaseTreeFeatureConfig, ?> ALIEN_TREE;
	public static ConfiguredFeature<BaseTreeFeatureConfig, ?> CHARRED_TREE;
	public static ConfiguredFeature<NoFeatureConfig, ?> CRYSTAL_SPIRE;
	   
	public static void initFeature() {
		CRYSTAL_FEATURE = new WorldGenLargeCrystal(NoFeatureConfig.CODEC);

		ALIEN_TREE = registerTree("alientree", Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(AdvancedRocketryBlocks.blockLightwoodLog.getDefaultState()), new SimpleBlockStateProvider(AdvancedRocketryBlocks.blockLightwoodLeaves.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.create(2), FeatureSpread.create(0), 3), new WorldGenAlienTree(4, 2, 0), new TwoLayerFeature(1, 0, 1))).setIgnoreVines().build()));
		CHARRED_TREE = registerTree("charredtree", Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(AdvancedRocketryBlocks.blockCharcoalLog.getDefaultState()), new SimpleBlockStateProvider(Blocks.AIR.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.create(0), FeatureSpread.create(0), 0), new WorldGenCharredTree(4, 2, 0, 0), new TwoLayerFeature(1, 0, 1))).setIgnoreVines().build()));
		CRYSTAL_SPIRE = registerTree("crystal", CRYSTAL_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG));		
	}
	
	public static void registerFeature(RegistryEvent.Register<Feature<?>> evt) {
		if(CRYSTAL_FEATURE == null)
			initFeature();
		evt.getRegistry().register(CRYSTAL_FEATURE.setRegistryName("largecrystal"));
		
	}

	private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> registerTree(String p_243968_0_, ConfiguredFeature<FC, ?> p_243968_1_) {
		return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(Constants.modId, p_243968_0_), p_243968_1_);
	}

	// BiomeGeneration settings
	private static BiomeGenerationSettings barren;
	private static BiomeGenerationSettings barrenDark;
	private static BiomeGenerationSettings hotDry;
	private static BiomeGenerationSettings genalienForest;
	private static BiomeGenerationSettings spaceBiomeGen;
	private static BiomeGenerationSettings genCharredLand;
	private static BiomeGenerationSettings genCrystalLandscape;
	private static BiomeGenerationSettings genSwamp;
	private static BiomeGenerationSettings genOceanSpires;
	private static BiomeGenerationSettings genVolcanicBasalt;
	private static BiomeGenerationSettings genVolcanicBasaltBarren;
	
	public static void registerBiomeGenerationSettings() {
		configureSurfaceBuilders();
		barren = createBuilder(MOON_LUNAR_LIGHT_CONFIG, true).withCarver(Carving.AIR, ConfiguredCarvers.CAVE).build();
		barrenDark = createBuilder(MOON_LUNAR_DARK_CONFIG, true).withCarver(Carving.AIR, ConfiguredCarvers.CAVE).build();
		hotDry = createBuilder(HOTDRY_CONFIG, true).build();
		genalienForest = createBuilder(ConfiguredSurfaceBuilders.GRASS, false).withFeature(Decoration.VEGETAL_DECORATION, ALIEN_TREE).build();
		spaceBiomeGen = new BiomeGenerationSettings.Builder().withSurfaceBuilder(SPACE_CONFIG).build();
		genCharredLand = createBuilder(ConfiguredSurfaceBuilders.GRASS, false).withFeature(Decoration.VEGETAL_DECORATION, CHARRED_TREE).build();
		genCrystalLandscape = createBuilder(ConfiguredSurfaceBuilders.ICE_SPIKES, false).withFeature(Decoration.SURFACE_STRUCTURES, CRYSTAL_SPIRE).build();
		
		Builder swampBuilder = createBuilder(ConfiguredSurfaceBuilders.SWAMP, false).withCarver(Carving.AIR, CONFIGURED_BIG_TREE).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_SWAMP);
		DefaultBiomeFeatures.withSwampVegetation(swampBuilder);
		genSwamp = swampBuilder.build();
		
		genOceanSpires = createBuilder(GRAVEL_CONFIG, false).withCarver(Carving.AIR, CONFIGURED_INVERTED_PILLAR).build();
		genVolcanicBasalt = createBuilder(BASALT_CONFIG, false).withStructure(CONFIGURED_VOLCANO).withFeature(Decoration.VEGETAL_DECORATION, CHARRED_TREE).build();
		genVolcanicBasaltBarren = createBuilder(BASALT_CONFIG, false).withStructure(CONFIGURED_VOLCANO).build();
		
	}

	private static BiomeGenerationSettings.Builder createBuilder(ConfiguredSurfaceBuilder<?> surface, boolean space) {
		BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder();
		builder = builder.withSurfaceBuilder(surface);
		if(!space) {
			DefaultBiomeFeatures.withCavesAndCanyons(builder);
		}
		//Add ores
		DefaultBiomeFeatures.withOverworldOres(builder);
		

		return builder;
	}


	public static Biome.Builder makeSwamp(boolean p_244236_2_) {
		MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
		DefaultBiomeFeatures.withPassiveMobs(mobspawninfo$builder);
		DefaultBiomeFeatures.withBatsAndHostiles(mobspawninfo$builder);
		mobspawninfo$builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SLIME, 1, 1, 1));
		BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ConfiguredSurfaceBuilders.SWAMP);
		if (!p_244236_2_) {
			biomegenerationsettings$builder.withStructure(StructureFeatures.SWAMP_HUT);
		}

		biomegenerationsettings$builder.withStructure(StructureFeatures.MINESHAFT);
		biomegenerationsettings$builder.withStructure(StructureFeatures.MINESHAFT);
		DefaultBiomeFeatures.withCavesAndCanyons(biomegenerationsettings$builder);
		if (!p_244236_2_) {
			DefaultBiomeFeatures.withFossils(biomegenerationsettings$builder);
		}

		DefaultBiomeFeatures.withLavaAndWaterLakes(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withMonsterRoom(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withCommonOverworldBlocks(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withOverworldOres(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withClayDisks(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withSwampVegetation(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withNormalMushroomGeneration(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withSwampSugarcaneAndPumpkin(biomegenerationsettings$builder);
		DefaultBiomeFeatures.withLavaAndWaterSprings(biomegenerationsettings$builder);
		if (p_244236_2_) {
			DefaultBiomeFeatures.withFossils(biomegenerationsettings$builder);
		} else {
			biomegenerationsettings$builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.SEAGRASS_SWAMP);
		}

		DefaultBiomeFeatures.withFrozenTopLayer(biomegenerationsettings$builder);
		return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.SWAMP).setEffects((new BiomeAmbience.Builder()).setWaterColor(6388580).setWaterFogColor(2302743).setFogColor(12638463).withSkyColor(getHue(0.8F)).withFoliageColor(6975545).withGrassColorModifier(BiomeAmbience.GrassColorModifier.SWAMP).setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build()).withMobSpawnSettings(mobspawninfo$builder.build()).withGenerationSettings(biomegenerationsettings$builder.build());
	}

	private static int getHue(float p_244206_0_) {
		float lvt_1_1_ = p_244206_0_ / 3.0F;
		lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
		return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}

	public static Biome moonBiome;
	public static Biome hotDryBiome;
	//int foliagecolor = 0x55ffe1;
	//int grasscolor = 0x7777ff
	public static Biome alienForest;
	public static Biome spaceBiome;

	// grasscolor = 0x202020
	// has mushrooms
	public static Biome stormLandsBiome;
	public static Biome crystalChasms;
	public static Biome swampDeepBiome;
	public static Biome marsh;
	public static Biome oceanSpires;
	public static Biome moonBiomeDark;
	public static Biome volcanic;
	public static Biome volcanicBarren;


	private AdvancedRocketryBiomes() {
		registeredBiomes = new ArrayList<>();
		registeredHighPressureBiomes = new LinkedList<>();
		blackListedBiomeIds = new ArrayList<>();
		registeredSingleBiome = new ArrayList<>();
	}

	
	private static int getSkyColor(float p_244206_0_) {
	    float lvt_1_1_ = p_244206_0_ / 3.0F;
	    lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
	    return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}
	
	public static void registerBiomes(RegistryEvent.Register<Biome> evt) {
		if(CRYSTAL_FEATURE == null)
			initFeature();
		
        /*return new BiomeAmbience(this.field_235230_a_.orElseThrow(() -> {
            return new IllegalStateException("Missing 'fog' color.");
         }), this.field_235231_b_.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water' color.");
         }), this.field_235232_c_.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water fog' color.");
         }), this.field_242533_d.orElseThrow(() -> {
            return new IllegalStateException("Missing 'sky' color.");*/
		
		registerBiomeGenerationSettings();
		BiomeAmbience noAmbience = new BiomeAmbience.Builder().withSkyColor(/*sky color*/ getSkyColor(0.6F)).setWaterColor(/*water color*/  4159204).setWaterFogColor(/*water fog color*/ 329011).setFogColor( /*fog color*/ 12638463).build();
		MobSpawnInfo noMobs = new MobSpawnInfo.Builder().build();
		
		moonBiome = (new Biome.Builder()).category(Category.NONE).precipitation(RainType.NONE).depth(1f).downfall(0).scale(0.02f).temperature(0.3f).withGenerationSettings(barren).setEffects(noAmbience).withMobSpawnSettings(noMobs).build();
		hotDryBiome = new Biome.Builder().category(Category.NONE).precipitation(RainType.NONE).depth(1f).scale(0.01f).downfall(0).temperature(0.9f).withGenerationSettings(hotDry).setEffects(noAmbience).withMobSpawnSettings(noMobs).build();
		//int foliagecolor = 0x55ffe1;
		//int grasscolor = 0x7777ff
		alienForest = new Biome.Builder().category(Category.FOREST).precipitation(RainType.RAIN).depth(1f).downfall(0).scale(0.2f).temperature(0.6f).withGenerationSettings(genalienForest).setEffects(new BiomeAmbience.Builder().withGrassColorModifier(GrassColorModifier.DARK_FOREST).withSkyColor(/*sky color*/ getSkyColor(0.6F)).setWaterColor(/*water color*/  4159204).setWaterFogColor(/*water fog color*/ 329011).setFogColor( /*fog color*/ 12638463).build()).withMobSpawnSettings(noMobs).build();
		spaceBiome = new Biome.Builder().category(Category.NONE).downfall(0).precipitation(RainType.NONE).depth(-2f).scale(0).temperature(1f).withGenerationSettings(spaceBiomeGen).setEffects(noAmbience).withMobSpawnSettings(noMobs).build();

		// grasscolor = 0x202020
		// has mushrooms
		stormLandsBiome = new Biome.Builder().category(Category.FOREST).precipitation(RainType.RAIN).temperature(0.6f).downfall(2f).depth(.25f).scale(0.1f).withGenerationSettings(genCharredLand).setEffects(new BiomeAmbience.Builder().withGrassColorModifier(GrassColorModifier.DARK_FOREST).withSkyColor(/*sky color*/ getSkyColor(0.6F)).setWaterColor(/*water color*/  4159204).setWaterFogColor(/*water fog color*/ 329011).setFogColor( /*fog color*/ 12638463).build()).withMobSpawnSettings(noMobs).build();
		crystalChasms = new Biome.Builder().category(Category.PLAINS).precipitation(RainType.SNOW).depth(.25f).scale(0.02f).temperature(0.3f).downfall(1f).withGenerationSettings(genCrystalLandscape).setEffects(new BiomeAmbience.Builder().withGrassColorModifier(GrassColorModifier.DARK_FOREST).withSkyColor(/*sky color*/ getSkyColor(0.6F)).setWaterColor(/*water color*/  4159204).setWaterFogColor(/*water fog color*/ 329011).setFogColor( /*fog color*/ 12638463).build()).withMobSpawnSettings(noMobs).build();
		swampDeepBiome = new Biome.Builder().category(Category.SWAMP).precipitation(RainType.RAIN).depth(-0.1f).withGenerationSettings(genSwamp).scale(0.02f).downfall(0.9f).temperature(0.9f).setEffects(new BiomeAmbience.Builder().withGrassColorModifier(GrassColorModifier.DARK_FOREST).withSkyColor(/*sky color*/ getSkyColor(0.6F)).setWaterColor(/*water color*/  4159204).setWaterFogColor(/*water fog color*/ 329011).setFogColor( /*fog color*/ 12638463).build()).withMobSpawnSettings(noMobs).build();
		marsh = makeSwamp(false).category(Category.PLAINS).precipitation(RainType.RAIN).downfall(0.7f).depth(-0.4f).temperature(0.5f).scale(0f).setEffects(noAmbience).withMobSpawnSettings(noMobs).build();
		oceanSpires = new Biome.Builder().category(Category.BEACH).precipitation(RainType.RAIN).downfall(0.7f).depth(-0.5f).temperature(0.5f).scale(0f).withGenerationSettings(genOceanSpires).setEffects(noAmbience).withMobSpawnSettings(noMobs).build();
		moonBiomeDark  = new Biome.Builder().category(Category.NONE).precipitation(RainType.NONE).depth(1f).downfall(0).scale(0.02f).temperature(0.3f).withGenerationSettings(barrenDark).setEffects(noAmbience).withMobSpawnSettings(noMobs).build();
		volcanic = new Biome.Builder().category(Category.NONE).precipitation(RainType.NONE).depth(0f).downfall(0.0f).scale(0.09f).temperature(1.0f).withGenerationSettings(genVolcanicBasalt).setEffects(noAmbience).withMobSpawnSettings(noMobs).build();
		volcanicBarren = new Biome.Builder().category(Category.NONE).precipitation(RainType.NONE).downfall(0.0f).depth(0f).scale(0.09f).temperature(1.0f).withGenerationSettings(genVolcanicBasaltBarren).setEffects(noAmbience).withMobSpawnSettings(noMobs).build();

		evt.getRegistry().registerAll(
				moonBiome.setRegistryName("moon"),
				hotDryBiome.setRegistryName("hotdry"),
				/*alienForest.setRegistryName("alien_forest"),*/
				spaceBiome.setRegistryName("space"),
				stormLandsBiome.setRegistryName("stormland"),
				crystalChasms.setRegistryName("crystal_chasm"),
				swampDeepBiome.setRegistryName("deepswamp"),
				marsh.setRegistryName("marsh"),
				oceanSpires.setRegistryName("oceanspires"),
				moonBiomeDark.setRegistryName("moon_dark"),
				volcanic.setRegistryName("volcanic"),
				volcanicBarren.setRegistryName("volcanic_barren")
				);
	}



	/**
	 * Registers biomes you don't want to spawn on any planet unless registered with highpressure or similar feature
	 */
	public void registerBlackListBiome(Biome biome) {
		blackListedBiomeIds.add(getBiomeResource(biome));
	}

	/**
	 * Gets a list of the blacklisted Biome Ids
	 */
	public List<ResourceLocation> getBlackListedBiomes() {
		return blackListedBiomeIds;
	}

	/**
	 * Registers a biome as high pressure for use with the planet generators (It will only spawn on planets with high pressure)
	 * @param biome biome to register
	 */
	public void registerHighPressureBiome(Biome biome) {
		registeredHighPressureBiomes.add(biome);
		registerBlackListBiome(biome);
	}

	public List<Biome> getHighPressureBiomes() {
		return registeredHighPressureBiomes;	
	}

	/**
	 * Registers a biome to have a chance to spawn as the only biome on a planet, will not register the biome if it is in the blacklist already
	 * @param biome biome to register
	 */
	public void registerSingleBiome(Biome biome) {
		if(!blackListedBiomeIds.contains(getBiomeResource(biome)))
			registeredSingleBiome.add(biome);
	}

	public void blackListVanillaBiomes() {
		//Good grief... this is long, better than making users do it though..
		/*for(int i = 0; i < 40; i++)
			blackListedBiomeIds.add(i);
		Biomes
		blackListedBiomeIds.add(127);
		blackListedBiomeIds.add(129);
		blackListedBiomeIds.add(130);
		blackListedBiomeIds.add(131);
		blackListedBiomeIds.add(132);
		blackListedBiomeIds.add(133);
		blackListedBiomeIds.add(134);
		blackListedBiomeIds.add(140);
		blackListedBiomeIds.add(149);
		blackListedBiomeIds.add(151);
		blackListedBiomeIds.add(155);
		blackListedBiomeIds.add(156);
		blackListedBiomeIds.add(157);
		blackListedBiomeIds.add(158);
		blackListedBiomeIds.add(160);
		blackListedBiomeIds.add(161);
		blackListedBiomeIds.add(162);
		blackListedBiomeIds.add(163);
		blackListedBiomeIds.add(164);
		blackListedBiomeIds.add(165);
		blackListedBiomeIds.add(166);
		blackListedBiomeIds.add(167);*/
	}

	public List<Biome> getSingleBiome() {
		return registeredSingleBiome;	
	}

	public static Biome getBiome(String string) {
		Biome biome;
		biome = getBiomeFromResourceLocation(new ResourceLocation(string));

		return biome;
	}


	public static Temps getBiomeTemp(Biome biome) {
		return Temps.getTempFromValue((int) (biome.getTemperature(new BlockPos(0,100,0))*Temps.HOT.getTemp()));
	}

	public static Iterator<Biome> getAllBiomes()
	{
		return getForgeRegistry().iterator();
	}

	public static ResourceLocation getBiomeResource(Biome biome)
	{
		return getForgeRegistry().getKey(biome);
	}

	public static Biome getBiomeFromResourceLocation(ResourceLocation key)
	{
		return getForgeRegistry().getValue(key);
	}

	public static boolean doesBiomeExist(ResourceLocation key)
	{
		return getForgeRegistry().containsKey(key);
	}

	public static int getBiomeId(Biome key)
	{
		return getBiomeRegistry().getId(key);
	}

	public static int getBiomeId(ResourceLocation key) {
		return getBiomeRegistry().getId(getBiomeFromResourceLocation(key));
	}

	private static IForgeRegistry<Biome> getForgeRegistry()
	{
		return ForgeRegistries.BIOMES;
	}
	
	public static Registry<Biome> getBiomeRegistry() {
		return DynamicRegistries.func_239770_b_().func_230521_a_(Registry.BIOME_KEY).get();
	}

}
