package zmaster587.advancedRocketry.api;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
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
import zmaster587.advancedRocketry.util.Asteroid;
import zmaster587.advancedRocketry.util.SealableBlockHandler;
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
import zmaster587.libVulpes.util.ZUtils;

/**
 * Stores config variables
 */

public class ARConfiguration {
	public static final String configFolder = "advancedrocketry";
	private final static byte MAGIC_CODE = (byte)197;
	private final static long MAGIC_CODE_PT2 = 2932031007403L; // Prime

	private final static String CATEGORY_GENERAL = "General";
	private final static String CATEGORY_WORLD_GENERATION = "World Generation";
	private final static String CATEGORY_ROCKET = "Rockets";
	private final static String CATEGORY_TERRAFORMING = "Terraforming";
	private final static String CATEGORY_PLANET = "Planets";
	private final static String CATEGORY_STATION = "Stations";
	private final static String CATEGORY_ENERGY = "Energy";
	private final static String CATEGORY_LASERDRILL = "Orbital Laser Drill";
	private final static String CATEGORY_RESOURCE_MISSION = "Resource Collection Missions";
	private final static String CATEGORY_PERFORMANCE = "Performance";
	private final static String CATEGORY_CLIENT = "Client";
	private final static String CATEGORY_OXYGEN = "Oxygen";
	public static Logger logger = LogManager.getLogger(Constants.modId);

	private static ConfigValue<List<? extends String>> sealableBlockWhiteList;
	private static ConfigValue<List<? extends String>> sealableBlockBlackList, breakableTorches,  blackListRocketBlocksStr, harvestableGasses, entityList, geodeOres, blackHoleGeneratorTiming, orbitalLaserOres, liquidMonopropellant, liquidBipropellant, liquidOxidizer;
	public static ConfigValue<List<? extends String>> biomeBlackList;
	public static ConfigValue<List<? extends String>> biomeHighPressure;
	public static ConfigValue<List<? extends String>> biomeSingle;
	private static ConfigValue<List<? extends String>> spawnableGasses;
	private static ConfigValue<List<? extends String>> liquidNuclearWorkingFluid;


	//Only to be set in preinit
	private static ARConfiguration currentConfig;
	private static ARConfiguration diskConfig;
	private static boolean usingServerConfig = false;

	private static List<ConfigValue<?>> allConfigValues = new LinkedList<>();

	private static final ForgeConfigSpec commonSpec;

	static {
		Pair<ARConfiguration, ForgeConfigSpec> commonConfiguration = new ForgeConfigSpec.Builder().configure(ARConfiguration::new);
		commonSpec = commonConfiguration.getRight();
	}

	public static void register() {
		registerConfig(ModConfig.Type.COMMON, commonSpec, "advancedrocketry.toml");
	}

	private static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec, String fileName) {
		AdvancedRocketry.MOD_CONTAINER.addConfig(new ModConfig(type, spec, AdvancedRocketry.MOD_CONTAINER, fileName));
	}


	private ARConfiguration() { }

	public ARConfiguration(ForgeConfigSpec.Builder builder) {
		currentConfig = new ARConfiguration();

		ARConfiguration arConfig = getCurrentConfig();

		builder.push(CATEGORY_GENERAL);
		arConfig.lowGravityBoots = builder.comment("Should the padded boots only function in <1G: [Default:false]").define("lowGravityBoots", false);
		arConfig.jetPackThrust = builder.comment("Acceleration in G that the jetpack should provide: [Default:1.3]").define("jetpackAcceleration", 1.3);
		arConfig.buildSpeedMultiplier = builder.comment("Rocket assembling speed multiplier: [Default:1.0]").define("assemblySpeedMultipluer", 1d);
		arConfig.crystalliserMaximumGravity = builder.comment("The maximum gravity the Crystallizer should function at. 0 Disables this: [Default:0]").define("crystalliserMaximumGravity", 0);
		builder.pop();
		
		builder.push(ARConfiguration.CATEGORY_LASERDRILL);
		arConfig.spaceLaserPowerMult = builder.comment("Multiplier for the power the laser drill should consume to operate. [Default:1.0]").define("LaserDrillPowerMultiplier", 1d);
		List<String> laserOreList = new LinkedList<>();
		laserOreList.add("forge:ores/iron");
		laserOreList.add("forge:ores/gold");
		laserOreList.add("forge:ores/copper");
		laserOreList.add("forge:ores/tin");
		laserOreList.add("forge:ores/redstone");
		laserOreList.add("forge:ores/diamond");
		laserOreList.add("forge:ores/coal");
		laserOreList.add("forge:ores/aluminum");
		orbitalLaserOres = builder.comment("List of tags/block names of ores allowed to be mined by the laser drill if surface drilling is disabled.  Format is either tag;size or modname:item;size: ").defineList("laserDrillOres", laserOreList, (val)-> true);
		arConfig.laserDrillPlanet = builder.comment("Should the laser drill mine the planet below: [Default:false]").define("laserDrillPlanet", false);
		arConfig.laserBlackListDims= builder.comment("Dimensions the laser drill should not mine: [Default: ]").defineList("laserDrillBlacklist", new LinkedList<>(), (val) -> true);
		builder.pop();
		
		builder.push(ARConfiguration.CATEGORY_TERRAFORMING);
		arConfig.allowTerraforming = builder.comment("Should the terraformer be able to function: WARNING: This has been known to cause strange generation! [Default:false]").define("allowTerraforming", false);
		arConfig.terraformSpeed = builder.comment("Multiplier for the speed at which the terraformer modifies the atmosphere: [Default:1]").define("terraformMult", 1d);
		arConfig.terraformliquidRate = builder.comment("Terraformer oxygen/nitrogen consumption in mB/t: [Default:40]").define("terraformerFluidRate", 40);
		arConfig.allowTerraformNonAR = builder.comment("Should dimensions not provided by AR be able to be terraformed: [Default:false]").define("allowTerraformingNonARWorlds", false);
		builder.pop();
		
        builder.push(ARConfiguration.CATEGORY_OXYGEN);
		arConfig.enableOxygen = builder.comment("Should the atmosphere system be enabled: [Default:true]").define("EnableAtmosphericEffects", true);
		arConfig.scrubberRequiresCartrige = builder.comment("Should CO2 scrubbers require a carbon collection cartridge: [Default:true]").define("scrubberRequiresCartridge", true);
		arConfig.oxygenVentPowerMultiplier = builder.comment("Multiplier for the power consumption of the oxygen vent: [Default:1.0]").define("oxygenVentPowerMultiplier", 1.0d);
		arConfig.spaceSuitOxygenTime = builder.comment("Maximum time in minutes that the spacesuit's internal buffer can store O2 for").define("spaceSuitO2Buffer", 30);
		arConfig.dropExTorches = builder.comment("Should breaking a torch drop an extinguished torch: [Default:false]").define("dropExtinguishedTorches", false);
		arConfig.oxygenVentConsumptionMult = builder.comment("Multiplier on how much O2 an oxygen vent consumes per tick").define("oxygenVentConsumptionMultiplier", 1d);
		arConfig.suitTankCapacity = builder.comment("Multiplier for the amount of fluid this tank can hold").define("suitTankMultiplier", 1.0);
		sealableBlockWhiteList = builder.comment("Blocks that are not automatically detected as sealable but should seal. Format \"Mod:Blockname\"  for example \"minecraft:chest\"").defineList("sealableBlockWhiteList", new LinkedList<>(), (val) -> true);
		sealableBlockBlackList = builder.comment("Blocks that are automatically detected as sealable but should not seal. Format \"Mod:Blockname\"  for example \"minecraft:chest\"").defineList("sealableBlockBlackList", new LinkedList<>(), (val) -> true);
		entityList = builder.comment("List of entities that completely ignore atmosphere properties and effects").defineList("entityAtmBypass", new LinkedList<>(), (val) -> true);
		breakableTorches = builder.comment("Mod:Blockname  for example \"minecraft:chest\"").define("torchBlocks", new LinkedList<>(), (val) -> true);
		builder.pop();
		
		builder.push(ARConfiguration.CATEGORY_STATION);
		arConfig.travelTimeMultiplier = builder.comment("Multiplier for warp travel time: [Default:1.0]").define("warpTravelTime", 1d);
		arConfig.stationSize = builder.comment("The size each station area is created as: [Default:1024]").define("stationSize", 1024);
		arConfig.allowZeroGSpacestations = builder.comment("Should players be able to completely disable gravity on space stations: WARNING: You can get stuck and require a teleport! [Default:false]").define("stationGravity", false);
		arConfig.fuelPointsPerDilithium = builder.comment("Dilithium fuel points per crystal: [Default:500]").define("pointsPerDilithium", 500);
		builder.pop();
		
		builder.push(ARConfiguration.CATEGORY_RESOURCE_MISSION);
		arConfig.gasCollectionMult = builder.comment("Multiplier for the amount of time gas collection missions take: [Default:1.0]").define("gasMissionMultiplier", 1.0);
		arConfig.asteroidMiningTimeMult = builder.comment("Multiplier for the amount of time asteroid collection missions take: [Default:1.0]").define("asteroidMissingMultiplier", 1.0);
		harvestableGasses =  builder.comment("List of fluid names that all gas giants will have available to mine: [Default: ]").defineList("harvestableGasses", new LinkedList<>(), (val) -> true);
//Spawnable gasses
		builder.pop();


		builder.push(ARConfiguration.CATEGORY_ENERGY);
		arConfig.solarGeneratorMult = builder.comment("Multiplier for tha mount of power the solar generator should produce:  [Default:1.0]").define("solarGeneratorMultiplier", 1d);
		arConfig.microwaveRecieverMulitplier = builder.comment("Multiplier for tha mount of power the microwave receiver should produce:  [Default:1.0]").define("microwaveRecieverMultiplier", 1d);
		arConfig.blackHolePowerMultiplier = builder.comment("Multiplier for tha mount of power the black hole generator should produce:  [Default:1.0]").define("blackHoleGeneratorMultiplier", 1f);
		List<String> blackHoleGen = new LinkedList<>();
		blackHoleGen.add("minecraft:stone;1");
		blackHoleGen.add("minecraft:dirt;1");
		blackHoleGen.add("minecraft:netherrack;1");
		blackHoleGen.add("minecraft:cobblestone;1");
		blackHoleGeneratorTiming = builder.comment("List of stacks that can be used as fuel in the black hole generator. Format is modid:item;multiplier: ").defineList("blackHoleTimings", blackHoleGen, (val) -> true);
		arConfig.defaultItemTimeBlackHole = builder.comment("Ticks per item in the black hole generator: [Default:500]").define("defaultBurnTime", 500);
		builder.pop();
		
		builder.push(ARConfiguration.CATEGORY_PLANET);
		arConfig.maxBiomesPerPlanet = builder.comment("Maximum unique biomes per planet").define("maxBiomesPerPlanet", 5);
		arConfig.resetFromXML = builder.comment("setting this to true will force AR to read from the XML file in the config/advRocketry instead of the local data, intended for use pack developers to ensure updates are pushed through").define("resetPlanetsFromXML", false);
		arConfig.canPlayerRespawnInSpace = builder.comment("If true, players will respawn near beds on planets IF the spawn location is in a breathable atmosphere").define("allowPlanetRespawn", false);
		arConfig.forcePlayerRespawnInSpace = builder.comment("If true, players will respawn near beds on planets REGARDLESS of the spawn location being in a non-breathable atmosphere. Requires 'allowPlanetRespawn' being true.").define("forcePlanetRespawn", false);
		arConfig.planetsMustBeDiscovered = builder.comment("If true, planets must be discovered in the warp controller before being visible").define("planetsMustBeDiscovered", false);
		arConfig.blackListAllVanillaBiomes = builder.comment("Prevents any vanilla biomes from spawning on planets").define("blackListVanillaBiomes", false);
		arConfig.planetDiscoveryChance = builder.comment("Chance of planet discovery in the warp ship monitor is not all planets are initially discovered").define("planetDiscoveryChance", 5);
		builder.pop();
		
		builder.push(ARConfiguration.CATEGORY_CLIENT);
		arConfig.enableNausea = builder.comment("Should players experience nausea in low-oxygen environments: [Default:true]").define("enableNausea", true);
		arConfig.stationSkyOverride = builder.comment("Should players see a custom skybox on stations: [Default:true]").define("stationSkyOverride", true);
		arConfig.planetSkyOverride = builder.comment("Should players see a custom skybox on planets, including the Overworld: [Default:true]").define("planetSkyOverride", true);

		builder.push(CATEGORY_PERFORMANCE);
		arConfig.atmosphereHandleBitMask = builder.comment("BitMask: 0: no threading, radius based; 1: threading, radius based; 2: no threading volume based; 3: threading volume based: [Default:3]").define("atmosphereCalculationMethod", 3);
		arConfig.oxygenVentSize = builder.comment("Radius of the O2 vent, volume is calculated if volume-based. WARNING: larger numbers can lead to lag: [Default:32]").define("oxygenVentSize", 32);
		builder.pop();
		
		builder.push(ARConfiguration.CATEGORY_ROCKET);
		arConfig.orbit = builder.comment("Height in blocks, from bedrock, that rockets should consider low orbit: [Default:1000]").defineInRange("orbitAltitude", 1000, 255, Integer.MAX_VALUE);
		List<String> fuels = new LinkedList<>();
		fuels.add("advancedrocketry:rocket_fuel;2");
		liquidMonopropellant = builder.comment("List of fluid IDs that can be used as rocket monopropellants: [Default: ]").defineList("rocketMonopropellants", fuels, (val) -> true);
		List<String> bifuels = new LinkedList<>();
		bifuels.add("advancedrocketry:hydrogen");
		liquidBipropellant = builder.comment("List of fluid IDs that can be used as rocket bipropellants: [Default: ]").defineList("rocketBipropellants", bifuels, (val) -> true);
		List<String> bioxydizers = new LinkedList<>();
		bioxydizers.add("advancedrocketry:oxygen");
		liquidOxidizer = builder.comment("List of fluid IDs that can be used as rocket oxidizers: [Default: ]").defineList("rocketOxidizers", bioxydizers, (val) -> true);
		List<String> working = new LinkedList<>();
		working.add("advancedrocketry:hydrogen");
		liquidNuclearWorkingFluid = builder.comment("List of fluid IDs that can be used as rocket working fluids: [Default: ]").defineList("rocketWorkingFluids", working, (val) -> true);
		arConfig.automaticRetroRockets = builder.comment("Should automatic retrothrust be enabled: [Default:true]").define("autoRetroRockets", true);
		arConfig.gravityAffectsFuel = builder.comment("Should gravity affect rocket total thrust: [Default:true]").define("gravityAffectsRockets", true);
		arConfig.rocketRequireFuel = builder.comment("Shoud rockets require fuel to fly: [Default:true]").define("rocketsRequireFuel", true);
		arConfig.canBeFueledByHand = builder.comment("Should rockets be able to be fueled with fluid containers by hand: [Default:true]").define("canBeFueledByHand", true);
		arConfig.rocketThrustMultiplier = builder.comment("Multiplier for per-engine thrust [Default:1.0]").define("thrustMultiplier", 1d);
		arConfig.fuelCapacityMultiplier = builder.comment("Multiplier for per-tank capacity [Default:1.0]").define("fuelCapacityMultiplier", 1d);
		LinkedList<String> blackListRocketBlocksStrList = new LinkedList<>();
		blackListRocketBlocksStrList.add("minecraft:portal");
		blackListRocketBlocksStrList.add("minecraft:bedrock");
		blackListRocketBlocksStrList.add("minecraft:snow_layer");
		blackListRocketBlocksStrList.add("minecraft:flowing_water");
		blackListRocketBlocksStrList.add( "minecraft:lava");
		blackListRocketBlocksStrList.add("minecraft:flowing_lava");
		blackListRocketBlocksStr = builder.comment("List of tags/block names of blocks blacklisted from rockets. Format is either tag or modname:item [Default: ]").defineList("rocketBlockBlackList", blackListRocketBlocksStrList, (val) -> true);
		arConfig.launchingDestroysBlocks = builder.comment("Should rockets damage blocks below them upon launch: [Default:false]").define("rocketsDamageBlocks", false);
		arConfig.stationClearanceHeight = builder.comment("Height in blocks, from the bottom of the world, that rockets should consider far enough from a station to clear: [Default:1000]").defineInRange("stationClearanceHeight", 1000, 255, Integer.MAX_VALUE);
		arConfig.asteroidTBIBurnMult = builder.comment("Multiplier for the height in blocks rockets should burn after reaching low orbit used for asteroids: WARNING: This is multiplied by transBodyInjection to get the total added height! [Default:1.0]").define("asteroidTBIBurnMult", 1.0);
		arConfig.transBodyInjection = builder.comment("Base height in blocks rockets should burn for after reaching low orbit to fly to other destinations: WARNING This is always zero for ground to station! [Default:0]").define("transBodyInjection", 0);
		arConfig.warpTBIBurnMult = builder.comment("Mutlipliter for the height in blocks rockets should burn after reaching low orbit used for interplanetary flight: WARNING: This is multiplied by transBodyInjection to get the total added height! [Default: 7.0]").define("interplanetaryTBIBurnMult", 12.0);
		builder.pop();
		
		builder.push(ARConfiguration.CATEGORY_WORLD_GENERATION);
		arConfig.electricPlantsSpawnLightning = builder.comment("Should Electric Mushrooms be able to spawn lightning: [Default:true]").define("electricPlantsSpawnLightning", true);
		List<String> geodeOresList = new LinkedList<>();
		geodeOresList.add(Blocks.IRON_ORE.getRegistryName().toString());
		geodeOresList.add(Blocks.GOLD_ORE.getRegistryName().toString());
		geodeOresList.add("libvulpes:orecopper");
		geodeOresList.add("libvulpes:oretin");
		geodeOresList.add(Blocks.REDSTONE_ORE.getRegistryName().toString());
		geodeOres = builder.comment("List of tags/block names of ores allowed to be spawned within geodes. Format is either tag or modname:item [Default: ]").defineList("geodeOres", geodeOresList, (val) -> true);
		arConfig.generateGeodes = builder.comment("Should geodes generate on high pressure planets? [Default:true]").define("generateGeodes", true);
		arConfig.geodeBaseSize = builder.comment("Average geode size in blocks: [Default:36]").define("geodeBaseSize", 36);
		arConfig.geodeVariation = builder.comment("Maximum geode size variation: [Default:24]").define("geodeVariation", 24);
		arConfig.generateCraters = builder.comment("Should planets be able to have generate craters: [Default:true]").define("generateCraters", true);
		arConfig.generateVolcanoes = builder.comment("Should planets be able to generate any volcanoes: [Default:true]").define("generateVolcanos", true);
		arConfig.generateVanillaStructures = builder.comment("Should planets be able to generate any Vanilla structures: [Default:true]").define("generateVanillaStructures", false);

		LinkedList<String> blackListedbiomes = new LinkedList<>();
		blackListedbiomes.add(Biomes.RIVER.getLocation().toString());
		blackListedbiomes.add(Biomes.THE_END.getLocation().toString());
		blackListedbiomes.add(Biomes.BADLANDS.getLocation().toString());
		blackListedbiomes.add(Biomes.THE_VOID.getLocation().toString());
		//blackListedbiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.alienForest).toString());

		ARConfiguration.biomeBlackList = builder.comment("List of Biomes to be blacklisted from spawning as BiomeIds, default is: river, sky, hell, void, alienForest").
				defineList("BlacklistedBiomes", blackListedbiomes, (item) -> true);


		LinkedList<String> highPressureBiome = new LinkedList<>();
		//highPressureBiome.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.stormLandsBiome).toString());
		//highPressureBiome.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.swampDeepBiome).toString());
		ARConfiguration.biomeHighPressure = builder.comment("Biomes that only spawn on worlds with pressures over 125, will override blacklist.").
				defineList("HighPressureBiomes", highPressureBiome, (item) -> true);

		LinkedList<String> singleBiomes = new LinkedList<>();
		//singleBiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.volcanicBarren).toString());
		//singleBiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.swampDeepBiome).toString());
		//singleBiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.crystalChasms).toString());
		//singleBiomes.add(AdvancedRocketryBiomes.getBiomeResource(AdvancedRocketryBiomes.alienForest).toString());
		singleBiomes.add(Biomes.DESERT_HILLS.getLocation().toString());
		singleBiomes.add(Biomes.MUSHROOM_FIELDS.getLocation().toString());
		singleBiomes.add(Biomes.TALL_BIRCH_HILLS.getLocation().toString());
		singleBiomes.add(Biomes.ICE_SPIKES.getLocation().toString());

		ARConfiguration.biomeSingle = builder.comment("Some worlds have a chance of spawning single biomes contained in this list.").
				defineList("SingleBiomes", singleBiomes, (item) -> true);

		builder.pop();

	}

	public ARConfiguration(ARConfiguration config) {
		Field[] fields = ARConfiguration.class.getDeclaredFields();
		List<Field> fieldList = new ArrayList<>(fields.length);


		// getDeclaredFields returns an unordered list, so we need to sort them
		for(Field field : fields) {
			if(field.isAnnotationPresent(ConfigProperty.class))
				fieldList.add(field);
		}

		fieldList.sort(Comparator.comparing(Field::getName));


		// do a Shallow copy
		for(Field field : fieldList) {
			try {
				if(field.getClass().isAssignableFrom(List.class)) {
					List otherList = (List)field.get(config);
					List list = otherList.getClass().newInstance();
					list.addAll(otherList);
					field.set(this, list);
				} else if(field.getClass().isAssignableFrom(Map.class)) {
					Map otherMap = (Map)field.get(config);
					Map map = otherMap.getClass().newInstance();

					for(Object key : otherMap.keySet())
					{
						Object value = otherMap.get(key);
						map.put(key, value);
					}

					field.set(this, map);
				} else
					field.set(this, field.get(config));
			} catch (IllegalArgumentException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeConfigToNetwork(PacketBuffer out) {
		Field[] fields = ARConfiguration.class.getDeclaredFields();
		List<Field> fieldList = new ArrayList<>(fields.length);


		// getDeclaredFields returns an unordered list, so we need to sort them
		for(Field field : fields) {
			if(field.isAnnotationPresent(ConfigProperty.class) && field.getAnnotation(ConfigProperty.class).needsSync())
				fieldList.add(field);
		}

		fieldList.sort(Comparator.comparing(Field::getName));

		try {
			for(Field field : fieldList ) {
				ConfigProperty props = field.getAnnotation(ConfigProperty.class);
				int hash = field.getName().hashCode();
				out.writeInt(hash);
				try {
					writeDatum( out, field.getType(), field.get(this), props);
				} catch (IllegalArgumentException | IllegalAccessException | InvalidClassException e) {
					e.printStackTrace();
				}
			}
		}
		finally {
			out.writeByte(MAGIC_CODE);
			out.writeLong(MAGIC_CODE_PT2);
		}

	}

	private void writeDatum(PacketBuffer out, Class type, Object value, ConfigProperty property) throws InvalidClassException {

		if(Integer.class.isAssignableFrom(type) || type == int.class)
			out.writeInt((Integer)value);
		else if(ConfigValue.class.isAssignableFrom(type)) {
			writeDatum(out, ((ConfigValue)value).get().getClass(), ((ConfigValue)value).get(), property);
		} else if(Float.class.isAssignableFrom(type) || type == float.class)
			out.writeFloat((Float)value);
		else if(Double.class.isAssignableFrom(type) || type == double.class)
			out.writeDouble((Double)value);
		else if(Boolean.class.isAssignableFrom(type) || type == boolean.class)
			out.writeBoolean((Boolean)value);
		else if(Asteroid.class.isAssignableFrom(type)) {
			Asteroid asteroid = (Asteroid)value;
			out.writeString(asteroid.ID);
			out.writeInt(asteroid.distance);
			out.writeInt(asteroid.mass);
			out.writeInt(asteroid.minLevel);
			out.writeFloat(asteroid.massVariability);
			out.writeFloat(asteroid.richness);					//factor of the ratio of ore to stone
			out.writeFloat(asteroid.richnessVariability);		//variability of richness
			out.writeFloat(asteroid.probability);				//probability of the asteroid spawning
			out.writeFloat(asteroid.timeMultiplier);

			out.writeInt(asteroid.stackProbabilities.size());
			for(int i = 0; i < asteroid.stackProbabilities.size(); i++)
			{
				out.writeItemStack(asteroid.itemStacks.get(i));
				out.writeFloat(asteroid.stackProbabilities.get(i));
			}
		} else if(ResourceLocation.class.isAssignableFrom(type)) {
			out.writeResourceLocation((ResourceLocation) value);
		} else if(String.class.isAssignableFrom(type)) {
			out.writeString((String) value);
		} else if(List.class.isAssignableFrom(type)) {
			List list = (List)value;
			out.writeShort(list.size());
			for(Object o : list) {
				writeDatum(out, property.internalType(), o, property);
			}
		} else if(Set.class.isAssignableFrom(type)) {
			Set list = (Set)value;
			out.writeShort(list.size());
			for(Object o : list) {
				writeDatum(out, property.internalType(), o, property);
			}
		}
		//TODO: maps and lists with arbitrary types
		else if(Map.class.isAssignableFrom(type)) {
			Map map = (Map)value;

			out.writeInt(map.size());
			for(Object key : map.keySet()) {
				Object mapValue = map.get(key);
				writeDatum(out, property.keyType(), key, property);
				writeDatum(out, property.valueType(), mapValue, property);
			}
		} else {
			throw new InvalidClassException("Cannot transmit class type " + type.getName());
		}

	}

	private Object readDatum(PacketBuffer in, Class type, ConfigProperty property) throws InvalidClassException, InstantiationException, IllegalAccessException {

		if(Integer.class.isAssignableFrom(type) || type == int.class)
			return in.readInt();
		else if(ConfigValue.class.isAssignableFrom(type)) {
			return readDatum(in, property.internalType(), property);
		} else if(Float.class.isAssignableFrom(type) || type == float.class)
			return in.readFloat();
		else if(Double.class.isAssignableFrom(type) || type == double.class)
			return in.readDouble();
		else if(boolean.class.isAssignableFrom(type) || type == boolean.class)
			return in.readBoolean();
		else if(ResourceLocation.class.isAssignableFrom(type)) {
			return in.readResourceLocation();
		} else if(String.class.isAssignableFrom(type)) {
			return in.readString(256);
		} else if(Asteroid.class.isAssignableFrom(type)) {
			Asteroid asteroid = new Asteroid();

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
			for(int i = 0; i < size; i++) {
				asteroid.itemStacks.add(in.readItemStack());
				asteroid.stackProbabilities.add(in.readFloat());
			}
			return asteroid;
		} else if(List.class.isAssignableFrom(type)) {
			List list = (List)type.newInstance();

			short listsize=in.readShort();
			for(int i = 0; i < listsize; i++)
			{
				list.add(readDatum(in, property.internalType(), property));
			}

			return list;
		} else if(Set.class.isAssignableFrom(type)) {
			Set set = (Set)type.newInstance();

			short listsize=in.readShort();
			for(int i = 0; i < listsize; i++)
			{
				set.add(readDatum(in, property.internalType(), property));
			}

			return set;
		}
		//TODO: maps and lists with arbitrary types
		else if(Map.class.isAssignableFrom(type)) {
			Map map = (Map)type.newInstance();
			int mapCount = in.readInt();

			for(int i = 0; i < mapCount; i++) {
				Object key = readDatum(in, property.keyType(), property);
				Object value = readDatum(in, property.valueType(), property);
				map.put(key, value);
			}
			return map;
		} else {
			throw new InvalidClassException("Cannot transmit class type " + type.getName());
		}
	}

	public ARConfiguration readConfigFromNetwork(PacketBuffer in) {
		Field[] fields = ARConfiguration.class.getDeclaredFields();
		List<Field> fieldList = new ArrayList<>(fields.length);


		// getDeclaredFields returns an unordered list, so we need to sort them
		for(Field field : fields) {
			if(field.isAnnotationPresent(ConfigProperty.class) && field.getAnnotation(ConfigProperty.class).needsSync())
				fieldList.add(field);
		}

		fieldList.sort(Comparator.comparing(Field::getName));

		for(Field field : fieldList ) {
			ConfigProperty props = field.getAnnotation(ConfigProperty.class);
			int hash = field.getName().hashCode();
			if(hash != in.readInt())
				return this; //Bail

			try {
				Object data = readDatum( in, field.getType(), props);
				field.set(this, data);
			} catch (IllegalArgumentException | IllegalAccessException | InvalidClassException | InstantiationException e) {
				e.printStackTrace();
			}
		}

		while(in.readByte() != MAGIC_CODE && in.readLong() == MAGIC_CODE_PT2);

		return this;
	}

	public static ARConfiguration getCurrentConfig() {
		if(currentConfig == null) {
			logger.error("Had to generate a new config, this shouldn't happen");
			throw new NullPointerException("Expected config to not be null");
		}
		return currentConfig;
	}

	public static void loadConfigFromServer(ARConfiguration config)	{
		if(usingServerConfig)
			throw new IllegalStateException("Cannot load server config when already using server config!");

		diskConfig = currentConfig;
		currentConfig = config;
		usingServerConfig = true;
	}

	public static void useClientDiskConfig() {
		if(usingServerConfig) {
			currentConfig = diskConfig;
			usingServerConfig = false;
		}
	}

	public void save() {
		if(!usingServerConfig)
			for(ConfigValue<?> value :  allConfigValues)
				value.save();
	}

	public static void registerFuelEntries(FuelType type, List<? extends String> fuels) {
		for(String str : fuels) {
			String[] splitStr = str.split(";");
			Fluid fluid = ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryCreate(splitStr[0]));
			float multiplier = 1.0f;
			if (splitStr.length > 1) {
				multiplier = Float.parseFloat(splitStr[1]);
			}

			if(fluid != null) {
				logger.info("Registering fluid "+ str + " as rocket " + type.name());
				FuelRegistry.instance.registerFuel(type, fluid, multiplier);
			}
			else
				logger.warn("Fluid name" + str  + " is not a registered fluid!");
		}
	}

	public static void loadPostInit() {
		ARConfiguration arConfig = getCurrentConfig();

		//Register fuels
		logger.info("Start registering liquid rocket fuels");
		registerFuelEntries(FuelType.LIQUID_MONOPROPELLANT, liquidMonopropellant.get());
		liquidMonopropellant = null;
		registerFuelEntries(FuelType.LIQUID_BIPROPELLANT, liquidBipropellant.get());
		liquidBipropellant = null;
		registerFuelEntries(FuelType.LIQUID_OXIDIZER, liquidOxidizer.get());
		liquidOxidizer = null;
		registerFuelEntries(FuelType.NUCLEAR_WORKING_FLUID, liquidNuclearWorkingFluid.get());
		liquidNuclearWorkingFluid = null;
		logger.info("Finished registering liquid rocket fuels");

		//Register Whitelisted Sealable Blocks
		logger.info("Start registering sealable blocks (sealableBlockWhiteList)");
		SealableBlockHandler.INSTANCE.addSealableBlocks(ZUtils.readBlockListFromStringList(sealableBlockWhiteList.get()));
		logger.info("End registering sealable blocks");
		sealableBlockWhiteList = null;

		logger.info("Start registering unsealable blocks (sealableBlockBlackList)");
		SealableBlockHandler.INSTANCE.addUnsealableBlocks(ZUtils.readBlockListFromStringList(sealableBlockBlackList.get()));
		logger.info("End registering unsealable blocks");
		sealableBlockBlackList = null;

		logger.info("Start registering torch blocks");
		arConfig.torchBlocks = ZUtils.readBlockListFromStringList(breakableTorches.get());
		logger.info("End registering torch blocks");
		breakableTorches = null;

		logger.info("Start registering blackhole generator blocks");
		for(String str : blackHoleGeneratorTiming.get()) {
			String[] splitStr = str.split(";");

			String[] blockString = splitStr[0].split(":");

			Item block = ForgeRegistries.ITEMS.getValue(new ResourceLocation(blockString[0],blockString[1]));

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
		blackHoleGeneratorTiming = null;


		logger.info("Start registering rocket blacklist blocks");
		arConfig.blackListRocketBlocks = ZUtils.readBlockListFromStringList(blackListRocketBlocksStr.get());
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

		//Geode & Laser ores
		logger.info("Start registering geode ores");
		arConfig.standardGeodeOres = ZUtils.readBlockListFromStringList(geodeOres.get());
		logger.info("End registering geode ores");
		geodeOres = null;

		logger.info("Start registering laser drill ores");
		for (String str : orbitalLaserOres.get()) arConfig.standardLaserDrillOres.addAll(ZUtils.readListFromString(str));
		logger.info("End registering laser drill ores");
		orbitalLaserOres = null;
	}

	@ConfigProperty
	public ConfigValue<Boolean> launchingDestroysBlocks;

	@ConfigProperty
	public ConfigValue<Double> suitTankCapacity;
	@ConfigProperty
	public ConfigValue<Integer> crystalliserMaximumGravity;

	@ConfigProperty(needsSync=true, internalType=Integer.class)
	public  ConfigValue<Integer> orbit;

	@ConfigProperty
	public  ConfigValue<Boolean> resetFromXML;

	@ConfigProperty(needsSync=true)
	public ConfigValue<Integer> stationClearanceHeight;

	@ConfigProperty(needsSync=true)
	public ConfigValue<Integer> transBodyInjection;

	@ConfigProperty(needsSync=true)
	public ConfigValue<Double> asteroidTBIBurnMult;

	@ConfigProperty(needsSync=true)
	public ConfigValue<Double> warpTBIBurnMult;

	@ConfigProperty
	public ResourceLocation MoonId = Constants.INVALID_PLANET;

	@ConfigProperty(needsSync=true, internalType=Integer.class)
	public  ConfigValue<Integer> stationSize;

	@ConfigProperty
	public  ConfigValue<Double> rocketThrustMultiplier;
	@ConfigProperty
	public  ConfigValue<Double> nuclearCoreThrustRatio;

	@ConfigProperty
	public  ConfigValue<Double> fuelCapacityMultiplier;

	@ConfigProperty
	public  ConfigValue<Integer> maxBiomes;

	@ConfigProperty
	public  ConfigValue<Boolean> rocketRequireFuel;

	@ConfigProperty
	public  ConfigValue<Boolean> enableNausea;

	@ConfigProperty
	public ConfigValue<Boolean> canBeFueledByHand;

	@ConfigProperty
	public  ConfigValue<Boolean> enableOxygen;

	@ConfigProperty
	public ConfigValue<Double> buildSpeedMultiplier;

	@ConfigProperty
	public  ConfigValue<Boolean> scrubberRequiresCartrige;

	@ConfigProperty
	public  ConfigValue<Integer> fuelPointsPerDilithium;

	@ConfigProperty
	public  ConfigValue<Boolean> electricPlantsSpawnLightning;

	@ConfigProperty
	public  ConfigValue<Integer> atmosphereHandleBitMask;

	@ConfigProperty
	public  ConfigValue<Boolean> automaticRetroRockets;
	@ConfigProperty
	public  ConfigValue<Integer> spaceSuitOxygenTime;

	@ConfigProperty
	public  ConfigValue<Double> travelTimeMultiplier;

	@ConfigProperty
	public  ConfigValue<Integer>  maxBiomesPerPlanet;

	@ConfigProperty
	public  ConfigValue<Double> gasCollectionMult;

	@ConfigProperty
	public  ConfigValue<Boolean> allowTerraforming;

	@ConfigProperty
	public  ConfigValue<Double> terraformSpeed;

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
	public LinkedList<ItemStack> standardLaserDrillOres = new LinkedList<>();

	@ConfigProperty
	public  ConfigValue<Boolean> laserDrillPlanet;

	/** list of entities of which atmospheric effects should not be applied **/
	@ConfigProperty
	public LinkedList<EntityType> bypassEntity = new LinkedList<>();

	@ConfigProperty
	public LinkedList<Block> torchBlocks = new LinkedList<>();

	@ConfigProperty
	public LinkedList<Block> blackListRocketBlocks = new LinkedList<>();

	@ConfigProperty
	public LinkedList<Block> standardGeodeOres = new LinkedList<>();

	@ConfigProperty(needsSync=true, internalType=ResourceLocation.class)
	public HashSet<ResourceLocation> initiallyKnownPlanets = new HashSet<>();

	@ConfigProperty(needsSync=true, keyType=String.class, valueType=Asteroid.class)
	public HashMap<String, Asteroid> asteroidTypes = new HashMap<>();

	@ConfigProperty
	public  ConfigValue<Integer> oxygenVentSize;

	@ConfigProperty
	public  ConfigValue<Double> solarGeneratorMult;

	@ConfigProperty
	public  ConfigValue<Boolean> gravityAffectsFuel;

	@ConfigProperty
	public  ConfigValue<Boolean> lowGravityBoots;

	@ConfigProperty
	public  ConfigValue<Double> jetPackThrust;

	@ConfigProperty(needsSync=true, internalType=Boolean.class)
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
	public  ConfigValue<Integer> planetDiscoveryChance;

	@ConfigProperty
	public  ConfigValue<Double> oxygenVentPowerMultiplier;

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
	public Map<ItemStack, Integer> blackHoleGeneratorBlocks = new HashMap<>();

	@ConfigProperty
	public  ConfigValue<Boolean> generateVanillaStructures;

	@ConfigProperty
	public  ConfigValue<Boolean> generateCraters;

	@ConfigProperty
	public  ConfigValue<Boolean> generateVolcanoes;

	@ConfigProperty(needsSync=true, internalType=Boolean.class)
	public  ConfigValue<Boolean> experimentalSpaceFlight;


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ConfigProperty {
		boolean needsSync() default false;
		Class internalType() default Object.class;
		Class keyType() default Object.class;
		Class valueType() default Object.class;
	}
}