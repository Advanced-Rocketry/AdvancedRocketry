package zmaster587.advancedRocketry;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.atmosphere.AtmosphereVacuum;
import zmaster587.advancedRocketry.block.BlockCharcoalLog;
import zmaster587.advancedRocketry.block.BlockCrystal;
import zmaster587.advancedRocketry.block.BlockDoor2;
import zmaster587.advancedRocketry.block.BlockElectricMushroom;
import zmaster587.advancedRocketry.block.BlockFluid;
import zmaster587.advancedRocketry.block.BlockGeneric;
import zmaster587.advancedRocketry.block.BlockLandingPad;
import zmaster587.advancedRocketry.block.BlockLaser;
import zmaster587.advancedRocketry.block.BlockLightSource;
import zmaster587.advancedRocketry.block.BlockLinkedHorizontalTexture;
import zmaster587.advancedRocketry.block.BlockMiningDrill;
import zmaster587.advancedRocketry.block.BlockPlanetSoil;
import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.block.BlockQuartzCrucible;
import zmaster587.advancedRocketry.block.BlockRedstoneEmitter;
import zmaster587.advancedRocketry.block.BlockRocketMotor;
import zmaster587.advancedRocketry.block.BlockRotatableModel;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.block.BlockFuelTank;
import zmaster587.advancedRocketry.block.BlockSolarPanel;
import zmaster587.advancedRocketry.block.BlockTileNeighborUpdate;
import zmaster587.advancedRocketry.block.BlockTileRedstoneEmitter;
import zmaster587.advancedRocketry.block.BlockWarpCore;
import zmaster587.advancedRocketry.block.BlockWarpShipMonitor;
import zmaster587.advancedRocketry.block.cable.BlockDataCable;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.block.plant.BlockAlienLeaves;
import zmaster587.advancedRocketry.block.plant.BlockAlienSapling;
import zmaster587.advancedRocketry.block.plant.BlockAlienWood;
import zmaster587.advancedRocketry.block.BlockTorchUnlit;
import zmaster587.advancedRocketry.command.WorldCommand;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityDummy;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.event.BucketHandler;
import zmaster587.advancedRocketry.event.CableTickHandler;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.WorldEvents;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.integration.GalacticCraftHandler;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.item.ItemBlockMeta;
import zmaster587.libVulpes.items.ItemIngredient;
import zmaster587.libVulpes.items.ItemProjector;
import zmaster587.advancedRocketry.item.*;
import zmaster587.advancedRocketry.item.components.ItemJetpack;
import zmaster587.advancedRocketry.item.components.ItemPressureTank;
import zmaster587.advancedRocketry.item.components.ItemUpgrade;
import zmaster587.advancedRocketry.mission.MissionOreMining;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.advancedRocketry.network.PacketSatellite;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.network.PacketStellarInfo;
import zmaster587.advancedRocketry.satellite.SatelliteComposition;
import zmaster587.advancedRocketry.satellite.SatelliteDensity;
import zmaster587.advancedRocketry.satellite.SatelliteEnergy;
import zmaster587.advancedRocketry.satellite.SatelliteMassScanner;
import zmaster587.advancedRocketry.satellite.SatelliteOptical;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.advancedRocketry.tile.Satellite.TileEntitySatelliteControlCenter;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteBuilder;
import zmaster587.advancedRocketry.tile.*;
import zmaster587.advancedRocketry.tile.cables.TileDataPipe;
import zmaster587.advancedRocketry.tile.cables.TileLiquidPipe;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityFuelingStation;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityMoniteringStation;
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
import zmaster587.advancedRocketry.tile.station.TileStationGravityController;
import zmaster587.advancedRocketry.tile.station.TileStationOrientationControl;
import zmaster587.advancedRocketry.tile.station.TileWarpShipMonitor;
import zmaster587.advancedRocketry.util.FluidColored;
import zmaster587.advancedRocketry.util.SealableBlockHandler;
import zmaster587.advancedRocketry.util.XMLPlanetLoader;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import zmaster587.advancedRocketry.world.biome.BiomeGenCrystal;
import zmaster587.advancedRocketry.world.biome.BiomeGenDeepSwamp;
import zmaster587.advancedRocketry.world.biome.BiomeGenHotDryRock;
import zmaster587.advancedRocketry.world.biome.BiomeGenMoon;
import zmaster587.advancedRocketry.world.biome.BiomeGenMarsh;
import zmaster587.advancedRocketry.world.biome.BiomeGenOceanSpires;
import zmaster587.advancedRocketry.world.biome.BiomeGenSpace;
import zmaster587.advancedRocketry.world.biome.BiomeGenStormland;
import zmaster587.advancedRocketry.world.ore.OreGenerator;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.advancedRocketry.world.type.WorldTypePlanetGen;
import zmaster587.advancedRocketry.world.type.WorldTypeSpace;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.LibVulpesItems;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.api.material.MixedMaterial;
import zmaster587.libVulpes.api.material.Material.Materials;
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.TileMaterial;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.InputSyncHandler;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;


@Mod(modid="advancedRocketry", name="Advanced Rocketry", version="%VERSION%", dependencies="required-after:libVulpes@[%LIBVULPESVERSION%,)")
public class AdvancedRocketry {


	@SidedProxy(clientSide="zmaster587.advancedRocketry.client.ClientProxy", serverSide="zmaster587.advancedRocketry.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(value = Constants.modId)
	public static AdvancedRocketry instance;
	public static WorldType planetWorldType;
	public static WorldType spaceWorldType;

	public static CompatibilityMgr compat = new CompatibilityMgr();
	public static Logger logger = Logger.getLogger(Constants.modId);
	private static Configuration config;
	private static final String BIOMECATETORY = "Biomes";
	String[] sealableBlockWhileList;

	private HashMap<AllowedProducts, HashSet<String>> modProducts = new HashMap<AllowedProducts, HashSet<String>>();


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

		final String oreGen = "Ore Generation";
		final String ROCKET = "Rockets";
		final String MOD_INTERACTION = "Mod Interaction";
		final String PLANET = "Planet";
		final String ASTEROID = "Asteroid";
		final String PERFORMANCE = "Performance";

		AtmosphereVacuum.damageValue = (int) config.get(Configuration.CATEGORY_GENERAL, "vacuumDamage", 1, "Amount of damage taken every second in a vacuum").getInt();
		zmaster587.advancedRocketry.api.Configuration.buildSpeedMultiplier = (float) config.get(Configuration.CATEGORY_GENERAL, "buildSpeedMultiplier", 1f, "Multiplier for the build speed of the Rocket Builder (0.5 is twice as fast 2 is half as fast").getDouble();
		zmaster587.advancedRocketry.api.Configuration.spaceDimId = config.get(Configuration.CATEGORY_GENERAL,"spaceStationId" , -2,"Dimension ID to use for space stations").getInt();
		zmaster587.advancedRocketry.api.Configuration.enableOxygen = config.get(Configuration.CATEGORY_GENERAL, "EnableAtmosphericEffects", true, "If true, allows players being hurt due to lack of oxygen and allows effects from non-standard atmosphere types").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods = config.get(Configuration.CATEGORY_GENERAL, "makeMaterialsForOtherMods", true, "If true the machines from AdvancedRocketry will produce things like plates/rods for other mods even if Advanced Rocketry itself does not use the material (This can increase load time)").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.scrubberRequiresCartrige = config.get(Configuration.CATEGORY_GENERAL, "scrubberRequiresCartrige", true, "If true the Oxygen scrubbers require a consumable carbon collection cartridge").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.enableLaserDrill = config.get(Configuration.CATEGORY_GENERAL, "EnableLaserDrill", true, "Enables the laser drill machine").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.spaceSuitOxygenTime = config.get(Configuration.CATEGORY_GENERAL, "spaceSuitO2Buffer", 30, "Maximum time in minutes that the spacesuit's internal buffer can store O2 for").getInt();

		DimensionManager.dimOffset = config.getInt("minDimension", PLANET, 2, -127, 127, "Dimensions including and after this number are allowed to be made into planets");
		zmaster587.advancedRocketry.api.Configuration.overrideGCAir = config.get(MOD_INTERACTION, "OverrideGCAir", true, "If true Galaciticcraft's air will be disabled entirely requiring use of Advanced Rocketry's Oxygen system on GC planets").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.fuelPointsPerDilithium = config.get(Configuration.CATEGORY_GENERAL, "pointsPerDilithium", 500, "How many units of fuel should each Dilithium Crystal give to warp ships", 1, 1000).getInt();
		zmaster587.advancedRocketry.api.Configuration.electricPlantsSpawnLightning = config.get(Configuration.CATEGORY_GENERAL, "electricPlantsSpawnLightning", true, "Should Electric Mushrooms be able to spawn lightning").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.allowSawmillVanillaWood = config.get(Configuration.CATEGORY_GENERAL, "sawMillCutVanillaWood", true, "Should the cutting machine be able to cut vanilla wood into planks").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.automaticRetroRockets = config.get(ROCKET, "autoRetroRockets", true, "Setting to false will disable the retrorockets that fire automatically on reentry on both player and automated rockets").getBoolean();


		zmaster587.advancedRocketry.api.Configuration.atmosphereHandleBitMask = config.get(PERFORMANCE, "atmosphereCalculationMethod", 0, "BitMask: 0: no threading, radius based; 1: threading, radius based (EXP); 2: no threading volume based; 3: threading volume based (EXP)").getInt();
		zmaster587.advancedRocketry.api.Configuration.advancedVFX = config.get(PERFORMANCE, "advancedVFX", true, "Advanced visual effects").getBoolean();


		zmaster587.advancedRocketry.api.Configuration.asteroidMiningMult = config.get(ASTEROID, "miningMissionMultiplier", 1.0, "Multiplier changing how much total material is brought back from a mining mission").getDouble();
		zmaster587.advancedRocketry.api.Configuration.standardAsteroidOres = config.get(ASTEROID, "standardOres", new String[] {"oreIron", "oreGold", "oreCopper", "oreTin", "oreRedstone"}, "List of oredictionary names of ores allowed to spawn in asteriods").getStringList();


		//Client
		zmaster587.advancedRocketry.api.Configuration.rocketRequireFuel = config.get(ROCKET, "rocketsRequireFuel", true, "Set to false if rockets should not require fuel to fly").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.rocketThrustMultiplier = config.get(ROCKET, "thrustMultiplier", 1f, "Multiplier for per-engine thrust").getDouble();
		zmaster587.advancedRocketry.api.Configuration.fuelCapacityMultiplier = config.get(ROCKET, "fuelCapacityMultiplier", 1f, "Multiplier for per-tank capacity").getDouble();

		//Copper config
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

		zmaster587.advancedRocketry.api.Configuration.generateRutile = config.get(oreGen, "GenerateRutile", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.rutileClumpSize = config.get(oreGen, "RutilePerClump", 3).getInt();
		zmaster587.advancedRocketry.api.Configuration.rutilePerChunk = config.get(oreGen, "RutilePerChunk", 6).getInt();
		sealableBlockWhileList = config.getStringList(Configuration.CATEGORY_GENERAL, "sealableBlockWhiteList", new String[] {}, "Mod:Blockname  for example \"minecraft:chest\"");

		config.save();

		//Register Packets
		PacketHandler.addDiscriminator(PacketDimInfo.class);
		PacketHandler.addDiscriminator(PacketSatellite.class);
		PacketHandler.addDiscriminator(PacketStellarInfo.class);
		PacketHandler.addDiscriminator(PacketItemModifcation.class);
		PacketHandler.addDiscriminator(PacketOxygenState.class);
		PacketHandler.addDiscriminator(PacketStationUpdate.class);
		PacketHandler.addDiscriminator(PacketSpaceStationInfo.class);
		
		//if(zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods)
		MinecraftForge.EVENT_BUS.register(this);

		//Satellites ---------------------------------------------------------------------------------------------
		SatelliteRegistry.registerSatellite("optical", SatelliteOptical.class);
		SatelliteRegistry.registerSatellite("solar", SatelliteEnergy.class);
		SatelliteRegistry.registerSatellite("density", SatelliteDensity.class);
		SatelliteRegistry.registerSatellite("composition", SatelliteComposition.class);
		SatelliteRegistry.registerSatellite("mass", SatelliteMassScanner.class);
		SatelliteRegistry.registerSatellite("asteroidMiner", MissionOreMining.class);
		SatelliteRegistry.registerSatellite("solarEnergy", SatelliteEnergy.class);
		SatelliteRegistry.registerSatellite("oreScanner", SatelliteOreMapping.class);

		//Blocks -------------------------------------------------------------------------------------
		AdvancedRocketryBlocks.blocksGeode = new BlockGeneric(MaterialGeode.geode).setBlockName("geode").setCreativeTab(LibVulpes.tabLibVulpesOres).setBlockTextureName("advancedrocketry:geode").setHardness(6f).setResistance(2000F);
		AdvancedRocketryBlocks.blocksGeode.setHarvestLevel("jackhammer", 2);

		AdvancedRocketryBlocks.blockLaunchpad = new BlockLinkedHorizontalTexture(Material.rock).setBlockName("pad").setCreativeTab(tabAdvRocketry).setBlockTextureName("advancedrocketry:rocketPad").setHardness(2f).setResistance(10f);
		AdvancedRocketryBlocks.blockStructureTower = new BlockAlphaTexture(Material.rock).setBlockName("structuretower").setCreativeTab(tabAdvRocketry).setBlockTextureName("advancedrocketry:structuretower").setHardness(2f);
		AdvancedRocketryBlocks.blockGenericSeat = new BlockSeat(Material.cloth).setBlockName("seat").setCreativeTab(tabAdvRocketry).setBlockTextureName("minecraft:wool_colored_silver").setHardness(0.5f);
		AdvancedRocketryBlocks.blockEngine = new BlockRocketMotor(Material.rock).setBlockName("rocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockFuelTank = new BlockFuelTank(Material.rock).setBlockName("fuelTank").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockSawBlade = new BlockRotatableModel(Material.rock, TileModelRender.models.SAWBLADE.ordinal()).setCreativeTab(tabAdvRocketry).setBlockName("sawBlade").setHardness(2f);
		AdvancedRocketryBlocks.blockMotor = new BlockRotatableModel(Material.rock, TileModelRender.models.MOTOR.ordinal()).setCreativeTab(tabAdvRocketry).setBlockName("motor").setHardness(2f);
		AdvancedRocketryBlocks.blockConcrete = new BlockGeneric(Material.rock).setBlockName("concrete").setBlockTextureName("advancedRocketry:rocketPad_noEdge").setCreativeTab(tabAdvRocketry).setHardness(3f).setResistance(16f);
		AdvancedRocketryBlocks.blockPlatePress = new BlockPress().setBlockName("blockHandPress").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockAirLock = new BlockDoor2(Material.rock).setBlockName("smallAirlockDoor").setBlockTextureName("advancedRocketry:smallAirlockDoor").setHardness(3f).setResistance(8f);
		AdvancedRocketryBlocks.blockLandingPad = new BlockLandingPad(Material.rock).setBlockName("dockingPad").setBlockTextureName("advancedRocketry:rocketPad_").setHardness(3f).setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockOxygenDetection = new BlockRedstoneEmitter(Material.rock,"advancedrocketry:atmosphereDetector_active").setBlockName("atmosphereDetector").setBlockTextureName("advancedRocketry:atmosphereDetector").setHardness(3f).setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockOxygenScrubber = new BlockTile(TileCO2Scrubber.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockTextureName("advancedrocketry:machineScrubber","advancedrocketry:machineScrubberActive").setCreativeTab(tabAdvRocketry).setBlockName("scrubber").setHardness(3f);
		AdvancedRocketryBlocks.blockUnlitTorch = new BlockTorchUnlit().setHardness(0.0F).setBlockName("unlittorch").setBlockTextureName("minecraft:torch_on");
		AdvancedRocketryBlocks.blockVitrifiedSand = new BlockGeneric(Material.sand).setBlockName("vitrifiedSand").setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("advancedrocketry:vitrifiedSand").setHardness(0.5F).setStepSound(Block.soundTypeSand);
		AdvancedRocketryBlocks.blockCharcoalLog = new BlockCharcoalLog().setBlockName("charcoallog").setCreativeTab(CreativeTabs.tabBlock);
		AdvancedRocketryBlocks.blockElectricMushroom = new BlockElectricMushroom().setBlockName("electricMushroom").setCreativeTab(tabAdvRocketry).setBlockTextureName("advancedrocketry:mushroom_electric").setHardness(0.0F).setStepSound(Block.soundTypeGrass);
		AdvancedRocketryBlocks.blockCrystal = new BlockCrystal().setBlockName("crystal").setCreativeTab(LibVulpes.tabLibVulpesOres).setBlockTextureName("advancedrocketry:crystal").setHardness(2f);

		AdvancedRocketryBlocks.blockOrientationController = new BlockTile(TileStationOrientationControl.class,  GuiHandler.guiId.MODULAR.ordinal()).setBlockTextureName("advancedrocketry:machineScrubber").setCreativeTab(tabAdvRocketry).setBlockName("orientationControl").setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockOrientationController).setSideTexture("advancedrocketry:machineOrientationControl");
		((BlockTile) AdvancedRocketryBlocks.blockOrientationController).setTopTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockOrientationController).setFrontTexture("advancedrocketry:machineOrientationControl");

		AdvancedRocketryBlocks.blockGravityController = new BlockTile(TileStationGravityController.class,  GuiHandler.guiId.MODULAR.ordinal()).setBlockTextureName("advancedrocketry:machineScrubber").setCreativeTab(tabAdvRocketry).setBlockName("gravityControl").setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockGravityController).setSideTexture("advancedrocketry:machineOrientationControl");
		((BlockTile) AdvancedRocketryBlocks.blockGravityController).setTopTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockGravityController).setFrontTexture("advancedrocketry:machineOrientationControl");


		AdvancedRocketryBlocks.blockOxygenCharger = new BlockTile(TileOxygenCharger.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("oxygenCharger").setCreativeTab(tabAdvRocketry).setBlockTextureName("libvulpes:machineGeneric").setHardness(3f);
		AdvancedRocketryBlocks.blockOxygenCharger.setBlockBounds(0, 0, 0, 1, 0.5f, 1);
		((BlockTile) AdvancedRocketryBlocks.blockOxygenCharger).setSideTexture("advancedrocketry:panelSide");
		((BlockTile) AdvancedRocketryBlocks.blockOxygenCharger).setTopTexture("advancedrocketry:gasChargerTop");
		((BlockTile) AdvancedRocketryBlocks.blockOxygenCharger).setFrontTexture("advancedrocketry:panelSide");

		AdvancedRocketryBlocks.blockOxygenVent = new BlockTile(TileOxygenVent.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("oxygenVent").setCreativeTab(tabAdvRocketry).setBlockTextureName("libvulpes:machineGeneric").setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockOxygenVent).setSideTexture("advancedrocketry:machineVent");
		((BlockTile) AdvancedRocketryBlocks.blockOxygenVent).setTopTexture("advancedrocketry:machineVent");
		((BlockTile) AdvancedRocketryBlocks.blockOxygenVent).setFrontTexture("advancedrocketry:machineVent");

		AdvancedRocketryBlocks.blockRocketBuilder = new BlockTile(TileRocketBuilder.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setBlockName("rocketAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockRocketBuilder).setSideTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockRocketBuilder).setTopTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockRocketBuilder).setFrontTexture("advancedrocketry:MonitorFront");

		AdvancedRocketryBlocks.blockStationBuilder = new BlockTile(TileStationBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("stationAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockStationBuilder).setSideTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockStationBuilder).setTopTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockStationBuilder).setFrontTexture("advancedrocketry:MonitorFront");

		AdvancedRocketryBlocks.blockFuelingStation = new BlockTileRedstoneEmitter(TileEntityFuelingStation.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("fuelStation").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockFuelingStation).setSideTexture("Advancedrocketry:FuelingMachine");
		((BlockTile) AdvancedRocketryBlocks.blockFuelingStation).setTopTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockFuelingStation).setFrontTexture("Advancedrocketry:FuelingMachine");

		AdvancedRocketryBlocks.blockMonitoringStation = new BlockTileNeighborUpdate(TileEntityMoniteringStation.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockMonitoringStation).setSideTexture("libvulpes:machineGeneric", "libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockMonitoringStation).setTopTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockMonitoringStation).setFrontTexture("Advancedrocketry:MonitorRocket");
		AdvancedRocketryBlocks.blockMonitoringStation.setBlockName("monitoringstation");

		AdvancedRocketryBlocks.blockWarpShipMonitor = new BlockWarpShipMonitor(TileWarpShipMonitor.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockWarpShipMonitor).setSideTexture("libvulpes:machineGeneric", "libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockWarpShipMonitor).setTopTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockWarpShipMonitor).setFrontTexture("Advancedrocketry:starshipcontrolPanel");
		AdvancedRocketryBlocks.blockWarpShipMonitor.setBlockName("stationmonitor");

		AdvancedRocketryBlocks.blockSatelliteBuilder = new BlockMultiblockMachine(TileSatelliteBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteBuilder).setSideTexture("libvulpes:machineGeneric", "libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteBuilder).setTopTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteBuilder).setFrontTexture("Advancedrocketry:satelliteAssembler");
		AdvancedRocketryBlocks.blockSatelliteBuilder.setBlockName("satelliteBuilder");

		AdvancedRocketryBlocks.blockSatelliteControlCenter = new BlockTile(TileEntitySatelliteControlCenter.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteControlCenter).setSideTexture("libvulpes:machineGeneric", "libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteControlCenter).setTopTexture("libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteControlCenter).setFrontTexture("Advancedrocketry:MonitorSatellite");
		AdvancedRocketryBlocks.blockSatelliteControlCenter.setBlockName("satelliteMonitor");

		AdvancedRocketryBlocks.blockMicrowaveReciever = new BlockMultiblockMachine(TileMicrowaveReciever.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockMicrowaveReciever).setSideTexture("libvulpes:machineGeneric", "libvulpes:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockMicrowaveReciever).setTopTexture("Advancedrocketry:solar");
		((BlockTile) AdvancedRocketryBlocks.blockMicrowaveReciever).setFrontTexture("libvulpes:machineGeneric");
		AdvancedRocketryBlocks.blockMicrowaveReciever.setBlockName("microwaveReciever");

		//Arcfurnace
		AdvancedRocketryBlocks.blockArcFurnace = new BlockMultiblockMachine(TileElectricArcFurnace.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("electricArcFurnace").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockArcFurnace).setSideTexture("Advancedrocketry:BlastBrick");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockArcFurnace).setFrontTexture("Advancedrocketry:BlastBrickFront", "Advancedrocketry:BlastBrickFrontActive");

		AdvancedRocketryBlocks.blockMoonTurf = new BlockPlanetSoil().setMapColor(MapColor.snowColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("turf").setBlockTextureName("advancedrocketry:moon_turf").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockHotTurf = new BlockPlanetSoil().setMapColor(MapColor.netherrackColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("hotDryturf").setBlockTextureName("advancedrocketry:hotdry_turf").setCreativeTab(tabAdvRocketry);

		AdvancedRocketryBlocks.blockLoader = new BlockARHatch(Material.rock).setBlockName("loader").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockAlienWood = new BlockAlienWood().setBlockName("log").setBlockTextureName("advancedrocketry:log").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienLeaves = new BlockAlienLeaves().setBlockName("leaves2").setBlockTextureName("leaves").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienSapling = new BlockAlienSapling().setBlockName("sapling").setBlockTextureName("advancedrocketry:sapling").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockLightSource = new BlockLightSource();

		AdvancedRocketryBlocks.blockBlastBrick = new BlockMultiBlockComponentVisible(Material.rock).setCreativeTab(tabAdvRocketry).setBlockName("blastBrick").setBlockTextureName("advancedRocketry:BlastBrick").setHardness(3F).setResistance(15F);
		AdvancedRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible();

		AdvancedRocketryBlocks.blockPrecisionAssembler = new BlockMultiblockMachine(TilePrecisionAssembler.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("precisionAssemblingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPrecisionAssembler).setFrontTexture("advancedrocketry:PrecisionAssemblerFront", "advancedrocketry:PrecisionAssemblerFront_Active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPrecisionAssembler).setSideTexture("libvulpes:machineGeneric");

		AdvancedRocketryBlocks.blockCuttingMachine = new BlockMultiblockMachine(TileCuttingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("cuttingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCuttingMachine).setFrontTexture("advancedrocketry:CuttingMachine", "advancedrocketry:CuttingMachine_active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCuttingMachine).setSideTexture("libvulpes:machineGeneric");

		AdvancedRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(TileCrystallizer.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("Crystallizer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCrystallizer).setSideTexture("Advancedrocketry:Crystallizer", "Advancedrocketry:Crystallizer_active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCrystallizer).setTopTexture("libvulpes:machineGeneric");

		AdvancedRocketryBlocks.blockWarpCore = new BlockWarpCore(TileWarpCore.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("warpCore").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockWarpCore).setSideTexture("Advancedrocketry:warpcore");

		AdvancedRocketryBlocks.blockChemicalReactor = new BlockMultiblockMachine(TileChemicalReactor.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("chemreactor").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockChemicalReactor).setFrontTexture("Advancedrocketry:Crystallizer", "Advancedrocketry:Crystallizer_active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockChemicalReactor).setTopTexture("libvulpes:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockChemicalReactor).setSideTexture("libvulpes:machineGeneric");

		AdvancedRocketryBlocks.blockLathe = new BlockMultiblockMachine(TileLathe.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("lathe").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockLathe).setFrontTexture("Advancedrocketry:controlPanel");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockLathe).setSideTexture("libvulpes:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockLathe).setTopTexture("libvulpes:machineGeneric");

		AdvancedRocketryBlocks.blockRollingMachine = new BlockMultiblockMachine(TileRollingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("rollingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockRollingMachine).setFrontTexture("Advancedrocketry:controlPanel");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockRollingMachine).setSideTexture("libvulpes:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockRollingMachine).setTopTexture("libvulpes:machineGeneric");

		AdvancedRocketryBlocks.blockElectrolyser = new BlockMultiblockMachine(TileElectrolyser.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("electrolyser").setCreativeTab(tabAdvRocketry).setHardness(3f);

		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockElectrolyser).setFrontTexture("Advancedrocketry:machineElectrolzyer", "Advancedrocketry:machineElectrolzyer_active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockElectrolyser).setSideTexture("libvulpes:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockElectrolyser).setTopTexture("libvulpes:machineGeneric");

		AdvancedRocketryBlocks.blockPlanetAnalyser = new BlockMultiblockMachine(TilePlanetAnalyser.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setBlockName("planetanalyser").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPlanetAnalyser).setTopTexture("libvulpes:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPlanetAnalyser).setSideTexture("libvulpes:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPlanetAnalyser).setFrontTexture("advancedrocketry:MonitorPlanet","advancedrocketry:MonitorPlanet_active");

		AdvancedRocketryBlocks.blockObservatory = (BlockMultiblockMachine) new BlockMultiblockMachine(TileObservatory.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("observatory").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockObservatory).setTopTexture("libvulpes:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockObservatory).setSideTexture("libvulpes:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockObservatory).setFrontTexture("advancedrocketry:MonitorFrontMid","advancedrocketry:MonitorFrontMid");

		AdvancedRocketryBlocks.blockGuidanceComputer = new BlockTile(TileGuidanceComputer.class,GuiHandler.guiId.MODULAR.ordinal()).setBlockName("guidanceComputer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer).setTopTexture("libvulpes:machineGeneric", "libvulpes:machineGeneric");
		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer).setSideTexture("Advancedrocketry:MonitorSide");
		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer).setFrontTexture("Advancedrocketry:guidanceComputer");

		AdvancedRocketryBlocks.blockPlanetSelector = new BlockTile(TilePlanetSelector.class,GuiHandler.guiId.MODULARFULLSCREEN.ordinal()).setBlockName("planetSelector").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector).setTopTexture("libvulpes:machineGeneric", "libvulpes:machineGeneric");
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector).setSideTexture("Advancedrocketry:MonitorSide");
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector).setFrontTexture("Advancedrocketry:guidanceComputer");

		AdvancedRocketryBlocks.blockDrill = new BlockMiningDrill().setBlockName("drill").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile)AdvancedRocketryBlocks.blockDrill).setTopTexture("Advancedrocketry:laserBottom", "Advancedrocketry:laserBottom");
		((BlockTile)AdvancedRocketryBlocks.blockDrill).setSideTexture("Advancedrocketry:machineWarning");
		((BlockTile)AdvancedRocketryBlocks.blockDrill).setFrontTexture("Advancedrocketry:machineWarning");
		
		AdvancedRocketryBlocks.blockSuitWorkStation = new BlockTile(TileSuitWorkStation.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("suitWorkStation").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile)AdvancedRocketryBlocks.blockSuitWorkStation).setTopTexture("Advancedrocketry:suitWorkStation");
		((BlockTile)AdvancedRocketryBlocks.blockSuitWorkStation).setSideTexture("Advancedrocketry:panelSideWorkStation");
		((BlockTile)AdvancedRocketryBlocks.blockSuitWorkStation).setFrontTexture("Advancedrocketry:panelSideWorkStation");
		
		
		AdvancedRocketryBlocks.blockSolarPanel = new BlockSolarPanel(Material.iron).setBlockName("solarPanel").setCreativeTab(tabAdvRocketry).setHardness(3f);

		if(zmaster587.advancedRocketry.api.Configuration.enableLaserDrill) {
			AdvancedRocketryBlocks.blockSpaceLaser = new BlockLaser();
			AdvancedRocketryBlocks.blockSpaceLaser.setCreativeTab(tabAdvRocketry);
		}
			
		
		//Fluid Registration
		AdvancedRocketryFluids.fluidOxygen = new FluidColored("oxygen",0x8f94b9).setUnlocalizedName("oxygen").setGaseous(true);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidOxygen))
		{
			AdvancedRocketryFluids.fluidOxygen = FluidRegistry.getFluid("oxygen");
		}

		AdvancedRocketryFluids.fluidHydrogen = new FluidColored("hydrogen",0xdbc1c1).setUnlocalizedName("hydrogen").setGaseous(true);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidHydrogen))
		{
			AdvancedRocketryFluids.fluidHydrogen = FluidRegistry.getFluid("hydrogen");
		}

		AdvancedRocketryFluids.fluidRocketFuel = new FluidColored("rocketFuel", 0xe5d884).setUnlocalizedName("rocketFuel").setGaseous(true);
		if(!FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidRocketFuel))
		{
			AdvancedRocketryFluids.fluidRocketFuel = FluidRegistry.getFluid("rocketFuel");
		}

		AdvancedRocketryBlocks.blockOxygenFluid = new BlockFluid(AdvancedRocketryFluids.fluidOxygen, Material.water).setBlockName("oxygenFluidBlock").setCreativeTab(CreativeTabs.tabMisc);
		AdvancedRocketryBlocks.blockHydrogenFluid = new BlockFluid(AdvancedRocketryFluids.fluidHydrogen, Material.water).setBlockName("hydrogenFluidBlock").setCreativeTab(CreativeTabs.tabMisc);
		AdvancedRocketryBlocks.blockFuelFluid = new BlockFluid(AdvancedRocketryFluids.fluidRocketFuel, Material.water).setBlockName("rocketFuelBlock").setCreativeTab(CreativeTabs.tabMisc);

		//Cables
		//AdvancedRocketryBlocks.blockFluidPipe = new BlockLiquidPipe(Material.iron).setBlockName("liquidPipe").setCreativeTab(CreativeTabs.tabTransport);
		AdvancedRocketryBlocks.blockDataPipe = new BlockDataCable(Material.iron).setBlockName("dataPipe").setCreativeTab(tabAdvRocketry).setBlockTextureName("AdvancedRocketry:pipeData");

		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockDataPipe , AdvancedRocketryBlocks.blockDataPipe .getUnlocalizedName());
		//GameRegistry.registerBlock(AdvancedRocketryBlocks.blockFluidPipe , AdvancedRocketryBlocks.blockFluidPipe .getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockLaunchpad, "launchpad");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockRocketBuilder, "rocketBuilder");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockStructureTower, "structureTower");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockGenericSeat, "seat");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockEngine, "rocketmotor");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockFuelTank, "fuelTank");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockFuelingStation, "fuelingStation");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockMonitoringStation, "blockMonitoringStation");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockSatelliteBuilder, "blockSatelliteBuilder");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockMoonTurf, "moonTurf");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockHotTurf, "blockHotTurf");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockLoader, ItemBlockMeta.class, AdvancedRocketryBlocks.blockLoader.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockPrecisionAssembler, "precisionassemblingmachine");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockBlastBrick, "utilBlock");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockQuartzCrucible, "quartzcrucible");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockCrystallizer, "crystallizer");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockCuttingMachine, "cuttingMachine");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockAlienWood, "alienWood");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockAlienLeaves, "alienLeaves");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockAlienSapling, "alienSapling");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockObservatory, "observatory");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockConcrete, AdvancedRocketryBlocks.blockConcrete.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockPlanetSelector, AdvancedRocketryBlocks.blockPlanetSelector.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockSatelliteControlCenter, AdvancedRocketryBlocks.blockSatelliteControlCenter.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockPlanetAnalyser, AdvancedRocketryBlocks.blockPlanetAnalyser.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockGuidanceComputer, AdvancedRocketryBlocks.blockGuidanceComputer.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockArcFurnace, AdvancedRocketryBlocks.blockArcFurnace.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockSawBlade, AdvancedRocketryBlocks.blockSawBlade.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockMotor, AdvancedRocketryBlocks.blockMotor.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockLathe, AdvancedRocketryBlocks.blockLathe.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockRollingMachine, AdvancedRocketryBlocks.blockRollingMachine.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockPlatePress, AdvancedRocketryBlocks.blockPlatePress .getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockStationBuilder, AdvancedRocketryBlocks.blockStationBuilder.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockElectrolyser, AdvancedRocketryBlocks.blockElectrolyser.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockChemicalReactor, AdvancedRocketryBlocks.blockChemicalReactor.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOxygenScrubber, AdvancedRocketryBlocks.blockOxygenScrubber.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOxygenVent, AdvancedRocketryBlocks.blockOxygenVent.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOxygenCharger, AdvancedRocketryBlocks.blockOxygenCharger.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockAirLock, AdvancedRocketryBlocks.blockAirLock.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockLandingPad, AdvancedRocketryBlocks.blockLandingPad.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockWarpCore, AdvancedRocketryBlocks.blockWarpCore.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockWarpShipMonitor, AdvancedRocketryBlocks.blockWarpShipMonitor.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOxygenDetection, AdvancedRocketryBlocks.blockOxygenDetection.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockUnlitTorch, AdvancedRocketryBlocks.blockUnlitTorch.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blocksGeode,AdvancedRocketryBlocks.blocksGeode.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOxygenFluid,ItemFluid.class, AdvancedRocketryBlocks.blockOxygenFluid.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockHydrogenFluid,ItemFluid.class, AdvancedRocketryBlocks.blockHydrogenFluid.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockFuelFluid, ItemFluid.class, AdvancedRocketryBlocks.blockFuelFluid.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockVitrifiedSand, AdvancedRocketryBlocks.blockVitrifiedSand.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockCharcoalLog, AdvancedRocketryBlocks.blockCharcoalLog.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockElectricMushroom, AdvancedRocketryBlocks.blockElectricMushroom.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockCrystal, ItemCrystalBlock.class, AdvancedRocketryBlocks.blockCrystal.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOrientationController, AdvancedRocketryBlocks.blockOrientationController.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockGravityController, AdvancedRocketryBlocks.blockGravityController.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockDrill, AdvancedRocketryBlocks.blockDrill.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockMicrowaveReciever, AdvancedRocketryBlocks.blockMicrowaveReciever.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockLightSource, AdvancedRocketryBlocks.blockLightSource.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockSolarPanel, AdvancedRocketryBlocks.blockSolarPanel.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockSuitWorkStation, AdvancedRocketryBlocks.blockSuitWorkStation.getUnlocalizedName());
		
		//TODO, use different mechanism to enable/disable drill
		if(zmaster587.advancedRocketry.api.Configuration.enableLaserDrill)
			GameRegistry.registerBlock(AdvancedRocketryBlocks.blockSpaceLaser, "laserController");

		


		//Items -------------------------------------------------------------------------------------
		AdvancedRocketryItems.itemWafer = new ItemIngredient(1).setUnlocalizedName("advancedrocketry:wafer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCircuitPlate = new ItemIngredient(2).setUnlocalizedName("advancedrocketry:circuitplate").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemIC = new ItemIngredient(6).setUnlocalizedName("advancedrocketry:circuitIC").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemMisc = new ItemIngredient(2).setUnlocalizedName("advancedrocketry:miscpart").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSawBlade = new ItemIngredient(1).setUnlocalizedName("advancedrocketry:sawBlade").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStationChip = new ItemStationChip().setUnlocalizedName("stationChip").setTextureName("advancedRocketry:stationIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemAsteroidChip = new ItemAsteroidChip().setUnlocalizedName("asteroidChip").setTextureName("advancedRocketry:stationIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStation = new ItemPackedStructure().setUnlocalizedName("station").setTextureName("advancedRocketry:SpaceStation");
		AdvancedRocketryItems.itemSmallAirlockDoor = new ItemDoor2(Material.rock).setUnlocalizedName("smallAirlock").setTextureName("advancedRocketry:smallAirlock").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCarbonScrubberCartridge = new Item().setMaxDamage(172800).setUnlocalizedName("carbonScrubberCartridge").setTextureName("advancedRocketry:carbonCartridge").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemLens = new ItemIngredient(1).setUnlocalizedName("advancedrocketry:lens").setCreativeTab(LibVulpes.tabLibVulpesOres);
		AdvancedRocketryItems.itemSatellitePowerSource = new ItemIngredient(2).setUnlocalizedName("advancedrocketry:satellitePowerSource").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSatellitePrimaryFunction = new ItemIngredient(5).setUnlocalizedName("advancedrocketry:satellitePrimaryFunction").setCreativeTab(tabAdvRocketry);


		//TODO: move registration in the case we have more than one chip type
		AdvancedRocketryItems.itemDataUnit = new ItemData().setUnlocalizedName("advancedrocketry:dataUnit").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemOreScanner = new ItemOreScanner().setUnlocalizedName("OreScanner").setTextureName("advancedRocketry:oreScanner").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemQuartzCrucible = (new ItemBlockWithIcon(AdvancedRocketryBlocks.blockQuartzCrucible)).setUnlocalizedName("qcrucible").setCreativeTab(tabAdvRocketry).setTextureName("advancedRocketry:qcrucible");
		AdvancedRocketryItems.itemSatellite = new ItemSatellite().setUnlocalizedName("satellite").setTextureName("advancedRocketry:satellite");
		AdvancedRocketryItems.itemSatelliteIdChip = new ItemSatelliteIdentificationChip().setUnlocalizedName("satelliteIdChip").setTextureName("advancedRocketry:satelliteIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemPlanetIdChip = new ItemPlanetIdentificationChip().setUnlocalizedName("planetIdChip").setTextureName("advancedRocketry:planetIdChip").setCreativeTab(tabAdvRocketry);
		
		//Fluids
		AdvancedRocketryItems.itemBucketRocketFuel = new ItemBucket(AdvancedRocketryBlocks.blockFuelFluid).setCreativeTab(LibVulpes.tabLibVulpesOres).setUnlocalizedName("bucketRocketFuel").setTextureName("advancedRocketry:bucket_liquid").setContainerItem(Items.bucket);

		//Suit Component Registration
		AdvancedRocketryItems.itemJetpack = new ItemJetpack().setCreativeTab(tabAdvRocketry).setUnlocalizedName("jetPack").setTextureName("advancedRocketry:jetpack");
		AdvancedRocketryItems.itemPressureTank = new ItemPressureTank(4, 1000).setCreativeTab(tabAdvRocketry).setUnlocalizedName("advancedrocketry:pressureTank").setTextureName("advancedRocketry:pressureTank");
		AdvancedRocketryItems.itemUpgrade = new ItemUpgrade(4).setCreativeTab(tabAdvRocketry).setUnlocalizedName("advancedrocketry:itemUpgrade").setTextureName("advancedRocketry:itemUpgrade");
		
		//Armor registration
		AdvancedRocketryItems.itemSpaceSuit_Helmet = new ItemSpaceArmor(AdvancedRocketryItems.spaceSuit, 0).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceHelmet").setTextureName("advancedRocketry:space_helmet");
		AdvancedRocketryItems.itemSpaceSuit_Chest = new ItemSpaceArmor(AdvancedRocketryItems.spaceSuit, 1).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceChest").setTextureName("advancedRocketry:space_chestplate");
		AdvancedRocketryItems.itemSpaceSuit_Leggings = new ItemSpaceArmor(AdvancedRocketryItems.spaceSuit, 2).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceLeggings").setTextureName("advancedRocketry:space_leggings");
		AdvancedRocketryItems.itemSpaceSuit_Boots = new ItemSpaceArmor(AdvancedRocketryItems.spaceSuit, 3).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceBoots").setTextureName("advancedRocketry:space_boots");

		AdvancedRocketryItems.itemSealDetector = new ItemSealDetector().setMaxStackSize(1).setCreativeTab(tabAdvRocketry).setUnlocalizedName("sealDetector").setTextureName("advancedRocketry:seal_detector");

		//Tools
		AdvancedRocketryItems.itemJackhammer = new ItemJackHammer(ToolMaterial.EMERALD).setTextureName("advancedRocketry:jackHammer").setUnlocalizedName("jackhammer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemJackhammer.setHarvestLevel("jackhammer", 3);
		AdvancedRocketryItems.itemJackhammer.setHarvestLevel("pickaxe", 3);
		
		//Register Satellite Properties
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOptical.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteComposition.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 2), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMassScanner.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 3), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteEnergy.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 4), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOreMapping.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0), new SatelliteProperties().setPowerGeneration(1));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,1), new SatelliteProperties().setPowerGeneration(10));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(LibVulpesItems.itemBattery, 1, 0), new SatelliteProperties().setPowerStorage(100));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), new SatelliteProperties().setMaxData(1000));


		//Item Registration
		GameRegistry.registerItem(AdvancedRocketryItems.itemQuartzCrucible, "iquartzcrucible");
		GameRegistry.registerItem(AdvancedRocketryItems.itemOreScanner, "oreScanner");
		GameRegistry.registerItem(AdvancedRocketryItems.itemSatellitePowerSource, AdvancedRocketryItems.itemSatellitePowerSource.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSatellitePrimaryFunction, AdvancedRocketryItems.itemSatellitePrimaryFunction.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemCircuitPlate, AdvancedRocketryItems.itemCircuitPlate.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemIC, AdvancedRocketryItems.itemIC.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemWafer, AdvancedRocketryItems.itemWafer.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemDataUnit, AdvancedRocketryItems.itemDataUnit.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSatellite, AdvancedRocketryItems.itemSatellite.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSatelliteIdChip, AdvancedRocketryItems.itemSatelliteIdChip.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemPlanetIdChip,AdvancedRocketryItems.itemPlanetIdChip.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemMisc, AdvancedRocketryItems.itemMisc.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSawBlade, AdvancedRocketryItems.itemSawBlade.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSpaceStationChip, AdvancedRocketryItems.itemSpaceStationChip.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSpaceStation, AdvancedRocketryItems.itemSpaceStation.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSpaceSuit_Helmet, AdvancedRocketryItems.itemSpaceSuit_Helmet.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSpaceSuit_Boots, AdvancedRocketryItems.itemSpaceSuit_Boots.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSpaceSuit_Chest, AdvancedRocketryItems.itemSpaceSuit_Chest.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSpaceSuit_Leggings, AdvancedRocketryItems.itemSpaceSuit_Leggings.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemBucketRocketFuel, AdvancedRocketryItems.itemBucketRocketFuel.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSmallAirlockDoor, AdvancedRocketryItems.itemSmallAirlockDoor.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemCarbonScrubberCartridge, AdvancedRocketryItems.itemCarbonScrubberCartridge.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSealDetector, AdvancedRocketryItems.itemSealDetector.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemJackhammer, AdvancedRocketryItems.itemJackhammer.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemAsteroidChip, AdvancedRocketryItems.itemAsteroidChip.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemLens, AdvancedRocketryItems.itemLens.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemJetpack, AdvancedRocketryItems.itemJetpack.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemPressureTank, AdvancedRocketryItems.itemPressureTank.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemUpgrade, AdvancedRocketryItems.itemUpgrade.getUnlocalizedName());
		
		//Register multiblock items with the projector
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCuttingMachine(), (BlockTile)AdvancedRocketryBlocks.blockCuttingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileLathe(), (BlockTile)AdvancedRocketryBlocks.blockLathe);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileCrystallizer(), (BlockTile)AdvancedRocketryBlocks.blockCrystallizer);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TilePrecisionAssembler(), (BlockTile)AdvancedRocketryBlocks.blockPrecisionAssembler);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileObservatory(), (BlockTile)AdvancedRocketryBlocks.blockObservatory);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TilePlanetAnalyser(), (BlockTile)AdvancedRocketryBlocks.blockPlanetAnalyser);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileRollingMachine(), (BlockTile)AdvancedRocketryBlocks.blockRollingMachine);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectricArcFurnace(), (BlockTile)AdvancedRocketryBlocks.blockArcFurnace);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileElectrolyser(), (BlockTile)AdvancedRocketryBlocks.blockElectrolyser);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileChemicalReactor(), (BlockTile)AdvancedRocketryBlocks.blockChemicalReactor);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileWarpCore(), (BlockTile)AdvancedRocketryBlocks.blockWarpCore);
		((ItemProjector)LibVulpesItems.itemHoloProjector).registerMachine(new TileMicrowaveReciever(), (BlockTile)AdvancedRocketryBlocks.blockMicrowaveReciever);

		//End Items

		//Entity Registration ---------------------------------------------------------------------------------------------
		EntityRegistry.registerModEntity(EntityDummy.class, "mountDummy", 0, this, 16, 20, false);
		EntityRegistry.registerModEntity(EntityRocket.class, "rocket", 1, this, 64, 3, true);
		EntityRegistry.registerModEntity(EntityLaserNode.class, "laserNode", 2, instance, 256, 20, false);

		//TileEntity Registration ---------------------------------------------------------------------------------------------
		GameRegistry.registerTileEntity(TileRocketBuilder.class, "ARrocketBuilder");
		GameRegistry.registerTileEntity(TileWarpCore.class, "ARwarpCore");
		GameRegistry.registerTileEntity(TileModelRender.class, "ARmodelRenderer");
		GameRegistry.registerTileEntity(TileEntityFuelingStation.class, "ARfuelingStation");
		GameRegistry.registerTileEntity(TileEntityMoniteringStation.class, "ARmonitoringStation");
		GameRegistry.registerTileEntity(TileMissionController.class, "ARmissionControlComp");
		GameRegistry.registerTileEntity(TileSpaceLaser.class, "ARspaceLaser");
		GameRegistry.registerTileEntity(TilePrecisionAssembler.class, "ARprecisionAssembler");
		GameRegistry.registerTileEntity(TileObservatory.class, "ARobservatory");
		GameRegistry.registerTileEntity(TileCrystallizer.class, "ARcrystallizer");
		GameRegistry.registerTileEntity(TileCuttingMachine.class, "ARcuttingmachine");
		GameRegistry.registerTileEntity(TileDataBus.class, "ARdataBus");
		GameRegistry.registerTileEntity(TileSatelliteHatch.class, "ARsatelliteHatch");
		GameRegistry.registerTileEntity(TileSatelliteBuilder.class, "ARsatelliteBuilder");
		GameRegistry.registerTileEntity(TileEntitySatelliteControlCenter.class, "ARTileEntitySatelliteControlCenter");
		GameRegistry.registerTileEntity(TilePlanetAnalyser.class, "ARplanetAnalyser");
		GameRegistry.registerTileEntity(TileGuidanceComputer.class, "ARguidanceComputer");
		GameRegistry.registerTileEntity(TileElectricArcFurnace.class, "ARelectricArcFurnace");
		GameRegistry.registerTileEntity(TilePlanetSelector.class, "ARTilePlanetSelector");
		GameRegistry.registerTileEntity(TileModelRenderRotatable.class, "ARTileModelRenderRotatable");
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
		GameRegistry.registerTileEntity(TileDrill.class, "ARDrill");
		GameRegistry.registerTileEntity(TileMicrowaveReciever.class, "ARMicrowaveReciever");
		GameRegistry.registerTileEntity(TileSuitWorkStation.class, "ARSuitWorkStation");
		GameRegistry.registerTileEntity(TileRocketLoader.class, "ARRocketLoader");
		GameRegistry.registerTileEntity(TileRocketUnloader.class, "ARRocketUnloader");



		//OreDict stuff
		OreDictionary.registerOre("waferSilicon", new ItemStack(AdvancedRocketryItems.itemWafer,1,0));
		OreDictionary.registerOre("ingotCarbon", new ItemStack(AdvancedRocketryItems.itemMisc, 1, 1));
		OreDictionary.registerOre("concrete", new ItemStack(AdvancedRocketryBlocks.blockConcrete));
		
		
		//MOD-SPECIFIC ENTRIES --------------------------------------------------------------------------------------------------------------------------


		//Register Space Objects
		SpaceObjectManager.getSpaceManager().registerSpaceObjectType("genericObject", SpaceObject.class);
		
		//Register Allowed Products
		

	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{

		zmaster587.advancedRocketry.cable.NetworkRegistry.registerFluidNetwork();
		ItemStack userInterface = new ItemStack(AdvancedRocketryItems.itemMisc, 1,0);
		ItemStack basicCircuit = new ItemStack(AdvancedRocketryItems.itemIC, 1,0);
		ItemStack advancedCircuit = new ItemStack(AdvancedRocketryItems.itemIC, 1,2);
		ItemStack controlCircuitBoard =  new ItemStack(AdvancedRocketryItems.itemIC,1,3);
		ItemStack itemIOBoard = new ItemStack(AdvancedRocketryItems.itemIC,1,4);
		ItemStack liquidIOBoard = new ItemStack(AdvancedRocketryItems.itemIC,1,5);
		ItemStack trackingCircuit = new ItemStack(AdvancedRocketryItems.itemIC,1,1);
		ItemStack opticalSensor = new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0);
		ItemStack smallSolarPanel =  new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0); 
		ItemStack largeSolarPanel = new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,1);

		//Register Alloys
		MaterialRegistry.registerMixedMaterial(new MixedMaterial(TileElectricArcFurnace.class, "oreRutile", new ItemStack[] {Materials.TITANIUM.getProduct(AllowedProducts.getProductByName("INGOT"))}));

		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockBlastBrick,16), new ItemStack(Items.potionitem,1,8195), new ItemStack(Items.potionitem,1,8201), Blocks.brick_block, Blocks.brick_block, Blocks.brick_block, Blocks.brick_block);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockArcFurnace), "aga","ice", "aba", 'a', Items.netherbrick, 'g', userInterface, 'i', itemIOBoard, 'e',controlCircuitBoard, 'c', AdvancedRocketryBlocks.blockBlastBrick, 'b', "ingotCopper"));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemQuartzCrucible), " a ", "aba", " a ", Character.valueOf('a'), Items.quartz, Character.valueOf('b'), Items.cauldron);
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockPlatePress, "   ", " a ", "iii", 'a', Blocks.piston, 'i', Items.iron_ingot));
		GameRegistry.addRecipe(new ShapedOreRecipe(MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.getProductByName("STICK")), "x  ", " x ", "  x", 'x', "ingotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(MaterialRegistry.getItemStackFromMaterialAndType(Materials.STEEL, AllowedProducts.getProductByName("STICK")), "x  ", " x ", "  x", 'x', "ingotSteel"));
		GameRegistry.addSmelting(Materials.DILITHIUM.getProduct(AllowedProducts.getProductByName("ORE")), Materials.DILITHIUM.getProduct(AllowedProducts.getProductByName("DUST")), 0);


		//Supporting Materials
		GameRegistry.addRecipe(new ShapedOreRecipe(userInterface, "lrl", "fgf", 'l', "dyeLime", 'r', Items.redstone, 'g', Blocks.glass_pane, 'f', Items.glowstone_dust));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockGenericSeat), "xxx", 'x', Blocks.wool);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockConcrete), Blocks.sand, Blocks.gravel, Items.water_bucket);
		GameRegistry.addRecipe(new ShapelessOreRecipe(AdvancedRocketryBlocks.blockLaunchpad, "concrete", "dyeBlack", "dyeYellow"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockStructureTower, "ooo", " o ", "ooo", 'o', "stickSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockEngine, "sss", " t ","t t", 's', "ingotSteel", 't', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockFuelTank, "s s", "p p", "s s", 'p', "plateSteel", 's', "stickSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(LibVulpesBlocks.blockStructureBlock, "sps", "psp", "sps", 'p', "plateIron", 's', "stickIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LibVulpesItems.itemBattery,1,0), " c ","prp", "prp", 'c', "stickIron", 'r', Items.redstone, 'p', "plateTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), "ppp", " g ", " l ", 'p', Blocks.glass_pane, 'g', Items.glowstone_dust, 'l', "plateGold"));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockObservatory), "gug", "pbp", "rrr", 'g', Blocks.glass_pane, 'u', userInterface, 'b', LibVulpesBlocks.blockStructureBlock, 'r', MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.getProductByName("STICK")));

		//Hatches
		GameRegistry.addShapedRecipe(new ItemStack(LibVulpesBlocks.blockHatch,1,0), "c", "m"," ", 'c', Blocks.chest, 'm', LibVulpesBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(LibVulpesBlocks.blockHatch,1,1), "m", "c"," ", 'c', Blocks.chest, 'm', LibVulpesBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(LibVulpesBlocks.blockHatch,1,2), "c", "m", " ", 'c', AdvancedRocketryBlocks.blockFuelTank, 'm', LibVulpesBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(LibVulpesBlocks.blockHatch,1,3), "m", "c", " ", 'c', AdvancedRocketryBlocks.blockFuelTank, 'm', LibVulpesBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,0), "m", "c"," ", 'c', AdvancedRocketryItems.itemDataUnit, 'm', LibVulpesBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,1), " x ", "xmx"," x ", 'x', "stickTitanium", 'm', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,2), new ItemStack(LibVulpesBlocks.blockHatch,1,1), trackingCircuit);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockLoader,1,3), new ItemStack(LibVulpesBlocks.blockHatch,1,0), trackingCircuit);


		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockMotor), " cp", "rrp"," cp", 'c', "coilCopper", 'p', "plateSteel", 'r', "stickSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0), "rrr", "ggg","ppp", 'r', Items.redstone, 'g', Items.glowstone_dust, 'p', "plateGold"));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LibVulpesItems.itemHoloProjector), "oro", "rpr", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'r', Items.redstone, 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 2), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', basicCircuit, 'd', "crystalDilithium"));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 1), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', basicCircuit, 'd', trackingCircuit);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 3), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemLens, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', basicCircuit, 'd', trackingCircuit);

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0), " x ","xox", " x ", 'x', "plateIron", 'o', "stickIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSawBlade,1,0), "r r","xox", "x x", 'r', "stickIron", 'x', "plateIron", 'o', new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0)));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSpaceStationChip), LibVulpes.itemLinker , basicCircuit);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge), "xix", "xix", "xix", 'x', "sheetIron", 'i', Blocks.iron_bars));


		//Plugs
		GameRegistry.addShapedRecipe(new ItemStack(LibVulpesBlocks.blockRFBattery), " x ", "xmx"," x ", 'x', LibVulpesItems.itemBattery, 'm', LibVulpesBlocks.blockStructureBlock);


		//O2 Support
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenVent), "bfb", "bmb", "btb", 'b', Blocks.iron_bars, 'f', "fanSteel", 'm', AdvancedRocketryBlocks.blockMotor, 't', AdvancedRocketryBlocks.blockFuelTank));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenScrubber), "bfb", "bmb", "btb", 'b', Blocks.iron_bars, 'f', "fanSteel", 'm', AdvancedRocketryBlocks.blockMotor, 't', "ingotCarbon"));

		//MACHINES
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockPrecisionAssembler), "abc", "def", "ghi", 'a', Items.repeater, 'b', userInterface, 'c', Items.diamond, 'd', itemIOBoard, 'e', LibVulpesBlocks.blockStructureBlock, 'f', controlCircuitBoard, 'g', Blocks.furnace, 'h', "gearSteel", 'i', Blocks.dropper));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockCrystallizer), "ada", "ecf","bgb", 'a', Items.quartz, 'b', Items.repeater, 'c', LibVulpesBlocks.blockStructureBlock, 'd', userInterface, 'e', itemIOBoard, 'f', controlCircuitBoard, 'g', "plateSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockCuttingMachine), "aba", "cde", "opo", 'a', "gearSteel", 'b', userInterface, 'c', itemIOBoard, 'e', controlCircuitBoard, 'p', "plateSteel", 'o', Blocks.obsidian, 'd', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockLathe), "rsr", "abc", "pgp", 'r', "stickIron",'a', itemIOBoard, 'c', controlCircuitBoard, 'g', "gearSteel", 'p', "plateSteel", 'b', LibVulpesBlocks.blockStructureBlock, 's', userInterface));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockRollingMachine), "psp", "abc", "iti", 'a', itemIOBoard, 'c', controlCircuitBoard, 'p', "gearSteel", 's', userInterface, 'b', LibVulpesBlocks.blockStructureBlock, 'i', "blockIron",'t', liquidIOBoard));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockMonitoringStation), "coc", "cbc", "cpc", 'c', "stickCopper", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'b', LibVulpesBlocks.blockStructureBlock, 'p', LibVulpesItems.itemBattery));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockFuelingStation), "bgb", "lbf", "ppp", 'p', "plateTin", 'f', "fanSteel", 'l', liquidIOBoard, 'g', AdvancedRocketryItems.itemMisc, 'x', AdvancedRocketryBlocks.blockFuelTank, 'b', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSatelliteControlCenter), "oso", "cbc", "rtr", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 's', userInterface, 'c', "stickCopper", 'b', LibVulpesBlocks.blockStructureBlock, 'r', Items.repeater, 't', LibVulpesItems.itemBattery));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSatelliteBuilder), "dht", "cbc", "mas", 'd', AdvancedRocketryItems.itemDataUnit, 'h', Blocks.hopper, 'c', basicCircuit, 'b', LibVulpesBlocks.blockStructureBlock, 'm', AdvancedRocketryBlocks.blockMotor, 'a', Blocks.anvil, 's', AdvancedRocketryBlocks.blockSawBlade, 't', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockPlanetAnalyser), "tst", "pbp", "cpc", 't', trackingCircuit, 's', userInterface, 'b', LibVulpesBlocks.blockStructureBlock, 'p', "plateTin", 'c', AdvancedRocketryItems.itemPlanetIdChip));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockGuidanceComputer), "ctc", "rbr", "crc", 'c', trackingCircuit, 't', "plateTitanium", 'r', Items.redstone, 'b', LibVulpesBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockPlanetSelector), "cpc", "lbl", "coc", 'c', trackingCircuit, 'o',new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'l', Blocks.lever, 'b', AdvancedRocketryBlocks.blockGuidanceComputer, 'p', Blocks.stone_button));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockRocketBuilder), "sgs", "cbc", "tdt", 's', "stickTitanium", 'g', AdvancedRocketryItems.itemMisc, 'c', controlCircuitBoard, 'b', LibVulpesBlocks.blockStructureBlock, 't', "gearTitanium", 'd', AdvancedRocketryBlocks.blockConcrete));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockStationBuilder), "gdg", "dsd", "ada", 'g', "gearTitanium", 'a', advancedCircuit, 'd', "dustDilithium", 's', new ItemStack(AdvancedRocketryBlocks.blockRocketBuilder)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockElectrolyser), "pip", "abc", "ded", 'd', basicCircuit, 'p', "plateSteel", 'i', userInterface, 'a', liquidIOBoard, 'c', controlCircuitBoard, 'b', LibVulpesBlocks.blockStructureBlock, 'e', Blocks.redstone_torch));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenCharger), "fif", "tbt", "pcp", 'p', "plateSteel", 'f', "fanSteel", 'c', Blocks.heavy_weighted_pressure_plate, 'i', AdvancedRocketryItems.itemMisc, 'b', LibVulpesBlocks.blockStructureBlock, 't', AdvancedRocketryBlocks.blockFuelTank));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockChemicalReactor), "pip", "abd", "rcr", 'a', itemIOBoard, 'd', controlCircuitBoard, 'r', basicCircuit, 'p', "plateGold", 'i', userInterface, 'c', liquidIOBoard, 'b', LibVulpesBlocks.blockStructureBlock, 'g', "plateGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockWarpCore), "gcg", "pbp", "gcg", 'p', "plateSteel", 'c', advancedCircuit, 'b', "coilCopper", 'g', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenDetection), "pip", "gbf", "pcp", 'p', "plateSteel",'f', "fanSteel", 'i', userInterface, 'c', basicCircuit, 'b', LibVulpesBlocks.blockStructureBlock, 'g', Blocks.iron_bars));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockWarpShipMonitor), "pip", "obo", "pcp", 'o', controlCircuitBoard, 'p', "plateSteel", 'i', userInterface, 'c', advancedCircuit, 'b', LibVulpesBlocks.blockStructureBlock));

		//Armor recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Boots, " r ", "w w", "p p", 'r', "stickIron", 'w', Blocks.wool, 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Leggings, "wrw", "w w", "w w", 'w', Blocks.wool, 'r', "stickIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Chest, "wrw", "wtw", "wfw", 'w', Blocks.wool, 'r', "stickIron", 't', AdvancedRocketryBlocks.blockFuelTank, 'f', "fanSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Helmet, "prp", "rgr", "www", 'w', Blocks.wool, 'r', "stickIron", 'p', "plateIron", 'g', Blocks.glass_pane));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemJetpack, "cpc", "lsl", "f f", 'c', AdvancedRocketryItems.itemPressureTank, 'f', Items.fire_charge, 's', Items.string, 'l', Blocks.lever, 'p', "plateSteel"));
		
		
		//Tool Recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemJackhammer, " pt","imp","di ",'d', Items.diamond, 'm', AdvancedRocketryBlocks.blockMotor, 'p', "plateBronze", 't', "stickTitanium", 'i', "stickIron"));

		//Other blocks
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSmallAirlockDoor, "pp", "pp","pp", 'p', "plateSteel"));

		//TEMP RECIPES
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemPlanetIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0), new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemMisc,1,1), new ItemStack(Items.coal,1,1), new ItemStack(Items.coal,1,1), new ItemStack(Items.coal,1,1), new ItemStack(Items.coal,1,1) ,new ItemStack(Items.coal,1,1) ,new ItemStack(Items.coal,1,1));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockLandingPad), new ItemStack(AdvancedRocketryBlocks.blockConcrete), trackingCircuit);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemAsteroidChip), trackingCircuit.copy(), AdvancedRocketryItems.itemDataUnit);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockDataPipe, 8), "ggg", " d ", "ggg", 'g', Blocks.glass_pane, 'd', AdvancedRocketryItems.itemDataUnit);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockDrill), LibVulpesBlocks.blockStructureBlock, Items.iron_pickaxe);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockOrientationController), LibVulpesBlocks.blockStructureBlock, Items.compass, userInterface);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockGravityController), LibVulpesBlocks.blockStructureBlock, Blocks.piston, Blocks.redstone_block);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemLens), " g ", "g g", 'g', Blocks.glass_pane);
		GameRegistry.addShapelessRecipe(new ItemStack(LibVulpesBlocks.blockRFBattery), new ItemStack(LibVulpesBlocks.blockRFOutput));
		GameRegistry.addShapelessRecipe(new ItemStack(LibVulpesBlocks.blockRFOutput), new ItemStack(LibVulpesBlocks.blockRFBattery));
		GameRegistry.addShapelessRecipe(largeSolarPanel.copy(), smallSolarPanel, smallSolarPanel, smallSolarPanel, smallSolarPanel, smallSolarPanel, smallSolarPanel);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockMicrowaveReciever), "ggg", "tbc", "aoa", 'g', "plateGold", 't', trackingCircuit, 'b', LibVulpesBlocks.blockStructureBlock, 'c', controlCircuitBoard, 'a', advancedCircuit, 'o', opticalSensor));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockSolarPanel, "rrr", "gbg", "ppp", 'r' , Items.redstone, 'g', Items.glowstone_dust, 'b', LibVulpesBlocks.blockStructureBlock, 'p', "plateGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemOreScanner, "lwl", "bgb", "   ", 'l', Blocks.lever, 'g', userInterface, 'b', "battery", 'w', advancedCircuit));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 4), " c ","sss", "tot", 'c', "stickCopper", 's', "sheetIron", 'o', AdvancedRocketryItems.itemOreScanner, 't', trackingCircuit));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockSuitWorkStation), "c","b", 'c', Blocks.crafting_table, 'b', LibVulpesBlocks.blockStructureBlock);
		
		
		RecipesMachine.getInstance().addRecipe(TileElectrolyser.class, new Object[] {new FluidStack(AdvancedRocketryFluids.fluidOxygen, 100), new FluidStack(AdvancedRocketryFluids.fluidHydrogen, 100)}, 100, 20, new FluidStack(FluidRegistry.WATER, 10));
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new FluidStack(AdvancedRocketryFluids.fluidRocketFuel, 20), 100, 10, new FluidStack(AdvancedRocketryFluids.fluidOxygen, 10), new FluidStack(AdvancedRocketryFluids.fluidHydrogen, 10));


		if(zmaster587.advancedRocketry.api.Configuration.enableLaserDrill) {
			GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockSpaceLaser, "ata", "bec", "gpg", 'a', advancedCircuit, 't', trackingCircuit, 'b', LibVulpesItems.itemBattery, 'e', Items.emerald, 'c', controlCircuitBoard, 'g', "gearTitanium", 'p', LibVulpesBlocks.blockStructureBlock));
		}
		
		//Control boards
		GameRegistry.addRecipe(new ShapedOreRecipe(itemIOBoard, "rvr", "dwd", "dpd", 'r', Items.redstone, 'v', Items.diamond, 'd', "dustGold", 'w', Blocks.wooden_slab, 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(controlCircuitBoard, "rvr", "dwd", "dpd", 'r', Items.redstone, 'v', Items.diamond, 'd', "dustCopper", 'w', Blocks.wooden_slab, 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(liquidIOBoard, "rvr", "dwd", "dpd", 'r', Items.redstone, 'v', Items.diamond, 'd', new ItemStack(Items.dye, 1, 4), 'w', Blocks.wooden_slab, 'p', "plateIron"));

		//Cutting Machine
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemIC, 4, 0), 300, 100, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0));
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemIC, 4, 2), 300, 100, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,1));
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemWafer, 4, 0), 300, 100, "bouleSilicon");

		//Lathe
		RecipesMachine.getInstance().addRecipe(TileLathe.class, MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.getProductByName("STICK")), 300, 100, "ingotIron");

		//Precision Assembler recipes
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), 900, 100, Items.gold_ingot, Items.redstone, "waferSilicon");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,1), 900, 100, Items.gold_ingot, Blocks.redstone_block, "waferSilicon");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), 500, 60, Items.emerald, basicCircuit, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, trackingCircuit, 900, 50, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), Items.ender_eye, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, itemIOBoard, 200, 10, "plateSilicon", "plateGold", basicCircuit, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, controlCircuitBoard, 200, 10, "plateSilicon", "plateCopper", basicCircuit, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, liquidIOBoard, 200, 10, "plateSilicon", new ItemStack(Items.dye, 1, 4), basicCircuit, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,0), 400, 1, Items.redstone, Blocks.redstone_torch, basicCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,1), 400, 1, Items.fire_charge, Items.diamond, advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,2), 400, 1, AdvancedRocketryBlocks.blockMotor, "rodTitanium", advancedCircuit, controlCircuitBoard);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemUpgrade,1,3), 400, 1, Items.leather_boots, Items.feather, advancedCircuit, controlCircuitBoard);
		
		//BlastFurnace
		RecipesMachine.getInstance().addRecipe(TileElectricArcFurnace.class, Materials.SILICON.getProduct(AllowedProducts.getProductByName("INGOT")), 12000, 1, Blocks.sand);
		RecipesMachine.getInstance().addRecipe(TileElectricArcFurnace.class, Materials.STEEL.getProduct(AllowedProducts.getProductByName("INGOT")), 6000, 1, "ingotIron", Items.coal);

		//Chemical Reactor
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new Object[] {new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge,1, 0), new ItemStack(Items.coal, 1, 1)}, 40, 20, new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge, 1, AdvancedRocketryItems.itemCarbonScrubberCartridge.getMaxDamage()));

		//Rolling Machine
		RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 0), 100, 1, "sheetIron", "sheetIron");
		RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 1), 200, 2, "sheetSteel", "sheetSteel");
		RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 2), 100, 1, "sheetAluminum", "sheetAluminum");
		RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 3), 1000, 8, "sheetTitanium", "sheetTitanium");
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new zmaster587.advancedRocketry.inventory.GuiHandler());
		planetWorldType = new WorldTypePlanetGen("PlanetCold");
		spaceWorldType = new WorldTypeSpace("Space");

		//Biomes --------------------------------------------------------------------------------------

		AdvancedRocketryBiomes.moonBiome = new BiomeGenMoon(config.get(BIOMECATETORY, "moonBiomeId", 90).getInt(), true);
		AdvancedRocketryBiomes.alienForest = new BiomeGenAlienForest(config.get(BIOMECATETORY, "alienForestBiomeId", 91).getInt(), true);
		AdvancedRocketryBiomes.hotDryBiome = new BiomeGenHotDryRock(config.get(BIOMECATETORY, "hotDryBiome", 92).getInt(), true);
		AdvancedRocketryBiomes.spaceBiome = new BiomeGenSpace(config.get(BIOMECATETORY, "spaceBiomeId", 93).getInt(), true);
		AdvancedRocketryBiomes.stormLandsBiome = new BiomeGenStormland(config.get(BIOMECATETORY, "stormLandsBiomeId", 94).getInt(), true);
		AdvancedRocketryBiomes.crystalChasms = new BiomeGenCrystal(config.get(BIOMECATETORY, "crystalChasmsBiomeId", 95).getInt(), true);
		AdvancedRocketryBiomes.swampDeepBiome = new BiomeGenDeepSwamp(config.get(BIOMECATETORY, "deepSwampBiomeId", 96).getInt(), true);
		AdvancedRocketryBiomes.marsh = new BiomeGenMarsh(config.get(BIOMECATETORY, "marsh", 97).getInt(), true);
		AdvancedRocketryBiomes.oceanSpires = new BiomeGenOceanSpires(config.get(BIOMECATETORY, "oceanSpires", 98).getInt(), true);

		config.save();

		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.alienForest);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.hotDryBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.spaceBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.stormLandsBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.crystalChasms);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.swampDeepBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.marsh);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.oceanSpires);

		//Prevent these biomes from spawning normally
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.hotDryBiome);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.alienForest);
		AdvancedRocketryBiomes.instance.registerBlackListBiome(AdvancedRocketryBiomes.spaceBiome);


		AdvancedRocketryBiomes.instance.registerHighPressureBiome(AdvancedRocketryBiomes.stormLandsBiome);
		AdvancedRocketryBiomes.instance.registerHighPressureBiome(AdvancedRocketryBiomes.swampDeepBiome);

		AdvancedRocketryBiomes.instance.registerSingleBiome(AdvancedRocketryBiomes.swampDeepBiome);
		AdvancedRocketryBiomes.instance.registerSingleBiome(AdvancedRocketryBiomes.crystalChasms);
		AdvancedRocketryBiomes.instance.registerSingleBiome(AdvancedRocketryBiomes.alienForest);
		AdvancedRocketryBiomes.instance.registerSingleBiome(BiomeGenBase.desertHills);
		AdvancedRocketryBiomes.instance.registerSingleBiome(BiomeGenBase.mushroomIsland);
		AdvancedRocketryBiomes.instance.registerSingleBiome(BiomeGenBase.extremeHillsPlus);
		AdvancedRocketryBiomes.instance.registerSingleBiome(BiomeGenBase.icePlains);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.registerEventHandlers();
		proxy.registerKeyBindings();

		//TODO: debug
		//ClientCommandHandler.instance.registerCommand(new Debugger());

		PlanetEventHandler handle = new PlanetEventHandler();
		FMLCommonHandler.instance().bus().register(handle);
		MinecraftForge.EVENT_BUS.register(handle);
		MinecraftForge.EVENT_BUS.register(new BucketHandler());

		CableTickHandler cable = new CableTickHandler();
		FMLCommonHandler.instance().bus().register(cable);
		MinecraftForge.EVENT_BUS.register(cable);
		
		InputSyncHandler inputSync = new InputSyncHandler();
		FMLCommonHandler.instance().bus().register(inputSync);
		MinecraftForge.EVENT_BUS.register(inputSync);

		if(Loader.isModLoaded("GalacticraftCore") && zmaster587.advancedRocketry.api.Configuration.overrideGCAir) {
			GalacticCraftHandler eventHandler = new GalacticCraftHandler();
			MinecraftForge.EVENT_BUS.register(eventHandler);
			if(event.getSide().isClient())
				FMLCommonHandler.instance().bus().register(eventHandler);
		}

		FMLCommonHandler.instance().bus().register(SpaceObjectManager.getSpaceManager());

		PacketHandler.init();
		FuelRegistry.instance.registerFuel(FuelType.LIQUID, AdvancedRocketryFluids.fluidRocketFuel, 1);

		GameRegistry.registerWorldGenerator(new OreGenerator(), 100);

		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());


		//AutoGenned Recipes
		for(Materials ore : Materials.values()) {
			if(AllowedProducts.getProductByName("ORE").isOfType(ore.getAllowedProducts()) && AllowedProducts.getProductByName("INGOT").isOfType(ore.getAllowedProducts()))
				GameRegistry.addSmelting(ore.getProduct(AllowedProducts.getProductByName("ORE")), ore.getProduct(AllowedProducts.getProductByName("INGOT")), 0);

			if(AllowedProducts.getProductByName("NUGGET").isOfType(ore.getAllowedProducts())) {
				ItemStack nugget = ore.getProduct(AllowedProducts.getProductByName("NUGGET"));
				nugget.stackSize = 9;
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapelessOreRecipe(nugget, AllowedProducts.getProductByName("INGOT").name().toLowerCase() + str));
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("INGOT")), "ooo", "ooo", "ooo", 'o', AllowedProducts.getProductByName("NUGGET").name().toLowerCase() + str));
				}
			}

			if(AllowedProducts.getProductByName("CRYSTAL").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, ore.getProduct(AllowedProducts.getProductByName("CRYSTAL")), 300, 200, AllowedProducts.getProductByName("DUST").name().toLowerCase() + str);
			}

			if(AllowedProducts.getProductByName("BOULE").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, ore.getProduct(AllowedProducts.getProductByName("BOULE")), 300, 200, AllowedProducts.getProductByName("INGOT").name().toLowerCase() + str, AllowedProducts.getProductByName("NUGGET").name().toLowerCase() + str);
			}

			if(AllowedProducts.getProductByName("STICK").isOfType(ore.getAllowedProducts()) && AllowedProducts.getProductByName("INGOT").isOfType(ore.getAllowedProducts())) {
				for(String name : ore.getOreDictNames())
					if(OreDictionary.doesOreNameExist(AllowedProducts.getProductByName("INGOT").name().toLowerCase() + name))
						RecipesMachine.getInstance().addRecipe(TileLathe.class, ore.getProduct(AllowedProducts.getProductByName("STICK")), 300, 200, AllowedProducts.getProductByName("INGOT").name().toLowerCase() + name); //ore.getProduct(AllowedProducts.getProductByName("INGOT")));
			}

			if(AllowedProducts.getProductByName("PLATE").isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames()) {
					if(OreDictionary.doesOreNameExist(AllowedProducts.getProductByName("INGOT").name().toLowerCase() + oreDictNames)) {
						RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, ore.getProduct(AllowedProducts.getProductByName("PLATE")), 300, 200, AllowedProducts.getProductByName("INGOT").name().toLowerCase() + oreDictNames);
						if(AllowedProducts.getProductByName("BLOCK").isOfType(ore.getAllowedProducts()) || ore.isVanilla())
							RecipesMachine.getInstance().addRecipe(BlockPress.class, ore.getProduct(AllowedProducts.getProductByName("PLATE"),3), 0, 0, AllowedProducts.getProductByName("BLOCK").name().toLowerCase() + oreDictNames);
					}
				}
			}

			if(AllowedProducts.getProductByName("SHEET").isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames()) {
					RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, ore.getProduct(AllowedProducts.getProductByName("SHEET")), 300, 200, AllowedProducts.getProductByName("PLATE").name().toLowerCase() + oreDictNames);
				}
			}

			if(AllowedProducts.getProductByName("COIL").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("COIL")), "ooo", "o o", "ooo",'o', AllowedProducts.getProductByName("INGOT").name().toLowerCase() + str));
			}

			if(AllowedProducts.getProductByName("FAN").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("FAN")), "p p", " r ", "p p", 'p', AllowedProducts.getProductByName("PLATE").name().toLowerCase() + str, 'r', AllowedProducts.getProductByName("STICK").name().toLowerCase() + str));
				}
			}
			if(AllowedProducts.getProductByName("GEAR").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("GEAR")), "sps", " r ", "sps", 'p', AllowedProducts.getProductByName("PLATE").name().toLowerCase() + str, 's', AllowedProducts.getProductByName("STICK").name().toLowerCase() + str, 'r', AllowedProducts.getProductByName("INGOT").name().toLowerCase() + str));
				}
			}
			if(AllowedProducts.getProductByName("BLOCK").isOfType(ore.getAllowedProducts())) {
				ItemStack ingot = ore.getProduct(AllowedProducts.getProductByName("INGOT"));
				ingot.stackSize = 9;
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapelessOreRecipe(ingot, AllowedProducts.getProductByName("BLOCK").name().toLowerCase() + str));
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("BLOCK")), "ooo", "ooo", "ooo", 'o', AllowedProducts.getProductByName("INGOT").name().toLowerCase() + str));
				}
			}

			if(AllowedProducts.getProductByName("DUST").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames()) {
					if(AllowedProducts.getProductByName("ORE").isOfType(ore.getAllowedProducts()) || ore.isVanilla())
						RecipesMachine.getInstance().addRecipe(BlockPress.class, ore.getProduct(AllowedProducts.getProductByName("DUST")), 0, 0, AllowedProducts.getProductByName("ORE").name().toLowerCase() + str);
				}
			}
		}

		//Handle vanilla integration
		if(zmaster587.advancedRocketry.api.Configuration.allowSawmillVanillaWood) {
			for(int i = 0; i < 4; i++) {
				RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(Blocks.planks, 6, i), 80, 10, new ItemStack(Blocks.log,1, i));
			}
			RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(Blocks.planks, 6, 4), 80, 10, new ItemStack(Blocks.log2,1, 0));
		}

		//Handle items from other mods
		if(zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods) {
			for(Entry<AllowedProducts, HashSet<String>> entry : modProducts.entrySet()) {
				if(entry.getKey() == AllowedProducts.getProductByName("PLATE")) {
					for(String str : entry.getValue()) {
						Materials material = Materials.valueOfSafe(str.toUpperCase());

						if(OreDictionary.doesOreNameExist("ingot" + str) && OreDictionary.getOres("ingot" + str).size() > 0 && (material == null || !AllowedProducts.getProductByName("PLATE").isOfType(material.getAllowedProducts())) ) {

							RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, OreDictionary.getOres("plate" + str).get(0), 300, 200, "ingot" + str);
						}
					}
				}
				else if(entry.getKey() == AllowedProducts.getProductByName("STICK")) {
					for(String str : entry.getValue()) {
						Materials material = Materials.valueOfSafe(str.toUpperCase());

						if(OreDictionary.doesOreNameExist("ingot" + str) && OreDictionary.getOres("ingot" + str).size() > 0 && (material == null || !AllowedProducts.getProductByName("STICK").isOfType(material.getAllowedProducts())) ) {

							//GT registers rods as sticks
							if(OreDictionary.doesOreNameExist("rod" + str) && OreDictionary.getOres("rod" + str).size() > 0)
								RecipesMachine.getInstance().addRecipe(TileLathe.class, OreDictionary.getOres("rod" + str).get(0), 300, 200, "ingot" + str);
							else if(OreDictionary.doesOreNameExist("stick" + str)  && OreDictionary.getOres("stick" + str).size() > 0) {
								RecipesMachine.getInstance().addRecipe(TileLathe.class, OreDictionary.getOres("stick" + str).get(0), 300, 200, "ingot" + str);
							}

						}

					}
				}
			}
		}

		//Register buckets
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockFuelFluid, AdvancedRocketryItems.itemBucketRocketFuel);
		FluidContainerRegistry.registerFluidContainer(AdvancedRocketryFluids.fluidRocketFuel, new ItemStack(AdvancedRocketryItems.itemBucketRocketFuel), new ItemStack(Items.bucket));

		//Register mixed material's recipes
		for(MixedMaterial material : MaterialRegistry.getMixedMaterialList()) {
			RecipesMachine.getInstance().addRecipe(material.getMachine(), material.getProducts(), 100, 10, material.getInput());
		}

		//Register space dimension
		net.minecraftforge.common.DimensionManager.registerProviderType(zmaster587.advancedRocketry.api.Configuration.spaceDimId, WorldProviderSpace.class, true);
		net.minecraftforge.common.DimensionManager.registerDimension(zmaster587.advancedRocketry.api.Configuration.spaceDimId,zmaster587.advancedRocketry.api.Configuration.spaceDimId);

		//Register Whitelisted Sealable Blocks

		logger.fine("Start registering sealable blocks");
		for(String str : sealableBlockWhileList) {
			Block block = Block.getBlockFromName(str);
			if(block == null)
				logger.warning("'" + str + "' is not a valid Block");
			else
				SealableBlockHandler.INSTANCE.addSealableBlock(block);
		}
		logger.fine("End registering sealable blocks");
		sealableBlockWhileList = null;
		
		//Add mappings for multiblockmachines
		//Data mapping 'D'
		
		List<BlockMeta> list = new LinkedList<BlockMeta>();
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockLoader, 0));
		list.add(new BlockMeta(AdvancedRocketryBlocks.blockLoader, 8));
		TileMultiBlock.addMapping('D', list);

	}

	@EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		for (int dimId : DimensionManager.getInstance().getLoadedDimensions()) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(dimId);
			if(!properties.isNativeDimension) {
				if(properties.getId() != zmaster587.advancedRocketry.api.Configuration.MoonId)
					DimensionManager.getInstance().deleteDimension(properties.getId());
				else if (!Loader.isModLoaded("GalacticraftCore"))
					properties.isNativeDimension = true;
			}
		}
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new WorldCommand());

		if(Loader.isModLoaded("GalacticraftCore") ) {
			zmaster587.advancedRocketry.api.Configuration.MoonId = ConfigManagerCore.idDimensionMoon;
			OreGenerator.setDilithiumTargetBlock(GCBlocks.blockMoon);
		}
		else
			OreGenerator.setDilithiumTargetBlock(Blocks.stone);

		//Register hard coded dimensions
		if(!zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().loadDimensions(zmaster587.advancedRocketry.dimension.DimensionManager.filePath)) {
			int numRandomGeneratedPlanets = 6;
			File file = new File("./config/" + zmaster587.advancedRocketry.api.Configuration.configFolder + "/planetDefs.xml");
			logger.info("Checking for config at " + file.getAbsolutePath());
			if(file.exists()) {
				logger.info("File found!");
				XMLPlanetLoader loader = new XMLPlanetLoader();
				try {
					loader.loadFile(file);
					List<DimensionProperties> list = loader.readAllPlanets();
					for(DimensionProperties properties : list)
						DimensionManager.getInstance().registerDim(properties, true);
					numRandomGeneratedPlanets = loader.getMaxNumPlanets();

				} catch(IOException e) {
					logger.severe("XML planet config exists but cannot be loaded!  Defaulting to random gen.");
				}
			}

			if(zmaster587.advancedRocketry.api.Configuration.MoonId == -1)
				zmaster587.advancedRocketry.api.Configuration.MoonId = DimensionManager.getInstance().getNextFreeDim();

			DimensionProperties dimensionProperties = new DimensionProperties(zmaster587.advancedRocketry.api.Configuration.MoonId);
			dimensionProperties.atmosphereDensity = 0;
			dimensionProperties.averageTemperature = 20;
			dimensionProperties.gravitationalMultiplier = .166f; //Actual moon value
			dimensionProperties.setName("Luna");
			dimensionProperties.orbitalDist = 150;
			dimensionProperties.addBiome(AdvancedRocketryBiomes.moonBiome);

			dimensionProperties.setParentPlanet(DimensionManager.overworldProperties);
			dimensionProperties.setStar(DimensionManager.getSol());
			dimensionProperties.isNativeDimension = !Loader.isModLoaded("GalacticraftCore");
			DimensionManager.getInstance().registerDimNoUpdate(dimensionProperties, !Loader.isModLoaded("GalacticraftCore"));


			Random random = new Random(System.currentTimeMillis());

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


				DimensionProperties properties = DimensionManager.getInstance().generateRandom(baseDistance,baseAtm,125,100,100,75);

				if(properties.gravitationalMultiplier >= 1f) {
					int numMoons = random.nextInt(4);

					for(int ii = 0; ii < numMoons; ii++) {
						DimensionProperties moonProperties = DimensionManager.getInstance().generateRandom(properties.getName() + ": " + ii, 25,100, (int)(properties.gravitationalMultiplier/.02f), 25, 100, 50);
						moonProperties.setParentPlanet(properties);
					}
				}
			}
		}
		else if(Loader.isModLoaded("GalacticraftCore")  ) {
			DimensionManager.getInstance().getDimensionProperties(zmaster587.advancedRocketry.api.Configuration.MoonId).isNativeDimension = false;
		}
	}


	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().unregisterAllDimensions();
	}

	/*@SideOnly(Side.CLIENT)
    @SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
    		//IIcon texture = event.map.registerIcon("advancedrocketry:fluid/oxygen_still");
    	if(event.map)
    		AdvancedRocketryFluids.fluidRocketFuel.setIcons(event.map.registerIcon("advancedrocketry:fluid/oxygen_still"));
    		AdvancedRocketryFluids.fluidHydrogen.setIcons(event.map.registerIcon("advancedrocketry:fluid/oxygen_still"));
    		AdvancedRocketryFluids.fluidOxygen.setIcons(event.map.registerIcon("advancedrocketry:fluid/oxygen_still"));

	}*/

	@SubscribeEvent
	public void registerOre(OreRegisterEvent event) {
		if(!zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods)
			return;

		for(AllowedProducts product : AllowedProducts.getAllAllowedProducts() ) {
			if(event.Name.startsWith(product.name().toLowerCase())) {
				HashSet<String> list = modProducts.get(product);
				if(list == null) {
					list = new HashSet<String>();
					modProducts.put(product, list);
				}
				list.add(event.Name.substring(product.name().length()));
			}
		}

		//GT uses stick instead of Rod
		if(event.Name.startsWith("stick")) {
			HashSet<String> list = modProducts.get(AllowedProducts.getProductByName("STICK"));
			if(list == null) {
				list = new HashSet<String>();
				modProducts.put(AllowedProducts.getProductByName("STICK"), list);
			}

			list.add(event.Name.substring("stick".length()));
		}
	}
	
	//Patch missing mappings
	@Mod.EventHandler
	public void missingMappingEvent(FMLMissingMappingsEvent event) {
		Iterator<MissingMapping> itr = event.getAll().iterator();
		while(itr.hasNext()) {
			MissingMapping mapping = itr.next();

			if(mapping.name.equalsIgnoreCase("advancedrocketry:" + LibVulpesItems.itemBattery.getUnlocalizedName()))
				mapping.remap(LibVulpesItems.itemBattery);

			/*if(mapping.name.equalsIgnoreCase("advancedrocketry:item.sawBlade")) {
				if(mapping.type == mapping.type.ITEM) {
					mapping.remap(Item.getItemFromBlock(AdvancedRocketryBlocks.blockSawBlade));
				}	
			}*/
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.satellitePowerSource")) 
					mapping.remap(AdvancedRocketryItems.itemSatellitePowerSource);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.circuitplate")) 
				mapping.remap(AdvancedRocketryItems.itemCircuitPlate);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.wafer")) 
				mapping.remap(AdvancedRocketryItems.itemWafer);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.itemUpgrade")) 
				mapping.remap(AdvancedRocketryItems.itemUpgrade);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.dataUnit")) 
				mapping.remap(AdvancedRocketryItems.itemDataUnit);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.satellitePrimaryFunction")) 
				mapping.remap(AdvancedRocketryItems.itemSatellitePrimaryFunction);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.pressureTank")) 
				mapping.remap(AdvancedRocketryItems.itemPressureTank);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.pressureTank")) 
				mapping.remap(AdvancedRocketryItems.itemPressureTank);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.lens")) 
				mapping.remap(AdvancedRocketryItems.itemLens);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.miscpart")) 
				mapping.remap(AdvancedRocketryItems.itemMisc);
			
			if(mapping.name.equalsIgnoreCase("advancedRocketry:item.circuitIC")) 
				mapping.remap(AdvancedRocketryItems.itemIC);
		}
	}
}
