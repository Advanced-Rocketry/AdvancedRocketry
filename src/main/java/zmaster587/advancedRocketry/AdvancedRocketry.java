package zmaster587.advancedRocketry;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.api.MaterialRegistry.Materials;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.FuelRegistry;
import zmaster587.advancedRocketry.api.MixedMaterial;
import zmaster587.advancedRocketry.api.MaterialRegistry;
import zmaster587.advancedRocketry.api.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.block.BlockAlphaTexture;
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
import zmaster587.advancedRocketry.block.multiblock.BlockRFBattery;
import zmaster587.advancedRocketry.block.plant.BlockAlienLeaves;
import zmaster587.advancedRocketry.block.plant.BlockAlienSapling;
import zmaster587.advancedRocketry.block.plant.BlockAlienWood;
import zmaster587.advancedRocketry.command.WorldCommand;
import zmaster587.advancedRocketry.common.CommonProxy;
import zmaster587.advancedRocketry.entity.EntityDummy;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.item.ItemBlockMeta;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.item.ItemIngredient;
import zmaster587.advancedRocketry.item.ItemOreScanner;
import zmaster587.advancedRocketry.item.ItemPackedStructure;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemProjector;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.satellite.SatelliteDefunct;
import zmaster587.advancedRocketry.satellite.SatelliteDensity;
import zmaster587.advancedRocketry.satellite.SatelliteMassScanner;
import zmaster587.advancedRocketry.satellite.SatelliteOptical;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.TileInputHatch;
import zmaster587.advancedRocketry.tile.TileMaterial;
import zmaster587.advancedRocketry.tile.TileMissionController;
import zmaster587.advancedRocketry.tile.TileModelRender;
import zmaster587.advancedRocketry.tile.TileModelRenderRotatable;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import zmaster587.advancedRocketry.tile.TileRFBattery;
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
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileElectricArcFurnace;
import zmaster587.advancedRocketry.tile.multiblock.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.TileRollingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetAnalyser;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetSelector;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;
import zmaster587.advancedRocketry.util.Debugger;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.advancedRocketry.world.DimensionProperties;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import zmaster587.advancedRocketry.world.biome.BiomeGenHotDryRock;
import zmaster587.advancedRocketry.world.biome.BiomeGenMoon;
import zmaster587.advancedRocketry.world.biome.BiomeGenSpace;
import zmaster587.advancedRocketry.world.ore.OreGenerator;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.advancedRocketry.world.type.WorldTypePlanetGen;
import zmaster587.advancedRocketry.world.type.WorldTypeSpace;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;


@Mod(modid="advancedRocketry", name="Advanced Rocketry", version="0.2.0", dependencies="required-after:libVulpes")
public class AdvancedRocketry {
	public static final String modId = "advancedRocketry";

	@SidedProxy(clientSide="zmaster587.advancedRocketry.client.ClientProxy", serverSide="zmaster587.advancedRocketry.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(value = modId)
	public static AdvancedRocketry instance;
	public static WorldType planetWorldType;
	public static WorldType spaceWorldType;

	public static CompatibilityMgr compat = new CompatibilityMgr();
	public static Logger logger = Logger.getLogger(modId);


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
		//Configuration  ---------------------------------------------------------------------------------------------
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		final String oreGen = "Ore Generation";
		final String ROCKET = "Rockets";

		zmaster587.advancedRocketry.util.Configuration.buildSpeedMultiplier = (float) config.get(Configuration.CATEGORY_GENERAL, "buildSpeedMultiplier", 1f, "Multiplier for the build speed of the Rocket Builder (0.5 is twice as fast 2 is half as fast").getDouble();
		zmaster587.advancedRocketry.util.Configuration.MoonId = config.get(Configuration.CATEGORY_GENERAL,"moonId" , 2,"Dimension ID to use for the moon").getInt();

		zmaster587.advancedRocketry.util.Configuration.rocketRequireFuel = config.get(ROCKET, "rocketsRequireFuel", true, "Set to false if rockets should not require fuel to fly").getBoolean();
		zmaster587.advancedRocketry.util.Configuration.rocketThrustMultiplier = config.get(ROCKET, "thrustMultiplier", 1f, "Multiplier for per-engine thrust").getDouble();
		zmaster587.advancedRocketry.util.Configuration.fuelCapacityMultiplier = config.get(ROCKET, "fuelCapacityMultiplier", 1f, "Multiplier for per-tank capacity").getDouble();

		zmaster587.advancedRocketry.util.Configuration.generateCopper = config.get(oreGen, "GenerateCopper", true).getBoolean();
		zmaster587.advancedRocketry.util.Configuration.copperClumpSize = config.get(oreGen, "CopperPerClump", 6).getInt();
		zmaster587.advancedRocketry.util.Configuration.copperPerChunk = config.get(oreGen, "CopperPerChunk", 10).getInt();

		zmaster587.advancedRocketry.util.Configuration.generateTin = config.get(oreGen, "GenerateTin", true).getBoolean();
		zmaster587.advancedRocketry.util.Configuration.tinClumpSize = config.get(oreGen, "TinPerClump", 6).getInt();
		zmaster587.advancedRocketry.util.Configuration.tinPerChunk = config.get(oreGen, "TinPerChunk", 10).getInt();

		zmaster587.advancedRocketry.util.Configuration.generateRutile = config.get(oreGen, "GenerateRutile", true).getBoolean();
		zmaster587.advancedRocketry.util.Configuration.rutileClumpSize = config.get(oreGen, "RutilePerClump", 3).getInt();
		zmaster587.advancedRocketry.util.Configuration.rutilePerChunk = config.get(oreGen, "RutilePerChunk", 2).getInt();
		config.save();


		//Satellites ---------------------------------------------------------------------------------------------
		SatelliteRegistry.registerSatellite("defunct", SatelliteDefunct.class);
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
		AdvancedRocketryBlocks.blockControllerDummy = new BlockGeneric(Material.rock).setBlockName("blockGenericMachine").setBlockTextureName("Advancedrocketry:machineGeneric");
		AdvancedRocketryBlocks.blockPlatePress = new BlockPress().setBlockName("blockHandPress").setCreativeTab(tabAdvRocketry).setHardness(2f);

		AdvancedRocketryBlocks.blockRocketBuilder = new BlockTile(TileRocketBuilder.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setBlockName("rocketAssembler").setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvancedRocketryBlocks.blockRocketBuilder).setSideTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockRocketBuilder).setTopTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockRocketBuilder).setFrontTexture("advancedrocketry:MonitorFront");

		AdvancedRocketryBlocks.blockStationBuilder = new BlockTile(TileStationBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("stationAssembler").setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvancedRocketryBlocks.blockStationBuilder).setSideTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockStationBuilder).setTopTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockStationBuilder).setFrontTexture("advancedrocketry:MonitorFront");

		AdvancedRocketryBlocks.blockFuelingStation = new BlockTile(TileEntityFuelingStation.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("fuelStation").setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvancedRocketryBlocks.blockFuelingStation).setSideTexture("Advancedrocketry:FuelingMachine");
		((BlockTile) AdvancedRocketryBlocks.blockFuelingStation).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockFuelingStation).setFrontTexture("Advancedrocketry:FuelingMachine");

		AdvancedRocketryBlocks.blockMonitoringStation = new BlockTile(TileEntityMoniteringStation.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvancedRocketryBlocks.blockMonitoringStation).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockMonitoringStation).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockMonitoringStation).setFrontTexture("Advancedrocketry:MonitorRocket");
		AdvancedRocketryBlocks.blockMonitoringStation.setBlockName("monitoringstation");

		AdvancedRocketryBlocks.blockSatelliteBuilder = new BlockMultiblockMachine(TileSatelliteBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteBuilder).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteBuilder).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteBuilder).setFrontTexture("Advancedrocketry:satelliteAssembler");
		AdvancedRocketryBlocks.blockSatelliteBuilder.setBlockName("satelliteBuilder");

		AdvancedRocketryBlocks.blockSatelliteControlCenter = new BlockTile(TileEntitySatelliteControlCenter.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteControlCenter).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteControlCenter).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvancedRocketryBlocks.blockSatelliteControlCenter).setFrontTexture("Advancedrocketry:MonitorSatellite");
		AdvancedRocketryBlocks.blockSatelliteControlCenter.setBlockName("satelliteMonitor");

		//Arcfurnace
		AdvancedRocketryBlocks.blockArcFurnace = new BlockMultiblockMachine(TileElectricArcFurnace.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("electricArcFurnace").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockArcFurnace).setSideTexture("Advancedrocketry:BlastBrick");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockArcFurnace).setFrontTexture("Advancedrocketry:BlastBrickFront", "Advancedrocketry:BlastBrickFrontActive");

		AdvancedRocketryBlocks.blockMoonTurf = new BlockPlanetSoil().setMapColor(MapColor.snowColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("turf").setBlockTextureName("advancedrocketry:moon_turf").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockHotTurf = new BlockPlanetSoil().setMapColor(MapColor.netherrackColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("hotDryturf").setBlockTextureName("advancedrocketry:hotdry_turf").setCreativeTab(tabAdvRocketry);

		AdvancedRocketryBlocks.blockHatch = new BlockHatch(Material.rock).setBlockName("hatch").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockPlaceHolder = new BlockMultiblockPlaceHolder().setBlockName("placeHolder").setBlockTextureName("advancedrocketry:machineGeneric");
		AdvancedRocketryBlocks.blockRFBattery = new BlockRFBattery(Material.rock).setBlockName("rfBattery").setBlockTextureName("advancedrocketry:batteryRF").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockStructureBlock = new BlockAlphaTexture(Material.rock).setBlockName("structureMachine").setBlockTextureName("advancedrocketry:structureBlock").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockAlienWood = new BlockAlienWood().setBlockName("log").setBlockTextureName("advancedrocketry:log").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockAlienLeaves = new BlockAlienLeaves().setBlockName("leaves2").setBlockTextureName("leaves").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryBlocks.blockAlienSapling = new BlockAlienSapling().setBlockName("sapling").setBlockTextureName("advancedrocketry:sapling").setCreativeTab(tabAdvRocketry);

		AdvancedRocketryBlocks.blockLightSource = new BlockLightSource();
		AdvancedRocketryBlocks.blockSpaceLaser = new BlockLaser();
		AdvancedRocketryBlocks.blockBlastBrick = new BlockMultiBlockComponentVisible(Material.rock).setCreativeTab(tabAdvRocketry).setBlockName("blastBrick").setBlockTextureName("advancedRocketry:BlastBrick").setHardness(3F).setResistance(15F);
		AdvancedRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible();

		AdvancedRocketryBlocks.blockPrecisionAssembler = new BlockMultiblockMachine(TilePrecisionAssembler.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("precisionAssemblingMachine").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPrecisionAssembler).setFrontTexture("advancedrocketry:PrecisionAssemblerFront", "advancedrocketry:PrecisionAssemblerFront_Active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPrecisionAssembler).setSideTexture("advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockCuttingMachine = new BlockMultiblockMachine(TileCuttingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("cuttingMachine").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCuttingMachine).setFrontTexture("advancedrocketry:CuttingMachine", "advancedrocketry:CuttingMachine_active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCuttingMachine).setSideTexture("advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(TileCrystallizer.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("Crystallizer").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCrystallizer).setSideTexture("Advancedrocketry:Crystallizer", "Advancedrocketry:Crystallizer_active");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockCrystallizer).setTopTexture("Advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockLathe = new BlockMultiblockMachine(TileLathe.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("lathe").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockLathe).setFrontTexture("Advancedrocketry:controlPanel");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockLathe).setSideTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockLathe).setTopTexture("Advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockRollingMachine = new BlockMultiblockMachine(TileRollingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("rollingMachine").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockRollingMachine).setFrontTexture("Advancedrocketry:controlPanel");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockRollingMachine).setSideTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockRollingMachine).setTopTexture("Advancedrocketry:machineGeneric");

		AdvancedRocketryBlocks.blockPlanetAnalyser = new BlockMultiblockMachine(TilePlanetAnalyser.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setBlockName("planetanalyser").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPlanetAnalyser).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPlanetAnalyser).setSideTexture("advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockPlanetAnalyser).setFrontTexture("advancedrocketry:MonitorPlanet","advancedrocketry:MonitorPlanet_active");

		AdvancedRocketryBlocks.blockObservatory = (BlockMultiblockMachine) new BlockMultiblockMachine(TileObservatory.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("observatory").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockObservatory).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockObservatory).setSideTexture("advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvancedRocketryBlocks.blockObservatory).setFrontTexture("advancedrocketry:MonitorFrontMid","advancedrocketry:MonitorFrontMid");

		AdvancedRocketryBlocks.blockGuidanceComputer = new BlockTile(TileGuidanceComputer.class,GuiHandler.guiId.MODULAR.ordinal()).setBlockName("guidanceComputer").setCreativeTab(tabAdvRocketry);
		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer).setTopTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer).setSideTexture("Advancedrocketry:MonitorSide");
		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer).setFrontTexture("Advancedrocketry:guidanceComputer");

		AdvancedRocketryBlocks.blockPlanetSelector = new BlockTile(TilePlanetSelector.class,GuiHandler.guiId.MODULARFULLSCREEN.ordinal()).setBlockName("planetSelector").setCreativeTab(tabAdvRocketry);
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector).setTopTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector).setSideTexture("Advancedrocketry:MonitorSide");
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector).setFrontTexture("Advancedrocketry:guidanceComputer");

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
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockControllerDummy, AdvancedRocketryBlocks.blockControllerDummy.getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockPlatePress, AdvancedRocketryBlocks.blockPlatePress .getUnlocalizedName());
		GameRegistry.registerBlock(AdvancedRocketryBlocks.blockStationBuilder, AdvancedRocketryBlocks.blockStationBuilder.getUnlocalizedName());

		BlockOre.registerOres(tabAdvRocketryOres);


		//Items -------------------------------------------------------------------------------------
		AdvancedRocketryItems.itemWafer = new ItemIngredient(1).setUnlocalizedName("wafer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCircuitPlate = new ItemIngredient(1).setUnlocalizedName("circuitplate").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemIC = new ItemIngredient(2).setUnlocalizedName("circuitIC").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemBattery = new ItemIngredient(1).setUnlocalizedName("battery").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemMisc = new ItemIngredient(1).setUnlocalizedName("miscpart").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSawBlade = new ItemIngredient(1).setUnlocalizedName("sawBlade").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStationChip = new ItemStationChip().setUnlocalizedName("stationChip").setTextureName("advancedRocketry:stationIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSpaceStation = new ItemPackedStructure().setUnlocalizedName("station").setTextureName("advancedRocketry:SpaceStation");

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
		AdvancedRocketryItems.itemQuartzCrucible = (new ItemReed(AdvancedRocketryBlocks.blockQuartzCrucible)).setUnlocalizedName("qcrucible").setCreativeTab(tabAdvRocketry).setTextureName("advancedRocketry:qcrucible");

		AdvancedRocketryItems.itemSatellite = new ItemSatellite().setUnlocalizedName("satellite").setTextureName("advancedRocketry:satellite");
		AdvancedRocketryItems.itemSatelliteIdChip = new ItemSatelliteIdentificationChip().setUnlocalizedName("satelliteIdChip").setTextureName("advancedRocketry:satelliteIdChip").setCreativeTab(tabAdvRocketry);

		AdvancedRocketryItems.itemPlanetIdChip = new ItemPlanetIdentificationChip().setUnlocalizedName("planetIdChip").setTextureName("advancedRocketry:planetIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemHoloProjector = new ItemProjector().setUnlocalizedName("holoProjector").setTextureName("advancedRocketry:holoProjector").setCreativeTab(tabAdvRocketry);
		//OreDict stuff
		OreDictionary.registerOre("waferSilicon", new ItemStack(AdvancedRocketryItems.itemWafer,1,0));


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

		//Register multiblock items with the projector
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileCuttingMachine());
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileLathe());
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileCrystallizer());
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TilePrecisionAssembler());
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileObservatory());
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TilePlanetAnalyser());
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileRollingMachine());
		((ItemProjector)AdvancedRocketryItems.itemHoloProjector).registerMachine(new TileElectricArcFurnace());

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
		GameRegistry.registerTileEntity(TileRFBattery.class, "ARrfBattery");
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
		EntityRegistry.registerModEntity(EntityLaserNode.class, "laserNode", 0, instance, 256, 20, false);

		//Biomes --------------------------------------------------------------------------------------
		AdvancedRocketryBiomes.moonBiome = new BiomeGenMoon(90, true);
		AdvancedRocketryBiomes.alienForest = new BiomeGenAlienForest(91, true);
		AdvancedRocketryBiomes.hotDryBiome = new BiomeGenHotDryRock(92, true);
		AdvancedRocketryBiomes.spaceBiome = new BiomeGenSpace(93, true);

		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.alienForest);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.hotDryBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.spaceBiome);

	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{

		//Register Alloys
		MaterialRegistry.registerMixedMaterial(new MixedMaterial(TileElectricArcFurnace.class, "oreRutile", new ItemStack[] {MaterialRegistry.Materials.TITANIUM.getProduct(AllowedProducts.INGOT)}));

		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryBlocks.blockBlastBrick,4), new ItemStack(Items.potionitem,1,8195), new ItemStack(Items.potionitem,1,8201), Blocks.brick_block, Blocks.brick_block, Blocks.brick_block, Blocks.brick_block);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockArcFurnace), "aba","bcb", "aba", Character.valueOf('a'), Items.brick, Character.valueOf('b'), new ItemStack(Items.dye,1,15), Character.valueOf('c'), AdvancedRocketryBlocks.blockBlastBrick);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemQuartzCrucible), " a ", "aba", " a ", Character.valueOf('a'), Items.quartz, Character.valueOf('b'), Items.cauldron);
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvancedRocketryBlocks.blockPlatePress, "   ", " a ", "iii", 'a', Blocks.piston, 'i', Items.iron_ingot));

		GameRegistry.addRecipe(new ShapedOreRecipe(MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.ROD), "x  ", " x ", "  x", 'x', "ingotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(MaterialRegistry.getItemStackFromMaterialAndType(Materials.STEEL, AllowedProducts.ROD), "x  ", " x ", "  x", 'x', "ingotSteel"));

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
					RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, ore.getProduct(MaterialRegistry.AllowedProducts.CRYSTAL), 300, 200, MaterialRegistry.AllowedProducts.DUST.name().toLowerCase() + str);
			}

			if(MaterialRegistry.AllowedProducts.BOULE.isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, ore.getProduct(MaterialRegistry.AllowedProducts.BOULE), 300, 200, ore.getProduct(MaterialRegistry.AllowedProducts.INGOT), MaterialRegistry.AllowedProducts.NUGGET.name().toLowerCase() + str);
			}

			if(MaterialRegistry.AllowedProducts.ROD.isOfType(ore.getAllowedProducts()) && MaterialRegistry.AllowedProducts.INGOT.isOfType(ore.getAllowedProducts())) {
				for(String name : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(TileLathe.class, ore.getProduct(MaterialRegistry.AllowedProducts.ROD), 300, 200, MaterialRegistry.AllowedProducts.INGOT.name().toLowerCase() + name); //ore.getProduct(MaterialRegistry.AllowedProducts.INGOT));
			}

			if(MaterialRegistry.AllowedProducts.PLATE.isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames()) {
					RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, ore.getProduct(MaterialRegistry.AllowedProducts.PLATE), 300, 200, MaterialRegistry.AllowedProducts.INGOT.name().toLowerCase() + oreDictNames);
					if(AllowedProducts.BLOCK.isOfType(ore.getAllowedProducts()) || ore.isVanilla())
						RecipesMachine.getInstance().addRecipe(BlockPress.class, ore.getProduct(MaterialRegistry.AllowedProducts.PLATE), 0, 0, MaterialRegistry.AllowedProducts.BLOCK.name().toLowerCase() + oreDictNames);
				}
			}

			if(MaterialRegistry.AllowedProducts.COIL.isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(MaterialRegistry.AllowedProducts.COIL), "ooo", "o o", "ooo",'o', MaterialRegistry.AllowedProducts.INGOT.name().toLowerCase() + str));
			}
		}


		//Register mixed material's recipes
		for(MixedMaterial material : MaterialRegistry.getMixedMaterialList()) {
			RecipesMachine.getInstance().addRecipe(material.getMachine(), Arrays.asList(material.getProducts()), 100, 10, material.getInput());
		}


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

		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockObservatory), "gug", "pbp", "rrr", 'g', Blocks.glass_pane, 'u', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'b', AdvancedRocketryBlocks.blockStructureBlock, 'r', MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.ROD));

		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,0), "c", "m"," ", 'c', Blocks.chest, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,1), "m", "c"," ", 'c', Blocks.chest, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,2), "m", "c"," ", 'c', AdvancedRocketryItems.itemDataUnit, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockHatch,1,3), " x ", "xmx"," x ", 'x', "rodTitanium", 'm', AdvancedRocketryBlocks.blockStructureBlock));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryBlocks.blockRFBattery), " x ", "xmx"," x ", 'x', AdvancedRocketryItems.itemBattery, 'm', AdvancedRocketryBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockMotor), " cp", "rrp"," cp", 'c', "coilCopper", 'p', "plateSteel", 'r', "rodSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePowerSource,1,0), "rrr", "ggg","ppp", 'r', Items.redstone, 'g', Items.glowstone_dust, 'p', "plateGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemHoloProjector), "oro", "rpr", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'r', Items.redstone, 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 2), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'd', "crystalDilithium"));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 1), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.itemSatellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'd', new ItemStack(AdvancedRocketryItems.itemIC,1,1));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0), " x ","xox", " x ", 'x', "plateIron", 'o', "rodIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryBlocks.blockSawBlade,1,0), "xox", "x x", 'x', "plateIron", 'o', new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0)));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSpaceStationChip), MaterialRegistry.getItemStackFromMaterialAndType(Materials.DILITHIUM, AllowedProducts.CRYSTAL),MaterialRegistry.getItemStackFromMaterialAndType(Materials.DILITHIUM, AllowedProducts.CRYSTAL), new ItemStack(AdvancedRocketryItems.itemIC,1,0));

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

		//TEMP RECIPES
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemPlanetIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0), new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip));

		//Cutting Machine
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemIC, 4, 0), 300, 100, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0));
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemWafer, 4, 0), 300, 100, "bouleSilicon");

		//Lathe
		RecipesMachine.getInstance().addRecipe(TileLathe.class, MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.ROD), 300, 100, "ingotIron");


		//Precision Assembler recipes
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), 900, 100, Items.gold_ingot, Items.redstone, "waferSilicon");
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), 500, 60, "plateGold", AdvancedRocketryItems.itemIC, Items.redstone);
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemIC,1,1), 900, 50, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), Items.ender_eye, Items.redstone);

		//BlastFurnace
		RecipesMachine.getInstance().addRecipe(TileElectricArcFurnace.class, MaterialRegistry.Materials.SILICON.getProduct(AllowedProducts.INGOT), 12000, 1, Blocks.sand);
		RecipesMachine.getInstance().addRecipe(TileElectricArcFurnace.class, MaterialRegistry.Materials.STEEL.getProduct(AllowedProducts.INGOT), 6000, 1, "ingotIron", Blocks.coal_block);


		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		planetWorldType = new WorldTypePlanetGen("PlanetCold");
		spaceWorldType = new WorldTypeSpace("Space");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.registerEventHandlers();
		proxy.registerKeyBindings();

		//TODO: debug
		ClientCommandHandler.instance.registerCommand(new Debugger());

		PlanetEventHandler handle = new PlanetEventHandler();
		FMLCommonHandler.instance().bus().register(handle);
		MinecraftForge.EVENT_BUS.register(handle);
		FMLCommonHandler.instance().bus().register(DimensionManager.getSpaceManager());

		PacketHandler.init();
		FuelRegistry.instance.registerFuel(FuelType.LIQUID, FluidRegistry.WATER, 100);

		GameRegistry.registerWorldGenerator(new OreGenerator(), 100);

		/*ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());

		proxy.registerKeyBinds();*/

		//Register space dimension
		net.minecraftforge.common.DimensionManager.registerProviderType(zmaster587.advancedRocketry.util.Configuration.space, WorldProviderSpace.class, true);
		net.minecraftforge.common.DimensionManager.registerDimension(zmaster587.advancedRocketry.util.Configuration.space,zmaster587.advancedRocketry.util.Configuration.space);
	}

	@EventHandler
	public void serverStarted(FMLServerStartingEvent event) {
		event.registerServerCommand(new WorldCommand());

		zmaster587.advancedRocketry.world.DimensionManager.getInstance().loadDimensions(zmaster587.advancedRocketry.world.DimensionManager.filePath);
		//Register hard coded dimensions
		if(!DimensionManager.getInstance().isDimensionCreated(zmaster587.advancedRocketry.util.Configuration.MoonId)) {
			DimensionProperties dimensionProperties = new DimensionProperties(zmaster587.advancedRocketry.util.Configuration.MoonId);
			dimensionProperties.atmosphereDensity = 0;
			dimensionProperties.averageTemperature = 20;
			dimensionProperties.gravitationalMultiplier = .166f; //Actual moon value
			dimensionProperties.name = "Luna";
			dimensionProperties.orbitalDist = 150;
			dimensionProperties.addBiome(AdvancedRocketryBiomes.moonBiome);

			dimensionProperties.setParentPlanet(0);
			dimensionProperties.setStar(DimensionManager.getSol());
			DimensionManager.getInstance().registerDimNoUpdate(dimensionProperties);

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
		zmaster587.advancedRocketry.world.DimensionManager.getInstance().unregisterAllDimensions();
	}
}
