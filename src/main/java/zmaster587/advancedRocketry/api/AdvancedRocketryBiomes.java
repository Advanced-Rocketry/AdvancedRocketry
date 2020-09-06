package zmaster587.advancedRocketry.api;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.BiomeAmbience.GrassColorModifier;
import net.minecraft.world.biome.BiomeAmbience;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.GenerationStage.Carving;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import net.minecraft.world.gen.feature.MultipleRandomFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MoodSoundAmbience;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import zmaster587.advancedRocketry.dimension.DimensionProperties.Temps;
import zmaster587.advancedRocketry.world.decoration.MapGenCrater;
import zmaster587.advancedRocketry.world.decoration.MapGenInvertedPillar;
import zmaster587.advancedRocketry.world.decoration.MapGenVolcano;
import zmaster587.advancedRocketry.world.gen.WorldGenAlienTree;
import zmaster587.advancedRocketry.world.gen.WorldGenCharredTree;
import zmaster587.advancedRocketry.world.gen.WorldGenLargeCrystal;
import zmaster587.advancedRocketry.world.gen.WorldGenSwampTree;


/**
 * Stores information relating to the biomes and biome registry of AdvancedRocketry
 */
public class AdvancedRocketryBiomes {

	public static final AdvancedRocketryBiomes instance = new AdvancedRocketryBiomes();
	private List<Biome> registeredBiomes;
	private List<Biome> registeredHighPressureBiomes;
	private List<Biome> registeredSingleBiome;
	private static List<ResourceLocation> blackListedBiomeIds;

	// Surface Builder config
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> MOON_LUNAR_LIGHT_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> MOON_LUNAR_DARK_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> HOTDRY_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> SPACE_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> GRAVEL_CONFIG;
	public static ConfiguredSurfaceBuilder<SurfaceBuilderConfig> BASALT_CONFIG;

	private static <SC extends ISurfaceBuilderConfig> ConfiguredSurfaceBuilder<SC> registerSurfaceBuilder(String p_244192_0_, ConfiguredSurfaceBuilder<SC> p_244192_1_) {
		return  WorldGenRegistries.func_243663_a(WorldGenRegistries.field_243651_c, p_244192_0_, p_244192_1_);
	}

	public static void configureSurfaceBuilders()
	{
		MOON_LUNAR_LIGHT_CONFIG = registerSurfaceBuilder("lunargenerationlight", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(AdvancedRocketryBlocks.blockMoonTurf.getDefaultState(), AdvancedRocketryBlocks.blockMoonTurf.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
		MOON_LUNAR_DARK_CONFIG = registerSurfaceBuilder("lunargenerationdark", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(AdvancedRocketryBlocks.blockMoonTurfDark.getDefaultState(), AdvancedRocketryBlocks.blockMoonTurfDark.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
		HOTDRY_CONFIG = registerSurfaceBuilder("hotdrygeneration", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(AdvancedRocketryBlocks.blockHotTurf.getDefaultState(), AdvancedRocketryBlocks.blockHotTurf.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
		SPACE_CONFIG = registerSurfaceBuilder("spacegeneration", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState())));
		GRAVEL_CONFIG = registerSurfaceBuilder("gravel", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
		BASALT_CONFIG = registerSurfaceBuilder("basalt", SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(AdvancedRocketryBlocks.blockBasalt.getDefaultState(), AdvancedRocketryBlocks.blockBasalt.getDefaultState(), AdvancedRocketryBlocks.blockBasalt.getDefaultState())));
	}

	// End surface Builder config


	// Start Carver config
	public static WorldCarver<ProbabilityConfig> CRATER = (WorldCarver<ProbabilityConfig>) new MapGenCrater(ProbabilityConfig.field_236576_b_, 256);
	public static WorldCarver<ProbabilityConfig> BIG_TREE = new WorldGenSwampTree(ProbabilityConfig.field_236576_b_, 256);
	public static WorldCarver<ProbabilityConfig> INVERTED_PILLAR = new MapGenInvertedPillar(ProbabilityConfig.field_236576_b_, 256, Blocks.STONE.getDefaultState(), Blocks.GRAVEL.getDefaultState(), Blocks.DIRT.getDefaultState());
	public static WorldCarver<ProbabilityConfig> VOLCANO = new MapGenVolcano(ProbabilityConfig.field_236576_b_, 256);

	public static ConfiguredCarver<ProbabilityConfig> CONFIGURED_BIG_TREE = configureCarver("big_tree", BIG_TREE.func_242761_a(new ProbabilityConfig(0.02F)));
	public static ConfiguredCarver<ProbabilityConfig> CONFIGURED_INVERTED_PILLAR = configureCarver("inverted_pillar", INVERTED_PILLAR.func_242761_a(new ProbabilityConfig(0.02F)));
	public static ConfiguredCarver<ProbabilityConfig> CONFIGURED_VOLCANO = configureCarver("volcano", VOLCANO.func_242761_a(new ProbabilityConfig(0.02F)));

	public static void registerCarvers(RegistryEvent.Register<WorldCarver<?>> evt)
	{
		evt.getRegistry().register(CRATER.setRegistryName("crater"));
		evt.getRegistry().register(BIG_TREE.setRegistryName("swamp_tree"));
		evt.getRegistry().register(INVERTED_PILLAR.setRegistryName("inverted_pillar"));
		evt.getRegistry().register(VOLCANO.setRegistryName("volcano"));
		
	}

	private static <WC extends ICarverConfig> ConfiguredCarver<WC> configureCarver(String p_243773_0_, ConfiguredCarver<WC> p_243773_1_) {
		return WorldGenRegistries.func_243663_a(WorldGenRegistries.field_243652_d, p_243773_0_, p_243773_1_);
	}
	// End Carver config

	// feature config
	public static Feature<NoFeatureConfig> CRYSTAL_FEATURE;
	
	public static ConfiguredFeature<BaseTreeFeatureConfig, ?> ALIEN_TREE;
	public static ConfiguredFeature<BaseTreeFeatureConfig, ?> CHARRED_TREE;
	public static ConfiguredFeature<NoFeatureConfig, ?> CRYSTAL_SPIRE;

	private static <C extends IFeatureConfig, F extends Feature<C>> F registerFeature(String key, F value) {
		return Registry.register(Registry.FEATURE, key, value);
	}

	public static void initFeature()
	{
		CRYSTAL_FEATURE = new WorldGenLargeCrystal(NoFeatureConfig.field_236558_a_);

		ALIEN_TREE = registerTree("alientree", Feature.field_236291_c_.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(AdvancedRocketryBlocks.blockAlienWood.getDefaultState()), new SimpleBlockStateProvider(AdvancedRocketryBlocks.blockAlienLeaves.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3), new WorldGenAlienTree(4, 2, 0), new TwoLayerFeature(1, 0, 1))).func_236700_a_().build()));
		CHARRED_TREE = registerTree("charredtree", Feature.field_236291_c_.withConfiguration((new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(AdvancedRocketryBlocks.blockCharcoalLog.getDefaultState()), new SimpleBlockStateProvider(Blocks.AIR.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), 0), new WorldGenCharredTree(4, 2, 0, 0), new TwoLayerFeature(1, 0, 1))).func_236700_a_().build()));
		CRYSTAL_SPIRE = registerTree("crystal", CRYSTAL_FEATURE.withConfiguration(NoFeatureConfig.NO_FEATURE_CONFIG));		
	}
	
	public static void registerFeature(RegistryEvent.Register<Feature<?>> evt)
	{
		if(CRYSTAL_FEATURE == null)
			initFeature();
		evt.getRegistry().register(CRYSTAL_FEATURE.setRegistryName("largecrystal"));
	}


	


	private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> registerTree(String p_243968_0_, ConfiguredFeature<FC, ?> p_243968_1_) {
		return Registry.register(WorldGenRegistries.field_243653_e, p_243968_0_, p_243968_1_);
	}

	//


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


	public static void registerBiomeGenerationSettings()
	{
		configureSurfaceBuilders();
		barren = createBuilder(MOON_LUNAR_LIGHT_CONFIG, true).func_242508_a();
		barrenDark = createBuilder(MOON_LUNAR_LIGHT_CONFIG, true).func_242508_a();
		hotDry = createBuilder(HOTDRY_CONFIG, true).func_242508_a();
		genalienForest = createBuilder(ConfiguredSurfaceBuilders.field_244178_j, false).func_242513_a(Decoration.VEGETAL_DECORATION, ALIEN_TREE).func_242508_a();
		spaceBiomeGen = new BiomeGenerationSettings.Builder().func_242517_a(SPACE_CONFIG).func_242508_a();
		genCharredLand = createBuilder(ConfiguredSurfaceBuilders.field_244178_j, false).func_242513_a(Decoration.VEGETAL_DECORATION, CHARRED_TREE).func_242508_a();
		genCrystalLandscape = createBuilder(ConfiguredSurfaceBuilders.field_244178_j, false).func_242513_a(Decoration.SURFACE_STRUCTURES, CRYSTAL_SPIRE).func_242508_a();
		genSwamp = createBuilder(ConfiguredSurfaceBuilders.field_244178_j, false).func_242512_a(Carving.AIR, CONFIGURED_BIG_TREE).func_242508_a();
		genOceanSpires = createBuilder(GRAVEL_CONFIG, false).func_242512_a(Carving.AIR, CONFIGURED_INVERTED_PILLAR).func_242508_a();
		genVolcanicBasalt = createBuilder(BASALT_CONFIG, false).func_242512_a(Carving.AIR, CONFIGURED_VOLCANO).func_242513_a(Decoration.VEGETAL_DECORATION, CHARRED_TREE).func_242508_a();
		genVolcanicBasaltBarren = createBuilder(BASALT_CONFIG, false).func_242512_a(Carving.AIR, CONFIGURED_VOLCANO).func_242508_a();
		
	}

	private static BiomeGenerationSettings.Builder createBuilder(ConfiguredSurfaceBuilder<?> surface, boolean space)
	{
		BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder();
		builder = builder.func_242517_a(surface);
		if(!space)
		{
			DefaultBiomeFeatures.func_243738_d(builder);
		}
		//Add ores
		DefaultBiomeFeatures.func_243750_j(builder);

		return builder;
	}


	public static Biome.Builder makeSwamp(boolean p_244236_2_) {
		MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
		DefaultBiomeFeatures.func_243714_a(mobspawninfo$builder);
		DefaultBiomeFeatures.func_243737_c(mobspawninfo$builder);
		mobspawninfo$builder.func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.SLIME, 1, 1, 1));
		BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).func_242517_a(ConfiguredSurfaceBuilders.field_244189_u);
		if (!p_244236_2_) {
			biomegenerationsettings$builder.func_242516_a(StructureFeatures.field_244144_j);
		}

		biomegenerationsettings$builder.func_242516_a(StructureFeatures.field_244136_b);
		biomegenerationsettings$builder.func_242516_a(StructureFeatures.field_244131_B);
		DefaultBiomeFeatures.func_243738_d(biomegenerationsettings$builder);
		if (!p_244236_2_) {
			DefaultBiomeFeatures.func_243723_ag(biomegenerationsettings$builder);
		}

		DefaultBiomeFeatures.func_243742_f(biomegenerationsettings$builder);
		DefaultBiomeFeatures.func_243746_h(biomegenerationsettings$builder);
		DefaultBiomeFeatures.func_243748_i(biomegenerationsettings$builder);
		DefaultBiomeFeatures.func_243750_j(biomegenerationsettings$builder);
		DefaultBiomeFeatures.func_243755_o(biomegenerationsettings$builder);
		DefaultBiomeFeatures.func_243702_P(biomegenerationsettings$builder);
		DefaultBiomeFeatures.func_243712_Z(biomegenerationsettings$builder);
		DefaultBiomeFeatures.func_243721_ae(biomegenerationsettings$builder);
		DefaultBiomeFeatures.func_243727_ak(biomegenerationsettings$builder);
		if (p_244236_2_) {
			DefaultBiomeFeatures.func_243723_ag(biomegenerationsettings$builder);
		} else {
			biomegenerationsettings$builder.func_242513_a(GenerationStage.Decoration.VEGETAL_DECORATION, Features.field_243965_y);
		}

		DefaultBiomeFeatures.func_243730_an(biomegenerationsettings$builder);
		return (new Biome.Builder()).precipitation(Biome.RainType.RAIN).category(Biome.Category.SWAMP).func_235097_a_((new BiomeAmbience.Builder()).func_235246_b_(6388580).func_235248_c_(2302743).func_235239_a_(12638463).func_242539_d(getHue(0.8F)).func_242540_e(6975545).func_242537_a(BiomeAmbience.GrassColorModifier.SWAMP).func_235243_a_(MoodSoundAmbience.field_235027_b_).func_235238_a_()).func_242458_a(mobspawninfo$builder.func_242577_b()).func_242457_a(biomegenerationsettings$builder.func_242508_a());
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
		registeredBiomes = new ArrayList<Biome>();
		registeredHighPressureBiomes = new LinkedList<Biome>();
		blackListedBiomeIds = new ArrayList<ResourceLocation>();
		registeredSingleBiome = new ArrayList<Biome>();
	}

	
	   private static int getSkyColor(float p_244206_0_) {
		      float lvt_1_1_ = p_244206_0_ / 3.0F;
		      lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
		      return MathHelper.hsvToRGB(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
		   }
	
	public static void registerBiomes(RegistryEvent.Register<Biome> evt)
	{
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
		BiomeAmbience noAmbience = new BiomeAmbience.Builder().func_242539_d(/*sky color*/ getSkyColor(0.6F)).func_235246_b_(/*water color*/  4159204).func_235248_c_(/*water fog color*/ 329011).func_235239_a_( /*fog color*/ 12638463).func_235238_a_();
		MobSpawnInfo noMobs = new MobSpawnInfo.Builder().func_242577_b();
		
		moonBiome = (new Biome.Builder()).category(Category.NONE).precipitation(RainType.NONE).depth(1f).downfall(0).scale(0.2f).temperature(0.3f).func_242457_a(barren).func_235097_a_(noAmbience).func_242458_a(noMobs).func_242455_a();
		hotDryBiome = new Biome.Builder().category(Category.NONE).precipitation(RainType.NONE).depth(1f).scale(0.01f).downfall(0).temperature(0.9f).func_242457_a(hotDry).func_235097_a_(noAmbience).func_242458_a(noMobs).func_242455_a();
		//int foliagecolor = 0x55ffe1;
		//int grasscolor = 0x7777ff
		alienForest = new Biome.Builder().category(Category.FOREST).precipitation(RainType.RAIN).depth(1f).downfall(0).scale(0.2f).temperature(0.6f).func_242457_a(genalienForest).func_235097_a_(new BiomeAmbience.Builder().func_242537_a(GrassColorModifier.DARK_FOREST).func_242539_d(/*sky color*/ getSkyColor(0.6F)).func_235246_b_(/*water color*/  4159204).func_235248_c_(/*water fog color*/ 329011).func_235239_a_( /*fog color*/ 12638463).func_235238_a_()).func_242458_a(noMobs).func_242455_a();
		spaceBiome = new Biome.Builder().category(Category.NONE).downfall(0).precipitation(RainType.NONE).depth(-2f).scale(0).temperature(1f).func_242457_a(spaceBiomeGen).func_235097_a_(noAmbience).func_242458_a(noMobs).func_242455_a();

		// grasscolor = 0x202020
		// has mushrooms
		stormLandsBiome = new Biome.Builder().category(Category.FOREST).precipitation(RainType.RAIN).temperature(0.6f).downfall(10f).depth(.25f).scale(0.1f).func_242457_a(genCharredLand).func_235097_a_(new BiomeAmbience.Builder().func_242537_a(GrassColorModifier.DARK_FOREST).func_242539_d(/*sky color*/ getSkyColor(0.6F)).func_235246_b_(/*water color*/  4159204).func_235248_c_(/*water fog color*/ 329011).func_235239_a_( /*fog color*/ 12638463).func_235238_a_()).func_242458_a(noMobs).func_242455_a();
		crystalChasms = new Biome.Builder().category(Category.PLAINS).precipitation(RainType.SNOW).depth(.25f).scale(0.1f).temperature(0.3f).downfall(1f).func_242457_a(genCrystalLandscape).func_235097_a_(new BiomeAmbience.Builder().func_242537_a(GrassColorModifier.DARK_FOREST).func_242539_d(/*sky color*/ getSkyColor(0.6F)).func_235246_b_(/*water color*/  4159204).func_235248_c_(/*water fog color*/ 329011).func_235239_a_( /*fog color*/ 12638463).func_235238_a_()).func_242458_a(noMobs).func_242455_a();
		swampDeepBiome = new Biome.Builder().category(Category.SWAMP).precipitation(RainType.RAIN).depth(-0.1f).func_242457_a(genSwamp).scale(0.2f).downfall(0.9f).temperature(0.9f).func_235097_a_(new BiomeAmbience.Builder().func_242537_a(GrassColorModifier.DARK_FOREST).func_242539_d(/*sky color*/ getSkyColor(0.6F)).func_235246_b_(/*water color*/  4159204).func_235248_c_(/*water fog color*/ 329011).func_235239_a_( /*fog color*/ 12638463).func_235238_a_()).func_242458_a(noMobs).func_242455_a();
		marsh = makeSwamp(false).category(Category.PLAINS).precipitation(RainType.RAIN).downfall(0.7f).depth(-0.4f).temperature(0.5f).scale(0f).func_235097_a_(noAmbience).func_242458_a(noMobs).func_242455_a();
		oceanSpires = new Biome.Builder().category(Category.BEACH).precipitation(RainType.RAIN).downfall(0.7f).depth(-0.5f).temperature(0.5f).scale(0f).func_242457_a(genOceanSpires).func_235097_a_(noAmbience).func_242458_a(noMobs).func_242455_a();
		moonBiomeDark  = new Biome.Builder().category(Category.NONE).precipitation(RainType.NONE).depth(1f).downfall(0).scale(0.2f).temperature(0.3f).func_242457_a(barrenDark).func_235097_a_(noAmbience).func_242458_a(noMobs).func_242455_a();
		volcanic = new Biome.Builder().category(Category.NONE).precipitation(RainType.NONE).depth(0f).downfall(0.0f).scale(0.9f).temperature(1.0f).func_242457_a(genVolcanicBasalt).func_235097_a_(noAmbience).func_242458_a(noMobs).func_242455_a();
		volcanicBarren = new Biome.Builder().category(Category.NONE).precipitation(RainType.NONE).downfall(0.0f).depth(0f).scale(0.9f).temperature(1.0f).func_242457_a(genVolcanicBasaltBarren).func_235097_a_(noAmbience).func_242458_a(noMobs).func_242455_a();

		evt.getRegistry().registerAll(
				moonBiome.setRegistryName("moon"),
				hotDryBiome.setRegistryName("hotdry"),
				alienForest.setRegistryName("alien_forest"),
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
	 * @param biome
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
	 * @param biome
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

	public static Biome getBiome(String string)
	{
		Biome biome;
		int id = 0;
		biome = getBiomeFromResourceLocation(new ResourceLocation(string));

		return biome;
	}


	public static Temps getBiomeTemp(Biome biome)
	{
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

	public static int getBiomeId(ResourceLocation key)
	{
		return getBiomeRegistry().getId(getBiomeFromResourceLocation(key));
	}

	private static IForgeRegistry<Biome> getForgeRegistry()
	{
		return ForgeRegistries.BIOMES;
	}
	
	public static Registry<Biome> getBiomeRegistry()
	{
		
		return DynamicRegistries.func_239770_b_().func_230521_a_(Registry.field_239720_u_).get();
	}

}
