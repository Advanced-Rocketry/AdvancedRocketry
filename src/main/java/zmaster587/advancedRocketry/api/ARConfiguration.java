package zmaster587.advancedRocketry.api;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.util.AsteroidSmall;
import zmaster587.advancedRocketry.util.SealableBlockHandler;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.config.CommonConfig;

import java.io.InvalidClassException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Stores config variables
 */

public class ARConfiguration {
	public static final String configFolder = "advRocketry";
	final static byte MAGIC_CODE = (byte)197;
	final static long MAGIC_CODE_PT2 = 2932031007403L; // Prime

	final static String oreGen = "Ore Generation";
	final static String ROCKET = "Rockets";
	final static String MOD_INTERACTION = "Mod Interaction";
	final static String PLANET = "Planet";
	final static String ASTEROID = "Asteroid";
	final static String BLACK_HOLE = "Black_hole_generator";
	final static String GAS_MINING = "GasMining";
	final static String PERFORMANCE = "Performance";
	final static String CLIENT = "Client";
	public static Logger logger = LogManager.getLogger(Constants.modId);

	static ConfigValue<List<? extends String>> sealableBlockWhiteList;
	static ConfigValue<List<? extends String>>  sealableBlockBlackList, breakableTorches,  blackListRocketBlocksStr, harvestableGasses, entityList, asteriodOres, geodeOres, blackHoleGeneratorTiming, orbitalLaserOres;
	static ConfigValue<List<? extends String>> liquidRocketFuel;
	public static ConfigValue<List<? extends String>> biomeBlackList;
	public static ConfigValue<List<? extends String>> biomeHighPressure;
	public static ConfigValue<List<? extends String>> biomeSingle;


	//Only to be set in preinit
	private static ARConfiguration currentConfig;
	private static ARConfiguration diskConfig;
	private static boolean usingServerConfig = false;
	
	static List<ConfigValue<?>> allConfigValues = new LinkedList<ConfigValue<?>>();
	
	private static final ForgeConfigSpec commonSpec;
	
	static {
		Pair<ARConfiguration, ForgeConfigSpec> commonConfiguration = new ForgeConfigSpec.Builder().configure(ARConfiguration::new);
		commonSpec = commonConfiguration.getRight();
		ARConfiguration config = commonConfiguration.getLeft();
	}
	
	public static void register() {
		registerConfig(ModConfig.Type.COMMON, commonSpec, "advancedRocketry.toml");
	}

	private static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec, String fileName) {
		AdvancedRocketry.MOD_CONTAINER.addConfig(new ModConfig(type, spec, AdvancedRocketry.MOD_CONTAINER, fileName));
	}


	private ARConfiguration()
	{
	}
	
	public ARConfiguration(ForgeConfigSpec.Builder builder)
	{
		currentConfig = new ARConfiguration();
		//allConfigValues;
		final String CATEGORY_GENERAL = "General";
		
		ARConfiguration arConfig = getCurrentConfig();
		//net.minecraftforge.common.config.Configuration config = arConfig.config;
		
		builder.push(CATEGORY_GENERAL);
		arConfig.buildSpeedMultiplier = builder.comment("Multiplier for the build speed of the Rocket Builder (0.5 is twice as fast 2 is half as fast").define("buildSpeedMultiplier", 1d);
		arConfig.spaceDimId = builder.comment("Dimension ID to use for space stations, changing this could really break things!").define("spaceStationId", Constants.modId + ":space");
		arConfig.enableNausea = builder.comment("If true, allows players to experience nausea on non-standard atmosphere types").define("EnableAtmosphericNausea", true);
		arConfig.enableOxygen = builder.comment("If true, allows players being hurt due to lack of oxygen and allows effects from non-standard atmosphere types").define("EnableAtmosphericEffects", true);
		arConfig.allowMakingItemsForOtherMods = builder.comment("If true, the machines from AdvancedRocketry will produce things like plates/rods for other mods even if Advanced Rocketry itself does not use the material (This can increase load time)").define("makeMaterialsForOtherMods", true);
		arConfig.scrubberRequiresCartrige = builder.comment("If true, the Oxygen scrubbers require a consumable carbon collection cartridge").define("scrubberRequiresCartrige", true);
		arConfig.vacuumDamageValue = builder.comment("Amount of damage inflicted with each tick on an entity in a vacuum").defineInRange("vacuumDamage", 1, 0, 100);
		arConfig.enableLaserDrill = builder.comment("Enables the laser drill machine").define("EnableLaserDrill", true);
		arConfig.spaceLaserPowerMult = builder.comment("Power multiplier for the laser drill machine").define("LaserDrillPowerMultiplier", 1d);
		arConfig.lowGravityBoots = builder.comment("If true, the boots only protect the player on planets with low gravity").define("lowGravityBoots", false);
		arConfig.jetPackThrust = builder.comment("Amount of force the jetpack provides with respect to gravity").define("jetPackForce", 1.3);
		arConfig.orbit = builder.comment("How high the rocket has to go before it reaches orbit").defineInRange("OrbitHeight", 1000, 255, Integer.MAX_VALUE);
		arConfig.resetFromXML = builder.comment("setting this to true will force AR to read from the XML file in the config/advRocketry instead of the local data, intended for use pack developers to ensure updates are pushed through").define("resetPlanetsFromXML", false);
		
		
		arConfig.enableTerraforming = builder.comment("Enables terraforming items and blocks").define("EnableTerraforming", true);
		arConfig.oxygenVentPowerMultiplier = builder.comment("Power consumption multiplier for the oxygen vent").define("OxygenVentPowerMultiplier", 1.0d);
		arConfig.spaceSuitOxygenTime = builder.comment("Maximum time in minutes that the spacesuit's internal buffer can store O2 for").define("spaceSuitO2Buffer", 30);
		arConfig.travelTimeMultiplier = builder.comment("Multiplier for warp travel time").define("warpTravelTime", 1d);
		arConfig.maxBiomesPerPlanet = builder.comment("Maximum unique biomes per planet").define("maxBiomesPerPlanet", 5);
		arConfig.allowTerraforming = builder.comment("EXPERIMENTAL: If set to true allows contruction and usage of the terraformer.  This is known to cause strange world generation after successful terraform").define("allowTerraforming", false);
		arConfig.terraformingBlockSpeed = builder.comment("How many blocks have the biome changed per tick.  Large numbers can slow the server down").define("biomeUpdateSpeed", 1);
		arConfig.terraformSpeed = builder.comment("Multplier for atmosphere change speed").define("terraformMult", 1d);
		arConfig.terraformPlanetSpeed = builder.comment("Max number of blocks allowed to be changed per tick").define("terraformBlockPerTick", 1);
		arConfig.terraformRequiresFluid = builder.define("TerraformerRequiresFluids", true);
		arConfig.terraformliquidRate = builder.comment("how many millibuckets/t are required to keep the terraformer running").define("TerraformerFluidConsumeRate", 40);
		arConfig.allowTerraformNonAR = builder.comment("If true, dimensions not added by AR can be terraformed").define("allowTerraformingNonARWorlds", false);

		List<String> fuels = new LinkedList();
		fuels.add("advancedrocketry:rocket_fuel");
		liquidRocketFuel = builder.comment("List of fluid names for fluids that can be used as rocket fuel").defineList("rocketFuels", fuels, (val) -> {return true;} );

		arConfig.stationSize = builder.comment("The largest size a space station can be.  Should also be a power of 2 (512)").define("SpaceStationBuildRadius", 1024);
		arConfig.canPlayerRespawnInSpace = builder.comment("If true, players will respawn near beds on planets IF the spawn location is in a breathable atmosphere").define("allowPlanetRespawn", false);
		arConfig.forcePlayerRespawnInSpace = builder.comment("If true, players will respawn near beds on planets REGARDLESS of the spawn location being in a non-breathable atmosphere. Requires 'allowPlanetRespawn' being true.").define("forcePlanetRespawn", false);
		arConfig.solarGeneratorMult = builder.comment("Amount of power per tick the solar generator should produce").define("solarGeneratorMultiplier", 1);
		arConfig.blackHolePowerMultiplier = builder.comment("Multiplier for the amount of power per tick the black hole generator should produce").define("blackHoleGeneratorMultiplier", 1f);
		arConfig.enableGravityController = builder.comment("If false, the gravity controller cannot be built or used").define("enableGravityMachine", true);
		arConfig.planetsMustBeDiscovered = builder.comment("If true, planets must be discovered in the warp controller before being visible").define("planetsMustBeDiscovered", false);
		arConfig.dropExTorches = builder.comment("If true, breaking an extinguished torch will drop an extinguished torch instead of a vanilla torch").define("dropExtinguishedTorches", false);


		arConfig.blackListAllVanillaBiomes = builder.comment("Prevents any vanilla biomes from spawning on planets").define("blackListVanillaBiomes", false);
		arConfig.overrideGCAir = builder.comment("If true, Galacticcraft's air will be disabled entirely requiring use of Advanced Rocketry's Oxygen system on GC planets").define("OverrideGCAir", true);
		arConfig.fuelPointsPerDilithium = builder.comment("How many units of fuel should each Dilithium Crystal give to warp ships").define("pointsPerDilithium", 500);
		arConfig.electricPlantsSpawnLightning = builder.comment("Should Electric Mushrooms be able to spawn lightning").define("electricPlantsSpawnLightning", true);
		arConfig.allowSawmillVanillaWood = builder.comment("Should the cutting machine be able to cut vanilla wood into planks").define("sawMillCutVanillaWood", true);
		arConfig.automaticRetroRockets = builder.comment("Setting to false will disable the retrorockets that fire automatically on reentry on both player and automated rockets").define("autoRetroRockets", true);
		arConfig.atmosphereHandleBitMask = builder.comment("BitMask: 0: no threading, radius based; 1: threading, radius based; 2: no threading volume based; 3: threading volume based").define("atmosphereCalculationMethod", 3);
		arConfig.oxygenVentSize = builder.comment("Radius of the O2 vent.  if atmosphereCalculationMethod is 2 or 3 then max volume is calculated from this radius.  WARNING: larger numbers can lead to lag").define("oxygenVentSize", 32);
		arConfig.oxygenVentConsumptionMult = builder.comment("Multiplier on how much O2 an oxygen vent consumes per tick").define("oxygenVentConsumptionMultiplier", 1d);
		arConfig.gravityAffectsFuel = builder.comment("If true, planets with higher gravity require more fuel and lower gravity would require less").define("gravityAffectsFuels", true);
		arConfig.allowZeroGSpacestations = builder.comment("If true, players will be able to completely disable gravity on spacestation.  It's possible to get stuck and require a teleport").define("allowZeroGSpacestations", false);
		arConfig.experimentalSpaceFlight = builder.comment("If true, rockets will be able to actually fly around space").define("experimentalSpaceFlight", false);


		arConfig.stationSkyOverride = builder.comment("If true, AR will use a custom skybox on space stations").define("StationSkyOverride", true);
		arConfig.planetSkyOverride = builder.comment("If true, AR will use a custom skybox on planets").define("PlanetSkyOverride", true);
		arConfig.skyOverride = builder.define("overworldSkyOverride", true);
		arConfig.advancedVFX = builder.comment("Advanced visual effects").define("advancedVFX", true);
		arConfig.gasCollectionMult = builder.comment("Multiplier for the amount of time gas collection missions take").define("gasMissionMultiplier", 1.0);
		arConfig.asteroidMiningTimeMult = builder.comment("Multiplier changing how long a mining mission takes").define("miningMissionTmeMultiplier", 1.0);
		List<String> asteroidOres = new LinkedList<>();
		asteroidOres.add(Blocks.IRON_ORE.getRegistryName().toString());
		asteroidOres.add(Blocks.GOLD_ORE.getRegistryName().toString());
		asteroidOres.add("libvulpes:orecopper");
		asteroidOres.add("libvulpes:oretin");
		asteroidOres.add(Blocks.REDSTONE_ORE.getRegistryName().toString());
		
		asteriodOres = builder.comment("List of b names of ores allowed to spawn in asteriods").defineList("standardOres", asteroidOres, (val) -> { return true;});
		
		List<String> geodeOresList = new LinkedList<>();
		geodeOresList.add(Blocks.IRON_ORE.getRegistryName().toString());
		geodeOresList.add(Blocks.GOLD_ORE.getRegistryName().toString());
		geodeOresList.add("libvulpes:orecopper");
		geodeOresList.add("libvulpes:oretin");
		geodeOresList.add(Blocks.REDSTONE_ORE.getRegistryName().toString());
		
		geodeOres = builder.comment("List of block names of blocks (usally ores) allowed to spawn in geodes").defineList("geodeOres", geodeOresList, (val) -> {return true;} );
		
		List<String> blackHoleGen = new LinkedList<>();
		blackHoleGen.add("minecraft:stone;1");
		blackHoleGen.add("minecraft:dirt;1");
		blackHoleGen.add("minecraft:netherrack;1");
		blackHoleGen.add("minecraft:cobblestone;1");
		
		blackHoleGeneratorTiming = builder.comment("minecraft:dirt;1").defineList("blackHoleTimings", blackHoleGen, (val) -> {return true;});
		arConfig.defaultItemTimeBlackHole = builder.comment("List of blocks and the amount of ticks they can power the black hole generator format: 'modname:block:meta;number_of_ticks'").define("defaultBurnTime", 500);

		arConfig.geodeOresBlackList = builder.comment("True if the ores in geodeOres should be a blacklist").define("geodeOres_blacklist", false);
		arConfig.generateGeodes = builder.comment("If true, then ore-containing geodes are generated on high pressure planets").define("generateGeodes", true);
		arConfig.geodeBaseSize = builder.comment("average size of the geodes").define("geodeBaseSize", 36);
		arConfig.geodeVariation = builder.comment("variation in geode size").define("geodeVariation", 24);

		arConfig.generateCraters = builder.comment("If true, then low pressure planets will have meteor craters.  Note: setting this option to false overrides 'generageCraters' in the planetDefs.xml").define("generateCraters", true);
		arConfig.generateVolcanos = builder.comment("If true, then very hot planets planets will volcanos.  Note: setting this option to false overrides 'generateVolcanos' in the planetDefs.xml").define("generateVolcanos", true);
		arConfig.generateVanillaStructures = builder.comment("Enable to allow structures like villages and mineshafts to generate on planets with a breathable atmosphere.  Note, setting this to false will override 'generateStructures' in the planetDefs.xml").define("generateVanillaStructures", false);
		arConfig.planetDiscoveryChance = builder.comment("Chance of planet discovery in the warp ship monitor is not all planets are initially discovered").define("planetDiscoveryChance", 5);

		List<String> laserOreList = new LinkedList<>();
		laserOreList.add("oreIron");
		laserOreList.add("oreGold");
		laserOreList.add("oreCopper");
		laserOreList.add("oreTin");
		laserOreList.add("oreRedstone");
		laserOreList.add("oreDiamond");
		
		orbitalLaserOres = builder.comment("List of oredictionary names of ores allowed to be mined by the laser drill if surface drilling is disabled.  Ores can be specified by just the oreName:<size> or by <modname>:<blockname>:<meta>:<size> where size is optional").defineList("laserDrillOres", laserOreList, (val)->{return true;} );
		arConfig.laserDrillOresBlackList = builder.comment("True if the ores in laserDrillOres should be a blacklist, false for a whitelist").define("laserDrillOres_blacklist", false);
		arConfig.laserDrillPlanet = builder.comment("If true, the orbital laser will actually mine blocks on the planet below").define("laserDrillPlanet", false);

		//Client
		arConfig.rocketRequireFuel = builder.comment("Set to false if rockets should not require fuel to fly").define("rocketsRequireFuel", true);
		arConfig.rocketThrustMultiplier = builder.comment("Multiplier for per-engine thrust").define("thrustMultiplier", 1d);
		arConfig.fuelCapacityMultiplier = builder.comment("Multiplier for per-tank capacity").define("fuelCapacityMultiplier", 1d);
		
		
		
		LinkedList<String> blackListedbiomes = new LinkedList<String>();
		blackListedbiomes.add(Biomes.RIVER.getLocation().toString());
		blackListedbiomes.add(Biomes.THE_END.getLocation().toString());
		blackListedbiomes.add(Biomes.BADLANDS.getLocation().toString());
		blackListedbiomes.add(Biomes.THE_VOID.getLocation().toString());
		//blackListedbiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.alienForest).toString());
		
		arConfig.biomeBlackList = builder.comment("List of Biomes to be blacklisted from spawning as BiomeIds, default is: river, sky, hell, void, alienForest").
				defineList("BlacklistedBiomes", blackListedbiomes, (item) -> { return true; });
		
		
		LinkedList<String> highPressureBiome = new LinkedList<String>();
		//highPressureBiome.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.stormLandsBiome).toString());
		//highPressureBiome.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.swampDeepBiome).toString());
		arConfig.biomeHighPressure = builder.comment("Biomes that only spawn on worlds with pressures over 125, will override blacklist.").
				defineList("HighPressureBiomes", highPressureBiome, (item) -> { return true; });
		
		LinkedList<String> singleBiomes = new LinkedList<String>();
		//singleBiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.volcanicBarren).toString());
		//singleBiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.swampDeepBiome).toString());
		//singleBiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.crystalChasms).toString());
		//singleBiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.alienForest).toString());
		singleBiomes.add(Biomes.DESERT_HILLS.getLocation().toString());
		singleBiomes.add(Biomes.MUSHROOM_FIELDS.getLocation().toString());
		singleBiomes.add(Biomes.TALL_BIRCH_HILLS.getLocation().toString());
		singleBiomes.add(Biomes.ICE_SPIKES.getLocation().toString());
		
		arConfig.biomeSingle = builder.comment("Some worlds have a chance of spawning single biomes contained in this list.").
				defineList("SingleBiomes", singleBiomes, (item) -> { return true; });

		final ConfigValue<Boolean> masterToggle = builder.define("EnableOreGen", true);

		//Copper Config
		arConfig.generateCopper = builder.define("GenerateCopper", true);
		arConfig.copperClumpSize = builder.define("CopperPerClump", 6);
		arConfig.copperPerChunk = builder.define("CopperPerChunk", 10);

		//Tin Config
		arConfig.generateTin = builder.define("GenerateTin", true);
		arConfig.tinClumpSize = builder.define("TinPerClump", 6);
		arConfig.tinPerChunk = builder.define("TinPerChunk", 10);

		arConfig.generateDilithium = builder.define("generateDilithium", true);
		arConfig.dilithiumClumpSize = builder.define("DilithiumPerClump", 16);
		arConfig.dilithiumPerChunk = builder.define("DilithiumPerChunk", 1);
		arConfig.dilithiumPerChunkMoon = builder.define("DilithiumPerChunkLuna", 10);

		arConfig.generateAluminum = builder.define("generateAluminum", true);
		arConfig.aluminumClumpSize = builder.define("AluminumPerClump", 16);
		arConfig.aluminumPerChunk = builder.define("AluminumPerChunk", 1);

		arConfig.generateIridium = builder.define("generateIridium", false);
		arConfig.IridiumClumpSize = builder.define("IridiumPerClump", 16);
		arConfig.IridiumPerChunk = builder.define("IridiumPerChunk", 1);

		arConfig.generateRutile = builder.define("GenerateRutile", true);
		arConfig.rutileClumpSize = builder.define("RutilePerClump", 6);
		arConfig.rutilePerChunk = builder.define("RutilePerChunk", 6);

		sealableBlockWhiteList = builder.comment("Blocks that are not automatically detected as sealable but should seal.  Format \"Mod:Blockname\"  for example \"minecraft:chest\"").defineList("sealableBlockWhiteList", new LinkedList<String>(), (val) -> { return true; });
		sealableBlockBlackList = builder.comment("Blocks that are automatically detected as sealable but should not seal.  Format \"Mod:Blockname\"  for example \"minecraft:chest\"").defineList("sealableBlockBlackList", new LinkedList<String>(), (val) -> { return true; });
		
		LinkedList<String> blackListRocketBlocksStrList = new LinkedList<String>();
		blackListRocketBlocksStrList.add("minecraft:portal");
		blackListRocketBlocksStrList.add("minecraft:bedrock");
		blackListRocketBlocksStrList.add("minecraft:snow_layer");
		blackListRocketBlocksStrList.add("minecraft:flowing_water");
		blackListRocketBlocksStrList.add( "minecraft:lava");
		blackListRocketBlocksStrList.add("minecraft:flowing_lava");
		
		blackListRocketBlocksStr = builder.comment("Mod:Blockname  for example \"minecraft:chest\"").defineList("rocketBlockBlackList", blackListRocketBlocksStrList, (val) -> {return true;} );
		breakableTorches = builder.comment("Mod:Blockname  for example \"minecraft:chest\"").define("torchBlocks", new LinkedList<String>(), (val) -> {return true;});

		//Enriched Lava in the centrifuge
		//arConfig.lavaCentrifugeOutputs = config.getStringList("lavaCentrifugeOutputs", CATEGORY_GENERAL,
		//new String[] {"nuggetCopper:100", "nuggetIron:100", "nuggetTin:100", "nuggetLead:100", "nuggetSilver:100",
		//"nuggetGold:75" ,"nuggetDiamond:10", "nuggetUranium:10", "nuggetIridium:1"}, "Outputs and chances of objects from Enriched Lava in the Centrifuge.  Format: <oredictionaryEntry>:<weight>.  Larger weights are more frequent");

		harvestableGasses =  builder.comment("list of fluid names that can be harvested as Gas").defineList("harvestableGasses",  new LinkedList<String>(), (val) -> {return true;} );
		entityList = builder.comment("list entities which should not be affected by atmosphere properties").defineList("entityAtmBypass", new LinkedList<String>(), (val) -> {return true;});
		//Satellite config
		arConfig.microwaveRecieverMulitplier = builder.comment("Multiplier for the amount of energy produced by the microwave reciever").define("MicrowaveRecieverMultiplier", 1d);

		
		arConfig.laserBlackListDims= builder.comment("Laser drill will not mine these dimension").defineList("spaceLaserDimIdBlackList", new LinkedList<String>(), (val) -> { return true; });
	
	
		//TOOD: Client stuff
		arConfig.lockUI = builder.comment("If UI is not locked, the middle mouse can be used to drag certain AR UIs around the screen, positions are saved on hitting quit in the menu").define("lockUI", true);
		
	}
	
	public ARConfiguration(ARConfiguration config)
	{
		Field[] fields = ARConfiguration.class.getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(fields.length);


		// getDeclaredFields returns an unordered list, so we need to sort them
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(ConfigProperty.class))
				fieldList.add(field);
		}

		fieldList.sort(new Comparator<Field>() {
			public int compare(Field arg0, Field arg1) { return arg0.getName().compareTo(arg1.getName()); };
		});
		
		
		// do a Shallow copy
		for(Field field : fieldList)
		{
			try {
				if(field.getClass().isAssignableFrom(List.class))
				{
					List otherList = (List)field.get(config);
					List list = otherList.getClass().newInstance();
					list.addAll(otherList);
					field.set(this, list);
				}
				else if(field.getClass().isAssignableFrom(Map.class))
				{
					Map otherMap = (Map)field.get(config);
					Map map = otherMap.getClass().newInstance();
					
					for(Object key : otherMap.keySet())
					{
						Object value = otherMap.get(key);
						map.put(key, value);
					}
					
					field.set(this, map);
				}
				else
					field.set(this, field.get(config));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} 
		}
	}

	public void writeConfigToNetwork(PacketBuffer out)
	{
		Field[] fields = ARConfiguration.class.getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(fields.length);


		// getDeclaredFields returns an unordered list, so we need to sort them
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(ConfigProperty.class) && field.getAnnotation(ConfigProperty.class).needsSync())
				fieldList.add(field);
		}

		fieldList.sort(new Comparator<Field>() {
			public int compare(Field arg0, Field arg1) { return arg0.getName().compareTo(arg1.getName()); };
		});

		try {
			for(Field field : fieldList )
			{
				ConfigProperty props = field.getAnnotation(ConfigProperty.class);
				int hash = field.getName().hashCode();
				out.writeInt(hash);
				try {
					writeDatum( out, field.getType(), field.get(this), props);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvalidClassException e) {
					e.printStackTrace();
				}
			}
		}
		finally
		{
			out.writeByte(MAGIC_CODE);
			out.writeLong(MAGIC_CODE_PT2);
		}

	}

	private void writeDatum(PacketBuffer out, Class type, Object value, ConfigProperty property) throws InvalidClassException
	{

		if(Integer.class.isAssignableFrom(type) || type == int.class)
			out.writeInt((Integer)value);
		else if(Float.class.isAssignableFrom(type) || type == float.class)
			out.writeFloat((Float)value);
		else if(Double.class.isAssignableFrom(type) || type == double.class)
			out.writeDouble((Double)value);
		else if(Boolean.class.isAssignableFrom(type) || type == boolean.class)
			out.writeBoolean((Boolean)value);
		else if(AsteroidSmall.class.isAssignableFrom(type))
		{
			AsteroidSmall asteroid = (AsteroidSmall)value;
			out.writeString(asteroid.ID);
			out.writeInt(asteroid.distance);
			out.writeInt(asteroid.mass);
			out.writeInt(asteroid.minLevel);
			out.writeFloat(asteroid.massVariability);
			out.writeFloat(asteroid.richness);					//factor of the ratio of ore to stone
			out.writeFloat(asteroid.richnessVariability);		//variability of richness
			out.writeFloat(asteroid.probability);				//probability of the asteroid spawning
			out.writeFloat(asteroid.timeMultiplier);
			
			out.writeInt(asteroid.stackProbabilites.size());
			for(int i = 0; i < asteroid.stackProbabilites.size(); i++)
			{
				out.writeItemStack(asteroid.itemStacks.get(i));
				out.writeFloat(asteroid.stackProbabilites.get(i));
			}
		}
		else if(String.class.isAssignableFrom(type))
		{
			out.writeString((String) value);
		}
		else if(List.class.isAssignableFrom(type))
		{
			List list = (List)value;
			out.writeShort(list.size());
			for(Object o : list)
			{
				writeDatum(out, property.internalType(), o, property);
			}
		}
		else if(Set.class.isAssignableFrom(type))
		{
			Set list = (Set)value;
			out.writeShort(list.size());
			for(Object o : list)
			{
				writeDatum(out, property.internalType(), o, property);
			}
		}
		//TODO: maps and lists with arbitrary types
		else if(Map.class.isAssignableFrom(type))
		{
			Map map = (Map)value;

			out.writeInt(map.size());
			for(Object key : map.keySet())
			{
				Object mapValue = map.get(key);
				writeDatum(out, property.keyType(), key, property);
				writeDatum(out, property.valueType(), mapValue, property);
			}
		}
		else
		{
			throw new InvalidClassException("Cannot transmit class type " + type.getName());
		}

	}

	private Object readDatum(PacketBuffer in, Class type, ConfigProperty property) throws InvalidClassException, InstantiationException, IllegalAccessException
	{

		if(Integer.class.isAssignableFrom(type) || type == int.class)
			return in.readInt();
		else if(Float.class.isAssignableFrom(type) || type == float.class)
			return in.readFloat();
		else if(Double.class.isAssignableFrom(type) || type == double.class)
			return in.readDouble();
		else if(boolean.class.isAssignableFrom(type) || type == boolean.class)
			return in.readBoolean();
		else if(String.class.isAssignableFrom(type))
		{
			return in.readString(256);
		}
		else if(AsteroidSmall.class.isAssignableFrom(type))
		{
			AsteroidSmall asteroid = new AsteroidSmall();
			
			asteroid.ID = in.readString(128);
			asteroid.distance = in.readInt();
			asteroid.mass = in.readInt();
			asteroid.minLevel = in.readInt();
			asteroid.massVariability = in.readFloat();
			asteroid.richness = in.readFloat();					//factor of the ratio of ore to stone
			asteroid.richnessVariability = in.readFloat();		//variability of richness
			asteroid.probability = in.readFloat();				//probability of the asteroid spawning
			asteroid.timeMultiplier = in.readFloat();
			
			int size = in.readInt();
			for(int i = 0; i < size; i++)
			{
				asteroid.itemStacks.add(in.readItemStack());
				asteroid.stackProbabilites.add(in.readFloat());
			}
			return asteroid;
		}
		else if(List.class.isAssignableFrom(type))
		{
			List list = (List)type.newInstance();

			short listsize=in.readShort();
			for(int i = 0; i < listsize; i++)
			{
				list.add(readDatum(in, property.internalType(), property));
			}

			return list;
		}
		else if(Set.class.isAssignableFrom(type))
		{
			Set set = (Set)type.newInstance();

			short listsize=in.readShort();
			for(int i = 0; i < listsize; i++)
			{
				set.add(readDatum(in, property.internalType(), property));
			}

			return set;
		}
		//TODO: maps and lists with arbitrary types
		else if(Map.class.isAssignableFrom(type))
		{
			Map map = (Map)type.newInstance();
			int mapCount = in.readInt();
			
			for(int i = 0; i < mapCount; i++)
			{
				Object key = readDatum(in, property.keyType(), property);
				Object value = readDatum(in, property.valueType(), property);
				map.put(key, value);
			}
			return map;
		}
		else
		{
			throw new InvalidClassException("Cannot transmit class type " + type.getName());
		}
	}

	public ARConfiguration readConfigFromNetwork(PacketBuffer in)
	{
		Field[] fields = ARConfiguration.class.getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(fields.length);


		// getDeclaredFields returns an unordered list, so we need to sort them
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(ConfigProperty.class) && field.getAnnotation(ConfigProperty.class).needsSync())
				fieldList.add(field);
		}

		fieldList.sort(new Comparator<Field>() {
			public int compare(Field arg0, Field arg1) { return arg0.getName().compareTo(arg1.getName()); };
		});

		for(Field field : fieldList )
		{
			ConfigProperty props = field.getAnnotation(ConfigProperty.class);
			int hash = field.getName().hashCode();
			if(hash != in.readInt())
				return this; //Bail

			try {
				Object data = readDatum( in, field.getType(), props);
				field.set(this, data);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvalidClassException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}

		while(in.readByte() != MAGIC_CODE && in.readLong() == MAGIC_CODE_PT2);

		return this;
	}

	public static ARConfiguration getCurrentConfig()
	{
		if(currentConfig == null)
		{
			logger.error("Had to generate a new config, this shouldn't happen");
			throw new NullPointerException("Expected config to not be null");
		}
		return currentConfig;
	}
	
	public static ResourceLocation GetSpaceDimId()
	{
		return new ResourceLocation(getCurrentConfig().spaceDimId.get());
	}

	public static void loadConfigFromServer(ARConfiguration config) throws Exception
	{
		if(usingServerConfig)
			throw new IllegalStateException("Cannot load server config when already using server config!");

		diskConfig = currentConfig;
		currentConfig = config;
		usingServerConfig = true;
	}

	public static void useClientDiskConfig()
	{
		if(usingServerConfig)
		{
			currentConfig = diskConfig;
			usingServerConfig = false;
		}
	}

	public void save()
	{
		if(!usingServerConfig)
			for(ConfigValue<?> value :  allConfigValues)
				value.save();
	}

	public void addTorchblock(Block newblock) {
		torchBlocks.add(newblock);
		List<String> blocks = new ArrayList<String>(torchBlocks.size());
		int index = 0;
		for( Block block : torchBlocks)
		{
			blocks.add(block.getRegistryName().toString());
		}
		
		breakableTorches.set(blocks);
		breakableTorches.save();
	}

	public void addSealedBlock(Block newblock) {
		SealableBlockHandler.INSTANCE.addSealableBlock(newblock);
		List<Block> blockList = SealableBlockHandler.INSTANCE.getOverridenSealableBlocks();
		List<String> blocks = new ArrayList<String>(blockList.size());
		int index = 0;
		for( Block block : blockList)
		{
			blocks.add(block.getRegistryName().toString());
		}
		
		sealableBlockWhiteList.set(blocks);
		sealableBlockWhiteList.save();
		save();
	}

	public static void loadPostInit()
	{
		ARConfiguration arConfig = getCurrentConfig();

		//Register fuels
		logger.info("Start registering liquid rocket fuels");
		for(String str : liquidRocketFuel.get()) {
			Fluid fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryCreate(str));

			if(fluid != null) {
				logger.info("Registering fluid "+ str + " as rocket fuel");
				FuelRegistry.instance.registerFuel(FuelType.LIQUID, fluid, 1f);
			}
			else
				logger.warn("Fluid name" + str  + " is not a registered fluid!");
		}
		logger.info("Finished registering liquid rocket fuels");
		liquidRocketFuel = null; //clean up

		//Register Whitelisted Sealable Blocks

		logger.info("Start registering sealable blocks (sealableBlockWhiteList)");
		for(String str : sealableBlockWhiteList.get()) {
			Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryCreate(str));
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				SealableBlockHandler.INSTANCE.addSealableBlock(block);
		}
		logger.info("End registering sealable blocks");
		sealableBlockWhiteList = null;

		logger.info("Start registering unsealable blocks (sealableBlockBlackList)");
		for(String str : sealableBlockBlackList.get()) {
			Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryCreate(str));
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				SealableBlockHandler.INSTANCE.addUnsealableBlock(block);
		}
		logger.info("End registering unsealable blocks");
		sealableBlockBlackList = null;

		logger.info("Start registering torch blocks");
		for(String str : breakableTorches.get()) {
			Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryCreate(str));
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				arConfig.torchBlocks.add(block);
		}
		logger.info("End registering torch blocks");
		breakableTorches = null;

		logger.info("Start registering blackhole generator blocks");
		for(String str : blackHoleGeneratorTiming.get()) {
			String splitStr[] = str.split(";");

			String blockString[] = splitStr[0].split(":");

			Item block = ForgeRegistries.ITEMS.getValue(new ResourceLocation(blockString[0],blockString[1]));
			int metaValue = 0;

			if(blockString.length > 2) {
				try {
					metaValue = Integer.parseInt(blockString[2]);
				}
				catch(NumberFormatException e) {
					logger.warn("Invalid meta value location for black hole generator: " + splitStr[0] + " using " + blockString[2] );
				}
			}

			int time = 0;

			try {
				time = Integer.parseInt(splitStr[1]);
			} catch (NumberFormatException e) {
				logger.warn("Invalid time value for black hole generator: " + str );
			}

			if(block == null)
				logger.warn("'" + splitStr[0] + "' is not a valid Block");
			else
				arConfig.blackHoleGeneratorBlocks.put(new ItemStack(block, 1), time);
		}
		logger.info("End registering blackhole generator blocks");
		breakableTorches = null;


		logger.info("Start registering rocket blacklist blocks");
		for(String str : blackListRocketBlocksStr.get()) {
			Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryCreate(str));
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				arConfig.blackListRocketBlocks.add(block);
		}
		logger.info("End registering rocket blacklist blocks");
		blackListRocketBlocksStr = null;

		logger.info("Start registering Harvestable Gasses");
		for(String str : harvestableGasses.get()) {
			Fluid fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryCreate(str));
			if(fluid == null)
				logger.warn("'" + str + "' is not a valid Fluid");
			else
				AtmosphereRegister.getInstance().registerHarvestableFluid(fluid);
		}
		logger.info("End registering Harvestable Gasses");
		harvestableGasses = null;

		logger.info("Start registering entity atmosphere bypass");

		//Add armor stand by default
		arConfig.bypassEntity.add(EntityType.ARMOR_STAND);


		for(String str : entityList.get()) {
			EntityType.byKey(str).ifPresent(value -> arConfig.bypassEntity.add(value) );
			Optional<EntityType<?>> entityType = EntityType.byKey(str);
			
			if(entityType.isPresent())
			{
				logger.info("Registering " + str + " for atmosphere bypass");
				EntityType.byKey(str).ifPresent(value -> arConfig.bypassEntity.add(value) );
			}
			else
				logger.warn("Cannot find " + str + " while registering entity for atmosphere bypass");
		}

		//Free memory
		entityList = null;
		logger.info("End registering entity atmosphere bypass");

		//Register geodeOres
		if(!arConfig.geodeOresBlackList.get()) {
			for(String str : geodeOres.get())
				arConfig.standardGeodeOres.add(ResourceLocation.tryCreate(str));
			while(arConfig.standardGeodeOres.remove(null));
		}

		//Register laserDrill ores
		if(!arConfig.laserDrillOresBlackList.get()) {
			for(String str  : orbitalLaserOres.get())
				arConfig.standardLaserDrillOres.add(str);
			while(arConfig.standardLaserDrillOres.remove(null));
		}


		//Do blacklist stuff for ore registration
		for(ResourceLocation oreName : BlockTags.getCollection().getRegisteredTags()) {

			if(arConfig.geodeOresBlackList.get() && oreName.getPath().startsWith("ore")) {
				boolean found = false;
				for(String str : geodeOres.get()) {
					if(oreName.equals(str)) {
						found = true;
						break;
					}
				}
				if(!found)
					arConfig.standardGeodeOres.add(oreName);
			}

			if(arConfig.laserDrillOresBlackList.get() && oreName.getPath().startsWith("ore")) {
				boolean found = false;
				for(String str : orbitalLaserOres.get()) {
					if(oreName.equals(str)) {
						found = true;
						break;
					}
				}
				if(!found)
					arConfig.standardLaserDrillOres.add(oreName.toString());
			}
		}
	}

	@ConfigProperty
	public  ConfigValue<Integer> vacuumDamageValue;
	
	@ConfigProperty(needsSync=true)
	public  ConfigValue<Integer> orbit;
	
	@ConfigProperty
	public  ConfigValue<Boolean> resetFromXML;

	@ConfigProperty
	public ResourceLocation MoonId = Constants.INVALID_PLANET;

	@ConfigProperty(needsSync=true)
	public  ConfigValue<String> spaceDimId;

	@ConfigProperty
	public  ConfigValue<Integer> fuelPointsPer10Mb;

	@ConfigProperty(needsSync=true)
	public  ConfigValue<Integer> stationSize;

	@ConfigProperty
	public  ConfigValue<Double> rocketThrustMultiplier;

	@ConfigProperty
	public  ConfigValue<Double> fuelCapacityMultiplier;

	@ConfigProperty
	public  ConfigValue<Integer> maxBiomes;

	@ConfigProperty
	public  ConfigValue<Boolean> rocketRequireFuel;

	@ConfigProperty
	public  ConfigValue<Boolean> enableNausea;

	@ConfigProperty
	public  ConfigValue<Boolean> enableOxygen;

	@ConfigProperty
	public ConfigValue<Double> buildSpeedMultiplier;

	@ConfigProperty
	public  ConfigValue<Boolean> generateCopper;

	@ConfigProperty
	public  ConfigValue<Integer> copperPerChunk;

	@ConfigProperty
	public  ConfigValue<Integer> copperClumpSize;

	@ConfigProperty
	public  ConfigValue<Boolean> generateTin;

	@ConfigProperty
	public  ConfigValue<Integer> tinPerChunk;

	@ConfigProperty
	public  ConfigValue<Integer> tinClumpSize;

	@ConfigProperty
	public  ConfigValue<Boolean> generateDilithium;

	@ConfigProperty
	public  ConfigValue<Integer> dilithiumClumpSize;

	@ConfigProperty
	public  ConfigValue<Integer> dilithiumPerChunk;

	@ConfigProperty
	public  ConfigValue<Integer> dilithiumPerChunkMoon;

	public  ConfigValue<Integer> aluminumPerChunk;

	@ConfigProperty
	public  ConfigValue<Integer> aluminumClumpSize;

	@ConfigProperty
	public  ConfigValue<Boolean> generateAluminum;

	@ConfigProperty
	public  ConfigValue<Boolean> generateIridium;

	@ConfigProperty
	public  ConfigValue<Integer> IridiumClumpSize;

	@ConfigProperty
	public  ConfigValue<Integer> IridiumPerChunk;

	@ConfigProperty
	public  ConfigValue<Boolean> generateRutile;

	@ConfigProperty
	public  ConfigValue<Integer> rutilePerChunk;

	@ConfigProperty
	public  ConfigValue<Integer> rutileClumpSize;

	@ConfigProperty
	public  ConfigValue<Boolean> allowMakingItemsForOtherMods;

	@ConfigProperty
	public  ConfigValue<Boolean> scrubberRequiresCartrige;

	@ConfigProperty
	public float EUMult;

	@ConfigProperty
	public float RFMult;

	@ConfigProperty
	public  ConfigValue<Boolean> overrideGCAir;

	@ConfigProperty
	public  ConfigValue<Integer> fuelPointsPerDilithium;

	@ConfigProperty
	public  ConfigValue<Boolean> electricPlantsSpawnLightning;

	@ConfigProperty
	public  ConfigValue<Boolean> allowSawmillVanillaWood;

	@ConfigProperty
	public  ConfigValue<Integer> atmosphereHandleBitMask;

	@ConfigProperty
	public  ConfigValue<Boolean> automaticRetroRockets;

	@ConfigProperty
	public  ConfigValue<Boolean> advancedVFX;

	@ConfigProperty
	public  ConfigValue<Boolean> enableLaserDrill;

	@ConfigProperty
	public  ConfigValue<Integer> spaceSuitOxygenTime;

	@ConfigProperty
	public  ConfigValue<Double> travelTimeMultiplier;

	@ConfigProperty
	public  ConfigValue<Integer>  maxBiomesPerPlanet;

	@ConfigProperty
	public  ConfigValue<Boolean> enableTerraforming;

	@ConfigProperty
	public  ConfigValue<Double> gasCollectionMult;

	@ConfigProperty
	public  ConfigValue<Boolean> allowTerraforming;

	@ConfigProperty
	public  ConfigValue<Integer> terraformingBlockSpeed;

	@ConfigProperty
	public  ConfigValue<Double> terraformSpeed;

	@ConfigProperty
	public  ConfigValue<Boolean> terraformRequiresFluid;

	@ConfigProperty
	public  ConfigValue<Double> microwaveRecieverMulitplier;

	@ConfigProperty
	public  ConfigValue<Boolean> blackListAllVanillaBiomes;

	@ConfigProperty
	public  ConfigValue<Double> asteroidMiningTimeMult;

	@ConfigProperty
	public  ConfigValue<Boolean> canPlayerRespawnInSpace;

	@ConfigProperty
	public  ConfigValue<Boolean> forcePlayerRespawnInSpace;

	@ConfigProperty
	public  ConfigValue<Double> spaceLaserPowerMult;

	@ConfigProperty
	public ConfigValue<List<? extends String>> laserBlackListDims;

	@ConfigProperty
	public LinkedList<String> standardLaserDrillOres = new LinkedList<String>();

	@ConfigProperty
	public  ConfigValue<Boolean> laserDrillPlanet;

	/** list of entities of which atmospheric effects should not be applied **/
	@ConfigProperty
	public LinkedList<EntityType> bypassEntity = new LinkedList<EntityType>();

	@ConfigProperty
	public LinkedList<Block> torchBlocks = new LinkedList<Block>();

	@ConfigProperty
	public LinkedList<Block> blackListRocketBlocks = new LinkedList<Block>();

	@ConfigProperty
	public LinkedList<ResourceLocation> standardGeodeOres = new LinkedList<ResourceLocation>();

	@ConfigProperty(needsSync=true, internalType=Integer.class)
	public HashSet<ResourceLocation> initiallyKnownPlanets = new HashSet<ResourceLocation>();

	@ConfigProperty
	public  ConfigValue<Boolean> geodeOresBlackList;

	@ConfigProperty
	public  ConfigValue<Boolean> laserDrillOresBlackList;

	@ConfigProperty
	public  ConfigValue<Boolean> lockUI;

	@ConfigProperty(needsSync=true, keyType=String.class, valueType=AsteroidSmall.class)
	public HashMap<String, AsteroidSmall> asteroidTypes = new HashMap<String, AsteroidSmall>();

	@ConfigProperty
	public HashMap<String, AsteroidSmall> prevAsteroidTypes = new HashMap<String, AsteroidSmall>();

	@ConfigProperty
	public  ConfigValue<Integer> oxygenVentSize;

	@ConfigProperty
	public  ConfigValue<Integer> solarGeneratorMult;

	@ConfigProperty
	public  ConfigValue<Boolean> gravityAffectsFuel;

	@ConfigProperty
	public  ConfigValue<Boolean> lowGravityBoots;

	@ConfigProperty
	public  ConfigValue<Double> jetPackThrust;

	@ConfigProperty
	public  ConfigValue<Boolean> enableGravityController;

	@ConfigProperty(needsSync=true)
	public  ConfigValue<Boolean> planetsMustBeDiscovered;

	@ConfigProperty
	public  ConfigValue<Boolean> generateGeodes;

	@ConfigProperty
	public  ConfigValue<Integer> geodeBaseSize;

	@ConfigProperty
	public  ConfigValue<Integer> geodeVariation;

	@ConfigProperty
	public  ConfigValue<Integer> terraformliquidRate;

	@ConfigProperty
	public  ConfigValue<Boolean> dropExTorches;

	@ConfigProperty
	public  ConfigValue<Double> oxygenVentConsumptionMult;

	@ConfigProperty
	public  ConfigValue<Integer> terraformPlanetSpeed;

	@ConfigProperty
	public  ConfigValue<Integer> planetDiscoveryChance;

	@ConfigProperty
	public  ConfigValue<Double> oxygenVentPowerMultiplier;

	@ConfigProperty
	public  ConfigValue<Boolean> skyOverride;

	@ConfigProperty
	public  ConfigValue<Boolean> planetSkyOverride;

	@ConfigProperty
	public  ConfigValue<Boolean> stationSkyOverride;

	@ConfigProperty
	public  ConfigValue<Boolean> allowTerraformNonAR;

	@ConfigProperty
	public  ConfigValue<Boolean> allowZeroGSpacestations;

	@ConfigProperty
	public  ConfigValue<Float> blackHolePowerMultiplier;

	@ConfigProperty
	public  ConfigValue<Integer> defaultItemTimeBlackHole;

	@ConfigProperty
	public Map<ItemStack, Integer> blackHoleGeneratorBlocks = new HashMap<ItemStack, Integer>();

	@ConfigProperty
	public String[] lavaCentrifugeOutputs;

	@ConfigProperty
	public  ConfigValue<Boolean> generateVanillaStructures;

	@ConfigProperty
	public  ConfigValue<Boolean> generateCraters;

	@ConfigProperty
	public  ConfigValue<Boolean> generateVolcanos;
	
	@ConfigProperty(needsSync=true)
	public  ConfigValue<Boolean> experimentalSpaceFlight;


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ConfigProperty
	{
		public boolean needsSync() default false;
		public Class internalType() default Object.class;
		public Class keyType() default Object.class;
		public Class valueType() default Object.class;
	}
}
