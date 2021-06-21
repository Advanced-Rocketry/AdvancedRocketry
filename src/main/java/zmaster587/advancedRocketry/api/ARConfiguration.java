package zmaster587.advancedRocketry.api;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.atmosphere.AtmosphereVacuum;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.util.Asteroid;
import zmaster587.advancedRocketry.util.SealableBlockHandler;

import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Stores config variables
 */

public class ARConfiguration {
	public static final String configFolder = "advRocketry";
	private final static byte MAGIC_CODE = (byte)197;
	private final static long MAGIC_CODE_PT2 = 2932031007403L; // Prime

	private final static String WORLDGEN = "World and Ore Generation";
	private final static String ROCKET = "Rockets";
	private final static String STATION = "Station Configuration";
	private final static String PLANET = "Planet";
	private final static String OXYGEN = "Oxygen System";
	private final static String ENERGY = "Energy Production";
	private final static String MISSION = "Resource Collection Missions";
	private final static String PERFORMANCE = "Performance";
	private final static String CLIENT = "Client";
	public static Logger logger = LogManager.getLogger(Constants.modId);

	private static String[] sealableBlockWhiteList, sealableBlockBlackList, breakableTorches,  blackListRocketBlocksStr, harvestableGasses, spawnableGasses, entityList, geodeOres, blackHoleGeneratorTiming, orbitalLaserOres, liquidMonopropellant, liquidBipropellantFuel, liquidBipropellantOxidizer, liquidNuclearWorkingFluid;


	//Only to be set in preinit
	public net.minecraftforge.common.config.Configuration config;
	private static ARConfiguration currentConfig = new ARConfiguration();
	private static ARConfiguration diskConfig;
	private static boolean usingServerConfig = false;


	public ARConfiguration()
	{

	}
	
	public ARConfiguration(ARConfiguration config)
	{
		Field[] fields = ARConfiguration.class.getDeclaredFields();
		List<Field> fieldList = new ArrayList<>(fields.length);


		// getDeclaredFields returns an unordered list, so we need to sort them
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(ConfigProperty.class))
				fieldList.add(field);
		}

		fieldList.sort(Comparator.comparing(Field::getName));
		
		
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
			} catch (IllegalArgumentException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeConfigToNetwork(PacketBuffer out)
	{
		Field[] fields = ARConfiguration.class.getDeclaredFields();
		List<Field> fieldList = new ArrayList<>(fields.length);


		// getDeclaredFields returns an unordered list, so we need to sort them
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(ConfigProperty.class) && field.getAnnotation(ConfigProperty.class).needsSync())
				fieldList.add(field);
		}

		fieldList.sort(Comparator.comparing(Field::getName));

		try {
			for(Field field : fieldList )
			{
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
		else if(Asteroid.class.isAssignableFrom(type))
		{
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
		else if(Asteroid.class.isAssignableFrom(type))
		{
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
			for(int i = 0; i < size; i++)
			{
				try {
					asteroid.itemStacks.add(in.readItemStack());
					asteroid.stackProbabilities.add(in.readFloat());
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		List<Field> fieldList = new ArrayList<>(fields.length);


		// getDeclaredFields returns an unordered list, so we need to sort them
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(ConfigProperty.class) && field.getAnnotation(ConfigProperty.class).needsSync())
				fieldList.add(field);
		}

		fieldList.sort(Comparator.comparing(Field::getName));

		for(Field field : fieldList )
		{
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

	public static ARConfiguration getCurrentConfig()
	{
		if(currentConfig == null)
		{
			logger.error("Had to generate a new config, this shouldn't happen");
			return new ARConfiguration();
		}
		return currentConfig;
	}

	public static void loadConfigFromServer(ARConfiguration config) {
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
			config.save();
	}

	public void addTorchblock(Block newblock) {
		torchBlocks.add(newblock);
		String[] blocks = new String[torchBlocks.size()];
		int index = 0;
		for( Block block : torchBlocks)
		{
			blocks[index++] = block.getRegistryName().toString();
		}
		config.get(OXYGEN, "torchBlocks","").set(blocks);
		save();
	}

	public void addSealedBlock(Block newblock) {
		SealableBlockHandler.INSTANCE.addSealableBlock(newblock);
		List<Block> blockList = SealableBlockHandler.INSTANCE.getOverriddenSealableBlocks();
		String[] blocks = new String[blockList.size()];
		int index = 0;
		for( Block block : blockList)
		{
			blocks[index++] = block.getRegistryName().toString();
		}
		config.get(OXYGEN, "sealableBlockWhiteList","").set(blocks);
		save();
	}

	public static void loadPreInit()
	{

		ARConfiguration arConfig = getCurrentConfig();
		net.minecraftforge.common.config.Configuration config = arConfig.config;

		//General
		arConfig.allowMakingItemsForOtherMods = config.get(Configuration.CATEGORY_GENERAL, "makeMaterialsForOtherMods", true, "If true the machines from AdvancedRocketry will produce things like plates/rods for other mods even if Advanced Rocketry itself does not use the material (This can increase load time)").getBoolean();
		arConfig.allowSawmillVanillaWood = config.get(Configuration.CATEGORY_GENERAL, "sawMillCutVanillaWood", true, "Should the cutting machine be able to cut vanilla wood into planks").getBoolean();
		arConfig.lowGravityBoots = config.get(Configuration.CATEGORY_GENERAL, "lowGravityBoots", false, "If true the boots only protect the player on planets with low gravity").getBoolean();
		arConfig.jetPackThrust = (float)config.get(Configuration.CATEGORY_GENERAL, "jetPackForce", 1.3, "Amount of force the jetpack provides with respect to gravity, 1 is the same acceleration as caused by Earth's gravity, 2 is 2x the acceleration caused by Earth's gravity, etc.  To make jetpack only work on low gravity planets, simply set it to a value less than 1").getDouble();
		arConfig.buildSpeedMultiplier = (float) config.get(Configuration.CATEGORY_GENERAL, "buildSpeedMultiplier", 1f, "Multiplier for the build speed of the Rocket Builder (0.5 is twice as fast 2 is half as fast").getDouble();
		arConfig.blockTankCapacity = (float)config.get(Configuration.CATEGORY_GENERAL, "blockTankCapacity", 1.0f, "Multiplier for the pressurized tank's (block) capacity", 0, Float.MAX_VALUE).getDouble();
		//Enriched Lava in the centrifuge
		arConfig.lavaCentrifugeOutputs = config.getStringList("lavaCentrifugeOutputs", Configuration.CATEGORY_GENERAL, new String[] {"nuggetCopper:100", "nuggetIron:100", "nuggetTin:100", "nuggetLead:100", "nuggetSilver:100", "nuggetGold:75" ,"nuggetDiamond:10", "nuggetUranium:10", "nuggetIridium:1"}, "Outputs and chances of objects from Enriched Lava in the Centrifuge.  Format: <oredictionaryEntry>:<weight>.  Larger weights are more frequent");
		arConfig.crystalliserMaximumGravity = (float)config.get(Configuration.CATEGORY_GENERAL, "crystalliserMaximumGravity", 0f, "Maximum gravity the crystalliser will function at. Use 0.0 to disable!").getDouble();
		arConfig.enableLaserDrill = config.get(Configuration.CATEGORY_GENERAL, "EnableLaserDrill", true, "Enables the laser drill machine").getBoolean();
		arConfig.spaceLaserPowerMult = (float)config.get(Configuration.CATEGORY_GENERAL, "LaserDrillPowerMultiplier", 1d, "Power multiplier for the laser drill machine").getDouble();
		arConfig.laserDrillPlanet = config.get(Configuration.CATEGORY_GENERAL, "laserDrillPlanet", false, "If true the orbital laser will actually mine blocks on the planet below").getBoolean();
		String[] str = config.getStringList("spaceLaserDimIdBlackList", Configuration.CATEGORY_GENERAL, new String[] {}, "Laser drill will not mine these dimension");
		arConfig.enableTerraforming = config.get(Configuration.CATEGORY_GENERAL, "EnableTerraforming", true,"Enables terraforming items and blocks").getBoolean();
		arConfig.terraformingBlockSpeed = config.get(Configuration.CATEGORY_GENERAL, "biomeUpdateSpeed", 1, "How many blocks have the biome changed per tick.  Large numbers can slow the server down", Integer.MAX_VALUE, 1).getInt();
		arConfig.terraformSpeed = config.get(Configuration.CATEGORY_GENERAL, "terraformMult", 1f, "Multplier for atmosphere change speed").getDouble();
		arConfig.terraformPlanetSpeed = config.get(Configuration.CATEGORY_GENERAL, "terraformBlockPerTick", 1, "Max number of blocks allowed to be changed per tick").getInt();
		arConfig.terraformRequiresFluid = config.get(Configuration.CATEGORY_GENERAL, "TerraformerRequiresFluids", true, "Whether the Terraformer should consume fluids at all, independent of rate").getBoolean();
		arConfig.terraformliquidRate = config.get(Configuration.CATEGORY_GENERAL, "TerraformerFluidConsumeRate", 40, "how many millibuckets/t are required to keep the terraformer running").getInt();
		arConfig.allowTerraformNonAR = config.get(Configuration.CATEGORY_GENERAL, "allowTerraformingNonARWorlds", false, "If true dimensions not added by AR can be terraformed, including the overworld").getBoolean();
		arConfig.enableGravityController = config.get(Configuration.CATEGORY_GENERAL, "enableGravityMachine", true, "If false the gravity controller cannot be built or used").getBoolean();

		//Oxygen
		arConfig.enableOxygen = config.get(OXYGEN, "EnableAtmosphericEffects", true, "If true, allows players being hurt due to lack of oxygen and allows effects from non-standard atmosphere types").getBoolean();
		AtmosphereVacuum.damageValue = config.get(OXYGEN, "vacuumDamage", 1, "Amount of damage taken every second in a vacuum").getInt();
		arConfig.overrideGCAir = config.get(OXYGEN, "OverrideGCAir", true, "If true Galacticcraft's air will be disabled entirely requiring use of Advanced Rocketry's Oxygen system on GC planets").getBoolean();
		arConfig.oxygenVentConsumptionMult = config.get(OXYGEN, "oxygenVentConsumptionMultiplier", 1f, "Multiplier on how much O2 an oxygen vent consumes per tick").getDouble();
		arConfig.oxygenVentPowerMultiplier = config.get(OXYGEN, "OxygenVentPowerMultiplier", 1.0f, "Power consumption multiplier for the oxygen vent", 0, Float.MAX_VALUE).getDouble();
		arConfig.spaceSuitOxygenTime = config.get(OXYGEN, "spaceSuitO2Buffer", 30, "Maximum time in minutes that the spacesuit's internal buffer can store O2 for").getInt();
		arConfig.suitTankCapacity = (float)config.get(OXYGEN, "suitTankCapacity", 1.0f, "Global multiplier for suit extra tank capacity", 0, Float.MAX_VALUE).getDouble();
		arConfig.scrubberRequiresCartrige = config.get(OXYGEN, "scrubberRequiresCartrige", true, "If true the Oxygen scrubbers require a consumable carbon collection cartridge").getBoolean();
		arConfig.dropExTorches = config.get(OXYGEN, "dropExtinguishedTorches", false, "If true, breaking an extinguished torch will drop an extinguished torch instead of a vanilla torch").getBoolean();
		sealableBlockWhiteList = config.getStringList("sealableBlockWhiteList", OXYGEN, new String[] {}, "Blocks that are not automatically detected as sealable but should seal.  Format \"Mod:Blockname\"  for example \"minecraft:chest\"");
		sealableBlockBlackList = config.getStringList("sealableBlockBlackList", OXYGEN, new String[] {}, "Blocks that are automatically detected as sealable but should not seal.  Format \"Mod:Blockname\"  for example \"minecraft:chest\"");
		breakableTorches = config.getStringList("torchBlocks", OXYGEN, new String[] {}, "Mod:Blockname  for example \"minecraft:chest\"");
		entityList = config.getStringList("entityAtmBypass", OXYGEN, new String[] {}, "list entities which should not be affected by atmosphere properties");

		//Station
		arConfig.spaceDimId = config.get(STATION,"spaceStationId" , -2,"Dimension ID to use for space stations").getInt();
		arConfig.stationSize = config.get(STATION, "SpaceStationBuildRadius", 1024, "The largest size a space station can be.  Should also be a power of 2 (512, 1024, 2048, 4096, ...).  CAUTION: CHANGING THIS OPTION WILL DAMAGE EXISTING STATIONS!!!").getInt();
		arConfig.allowZeroGSpacestations = config.get(STATION, "allowZeroGSpacestations", false, "If true players will be able to completely disable gravity on spacestation.  It's possible to get stuck and require a teleport, you have been warned!").getBoolean();
		arConfig.fuelPointsPerDilithium = config.get(STATION, "pointsPerDilithium", 500, "How many units of fuel should each Dilithium Crystal give to warp ships", 1, 1000).getInt();
		arConfig.travelTimeMultiplier = (float)config.get(STATION, "warpTravelTime", 1f, "Multiplier for warp travel time").getDouble();

        //Missions
		arConfig.asteroidMiningTimeMult = config.get(MISSION, "miningMissionTmeMultiplier", 1.0, "Multiplier changing how long a mining mission takes").getDouble();
		arConfig.gasCollectionMult = config.get(MISSION, "gasMissionMultiplier", 1.0, "Multiplier for the amount of time gas collection missions take").getDouble();
		harvestableGasses = config.getStringList("harvestableGasses", MISSION, new String[] {}, "list of fluid names that can be harvested as Gas from any gas giant");
		spawnableGasses = config.getStringList("spawnableGasses", MISSION, new String[] {"hydrogen;125;1600;1.0", "helium;125;1600;0.9", "helium3;175;1600;0.2", "oxygen;0;124;1.0", "nitrogen;0;124;1.0", "ammonia;0;124;0.75", "methane;0;124;0.25"}, "list of fluid names that can be spawned as a gas giant. Format is fluid;minGravity;maxGravity;chance");

		//Energy Production
		arConfig.solarGeneratorMult = config.get(ENERGY, "solarGeneratorMultiplier", 1, "Amount of power per tick the solar generator should produce").getInt();
		arConfig.microwaveRecieverMulitplier = (float)config.get(ENERGY, "MicrowaveRecieverMultiplier", 1f, "Multiplier for the amount of energy produced by the microwave reciever").getDouble();
		arConfig.defaultItemTimeBlackHole = config.get(ENERGY, "defaultBurnTime", 500, "List of blocks and the amount of ticks they can power the black hole generator format: 'modname:block:meta;number_of_ticks'").getInt();
		arConfig.blackHolePowerMultiplier = config.get(ENERGY, "blackHoleGeneratorMultiplier", 1, "Multiplier for the amount of power per tick the black hole generator should produce").getInt();
		blackHoleGeneratorTiming = config.get(ENERGY, "blackHoleTimings", new String[] {"minecraft:stone;1", "minecraft:dirt;1", "minecraft:netherrack;1", "minecraft:cobblestone;1"}, "List of blocks and the amount of ticks they can power the black hole generator format: 'modname:block:meta;number_of_ticks'").getStringList();

		//Planet
		arConfig.planetsMustBeDiscovered = config.get(PLANET, "planetsMustBeDiscovered", false, "If true planets must be discovered in the warp controller before being visible").getBoolean();
		arConfig.planetDiscoveryChance = config.get(PLANET, "planetDiscoveryChance", 5, "Chance of planet discovery in the warp ship monitor is not all planets are initially discoved, chance is 1/n", 1, Integer.MAX_VALUE).getInt();
		boolean resetResetFromXml = config.getBoolean("ResetOnlyOnce", PLANET, true, "setting this to false will will prevent resetPlanetsFromXML from being set to false upon world reload.  Recommended for those who want to force ALL saves to ALWAYS use the planetDefs XML in the /config folder.  Essentially that 'Are you sure you're sure' option.  If resetPlanetsFromXML is false, this option does nothing.");
        //Reset to false
		if (resetResetFromXml)
			config.get(PLANET, "resetPlanetsFromXML",false, "Whether the planets should be reset from the config XML on this restart").set(false);
		DimensionManager.dimOffset = config.getInt("minDimension", PLANET, 2, -127, 8000, "Dimensions including and after this number are allowed to be made into planets");
		arConfig.canPlayerRespawnInSpace = config.get(PLANET, "allowPlanetRespawn", false, "If true players will respawn near beds on planets IF the spawn location is in a breathable atmosphere").getBoolean();
		arConfig.forcePlayerRespawnInSpace = config.get(PLANET, "forcePlanetRespawn", false, "If true players will respawn near beds on planets REGARDLESS of the spawn location being in a non-breathable atmosphere. Requires 'allowPlanetRespawn' being true.").getBoolean();
		arConfig.blackListAllVanillaBiomes = config.getBoolean("blackListVanillaBiomes", PLANET, false, "Prevents any vanilla biomes from spawning on planets");
		arConfig.maxBiomesPerPlanet = config.get(PLANET, "maxBiomesPerPlanet", 5, "Maximum unique biomes per planet, -1 to disable").getInt();

		//Client
		arConfig.stationSkyOverride = config.get(CLIENT, "StationSkyOverride", true, "If true, AR will use a custom skybox on space stations").getBoolean();
		arConfig.planetSkyOverride = config.get(CLIENT, "PlanetSkyOverride", true, "If true, AR will use a custom skybox on planets").getBoolean();
		arConfig.skyOverride = config.get(CLIENT, "overworldSkyOverride", true).getBoolean();
		arConfig.advancedVFX = config.get(CLIENT, "advancedVFX", true, "Advanced visual effects").getBoolean();
		arConfig.enableNausea = config.get(CLIENT, "EnableAtmosphericNausea", true, "If true, allows players to experience nausea on non-standard atmosphere types").getBoolean();
		arConfig.electricPlantsSpawnLightning = config.get(CLIENT, "electricPlantsSpawnLightning", true, "Should Electric Mushrooms be able to spawn lightning").getBoolean();

		//Performance
		arConfig.atmosphereHandleBitMask = config.get(PERFORMANCE, "atmosphereCalculationMethod", 3, "BitMask: 0: no threading, radius based; 1: threading, radius based (EXP); 2: no threading volume based; 3: threading volume based (EXP)").getInt();
		arConfig.oxygenVentSize = config.get(PERFORMANCE, "oxygenVentSize", 32, "Radius of the O2 vent.  if atmosphereCalculationMethod is 2 or 3 then max volume is calculated from this radius.  WARNING: larger numbers can lead to lag").getInt();

		//Rockets
		arConfig.rocketRequireFuel = config.get(ROCKET, "rocketsRequireFuel", true, "Set to false if rockets should not require fuel to fly").getBoolean();
		arConfig.canBeFueledByHand = config.get(ROCKET, "canBeFueledByHand", true, "Set to false if rockets should not be able to be fueled by and and will require a fueling station").getBoolean();
		liquidMonopropellant = config.get(ROCKET, "rocketFuels", new String[] {"rocketfuel;2"}, "List of fluid names for fluids that can be used as rocket monopropellants").getStringList();
		liquidBipropellantFuel = config.get(ROCKET, "rocketBipropellants", new String[] {"hydrogen"}, "List of fluid names for fluids that can be used as rocket bipropellant fuels").getStringList();
		liquidBipropellantOxidizer = config.get(ROCKET, "rocketOxidizers", new String[] {"oxygen"}, "List of fluid names for fluids that can be used as rocket bipropellant oxidizers").getStringList();
		liquidNuclearWorkingFluid = config.get(ROCKET, "rocketNuclearWorkingFluids", new String[] {"hydrogen"}, "List of fluid names for fluids that can be used as rocket nuclear working fluids").getStringList();
		arConfig.rocketThrustMultiplier = config.get(ROCKET, "thrustMultiplier", 1f, "Multiplier for per-engine thrust").getDouble();
		arConfig.fuelCapacityMultiplier = config.get(ROCKET, "fuelCapacityMultiplier", 1f, "Multiplier for per-tank capacity").getDouble();
		arConfig.nuclearCoreThrustRatio = config.get(ROCKET, "nuclearCoreThrustRatio", 1.0, "The multiplier for the thrust of the nuclear core block. With default configuration, this value provides a (max) thrust of 1000 per core.").getDouble();
		arConfig.automaticRetroRockets = config.get(ROCKET, "autoRetroRockets", true, "Setting to false will disable the retrorockets that fire automatically on reentry on both player and automated rockets").getBoolean();
		arConfig.orbit = config.getInt("orbitHeight", ROCKET, 1000, 255, Integer.MAX_VALUE, "How high the rocket has to go before it reaches orbit. This is used by itself when launching from a planet to LEO, which can be either a satellite, a space station, or another point on this planet's surface. It's used in conjunction with the TBI burn when launching to the moon or asteroids. Warp flights will need orbit height + 10x TBI to launch from planets");
		arConfig.stationClearanceHeight = config.getInt("stationClearance", ROCKET, 1000, 255, Integer.MAX_VALUE, "How high the rocket has to go before it clears a space station and can enter its own orbit - WARNING: This property is not synced with orbitHeight and so will be displayed incorrectly on monitors if not equal to it. Burn length here is used by itself when launching from a station to either another station or the same station, or to the planet it is orbiting. it is used in conjunction with the TBI burn when launching to a moon or asteroid");
		arConfig.transBodyInjection = config.getInt("transBodyInjection", ROCKET, 0, 0, Integer.MAX_VALUE, "How long the burn for trans-body injection is - this is performed soley after entering orbit and is in blocks - WARNING: This property is not taken into account by any machines when determining whether the rocket is fit to fly or not - Rockets that can reach LEO and so are flightworthy may not make TBI and will fall back to the parent planet. When enabled, the burn sequence is [Burn to LEO], [TBI Burn] when launching from a planet to moons or asteroids; and the sequence is [Station clearance burn], [TBI Burn] when launching from a station to a moon or asteroid. This distance varies by object distance");
		arConfig.asteroidTBIBurnMult = (float) config.get(ROCKET, "asteroidTBIBurnMult", 1.0, "The multiplier that asteroids should be considered as for TBI distance").getDouble();
		arConfig.warpTBIBurnMult = (float) config.get(ROCKET, "warpTBIBurnMult", 10.0, "The multiplier that warp rocket flights should be considered as for TBI distance").getDouble();
		arConfig.experimentalSpaceFlight = config.get(ROCKET, "experimentalSpaceFlight", false, "If true, rockets will be able to actually fly around space, EXPERIMENTAL").getBoolean();
		arConfig.gravityAffectsFuel = config.get(ROCKET, "gravityAffectsFuels", true, "If true planets with higher gravity require more fuel and lower gravity would require less").getBoolean();
		arConfig.launchingDestroysBlocks = config.get(ROCKET, "launchBlockDestruction", false, "If true rocket launches will kill plants, glass soil, turn rock into lava, and more").getBoolean();
		blackListRocketBlocksStr = config.getStringList("rocketBlockBlackList", ROCKET, new String[] {"minecraft:portal","minecraft:bedrock", "minecraft:snow_layer", "minecraft:water", "minecraft:flowing_water", "minecraft:lava", "minecraft:flowing_lava", "minecraft:fire", "advancedrocketry:rocketfire"}, "Mod:Blockname  for example \"minecraft:chest\"");

		//Ore and worldgen configuration
		//Ore configuration
		final boolean masterToggle = arConfig.generateCopper = config.get(WORLDGEN, "EnableOreGen", true).getBoolean();
		arConfig.generateCopper = config.get(WORLDGEN, "GenerateCopper", true).getBoolean() && masterToggle;
		arConfig.copperClumpSize = config.get(WORLDGEN, "CopperPerClump", 6).getInt();
		arConfig.copperPerChunk = config.get(WORLDGEN, "CopperPerChunk", 10).getInt();
		arConfig.generateTin = config.get(WORLDGEN, "GenerateTin", true).getBoolean() && masterToggle;
		arConfig.tinClumpSize = config.get(WORLDGEN, "TinPerClump", 6).getInt();
		arConfig.tinPerChunk = config.get(WORLDGEN, "TinPerChunk", 10).getInt();
		arConfig.generateDilithium = config.get(WORLDGEN, "generateDilithium", true).getBoolean() && masterToggle;
		arConfig.dilithiumClumpSize = config.get(WORLDGEN, "DilithiumPerClump", 16).getInt();
		arConfig.dilithiumPerChunk = config.get(WORLDGEN, "DilithiumPerChunk", 1).getInt();
		arConfig.dilithiumPerChunkMoon = config.get(WORLDGEN, "DilithiumPerChunkLuna", 10).getInt();
		arConfig.generateAluminum = config.get(WORLDGEN, "generateAluminum", true).getBoolean() && masterToggle;
		arConfig.aluminumClumpSize = config.get(WORLDGEN, "AluminumPerClump", 16).getInt();
		arConfig.aluminumPerChunk = config.get(WORLDGEN, "AluminumPerChunk", 1).getInt();
		arConfig.generateRutile = config.get(WORLDGEN, "GenerateRutile", true).getBoolean() && masterToggle;
		arConfig.rutileClumpSize = config.get(WORLDGEN, "RutilePerClump", 6).getInt();
		arConfig.rutilePerChunk = config.get(WORLDGEN, "RutilePerChunk", 6).getInt();
		arConfig.generateIridium = config.get(WORLDGEN, "generateIridium", false).getBoolean() && masterToggle;
		arConfig.IridiumClumpSize = config.get(WORLDGEN, "IridiumPerClump", 16).getInt();
		arConfig.IridiumPerChunk = config.get(WORLDGEN, "IridiumPerChunk", 1).getInt();
		//Orbital laser
		arConfig.laserDrillOresBlackList = config.get(WORLDGEN, "laserDrillOres_blacklist", false, "True if the ores in laserDrillOres should be a blacklist, false for whitelist").getBoolean();
		orbitalLaserOres = config.get(WORLDGEN, "laserDrillOres", new String[] {"oreIron", "oreGold", "oreCopper", "oreTin", "oreRedstone", "oreDiamond"}, "List of oredictionary names of ores allowed to be mined by the laser drill if surface drilling is disabled.  Ores can be specified by just the oreName:<size> or by <modname>:<blockname>:<meta>:<size> where size is optional").getStringList();
		//Geode
		arConfig.geodeOresBlackList = config.get(WORLDGEN, "geodeOres_blacklist", false, "True if the ores in geodeOres should be a blacklist, false for whitelist").getBoolean();
		arConfig.generateGeodes = config.get(WORLDGEN, "generateGeodes", true, "If true then ore-containing geodes are generated on high pressure planets").getBoolean();
		arConfig.geodeBaseSize = config.get(WORLDGEN, "geodeBaseSize", 36, "average size of the geodes").getInt();
		arConfig.geodeVariation = config.get(WORLDGEN, "geodeVariation", 24, "variation in geode size").getInt();
		geodeOres = config.get(WORLDGEN, "geodeOres", new String[] {"oreIron", "oreGold", "oreCopper", "oreTin", "oreRedstone"}, "List of oredictionary names of ores allowed to spawn in geodes").getStringList();
		//Other structures
		arConfig.generateCraters = config.get(WORLDGEN, "generateCraters", true, "If true then low pressure planets will have meteor craters.  Note: setting this option to false overrides 'generageCraters' in the planetDefs.xml").getBoolean();
		arConfig.generateVolcanos = config.get(WORLDGEN, "generateVolcanos", true, "If true then very hot planets planets will volcanos.  Note: setting this option to false overrides 'generateVolcanos' in the planetDefs.xml").getBoolean();
		arConfig.generateVanillaStructures = config.getBoolean("generateVanillaStructures", WORLDGEN, false, "Enable to allow structures like villages and mineshafts to generate on planets with a breathable atmosphere.  Note, setting this to false will override 'generateStructures' in the planetDefs.xml");



		//Load laser dimid blacklists
		for(String s : str) {

			try {
				arConfig.laserBlackListDims.add(Integer.parseInt(s));
			} catch (NumberFormatException e) {
				logger.warn("Invalid number \"" + s + "\" for laser dimid blacklist");
			}
		}
	}


	public static void loadPostInit()
	{
		ARConfiguration arConfig = getCurrentConfig();

		//Register fuels
		logger.info("Start registering liquid rocket fuels");
		for(String str : liquidMonopropellant) {
			String[] splitStr = str.split(";");
			Fluid fluid = FluidRegistry.getFluid(splitStr[0]);
			float multiplier = 1.0f;
			if (splitStr.length > 1) {
				multiplier = Float.parseFloat(splitStr[1]);
			}

			if(fluid != null) {
				logger.info("Registering fluid "+ str + " as rocket monopropellant");
				FuelRegistry.instance.registerFuel(FuelType.LIQUID_MONOPROPELLANT, fluid, multiplier);
			}
			else
				logger.warn("Fluid name" + str  + " is not a registered fluid!");
		}
		liquidMonopropellant = null; //clean up
		for(String str : liquidBipropellantFuel) {
			String[] splitStr = str.split(";");
			Fluid fluid = FluidRegistry.getFluid(splitStr[0]);
			float multiplier = 1.0f;
			if (splitStr.length > 1) {
				multiplier = Float.parseFloat(splitStr[1]);
			}

			if(fluid != null) {
				logger.info("Registering fluid "+ str + " as rocket bipropellant");
				FuelRegistry.instance.registerFuel(FuelType.LIQUID_BIPROPELLANT, fluid, multiplier);
			}
			else
				logger.warn("Fluid name" + str  + " is not a registered fluid!");
		}
		liquidBipropellantFuel = null; //clean up
		for(String str : liquidBipropellantOxidizer) {
			String[] splitStr = str.split(";");
			Fluid fluid = FluidRegistry.getFluid(splitStr[0]);
			float multiplier = 1.0f;
			if (splitStr.length > 1) {
				multiplier = Float.parseFloat(splitStr[1]);
			}

			if(fluid != null) {
				logger.info("Registering fluid "+ str + " as rocket oxidizer");
				FuelRegistry.instance.registerFuel(FuelType.LIQUID_OXIDIZER, fluid, multiplier);
			}
			else
				logger.warn("Fluid name" + str  + " is not a registered fluid!");
		}
		liquidBipropellantOxidizer = null; //clean up
		for(String str : liquidNuclearWorkingFluid) {
			String[] splitStr = str.split(";");
			Fluid fluid = FluidRegistry.getFluid(splitStr[0]);
			float multiplier = 1.0f;
			if (splitStr.length > 1) {
				multiplier = Float.parseFloat(splitStr[1]);
			}

			if(fluid != null) {
				logger.info("Registering fluid "+ str + " as rocket nuclear working fluid");
				FuelRegistry.instance.registerFuel(FuelType.NUCLEAR_WORKING_FLUID, fluid, multiplier);
			}
			else
				logger.warn("Fluid name" + str  + " is not a registered fluid!");
		}
		liquidNuclearWorkingFluid = null; //clean up
		logger.info("Finished registering liquid rocket fuels");

		//Register Whitelisted Sealable Blocks

		logger.info("Start registering sealable blocks (sealableBlockWhiteList)");
		for(String str : sealableBlockWhiteList) {
			Block block = Block.getBlockFromName(str);
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				SealableBlockHandler.INSTANCE.addSealableBlock(block);
		}
		logger.info("End registering sealable blocks");
		sealableBlockWhiteList = null;

		logger.info("Start registering unsealable blocks (sealableBlockBlackList)");
		for(String str : sealableBlockBlackList) {
			Block block = Block.getBlockFromName(str);
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				SealableBlockHandler.INSTANCE.addUnsealableBlock(block);
		}
		logger.info("End registering unsealable blocks");
		sealableBlockBlackList = null;

		logger.info("Start registering torch blocks");
		for(String str : breakableTorches) {
			Block block = Block.getBlockFromName(str);
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				arConfig.torchBlocks.add(block);
		}
		logger.info("End registering torch blocks");
		breakableTorches = null;

		logger.info("Start registering blackhole generator blocks");
		for(String str : blackHoleGeneratorTiming) {
			String[] splitStr = str.split(";");

			String[] blockString = splitStr[0].split(":");

			Item block = Item.REGISTRY.getObject(new ResourceLocation(blockString[0],blockString[1]));
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
				arConfig.blackHoleGeneratorBlocks.put(new ItemStack(block, 1, metaValue), time);
		}
		logger.info("End registering blackhole generator blocks");
		breakableTorches = null;


		logger.info("Start registering rocket blacklist blocks");
		for(String str : blackListRocketBlocksStr) {
			Block block = Block.getBlockFromName(str);
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				arConfig.blackListRocketBlocks.add(block);
		}
		logger.info("End registering rocket blacklist blocks");
		blackListRocketBlocksStr = null;

		logger.info("Start registering Harvestable Gasses");
		for(String str : harvestableGasses) {
			Fluid fluid = FluidRegistry.getFluid(str);
			if(fluid == null)
				logger.warn("'" + str + "' is not a valid Fluid");
			else
				AtmosphereRegister.getInstance().registerHarvestableFluid(fluid);
		}
		logger.info("End registering Harvestable Gasses");
		harvestableGasses = null;

		logger.info("Start registering Spawnable Gasses");
		for(String str : spawnableGasses) {

			String[] splitStr = str.split(";");
			Fluid fluid = FluidRegistry.getFluid(splitStr[0]);
			int minGravity = 0;
			int maxGravity = 1600;
			double chance = 1.0;
			if (splitStr.length > 1) {
				minGravity = Integer.parseInt(splitStr[1]);
			}
			if (splitStr.length > 2) {
				maxGravity = Integer.parseInt(splitStr[2]);
			}
			if (splitStr.length > 3) {
				chance = Double.parseDouble(splitStr[3]);
			}
			if(fluid == null)
				logger.warn("'" + str + "' is not a valid Fluid");
			else
				AdvancedRocketryFluids.registerGasGiantGas(fluid, minGravity, maxGravity, chance);
		}
		logger.info("End registering Spawnable Gasses");
		spawnableGasses = null;

		logger.info("Start registering entity atmosphere bypass");

		//Add armor stand by default
		arConfig.bypassEntity.add(EntityArmorStand.class);


		for(String str : entityList) {
			Class clazz = EntityList.getClass(new ResourceLocation(str));

			//If not using string name maybe it's a class name?
			if(clazz == null) {
				try {
					clazz = Class.forName(str);
					if(!Entity.class.isAssignableFrom(clazz))
						clazz = null;

				} catch (Exception e) {
					//Fail silently
				}
			}

			if(clazz != null) {
				logger.info("Registering " + clazz.getName() + " for atmosphere bypass");
				arConfig.bypassEntity.add(clazz);
			}
			else
				logger.warn("Cannot find " + str + " while registering entity for atmosphere bypass");
		}

		//Free memory
		entityList = null;
		logger.info("End registering entity atmosphere bypass");

		//Register geodeOres
		if(!arConfig.geodeOresBlackList) {
			arConfig.standardGeodeOres.addAll(Arrays.asList(geodeOres));
		}

		//Register laserDrill ores
		if(!arConfig.laserDrillOresBlackList) {
			arConfig.standardLaserDrillOres.addAll(Arrays.asList(orbitalLaserOres));
		}


		//Do blacklist stuff for ore registration
		for(String oreName : OreDictionary.getOreNames()) {

			if(arConfig.geodeOresBlackList && oreName.startsWith("ore")) {
				boolean found = false;
				for(String str : geodeOres) {
					if(oreName.equals(str)) {
						found = true;
						break;
					}
				}
				if(!found)
					arConfig.standardGeodeOres.add(oreName);
			}

			if(arConfig.laserDrillOresBlackList && oreName.startsWith("ore")) {
				boolean found = false;
				for(String str : orbitalLaserOres) {
					if(oreName.equals(str)) {
						found = true;
						break;
					}
				}
				if(!found)
					arConfig.standardLaserDrillOres.add(oreName);
			}
		}
	}

	@ConfigProperty(needsSync=true)
	public int orbit = 1000;

	@ConfigProperty(needsSync=true)
	public int stationClearanceHeight = 1000;

	@ConfigProperty(needsSync=true)
	public int transBodyInjection = 0;

	@ConfigProperty(needsSync=true)
	public double asteroidTBIBurnMult = 1.0;

	@ConfigProperty(needsSync=true)
	public double warpTBIBurnMult = 10.0;

	@ConfigProperty
	public int MoonId = Constants.INVALID_PLANET;

	@ConfigProperty(needsSync=true)
	public int spaceDimId = -2;

	@ConfigProperty
	public int fuelPointsPer10Mb = 10;

	@ConfigProperty(needsSync=true)
	public int stationSize = 1024;

	@ConfigProperty
	public double rocketThrustMultiplier;

	@ConfigProperty
	public double nuclearCoreThrustRatio;

	@ConfigProperty
	public double fuelCapacityMultiplier;

	@ConfigProperty
	public int maxBiomes = 512;

	@ConfigProperty
	public boolean rocketRequireFuel = true;

	@ConfigProperty
	public boolean canBeFueledByHand = true;

	@ConfigProperty
	public boolean enableNausea = true;

	@ConfigProperty
	public boolean enableOxygen = true;

	@ConfigProperty
	public boolean launchingDestroysBlocks;

	@ConfigProperty
	public float buildSpeedMultiplier = 1f;

	@ConfigProperty
	public boolean generateCopper;

	@ConfigProperty
	public int copperPerChunk;

	@ConfigProperty
	public int copperClumpSize;

	@ConfigProperty
	public boolean generateTin;

	@ConfigProperty
	public int tinPerChunk;

	@ConfigProperty
	public int tinClumpSize;

	@ConfigProperty
	public boolean generateDilithium;

	@ConfigProperty
	public int dilithiumClumpSize;

	@ConfigProperty
	public int dilithiumPerChunk;

	@ConfigProperty
	public int dilithiumPerChunkMoon;

	public int aluminumPerChunk;

	@ConfigProperty
	public int aluminumClumpSize;

	@ConfigProperty
	public boolean generateAluminum;

	@ConfigProperty
	public boolean generateIridium;

	@ConfigProperty
	public int IridiumClumpSize;

	@ConfigProperty
	public int IridiumPerChunk;

	@ConfigProperty
	public boolean generateRutile;

	@ConfigProperty
	public int rutilePerChunk;

	@ConfigProperty
	public int rutileClumpSize;

	@ConfigProperty
	public boolean allowMakingItemsForOtherMods;

	@ConfigProperty
	public boolean scrubberRequiresCartrige;

	@ConfigProperty
	public float EUMult;

	@ConfigProperty
	public float RFMult;

	@ConfigProperty
	public boolean overrideGCAir;

	@ConfigProperty
	public int fuelPointsPerDilithium;

	@ConfigProperty
	public boolean electricPlantsSpawnLightning;

	@ConfigProperty
	public boolean allowSawmillVanillaWood;

	@ConfigProperty
	public int atmosphereHandleBitMask;

	@ConfigProperty
	public boolean automaticRetroRockets;

	@ConfigProperty
	public boolean advancedVFX;

	@ConfigProperty
	public boolean enableLaserDrill;

	@ConfigProperty
	public int spaceSuitOxygenTime;

	@ConfigProperty
	public float suitTankCapacity;

	@ConfigProperty
	public float travelTimeMultiplier;

	@ConfigProperty
	public int maxBiomesPerPlanet;

	@ConfigProperty
	public boolean enableTerraforming;

	@ConfigProperty
	public double gasCollectionMult;

	@ConfigProperty
	public int terraformingBlockSpeed;

	@ConfigProperty
	public double terraformSpeed;

	@ConfigProperty
	public boolean terraformRequiresFluid;

	@ConfigProperty
	public float microwaveRecieverMulitplier;

	@ConfigProperty
	public boolean blackListAllVanillaBiomes;

	@ConfigProperty
	public double asteroidMiningTimeMult;

	@ConfigProperty
	public boolean canPlayerRespawnInSpace;

	@ConfigProperty
	public boolean forcePlayerRespawnInSpace;

	@ConfigProperty
	public float spaceLaserPowerMult;

	@ConfigProperty
	public float blockTankCapacity;

	@ConfigProperty
	public LinkedList<Integer> laserBlackListDims = new LinkedList<>();

	@ConfigProperty
	public LinkedList<String> standardLaserDrillOres = new LinkedList<>();

	@ConfigProperty
	public boolean laserDrillPlanet;

	/** list of entities of which atmospheric effects should not be applied **/
	@ConfigProperty
	public LinkedList<Class> bypassEntity = new LinkedList<>();

	@ConfigProperty
	public LinkedList<Block> torchBlocks = new LinkedList<>();

	@ConfigProperty
	public LinkedList<Block> blackListRocketBlocks = new LinkedList<>();

	@ConfigProperty
	public LinkedList<String> standardGeodeOres = new LinkedList<>();

	@ConfigProperty(needsSync=true, internalType=Integer.class)
	public HashSet<Integer> initiallyKnownPlanets = new HashSet<>();

	@ConfigProperty
	public boolean geodeOresBlackList;

	@ConfigProperty
	public boolean laserDrillOresBlackList;

	@ConfigProperty(needsSync=true, keyType=String.class, valueType= Asteroid.class)
	public HashMap<String, Asteroid> asteroidTypes = new HashMap<>();

	@ConfigProperty
	public HashMap<String, Asteroid> prevAsteroidTypes = new HashMap<>();

	@ConfigProperty
	public int oxygenVentSize;

	@ConfigProperty
	public int solarGeneratorMult;

	@ConfigProperty
	public boolean gravityAffectsFuel;

	@ConfigProperty
	public boolean lowGravityBoots;

	@ConfigProperty
	public float jetPackThrust;

	@ConfigProperty
	public boolean enableGravityController;

	@ConfigProperty(needsSync=true)
	public boolean planetsMustBeDiscovered;

	@ConfigProperty
	public boolean generateGeodes;

	@ConfigProperty
	public int geodeBaseSize;

	@ConfigProperty
	public int geodeVariation;

	@ConfigProperty
	public int terraformliquidRate;

	@ConfigProperty
	public boolean dropExTorches;

	@ConfigProperty
	public double oxygenVentConsumptionMult;

	@ConfigProperty
	public int terraformPlanetSpeed;

	@ConfigProperty
	public int planetDiscoveryChance;

	@ConfigProperty
	public double oxygenVentPowerMultiplier;

	@ConfigProperty
	public boolean skyOverride;

	@ConfigProperty
	public boolean planetSkyOverride;

	@ConfigProperty
	public boolean stationSkyOverride;

	@ConfigProperty
	public boolean allowTerraformNonAR;

	@ConfigProperty
	public float crystalliserMaximumGravity;

	@ConfigProperty
	public boolean allowZeroGSpacestations;

	@ConfigProperty
	public float blackHolePowerMultiplier;

	@ConfigProperty
	public int defaultItemTimeBlackHole;

	@ConfigProperty
	public Map<ItemStack, Integer> blackHoleGeneratorBlocks = new HashMap<>();

	@ConfigProperty
	public String[] lavaCentrifugeOutputs;

	@ConfigProperty
	public boolean generateVanillaStructures;

	@ConfigProperty
	public boolean generateCraters;

	@ConfigProperty
	public boolean generateVolcanos;
	
	@ConfigProperty(needsSync=true)
	public boolean experimentalSpaceFlight;


	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ConfigProperty
	{
		boolean needsSync() default false;
		Class internalType() default Object.class;
		Class keyType() default Object.class;
		Class valueType() default Object.class;
	}
}
