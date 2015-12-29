package zmaster587.advancedRocketry;


import ic2.api.item.IC2Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;

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
import net.minecraft.util.IIcon;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.api.dimension.DimensionManager;
import zmaster587.advancedRocketry.api.dimension.DimensionProperties;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.material.MaterialRegistry;
import zmaster587.advancedRocketry.api.material.MixedMaterial;
import zmaster587.advancedRocketry.api.material.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.api.material.MaterialRegistry.Materials;
import zmaster587.advancedRocketry.api.network.PacketHandler;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.block.BlockAlphaTexture;
import zmaster587.advancedRocketry.block.BlockDoor2;
import zmaster587.advancedRocketry.block.BlockFluid;
import zmaster587.advancedRocketry.block.BlockGeneric;
import zmaster587.advancedRocketry.block.BlockLaser;
import zmaster587.advancedRocketry.block.BlockLightSource;
import zmaster587.advancedRocketry.block.BlockLinkedHorizontalTexture;
import zmaster587.advancedRocketry.block.BlockOre;
import zmaster587.advancedRocketry.block.BlockPhantom;
import zmaster587.advancedRocketry.block.BlockPlanetSoil;
import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.block.BlockQuartzCrucible;
import zmaster587.advancedRocketry.block.BlockRocketMotor;
import zmaster587.advancedRocketry.block.BlockRotatableModel;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.block.BlockFuelTank;
import zmaster587.advancedRocketry.block.BlockTile;
import zmaster587.advancedRocketry.block.multiblock.BlockHatch;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiblockMachine;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiblockPlaceHolder;
import zmaster587.advancedRocketry.block.multiblock.BlockMultiMachineBattery;
import zmaster587.advancedRocketry.block.plant.BlockAlienLeaves;
import zmaster587.advancedRocketry.block.plant.BlockAlienSapling;
import zmaster587.advancedRocketry.block.plant.BlockAlienWood;
import zmaster587.advancedRocketry.command.WorldCommand;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.EntityDummy;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.event.BucketHandler;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.integration.GalacticCraftHandler;
import zmaster587.advancedRocketry.item.ItemBlockMeta;
import zmaster587.advancedRocketry.item.ItemBlockWithIcon;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.item.ItemDoor2;
import zmaster587.advancedRocketry.item.ItemFluid;
import zmaster587.advancedRocketry.item.ItemIngredient;
import zmaster587.advancedRocketry.item.ItemOreScanner;
import zmaster587.advancedRocketry.item.ItemPackedStructure;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemProjector;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.satellite.SatelliteDensity;
import zmaster587.advancedRocketry.satellite.SatelliteMassScanner;
import zmaster587.advancedRocketry.satellite.SatelliteOptical;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.TileIC2Plug;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.TileMaterial;
import zmaster587.advancedRocketry.tile.TileMissionController;
import zmaster587.advancedRocketry.tile.TileModelRender;
import zmaster587.advancedRocketry.tile.TileModelRenderRotatable;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import zmaster587.advancedRocketry.tile.TileOxygenCharger;
import zmaster587.advancedRocketry.tile.TileCO2Scrubber;
import zmaster587.advancedRocketry.tile.TileOxygenVent;
import zmaster587.advancedRocketry.tile.TileRFPlug;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.TileSchematic;
import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import zmaster587.advancedRocketry.tile.TileStationBuilder;
import zmaster587.advancedRocketry.tile.Satellite.TileEntitySatelliteControlCenter;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteBuilder;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.data.TileDataBus;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityFuelingStation;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityMoniteringStation;
import zmaster587.advancedRocketry.tile.multiblock.TileChemicalReactor;
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileElectricArcFurnace;
import zmaster587.advancedRocketry.tile.multiblock.TileElectrolyser;
import zmaster587.advancedRocketry.tile.multiblock.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.TileFluidHatch;
import zmaster587.advancedRocketry.tile.multiblock.TileRollingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetAnalyser;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetSelector;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;
import zmaster587.advancedRocketry.util.FluidColored;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import zmaster587.advancedRocketry.world.biome.BiomeGenHotDryRock;
import zmaster587.advancedRocketry.world.biome.BiomeGenMoon;
import zmaster587.advancedRocketry.world.biome.BiomeGenSpace;
import zmaster587.advancedRocketry.world.ore.OreGenerator;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.advancedRocketry.world.type.WorldTypePlanetGen;
import zmaster587.advancedRocketry.world.type.WorldTypeSpace;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@Mod(modid="advancedRocketry", name="Advanced Rocketry", version="%VERSION%", dependencies="required-after:libVulpes@[0.0.6c,)")
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

	private HashMap<AllowedProducts, HashSet<String>> modProducts = new HashMap<AllowedProducts, HashSet<String>>();


	private static CreativeTabs tabAdvRocketry = new CreativeTabs("advancedRocketry") {
		@Override
		public Item getTabIconItem() {
			return AdvancedRocketryItems.itemSatelliteIdChip;
		}
	};

	public static CreativeTabs tabAdvRocketryOres = new CreativeTabs("advancedRocketryOres") {

		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(AdvancedRocketryBlocks.blockOre.get(0));
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		//Init API
		DimensionManager.planetWorldProvider = WorldProviderPlanet.class;

		//Configuration  ---------------------------------------------------------------------------------------------
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		final String oreGen = "Ore Generation";
		final String ROCKET = "Rockets";
		final String MOD_INTERACTION = "Mod Interaction";
		final String PLANET = "Planet";

		zmaster587.advancedRocketry.api.Configuration.buildSpeedMultiplier = (float) config.get(Configuration.CATEGORY_GENERAL, "buildSpeedMultiplier", 1f, "Multiplier for the build speed of the Rocket Builder (0.5 is twice as fast 2 is half as fast").getDouble();
		zmaster587.advancedRocketry.api.Configuration.MoonId = config.get(Configuration.CATEGORY_GENERAL,"moonId" , 2,"Dimension ID to use for the moon").getInt();
		zmaster587.advancedRocketry.api.Configuration.spaceDimId = config.get(Configuration.CATEGORY_GENERAL,"spaceStationId" , -2,"Dimension ID to use for space stations").getInt();
		zmaster587.advancedRocketry.api.Configuration.enableOxygen = config.get(Configuration.CATEGORY_GENERAL, "EnableAtmosphericEffects", true, "If true, allows players being hurt due to lack of oxygen and allows effects from non-standard atmosphere types").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods = config.get(Configuration.CATEGORY_GENERAL, "makeMaterialsForOtherMods", true, "If true the machines from AdvancedRocketry will produce things like plates/rods for other mods even if Advanced Rocketry itself does not use the material (This can increase load time)").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.scrubberRequiresCartrige = config.get(Configuration.CATEGORY_GENERAL, "scrubberRequiresCartrige", true, "If true the Oxygen scrubbers require a consumable carbon collection cartridge").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.EUMult = (float)config.get(Configuration.CATEGORY_GENERAL, "EUPowerMultiplier", 7, "How many power unit one EU makes").getDouble();
		DimensionManager.dimOffset = config.getInt("minDimension", PLANET, 2, -127, 127, "Dimensions including and after this number are allowed to be made into planets");
		zmaster587.advancedRocketry.api.Configuration.overrideGCAir = config.get(MOD_INTERACTION, "OverrideGCAir", true, "If true Galaciticcraft's air will be disabled entirely requiring use of Advanced Rocketry's Oxygen system on GC planets").getBoolean();
		
		zmaster587.advancedRocketry.api.Configuration.rocketRequireFuel = config.get(ROCKET, "rocketsRequireFuel", true, "Set to false if rockets should not require fuel to fly").getBoolean();
		zmaster587.advancedRocketry.api.Configuration.rocketThrustMultiplier = config.get(ROCKET, "thrustMultiplier", 1f, "Multiplier for per-engine thrust").getDouble();
		zmaster587.advancedRocketry.api.Configuration.fuelCapacityMultiplier = config.get(ROCKET, "fuelCapacityMultiplier", 1f, "Multiplier for per-tank capacity").getDouble();

		zmaster587.advancedRocketry.api.Configuration.generateCopper = config.get(oreGen, "GenerateCopper", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.copperClumpSize = config.get(oreGen, "CopperPerClump", 6).getInt();
		zmaster587.advancedRocketry.api.Configuration.copperPerChunk = config.get(oreGen, "CopperPerChunk", 10).getInt();

		zmaster587.advancedRocketry.api.Configuration.generateTin = config.get(oreGen, "GenerateTin", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.tinClumpSize = config.get(oreGen, "TinPerClump", 6).getInt();
		zmaster587.advancedRocketry.api.Configuration.tinPerChunk = config.get(oreGen, "TinPerChunk", 10).getInt();

		zmaster587.advancedRocketry.api.Configuration.generateRutile = config.get(oreGen, "GenerateRutile", true).getBoolean();
		zmaster587.advancedRocketry.api.Configuration.rutileClumpSize = config.get(oreGen, "RutilePerClump", 3).getInt();
		zmaster587.advancedRocketry.api.Configuration.rutilePerChunk = config.get(oreGen, "RutilePerChunk", 2).getInt();
		config.save();

		//if(zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods)
			MinecraftForge.EVENT_BUS.register(this);

		//Satellites ---------------------------------------------------------------------------------------------
		SatelliteRegistry.registerSatellite("optical", SatelliteOptical.class);
		SatelliteRegistry.registerSatellite("density", SatelliteDensity.class);
		SatelliteRegistry.registerSatellite("mass", SatelliteMassScanner.class);


		//Blocks -------------------------------------------------------------------------------------
		AdvancedRocketryBlocks.blockLaunchpad = new BlockLinkedHorizontalTexture(Material.rock).setBlockName("pad").setCreativeTab(tabAdvRocketry).setBlockTextureName("advancedrocketry:rocketPad").setHardness(2f);
		AdvancedRocketryBlocks.blockStructureTower = new BlockAlphaTexture(Material.rock).setBlockName("structuretower").setCreativeTab(tabAdvRocketry).setBlockTextureName("advancedrocketry:structuretower").setHardness(2f);
		AdvancedRocketryBlocks.blockGenericSeat = new BlockSeat(Material.cloth).setBlockName("seat").setCreativeTab(tabAdvRocketry).setBlockTextureName("minecraft:wool_colored_silver").setHardness(0.5f);
		AdvancedRocketryBlocks.blockEngine = new BlockRocketMotor(Material.rock).setBlockName("rocket").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockFuelTank = new BlockFuelTank(Material.rock).setBlockName("fuelTank").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockSawBlade = new BlockRotatableModel(Material.rock, TileModelRender.models.SAWBLADE.ordinal()).setCreativeTab(tabAdvRocketry).setBlockName("sawBlade").setHardness(2f);
		AdvancedRocketryBlocks.blockMotor = new BlockRotatableModel(Material.rock, TileModelRender.models.MOTOR.ordinal()).setCreativeTab(tabAdvRocketry).setBlockName("motor").setHardness(2f);
		AdvancedRocketryBlocks.blockConcrete = new BlockGeneric(Material.rock).setBlockName("concrete").setBlockTextureName("advancedRocketry:rocketPad_noEdge").setCreativeTab(tabAdvRocketry).setHardness(3f).setResistance(16f);
		AdvancedRocketryBlocks.blockPhantom = new BlockPhantom(Material.circuits).setBlockName("blockPhantom");
		AdvancedRocketryBlocks.blockPlatePress = new BlockPress().setBlockName("blockHandPress").setCreativeTab(tabAdvRocketry).setHardness(2f);
		AdvancedRocketryBlocks.blockAirLock = new BlockDoor2(Material.rock).setBlockName("smallAirlockDoor").setBlockTextureName("advancedRocketry:smallAirlockDoor").setHardness(3f).setResistance(8f);

		AdvancedRocketryBlocks.blockOxygenScrubber = new BlockTile(TileCO2Scrubber.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockTextureName("advancedrocketry:machineScrubber","advancedrocketry:machineScrubberActive").setCreativeTab(tabAdvRocketry).setBlockName("scrubber").setHardness(3f);

		AdvancedRocketryBlocks.blockOxygenCharger = new BlockTile(TileOxygenCharger.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("oxygenCharger").setCreativeTab(tabAdvRocketry).setBlockTextureName("Advancedrocketry:machineGeneric").setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockOxygenCharger).setSideTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockOxygenCharger).setTopTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockOxygenCharger).setFrontTexture("advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockOxygenVent = new BlockTile(TileOxygenVent.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("oxygenVent").setCreativeTab(tabAdvRocketry).setBlockTextureName("Advancedrocketry:machineGeneric").setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockOxygenVent).setSideTexture("advancedrocketry:machineVent");
		((BlockTile) AdvancedRocketryBlocks.blockOxygenVent).setTopTexture("advancedrocketry:machineVent");
		((BlockTile) AdvancedRocketryBlocks.blockOxygenVent).setFrontTexture("advancedrocketry:machineVent");

		AdvancedRocketryBlocks.blockRocketBuilder = new BlockTile(TileRocketBuilder.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setBlockName("rocketAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockRocketBuilder).setSideTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockRocketBuilder).setTopTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockRocketBuilder).setFrontTexture("advancedrocketry:MonitorFront");

		AdvancedRocketryBlocks.blockStationBuilder = new BlockTile(TileStationBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("stationAssembler").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockStationBuilder).setSideTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockStationBuilder).setTopTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockStationBuilder).setFrontTexture("advancedrocketry:MonitorFront");

		AdvancedRocketryBlocks.blockFuelingStation = new BlockTile(TileEntityFuelingStation.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("fuelStation").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockFuelingStation).setSideTexture("Advancedrocketry:FuelingMachine");
		((BlockTile) AdvancedRocketryBlocks.blockFuelingStation).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockFuelingStation).setFrontTexture("Advancedrocketry:FuelingMachine");

		AdvancedRocketryBlocks.blockMonitoringStation = new BlockTile(TileEntityMoniteringStation.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockMonitoringStation).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockMonitoringStation).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockMonitoringStation).setFrontTexture("Advancedrocketry:MonitorRocket");
		AdvancedRocketryBlocks.blockMonitoringStation.setBlockName("monitoringstation");

		AdvancedRocketryBlocks.blockSatelliteBuilder = new BlockMultiblockMachine(TileSatelliteBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteBuilder).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteBuilder).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteBuilder).setFrontTexture("Advancedrocketry:satelliteAssembler");
		AdvancedRocketryBlocks.blockSatelliteBuilder.setBlockName("satelliteBuilder");

		AdvancedRocketryBlocks.blockSatelliteControlCenter = new BlockTile(TileEntitySatelliteControlCenter.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteControlCenter).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteControlCenter).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteControlCenter).setFrontTexture("Advancedrocketry:MonitorSatellite");
		AdvancedRocketryBlocks.blockSatelliteControlCenter.setBlockName("satelliteMonitor");

		//Arcfurnace
		AdvancedRocketryBlocks.blockArcFurnace = new BlockMultiblockMachine(TileElectricArcFurnace.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("electricArcFurnace").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockArcFurnace).setSideTexture("Advancedrocketry:BlastBrick");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockArcFurnace).setFrontTexture("Advancedrocketry:BlastBrickFront", "Advancedrocketry:BlastBrickFrontActive");

		AdvancedRocketryBlocks.blockMoonTurf = new BlockPlanetSoil().setMapColor(MapColor.snowColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("turf").setBlockTextureName("advancedrocketry:moon_turf").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockHotTurf = new BlockPlanetSoil().setMapColor(MapColor.netherrackColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("hotDryturf").setBlockTextureName("advancedrocketry:hotdry_turf").setCreativeTab(tabAdvRocketry);

		AdvancedRocketryBlocks.blockHatch = new BlockHatch(Material.rock).setBlockName("hatch").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockPlaceHolder = new BlockMultiblockPlaceHolder().setBlockName("placeHolder").setBlockTextureName("advancedrocketry:machineGeneric");
		AdvancedRocketryBlocks.blockRFBattery = new BlockMultiMachineBattery(Material.rock, TileRFPlug.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("rfBattery").setBlockTextureName("advancedrocketry:batteryRF").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockStructureBlock = new BlockAlphaTexture(Material.rock).setBlockName("structureMachine").setBlockTextureName("advancedrocketry:structureBlock").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienWood = new BlockAlienWood().setBlockName("log").setBlockTextureName("advancedrocketry:log").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienLeaves = new BlockAlienLeaves().setBlockName("leaves2").setBlockTextureName("leaves").setCreativeTab(tabAdvRocketry).setHardness(3f);
		AdvancedRocketryBlocks.blockAlienSapling = new BlockAlienSapling().setBlockName("sapling").setBlockTextureName("advancedrocketry:sapling").setCreativeTab(tabAdvRocketry).setHardness(3f);

		AdvancedRocketryBlocks.blockLightSource = new BlockLightSource();
		AdvancedRocketryBlocks.blockSpaceLaser = new BlockLaser();
		AdvancedRocketryBlocks.blockBlastBrick = new BlockMultiBlockComponentVisible(Material.rock).setCreativeTab(tabAdvRocketry).setBlockName("blastBrick").setBlockTextureName("advancedRocketry:BlastBrick").setHardness(3F).setResistance(15F);
		AdvancedRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible();

		AdvancedRocketryBlocks.blockPrecisionAssembler = new BlockMultiblockMachine(TilePrecisionAssembler.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("precisionAssemblingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPrecisionAssembler).setFrontTexture("advancedrocketry:PrecisionAssemblerFront", "advancedrocketry:PrecisionAssemblerFront_Active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPrecisionAssembler).setSideTexture("advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockCuttingMachine = new BlockMultiblockMachine(TileCuttingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("cuttingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCuttingMachine).setFrontTexture("advancedrocketry:CuttingMachine", "advancedrocketry:CuttingMachine_active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCuttingMachine).setSideTexture("advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(TileCrystallizer.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("Crystallizer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCrystallizer).setSideTexture("Advancedrocketry:Crystallizer", "Advancedrocketry:Crystallizer_active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCrystallizer).setTopTexture("Advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockChemicalReactor = new BlockMultiblockMachine(TileChemicalReactor.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("chemreactor").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockChemicalReactor).setFrontTexture("Advancedrocketry:Crystallizer", "Advancedrocketry:Crystallizer_active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockChemicalReactor).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockChemicalReactor).setSideTexture("Advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockLathe = new BlockMultiblockMachine(TileLathe.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("lathe").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockLathe).setFrontTexture("Advancedrocketry:controlPanel");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockLathe).setSideTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockLathe).setTopTexture("Advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockRollingMachine = new BlockMultiblockMachine(TileRollingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("rollingMachine").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockRollingMachine).setFrontTexture("Advancedrocketry:controlPanel");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockRollingMachine).setSideTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockRollingMachine).setTopTexture("Advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockElectrolyser = new BlockMultiblockMachine(TileElectrolyser.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("electrolyser").setCreativeTab(tabAdvRocketry).setHardness(3f);

		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockElectrolyser).setFrontTexture("Advancedrocketry:controlPanel");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockElectrolyser).setSideTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockElectrolyser).setTopTexture("Advancedrocketry:machineGeneric");


		AdvancedRocketryBlocks.blockPlanetAnalyser = new BlockMultiblockMachine(TilePlanetAnalyser.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setBlockName("planetanalyser").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPlanetAnalyser).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPlanetAnalyser).setSideTexture("advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPlanetAnalyser).setFrontTexture("advancedrocketry:MonitorPlanet","advancedrocketry:MonitorPlanet_active");

		AdvancedRocketryBlocks.blockObservatory = (BlockMultiblockMachine) new BlockMultiblockMachine(TileObservatory.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("observatory").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockObservatory).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockObservatory).setSideTexture("advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockObservatory).setFrontTexture("advancedrocketry:MonitorFrontMid","advancedrocketry:MonitorFrontMid");

		AdvancedRocketryBlocks.blockGuidanceComputer = new BlockTile(TileGuidanceComputer.class,GuiHandler.guiId.MODULAR.ordinal()).setBlockName("guidanceComputer").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer).setTopTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer).setSideTexture("Advancedrocketry:MonitorSide");
		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer).setFrontTexture("Advancedrocketry:guidanceComputer");

		AdvancedRocketryBlocks.blockPlanetSelector = new BlockTile(TilePlanetSelector.class,GuiHandler.guiId.MODULARFULLSCREEN.ordinal()).setBlockName("planetSelector").setCreativeTab(tabAdvRocketry).setHardness(3f);
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector).setTopTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector).setSideTexture("Advancedrocketry:MonitorSide");
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector).setFrontTexture("Advancedrocketry:guidanceComputer");


		//Fluid Registration
		AdvancedRocketryFluids.fluidOxygen = new FluidColored("oxygen",0x8f94b9).setUnlocalizedName("oxygen").setGaseous(true);
		FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidOxygen);

		AdvancedRocketryFluids.fluidHydrogen = new FluidColored("hydrogen",0xdbc1c1).setUnlocalizedName("hydrogen").setGaseous(true);
		FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidHydrogen);

		AdvancedRocketryFluids.fluidRocketFuel = new FluidColored("rocketFuel", 0xe5d884).setUnlocalizedName("rocketFuel").setGaseous(true);
		FluidRegistry.registerFluid(AdvancedRocketryFluids.fluidRocketFuel);

		AdvancedRocketryBlocks.blockOxygenFluid = new BlockFluid(AdvancedRocketryFluids.fluidOxygen, Material.water).setBlockName("oxygenFluidBlock").setCreativeTab(CreativeTabs.tabMisc);
		AdvancedRocketryBlocks.blockHydrogenFluid = new BlockFluid(AdvancedRocketryFluids.fluidHydrogen, Material.water).setBlockName("hydrogenFluidBlock").setCreativeTab(CreativeTabs.tabMisc);
		AdvancedRocketryBlocks.blockFuelFluid = new BlockFluid(AdvancedRocketryFluids.fluidRocketFuel, Material.water).setBlockName("rocketFuelBlock").setCreativeTab(CreativeTabs.tabMisc);

		
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
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockHatch, ItemBlockMeta.class, "blockHatch");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockPlaceHolder, "blockPlaceholder");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockRFBattery, "rfBattery");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockStructureBlock, "blockStructureBlock");
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockSpaceLaser, "laserController");
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
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockPhantom, AdvancedRocketryBlocks.blockPhantom.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockPlatePress, AdvancedRocketryBlocks.blockPlatePress .getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockStationBuilder, AdvancedRocketryBlocks.blockStationBuilder.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockElectrolyser, AdvancedRocketryBlocks.blockElectrolyser.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockChemicalReactor, AdvancedRocketryBlocks.blockChemicalReactor.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOxygenScrubber, AdvancedRocketryBlocks.blockOxygenScrubber.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOxygenVent, AdvancedRocketryBlocks.blockOxygenVent.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOxygenCharger, AdvancedRocketryBlocks.blockOxygenCharger.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockAirLock, AdvancedRocketryBlocks.blockAirLock.getUnlocalizedName());

		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockOxygenFluid,ItemFluid.class, AdvancedRocketryBlocks.blockOxygenFluid.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockHydrogenFluid,ItemFluid.class, AdvancedRocketryBlocks.blockHydrogenFluid.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockFuelFluid, ItemFluid.class, AdvancedRocketryBlocks.blockFuelFluid.getUnlocalizedName());

		BlockOre.registerOres(tabAdvRocketryOres);


		//Items -------------------------------------------------------------------------------------
		AdvancedRocketryItems.itemWafer = new ItemIngredient(1).setUnlocalizedName("wafer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCircuitPlate = new ItemIngredient(1).setUnlocalizedName("circuitplate").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemIC = new ItemIngredient(2).setUnlocalizedName("circuitIC").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemBattery = new ItemIngredient(1).setUnlocalizedName("battery").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemMisc = new ItemIngredient(2).setUnlocalizedName("miscpart").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSawBlade = new ItemIngredient(1).setUnlocalizedName("sawBlade").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStationChip = new ItemStationChip().setUnlocalizedName("stationChip").setTextureName("advancedRocketry:stationIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStation = new ItemPackedStructure().setUnlocalizedName("station").setTextureName("advancedRocketry:SpaceStation");
		AdvancedRocketryItems.itemSmallAirlockDoor = new ItemDoor2(Material.rock).setUnlocalizedName("smallAirlock").setTextureName("advancedRocketry:smallAirlock").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCarbonScrubberCartridge = new Item().setMaxDamage(172800).setUnlocalizedName("carbonScrubberCartridge").setTextureName("advancedRocketry:carbonCartridge").setCreativeTab(tabAdvRocketry);

		AdvancedRocketryItems.itemSatellitePowerSource = new ItemIngredient(1).setUnlocalizedName("satellitePowerSource").setCreativeTab(tabAdvRocketry);
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0), new SatelliteProperties().setPowerGeneration(10));

		AdvancedRocketryItems.itemSatellitePrimaryFunction = new ItemIngredient(3).setUnlocalizedName("satellitePrimaryFunction").setCreativeTab(tabAdvRocketry);
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOptical.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteDensity.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 2), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMassScanner.class)));

		//TODO: move registration in the case we have more than one chip type
		AdvancedRocketryItems.itemDataUnit = new ItemData().setUnlocalizedName("dataUnit").setCreativeTab(tabAdvRocketry);
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), new SatelliteProperties().setMaxData(1000));

		AdvancedRocketryItems.itemOreScanner = new ItemOreScanner().setUnlocalizedName("OreScanner").setTextureName("advancedRocketry:oreScanner");
		AdvancedRocketryItems.itemQuartzCrucible = (new ItemBlockWithIcon(AdvancedRocketryBlocks.blockQuartzCrucible)).setUnlocalizedName("qcrucible").setCreativeTab(tabAdvRocketry).setTextureName("advancedRocketry:qcrucible");

		AdvancedRocketryItems.itemSatellite = new ItemSatellite().setUnlocalizedName("satellite").setTextureName("advancedRocketry:satellite");
		AdvancedRocketryItems.itemSatelliteIdChip = new ItemSatelliteIdentificationChip().setUnlocalizedName("satelliteIdChip").setTextureName("advancedRocketry:satelliteIdChip").setCreativeTab(tabAdvRocketry);

		AdvancedRocketryItems.itemPlanetIdChip = new ItemPlanetIdentificationChip().setUnlocalizedName("planetIdChip").setTextureName("advancedRocketry:planetIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemHoloProjector = new ItemProjector().setUnlocalizedName("holoProjector").setTextureName("advancedRocketry:holoProjector").setCreativeTab(tabAdvRocketry);

		//Fluids
		AdvancedRocketryItems.itemBucketRocketFuel = new ItemBucket(AdvancedRocketryBlocks.blockFuelFluid).setCreativeTab(tabAdvRocketryOres).setUnlocalizedName("bucketRocketFuel").setTextureName("advancedRocketry:bucket_liquid");

		//Armor registration
		AdvancedRocketryItems.itemSpaceSuit_Helmet = new ItemSpaceArmor(AdvancedRocketryItems.spaceSuit, 0).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceHelmet").setTextureName("advancedRocketry:space_helmet");
		AdvancedRocketryItems.itemSpaceSuit_Chest = new ItemSpaceArmor(AdvancedRocketryItems.spaceSuit, 1).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceChest").setTextureName("advancedRocketry:space_chestplate");
		AdvancedRocketryItems.itemSpaceSuit_Leggings = new ItemSpaceArmor(AdvancedRocketryItems.spaceSuit, 2).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceLeggings").setTextureName("advancedRocketry:space_leggings");
		AdvancedRocketryItems.itemSpaceSuit_Boots = new ItemSpaceArmor(AdvancedRocketryItems.spaceSuit, 3).setCreativeTab(tabAdvRocketry).setUnlocalizedName("spaceBoots").setTextureName("advancedRocketry:space_boots");

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
		GameRegistry.registerItem(AdvancedRocketryItems.itemBattery, AdvancedRocketryItems.itemBattery.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemMisc, AdvancedRocketryItems.itemMisc.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemHoloProjector, AdvancedRocketryItems.itemHoloProjector.getUnlocalizedName());
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

		//Register multiblock items with the projector
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileCuttingMachine(), (BlockTile)AdvancedRocketryBlocks.blockCuttingMachine);
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileLathe(), (BlockTile)AdvancedRocketryBlocks.blockLathe);
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileCrystallizer(), (BlockTile)AdvancedRocketryBlocks.blockCrystallizer);
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TilePrecisionAssembler(), (BlockTile)AdvancedRocketryBlocks.blockPrecisionAssembler);
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileObservatory(), (BlockTile)AdvancedRocketryBlocks.blockObservatory);
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TilePlanetAnalyser(), (BlockTile)AdvancedRocketryBlocks.blockPlanetAnalyser);
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileRollingMachine(), (BlockTile)AdvancedRocketryBlocks.blockRollingMachine);
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileElectricArcFurnace(), (BlockTile)AdvancedRocketryBlocks.blockArcFurnace);
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileElectrolyser(), (BlockTile)AdvancedRocketryBlocks.blockElectrolyser);
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileChemicalReactor(), (BlockTile)AdvancedRocketryBlocks.blockChemicalReactor);
		//End Items

		//Entity Registration ---------------------------------------------------------------------------------------------
		EntityRegistry.registerModEntity(EntityDummy.class, "mountDummy", 0, this, 16, 20, false);
		EntityRegistry.registerModEntity(EntityRocket.class, "rocket", 1, this, 64, 20, true);


		//TileEntity Registration ---------------------------------------------------------------------------------------------
		GameRegistry.registerTileEntity(TileRocketBuilder.class, "ARrocketBuilder");
		GameRegistry.registerTileEntity(TileModelRender.class, "ARmodelRenderer");
		GameRegistry.registerTileEntity(TileEntityFuelingStation.class, "ARfuelingStation");
		GameRegistry.registerTileEntity(TileEntityMoniteringStation.class, "ARmonitoringStation");
		GameRegistry.registerTileEntity(TilePlaceholder.class, "ARplaceHolder");
		GameRegistry.registerTileEntity(TileMissionController.class, "ARmissionControlComp");
		GameRegistry.registerTileEntity(TileSpaceLaser.class, "ARspaceLaser");
		GameRegistry.registerTileEntity(TilePrecisionAssembler.class, "ARprecisionAssembler");
		GameRegistry.registerTileEntity(TileObservatory.class, "ARobservatory");
		GameRegistry.registerTileEntity(zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer.class, "ARcrystallizer");
		GameRegistry.registerTileEntity(TileOutputHatch.class, "ARoutputHatch");
		GameRegistry.registerTileEntity(TileInputHatch.class, "ARinputHatch");
		GameRegistry.registerTileEntity(TileRFPlug.class, "ARrfBattery");
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
		GameRegistry.registerTileEntity(TileSchematic.class, "ARTileSchematic");
		GameRegistry.registerTileEntity(TileStationBuilder.class, "ARStationBuilder");
		GameRegistry.registerTileEntity(TileFluidHatch.class, "ARFluidHatch");
		GameRegistry.registerTileEntity(TileElectrolyser.class, "ARElectrolyser");
		GameRegistry.registerTileEntity(TileChemicalReactor.class, "ARChemicalReactor");
		GameRegistry.registerTileEntity(TileOxygenVent.class, "AROxygenVent");
		GameRegistry.registerTileEntity(TileOxygenCharger.class, "AROxygenCharger");
		GameRegistry.registerTileEntity(TileCO2Scrubber.class, "ARCO2Scrubber");
		EntityRegistry.registerModEntity(EntityLaserNode.class, "laserNode", 0, instance, 256, 20, false);

		
		//OreDict stuff
		OreDictionary.registerOre("waferSilicon", new ItemStack(AdvancedRocketryItems.itemWafer,1,0));
		OreDictionary.registerOre("ingotCarbon", new ItemStack(AdvancedRocketryItems.itemMisc, 1, 1));

		//MOD-SPECIFIC ENTRIES --------------------------------------------------------------------------------------------------------------------------
		//Items dependant on IC2
		if(Loader.isModLoaded("IC2")) {
			AdvancedRocketryBlocks.blockIC2Plug = new BlockMultiMachineBattery(Material.rock ,TileIC2Plug.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("IC2Plug").setBlockTextureName("advancedrocketry:IC2Plug").setCreativeTab(tabAdvRocketry).setHardness(3f);
			GameRegistry.registerBlock(AdvancedRocketryBlocks.blockIC2Plug, AdvancedRocketryBlocks.blockIC2Plug.getUnlocalizedName());
			GameRegistry.registerTileEntity(TileIC2Plug.class, "ARIC2Plug");
		}

	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{

		//Register Alloys
		MaterialRegistry.registerMixedMaterial(new MixedMaterial(TileElectricArcFurnace.class, "oreRutile", new ItemStack[] {MaterialRegistry.Materials.TITANIUM.getProduct(AllowedProducts.INGOT)}));

		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockBlastBrick,16), new ItemStack(Items.potionitem,1,8195), new ItemStack(Items.potionitem,1,8201), Blocks.brick_block, Blocks.brick_block, Blocks.brick_block, Blocks.brick_block);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockArcFurnace), "aba","bcb", "aba", Character.valueOf('a'), Items.brick, Character.valueOf('b'), new ItemStack(Items.dye,1,15), Character.valueOf('c'), AdvancedRocketryBlocks.blockBlastBrick);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemQuartzCrucible), " a ", "aba", " a ", Character.valueOf('a'), Items.quartz, Character.valueOf('b'), Items.cauldron);
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockPlatePress, "   ", " a ", "iii", 'a', Blocks.piston, 'i', Items.iron_ingot));
		GameRegistry.addRecipe(new ShapedOreRecipe(MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.STICK), "x  ", " x ", "  x", 'x', "ingotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(MaterialRegistry.getItemStackFromMaterialAndType(Materials.STEEL, AllowedProducts.STICK), "x  ", " x ", "  x", 'x', "ingotSteel"));
		GameRegistry.addSmelting(MaterialRegistry.Materials.DILITHIUM.getProduct(MaterialRegistry.AllowedProducts.ORE), MaterialRegistry.Materials.DILITHIUM.getProduct(AllowedProducts.DUST), 0);


		//Supporting Materials
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemMisc,1,0), "lrl", "fgf", 'l', "dyeLime", 'r', Items.redstone, 'g', Blocks.glass_pane, 'f', Items.glowstone_dust));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockGenericSeat), "xxx", 'x', Blocks.wool);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockConcrete), Blocks.sand, Blocks.gravel, Items.water_bucket);
		GameRegistry.addRecipe(new ShapelessOreRecipe(AdvancedRocketryBlocks.blockLaunchpad, AdvancedRocketryBlocks.blockConcrete, "dyeBlack", "dyeYellow"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockStructureTower, "ooo", " o ", "ooo", 'o', "rodSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockEngine, "sss", " t ","t t", 's', "ingotSteel", 't', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockFuelTank, "s s", "p p", "s s", 'p', "plateSteel", 's', "rodSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockStructureBlock, "sps", "psp", "sps", 'p', "plateIron", 's', "rodIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemBattery,1,0), " c ","prp", "prp", 'c', "rodIron", 'r', Items.redstone, 'p', "plateTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), "ppp", " g ", " l ", 'p', Blocks.glass_pane, 'g', Items.glowstone_dust, 'l', "plateGold"));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockObservatory), "gug", "pbp", "rrr", 'g', Blocks.glass_pane, 'u', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'b', AdvancedRocketryBlocks.blockStructureBlock, 'r', MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.STICK));
		
		//Hatches
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,0), "c", "m"," ", 'c', Blocks.chest, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,1), "m", "c"," ", 'c', Blocks.chest, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,2), "m", "c"," ", 'c', AdvancedRocketryItems.itemDataUnit, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,4), "c", "m", " ", 'c', AdvancedRocketryBlocks.blockFuelTank, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,5), "m", "c", " ", 'c', AdvancedRocketryBlocks.blockFuelTank, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,3), " x ", "xmx"," x ", 'x', "rodTitanium", 'm', AdvancedRocketryBlocks.blockStructureBlock));
		
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockMotor), " cp", "rrp"," cp", 'c', "coilCopper", 'p', "plateSteel", 'r', "rodSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0), "rrr", "ggg","ppp", 'r', Items.redstone, 'g', Items.glowstone_dust, 'p', "plateGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemHoloProjector), "oro", "rpr", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'r', Items.redstone, 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 2), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'd', "crystalDilithium"));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 1), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'd', new ItemStack(AdvancedRocketryItems.itemIC,1,1));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0), " x ","xox", " x ", 'x', "plateIron", 'o', "rodIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSawBlade,1,0), "xox", "x x", 'x', "plateIron", 'o', new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0)));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSpaceStationChip), MaterialRegistry.getItemStackFromMaterialAndType(Materials.DILITHIUM, AllowedProducts.CRYSTAL),MaterialRegistry.getItemStackFromMaterialAndType(Materials.DILITHIUM, AllowedProducts.CRYSTAL), new ItemStack(AdvancedRocketryItems.itemIC,1,0));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge), "xix", "xix", "xix", 'x', "sheetIron", 'i', Blocks.iron_bars));


		//Plugs
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockRFBattery), " x ", "xmx"," x ", 'x', AdvancedRocketryItems.itemBattery, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		if(Loader.isModLoaded("IC2")) {
			GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockIC2Plug), AdvancedRocketryBlocks.blockStructureBlock, IC2Items.getItem("mvTransformer"));
		}

		//O2 Support
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenVent), "bfb", "bmb", "btb", 'b', Blocks.iron_bars, 'f', "fanSteel", 'm', AdvancedRocketryBlocks.blockMotor, 't', AdvancedRocketryBlocks.blockFuelTank));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenScrubber), "bfb", "bmb", "btb", 'b', Blocks.iron_bars, 'f', "fanSteel", 'm', AdvancedRocketryBlocks.blockMotor, 't', "ingotCarbon"));

		//MACHINES
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockPrecisionAssembler), "ap ", "bcd", "ef ", 'a', Blocks.dropper, 'b', Items.repeater, 'c', AdvancedRocketryBlocks.blockStructureBlock, 'd', Blocks.furnace, 'e', Blocks.heavy_weighted_pressure_plate, 'f', Items.diamond, 'p', new ItemStack(AdvancedRocketryItems.itemMisc,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockCrystallizer), "ada", "bcb","aba", 'a', Items.quartz, 'b', Items.repeater, 'c', AdvancedRocketryBlocks.blockStructureBlock, 'd', new ItemStack(AdvancedRocketryItems.itemMisc,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockCuttingMachine), "aba", "cdc", "a a", 'a', Items.diamond, 'b', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'c', Blocks.obsidian, 'd', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockLathe), "rsr", "pbp", "prp", 'r', "rodIron", 'p', "plateSteel", 'b', AdvancedRocketryBlocks.blockStructureBlock, 's', new ItemStack(AdvancedRocketryItems.itemMisc,1,0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockRollingMachine), "psp", "pbp", "iti", 'p', "plateSteel", 's', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'b', AdvancedRocketryBlocks.blockStructureBlock, 'i', "blockIron",'t', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockMonitoringStation), "coc", "cbc", "cpc", 'c', "rodCopper", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'b', AdvancedRocketryBlocks.blockStructureBlock, 'p', AdvancedRocketryItems.itemBattery));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockFuelingStation), "x", "b", "x", 'x', AdvancedRocketryBlocks.blockFuelTank, 'b', AdvancedRocketryBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSatelliteControlCenter), "oso", "cbc", "rtr", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 's', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'c', "rodCopper", 'b', AdvancedRocketryBlocks.blockStructureBlock, 'r', Items.repeater, 't', AdvancedRocketryItems.itemBattery));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSatelliteBuilder), "dht", "cbc", "mas", 'd', AdvancedRocketryItems.itemDataUnit, 'h', Blocks.hopper, 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'b', AdvancedRocketryBlocks.blockStructureBlock, 'm', AdvancedRocketryBlocks.blockMotor, 'a', Blocks.anvil, 's', AdvancedRocketryBlocks.blockSawBlade, 't', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockPlanetAnalyser), "tst", "pbp", "cpc", 't', new ItemStack(AdvancedRocketryItems.itemIC,1,1), 's', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'b', AdvancedRocketryBlocks.blockStructureBlock, 'p', "plateTin", 'c', AdvancedRocketryItems.itemPlanetIdChip));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockGuidanceComputer), "ctc", "rbr", "crc", 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,1), 't', "plateTitanium", 'r', Items.redstone, 'b', AdvancedRocketryBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockPlanetSelector), "coc", "lbl", "cpc", 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,1), 'o',new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'l', Blocks.lever, 'b', AdvancedRocketryBlocks.blockGuidanceComputer, 'p', Blocks.stone_button));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockRocketBuilder), "rrr", "pbp","ccc", 'r', "rodTitanium", 'p', "plateTitanium", 'b', AdvancedRocketryBlocks.blockStructureBlock, 'c', AdvancedRocketryBlocks.blockConcrete));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockStationBuilder), " d ", "dsd", " d ", 'd', "crystalDilithium", 's', new ItemStack(AdvancedRocketryBlocks.blockRocketBuilder)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockElectrolyser), "pip", "cbc", "pep", 'p', "plateSteel", 'i', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'b', AdvancedRocketryBlocks.blockStructureBlock, 'e', "coilCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockOxygenCharger), "fif", "tbt", "pcp", 'p', "plateSteel", 'f', "fanSteel", 'i', Blocks.heavy_weighted_pressure_plate, 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'b', AdvancedRocketryBlocks.blockStructureBlock, 't', AdvancedRocketryBlocks.blockFuelTank));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockChemicalReactor), "pip", "gbg", "pcp", 'p', "plateSteel", 'i', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'b', AdvancedRocketryBlocks.blockStructureBlock, 'g', "plateGold"));
		
		//Armor recipes
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Boots, " r ", "w w", "p p", 'r', "rodIron", 'w', Blocks.wool, 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Leggings, "wrw", "w w", "w w", 'w', Blocks.wool, 'r', "rodIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Chest, "wrw", "wtw", "wfw", 'w', Blocks.wool, 'r', "rodIron", 't', AdvancedRocketryBlocks.blockFuelTank, 'f', "fanSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSpaceSuit_Helmet, "prp", "rgr", "www", 'w', Blocks.wool, 'r', "rodIron", 'p', "plateIron", 'g', Blocks.glass_pane));

		//Other blocks
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryItems.itemSmallAirlockDoor, "pp", "pp","pp", 'p', "plateSteel"));
		
		//TEMP RECIPES
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemPlanetIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0), new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemMisc,1,1), new ItemStack(Items.coal,1,1), new ItemStack(Items.coal,1,1), new ItemStack(Items.coal,1,1), new ItemStack(Items.coal,1,1) ,new ItemStack(Items.coal,1,1) ,new ItemStack(Items.coal,1,1));


		RecipesMachine.getInstance().addRecipe(TileElectrolyser.class, new Object[] {new FluidStack(AdvancedRocketryFluids.fluidOxygen, 100), new FluidStack(AdvancedRocketryFluids.fluidHydrogen, 100)}, 100, 20, new FluidStack(FluidRegistry.WATER, 10));
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new FluidStack(AdvancedRocketryFluids.fluidRocketFuel, 20), 100, 10, new FluidStack(AdvancedRocketryFluids.fluidOxygen, 10), new FluidStack(AdvancedRocketryFluids.fluidHydrogen, 10));


		//Cutting Machine
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemIC, 4, 0), 300, 100, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0));
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemWafer, 4, 0), 300, 100, "bouleSilicon");

		//Lathe
		RecipesMachine.getInstance().addRecipe(TileLathe.class, MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.STICK), 300, 100, "ingotIron");

		//Precision Assembler recipes
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), 900, 100, Items.gold_ingot, Items.redstone, "waferSilicon");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), 500, 60, "plateGold", AdvancedRocketryItems.itemIC, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemIC,1,1), 900, 50, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), Items.ender_eye, Items.redstone);

		//BlastFurnace
		RecipesMachine.getInstance().addRecipe(TileElectricArcFurnace.class, MaterialRegistry.Materials.SILICON.getProduct(AllowedProducts.INGOT), 12000, 1, Blocks.sand);
		RecipesMachine.getInstance().addRecipe(TileElectricArcFurnace.class, MaterialRegistry.Materials.STEEL.getProduct(AllowedProducts.INGOT), 6000, 1, "ingotIron", Blocks.coal_block);

		//Chemical Reactor
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new Object[] {new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge,1, 0), new ItemStack(Items.coal, 1, 1)}, 40, 20, new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge, 1, AdvancedRocketryItems.itemCarbonScrubberCartridge.getMaxDamage()));

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		planetWorldType = new WorldTypePlanetGen("PlanetCold");
		spaceWorldType = new WorldTypeSpace("Space");

		//Biomes --------------------------------------------------------------------------------------

		AdvancedRocketryBiomes.moonBiome = new BiomeGenMoon(config.get(BIOMECATETORY, "moonBiomeId", 90).getInt(), true);
		AdvancedRocketryBiomes.alienForest = new BiomeGenAlienForest(config.get(BIOMECATETORY, "alienForestBiomeId", 91).getInt(), true);
		AdvancedRocketryBiomes.hotDryBiome = new BiomeGenHotDryRock(config.get(BIOMECATETORY, "hotDryBiome", 92).getInt(), true);
		AdvancedRocketryBiomes.spaceBiome = new BiomeGenSpace(config.get(BIOMECATETORY, "spaceBiomeId", 93).getInt(), true);
		config.save();

		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.alienForest);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.hotDryBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.spaceBiome);
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
		
		if(Loader.isModLoaded("GalacticraftCore") && zmaster587.advancedRocketry.api.Configuration.overrideGCAir) {
			GalacticCraftHandler eventHandler = new GalacticCraftHandler();
			MinecraftForge.EVENT_BUS.register(eventHandler);
			if(event.getSide().isClient())
				FMLCommonHandler.instance().bus().register(eventHandler);
		}

		FMLCommonHandler.instance().bus().register(DimensionManager.getSpaceManager());

		PacketHandler.init();
		FuelRegistry.instance.registerFuel(FuelType.LIQUID, AdvancedRocketryFluids.fluidRocketFuel, 1);

		GameRegistry.registerWorldGenerator(new OreGenerator(), 100);

		/*ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());

		proxy.registerKeyBinds();*/


		//AutoGenned Recipes
		for(MaterialRegistry.Materials ore : MaterialRegistry.Materials.values()) {
			if(MaterialRegistry.AllowedProducts.ORE.isOfType(ore.getAllowedProducts()) && MaterialRegistry.AllowedProducts.INGOT.isOfType(ore.getAllowedProducts()))
				GameRegistry.addSmelting(ore.getProduct(MaterialRegistry.AllowedProducts.ORE), ore.getProduct(AllowedProducts.INGOT), 0);

			if(MaterialRegistry.AllowedProducts.NUGGET.isOfType(ore.getAllowedProducts())) {
				ItemStack nugget = ore.getProduct(AllowedProducts.NUGGET);
				nugget.stackSize = 9;
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapelessOreRecipe(nugget, AllowedProducts.INGOT.name().toLowerCase() + str));
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.INGOT), "ooo", "ooo", "ooo", 'o', AllowedProducts.NUGGET.name().toLowerCase() + str));
				}
			}

			if(MaterialRegistry.AllowedProducts.CRYSTAL.isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, MaterialRegistry.AllowedProducts.CRYSTAL.name().toLowerCase() + str, 300, 200, MaterialRegistry.AllowedProducts.DUST.name().toLowerCase() + str);
			}

			if(MaterialRegistry.AllowedProducts.BOULE.isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, ore.getProduct(MaterialRegistry.AllowedProducts.BOULE), 300, 200, MaterialRegistry.AllowedProducts.INGOT.name().toLowerCase() + str, MaterialRegistry.AllowedProducts.NUGGET.name().toLowerCase() + str);
			}

			if(MaterialRegistry.AllowedProducts.STICK.isOfType(ore.getAllowedProducts()) && MaterialRegistry.AllowedProducts.INGOT.isOfType(ore.getAllowedProducts())) {
				for(String name : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(TileLathe.class, ore.getProduct(MaterialRegistry.AllowedProducts.STICK), 300, 200, MaterialRegistry.AllowedProducts.INGOT.name().toLowerCase() + name); //ore.getProduct(MaterialRegistry.AllowedProducts.INGOT));
			}

			if(MaterialRegistry.AllowedProducts.PLATE.isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames()) {
					RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, ore.getProduct(MaterialRegistry.AllowedProducts.PLATE), 300, 200, MaterialRegistry.AllowedProducts.INGOT.name().toLowerCase() + oreDictNames);
					if(AllowedProducts.BLOCK.isOfType(ore.getAllowedProducts()) || ore.isVanilla())
						RecipesMachine.getInstance().addRecipe(BlockPress.class, ore.getProduct(MaterialRegistry.AllowedProducts.PLATE), 0, 0, MaterialRegistry.AllowedProducts.BLOCK.name().toLowerCase() + oreDictNames);
				}
			}

			if(MaterialRegistry.AllowedProducts.SHEET.isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames()) {
					RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, ore.getProduct(MaterialRegistry.AllowedProducts.SHEET), 300, 200, MaterialRegistry.AllowedProducts.PLATE.name().toLowerCase() + oreDictNames);
				}
			}

			if(MaterialRegistry.AllowedProducts.COIL.isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(MaterialRegistry.AllowedProducts.COIL), "ooo", "o o", "ooo",'o', MaterialRegistry.AllowedProducts.INGOT.name().toLowerCase() + str));
			}

			if(MaterialRegistry.AllowedProducts.FAN.isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.FAN), "p p", " r ", "p p", 'p', AllowedProducts.PLATE.name().toLowerCase() + str, 'r', AllowedProducts.STICK.name().toLowerCase() + str));
				}
			}	
		}

		//Handle items from other mods
		if(zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods) {
			for(Entry<AllowedProducts, HashSet<String>> entry : modProducts.entrySet()) {
				if(entry.getKey() == AllowedProducts.PLATE) {
					for(String str : entry.getValue()) {
						MaterialRegistry.Materials material = MaterialRegistry.Materials.valueOfSafe(str.toUpperCase());

						if(OreDictionary.doesOreNameExist("ingot" + str) && (material == null || !AllowedProducts.PLATE.isOfType(material.getAllowedProducts())) )
							RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, OreDictionary.getOres("plate" + str).get(0), 300, 200, "ingot" + str);
					}
				}
				else if(entry.getKey() == AllowedProducts.STICK) {
					for(String str : entry.getValue()) {
						MaterialRegistry.Materials material = MaterialRegistry.Materials.valueOfSafe(str.toUpperCase());

						if(OreDictionary.doesOreNameExist("ingot" + str) && (material == null || !AllowedProducts.STICK.isOfType(material.getAllowedProducts())) ) {

							//GT registers rods as sticks
							if(OreDictionary.doesOreNameExist("rod" + str))
								RecipesMachine.getInstance().addRecipe(TileLathe.class, OreDictionary.getOres("rod" + str).get(0), 300, 200, "ingot" + str);
							else if(OreDictionary.doesOreNameExist("stick" + str)) {
								RecipesMachine.getInstance().addRecipe(TileLathe.class, OreDictionary.getOres("stick" + str).get(0), 300, 200, "ingot" + str);
							}

						}

					}
				}
			}
		}

		//Register buckets
		BucketHandler.INSTANCE.registerBucket(AdvancedRocketryBlocks.blockFuelFluid, AdvancedRocketryItems.itemBucketRocketFuel);
		FluidContainerRegistry.registerFluidContainer(AdvancedRocketryFluids.fluidRocketFuel, new ItemStack(AdvancedRocketryItems.itemBucketRocketFuel));

		//Register mixed material's recipes
		for(MixedMaterial material : MaterialRegistry.getMixedMaterialList()) {
			RecipesMachine.getInstance().addRecipe(material.getMachine(), material.getProducts(), 100, 10, material.getInput());
		}

		//Register space dimension
		net.minecraftforge.common.DimensionManager.registerProviderType(zmaster587.advancedRocketry.api.Configuration.spaceDimId, WorldProviderSpace.class, true);
		net.minecraftforge.common.DimensionManager.registerDimension(zmaster587.advancedRocketry.api.Configuration.spaceDimId,zmaster587.advancedRocketry.api.Configuration.spaceDimId);
	}

	@EventHandler
	public void serverStarted(FMLServerStartingEvent event) {
		event.registerServerCommand(new WorldCommand());


		//Register hard coded dimensions
		if(!zmaster587.advancedRocketry.api.dimension.DimensionManager.getInstance().loadDimensions(zmaster587.advancedRocketry.api.dimension.DimensionManager.filePath)) {

			if(Loader.isModLoaded("GalacticraftCore"))
				zmaster587.advancedRocketry.api.Configuration.MoonId = ConfigManagerCore.idDimensionMoon;
			
			DimensionProperties dimensionProperties = new DimensionProperties(zmaster587.advancedRocketry.api.Configuration.MoonId);
			dimensionProperties.atmosphereDensity = 0;
			dimensionProperties.averageTemperature = 20;
			dimensionProperties.gravitationalMultiplier = .166f; //Actual moon value
			dimensionProperties.name = "Luna";
			dimensionProperties.orbitalDist = 150;
			dimensionProperties.addBiome(AdvancedRocketryBiomes.moonBiome);

			dimensionProperties.setParentPlanet(0);
			dimensionProperties.setStar(DimensionManager.getSol());
			dimensionProperties.isNativeDimension = !Loader.isModLoaded("GalacticraftCore");
			DimensionManager.getInstance().registerDimNoUpdate(dimensionProperties, !Loader.isModLoaded("GalacticraftCore"));


			Random random = new Random(System.currentTimeMillis());

			for(int i = 0; i < 6; i++) {
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
						moonProperties.setParentPlanet(properties.getId());
					}
				}

				if(i == 6) {
					List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
					biomes.add(AdvancedRocketryBiomes.alienForest);

					properties.setBiomes(biomes);
				}
			}
		}
	}


	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		zmaster587.advancedRocketry.api.dimension.DimensionManager.getInstance().unregisterAllDimensions();
	}
	
    @SideOnly(Side.CLIENT)
	public void onTextureStitch(TextureStitchEvent.Post event) {
    	if(event.map.getTextureType() == 0) {
    		//IIcon texture = event.map.registerIcon("advancedrocketry:fluid/oxygen_still");
    		AdvancedRocketryFluids.fluidRocketFuel.setIcons(event.map.registerIcon("advancedrocketry:fluid/oxygen_still"));
    		AdvancedRocketryFluids.fluidHydrogen.setIcons(event.map.registerIcon("advancedrocketry:fluid/oxygen_still"));
    		AdvancedRocketryFluids.fluidOxygen.setIcons(event.map.registerIcon("advancedrocketry:fluid/oxygen_still"));
    	}
	}
	
	@SubscribeEvent
	public void registerOre(OreRegisterEvent event) {
		if(!zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods)
			return;
		
		for(AllowedProducts product : AllowedProducts.values() ) {
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
			HashSet<String> list = modProducts.get(AllowedProducts.STICK);
			if(list == null) {
				list = new HashSet<String>();
				modProducts.put(AllowedProducts.STICK, list);
			}

			list.add(event.Name.substring("stick".length()));
		}

	}
}
