package zmaster587.advancedRocketry;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zmaster587.advancedRocketry.achievements.ARAchivements;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.armor.ItemSpaceChest;
import zmaster587.advancedRocketry.atmosphere.AtmosphereVacuum;
import zmaster587.advancedRocketry.backwardCompat.VersionCompat;
import zmaster587.advancedRocketry.block.BlockAdvancedRocketMotor;
import zmaster587.advancedRocketry.block.BlockAstroBed;
import zmaster587.advancedRocketry.block.BlockBeacon;
import zmaster587.advancedRocketry.block.BlockCharcoalLog;
import zmaster587.advancedRocketry.block.BlockCrystal;
import zmaster587.advancedRocketry.block.BlockDoor2;
import zmaster587.advancedRocketry.block.BlockElectricMushroom;
import zmaster587.advancedRocketry.block.BlockFluid;
import zmaster587.advancedRocketry.block.BlockForceField;
import zmaster587.advancedRocketry.block.BlockForceFieldProjector;
import zmaster587.advancedRocketry.block.BlockHalfTile;
import zmaster587.advancedRocketry.block.BlockIntake;
import zmaster587.advancedRocketry.block.BlockLandingPad;
import zmaster587.advancedRocketry.block.BlockLaser;
import zmaster587.advancedRocketry.block.BlockLens;
import zmaster587.advancedRocketry.block.BlockLightSource;
import zmaster587.advancedRocketry.block.BlockLinkedHorizontalTexture;
import zmaster587.advancedRocketry.block.BlockMiningDrill;
import zmaster587.advancedRocketry.block.BlockPlanetSoil;
import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.block.BlockPressurizedFluidTank;
import zmaster587.advancedRocketry.block.BlockQuartzCrucible;
import zmaster587.advancedRocketry.block.BlockRedstoneEmitter;
import zmaster587.advancedRocketry.block.BlockRocketMotor;
import zmaster587.advancedRocketry.block.BlockSeal;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.block.BlockFuelTank;
import zmaster587.advancedRocketry.block.BlockSolarGenerator;
import zmaster587.advancedRocketry.block.BlockStationModuleDockingPort;
import zmaster587.advancedRocketry.block.BlockSuitWorkstation;
import zmaster587.advancedRocketry.block.BlockThermiteTorch;
import zmaster587.advancedRocketry.block.BlockTileNeighborUpdate;
import zmaster587.advancedRocketry.block.BlockTileRedstoneEmitter;
import zmaster587.advancedRocketry.block.BlockTorchUnlit;
import zmaster587.advancedRocketry.block.BlockTransciever;
import zmaster587.advancedRocketry.block.BlockWarpCore;
import zmaster587.advancedRocketry.block.BlockWarpShipMonitor;
import zmaster587.advancedRocketry.block.cable.BlockDataCable;
import zmaster587.advancedRocketry.block.cable.BlockEnergyCable;
import zmaster587.advancedRocketry.block.cable.BlockLiquidPipe;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.block.plant.BlockAlienLeaves;
import zmaster587.advancedRocketry.block.plant.BlockAlienPlank;
import zmaster587.advancedRocketry.block.plant.BlockAlienSapling;
import zmaster587.advancedRocketry.block.plant.BlockAlienWood;
import zmaster587.advancedRocketry.block.BlockTorchUnlit;
import zmaster587.advancedRocketry.capability.CapabilityProtectiveArmor;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.command.WorldCommand;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionProperties.AtmosphereTypes;
import zmaster587.advancedRocketry.dimension.DimensionProperties.Temps;
import zmaster587.advancedRocketry.enchant.EnchantmentSpaceBreathing;
import zmaster587.advancedRocketry.entity.EntityDummy;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.entity.EntityItemAbducted;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.EntityStationDeployedRocket;
import zmaster587.advancedRocketry.entity.EntityUIButton;
import zmaster587.advancedRocketry.entity.EntityUIPlanet;
import zmaster587.advancedRocketry.entity.EntityUIStar;
import zmaster587.advancedRocketry.event.BucketHandler;
import zmaster587.advancedRocketry.event.CableTickHandler;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.WorldEvents;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.items.ItemBlockMeta;
import zmaster587.libVulpes.items.ItemIngredient;
import zmaster587.libVulpes.items.ItemProjector;
import zmaster587.advancedRocketry.item.*;
import zmaster587.advancedRocketry.item.components.ItemJetpack;
import zmaster587.advancedRocketry.item.components.ItemPressureTank;
import zmaster587.advancedRocketry.item.components.ItemUpgrade;
import zmaster587.advancedRocketry.item.tools.ItemBasicLaserGun;
import zmaster587.advancedRocketry.mission.MissionGasCollection;
import zmaster587.advancedRocketry.mission.MissionOreMining;
import zmaster587.advancedRocketry.network.PacketAsteroidInfo;
import zmaster587.advancedRocketry.network.PacketAtmSync;
import zmaster587.advancedRocketry.network.PacketBiomeIDChange;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketInvalidLocationNotify;
import zmaster587.advancedRocketry.network.PacketLaserGun;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.network.PacketStellarInfo;
import zmaster587.advancedRocketry.network.PacketStorageTileUpdate;
import zmaster587.advancedRocketry.network.PacketAirParticle;
import zmaster587.advancedRocketry.satellite.SatelliteBiomeChanger;
import zmaster587.advancedRocketry.satellite.SatelliteComposition;
import zmaster587.advancedRocketry.satellite.SatelliteDensity;
import zmaster587.advancedRocketry.satellite.SatelliteEnergy;
import zmaster587.advancedRocketry.satellite.SatelliteMassScanner;
import zmaster587.advancedRocketry.satellite.SatelliteOptical;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.tile.Satellite.TileEntitySatelliteControlCenter;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteBuilder;
import zmaster587.advancedRocketry.tile.*;
import zmaster587.advancedRocketry.tile.cables.TileDataPipe;
import zmaster587.advancedRocketry.tile.cables.TileEnergyPipe;
import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;
import zmaster587.advancedRocketry.tile.cables.TileWirelessTransciever;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityFuelingStation;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityMoniteringStation;
import zmaster587.advancedRocketry.tile.infrastructure.TileGuidanceComputerHatch;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketFluidLoader;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketFluidUnloader;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketLoader;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketUnloader;
import zmaster587.advancedRocketry.tile.multiblock.*;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.advancedRocketry.tile.oxygen.TileCO2Scrubber;
import zmaster587.advancedRocketry.tile.oxygen.TileOxygenCharger;
import zmaster587.advancedRocketry.tile.oxygen.TileOxygenVent;
import zmaster587.advancedRocketry.tile.station.TilePlanetaryHologram;
import zmaster587.advancedRocketry.tile.station.TileStationAltitudeController;
import zmaster587.advancedRocketry.tile.station.TileDockingPort;
import zmaster587.advancedRocketry.tile.station.TileLandingPad;
import zmaster587.advancedRocketry.tile.station.TileStationGravityController;
import zmaster587.advancedRocketry.tile.station.TileStationOrientationControl;
import zmaster587.advancedRocketry.tile.station.TileWarpShipMonitor;
import zmaster587.advancedRocketry.util.AsteroidSmall;
import zmaster587.advancedRocketry.util.FluidColored;
import zmaster587.advancedRocketry.util.GravityHandler;
import zmaster587.advancedRocketry.util.OreGenProperties;
import zmaster587.advancedRocketry.util.RecipeHandler;
import zmaster587.advancedRocketry.util.SealableBlockHandler;
import zmaster587.advancedRocketry.util.XMLAsteroidLoader;
import zmaster587.advancedRocketry.util.XMLOreLoader;
import zmaster587.advancedRocketry.util.XMLPlanetLoader;
import zmaster587.advancedRocketry.util.XMLPlanetLoader.DimensionPropertyCoupling;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import zmaster587.advancedRocketry.world.biome.BiomeGenCrystal;
import zmaster587.advancedRocketry.world.biome.BiomeGenDeepSwamp;
import zmaster587.advancedRocketry.world.biome.BiomeGenHotDryRock;
import zmaster587.advancedRocketry.world.biome.BiomeGenMoon;
import zmaster587.advancedRocketry.world.biome.BiomeGenMarsh;
import zmaster587.advancedRocketry.world.biome.BiomeGenOceanSpires;
import zmaster587.advancedRocketry.world.biome.BiomeGenSpace;
import zmaster587.advancedRocketry.world.biome.BiomeGenStormland;
import zmaster587.advancedRocketry.world.decoration.MapGenLander;
import zmaster587.advancedRocketry.world.ore.OreGenerator;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.type.WorldTypePlanetGen;
import zmaster587.advancedRocketry.world.type.WorldTypeSpace;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.api.material.MixedMaterial;
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockMotor;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.TileMaterial;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.InputSyncHandler;
import zmaster587.libVulpes.util.SingleEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid="advancedRocketry", name="Advanced Rocketry", version="@MAJOR@.@MINOR@.@REVIS@@BUILD@", dependencies="required-after:libVulpes@[%LIBVULPESVERSION%,)")
public class AdvancedRocketry {


	@SidedProxy(clientSide="zmaster587.advancedRocketry.client.ClientProxy", serverSide="zmaster587.advancedRocketry.common.CommonProxy")
	public static CommonProxy proxy;

	public final static String version = "@MAJOR@.@MINOR@.@REVIS@@BUILD@";

	@Instance(value = Constants.modId)
	public static AdvancedRocketry instance;
	public static WorldType planetWorldType;
	public static WorldType spaceWorldType;
	public static final RecipeHandler machineRecipes = new RecipeHandler();

	final String oreGen = "Ore Generation";
	final String ROCKET = "Rockets";
	final String MOD_INTERACTION = "Mod Interaction";
	final String PLANET = "Planet";
	final String ASTEROID = "Asteroid";
	final String GAS_MINING = "GasMining";
	final String PERFORMANCE = "Performance";
	final String CLIENT = "Client";

	public static CompatibilityMgr compat = new CompatibilityMgr();
	public static Logger logger = LogManager.getLogger(Constants.modId);
	private static Configuration config;
	private static final String BIOMECATETORY = "Biomes";
	private boolean resetFromXml;
	String[] sealableBlockWhiteList, breakableTorches,  blackListRocketBlocks, harvestableGasses, entityList, asteriodOres, geodeOres, orbitalLaserOres, liquidRocketFuel;

	//static {
	//	FluidRegistry.enableUniversalBucket(); // Must be called before preInit
	//}

	public static MaterialRegistry materialRegistry = new MaterialRegistry(); 

	public static HashMap<AllowedProducts, HashSet<String>> modProducts = new HashMap<AllowedProducts, HashSet<String>>();


	private static CreativeTabs tabAdvRocketry = new CreativeTabs("advancedRocketry") {
		@Override
		public Item getTabIconItem() {
			return AdvancedRocketryItems.itemSatelliteIdChip;
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		//Init API
		DimensionManager.planetWorldProvider = WorldProviderPlanet.class;
		AdvancedRocketryAPI.atomsphereSealHandler = SealableBlockHandler.INSTANCE;
		((SealableBlockHandler)AdvancedRocketryAPI.atomsphereSealHandler).loadDefaultData();


		//Configuration  ---------------------------------------------------------------------------------------------

		config = new Configuration(new File(event.getModConfigurationDirectory(), "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/advancedRocketry.cfg"));
		config.load();

		AtmosphereVacuum.damageValue = (int) config.get(Configuration.CATEGORY_GENERAL, "vacuumDamage", 1, "Amount of damage taken every second in a vacuum").getInt();
		zmaster587.advancedRocketry.api.Configuration.buildSpeedMultiplier = (float) config.get(Configuration.CATEGORY_GENERAL, "buildSpeedMultiplier", 1f, "Multiplier for the build speed of the Rocket Builder (0.5 is twice as fast 2 is half as fast").getDouble();
		zmaster587.advancedRocketry.api.Configuration.spaceDimId = config.get(Configuration.CATEGORY_GENERAL,"spaceStationId" , -2,"Dimension ID to use for space stations").getInt();
		zmaster587.advancedRocketry.api.Configuration.enableOxygen = config.get(Configuration.CATEGORY_GENERAL, "EnableAtmosphericEffects", true, "If true, allows players being hurt due to lack of oxygen and allows effects from non-standard atmosphere types").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods = config.get(Configuration.CATEGORY_GENERAL, "makeMaterialsForOtherMods", true, "If true the machines from AdvancedRocketry will produce things like plates/rods for other mods even if Advanced Rocketry itself does not use the material (This can increase load time)").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.scrubberRequiresCartrige = config.get(Configuration.CATEGORY_GENERAL, "scrubberRequiresCartrige", true, "If true the Oxygen scrubbers require a consumable carbon collection cartridge").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.enableLaserDrill = config.get(Configuration.CATEGORY_GENERAL, "EnableLaserDrill", true, "Enables the laser drill machine").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.spaceLaserPowerMult = (float)config.get(Configuration.CATEGORY_GENERAL, "LaserDrillPowerMultiplier", 1d, "Power multiplier for the laser drill machine").getDouble();
		zmaster587.advancedRocketry.api.Configuration.lowGravityBoots = config.get(Configuration.CATEGORY_GENERAL, "lowGravityBoots", false, "If true the boots only protect the player on planets with low gravity").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.jetPackThrust = (float)config.get(Configuration.CATEGORY_GENERAL, "jetPackForce", 1.3, "Amount of force the jetpack provides with respect to gravity, 1 is the same acceleration as caused by Earth's gravity, 2 is 2x the acceleration caused by Earth's gravity, etc.  To make jetpack only work on low gravity planets, simply set it to a value less than 1").getDouble();

		int spaceBreathingId = config.get(Configuration.CATEGORY_GENERAL, "AirtightSealEnchantID", 128, "Enchantment ID for the airtight seal effect").getInt();
		
		zmaster587.advancedRocketry.api.Configuration.enableTerraforming = config.get(Configuration.CATEGORY_GENERAL, "EnableTerraforming", true,"Enables terraforming items and blocks").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.oxygenVentPowerMultiplier = config.get(Configuration.CATEGORY_GENERAL, "OxygenVentPowerMultiplier", 1.0f, "Power consumption multiplier for the oxygen vent", 0, Float.MAX_VALUE).getDouble();
		zmaster587.advancedRocketry.api.Configuration.spaceSuitOxygenTime = config.get(Configuration.CATEGORY_GENERAL, "spaceSuitO2Buffer", 30, "Maximum time in minutes that the spacesuit's internal buffer can store O2 for").getInt();
		zmaster587.advancedRocketry.api.Configuration.travelTimeMultiplier = (float)config.get(Configuration.CATEGORY_GENERAL, "warpTravelTime", 1f, "Multiplier for warp travel time").getDouble();
		zmaster587.advancedRocketry.api.Configuration.maxBiomesPerPlanet = config.get(Configuration.CATEGORY_GENERAL, "maxBiomesPerPlanet", 5, "Maximum unique biomes per planet, -1 to disable").getInt();
		zmaster587.advancedRocketry.api.Configuration.allowTerraforming = config.get(Configuration.CATEGORY_GENERAL, "allowTerraforming", false, "EXPERIMENTAL: If set to true allows contruction and usage of the terraformer.  This is known to cause strange world generation after successful terraform").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.terraformingBlockSpeed = config.get(Configuration.CATEGORY_GENERAL, "biomeUpdateSpeed", 1, "How many blocks have the biome changed per tick.  Large numbers can slow the server down", Integer.MAX_VALUE, 1).getInt();
		zmaster587.advancedRocketry.api.Configuration.terraformSpeed = config.get(Configuration.CATEGORY_GENERAL, "terraformMult", 1f, "Multplier for atmosphere change speed").getDouble();
		zmaster587.advancedRocketry.api.Configuration.terraformPlanetSpeed = config.get(Configuration.CATEGORY_GENERAL, "terraformBlockPerTick", 1, "Max number of blocks allowed to be changed per tick").getInt();
		zmaster587.advancedRocketry.api.Configuration.terraformRequiresFluid = config.get(Configuration.CATEGORY_GENERAL, "TerraformerRequiresFluids", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.terraformliquidRate = config.get(Configuration.CATEGORY_GENERAL, "TerraformerFluidConsumeRate", 40, "how many millibuckets/t are required to keep the terraformer running").getInt();
		zmaster587.advancedRocketry.api.Configuration.allowTerraformNonAR = config.get(Configuration.CATEGORY_GENERAL, "allowTerraformingNonARWorlds", false, "If true dimensions not added by AR can be terraformed, including the overworld").getBoolean();
		
		liquidRocketFuel = config.get(ROCKET, "rocketFuels", new String[] {"rocketfuel"}, "List of fluid names for fluids that can be used as rocket fuel").getStringList();
		
		zmaster587.advancedRocketry.api.Configuration.stationSize = config.get(Configuration.CATEGORY_GENERAL, "SpaceStationBuildRadius", 1024, "The largest size a space station can be.  Should also be a power of 2 (512, 1024, 2048, 4096, ...).  CAUTION: CHANGING THIS OPTION WILL DAMAGE EXISTING STATIONS!!!").getInt();
		zmaster587.advancedRocketry.api.Configuration.canPlayerRespawnInSpace = config.get(Configuration.CATEGORY_GENERAL, "allowPlanetRespawn", false, "If true players will respawn near beds on planets IF the spawn location is in a breathable atmosphere").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.solarGeneratorMult = config.get(Configuration.CATEGORY_GENERAL, "solarGeneratorMultiplier", 1, "Amount of power per tick the solar generator should produce").getInt();
		zmaster587.advancedRocketry.api.Configuration.enableGravityController = config.get(Configuration.CATEGORY_GENERAL, "enableGravityMachine", true, "If false the gravity controller cannot be built or used").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.planetsMustBeDiscovered = config.get(Configuration.CATEGORY_GENERAL, "planetsMustBeDiscovered", false, "If true planets must be discovered in the warp controller before being visible").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.dropExTorches = config.get(Configuration.CATEGORY_GENERAL, "dropExtinguishedTorches", false, "If true, breaking an extinguished torch will drop an extinguished torch instead of a vanilla torch").getBoolean();

		
		DimensionManager.dimOffset = config.getInt("minDimension", PLANET, 2, -127, 8000, "Dimensions including and after this number are allowed to be made into planets");
		zmaster587.advancedRocketry.api.Configuration.blackListAllVanillaBiomes = config.getBoolean("blackListVanillaBiomes", PLANET, false, "Prevents any vanilla biomes from spawning on planets");
		zmaster587.advancedRocketry.api.Configuration.overrideGCAir = config.get(MOD_INTERACTION, "OverrideGCAir", true, "If true Galaciticcraft's air will be disabled entirely requiring use of Advanced Rocketry's Oxygen system on GC planets").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.fuelPointsPerDilithium = config.get(Configuration.CATEGORY_GENERAL, "pointsPerDilithium", 500, "How many units of fuel should each Dilithium Crystal give to warp ships", 1, 1000).getInt();
		zmaster587.advancedRocketry.api.Configuration.electricPlantsSpawnLightning = config.get(Configuration.CATEGORY_GENERAL, "electricPlantsSpawnLightning", true, "Should Electric Mushrooms be able to spawn lightning").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.allowSawmillVanillaWood = config.get(Configuration.CATEGORY_GENERAL, "sawMillCutVanillaWood", true, "Should the cutting machine be able to cut vanilla wood into planks").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.automaticRetroRockets = config.get(ROCKET, "autoRetroRockets", true, "Setting to false will disable the retrorockets that fire automatically on reentry on both player and automated rockets").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.atmosphereHandleBitMask = config.get(PERFORMANCE, "atmosphereCalculationMethod", 3, "BitMask: 0: no threading, radius based; 1: threading, radius based (EXP); 2: no threading volume based; 3: threading volume based (EXP)").getInt();
		zmaster587.advancedRocketry.api.Configuration.oxygenVentSize = config.get(PERFORMANCE, "oxygenVentSize", 32, "Radius of the O2 vent.  if atmosphereCalculationMethod is 2 or 3 then max volume is calculated from this radius.  WARNING: larger numbers can lead to lag").getInt();
		zmaster587.advancedRocketry.api.Configuration.oxygenVentConsumptionMult = config.get(Configuration.CATEGORY_GENERAL, "oxygenVentConsumptionMultiplier", 1f, "Multiplier on how much O2 an oxygen vent consumes per tick").getDouble();
		zmaster587.advancedRocketry.api.Configuration.gravityAffectsFuel = config.get(Configuration.CATEGORY_GENERAL, "gravityAffectsFuels", true, "If true planets with higher gravity require more fuel and lower gravity would require less").getBoolean();

		zmaster587.advancedRocketry.api.Configuration.stationSkyOverride = config.get(CLIENT, "StationSkyOverride", true, "If true, AR will use a custom skybox on space stations").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.planetSkyOverride = config.get(CLIENT, "PlanetSkyOverride", true, "If true, AR will use a custom skybox on planets").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.skyOverride = config.get(CLIENT, "overworldSkyOverride", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.advancedVFX = config.get(PERFORMANCE, "advancedVFX", true, "Advanced visual effects").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.gasCollectionMult = config.get(GAS_MINING, "gasMissionMultiplier", 1.0, "Multiplier for the amount of time gas collection missions take").getDouble();
		zmaster587.advancedRocketry.api.Configuration.asteroidMiningTimeMult = config.get(ASTEROID, "miningMissionTmeMultiplier", 1.0, "Multiplier changing how long a mining mission takes").getDouble();
		asteriodOres = config.get(ASTEROID, "standardOres", new String[] {"oreIron", "oreGold", "oreCopper", "oreTin", "oreRedstone"}, "List of oredictionary names of ores allowed to spawn in asteriods").getStringList();
		geodeOres = config.get(oreGen, "geodeOres", new String[] {"oreIron", "oreGold", "oreCopper", "oreTin", "oreRedstone"}, "List of oredictionary names of ores allowed to spawn in geodes").getStringList();
		zmaster587.advancedRocketry.api.Configuration.geodeOresBlackList = config.get(oreGen, "geodeOres_blacklist", false, "True if the ores in geodeOres should be a blacklist, false for whitelist").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.generateGeodes = config.get(oreGen, "generateGeodes", true, "If true then ore-containing geodes are generated on high pressure planets").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.geodeBaseSize = config.get(oreGen, "geodeBaseSize", 36, "average size of the geodes").getInt();
		zmaster587.advancedRocketry.api.Configuration.geodeVariation = config.get(oreGen, "geodeVariation", 24, "variation in geode size").getInt();
		
		zmaster587.advancedRocketry.api.Configuration.planetDiscoveryChance = config.get(PLANET, "planetDiscoveryChance", 5, "Chance of planet discovery in the warp ship monitor is not all planets are initially discoved, chance is 1/n", 1, Integer.MAX_VALUE).getInt();

		orbitalLaserOres = config.get(Configuration.CATEGORY_GENERAL, "laserDrillOres", new String[] {"oreIron", "oreGold", "oreCopper", "oreTin", "oreRedstone", "oreDiamond"}, "List of oredictionary names of ores allowed to be mined by the laser drill if surface drilling is disabled.  Ores can be specified by just the oreName:<size> or by <modname>:<blockname>:<meta>:<size> where size is optional").getStringList();
		zmaster587.advancedRocketry.api.Configuration.laserDrillOresBlackList = config.get(Configuration.CATEGORY_GENERAL, "laserDrillOres_blacklist", false, "True if the ores in laserDrillOres should be a blacklist, false for whitelist").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.laserDrillPlanet = config.get(Configuration.CATEGORY_GENERAL, "laserDrillPlanet", false, "If true the orbital laser will actually mine blocks on the planet below").getBoolean();
		resetFromXml = config.getBoolean("resetPlanetsFromXML", Configuration.CATEGORY_GENERAL, false, "setting this to true will DELETE existing advancedrocketry planets and regen the solar system from the advanced planet XML file, satellites orbiting the overworld will remain intact and stations will be moved to the overworld.");
		//Reset to false
		config.get(Configuration.CATEGORY_GENERAL, "resetPlanetsFromXML",false).set(false);

		//Client
		zmaster587.advancedRocketry.api.Configuration.rocketRequireFuel = config.get(ROCKET, "rocketsRequireFuel", true, "Set to false if rockets should not require fuel to fly").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.rocketThrustMultiplier = config.get(ROCKET, "thrustMultiplier", 1f, "Multiplier for per-engine thrust").getDouble();
		zmaster587.advancedRocketry.api.Configuration.fuelCapacityMultiplier = config.get(ROCKET, "fuelCapacityMultiplier", 1f, "Multiplier for per-tank capacity").getDouble();

		//Copper Config
		zmaster587.advancedRocketry.api.Configuration.generateCopper = config.get(oreGen, "GenerateCopper", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.copperClumpSize = config.get(oreGen, "CopperPerClump", 6).getInt();
		zmaster587.advancedRocketry.api.Configuration.copperPerChunk = config.get(oreGen, "CopperPerChunk", 10).getInt();

		//Tin Config
		zmaster587.advancedRocketry.api.Configuration.generateTin = config.get(oreGen, "GenerateTin", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.tinClumpSize = config.get(oreGen, "TinPerClump", 6).getInt();
		zmaster587.advancedRocketry.api.Configuration.tinPerChunk = config.get(oreGen, "TinPerChunk", 10).getInt();

		zmaster587.advancedRocketry.api.Configuration.generateDilithium = config.get(oreGen, "generateDilithium", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.dilithiumClumpSize = config.get(oreGen, "DilithiumPerClump", 16).getInt();
		zmaster587.advancedRocketry.api.Configuration.dilithiumPerChunk = config.get(oreGen, "DilithiumPerChunk", 1).getInt();
		zmaster587.advancedRocketry.api.Configuration.dilithiumPerChunkMoon = config.get(oreGen, "DilithiumPerChunkLuna", 10).getInt();

		zmaster587.advancedRocketry.api.Configuration.generateAluminum = config.get(oreGen, "generateAluminum", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.aluminumClumpSize = config.get(oreGen, "AluminumPerClump", 16).getInt();
		zmaster587.advancedRocketry.api.Configuration.aluminumPerChunk = config.get(oreGen, "AluminumPerChunk", 1).getInt();
		
		zmaster587.advancedRocketry.api.Configuration.generateIridium = config.get(oreGen, "generateIridium", false).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.IridiumClumpSize = config.get(oreGen, "IridiumPerClump", 16).getInt();
		zmaster587.advancedRocketry.api.Configuration.IridiumPerChunk = config.get(oreGen, "IridiumPerChunk", 1).getInt();

		zmaster587.advancedRocketry.api.Configuration.generateRutile = config.get(oreGen, "GenerateRutile", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.rutileClumpSize = config.get(oreGen, "RutilePerClump", 6).getInt();
		zmaster587.advancedRocketry.api.Configuration.rutilePerChunk = config.get(oreGen, "RutilePerChunk", 6).getInt();
		sealableBlockWhiteList = config.getStringList(Configuration.CATEGORY_GENERAL, "sealableBlockWhiteList", new String[] {}, "Mod:Blockname  for example \"minecraft:chest\"");
		blackListRocketBlocks = config.getStringList("rocketBlockBlackList", Configuration.CATEGORY_GENERAL, new String[] {}, "Mod:Blockname  for example \"minecraft:chest\"");
		breakableTorches = config.getStringList("torchBlocks", Configuration.CATEGORY_GENERAL, new String[] {}, "Mod:Blockname  for example \"minecraft:chest\"");
		
		harvestableGasses = config.getStringList("harvestableGasses", GAS_MINING, new String[] {}, "list of fluid names that can be harvested as Gas");

		entityList = config.getStringList("entityAtmBypass", Configuration.CATEGORY_GENERAL, new String[] {}, "list entities which should not be affected by atmosphere properties");

		//Satellite config
		zmaster587.advancedRocketry.api.Configuration.microwaveRecieverMulitplier = 10*(float)config.get(Configuration.CATEGORY_GENERAL, "MicrowaveRecieverMultiplier", 1f, "Multiplier for the amount of energy produced by the microwave reciever").getDouble();

		String str[] = config.getStringList("spaceLaserDimIdBlackList", Configuration.CATEGORY_GENERAL, new String[] {}, "Laser drill will not mine these dimension");

		//Load laser dimid blacklists
		for(String s : str) {

			try {
				zmaster587.advancedRocketry.api.Configuration.laserBlackListDims.add(Integer.parseInt(s));
			} catch (NumberFormatException e) {
				logger.warn("Invalid number \"" + s + "\" for laser dimid blacklist");
			}
		}

		//Load client and UI positioning stuff
		proxy.loadUILayout(config);

		config.save();

		//Register cap events
		MinecraftForge.EVENT_BUS.register(new CapabilityProtectiveArmor());

		//Register Packets
		PacketHandler.INSTANCE.addDiscriminator(PacketDimInfo.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketSatellite.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketStellarInfo.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketItemModifcation.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketOxygenState.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketStationUpdate.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketSpaceStationInfo.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketAtmSync.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketBiomeIDChange.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketStorageTileUpdate.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketLaserGun.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketAsteroidInfo.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketAirParticle.class);
		PacketHandler.INSTANCE.addDiscriminator(PacketInvalidLocationNotify.class);
		
		//if(zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods)
		MinecraftForge.EVENT_BUS.register(this);

		//Satellites ---------------------------------------------------------------------------------------------
		SatelliteRegistry.registerSatellite("optical", SatelliteOptical.class);
		SatelliteRegistry.registerSatellite("solar", SatelliteEnergy.class);
		SatelliteRegistry.registerSatellite("density", SatelliteDensity.class);
		SatelliteRegistry.registerSatellite("composition", SatelliteComposition.class);
		SatelliteRegistry.registerSatellite("mass", SatelliteMassScanner.class);
		SatelliteRegistry.registerSatellite("asteroidMiner", MissionOreMining.class);
		SatelliteRegistry.registerSatellite("gasMining", MissionGasCollection.class);
		SatelliteRegistry.registerSatellite("solarEnergy", SatelliteEnergy.class);
		SatelliteRegistry.registerSatellite("oreScanner", SatelliteOreMapping.class);
		SatelliteRegistry.registerSatellite("biomeChanger", SatelliteBiomeChanger.class);


		//Blocks -------------------------------------------------------------------------------------
		AdvancedRocketryBlocks.blocksGeode = new Block(MaterialGeode.geode).setUnlocalizedName("geode").setCreativeTab(LibVulpes.tabLibVulpesOres).setHardness(6f).setResistance(2000F);
		AdvancedRocketryBlocks.blocksGeode.setHarvestLevel("jackhammer", 2);
		AdvancedRocketryBlocks.blockLaunchpad = new BlockLinkedHorizontalTexture(Material.ROCK).setUnlocalizedName("pad").setCreativeTab(tabAdvRocketry).setHardness(2f).setResistance(10f);
		AdvancedRocketryBlocks.blockStructureTower = new BlockAlphaTexture(Material.ROCK).setUnlocalizedName("structuretower").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockGenericSeat = new BlockSeat(Material.CLOTH).setUnlocalizedName("seat").setCreativeTab(tabAdvRocketry).setHardness(0.5f);
		AdvancedRocketryBlocks.blockEngine = new BlockRocketMotor(Material.ROCK).setUnlocalizedName("rocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockAdvEngine = new BlockAdvancedRocketMotor(Material.ROCK).setUnlocalizedName("advRocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockFuelTank = new BlockFuelTank(Material.ROCK).setUnlocalizedName("fuelTank").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockSawBlade = new BlockMotor(Material.ROCK,1f).setCreativeTab(tabAdvRocketry).setUnlocalizedName("sawBlade").setHardness(2f);

		AdvancedRocketryBlocks.blockConcrete = new Block(Material.ROCK).setUnlocalizedName("concrete").setCreativeTab(tabAdvRocketry).setHardness(3f).setResistance(16f);
		AdvancedRocketryBlocks.blockPlatePress = new BlockPress().setUnlocalizedName("blockHandPress").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockAirLock = new BlockDoor2(Material.ROCK).setUnlocalizedName("smallAirlockDoor").setHardness(3f).setResistance(8f);
		AdvancedRocketryBlocks.blockLandingPad = new BlockLandingPad(Material.ROCK).setUnlocalizedName("dockingPad").setHardness(3f).setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockOxygenDetection = new BlockRedstoneEmitter(Material.ROCK,"advancedrocketry:atmosphereDetector_active").setUnlocalizedName("atmosphereDetector").setHardness(3f).setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockOxygenScrubber = new BlockTile(TileCO2Scrubber.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("scrubber").setHardness(3f);
		AdvancedRocketryBlocks.blockUnlitTorch = new BlockTorchUnlit().setHardness(0.0F).setUnlocalizedName("unlittorch");
		AdvancedRocketryBlocks.blockVitrifiedSand = new Block(Material.SAND).setUnlocalizedName("vitrifiedSand").setCreativeTab(CreativeTabs.BUILDING_BLOCKS).setHardness(0.5F);
		AdvancedRocketryBlocks.blockCharcoalLog = new BlockCharcoalLog().setUnlocalizedName("charcoallog").setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		AdvancedRocketryBlocks.blockElectricMushroom = new BlockElectricMushroom().setUnlocalizedName("electricMushroom").setCreativeTab(tabAdvRocketry).setHardness(0.0F);
		AdvancedRocketryBlocks.blockCrystal = new BlockCrystal().setUnlocalizedName("crystal").setCreativeTab(LibVulpes.tabLibVulpesOres).setHardness(2f);
		AdvancedRocketryBlocks.blockOrientationController = new BlockTile(TileStationOrientationControl.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("orientationControl").setHardness(3f);
		AdvancedRocketryBlocks.blockGravityController = new BlockTile(TileStationGravityController.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("gravityControl").setHardness(3f);
		AdvancedRocketryBlocks.blockAltitudeController = new BlockTile(TileStationAltitudeController.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("altitudeController").setHardness(3f);
		AdvancedRocketryBlocks.blockOxygenCharger = new BlockHalfTile(TileOxygenCharger.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("oxygenCharger").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockOxygenVent = new BlockTile(TileOxygenVent.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("oxygenVent").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockCircleLight = new Block(Material.IRON).setUnlocalizedName("circleLight").setCreativeTab(tabAdvRocketry).setHardness(2f).setLightLevel(1f);
		AdvancedRocketryBlocks.blockLens = new BlockLens().setUnlocalizedName("lens").setCreativeTab(tabAdvRocketry).setHardness(0.3f);
		AdvancedRocketryBlocks.blockRocketBuilder = new BlockTile(TileRocketBuilder.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("rocketAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockForceField = new BlockForceField(Material.ROCK).setBlockUnbreakable().setResistance(6000000.0F).setUnlocalizedName("forceField");
		AdvancedRocketryBlocks.blockForceFieldProjector = new BlockForceFieldProjector(Material.ROCK).setUnlocalizedName("forceFieldProjector").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockDeployableRocketBuilder = new BlockTile(TileStationDeployedAssembler.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("deployableRocketAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockStationBuilder = new BlockTile(TileStationBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("stationAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockFuelingStation = new BlockTileRedstoneEmitter(TileEntityFuelingStation.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("fuelStation").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockMonitoringStation = new BlockTileNeighborUpdate(TileEntityMoniteringStation.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockMonitoringStation.setUnlocalizedName("monitoringstation");

		AdvancedRocketryBlocks.blockWarpShipMonitor = new BlockWarpShipMonitor(TileWarpShipMonitor.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockWarpShipMonitor.setUnlocalizedName("stationmonitor");

		AdvancedRocketryBlocks.blockSatelliteBuilder = new BlockMultiblockMachine(TileSatelliteBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSatelliteBuilder.setUnlocalizedName("satelliteBuilder");

		AdvancedRocketryBlocks.blockSatelliteControlCenter = new BlockTile(TileEntitySatelliteControlCenter.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSatelliteControlCenter.setUnlocalizedName("satelliteMonitor");

		AdvancedRocketryBlocks.blockMicrowaveReciever = new BlockMultiblockMachine(TileMicrowaveReciever.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockMicrowaveReciever.setUnlocalizedName("microwaveReciever");

		//Arcfurnace
		AdvancedRocketryBlocks.blockArcFurnace = new BlockMultiblockMachine(TileElectricArcFurnace.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("electricArcFurnace").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockMoonTurf = new BlockPlanetSoil().setMapColor(MapColor.SNOW).setHardness(0.5F).setUnlocalizedName("turf").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockHotTurf = new BlockPlanetSoil().setMapColor(MapColor.NETHERRACK).setHardness(0.5F).setUnlocalizedName("hotDryturf").setCreativeTab(tabAdvRocketry);

		AdvancedRocketryBlocks.blockLoader = new BlockARHatch(Material.ROCK).setUnlocalizedName("loader").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockAlienWood = new BlockAlienWood().setUnlocalizedName("log").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienLeaves = new BlockAlienLeaves().setUnlocalizedName("leaves2").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienSapling = new BlockAlienSapling().setUnlocalizedName("sapling").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienPlanks = new BlockAlienPlank().setUnlocalizedName("planks").setCreativeTab(tabAdvRocketry).setHardness(3f);
		
		AdvancedRocketryBlocks.blockLightSource = new BlockLightSource();

		AdvancedRocketryBlocks.blockBlastBrick = new BlockMultiBlockComponentVisible(Material.ROCK).setCreativeTab(tabAdvRocketry).setUnlocalizedName("blastBrick").setHardness(3F).setResistance(15F);
		AdvancedRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible().setUnlocalizedName("qcrucible").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockAstroBed = new BlockAstroBed().setHardness(0.2F).setUnlocalizedName("astroBed");

		AdvancedRocketryBlocks.blockPrecisionAssembler = new BlockMultiblockMachine(TilePrecisionAssembler.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("precisionAssemblingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockCuttingMachine = new BlockMultiblockMachine(TileCuttingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("cuttingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(TileCrystallizer.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("Crystallizer").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockWarpCore = new BlockWarpCore(TileWarpCore.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("warpCore").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockChemicalReactor = new BlockMultiblockMachine(TileChemicalReactor.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("chemreactor").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockLathe = new BlockMultiblockMachine(TileLathe.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("lathe").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockRollingMachine = new BlockMultiblockMachine(TileRollingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("rollingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockElectrolyser = new BlockMultiblockMachine(TileElectrolyser.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("electrolyser").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockAtmosphereTerraformer = new BlockMultiblockMachine(TileAtmosphereTerraformer.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("atmosphereTerraformer").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockPlanetAnalyser = new BlockMultiblockMachine(TileAstrobodyDataProcessor.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("planetanalyser").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockObservatory = (BlockMultiblockMachine) new BlockMultiblockMachine(TileObservatory.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("observatory").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockGuidanceComputer = new BlockTile(TileGuidanceComputer.class,GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("guidanceComputer").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockPlanetSelector = new BlockTile(TilePlanetSelector.class,GuiHandler.guiId.MODULARFULLSCREEN.ordinal()).setUnlocalizedName("planetSelector").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockPlanetHoloSelector = new BlockHalfTile(TilePlanetaryHologram.class,GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("planetHoloSelector").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockBiomeScanner = new BlockMultiblockMachine(TileBiomeScanner.class,GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("biomeScanner").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockDrill = new BlockMiningDrill().setUnlocalizedName("drill").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockSuitWorkStation = new BlockSuitWorkstation(TileSuitWorkStation.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("suitWorkStation").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockRailgun = new BlockMultiblockMachine(TileRailgun.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("railgun").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockSpaceElevatorController = new BlockMultiblockMachine(TileSpaceElevator.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceElevatorController").setHardness(3f);
		AdvancedRocketryBlocks.blockBeacon = new BlockBeacon(TileBeacon.class,  GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setUnlocalizedName("beacon").setHardness(3f);
		
		AdvancedRocketryBlocks.blockIntake = new BlockIntake(Material.IRON).setUnlocalizedName("gasIntake").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPressureTank = new BlockPressurizedFluidTank(Material.IRON).setUnlocalizedName("pressurizedTank").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSolarPanel = new Block(Material.IRON).setUnlocalizedName("solarPanel").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockSolarGenerator = new BlockSolarGenerator(TileSolarPanel.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f).setUnlocalizedName("solarGenerator");
		AdvancedRocketryBlocks.blockDockingPort = new BlockStationModuleDockingPort(Material.IRON).setUnlocalizedName("stationMarker").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPipeSealer = new BlockSeal(Material.IRON).setUnlocalizedName("pipeSeal").setCreativeTab(tabAdvRocketry).setHardness(0.5f);
		AdvancedRocketryBlocks.blockThermiteTorch = new BlockThermiteTorch().setUnlocalizedName("thermiteTorch").setCreativeTab(tabAdvRocketry).setHardness(0.1f).setLightLevel(1f);
		
		AdvancedRocketryBlocks.blockTransciever = new BlockTransciever(TileWirelessTransciever.class, GuiHandler.guiId.MODULAR.ordinal()).setUnlocalizedName("wirelessTransciever").setCreativeTab(tabAdvRocketry).setHardness(3f);
		
		//Configurable stuff
		if(zmaster587.advancedRocketry.api.Configuration.enableGravityController)
			AdvancedRocketryBlocks.blockGravityMachine = new BlockMultiblockMachine(TileGravityController.class,GuiHandler.guiId.MODULARNOINV.ordinal()).setUnlocalizedName("gravityMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);


		if(zmaster587.advancedRocketry.api.Configuration.enableLaserDrill) {
			AdvancedRocketryBlocks.blockSpaceLaser = new BlockLaser().setHardness(2f);
			AdvancedRocketryBlocks.blockSpaceLaser.setCreativeTab(tabAdvRocketry);
		}


		//Fluid Registration
		AdvancedRocketryFluids.fluidOxygen = new FluidColored("oxygen",0xFF8f94b9).setUnlocalizedName("oxygen").setGaseous(false).setLuminosity(10).setDensity(800).setViscosity(1500);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidOxygen))
		{
			AdvancedRocketryFluids.fluidOxygen = FluidRegistry.getFluid("oxygen");
		}

		AdvancedRocketryFluids.fluidHydrogen = new FluidColored("hydrogen",0xFFdbc1c1).setUnlocalizedName("hydrogen").setGaseous(false).setLuminosity(10).setDensity(800).setViscosity(1500);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidHydrogen))
		{
			AdvancedRocketryFluids.fluidHydrogen = FluidRegistry.getFluid("hydrogen");
		}

		AdvancedRocketryFluids.fluidRocketFuel = new FluidColored("rocketFuel", 0xFFe5d884).setUnlocalizedName("rocketFuel").setGaseous(false).setLuminosity(10).setDensity(800).setViscosity(1500);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidRocketFuel))
		{
			AdvancedRocketryFluids.fluidRocketFuel = FluidRegistry.getFluid("rocketFuel");
		}

		AdvancedRocketryFluids.fluidNitrogen = new FluidColored("nitrogen", 0xFF97a7e7).setUnlocalizedName("nitrogen").setGaseous(false).setLuminosity(10).setDensity(800).setViscosity(1500);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidNitrogen))
		{
			AdvancedRocketryFluids.fluidNitrogen = FluidRegistry.getFluid("nitrogen");
		}		

		AtmosphereRegister.getInstance().registerHarvestableFluid(AdvancedRocketryFluids.fluidNitrogen);
		AtmosphereRegister.getInstance().registerHarvestableFluid(AdvancedRocketryFluids.fluidHydrogen);
		AtmosphereRegister.getInstance().registerHarvestableFluid(AdvancedRocketryFluids.fluidOxygen);

		AdvancedRocketryBlocks.blockOxygenFluid = new BlockFluid(AdvancedRocketryFluids.fluidOxygen, Material.WATER).setUnlocalizedName("oxygenFluidBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockHydrogenFluid = new BlockFluid(AdvancedRocketryFluids.fluidHydrogen, Material.WATER).setUnlocalizedName("hydrogenFluidBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockFuelFluid = new BlockFluid(AdvancedRocketryFluids.fluidRocketFuel, new MaterialLiquid(MapColor.YELLOW)).setUnlocalizedName("rocketFuelBlock").setCreativeTab(CreativeTabs.MISC);
		AdvancedRocketryBlocks.blockNitrogenFluid = new BlockFluid(AdvancedRocketryFluids.fluidNitrogen, Material.WATER).setUnlocalizedName("nitrogenFluidBlock").setCreativeTab(CreativeTabs.MISC);

		//Cables
		AdvancedRocketryBlocks.blockFluidPipe = new BlockLiquidPipe(Material.IRON).setUnlocalizedName("liquidPipe").setCreativeTab(tabAdvRocketry).setHardness(1f);;
		AdvancedRocketryBlocks.blockDataPipe = new BlockDataCable(Material.IRON).setUnlocalizedName("dataPipe").setCreativeTab(tabAdvRocketry).setHardness(1f);;
		AdvancedRocketryBlocks.blockEnergyPipe = new BlockEnergyCable(Material.IRON).setUnlocalizedName("energyPipe").setCreativeTab(tabAdvRocketry).setHardness(1f);;

		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDataPipe.setRegistryName("dataPipe"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEnergyPipe.setRegistryName("energyPipe"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFluidPipe.setRegistryName("liquidPipe"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLaunchpad.setRegistryName("launchpad"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRocketBuilder.setRegistryName("rocketBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockStructureTower.setRegistryName("structureTower"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGenericSeat.setRegistryName("seat"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEngine.setRegistryName("rocketmotor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAdvEngine.setRegistryName("advRocketmotor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFuelTank.setRegistryName("fuelTank"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFuelingStation.setRegistryName("fuelingStation"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMonitoringStation.setRegistryName("monitoringStation"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSatelliteBuilder.setRegistryName("satelliteBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMoonTurf.setRegistryName("moonTurf"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockHotTurf.setRegistryName("hotTurf"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLoader.setRegistryName("loader"), ItemBlockMeta.class, false);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPrecisionAssembler.setRegistryName("precisionassemblingmachine"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBlastBrick.setRegistryName("blastBrick"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockQuartzCrucible.setRegistryName("quartzcrucible"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCrystallizer.setRegistryName("crystallizer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCuttingMachine.setRegistryName("cuttingMachine"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAlienWood.setRegistryName("alienWood"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAlienLeaves.setRegistryName("alienLeaves"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAlienSapling.setRegistryName("alienSapling"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockObservatory.setRegistryName("observatory"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockConcrete.setRegistryName("concrete"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlanetSelector.setRegistryName("planetSelector"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSatelliteControlCenter.setRegistryName("satelliteControlCenter"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlanetAnalyser.setRegistryName("planetAnalyser"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGuidanceComputer.setRegistryName("guidanceComputer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockArcFurnace.setRegistryName("arcFurnace"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSawBlade.setRegistryName("sawBlade"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLathe.setRegistryName("lathe"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRollingMachine.setRegistryName("rollingMachine"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlatePress.setRegistryName("platePress"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockStationBuilder.setRegistryName("stationBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockElectrolyser.setRegistryName("electrolyser"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockChemicalReactor.setRegistryName("chemicalReactor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenScrubber.setRegistryName("oxygenScrubber"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenVent.setRegistryName("oxygenVent"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenCharger.setRegistryName("oxygenCharger"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAirLock.setRegistryName("airlock_door"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLandingPad.setRegistryName("landingPad"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockWarpCore.setRegistryName("warpCore"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockWarpShipMonitor.setRegistryName("warpMonitor"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenDetection.setRegistryName("oxygenDetection"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockUnlitTorch.setRegistryName("unlitTorch"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blocksGeode.setRegistryName("geode"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOxygenFluid.setRegistryName("oxygenFluid"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockHydrogenFluid.setRegistryName("hydrogenFluid"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFuelFluid.setRegistryName("rocketFuel"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockNitrogenFluid.setRegistryName("nitrogenFluid"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockVitrifiedSand.setRegistryName("vitrifiedSand"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCharcoalLog.setRegistryName("charcoalLog"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockElectricMushroom.setRegistryName("electricMushroom"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCrystal.setRegistryName("crystal"), ItemBlockCrystal.class, true );
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockOrientationController.setRegistryName("orientationController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGravityController.setRegistryName("gravityController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDrill.setRegistryName("drill"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockMicrowaveReciever.setRegistryName("microwaveReciever"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLightSource.setRegistryName("lightSource"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSolarPanel.setRegistryName("solarPanel"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSuitWorkStation.setRegistryName("suitWorkStation"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBiomeScanner.setRegistryName("biomeScanner"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAtmosphereTerraformer.setRegistryName("terraformer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDeployableRocketBuilder.setRegistryName("deployableRocketBuilder"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPressureTank.setRegistryName("liquidTank"), ItemBlockFluidTank.class, true);
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockIntake.setRegistryName("intake"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockCircleLight.setRegistryName("circleLight"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSolarGenerator.setRegistryName("solarGenerator"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDockingPort.setRegistryName("stationMarker"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAltitudeController.setRegistryName("altitudeController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockRailgun .setRegistryName("railgun"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAstroBed .setRegistryName("astroBed"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPlanetHoloSelector.setRegistryName("planetHoloSelector"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockLens.setRegistryName("blockLens"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockForceField.setRegistryName("forceField"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockForceFieldProjector.setRegistryName("forceFieldProjector"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockPipeSealer.setRegistryName("pipeSealer"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSpaceElevatorController.setRegistryName("spaceElevatorController"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockBeacon.setRegistryName("beacon"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockAlienPlanks.setRegistryName("planks"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockThermiteTorch.setRegistryName("thermiteTorch"));
		LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockTransciever.setRegistryName("wirelessTransciever"));
		
		if(zmaster587.advancedRocketry.api.Configuration.enableGravityController)
			LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockGravityMachine.setRegistryName("gravityMachine"));

		//TODO, use different mechanism to enable/disable drill
		if(zmaster587.advancedRocketry.api.Configuration.enableLaserDrill)
			LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockSpaceLaser.setRegistryName("spaceLaser"));


		//Items -------------------------------------------------------------------------------------
		AdvancedRocketryItems.itemWafer = new ItemIngredient(1).setUnlocalizedName("advancedrocketry:wafer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCircuitPlate = new ItemIngredient(2).setUnlocalizedName("advancedrocketry:circuitplate").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemIC = new ItemIngredient(6).setUnlocalizedName("advancedrocketry:circuitIC").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemMisc = new ItemIngredient(2).setUnlocalizedName("advancedrocketry:miscpart").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSawBlade = new ItemIngredient(1).setUnlocalizedName("advancedrocketry:sawBlade").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStationChip = new ItemStationChip().setUnlocalizedName("stationChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceElevatorChip = new ItemSpaceElevatorChip().setUnlocalizedName("elevatorChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemAsteroidChip = new ItemAsteroidChip().setUnlocalizedName("asteroidChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStation = new ItemPackedStructure().setUnlocalizedName("station");
		AdvancedRocketryItems.itemSmallAirlockDoor = new ItemDoor(AdvancedRocketryBlocks.blockAirLock).setUnlocalizedName("smallAirlock").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCarbonScrubberCartridge = new Item().setMaxDamage(Short.MAX_VALUE).setUnlocalizedName("carbonScrubberCartridge").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemLens = new ItemIngredient(1).setUnlocalizedName("advancedrocketry:lens").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSatellitePowerSource = new ItemIngredient(2).setUnlocalizedName("advancedrocketry:satellitePowerSource").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSatellitePrimaryFunction = new ItemIngredient(6).setUnlocalizedName("advancedrocketry:satellitePrimaryFunction").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemThermite = new Item().setUnlocalizedName("thermite").setCreativeTab(tabAdvRocketry);

		//TODO: move registration in the case we have more than one chip type
		AdvancedRocketryItems.itemDataUnit = new ItemData().setUnlocalizedName("advancedrocketry:dataUnit").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemOreScanner = new ItemOreScanner().setUnlocalizedName("OreScanner").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemQuartzCrucible = new ItemBlock(AdvancedRocketryBlocks.blockQuartzCrucible).setUnlocalizedName("qcrucible").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSatellite = new ItemSatellite().setUnlocalizedName("satellite").setCreativeTab(tabAdvRocketry).setMaxStackSize(1);
		AdvancedRocketryItems.itemSatelliteIdChip = new ItemSatelliteIdentificationChip().setUnlocalizedName("satelliteIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemPlanetIdChip = new ItemPlanetIdentificationChip().setUnlocalizedName("planetIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemBiomeChanger = new ItemBiomeChanger().setUnlocalizedName("biomeChanger").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemBasicLaserGun = new ItemBasicLaserGun().setUnlocalizedName("basicLaserGun").setCreativeTab(tabAdvRocketry);

		//Fluids
		AdvancedRocketryItems.itemBucketRocketFuel = new Item().setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketRocketFuel").setContainerItem(Items.BUCKET);
		AdvancedRocketryItems.itemBucketNitrogen = new Item().setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketNitrogen").setContainerItem(Items.BUCKET);
		AdvancedRocketryItems.itemBucketHydrogen = new Item().setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketHydrogen").setContainerItem(Items.BUCKET);
		AdvancedRocketryItems.itemBucketOxygen = new Item().setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketOxygen").setContainerItem(Items.BUCKET);
		//FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidHydrogen);
		//FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidNitrogen);
		//FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidOxygen);
		//FluidRegistry.addBucketForFluid(AdvancedRocketryFluids.fluidRocketFuel);

		//Suit Component Registration
		AdvancedRocketryItems.itemJetpack = new ItemJetpack().setCreativeTab(tabAdvRocketry).setUnlocalizedName("jetPack");
		AdvancedRocketryItems.itemPressureTank = new ItemPressureTank(4, 1000).setCreativeTab(tabAdvRocketry).setUnlocalizedName("advancedrocketry:pressureTank");
		AdvancedRocketryItems.itemUpgrade = new ItemUpgrade(5).setCreativeTab(tabAdvRocketry).setUnlocalizedName("advancedrocketry:itemUpgrade");
		AdvancedRocketryItems.itemAtmAnalyser = new ItemAtmosphereAnalzer().setCreativeTab(tabAdvRocketry).setUnlocalizedName("atmAnalyser");
		AdvancedRocketryItems.itemBeaconFinder = new ItemBeaconFinder().setCreativeTab(tabAdvRocketry).setUnlocalizedName("beaconFinder");

		//Armor registration
		AdvancedRocketryItems.itemSpaceSuit_Helmet = new ItemSpaceArmor(ArmorMaterial.LEATHER, EntityEquipmentSlot.HEAD,4).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceHelmet");
		AdvancedRocketryItems.itemSpaceSuit_Chest = new ItemSpaceChest(ArmorMaterial.LEATHER, EntityEquipmentSlot.CHEST,6).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceChest");
		AdvancedRocketryItems.itemSpaceSuit_Leggings = new ItemSpaceArmor(ArmorMaterial.LEATHER, EntityEquipmentSlot.LEGS,4).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceLeggings");
		AdvancedRocketryItems.itemSpaceSuit_Boots = new ItemSpaceArmor(ArmorMaterial.LEATHER, EntityEquipmentSlot.FEET,4).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceBoots");
		AdvancedRocketryItems.itemSealDetector = new ItemSealDetector().setMaxStackSize(1).setCreativeTab(tabAdvRocketry).setUnlocalizedName("sealDetector");

		//Tools
		AdvancedRocketryItems.itemJackhammer = new ItemJackHammer(ToolMaterial.DIAMOND).setUnlocalizedName("jackhammer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemJackhammer.setHarvestLevel("jackhammer", 3);
		AdvancedRocketryItems.itemJackhammer.setHarvestLevel("pickaxe", 3);

		//Note: not registered
		AdvancedRocketryItems.itemAstroBed = new ItemAstroBed();

		//Register Satellite Properties
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOptical.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteComposition.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 2), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMassScanner.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 3), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteEnergy.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 4), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOreMapping.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 5), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteBiomeChanger.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0), new SatelliteProperties().setPowerGeneration(1));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,1), new SatelliteProperties().setPowerGeneration(10));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBattery, 1, 0), new SatelliteProperties().setPowerStorage(100));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBattery, 1, 1), new SatelliteProperties().setPowerStorage(400));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), new SatelliteProperties().setMaxData(1000));


		//Item Registration
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemQuartzCrucible.setRegistryName("iquartzcrucible"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemOreScanner.setRegistryName("oreScanner"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatellitePowerSource.setRegistryName("satellitePowerSource"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatellitePrimaryFunction.setRegistryName("satellitePrimaryFunction"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemCircuitPlate.setRegistryName("itemCircuitPlate"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemIC.setRegistryName("ic"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemWafer.setRegistryName("wafer"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemDataUnit.setRegistryName("dataUnit"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatellite.setRegistryName("satellite"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSatelliteIdChip.setRegistryName("satelliteIdChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemPlanetIdChip.setRegistryName("planetIdChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemMisc.setRegistryName("misc"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSawBlade.setRegistryName("sawBladeIron"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceStationChip.setRegistryName("spaceStationChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceStation.setRegistryName("spaceStation"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Helmet.setRegistryName("spaceHelmet"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Boots.setRegistryName("spaceBoots"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Chest.setRegistryName("spaceChestplate"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceSuit_Leggings.setRegistryName("spaceLeggings"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBucketRocketFuel.setRegistryName("bucketRocketFuel"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBucketNitrogen.setRegistryName("bucketNitrogen"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBucketHydrogen.setRegistryName("bucketHydrogen"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBucketOxygen.setRegistryName("bucketOxygen"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSmallAirlockDoor.setRegistryName("smallAirlockDoor"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemCarbonScrubberCartridge.setRegistryName("carbonScrubberCartridge"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSealDetector.setRegistryName("sealDetector"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemJackhammer.setRegistryName("jackHammer"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemAsteroidChip.setRegistryName("asteroidChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemSpaceElevatorChip.setRegistryName("elevatorChip"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemLens.setRegistryName("lens"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemJetpack.setRegistryName("jetPack"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemPressureTank.setRegistryName("pressureTank"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemUpgrade.setRegistryName("itemUpgrade"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemAtmAnalyser.setRegistryName("atmAnalyser"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBasicLaserGun.setRegistryName("basicLaserGun"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBeaconFinder.setRegistryName("beaconFinder"));
		LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemThermite.setRegistryName("thermite"));

		if(zmaster587.advancedRocketry.api.Configuration.enableTerraforming)
			LibVulpesBlocks.registerItem(AdvancedRocketryItems.itemBiomeChanger.setRegistryName("biomeChanger"));

		//End Items

		//Entity Registration ---------------------------------------------------------------------------------------------
		EntityRegistry.registerModEntity(EntityDummy.class, "mountDummy", 0, this, 16, 20, false);
		EntityRegistry.registerModEntity(EntityRocket.class, "rocket", 1, this, 64, 3, true);
		EntityRegistry.registerModEntity(EntityLaserNode.class, "laserNode", 2, instance, 256, 20, false);
		EntityRegistry.registerModEntity(EntityStationDeployedRocket.class, "deployedRocket", 3, this, 256, 600, true);
		EntityRegistry.registerModEntity(EntityItemAbducted.class, "ARAbductedItem", 4, this, 127, 600, false);
		EntityRegistry.registerModEntity(EntityUIPlanet.class, "ARPlanetUIItem", 5, this, 64, 1, false);
		EntityRegistry.registerModEntity(EntityUIButton.class, "ARPlanetUIButton", 6, this, 64, 20, false);
		EntityRegistry.registerModEntity(EntityUIStar.class, "ARStarUIButton", 7, this, 64, 20, false);
		EntityRegistry.registerModEntity(EntityElevatorCapsule.class, "ARSpaceElevatorCapsule", 8, this, 64, 20, true);
		
		//TileEntity Registration ---------------------------------------------------------------------------------------------
		GameRegistry.registerTileEntity(TileRocketBuilder.class, "ARrocketBuilder");
		GameRegistry.registerTileEntity(TileWarpCore.class, "ARwarpCore");
		//GameRegistry.registerTileEntity(TileModelRender.class, "ARmodelRenderer");
		GameRegistry.registerTileEntity(TileEntityFuelingStation.class, "ARfuelingStation");
		GameRegistry.registerTileEntity(TileEntityMoniteringStation.class, "ARmonitoringStation");
		//GameRegistry.registerTileEntity(TileMissionController.class, "ARmissionControlComp");
		GameRegistry.registerTileEntity(TileSpaceLaser.class, "ARspaceLaser");
		GameRegistry.registerTileEntity(TilePrecisionAssembler.class, "ARprecisionAssembler");
		GameRegistry.registerTileEntity(TileObservatory.class, "ARobservatory");
		GameRegistry.registerTileEntity(TileCrystallizer.class, "ARcrystallizer");
		GameRegistry.registerTileEntity(TileCuttingMachine.class, "ARcuttingmachine");
		GameRegistry.registerTileEntity(TileDataBus.class, "ARdataBus");
		GameRegistry.registerTileEntity(TileSatelliteHatch.class, "ARsatelliteHatch");
		GameRegistry.registerTileEntity(TileGuidanceComputerHatch.class, "ARguidanceComputerHatch");
		GameRegistry.registerTileEntity(TileSatelliteBuilder.class, "ARsatelliteBuilder");
		GameRegistry.registerTileEntity(TileEntitySatelliteControlCenter.class, "ARTileEntitySatelliteControlCenter");
		GameRegistry.registerTileEntity(TileAstrobodyDataProcessor.class, "ARplanetAnalyser");
		GameRegistry.registerTileEntity(TileGuidanceComputer.class, "ARguidanceComputer");
		GameRegistry.registerTileEntity(TileElectricArcFurnace.class, "ARelectricArcFurnace");
		GameRegistry.registerTileEntity(TilePlanetSelector.class, "ARTilePlanetSelector");
		//GameRegistry.registerTileEntity(TileModelRenderRotatable.class, "ARTileModelRenderRotatable");
		GameRegistry.registerTileEntity(TileMaterial.class, "ARTileMaterial");
		GameRegistry.registerTileEntity(TileLathe.class, "ARTileLathe");
		GameRegistry.registerTileEntity(TileRollingMachine.class, "ARTileMetalBender");
		GameRegistry.registerTileEntity(TileStationBuilder.class, "ARStationBuilder");
		GameRegistry.registerTileEntity(TileElectrolyser.class, "ARElectrolyser");
		GameRegistry.registerTileEntity(TileChemicalReactor.class, "ARChemicalReactor");
		GameRegistry.registerTileEntity(TileOxygenVent.class, "AROxygenVent");
		GameRegistry.registerTileEntity(TileOxygenCharger.class, "AROxygenCharger");
		GameRegistry.registerTileEntity(TileCO2Scrubber.class, "ARCO2Scrubber");
		GameRegistry.registerTileEntity(TileWarpShipMonitor.class, "ARStationMonitor");
		GameRegistry.registerTileEntity(TileAtmosphereDetector.class, "AROxygenDetector");
		GameRegistry.registerTileEntity(TileStationOrientationControl.class, "AROrientationControl");
		GameRegistry.registerTileEntity(TileStationGravityController.class, "ARGravityControl");
		GameRegistry.registerTileEntity(TileLiquidPipe.class, "ARLiquidPipe");
		GameRegistry.registerTileEntity(TileDataPipe.class, "ARDataPipe");
		GameRegistry.registerTileEntity(TileEnergyPipe.class, "AREnergyPipe");
		GameRegistry.registerTileEntity(TileDrill.class, "ARDrill");
		GameRegistry.registerTileEntity(TileMicrowaveReciever.class, "ARMicrowaveReciever");
		GameRegistry.registerTileEntity(TileSuitWorkStation.class, "ARSuitWorkStation");
		GameRegistry.registerTileEntity(TileRocketLoader.class, "ARRocketLoader");
		GameRegistry.registerTileEntity(TileRocketUnloader.class, "ARRocketUnloader");
		GameRegistry.registerTileEntity(TileBiomeScanner.class, "ARBiomeScanner");
		GameRegistry.registerTileEntity(TileAtmosphereTerraformer.class, "ARAttTerraformer");
		GameRegistry.registerTileEntity(TileLandingPad.class, "ARLandingPad");
		GameRegistry.registerTileEntity(TileStationDeployedAssembler.class, "ARStationDeployableRocketAssembler");
		GameRegistry.registerTileEntity(TileFluidTank.class, "ARFluidTank");
		GameRegistry.registerTileEntity(TileRocketFluidUnloader.class, "ARFluidUnloader");
		GameRegistry.registerTileEntity(TileRocketFluidLoader.class, "ARFluidLoader");
		GameRegistry.registerTileEntity(TileSolarPanel.class, "ARSolarGenerator");
		GameRegistry.registerTileEntity(TileDockingPort.class, "ARDockingPort");
		GameRegistry.registerTileEntity(TileStationAltitudeController.class, "ARStationAltitudeController");
		GameRegistry.registerTileEntity(TileRailgun.class, "ARRailgun");
		GameRegistry.registerTileEntity(TilePlanetaryHologram.class, "ARplanetHoloSelector");
		GameRegistry.registerTileEntity(TileForceFieldProjector.class, "ARForceFieldProjector");
		GameRegistry.registerTileEntity(TileSeal.class, "ARBlockSeal");
		GameRegistry.registerTileEntity(TileSpaceElevator.class, "ARSpaceElevator");
		GameRegistry.registerTileEntity(TileBeacon.class, "ARBeacon");
		GameRegistry.registerTileEntity(TileWirelessTransciever.class, "ARTransciever");

		if(zmaster587.advancedRocketry.api.Configuration.enableGravityController)
			GameRegistry.registerTileEntity(TileGravityController.class, "ARGravityMachine");


		//Register machine recipes
		LibVulpes.registerRecipeHandler(TileCuttingMachine.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/CuttingMachine.xml");
		LibVulpes.registerRecipeHandler(TilePrecisionAssembler.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/PrecisionAssembler.xml");
		LibVulpes.registerRecipeHandler(TileChemicalReactor.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/ChemicalReactor.xml");
		LibVulpes.registerRecipeHandler(TileCrystallizer.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/Crystallizer.xml");
		LibVulpes.registerRecipeHandler(TileElectrolyser.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/Electrolyser.xml");
		LibVulpes.registerRecipeHandler(TileElectricArcFurnace.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/ElectricArcFurnace.xml");
		LibVulpes.registerRecipeHandler(TileLathe.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/Lathe.xml");
		LibVulpes.registerRecipeHandler(TileRollingMachine.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/RollingMachine.xml");
		LibVulpes.registerRecipeHandler(BlockPress.class, event.getModConfigurationDirectory().getAbsolutePath() + "/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/SmallPlatePress.xml");

		//Enchantments
		AdvancedRocketryAPI.enchantmentSpaceProtection = new EnchantmentSpaceBreathing();
		Enchantment.REGISTRY.register(spaceBreathingId, new ResourceLocation("advancedrocketry:spacebreathing"), AdvancedRocketryAPI.enchantmentSpaceProtection);
		
		//AUDIO

		//MOD-SPECIFIC ENTRIES --------------------------------------------------------------------------------------------------------------------------


		//Register Space Objects
		SpaceObjectManager.getSpaceManager().registerSpaceObjectType("genericObject", SpaceObject.class);

		//Register Allowed Products
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("TitaniumAluminide", "pickaxe", 1, 0xaec2de, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));
		materialRegistry.registerMaterial(new zmaster587.libVulpes.api.material.Material("TitaniumIridium", "pickaxe", 1, 0xd7dfe4, AllowedProducts.getProductByName("PLATE").getFlagValue() | AllowedProducts.getProductByName("INGOT").getFlagValue() | AllowedProducts.getProductByName("NUGGET").getFlagValue() | AllowedProducts.getProductByName("DUST").getFlagValue() | AllowedProducts.getProductByName("STICK").getFlagValue() | AllowedProducts.getProductByName("BLOCK").getFlagValue() | AllowedProducts.getProductByName("GEAR").getFlagValue() | AllowedProducts.getProductByName("SHEET").getFlagValue(), false));

		materialRegistry.registerOres(LibVulpes.tabLibVulpesOres);

		//OreDict stuff
		OreDictionary.registerOre("waferSilicon", new ItemStack(AdvancedRocketryItems.itemWafer,1,0));
		OreDictionary.registerOre("ingotCarbon", new ItemStack(AdvancedRocketryItems.itemMisc, 1, 1));
		OreDictionary.registerOre("concrete", new ItemStack(AdvancedRocketryBlocks.blockConcrete));
		OreDictionary.registerOre("itemLens", AdvancedRocketryItems.itemLens);
		OreDictionary.registerOre("dustThermite", new ItemStack(AdvancedRocketryItems.itemThermite));
		OreDictionary.registerOre("turfMoon", new ItemStack(AdvancedRocketryBlocks.blockMoonTurf));




		OreDictionary.registerOre("itemSilicon", MaterialRegistry.getItemStackFromMaterialAndType("Silicon", AllowedProducts.getProductByName("INGOT")));

		//Regiser item/block crap
		proxy.preinit();
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{

		//TODO: move to proxy
		//Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((IBlockColor) AdvancedRocketryBlocks.blockFuelFluid, new Block[] {AdvancedRocketryBlocks.blockFuelFluid});

		proxy.init();

		zmaster587.advancedRocketry.cable.NetworkRegistry.registerFluidNetwork();
		ItemStack userInterface = new ItemStack(AdvancedRocketryItems.itemMisc, 1,0);
		ItemStack basicCircuit = new ItemStack(AdvancedRocketryItems.itemIC, 1,0);
		ItemStack advancedCircuit = new ItemStack(AdvancedRocketryItems.itemIC, 1,2);
		ItemStack controlCircuitBoard =  new ItemStack(AdvancedRocketryItems.itemIC,1,3);
		ItemStack itemIOBoard = new ItemStack(AdvancedRocketryItems.itemIC,1,4);
		ItemStack liquidIOBoard = new ItemStack(AdvancedRocketryItems.itemIC,1,5);
		ItemStack trackingCircuit = new ItemStack(AdvancedRocketryItems.itemIC,1,1);
		ItemStack opticalSensor = new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0);
		ItemStack massDetector = new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 2);
		ItemStack biomeChanger = new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 5);
		ItemStack smallSolarPanel =  new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0); 
		ItemStack largeSolarPanel = new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,1);
		ItemStack smallBattery = new ItemStack(LibVulpesItems.itemBattery,1,0);
		ItemStack battery2x = new ItemStack(LibVulpesItems.itemBattery,1,1);
		ItemStack superHighPressureTime = new ItemStack(AdvancedRocketryItems.itemPressureTank,1,3);
		ItemStack charcoal = new ItemStack(Items.COAL,1,1);

		//Register Alloys
		MaterialRegistry.registerMixedMaterial(new MixedMaterial(TileElectricArcFurnace.class, "oreRutile", new ItemStack[] {MaterialRegistry.getMaterialFromName("Titanium").getProduct(AllowedProducts.getProductByName("INGOT"))}));


		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockBlastBrick,16), new ItemStack(Items.MAGMA_CREAM,1), new ItemStack(Items.MAGMA_CREAM,1), Blocks.BRICK_BLOCK, Blocks.BRICK_BLOCK, Blocks.BRICK_BLOCK, Blocks.BRICK_BLOCK);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockArcFurnace), "aga","ice", "aba", 'a', Items.NETHERBRICK, 'g', userInterface, 'i', itemIOBoard, 'e',controlCircuitBoard, 'c', AdvancedRocketryBlocks.blockBlastBrick, 'b', "ingotCopper"));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemQuartzCrucible), " a ", "aba", " a ", Character.valueOf('a'), Items.QUARTZ, Character.valueOf('b'), Items.CAULDRON);
		GameRegistry.addRecipe(new ShapedOreRecipe(MaterialRegistry.getItemStackFromMaterialAndType("Iron", AllowedProducts.getProductByName("STICK"), 4), "x  ", " x ", "  x", 'x', "ingotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockPlatePress, "   ", " a ", "iii", 'a', Blocks.PISTON, 'i', "ingotIron"));
		GameRegistry.addSmelting(MaterialRegistry.getMaterialFromName("Dilithium").getProduct(AllowedProducts.getProductByName("ORE")), MaterialRegistry.getMaterialFromName("Dilithium").getProduct(AllowedProducts.getProductByName("DUST")), 0);

		//Supporting Materials
		GameRegistry.addRecipe(new ShapedOreRecipe(userInterface, "lrl", "fgf", 'l', "dyeLime", 'r', "dustRedstone", 'g', Blocks.GLASS_PANE, 'f', Items.GLOWSTONE_DUST));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockGenericSeat), "xxx", 'x', Blocks.WOOL);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockConcrete, 16), Blocks.SAND, Blocks.GRAVEL, Items.WATER_BUCKET);
		GameRegistry.addRecipe(new ShapelessOreRecipe(AdvancedRocketryBlocks.blockLaunchpad, "concrete", "dyeBlack", "dyeYellow"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockStructureTower, 8), "ooo", " o ", "ooo", 'o', "stickSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockEngine, "sss", " t ","t t", 's', "ingotSteel", 't', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockAdvEngine, "sss", " t ","t t", 's', "ingotTitaniumAluminide", 't', "plateTitaniumIridium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockFuelTank, "s s", "p p", "s s", 'p', "plateSteel", 's', "stickSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LibVulpesItems.itemBattery,4,0), " c ","prp", "prp", 'c', "stickIron", 'r', "dustRedstone", 'p', "plateTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LibVulpesItems.itemBattery,1,1), "bpb", "bpb", 'b', smallBattery, 'p', "plateCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), "ppp", " g ", " l ", 'p', Blocks.GLASS_PANE, 'g', Items.GLOWSTONE_DUST, 'l', "plateGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockObservatory), "gug", "pbp", "rrr", 'g', "paneGlass", 'u', userInterface, 'b', LibVulpesBlocks.blockStructureBlock, 'r', "stickIron"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(AdvancedRocketryItems.itemThermite, 3), "dustAluminum", "dustIron","dustIron"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockThermiteTorch, 4), new ItemStack(Items.STICK), "dustThermite"));
        
		//Hatches
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,0), "m", "c"," ", 'c', AdvancedRocketryItems.itemDataUnit, 'm', LibVulpesBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,1), " x ", "xmx"," x ", 'x', "stickTitanium", 'm', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,2), new ItemStack(LibVulpesBlocks.blockHatch,1,1), trackingCircuit);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,3), new ItemStack(LibVulpesBlocks.blockHatch,1,0), trackingCircuit);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,4), new ItemStack(LibVulpesBlocks.blockHatch,1,3), trackingCircuit);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,5), new ItemStack(LibVulpesBlocks.blockHatch,1,2), trackingCircuit);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,6), " z ", "xmx"," z ", 'z' , controlCircuitBoard, 'x', "stickCopper", 'm', LibVulpesBlocks.blockStructureBlock));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0), "rrr", "ggg","ppp", 'r', "dustRedstone", 'g', Items.GLOWSTONE_DUST, 'p', "plateGold"));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LibVulpesItems.itemHoloProjector), "oro", "rpr", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'r', "dustRedstone", 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(massDetector, "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', basicCircuit, 'd', "crystalDilithium"));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 1), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', basicCircuit, 'd', trackingCircuit);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 3), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemLens, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', basicCircuit, 'd', trackingCircuit);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0), " x ","xox", " x ", 'x', "plateIron", 'o', "stickIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSawBlade,1,0), "r r","xox", "x x", 'r', "stickIron", 'x', "plateIron", 'o', new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0)));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSpaceStationChip), LibVulpesItems.itemLinker , basicCircuit);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge), "xix", "xix", "xix", 'x', "sheetIron", 'i', Blocks.IRON_BARS));

		//O2 Support
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenVent), "bfb", "bmb", "btb", 'b', Blocks.IRON_BARS, 'f', "fanSteel", 'm', LibVulpesBlocks.blockMotor, 't', AdvancedRocketryBlocks.blockFuelTank));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenScrubber), "bfb", "bmb", "btb", 'b', Blocks.IRON_BARS, 'f', "fanSteel", 'm', LibVulpesBlocks.blockMotor, 't', "ingotCarbon"));

		//Knicknacks
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockForceFieldProjector), " c ", "pdp","psp", 'c', "coilCopper", 'p', "plateAluminum", 'd', "crystalDilithium", 's', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockPipeSealer, " c ", "csc", " c ", 'c', Items.CLAY_BALL, 's', "stickIron"));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockAlienPlanks, 4), AdvancedRocketryBlocks.blockAlienWood);
		
		if(zmaster587.advancedRocketry.api.Configuration.enableGravityController)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockGravityMachine), "sds", "sws", 's', "sheetTitanium", 'd', massDetector, 'w', AdvancedRocketryBlocks.blockWarpCore));

		//MACHINES
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockPrecisionAssembler), "abc", "def", "ghi", 'a', Items.REPEATER, 'b', userInterface, 'c', "gemDiamond", 'd', itemIOBoard, 'e', LibVulpesBlocks.blockStructureBlock, 'f', controlCircuitBoard, 'g', Blocks.FURNACE, 'h', "gearSteel", 'i', Blocks.DROPPER));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockCrystallizer), "ada", "ecf","bgb", 'a', Items.QUARTZ, 'b', Items.REPEATER, 'c', LibVulpesBlocks.blockStructureBlock, 'd', userInterface, 'e', itemIOBoard, 'f', controlCircuitBoard, 'g', "plateSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockCuttingMachine), "aba", "cde", "opo", 'a', "gearSteel", 'b', userInterface, 'c', itemIOBoard, 'e', controlCircuitBoard, 'p', "plateSteel", 'o', Blocks.OBSIDIAN, 'd', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockLathe), "rsr", "abc", "pgp", 'r', "stickIron",'a', itemIOBoard, 'c', controlCircuitBoard, 'g', "gearSteel", 'p', "plateSteel", 'b', LibVulpesBlocks.blockStructureBlock, 's', userInterface));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockRollingMachine), "psp", "abc", "iti", 'a', itemIOBoard, 'c', controlCircuitBoard, 'p', "gearSteel", 's', userInterface, 'b', LibVulpesBlocks.blockStructureBlock, 'i', "blockIron",'t', liquidIOBoard));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockMonitoringStation), "coc", "cbc", "cpc", 'c', "stickCopper", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'b', LibVulpesBlocks.blockStructureBlock, 'p', LibVulpesItems.itemBattery));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockFuelingStation), "bgb", "lbf", "ppp", 'p', "plateTin", 'f', "fanSteel", 'l', liquidIOBoard, 'g', AdvancedRocketryItems.itemMisc, 'x', AdvancedRocketryBlocks.blockFuelTank, 'b', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSatelliteControlCenter), "oso", "cbc", "rtr", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 's', userInterface, 'c', "stickCopper", 'b', LibVulpesBlocks.blockStructureBlock, 'r', Items.REPEATER, 't', LibVulpesItems.itemBattery));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSatelliteBuilder), "dht", "cbc", "mas", 'd', AdvancedRocketryItems.itemDataUnit, 'h', Blocks.HOPPER, 'c', basicCircuit, 'b', LibVulpesBlocks.blockStructureBlock, 'm', LibVulpesBlocks.blockMotor, 'a', Blocks.ANVIL, 's', AdvancedRocketryBlocks.blockSawBlade, 't', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockPlanetAnalyser), "tst", "pbp", "cpc", 't', trackingCircuit, 's', userInterface, 'b', LibVulpesBlocks.blockStructureBlock, 'p', "plateTin", 'c', AdvancedRocketryItems.itemPlanetIdChip));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockGuidanceComputer), "ctc", "rbr", "crc", 'c', trackingCircuit, 't', "plateTitanium", 'r', "dustRedstone", 'b', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockPlanetSelector), "cpc", "lbl", "coc", 'c', trackingCircuit, 'o',new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'l', Blocks.LEVER, 'b', AdvancedRocketryBlocks.blockGuidanceComputer, 'p', Blocks.STONE_BUTTON));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockRocketBuilder), "sgs", "cbc", "tdt", 's', "stickTitanium", 'g', AdvancedRocketryItems.itemMisc, 'c', controlCircuitBoard, 'b', LibVulpesBlocks.blockStructureBlock, 't', "gearTitanium", 'd', AdvancedRocketryBlocks.blockConcrete));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockStationBuilder), "gdg", "dsd", "ada", 'g', "gearTitanium", 'a', advancedCircuit, 'd', "dustDilithium", 's', new ItemStack(AdvancedRocketryBlocks.blockRocketBuilder)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockElectrolyser), "pip", "abc", "ded", 'd', basicCircuit, 'p', "plateSteel", 'i', userInterface, 'a', liquidIOBoard, 'c', controlCircuitBoard, 'b', LibVulpesBlocks.blockStructureBlock, 'e', Blocks.REDSTONE_TORCH));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenCharger), "fif", "tbt", "pcp", 'p', "plateSteel", 'f', "fanSteel", 'c', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 'i', AdvancedRocketryItems.itemMisc, 'b', LibVulpesBlocks.blockStructureBlock, 't', AdvancedRocketryBlocks.blockFuelTank));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockChemicalReactor), "pip", "abd", "rcr", 'a', itemIOBoard, 'd', controlCircuitBoard, 'r', basicCircuit, 'p', "plateGold", 'i', userInterface, 'c', liquidIOBoard, 'b', LibVulpesBlocks.blockStructureBlock, 'g', "plateGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockWarpCore), "gcg", "pbp", "gcg", 'p', "plateSteel", 'c', advancedCircuit, 'b', "coilCopper", 'g', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenDetection), "pip", "gbf", "pcp", 'p', "plateSteel",'f', "fanSteel", 'i', userInterface, 'c', basicCircuit, 'b', LibVulpesBlocks.blockStructureBlock, 'g', Blocks.IRON_BARS));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockWarpShipMonitor), "pip", "obo", "pcp", 'o', controlCircuitBoard, 'p', "plateSteel", 'i', userInterface, 'c', advancedCircuit, 'b', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockBiomeScanner), "plp", "bsb","ppp", 'p', "plateTin", 'l', biomeChanger, 'b', smallBattery, 's', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockDeployableRocketBuilder), "gdg", "dad", "rdr", 'g', "gearTitaniumAluminide", 'd', "dustDilithium", 'r', "stickTitaniumAluminide", 'a', AdvancedRocketryBlocks.blockRocketBuilder));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockPressureTank), "tgt","tgt","tgt", 't', superHighPressureTime, 'g', Blocks.GLASS_PANE));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockIntake), "rhr", "hbh", "rhr", 'r', "stickTitanium", 'h', Blocks.HOPPER, 'b', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockRailgun), " t ", "abc", "ded", 't', trackingCircuit, 'a', controlCircuitBoard, 'b', LibVulpesBlocks.blockAdvStructureBlock, 'c', itemIOBoard, 'd', "fanSteel", 'e', "coilCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSpaceElevatorController), " d ", "aba", "ccc", 'd', controlCircuitBoard, 'a', advancedCircuit, 'b', LibVulpesBlocks.blockAdvStructureBlock, 'c', "coilAluminum"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockBeacon), " c ", "dbt", "scs", 'c', "coilCopper", 'd', controlCircuitBoard, 't', trackingCircuit, 'b', LibVulpesBlocks.blockStructureBlock, 's', "sheetIron" ));

		//Armor recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Boots, " r ", "w w", "p p", 'r', "stickIron", 'w', Blocks.WOOL, 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Leggings, "wrw", "w w", "w w", 'w', Blocks.WOOL, 'r', "stickIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Chest, "wrw", "wtw", "wfw", 'w', Blocks.WOOL, 'r', "stickIron", 't', AdvancedRocketryBlocks.blockFuelTank, 'f', "fanSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Helmet, "prp", "rgr", "www", 'w', Blocks.WOOL, 'r', "stickIron", 'p', "plateIron", 'g', Blocks.GLASS_PANE));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemJetpack, "cpc", "lsl", "f f", 'c', AdvancedRocketryItems.itemPressureTank, 'f', Items.FIRE_CHARGE, 's', Items.STRING, 'l', Blocks.LEVER, 'p', "plateSteel"));

		//Tool Recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemJackhammer, " pt","imp","di ",'d', "gemDiamond", 'm', LibVulpesBlocks.blockMotor, 'p', "plateAluminum", 't', "stickTitanium", 'i', "stickIron"));

		//Other blocks
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSmallAirlockDoor, "pp", "pp","pp", 'p', "plateSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockCircleLight), "p  ", " l ", "   ", 'p', "sheetIron", 'l', Blocks.GLOWSTONE));

		//TEMP RECIPES
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemPlanetIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0), new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemMisc,1,1), charcoal, charcoal, charcoal, charcoal ,charcoal ,charcoal);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockLandingPad), new ItemStack(AdvancedRocketryBlocks.blockConcrete), trackingCircuit);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemAsteroidChip), trackingCircuit.copy(), AdvancedRocketryItems.itemDataUnit);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockTransciever, 8), "ggg", " d ", "ggg", 'g', Blocks.GLASS_PANE, 'd', AdvancedRocketryItems.itemDataUnit);
		//GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockEnergyPipe, 32), "ggg", " d ", "ggg", 'g', Items.CLAY_BALL, 'd', "stickCopper"));
		//GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockFluidPipe, 32), "ggg", " d ", "ggg", 'g', Items.CLAY_BALL, 'd', "sheetCopper"));

		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockDrill), LibVulpesBlocks.blockStructureBlock, Items.IRON_PICKAXE);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockOrientationController), LibVulpesBlocks.blockStructureBlock, Items.COMPASS, userInterface);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockGravityController), LibVulpesBlocks.blockStructureBlock, Blocks.PISTON, Blocks.REDSTONE_BLOCK);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockAltitudeController), LibVulpesBlocks.blockStructureBlock, userInterface, basicCircuit);

		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemLens), " g ", "g g", 'g', Blocks.GLASS_PANE);
		GameRegistry.addShapelessRecipe(largeSolarPanel.copy(), smallSolarPanel, smallSolarPanel, smallSolarPanel, smallSolarPanel, smallSolarPanel, smallSolarPanel);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockMicrowaveReciever), "ggg", "tbc", "aoa", 'g', "plateGold", 't', trackingCircuit, 'b', LibVulpesBlocks.blockStructureBlock, 'c', controlCircuitBoard, 'a', advancedCircuit, 'o', opticalSensor));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockSolarPanel, "rrr", "gbg", "ppp", 'r' , "dustRedstone", 'g', Items.GLOWSTONE_DUST, 'b', LibVulpesBlocks.blockStructureBlock, 'p', "plateGold"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(AdvancedRocketryBlocks.blockSolarGenerator, "itemBattery", LibVulpesBlocks.blockForgeOutputPlug, AdvancedRocketryBlocks.blockSolarPanel));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockDockingPort), trackingCircuit, new ItemStack(AdvancedRocketryBlocks.blockLoader, 1,1));

		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemOreScanner, "lwl", "bgb", "   ", 'l', Blocks.LEVER, 'g', userInterface, 'b', "itemBattery", 'w', advancedCircuit));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 4), " c ","sss", "tot", 'c', "stickCopper", 's', "sheetIron", 'o', AdvancedRocketryItems.itemOreScanner, 't', trackingCircuit));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockSuitWorkStation), "c","b", 'c', Blocks.CRAFTING_TABLE, 'b', LibVulpesBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellite), "sss", "rcr", "sss", 's', "sheetAluminum", 'r', "stickTitanium", 'c', controlCircuitBoard));
		
		
		if(zmaster587.advancedRocketry.api.Configuration.enableLaserDrill) {
			GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockSpaceLaser, "ata", "bec", "gpg", 'a', advancedCircuit, 't', trackingCircuit, 'b', LibVulpesItems.itemBattery, 'e', Items.EMERALD, 'c', controlCircuitBoard, 'g', "gearTitanium", 'p', LibVulpesBlocks.blockStructureBlock));
		}
		if(zmaster587.advancedRocketry.api.Configuration.allowTerraforming) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockAtmosphereTerraformer), "gdg", "lac", "gbg", 'g', "gearTitaniumAluminide", 'd', "crystalDilithium", 'l', liquidIOBoard, 'a', LibVulpesBlocks.blockAdvStructureBlock, 'c', controlCircuitBoard, 'b', battery2x));
		}

		//Control boards
		GameRegistry.addRecipe(new ShapedOreRecipe(itemIOBoard, "rvr", "dwd", "dpd", 'r', "dustRedstone", 'v', "gemDiamond", 'd', "dustGold", 'w', "slabWood", 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(controlCircuitBoard, "rvr", "dwd", "dpd", 'r', "dustRedstone", 'v', "gemDiamond", 'd', "dustCopper", 'w', "slabWood", 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(liquidIOBoard, "rvr", "dwd", "dpd", 'r', "dustRedstone", 'v', "gemDiamond", 'd', new ItemStack(Items.DYE, 1, 4), 'w', "slabWood", 'p', "plateIron"));

		//Register machines
		machineRecipes.registerMachine(TileElectrolyser.class);
		machineRecipes.registerMachine(TileCuttingMachine.class);
		machineRecipes.registerMachine(TileLathe.class);
		machineRecipes.registerMachine(TilePrecisionAssembler.class);
		machineRecipes.registerMachine(TileElectricArcFurnace.class);
		machineRecipes.registerMachine(TileChemicalReactor.class);
		machineRecipes.registerMachine(TileRollingMachine.class);
		machineRecipes.registerMachine(TileCrystallizer.class);

		//Register the machine recipes
		machineRecipes.registerAllMachineRecipes();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new zmaster587.advancedRocketry.inventory.GuiHandler());
		planetWorldType = new WorldTypePlanetGen("PlanetCold");
		spaceWorldType = new WorldTypeSpace("Space");

		//Biomes --------------------------------------------------------------------------------------

		AdvancedRocketryBiomes.moonBiome = new BiomeGenMoon(config.get(BIOMECATETORY, "moonBiomeId", 110).getInt(), true);
		AdvancedRocketryBiomes.alienForest = new BiomeGenAlienForest(config.get(BIOMECATETORY, "alienForestBiomeId", 111).getInt(), true);
		AdvancedRocketryBiomes.hotDryBiome = new BiomeGenHotDryRock(config.get(BIOMECATETORY, "hotDryBiome", 112).getInt(), true);
		AdvancedRocketryBiomes.spaceBiome = new BiomeGenSpace(config.get(BIOMECATETORY, "spaceBiomeId", 113).getInt(), true);
		AdvancedRocketryBiomes.stormLandsBiome = new BiomeGenStormland(config.get(BIOMECATETORY, "stormLandsBiomeId", 114).getInt(), true);
		AdvancedRocketryBiomes.crystalChasms = new BiomeGenCrystal(config.get(BIOMECATETORY, "crystalChasmsBiomeId", 115).getInt(), true);
		AdvancedRocketryBiomes.swampDeepBiome = new BiomeGenDeepSwamp(config.get(BIOMECATETORY, "deepSwampBiomeId", 116).getInt(), true);
		AdvancedRocketryBiomes.marsh = new BiomeGenMarsh(config.get(BIOMECATETORY, "marsh", 117).getInt(), true);
		AdvancedRocketryBiomes.oceanSpires = new BiomeGenOceanSpires(config.get(BIOMECATETORY, "oceanSpires", 118).getInt(), true);

		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.alienForest);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.hotDryBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.spaceBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.stormLandsBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.crystalChasms);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.swampDeepBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.marsh);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.oceanSpires);

		String[] biomeBlackList = config.getStringList("BlacklistedBiomes", "Planet", new String[] {"7", "8", "9", "127", String.valueOf(Biome.getIdForBiome(AdvancedRocketryBiomes.alienForest))}, "List of Biomes to be blacklisted from spawning as BiomeIds, default is: river, sky, hell, void, alienForest");
		String[] biomeHighPressure = config.getStringList("HighPressureBiomes", "Planet", new String[] { String.valueOf(Biome.getIdForBiome(AdvancedRocketryBiomes.swampDeepBiome)), String.valueOf(Biome.getIdForBiome(AdvancedRocketryBiomes.stormLandsBiome)) }, "Biomes that only spawn on worlds with pressures over 125, will override blacklist.  Defaults: StormLands, DeepSwamp");
		String[] biomeSingle = config.getStringList("SingleBiomes", "Planet", new String[] { String.valueOf(Biome.getIdForBiome(AdvancedRocketryBiomes.swampDeepBiome)), String.valueOf(Biome.getIdForBiome(AdvancedRocketryBiomes.crystalChasms)),  String.valueOf(Biome.getIdForBiome(AdvancedRocketryBiomes.alienForest)), String.valueOf(Biome.getIdForBiome(Biomes.DESERT_HILLS)), 
				String.valueOf(Biome.getIdForBiome(Biomes.MUSHROOM_ISLAND)), String.valueOf(Biome.getIdForBiome(Biomes.EXTREME_HILLS)), String.valueOf(Biome.getIdForBiome(Biomes.ICE_PLAINS)) }, "Some worlds have a chance of spawning single biomes contained in this list.  Defaults: deepSwamp, crystalChasms, alienForest, desert hills, mushroom island, extreme hills, ice plains");

		config.save();

		//Prevent these biomes from spawning normally
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.hotDryBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.spaceBiome);

		//Read BlackList from config and register Blacklisted biomes
		for(String string : biomeBlackList) {
			try {
				int id = Integer.parseInt(string);
				Biome biome = Biome.getBiome(id, null);

				if(biome == null)
					logger.warn(String.format("Error blackListing biome id \"%d\", a biome with that ID does not exist!", id));
				else
					AdvancedRocketryBiomes.instance.registerBlackListBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error blackListing \"" + string + "\".  It is not a valid number");
			}
		}

		if(zmaster587.advancedRocketry.api.Configuration.blackListAllVanillaBiomes) {
			AdvancedRocketryBiomes.instance.blackListVanillaBiomes();
		}


		//Read and Register High Pressure biomes from config
		for(String string : biomeHighPressure) {
			try {
				int id = Integer.parseInt(string);
				Biome biome = Biome.getBiome(id, null);

				if(biome == null)
					logger.warn(String.format("Error registering high pressure biome id \"%d\", a biome with that ID does not exist!", id));
				else
					AdvancedRocketryBiomes.instance.registerHighPressureBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error registering high pressure biome \"" + string + "\".  It is not a valid number");
			}
		}

		//Read and Register Single biomes from config
		for(String string : biomeSingle) {
			try {
				int id = Integer.parseInt(string);
				Biome biome = Biome.getBiome(id, null);

				if(biome == null)
					logger.warn(String.format("Error registering single biome id \"%d\", a biome with that ID does not exist!", id));
				else
					AdvancedRocketryBiomes.instance.registerSingleBiome(biome);
			} catch (NumberFormatException e) {
				logger.warn("Error registering single biome \"" + string + "\".  It is not a valid number");
			}
		}


		//Data mapping 'D'

		List<BlockMeta> list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockLoader, 0));
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockLoader, 8));
		TileMultiBlock.addMapping('D', list);
		
		machineRecipes.createAutoGennedRecipes(modProducts);
	}



	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{

		CapabilitySpaceArmor.register();
		//Need to raise the Max Entity Radius to allow player interaction with rockets
		World.MAX_ENTITY_RADIUS = 20;

		//Register multiblock items with the projector
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCuttingMachine(), (BlockTile)AdvancedRocketryBlocks.blockCuttingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileLathe(), (BlockTile)AdvancedRocketryBlocks.blockLathe);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCrystallizer(), (BlockTile)AdvancedRocketryBlocks.blockCrystallizer);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TilePrecisionAssembler(), (BlockTile)AdvancedRocketryBlocks.blockPrecisionAssembler);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileObservatory(), (BlockTile)AdvancedRocketryBlocks.blockObservatory);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAstrobodyDataProcessor(), (BlockTile)AdvancedRocketryBlocks.blockPlanetAnalyser);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileRollingMachine(), (BlockTile)AdvancedRocketryBlocks.blockRollingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectricArcFurnace(), (BlockTile)AdvancedRocketryBlocks.blockArcFurnace);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectrolyser(), (BlockTile)AdvancedRocketryBlocks.blockElectrolyser);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileChemicalReactor(), (BlockTile)AdvancedRocketryBlocks.blockChemicalReactor);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileWarpCore(), (BlockTile)AdvancedRocketryBlocks.blockWarpCore);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileMicrowaveReciever(), (BlockTile)AdvancedRocketryBlocks.blockMicrowaveReciever);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBiomeScanner(), (BlockTile)AdvancedRocketryBlocks.blockBiomeScanner);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileAtmosphereTerraformer(), (BlockTile)AdvancedRocketryBlocks.blockAtmosphereTerraformer);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileRailgun(), (BlockTile)AdvancedRocketryBlocks.blockRailgun);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSpaceElevator(), (BlockTile)AdvancedRocketryBlocks.blockSpaceElevatorController);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileBeacon(), (BlockTile)AdvancedRocketryBlocks.blockBeacon);

		if(zmaster587.advancedRocketry.api.Configuration.enableGravityController)
			((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileGravityController(), (BlockTile)AdvancedRocketryBlocks.blockGravityMachine);

		if(zmaster587.advancedRocketry.api.Configuration.enableLaserDrill)
			((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileSpaceLaser(), (BlockTile)AdvancedRocketryBlocks.blockSpaceLaser);

		proxy.registerEventHandlers();
		proxy.registerKeyBindings();

		ARAchivements.register();
		//TODO: debug
		//ClientCommandHandler.instance.registerCommand(new Debugger());

		PlanetEventHandler handle = new PlanetEventHandler();
		MinecraftForge.EVENT_BUS.register(handle);
		MinecraftForge.ORE_GEN_BUS.register(handle);
		MinecraftForge.EVENT_BUS.register(new BucketHandler());

		CableTickHandler cable = new CableTickHandler();
		MinecraftForge.EVENT_BUS.register(cable);

		InputSyncHandler inputSync = new InputSyncHandler();
		MinecraftForge.EVENT_BUS.register(inputSync);

		MinecraftForge.EVENT_BUS.register(new MapGenLander());
		AdvancedRocketryAPI.gravityManager = new GravityHandler();

		/*if(Loader.isModLoaded("GalacticraftCore") && zmaster587.advancedRocketry.api.Configuration.overrideGCAir) {
			GalacticCraftHandler eventHandler = new GalacticCraftHandler();
			MinecraftForge.EVENT_BUS.register(eventHandler);
			if(event.getSide().isClient())
				FMLCommonHandler.instance().bus().register(eventHandler);
		}*/

		MinecraftForge.EVENT_BUS.register(SpaceObjectManager.getSpaceManager());

		PacketHandler.init();

		GameRegistry.registerWorldGenerator(new OreGenerator(), 100);

		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());

		

		//Register buckets
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockFuelFluid, AdvancedRocketryItems.itemBucketRocketFuel);
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockHydrogenFluid, AdvancedRocketryItems.itemBucketHydrogen);
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockOxygenFluid, AdvancedRocketryItems.itemBucketOxygen);
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockNitrogenFluid, AdvancedRocketryItems.itemBucketNitrogen);
		
		//FluidContainerRegistry.registerFluidContainer(AdvancedRocketryFluids.fluidRocketFuel, new ItemStack(AdvancedRocketryItems.itemBucketRocketFuel), new ItemStack(Items.BUCKET));
		//FluidContainerRegistry.registerFluidContainer(AdvancedRocketryFluids.fluidNitrogen, new ItemStack(AdvancedRocketryItems.itemBucketNitrogen), new ItemStack(Items.BUCKET));
		//FluidContainerRegistry.registerFluidContainer(AdvancedRocketryFluids.fluidHydrogen, new ItemStack(AdvancedRocketryItems.itemBucketHydrogen), new ItemStack(Items.BUCKET));
		//FluidContainerRegistry.registerFluidContainer(AdvancedRocketryFluids.fluidOxygen, new ItemStack(AdvancedRocketryItems.itemBucketOxygen), new ItemStack(Items.BUCKET));
		
		//Register mixed material's recipes
		for(MixedMaterial material : MaterialRegistry.getMixedMaterialList()) {
			RecipesMachine.getInstance().addRecipe(material.getMachine(), material.getProducts(), 100, 10, material.getInput());
		}

		//Register space dimension
		net.minecraftforge.common.DimensionManager.registerDimension(zmaster587.advancedRocketry.api.Configuration.spaceDimId, DimensionManager.spaceDimensionType);

		//Register fuels
		logger.info("Start registering liquid rocket fuels");
		for(String str : liquidRocketFuel) {
			Fluid fluid = FluidRegistry.getFluid(str);
			
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

		logger.info("Start registering sealable blocks");
		for(String str : sealableBlockWhiteList) {
			Block block = Block.getBlockFromName(str);
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				SealableBlockHandler.INSTANCE.addSealableBlock(block);
		}
		logger.info("End registering sealable blocks");
		sealableBlockWhiteList = null;

		logger.info("Start registering torch blocks");
		for(String str : breakableTorches) {
			Block block = Block.getBlockFromName(str);
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				zmaster587.advancedRocketry.api.Configuration.torchBlocks.add(block);
		}
		logger.info("End registering torch blocks");
		breakableTorches = null;
		
		
		logger.info("Start registering rocket blacklist blocks");
		for(String str : blackListRocketBlocks) {
			Block block = Block.getBlockFromName(str);
			if(block == null)
				logger.warn("'" + str + "' is not a valid Block");
			else
				zmaster587.advancedRocketry.api.Configuration.blackListRocketBlocks.add(block);
		}
		logger.info("End registering rocket blacklist blocks");
		breakableTorches = null;

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

		logger.info("Start registering entity atmosphere bypass");

		//Add armor stand by default
		zmaster587.advancedRocketry.api.Configuration.bypassEntity.add(EntityArmorStand.class);


		for(String str : entityList) {
			Class clazz = (Class) EntityList.NAME_TO_CLASS.get(str);

			//If not using string name maybe it's a class name?
			if(clazz == null) {
				try {
					clazz = Class.forName(str);
					if(clazz != null && !Entity.class.isAssignableFrom(clazz))
						clazz = null;

				} catch (Exception e) {
					//Fail silently
				}
			}

			if(clazz != null) {
				logger.info("Registering " + clazz.getName() + " for atmosphere bypass");
				zmaster587.advancedRocketry.api.Configuration.bypassEntity.add(clazz);
			}
			else
				logger.warn("Cannot find " + str + " while registering entity for atmosphere bypass");
		}

		//Free memory
		entityList = null;
		logger.info("End registering entity atmosphere bypass");

		//Register geodeOres
		if(!zmaster587.advancedRocketry.api.Configuration.geodeOresBlackList) {
			for(String str  : geodeOres)
				zmaster587.advancedRocketry.api.Configuration.standardGeodeOres.add(str);
		}

		//Register laserDrill ores
		if(!zmaster587.advancedRocketry.api.Configuration.laserDrillOresBlackList) {
			for(String str  : orbitalLaserOres)
				zmaster587.advancedRocketry.api.Configuration.standardLaserDrillOres.add(str);
		}


		//Do blacklist stuff for ore registration
		for(String oreName : OreDictionary.getOreNames()) {

			if(zmaster587.advancedRocketry.api.Configuration.geodeOresBlackList && oreName.startsWith("ore")) {
				boolean found = false;
				for(String str : geodeOres) {
					if(oreName.equals(str)) {
						found = true;
						break;
					}
				}
				if(!found)
					zmaster587.advancedRocketry.api.Configuration.standardGeodeOres.add(oreName);
			}

			if(zmaster587.advancedRocketry.api.Configuration.laserDrillOresBlackList && oreName.startsWith("ore")) {
				boolean found = false;
				for(String str : orbitalLaserOres) {
					if(oreName.equals(str)) {
						found = true;
						break;
					}
				}
				if(!found)
					zmaster587.advancedRocketry.api.Configuration.standardLaserDrillOres.add(oreName);
			}
		}

		machineRecipes.registerXMLRecipes();

		//Add the overworld as a discovered planet
		zmaster587.advancedRocketry.api.Configuration.initiallyKnownPlanets.add(0);
	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		for (int dimId : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
			if(!properties.isNativeDimension && properties.getId() == zmaster587.advancedRocketry.api.Configuration.MoonId && !Loader.isModLoaded("GalacticraftCore")) {
				properties.isNativeDimension = true;
			}
		}
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new WorldCommand());

		int dimOffset = DimensionManager.dimOffset;
		//Open ore files

		
		//Load Asteroids from XML
		File file = new File("./config/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/asteroidConfig.xml");
		logger.info("Checking for asteroid config at " + file.getAbsolutePath());
		if(!file.exists()) {
			logger.info(file.getAbsolutePath() + " not found, generating");
			try {

				file.createNewFile();
				BufferedWriter stream;
				stream = new BufferedWriter(new FileWriter(file));
				stream.write("<Asteroids>\n\t<asteroid name=\"Small Asteroid\" distance=\"10\" mass=\"100\" massVariability=\"0.5\" minLevel=\"0\" probability=\"10\" richness=\"0.2\" richnessVariability=\"0.5\">"
						+ "\n\t\t<ore itemStack=\"minecraft:iron_ore\" chance=\"15\" />"
						+ "\n\t\t<ore itemStack=\"minecraft:gold_ore\" chance=\"10\" />"
						+ "\n\t\t<ore itemStack=\"minecraft:redstone_ore\" chance=\"10\" />"
						+ "\n\t</asteroid>"
						+ "\n\t<asteroid name=\"Iridium Enriched asteroid\" distance=\"100\" mass=\"25\" massVariability=\"0.5\" minLevel=\"0\" probability=\"0.75\" richness=\"0.2\" richnessVariability=\"0.3\">"
						+ "\n\t\t<ore itemStack=\"minecraft:iron_ore\" chance=\"25\" />"
						+ "\n\t\t<ore itemStack=\"libVulpes:ore0 10\" chance=\"5\" />"
						+ "\n\t</asteroid>"
						+ "\n</Asteroids>");
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		XMLAsteroidLoader load = new XMLAsteroidLoader();
		try {
			load.loadFile(file);
			for(AsteroidSmall asteroid : load.loadPropertyFile()) {
				zmaster587.advancedRocketry.api.Configuration.asteroidTypes.put(asteroid.ID, asteroid);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// End load asteroids from XML
		
		
		file = new File("./config/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/oreConfig.xml");
		logger.info("Checking for ore config at " + file.getAbsolutePath());
		if(!file.exists()) {
			logger.info(file.getAbsolutePath() + " not found, generating");
			try {

				file.createNewFile();
				BufferedWriter stream;
				stream = new BufferedWriter(new FileWriter(file));
				stream.write("<OreConfig>\n</OreConfig>");
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			XMLOreLoader oreLoader = new XMLOreLoader();
			try {
				oreLoader.loadFile(file);

				List<SingleEntry<HashedBlockPosition, OreGenProperties>> mapping = oreLoader.loadPropertyFile();

				for(Entry<HashedBlockPosition, OreGenProperties> entry : mapping) {
					int pressure = entry.getKey().x;
					int temp = entry.getKey().y;

					if(pressure == -1) {
						if(temp != -1) {
							OreGenProperties.setOresForTemperature(Temps.values()[temp], entry.getValue());
						}
					}
					else if(temp == -1) {
						if(pressure != -1) {
							OreGenProperties.setOresForPressure(AtmosphereTypes.values()[pressure], entry.getValue());
						}
					}
					else {
						OreGenProperties.setOresForPressureAndTemp(AtmosphereTypes.values()[pressure], Temps.values()[temp], entry.getValue());
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//End open and load ore files

		//Load planet files
		//Note: loading this modifies dimOffset
		DimensionPropertyCoupling dimCouplingList = null;
		XMLPlanetLoader loader = null;
		boolean loadedFromXML = false;

		//Check advRocketry folder first
		File localFile;
		localFile = file = new File(net.minecraftforge.common.DimensionManager.getCurrentSaveRootDirectory() + "/" + DimensionManager.workingPath + "/planetDefs.xml");
		logger.info("Checking for config at " + file.getAbsolutePath());

		if(!file.exists()) { //Hi, I'm if check #42, I am true if the config is not in the world/advRocketry folder
			file = new File("./config/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/planetDefs.xml");
			logger.info("File not found.  Now checking for config at " + file.getAbsolutePath());

			//Copy file to local dir
			if(file.exists()) {
				logger.info("Advanced Planet Config file Found!  Copying to world specific directory");
				try {
					File dir = new File(localFile.getAbsolutePath().substring(0, localFile.getAbsolutePath().length() - localFile.getName().length()));

					//File cannot exist due to if check #42
					if((dir.exists() || dir.mkdir()) && localFile.createNewFile()) {
						char buffer[] = new char[1024];

						FileReader reader = new FileReader(file);
						FileWriter writer = new FileWriter(localFile);
						int numChars = 0;
						while((numChars = reader.read(buffer)) > 0) {
							writer.write(buffer, 0, numChars);
						}

						reader.close();
						writer.close();
						logger.info("Copy success!");
					}
					else
						logger.warn("Unable to create file " + localFile.getAbsolutePath());
				} catch(IOException e) {
					logger.warn("Unable to write file " + localFile.getAbsolutePath());
				}
			}
		}

		if(file.exists()) {
			logger.info("Advanced Planet Config file Found!  Loading from file.");
			loader = new XMLPlanetLoader();
			try {
				loader.loadFile(file);
				dimCouplingList = loader.readAllPlanets();
				DimensionManager.dimOffset += dimCouplingList.dims.size();
			} catch(IOException e) {

			}
		}
		//End load planet files

		//Register hard coded dimensions
		if(!zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().loadDimensions(zmaster587.advancedRocketry.dimension.DimensionManager.workingPath)) {
			int numRandomGeneratedPlanets = 9;
			int numRandomGeneratedGasGiants = 1;
			
			if(dimCouplingList != null) {
				logger.info("Loading initial planet config!");

				for(StellarBody star : dimCouplingList.stars) {
					DimensionManager.getInstance().addStar(star);
				}

				for(DimensionProperties properties : dimCouplingList.dims) {
					DimensionManager.getInstance().registerDimNoUpdate(properties, properties.isNativeDimension);
					properties.setStar(properties.getStarId());
				}

				for(StellarBody star : dimCouplingList.stars) {
					numRandomGeneratedPlanets = loader.getMaxNumPlanets(star);
					numRandomGeneratedGasGiants = loader.getMaxNumGasGiants(star);
					dimCouplingList.dims.addAll(generateRandomPlanets(star, numRandomGeneratedPlanets, numRandomGeneratedGasGiants));
				}

				loadedFromXML = true;
			}

			if(!loadedFromXML) {
				//Make Sol				
				StellarBody sol = new StellarBody();
				sol.setTemperature(100);
				sol.setId(0);
				sol.setName("Sol");

				DimensionManager.getInstance().addStar(sol);

				//Add the overworld
				DimensionManager.getInstance().registerDimNoUpdate(DimensionManager.overworldProperties, false);
				sol.addPlanet(DimensionManager.overworldProperties);

				if(zmaster587.advancedRocketry.api.Configuration.MoonId == -1)
					zmaster587.advancedRocketry.api.Configuration.MoonId = DimensionManager.getInstance().getNextFreeDim(dimOffset);



				//Register the moon
				if(zmaster587.advancedRocketry.api.Configuration.MoonId != -1) {
					DimensionProperties dimensionProperties = new DimensionProperties(zmaster587.advancedRocketry.api.Configuration.MoonId);
					dimensionProperties.setAtmosphereDensityDirect(0);
					dimensionProperties.averageTemperature = 20;
					dimensionProperties.rotationalPeriod = 128000;
					dimensionProperties.gravitationalMultiplier = .166f; //Actual moon value
					dimensionProperties.setName("Luna");
					dimensionProperties.orbitalDist = 150;
					dimensionProperties.addBiome(AdvancedRocketryBiomes.moonBiome);

					dimensionProperties.setParentPlanet(DimensionManager.overworldProperties);
					dimensionProperties.setStar(DimensionManager.getSol());
					dimensionProperties.isNativeDimension = !Loader.isModLoaded("GalacticraftCore");

					DimensionManager.getInstance().registerDimNoUpdate(dimensionProperties, !Loader.isModLoaded("GalacticraftCore"));
				}

				generateRandomPlanets(DimensionManager.getSol(), numRandomGeneratedPlanets, numRandomGeneratedGasGiants);

				StellarBody star = new StellarBody();
				star.setTemperature(10);
				star.setPosX(300);
				star.setPosZ(-200);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Wolf 12");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 5, 0);

				star = new StellarBody();
				star.setTemperature(170);
				star.setPosX(-200);
				star.setPosZ(80);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Epsilon ire");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 7, 0);

				star = new StellarBody();
				star.setTemperature(200);
				star.setPosX(-150);
				star.setPosZ(250);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Proxima Centaurs");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 3, 0);

				star = new StellarBody();
				star.setTemperature(70);
				star.setPosX(-150);
				star.setPosZ(-250);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Magnis Vulpes");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 2, 0);
				
				
				star = new StellarBody();
				star.setTemperature(200);
				star.setPosX(50);
				star.setPosZ(-250);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Ma-Roo");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 6, 0);
				
				star = new StellarBody();
				star.setTemperature(120);
				star.setPosX(75);
				star.setPosZ(200);
				star.setId(DimensionManager.getInstance().getNextFreeStarId());
				star.setName("Alykitt");
				DimensionManager.getInstance().addStar(star);
				generateRandomPlanets(star, 3, 1);
				
			}
		}
		else {
			VersionCompat.upgradeDimensionManagerPostLoad(DimensionManager.prevBuild);
		}


		//Clean up floating stations and satellites
		if(resetFromXml) {
			//Move all space stations to the overworld
			for(ISpaceObject obj : SpaceObjectManager.getSpaceManager().getSpaceObjects()) {
				if(obj.getOrbitingPlanetId() != 0 ) {
					SpaceObjectManager.getSpaceManager().moveStationToBody(obj, 0, false);
				}
			}
			//Satellites are cleaned up on their own as dimension properties are reset
		}


		//Attempt to load ore config from adv planet XML
		if(dimCouplingList != null) {

			//Register new stars
			for(StellarBody star : dimCouplingList.stars) {
				if(DimensionManager.getInstance().getStar(star.getId()) == null)
					DimensionManager.getInstance().addStar(star);

				DimensionManager.getInstance().getStar(star.getId()).subStars = star.subStars;
			}

			for(DimensionProperties properties : dimCouplingList.dims) {

				//Register dimensions loaded by other mods if not already loaded
				if(!properties.isNativeDimension && properties.getStar() != null && !DimensionManager.getInstance().isDimensionCreated(properties.getId())) {
					for(StellarBody star : dimCouplingList.stars) {
						for(StellarBody loadedStar : DimensionManager.getInstance().getStars()) {
							if(star.getId() == properties.getStarId() && star.getName().equals(loadedStar.getName())) {
								DimensionManager.getInstance().registerDimNoUpdate(properties, false);
								properties.setStar(loadedStar);
							}
						}
					}
				}

				//Overwrite with loaded XML
				if(DimensionManager.getInstance().isDimensionCreated(properties.getId())) {
					DimensionProperties loadedProps = DimensionManager.getInstance().getDimensionProperties(properties.getId());

					loadedProps.fogColor = properties.fogColor;
					loadedProps.gravitationalMultiplier = properties.gravitationalMultiplier;
					loadedProps.hasRings = properties.hasRings;
					loadedProps.orbitalDist = properties.getOrbitalDist();
					loadedProps.ringColor = properties.ringColor;
					loadedProps.orbitalPhi = properties.orbitalPhi;
					loadedProps.rotationalPeriod = properties.rotationalPeriod;
					loadedProps.skyColor = properties.skyColor;
					loadedProps.setSeaLevel(properties.getSeaLevel());
					loadedProps.setOceanBlock(properties.getOceanBlock());
					loadedProps.setBiomeEntries(properties.getBiomes());
					loadedProps.setAtmosphereDensityDirect(properties.getAtmosphereDensity());
					loadedProps.setName(properties.getName());
					
					//Avoid CME
					for(int i : new HashSet<Integer>(properties.getChildPlanets()))
						loadedProps.addChildPlanet(DimensionManager.getInstance().getDimensionProperties(i));

					if(properties.isGasGiant()) loadedProps.setGasGiant();
					
					//Register gasses if needed
					if(!properties.getHarvestableGasses().isEmpty() && properties.getHarvestableGasses() != loadedProps.getHarvestableGasses()) {
						loadedProps.getHarvestableGasses().clear();
						loadedProps.getHarvestableGasses().addAll(properties.getHarvestableGasses());
					}
					
					if(!loadedProps.isMoon() && properties.isMoon()) loadedProps.setParentPlanet(properties.getParentProperties());
					if(loadedProps.isMoon() && !properties.isMoon()) {
						loadedProps.getParentProperties().removeChild(loadedProps.getId());
						loadedProps.setParentPlanet(null);
					}
				}
				else {
					DimensionManager.getInstance().registerDim(properties, properties.isNativeDimension);
				}

				if(!properties.customIcon.isEmpty()) {
					DimensionProperties loadedProps;
					if(DimensionManager.getInstance().isDimensionCreated(properties.getId())) {
						loadedProps = DimensionManager.getInstance().getDimensionProperties(properties.getId());
						loadedProps.customIcon = properties.customIcon;
					}
				}
				//TODO: add properties fromXML

				//Add artifacts if needed
				if(DimensionManager.getInstance().isDimensionCreated(properties.getId())) {
					DimensionProperties loadedProps;
					loadedProps = DimensionManager.getInstance().getDimensionProperties(properties.getId());
					List<ItemStack> list = new LinkedList<ItemStack>(properties.getRequiredArtifacts());
					loadedProps.getRequiredArtifacts().clear();
					loadedProps.getRequiredArtifacts().addAll(list);
				}


				if(properties.oreProperties != null) {
					DimensionProperties loadedProps = DimensionManager.getInstance().getDimensionProperties(properties.getId());

					if(loadedProps != null)
						loadedProps.oreProperties = properties.oreProperties;
				}
			}

			//Remove dimensions not in the XML
			for(int i : DimensionManager.getInstance().getRegisteredDimensions()) {
				boolean found = false;
				for(DimensionProperties properties : dimCouplingList.dims) {
					if(properties.getId() == i) {
						found = true;
						break;
					}
				}

				if(!found) {
					DimensionManager.getInstance().deleteDimension(i);
				}
			}

			//Remove stars not in the XML
			for(int i : new HashSet<Integer>(DimensionManager.getInstance().getStarIds())) {
				boolean found = false;
				for(StellarBody properties : dimCouplingList.stars) {
					if(properties.getId() == i) {
						found = true;
						break;
					}
				}

				if(!found) {
					DimensionManager.getInstance().removeStar(i);
				}
			}

			//Add planets
			for(StellarBody star : dimCouplingList.stars) {
				int numRandomGeneratedPlanets = loader.getMaxNumPlanets(star);
				int numRandomGeneratedGasGiants = loader.getMaxNumGasGiants(star);
				generateRandomPlanets(star, numRandomGeneratedPlanets, numRandomGeneratedGasGiants);
			}
		}

		// make sure to set dim offset back to original to make things consistant
		DimensionManager.dimOffset = dimOffset;
		
		DimensionManager.getInstance().knownPlanets.addAll(zmaster587.advancedRocketry.api.Configuration.initiallyKnownPlanets);
		
	}

	private List<DimensionProperties> generateRandomPlanets(StellarBody star, int numRandomGeneratedPlanets, int numRandomGeneratedGasGiants) {
		List<DimensionProperties> dimPropList = new LinkedList<DimensionProperties>();

		Random random = new Random(System.currentTimeMillis());


		for(int i = 0; i < numRandomGeneratedGasGiants; i++) {
			int baseAtm = 180;
			int baseDistance = 100;

			DimensionProperties	properties = DimensionManager.getInstance().generateRandomGasGiant(star.getId(), "",baseDistance + 50,baseAtm,125,100,100,75);

			dimPropList.add(properties);
			if(properties.gravitationalMultiplier >= 1f) {
				int numMoons = random.nextInt(8);

				for(int ii = 0; ii < numMoons; ii++) {
					DimensionProperties moonProperties = DimensionManager.getInstance().generateRandom(star.getId(), properties.getName() + ": " + ii, 25,100, (int)(properties.gravitationalMultiplier/.02f), 25, 100, 50);
					if(moonProperties == null)
						continue;

					dimPropList.add(moonProperties);

					moonProperties.setParentPlanet(properties);
					star.removePlanet(moonProperties);
				}
			}
		}

		for(int i = 0; i < numRandomGeneratedPlanets; i++) {
			int baseAtm = 75;
			int baseDistance = 100;

			if(i % 4 == 0) {
				baseAtm = 0;
			}
			else if(i != 6 && (i+2) % 4 == 0)
				baseAtm = 120;

			if(i % 3 == 0) {
				baseDistance = 170;
			}
			else if((i + 1) % 3 == 0) {
				baseDistance = 30;
			}

			DimensionProperties properties = DimensionManager.getInstance().generateRandom(star.getId(), baseDistance,baseAtm,125,100,100,75);

			if(properties == null)
				continue;

			dimPropList.add(properties);

			if(properties.gravitationalMultiplier >= 1f) {
				int numMoons = random.nextInt(4);

				for(int ii = 0; ii < numMoons; ii++) {
					DimensionProperties moonProperties = DimensionManager.getInstance().generateRandom(star.getId(), properties.getName() + ": " + ii, 25,100, (int)(properties.gravitationalMultiplier/.02f), 25, 100, 50);

					if(moonProperties == null)
						continue;

					dimPropList.add(moonProperties);
					moonProperties.setParentPlanet(properties);
					star.removePlanet(moonProperties);
				}
			}
		}

		return dimPropList;
	}


	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().unregisterAllDimensions();
		zmaster587.advancedRocketry.cable.NetworkRegistry.clearNetworks();
		SpaceObjectManager.getSpaceManager().onServerStopped();
		zmaster587.advancedRocketry.api.Configuration.MoonId = -1;
		DimensionManager.getInstance().overworldProperties.resetProperties();
		((BlockSeal)AdvancedRocketryBlocks.blockPipeSealer).clearMap();
		DimensionManager.dimOffset = config.getInt("minDimension", PLANET, 2, -127, 8000, "Dimensions including and after this number are allowed to be made into planets");
		
		DimensionManager.getInstance().knownPlanets.clear();
		
		if(!zmaster587.advancedRocketry.api.Configuration.lockUI)
			proxy.saveUILayout(config);
	}

	@SubscribeEvent
	public void registerOre(OreRegisterEvent event) {

		//Register ore products
		if(!zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods)
			return;

		for(AllowedProducts product : AllowedProducts.getAllAllowedProducts() ) {
			if(event.getName().startsWith(product.name().toLowerCase(Locale.ENGLISH))) {
				HashSet<String> list = modProducts.get(product);
				if(list == null) {
					list = new HashSet<String>();
					modProducts.put(product, list);
				}
				list.add(event.getName().substring(product.name().length()));
			}
		}

		//GT uses stick instead of Rod
		if(event.getName().startsWith("rod")) {
			HashSet<String> list = modProducts.get(AllowedProducts.getProductByName("STICK"));
			if(list == null) {
				list = new HashSet<String>();
				modProducts.put(AllowedProducts.getProductByName("STICK"), list);
			}

			list.add(event.getName().substring("rod".length()));
		}
	}
}
