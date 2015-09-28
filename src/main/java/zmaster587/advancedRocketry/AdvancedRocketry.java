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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.api.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.api.MaterialRegistry.Materials;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.FuelRegistry;
import zmaster587.advancedRocketry.api.MixedMaterial;
import zmaster587.advancedRocketry.api.MaterialRegistry;
import zmaster587.advancedRocketry.api.PlayerDataHandler;
import zmaster587.advancedRocketry.api.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.block.BlockAlphaTexture;
import zmaster587.advancedRocketry.block.BlockGeneric;
import zmaster587.advancedRocketry.block.BlockLaser;
import zmaster587.advancedRocketry.block.BlockLightSource;
import zmaster587.advancedRocketry.block.BlockLinkedHorizontalTexture;
import zmaster587.advancedRocketry.block.BlockMaterial;
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
import zmaster587.advancedRocketry.item.ItemMaterialBlock;
import zmaster587.advancedRocketry.item.ItemOreScanner;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemProjector;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.recipe.RecipesBlastFurnace;
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
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.advancedRocketry.world.DimensionProperties;
import zmaster587.advancedRocketry.world.WorldTypePlanetGen;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import zmaster587.advancedRocketry.world.biome.BiomeGenHotDryRock;
import zmaster587.advancedRocketry.world.biome.BiomeGenMoon;
import zmaster587.advancedRocketry.world.ore.OreGenerator;
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
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.GameRegistry;


@Mod(modid="advancedRocketry", name="Advanced Rocketry", version="0.0.1a", dependencies="required-after:libVulpes")
public class AdvancedRocketry {
	public static final String modId = "advancedRocketry";

	@SidedProxy(clientSide="zmaster587.advancedRocketry.client.ClientProxy", serverSide="zmaster587.advancedRocketry.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(value = modId)
	public static AdvancedRocketry instance;
	public static WorldType planetWorldType;
	
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
			return Item.getItemFromBlock(AdvRocketryBlocks.blockOre.get(0));
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
		zmaster587.advancedRocketry.util.Configuration.copperClumpSize = config.get(oreGen, "CopperPerClump", 16).getInt();
		zmaster587.advancedRocketry.util.Configuration.copperPerChunk = config.get(oreGen, "CopperPerChunk", 10).getInt();
		
		zmaster587.advancedRocketry.util.Configuration.generateTin = config.get(oreGen, "GenerateTin", true).getBoolean();
		zmaster587.advancedRocketry.util.Configuration.tinClumpSize = config.get(oreGen, "TinPerClump", 16).getInt();
		zmaster587.advancedRocketry.util.Configuration.tinPerChunk = config.get(oreGen, "TinPerChunk", 10).getInt();
		
		zmaster587.advancedRocketry.util.Configuration.generateRutile = config.get(oreGen, "GenerateRutile", true).getBoolean();
		zmaster587.advancedRocketry.util.Configuration.rutileClumpSize = config.get(oreGen, "RutilePerClump", 16).getInt();
		zmaster587.advancedRocketry.util.Configuration.rutilePerChunk = config.get(oreGen, "RutilePerChunk", 10).getInt();
		config.save();
		
		
		//Satellites ---------------------------------------------------------------------------------------------
		SatelliteRegistry.registerSatellite("defunct", SatelliteDefunct.class);
		SatelliteRegistry.registerSatellite("optical", SatelliteOptical.class);
		SatelliteRegistry.registerSatellite("density", SatelliteDensity.class);
		SatelliteRegistry.registerSatellite("mass", SatelliteMassScanner.class);
		
		
		//Blocks -------------------------------------------------------------------------------------
		AdvRocketryBlocks.launchpad = new BlockLinkedHorizontalTexture(Material.rock).setBlockName("pad").setCreativeTab(tabAdvRocketry).setBlockTextureName("advancedrocketry:rocketPad");
		AdvRocketryBlocks.structureTower = new BlockAlphaTexture(Material.rock).setBlockName("structuretower").setCreativeTab(tabAdvRocketry).setBlockTextureName("advancedrocketry:structuretower");
		AdvRocketryBlocks.genericSeat = new BlockSeat(Material.cloth).setBlockName("seat").setCreativeTab(tabAdvRocketry).setBlockTextureName("minecraft:wool_colored_silver");
		AdvRocketryBlocks.blockEngine = new BlockRocketMotor(Material.rock).setBlockName("rocket").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockFuelTank = new BlockFuelTank(Material.rock).setBlockName("fuelTank").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockSawBlade = new BlockRotatableModel(Material.rock, TileModelRender.models.SAWBLADE.ordinal()).setCreativeTab(tabAdvRocketry).setBlockName("sawBlade");
		AdvRocketryBlocks.blockMotor = new BlockRotatableModel(Material.rock, TileModelRender.models.MOTOR.ordinal()).setCreativeTab(tabAdvRocketry).setBlockName("motor");
		AdvRocketryBlocks.blockConcrete = new BlockGeneric(Material.rock).setBlockName("concrete").setBlockTextureName("advancedRocketry:rocketPad_noEdge").setCreativeTab(tabAdvRocketry).setHardness(3f).setResistance(16f);
		AdvRocketryBlocks.blockPhantom = new BlockPhantom(Material.circuits).setBlockName("blockPhantom");
		AdvRocketryBlocks.blockControllerDummy = new BlockGeneric(Material.rock).setBlockName("blockGenericMachine").setBlockTextureName("Advancedrocketry:machineGeneric");
		AdvRocketryBlocks.blockPlatePress = new BlockPress().setBlockName("blockHandPress").setCreativeTab(tabAdvRocketry);
		
		AdvRocketryBlocks.rocketBuilder = new BlockTile(TileRocketBuilder.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setBlockName("rocketAssembler").setCreativeTab(tabAdvRocketry); //(BlockrocketBuilder) new BlockrocketBuilder(Material.rock).setBlockName("rocketAssembler").setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvRocketryBlocks.rocketBuilder).setSideTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.rocketBuilder).setTopTexture("advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.rocketBuilder).setFrontTexture("advancedrocketry:MonitorFront");
		
		AdvRocketryBlocks.blockFuelingStation = new BlockTile(TileEntityFuelingStation.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("fuelStation").setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvRocketryBlocks.blockFuelingStation).setSideTexture("Advancedrocketry:FuelingMachine");
		((BlockTile) AdvRocketryBlocks.blockFuelingStation).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockFuelingStation).setFrontTexture("Advancedrocketry:FuelingMachine");
		
		AdvRocketryBlocks.blockMonitoringStation = new BlockTile(TileEntityMoniteringStation.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvRocketryBlocks.blockMonitoringStation).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockMonitoringStation).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockMonitoringStation).setFrontTexture("Advancedrocketry:MonitorRocket");
		AdvRocketryBlocks.blockMonitoringStation.setBlockName("monitoringstation");
		
		AdvRocketryBlocks.blockSatelliteBuilder = new BlockMultiblockMachine(TileSatelliteBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvRocketryBlocks.blockSatelliteBuilder).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockSatelliteBuilder).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockSatelliteBuilder).setFrontTexture("Advancedrocketry:satelliteAssembler");
		AdvRocketryBlocks.blockSatelliteBuilder.setBlockName("satelliteBuilder");
		
		AdvRocketryBlocks.blockSatelliteControlCenter = new BlockTile(TileEntitySatelliteControlCenter.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvRocketryBlocks.blockSatelliteControlCenter).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockSatelliteControlCenter).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockSatelliteControlCenter).setFrontTexture("Advancedrocketry:MonitorSatellite");
		AdvRocketryBlocks.blockSatelliteControlCenter.setBlockName("satelliteMonitor");
		
		//Arcfurnace
		AdvRocketryBlocks.blockArcFurnace = new BlockMultiblockMachine(TileElectricArcFurnace.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("electricArcFurnace").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockArcFurnace).setSideTexture("Advancedrocketry:BlastBrick");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockArcFurnace).setFrontTexture("Advancedrocketry:BlastBrickFront", "Advancedrocketry:BlastBrickFrontActive");
		
		AdvRocketryBlocks.blockMoonTurf = new BlockPlanetSoil().setMapColor(MapColor.snowColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("turf").setBlockTextureName("advancedrocketry:moon_turf").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockHotTurf = new BlockPlanetSoil().setMapColor(MapColor.netherrackColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("hotDryturf").setBlockTextureName("advancedrocketry:hotdry_turf").setCreativeTab(tabAdvRocketry);

		AdvRocketryBlocks.blockHatch = new BlockHatch(Material.rock).setBlockName("hatch").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockPlaceHolder = new BlockMultiblockPlaceHolder().setBlockName("placeHolder").setBlockTextureName("advancedrocketry:machineGeneric");
		AdvRocketryBlocks.blockRFBattery = new BlockRFBattery(Material.rock).setBlockName("rfBattery").setBlockTextureName("advancedrocketry:batteryRF").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockStructureBlock = new BlockAlphaTexture(Material.rock).setBlockName("structureMachine").setBlockTextureName("advancedrocketry:structureBlock").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockAlienWood = new BlockAlienWood().setBlockName("log").setBlockTextureName("advancedrocketry:log").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockAlienLeaves = new BlockAlienLeaves().setBlockName("leaves2").setBlockTextureName("leaves").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockAlienSapling = new BlockAlienSapling().setBlockName("sapling").setBlockTextureName("advancedrocketry:sapling").setCreativeTab(tabAdvRocketry);
		
		AdvRocketryBlocks.blockLightSource = new BlockLightSource();
		AdvRocketryBlocks.blockSpaceLaser = new BlockLaser();
		AdvRocketryBlocks.blockBlastBrick = new BlockMultiBlockComponentVisible(Material.rock).setCreativeTab(tabAdvRocketry).setBlockName("blastBrick").setBlockTextureName("advancedRocketry:BlastBrick").setHardness(3F).setResistance(15F);
		AdvRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible();

		AdvRocketryBlocks.blockPrecisionAssembler = new BlockMultiblockMachine(TilePrecisionAssembler.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("precisionAssemblingMachine").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockPrecisionAssembler).setFrontTexture("advancedrocketry:PrecisionAssemblerFront", "advancedrocketry:PrecisionAssemblerFront_Active");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockPrecisionAssembler).setSideTexture("advancedrocketry:machineGeneric");

		AdvRocketryBlocks.blockCuttingMachine = new BlockMultiblockMachine(TileCuttingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("cuttingMachine").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockCuttingMachine).setFrontTexture("advancedrocketry:CuttingMachine", "advancedrocketry:CuttingMachine_active");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockCuttingMachine).setSideTexture("advancedrocketry:machineGeneric");

		AdvRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(TileCrystallizer.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("Crystallizer").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockCrystallizer).setSideTexture("Advancedrocketry:Crystallizer", "Advancedrocketry:Crystallizer_active");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockCrystallizer).setTopTexture("Advancedrocketry:machineGeneric");
		
		AdvRocketryBlocks.blockLathe = new BlockMultiblockMachine(TileLathe.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("lathe").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockLathe).setFrontTexture("Advancedrocketry:controlPanel");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockLathe).setSideTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockLathe).setTopTexture("Advancedrocketry:machineGeneric");
		
		AdvRocketryBlocks.blockRollingMachine = new BlockMultiblockMachine(TileRollingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("rollingMachine").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockRollingMachine).setFrontTexture("Advancedrocketry:controlPanel");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockRollingMachine).setSideTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockRollingMachine).setTopTexture("Advancedrocketry:machineGeneric");
		
		AdvRocketryBlocks.blockPlanetAnalyser = new BlockMultiblockMachine(TilePlanetAnalyser.class, GuiHandler.guiId.MODULARNOINV.ordinal()).setBlockName("planetanalyser").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockPlanetAnalyser).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockPlanetAnalyser).setSideTexture("advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockPlanetAnalyser).setFrontTexture("advancedrocketry:MonitorPlanet","advancedrocketry:MonitorPlanet_active");
		
		AdvRocketryBlocks.blockObservatory = (BlockMultiblockMachine) new BlockMultiblockMachine(TileObservatory.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("observatory").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockObservatory).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockObservatory).setSideTexture("advancedrocketry:machineGeneric");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockObservatory).setFrontTexture("advancedrocketry:MonitorFrontMid","advancedrocketry:MonitorFrontMid");
		
		AdvRocketryBlocks.blockGuidanceComputer = new BlockTile(TileGuidanceComputer.class,GuiHandler.guiId.MODULAR.ordinal()).setBlockName("guidanceComputer").setCreativeTab(tabAdvRocketry);
		((BlockTile)AdvRocketryBlocks.blockGuidanceComputer).setTopTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile)AdvRocketryBlocks.blockGuidanceComputer).setSideTexture("Advancedrocketry:MonitorSide");
		((BlockTile)AdvRocketryBlocks.blockGuidanceComputer).setFrontTexture("Advancedrocketry:guidanceComputer");
		
		AdvRocketryBlocks.blockPlanetSelector = new BlockTile(TilePlanetSelector.class,GuiHandler.guiId.MODULARFULLSCREEN.ordinal()).setBlockName("planetSelector").setCreativeTab(tabAdvRocketry);
		((BlockTile)AdvRocketryBlocks.blockPlanetSelector).setTopTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile)AdvRocketryBlocks.blockPlanetSelector).setSideTexture("Advancedrocketry:MonitorSide");
		((BlockTile)AdvRocketryBlocks.blockPlanetSelector).setFrontTexture("Advancedrocketry:guidanceComputer");
		
		GameRegistry.registerBlock(AdvRocketryBlocks.launchpad, "launchpad");
		GameRegistry.registerBlock(AdvRocketryBlocks.rocketBuilder, "rocketBuilder");
		GameRegistry.registerBlock(AdvRocketryBlocks.structureTower, "structureTower");
		GameRegistry.registerBlock(AdvRocketryBlocks.genericSeat, "seat");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockEngine, "rocketmotor");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockFuelTank, "fuelTank");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockFuelingStation, "fuelingStation");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockMonitoringStation, "blockMonitoringStation");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockSatelliteBuilder, "blockSatelliteBuilder");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockMoonTurf, "moonTurf");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockHotTurf, "blockHotTurf");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockHatch, ItemBlockMeta.class, "blockHatch");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockPlaceHolder, "blockPlaceholder");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockRFBattery, "rfBattery");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockStructureBlock, "blockStructureBlock");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockSpaceLaser, "laserController");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockPrecisionAssembler, "precisionassemblingmachine");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockBlastBrick, "utilBlock");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockQuartzCrucible, "quartzcrucible");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockCrystallizer, "crystallizer");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockCuttingMachine, "cuttingMachine");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockAlienWood, "alienWood");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockAlienLeaves, "alienLeaves");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockAlienSapling, "alienSapling");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockObservatory, "observatory");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockConcrete, AdvRocketryBlocks.blockConcrete.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockPlanetSelector, AdvRocketryBlocks.blockPlanetSelector.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockSatelliteControlCenter, AdvRocketryBlocks.blockSatelliteControlCenter.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockPlanetAnalyser, AdvRocketryBlocks.blockPlanetAnalyser.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockGuidanceComputer, AdvRocketryBlocks.blockGuidanceComputer.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockArcFurnace, AdvRocketryBlocks.blockArcFurnace.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockSawBlade, AdvRocketryBlocks.blockSawBlade.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockMotor, AdvRocketryBlocks.blockMotor.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockLathe, AdvRocketryBlocks.blockLathe.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockRollingMachine, AdvRocketryBlocks.blockRollingMachine.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockPhantom, AdvRocketryBlocks.blockPhantom.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockControllerDummy, AdvRocketryBlocks.blockControllerDummy.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockPlatePress, AdvRocketryBlocks.blockPlatePress .getUnlocalizedName());
		
		BlockOre.registerOres(tabAdvRocketryOres);
		for(Item item : AdvancedRocketryItems.itemOreProduct)
			item.setCreativeTab(tabAdvRocketryOres);
		
		
		//Items -------------------------------------------------------------------------------------
		AdvancedRocketryItems.itemWafer = new ItemIngredient(1).setUnlocalizedName("wafer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCircuitPlate = new ItemIngredient(1).setUnlocalizedName("circuitplate").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemIC = new ItemIngredient(2).setUnlocalizedName("circuitIC").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemBattery = new ItemIngredient(1).setUnlocalizedName("battery").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemMisc = new ItemIngredient(1).setUnlocalizedName("miscpart").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemSawBlade = new ItemIngredient(1).setUnlocalizedName("sawBlade").setCreativeTab(tabAdvRocketry);
		
		AdvancedRocketryItems.satellitePowerSource = new ItemIngredient(1).setUnlocalizedName("satellitePowerSource").setCreativeTab(tabAdvRocketry);
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.satellitePowerSource,1,0), new SatelliteProperties().setPowerGeneration(10));
		
		AdvancedRocketryItems.satellitePrimaryFunction = new ItemIngredient(3).setUnlocalizedName("satellitePrimaryFunction").setCreativeTab(tabAdvRocketry);
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 0), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteOptical.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 1), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteDensity.class)));
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 2), new SatelliteProperties().setSatelliteType(SatelliteRegistry.getKey(SatelliteMassScanner.class)));
		
		//TODO: move registration in the case we have more than one chip type
		AdvancedRocketryItems.itemDataUnit = new ItemData().setUnlocalizedName("dataUnit").setCreativeTab(tabAdvRocketry);
		SatelliteRegistry.registerSatelliteProperty(new ItemStack(AdvancedRocketryItems.itemDataUnit, 1, 0), new SatelliteProperties().setMaxData(1000));
		
		AdvancedRocketryItems.oreScanner = new ItemOreScanner().setUnlocalizedName("OreScanner").setTextureName("advancedRocketry:oreScanner");
		AdvancedRocketryItems.quartzCrucible = (new ItemReed(AdvRocketryBlocks.blockQuartzCrucible)).setUnlocalizedName("qcrucible").setCreativeTab(tabAdvRocketry).setTextureName("advancedRocketry:qcrucible");
		
		AdvancedRocketryItems.itemSatellite = new ItemSatellite().setUnlocalizedName("satellite").setTextureName("advancedRocketry:satellite");
		AdvancedRocketryItems.itemSatelliteIdChip = new ItemSatelliteIdentificationChip().setUnlocalizedName("satelliteIdChip").setTextureName("advancedRocketry:satelliteIdChip").setCreativeTab(tabAdvRocketry);
		
		AdvancedRocketryItems.itemPlanetIdChip = new ItemPlanetIdentificationChip().setUnlocalizedName("planetIdChip").setTextureName("advancedRocketry:planetIdChip").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemHoloProjector = new ItemProjector().setUnlocalizedName("holoProjector").setTextureName("advancedRocketry:holoProjector").setCreativeTab(tabAdvRocketry);
		//OreDict stuff
		OreDictionary.registerOre("waferSilicon", new ItemStack(AdvancedRocketryItems.itemWafer,1,0));
		
		
		//Item Registration
		GameRegistry.registerItem(AdvancedRocketryItems.quartzCrucible, "iquartzcrucible");
		GameRegistry.registerItem(AdvancedRocketryItems.oreScanner, "oreScanner");
		GameRegistry.registerItem(AdvancedRocketryItems.satellitePowerSource, AdvancedRocketryItems.satellitePowerSource.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.satellitePrimaryFunction, AdvancedRocketryItems.satellitePrimaryFunction.getUnlocalizedName());
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
		GameRegistry.registerTileEntity(TileRocketBuilder.class, "rocketBuilder");
		GameRegistry.registerTileEntity(TileModelRender.class, "modelRenderer");
		GameRegistry.registerTileEntity(TileEntityFuelingStation.class, "fuelingStation");
		GameRegistry.registerTileEntity(TileEntityMoniteringStation.class, "monitoringStation");
		GameRegistry.registerTileEntity(TilePlaceholder.class, "placeHolder");
		GameRegistry.registerTileEntity(TileMissionController.class, "missionControlComp");
		GameRegistry.registerTileEntity(TileSpaceLaser.class, "spaceLaser");
		GameRegistry.registerTileEntity(TilePrecisionAssembler.class, "precisionAssembler");
		GameRegistry.registerTileEntity(TileObservatory.class, "observatory");
		GameRegistry.registerTileEntity(zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer.class, "crystallizer");
		GameRegistry.registerTileEntity(TileOutputHatch.class, "outputHatch");
		GameRegistry.registerTileEntity(TileInputHatch.class, "inputHatch");
		GameRegistry.registerTileEntity(TileRFBattery.class, "rfBattery");
		GameRegistry.registerTileEntity(TileCuttingMachine.class, "cuttingmachine");
		GameRegistry.registerTileEntity(TileDataBus.class, "dataBus");
		GameRegistry.registerTileEntity(TileSatelliteHatch.class, "satelliteHatch");
		GameRegistry.registerTileEntity(TileSatelliteBuilder.class, "satelliteBuilder");
		GameRegistry.registerTileEntity(TileEntitySatelliteControlCenter.class, "TileEntitySatelliteControlCenter");
		GameRegistry.registerTileEntity(TilePlanetAnalyser.class, "planetAnalyser");
		GameRegistry.registerTileEntity(TileGuidanceComputer.class, "guidanceComputer");
		GameRegistry.registerTileEntity(TileElectricArcFurnace.class, "electricArcFurnace");
		GameRegistry.registerTileEntity(TilePlanetSelector.class, "TilePlanetSelector");
		GameRegistry.registerTileEntity(TileModelRenderRotatable.class, "TileModelRenderRotatable");
		GameRegistry.registerTileEntity(TileMaterial.class, "TileMaterial");
		GameRegistry.registerTileEntity(TileLathe.class, "TileLathe");
		GameRegistry.registerTileEntity(TileRollingMachine.class, "TileMetalBender");
		GameRegistry.registerTileEntity(TileSchematic.class, "TileSchematic");
		//GameRegistry.registerTileEntity(TileChipStorage.class, "chipStorage");
		EntityRegistry.registerModEntity(EntityLaserNode.class, "laserNode", 0, instance, 256, 20, false);
		
		//Biomes --------------------------------------------------------------------------------------
		AdvancedRocketryBiomes.moonBiome = new BiomeGenMoon(90, true);
		AdvancedRocketryBiomes.alienForest = new BiomeGenAlienForest(91, true);
		AdvancedRocketryBiomes.hotDryBiome = new BiomeGenHotDryRock(92, true);
		
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.alienForest);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.hotDryBiome);

	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		
		//Register Alloys
		MaterialRegistry.registerMixedMaterial(new MixedMaterial(TileElectricArcFurnace.class, "oreRutile", new ItemStack[] {MaterialRegistry.Materials.TITANIUM.getProduct(AllowedProducts.INGOT)}));
		
		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		GameRegistry.addShapelessRecipe(new ItemStack(AdvRocketryBlocks.blockBlastBrick,4), new ItemStack(Items.potionitem,1,8195), new ItemStack(Items.potionitem,1,8201), Blocks.brick_block, Blocks.brick_block, Blocks.brick_block, Blocks.brick_block);
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockArcFurnace), "aba","bcb", "aba", Character.valueOf('a'), Items.brick, Character.valueOf('b'), new ItemStack(Items.dye,1,15), Character.valueOf('c'), AdvRocketryBlocks.blockBlastBrick);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.quartzCrucible), " a ", "aba", " a ", Character.valueOf('a'), Items.quartz, Character.valueOf('b'), Items.cauldron);
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvRocketryBlocks.blockPlatePress, "   ", " a ", "iii", 'a', Blocks.piston, 'i', Items.iron_ingot));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.ROD), "x  ", " x ", "  x", 'x', "ingotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(MaterialRegistry.getItemStackFromMaterialAndType(Materials.STEEL, AllowedProducts.ROD), "x  ", " x ", "  x", 'x', "ingotSteel"));
		
		//AutoGenned Recipes
		for(MaterialRegistry.Materials ore : MaterialRegistry.Materials.values()) {
			if(MaterialRegistry.AllowedProducts.ORE.isOfType(ore.getAllowedProducts()) && MaterialRegistry.AllowedProducts.INGOT.isOfType(ore.getAllowedProducts()))
				GameRegistry.addSmelting(ore.getProduct(MaterialRegistry.AllowedProducts.ORE), ore.getProduct(AllowedProducts.INGOT), 0);
			
			if(MaterialRegistry.AllowedProducts.NUGGET.isOfType(ore.getAllowedProducts())) {
				ItemStack nugget = ore.getProduct(AllowedProducts.NUGGET);
				nugget.stackSize = 9;
				GameRegistry.addRecipe(new ShapelessOreRecipe(nugget, ore.getProduct(AllowedProducts.INGOT)));
				GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.INGOT), "ooo", "ooo", "ooo", 'o', ore.getProduct(AllowedProducts.NUGGET)));
			}
			
			if(MaterialRegistry.AllowedProducts.CRYSTAL.isOfType(ore.getAllowedProducts())) {
				RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, ore.getProduct(MaterialRegistry.AllowedProducts.CRYSTAL), 300, 200, ore.getProduct(MaterialRegistry.AllowedProducts.DUST));
			}
			
			if(MaterialRegistry.AllowedProducts.BOULE.isOfType(ore.getAllowedProducts())) {
				RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, ore.getProduct(MaterialRegistry.AllowedProducts.BOULE), 300, 200, ore.getProduct(MaterialRegistry.AllowedProducts.INGOT),ore.getProduct(MaterialRegistry.AllowedProducts.NUGGET));
			}
			
			if(MaterialRegistry.AllowedProducts.ROD.isOfType(ore.getAllowedProducts()) && MaterialRegistry.AllowedProducts.INGOT.isOfType(ore.getAllowedProducts())) {
				RecipesMachine.getInstance().addRecipe(TileLathe.class, ore.getProduct(MaterialRegistry.AllowedProducts.ROD), 300, 200, ore.getProduct(MaterialRegistry.AllowedProducts.INGOT));
			}
			
			if(MaterialRegistry.AllowedProducts.PLATE.isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames())
				RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, ore.getProduct(MaterialRegistry.AllowedProducts.PLATE), 300, 200, "ingot" + oreDictNames);
			}
			
			if(MaterialRegistry.AllowedProducts.COIL.isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
				GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(MaterialRegistry.AllowedProducts.COIL), "ooo", "o o", "ooo",'o', "ingot" + str));
			}
		}
		
		
		//Register mixed material's recipes
		for(MixedMaterial material : MaterialRegistry.getMixedMaterialList()) {
			RecipesMachine.getInstance().addRecipe(material.getMachine(), Arrays.asList(material.getProducts()), 100, 10, material.getInput());
		}
		
		
		//Supporting Materials
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemMisc,1,0), "lrl", "fgf", 'l', "dyeLime", 'r', Items.redstone, 'g', Blocks.glass_pane, 'f', Items.glowstone_dust));
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.genericSeat), "xxx", 'x', Blocks.wool);
		GameRegistry.addShapelessRecipe(new ItemStack(AdvRocketryBlocks.blockConcrete), Blocks.sand, Blocks.gravel, Items.water_bucket);
		GameRegistry.addRecipe(new ShapelessOreRecipe(AdvRocketryBlocks.launchpad, AdvRocketryBlocks.blockConcrete, "dyeBlack", "dyeYellow"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvRocketryBlocks.structureTower, "ooo", " o ", "ooo", 'o', "rodSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvRocketryBlocks.blockEngine, "sss", " t ","t t", 's', "ingotSteel", 't', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvRocketryBlocks.blockFuelTank, "s s", "p p", "s s", 'p', "plateSteel", 's', "rodSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(AdvRocketryBlocks.blockStructureBlock, "sps", "psp", "sps", 'p', "plateSteel", 's', "rodSteel"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemBattery,1,0), " c ","prp", "prp", 'c', "rodCopper", 'r', Items.redstone, 'p', "plateTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 0), "ppp", " g ", " l ", 'p', Blocks.glass_pane, 'g', Items.glowstone_dust, 'l', "plateGold"));
		
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockObservatory), "gug", "pbp", "rrr", 'g', Blocks.glass_pane, 'u', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'b', AdvRocketryBlocks.blockStructureBlock, 'r', MaterialRegistry.getItemStackFromMaterialAndType(Materials.IRON, AllowedProducts.ROD));
		
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockHatch,1,0), "c", "m"," ", 'c', Blocks.chest, 'm', AdvRocketryBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockHatch,1,1), "m", "c"," ", 'c', Blocks.chest, 'm', AdvRocketryBlocks.blockStructureBlock);
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockHatch,1,2), "m", "c"," ", 'c', AdvancedRocketryItems.itemDataUnit, 'm', AdvRocketryBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockHatch,1,3), " x ", "xmx"," x ", 'x', "rodTitanium", 'm', AdvRocketryBlocks.blockStructureBlock));
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockRFBattery), " x ", "xmx"," x ", 'x', AdvancedRocketryItems.itemBattery, 'm', AdvRocketryBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockMotor), " cp", "rrp"," cp", 'c', "coilCopper", 'p', "plateSteel", 'r', "rodSteel"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.satellitePowerSource,1,0), "rrr", "ggg","ppp", 'r', Items.redstone, 'g', Items.glowstone_dust, 'p', "plateGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemHoloProjector), "oro", "rpr", 'o', new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 0), 'r', Items.redstone, 'p', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 2), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'd', "crystalDilithium"));
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 1), "odo", "pcp", 'o', new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 0), 'p', new ItemStack(AdvancedRocketryItems.itemWafer,1,0), 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'd', new ItemStack(AdvancedRocketryItems.itemIC,1,1));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0), " x ","xox", " x ", 'x', "plateIron", 'o', "rodIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockSawBlade,1,0), "xox", "x x", 'x', "plateIron", 'o', new ItemStack(AdvancedRocketryItems.itemSawBlade,1,0)));
		
		
		//MACHINES
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockPrecisionAssembler), "ap ", "bcd", "ef ", 'a', Blocks.dropper, 'b', Items.repeater, 'c', AdvRocketryBlocks.blockStructureBlock, 'd', Blocks.furnace, 'e', Blocks.heavy_weighted_pressure_plate, 'f', Items.diamond, 'p', new ItemStack(AdvancedRocketryItems.itemMisc,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockCrystallizer), "ada", "bcb","aba", 'a', Items.quartz, 'b', Items.repeater, 'c', AdvRocketryBlocks.blockStructureBlock, 'd', new ItemStack(AdvancedRocketryItems.itemMisc,1,0));
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockCuttingMachine), "aba", "cdc", "a a", 'a', Items.diamond, 'b', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'c', Blocks.obsidian, 'd', AdvRocketryBlocks.blockStructureBlock);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockLathe), "rsr", "pbp", "prp", 'r', "rodIron", 'p', "plateSteel", 'b', AdvRocketryBlocks.blockStructureBlock, 's', new ItemStack(AdvancedRocketryItems.itemMisc,1,0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockRollingMachine), "psp", "pbp", "iti", 'p', "plateSteel", 's', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'b', AdvRocketryBlocks.blockStructureBlock, 'i', "blockIron",'t', "plateIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockMonitoringStation), "coc", "cbc", "cpc", 'c', "rodCopper", 'o', new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 0), 'b', AdvRocketryBlocks.blockStructureBlock, 'p', AdvancedRocketryItems.itemBattery));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockFuelingStation), "x", "b", "x", 'x', AdvRocketryBlocks.blockFuelTank, 'b', AdvRocketryBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockSatelliteControlCenter), "oso", "cbc", "rtr", 'o', new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 0), 's', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'c', "rodCopper", 'b', AdvRocketryBlocks.blockStructureBlock, 'r', Items.repeater, 't', AdvancedRocketryItems.itemBattery));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockSatelliteBuilder), "dht", "cbc", "mas", 'd', AdvancedRocketryItems.itemDataUnit, 'h', Blocks.hopper, 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,0), 'b', AdvRocketryBlocks.blockStructureBlock, 'm', AdvRocketryBlocks.blockMotor, 'a', Blocks.anvil, 's', AdvRocketryBlocks.blockSawBlade, 't', "plateTitanium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockPlanetAnalyser), "tst", "pbp", "cpc", 't', new ItemStack(AdvancedRocketryItems.itemIC,1,1), 's', new ItemStack(AdvancedRocketryItems.itemMisc,1,0), 'b', AdvRocketryBlocks.blockStructureBlock, 'p', "plateTin", 'c', AdvancedRocketryItems.itemPlanetIdChip));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockGuidanceComputer), "ctc", "rbr", "crc", 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,1), 't', "plateTitanium", 'r', Items.redstone, 'b', AdvRocketryBlocks.blockStructureBlock));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.blockPlanetSelector), "coc", "lbl", "cpc", 'c', new ItemStack(AdvancedRocketryItems.itemIC,1,1), 'o',new ItemStack(AdvancedRocketryItems.satellitePrimaryFunction, 1, 0), 'l', Blocks.lever, 'b', AdvRocketryBlocks.blockGuidanceComputer, 'p', Blocks.stone_button));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(AdvRocketryBlocks.rocketBuilder), "rrr", "pbp","ccc", 'r', "rodTitanium", 'p', "plateTitanium", 'b', AdvRocketryBlocks.blockStructureBlock, 'c', AdvRocketryBlocks.blockConcrete));
		
		//TEMP RECIPES
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0));
		
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
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.registerEventHandlers();
		proxy.registerKeyBindings();

		PlanetEventHandler handle = new PlanetEventHandler();
		FMLCommonHandler.instance().bus().register(handle);
		MinecraftForge.EVENT_BUS.register(handle);

		PacketHandler.init();
		FuelRegistry.instance.registerFuel(FuelType.LIQUID, FluidRegistry.WATER, 100);
		
		GameRegistry.registerWorldGenerator(new OreGenerator(), 100);
		
		/*ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());

		proxy.registerKeyBinds();*/
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
