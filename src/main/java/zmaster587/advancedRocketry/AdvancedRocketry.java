package zmaster587.advancedRocketry;


import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.Inventory.GuiHandler;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.FuelRegistry;
import zmaster587.advancedRocketry.api.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.block.BlockAlphaTexture;
import zmaster587.advancedRocketry.block.BlockBlastBrick;
import zmaster587.advancedRocketry.block.BlockBlastFurnace;
import zmaster587.advancedRocketry.block.BlockLaser;
import zmaster587.advancedRocketry.block.BlockLightSource;
import zmaster587.advancedRocketry.block.BlockLinkedHorizontalTexture;
import zmaster587.advancedRocketry.block.BlockPlanetSoil;
import zmaster587.advancedRocketry.block.BlockQuartzCrucible;
import zmaster587.advancedRocketry.block.BlockRocketMotor;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.block.BlockFuelTank;
import zmaster587.advancedRocketry.block.BlockTile;
import zmaster587.advancedRocketry.block.multiblock.BlockHatch;
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
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
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
import zmaster587.advancedRocketry.tile.TileMissionController;
import zmaster587.advancedRocketry.tile.TileModelRender;
import zmaster587.advancedRocketry.tile.TileOutputHatch;
import zmaster587.advancedRocketry.tile.TileRFBattery;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import zmaster587.advancedRocketry.tile.Satellite.TileEntitySatelliteControlCenter;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteBuilder;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.data.TileDataBus;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityFuelingStation;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityMoniteringStation;
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileEntityBlastFurnace;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.tile.multiblock.TilePlaceholder;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetAnalyser;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;
import zmaster587.advancedRocketry.world.WorldTypePlanetGen;
import zmaster587.advancedRocketry.world.biome.BiomeGenAlienForest;
import zmaster587.advancedRocketry.world.biome.BiomeGenHotDryRock;
import zmaster587.advancedRocketry.world.biome.BiomeGenMoon;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;


@Mod(modid="advancedRocketry", name="Advanced Rocketry", version="0.0.1", dependencies="required-after:libVulpes")
public class AdvancedRocketry {
	public static final String modId = "advancedRocketry";

	@SidedProxy(clientSide="zmaster587.advancedRocketry.client.ClientProxy", serverSide="zmaster587.advancedRocketry.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(value = modId)
	public static AdvancedRocketry instance;
	public static WorldType planetWorldType;
	
	public static CompatibilityMgr compat = new CompatibilityMgr();

	
	private static CreativeTabs tabAdvRocketry = new CreativeTabs("advancedRocketry") {
		
		@Override
		public Item getTabIconItem() {
			return AdvancedRocketryItems.itemSatelliteIdChip;
		}
	};
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		
		//Configuration  ---------------------------------------------------------------------------------------------
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		zmaster587.advancedRocketry.util.Configuration.buildSpeedMultiplier = (float) config.get(Configuration.CATEGORY_GENERAL, "buildSpeedMultiplier", 1f, "Multiplier for the build speed of the Rocket Builder (0.5 is twice as fast 2 is half as fast").getDouble();
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
		
		AdvRocketryBlocks.blockSatelliteBuilder = new BlockTile(TileSatelliteBuilder.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvRocketryBlocks.blockSatelliteBuilder).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockSatelliteBuilder).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockSatelliteBuilder).setFrontTexture("Advancedrocketry:satelliteAssembler");
		AdvRocketryBlocks.blockSatelliteBuilder.setBlockName("satelliteBuilder");
		
		AdvRocketryBlocks.blockSatelliteControlCenter = new BlockTile(TileEntitySatelliteControlCenter.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvRocketryBlocks.blockSatelliteControlCenter).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockSatelliteControlCenter).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockSatelliteControlCenter).setFrontTexture("Advancedrocketry:MonitorSatellite");
		AdvRocketryBlocks.blockSatelliteControlCenter.setBlockName("satelliteMonitor");
		
		/*AdvRocketryBlocks.blockChipStorage = new BlockTile(TileChipStorage.class, GuiHandler.guiId.MODULAR.ordinal()).setCreativeTab(tabAdvRocketry);
		((BlockTile) AdvRocketryBlocks.blockChipStorage).setSideTexture("Advancedrocketry:machineGeneric", "Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockChipStorage).setTopTexture("Advancedrocketry:machineGeneric");
		((BlockTile) AdvRocketryBlocks.blockChipStorage).setFrontTexture("Advancedrocketry:machineStorage");
		AdvRocketryBlocks.blockChipStorage.setBlockName("chipStorage");*/
		
		
		AdvRocketryBlocks.blockMoonTurf = new BlockPlanetSoil().setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("turf").setBlockTextureName("advancedrocketry:moon_turf").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockHotTurf = new BlockPlanetSoil().setMapColor(MapColor.netherrackColor).setHardness(0.5F).setStepSound(Block.soundTypeGravel).setBlockName("hotDryturf").setBlockTextureName("advancedrocketry:hotdry_turf").setCreativeTab(tabAdvRocketry);

		AdvRocketryBlocks.blockHatch = new BlockHatch(Material.rock).setBlockName("hatch").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockPlaceHolder = new BlockMultiblockPlaceHolder().setBlockName("placeHolder").setBlockTextureName("advancedrocketry:machineGeneric").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockRFBattery = new BlockRFBattery(Material.rock).setBlockName("rfBattery").setBlockTextureName("advancedrocketry:batteryRF").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockStructureBlock = new BlockAlphaTexture(Material.rock).setBlockName("structureMachine").setBlockTextureName("advancedrocketry:structureBlock").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockAlienWood = new BlockAlienWood().setBlockName("log").setBlockTextureName("advancedrocketry:log").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockAlienLeaves = new BlockAlienLeaves().setBlockName("leaves2").setBlockTextureName("leaves").setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockAlienSapling = new BlockAlienSapling().setBlockName("sapling").setBlockTextureName("advancedrocketry:sapling").setCreativeTab(tabAdvRocketry);
		
		AdvRocketryBlocks.blockLightSource = new BlockLightSource();
		AdvRocketryBlocks.blockSpaceLaser = new BlockLaser();
		AdvRocketryBlocks.blockBlastFurnace = new BlockBlastFurnace().setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockBlastBrick = new BlockBlastBrick().setCreativeTab(tabAdvRocketry);
		AdvRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible();

		AdvRocketryBlocks.blockPrecisionAssembler = (BlockMultiblockMachine)new BlockMultiblockMachine(TilePrecisionAssembler.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("precisionAssemblingMachine").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockPrecisionAssembler).setFrontTexture("advancedrocketry:PrecisionAssemblerFront", "advancedrocketry:PrecisionAssemblerFront_Active");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockPrecisionAssembler).setSideTexture("advancedrocketry:machineGeneric");

		AdvRocketryBlocks.blockCuttingMachine = (BlockMultiblockMachine)new BlockMultiblockMachine(TileCuttingMachine.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("cuttingMachine").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockCuttingMachine).setFrontTexture("advancedrocketry:CuttingMachine", "advancedrocketry:CuttingMachine_active");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockCuttingMachine).setSideTexture("advancedrocketry:machineGeneric");

		AdvRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(TileCrystallizer.class, GuiHandler.guiId.MODULAR.ordinal()).setBlockName("Crystallizer").setCreativeTab(tabAdvRocketry);
		((BlockMultiblockMachine) AdvRocketryBlocks.blockCrystallizer).setSideTexture("Advancedrocketry:Crystallizer", "Advancedrocketry:Crystallizer_active");
		((BlockMultiblockMachine) AdvRocketryBlocks.blockCrystallizer).setTopTexture("Advancedrocketry:machineGeneric");
		
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
		GameRegistry.registerBlock(AdvRocketryBlocks.blockBlastFurnace, "blastFurnace");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockBlastBrick, "utilBlock");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockQuartzCrucible, "quartzcrucible");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockCrystallizer, "crystallizer");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockCuttingMachine, "cuttingMachine");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockAlienWood, "alienWood");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockAlienLeaves, "alienLeaves");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockAlienSapling, "alienSapling");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockObservatory, "observatory");
		GameRegistry.registerBlock(AdvRocketryBlocks.blockSatelliteControlCenter, AdvRocketryBlocks.blockSatelliteControlCenter.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockPlanetAnalyser, AdvRocketryBlocks.blockPlanetAnalyser.getUnlocalizedName());
		GameRegistry.registerBlock(AdvRocketryBlocks.blockGuidanceComputer, AdvRocketryBlocks.blockGuidanceComputer.getUnlocalizedName());
		//GameRegistry.registerBlock(AdvRocketryBlocks.blockChipStorage, AdvRocketryBlocks.blockChipStorage.getUnlocalizedName());
		
		//Items -------------------------------------------------------------------------------------
		AdvancedRocketryItems.itemIngot = new ItemIngredient(2).setUnlocalizedName("ingot").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemBoule =  new ItemIngredient(1).setUnlocalizedName("boule").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemNugget = new ItemIngredient(1).setUnlocalizedName("nugget").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemWafer = new ItemIngredient(1).setUnlocalizedName("wafer").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemCircuitPlate = new ItemIngredient(1).setUnlocalizedName("circuitplate").setCreativeTab(tabAdvRocketry);
		AdvancedRocketryItems.itemIC = new ItemIngredient(1).setUnlocalizedName("circuitIC").setCreativeTab(tabAdvRocketry);
		
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
		
		//OreDict stuff
		OreDictionary.registerOre("ingotSilicon", new ItemStack(AdvancedRocketryItems.itemIngot,1,0));
		OreDictionary.registerOre("ingotSteel", new ItemStack(AdvancedRocketryItems.itemIngot,1,1));
		OreDictionary.registerOre("bouleSilicon", new ItemStack(AdvancedRocketryItems.itemBoule,1,0));
		OreDictionary.registerOre("nuggetSilicon", new ItemStack(AdvancedRocketryItems.itemNugget,1,0));
		OreDictionary.registerOre("waferSilicon", new ItemStack(AdvancedRocketryItems.itemWafer,1,0));
		
		//Item Registration
		GameRegistry.registerItem(AdvancedRocketryItems.quartzCrucible, "iquartzcrucible");
		GameRegistry.registerItem(AdvancedRocketryItems.oreScanner, "oreScanner");
		GameRegistry.registerItem(AdvancedRocketryItems.itemIngot, AdvancedRocketryItems.itemIngot.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemBoule, AdvancedRocketryItems.itemBoule.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.satellitePowerSource, AdvancedRocketryItems.satellitePowerSource.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.satellitePrimaryFunction, AdvancedRocketryItems.satellitePrimaryFunction.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemCircuitPlate, AdvancedRocketryItems.itemCircuitPlate.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemIC, AdvancedRocketryItems.itemIC.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemWafer, AdvancedRocketryItems.itemWafer.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemNugget, AdvancedRocketryItems.itemNugget.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemDataUnit, AdvancedRocketryItems.itemDataUnit.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSatellite, AdvancedRocketryItems.itemSatellite.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemSatelliteIdChip, AdvancedRocketryItems.itemSatelliteIdChip.getUnlocalizedName());
		GameRegistry.registerItem(AdvancedRocketryItems.itemPlanetIdChip,AdvancedRocketryItems.itemPlanetIdChip.getUnlocalizedName());
		
		//End Items

		EntityRegistry.registerModEntity(EntityDummy.class, "mountDummy", 0, this, 16, 20, false);
		EntityRegistry.registerModEntity(EntityRocket.class, "rocket", 1, this, 64, 20, true);

		GameRegistry.registerTileEntity(TileRocketBuilder.class, "rocketBuilder");
		GameRegistry.registerTileEntity(TileModelRender.class, "modelRenderer");
		GameRegistry.registerTileEntity(TileEntityFuelingStation.class, "fuelingStation");
		GameRegistry.registerTileEntity(TileEntityMoniteringStation.class, "monitoringStation");
		GameRegistry.registerTileEntity(TilePlaceholder.class, "placeHolder");
		
		

		GameRegistry.registerTileEntity(TileMissionController.class, "missionControlComp");
		GameRegistry.registerTileEntity(TileSpaceLaser.class, "spaceLaser");
		GameRegistry.registerTileEntity(TilePrecisionAssembler.class, "precisionAssembler");
		GameRegistry.registerTileEntity(TileObservatory.class, "observatory");
		GameRegistry.registerTileEntity(TileEntityBlastFurnace.class, "blastFurnace");
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
		//GameRegistry.registerTileEntity(TileChipStorage.class, "chipStorage");
		EntityRegistry.registerModEntity(EntityLaserNode.class, "laserNode", 0, instance, 256, 20, false);
		
		//Biomes --------------------------------------------------------------------------------------
		AdvancedRocketryBiomes.moonBiome = new BiomeGenMoon(90, false);
		AdvancedRocketryBiomes.alienForest = new BiomeGenAlienForest(91, false);
		AdvancedRocketryBiomes.hotDryBiome = new BiomeGenHotDryRock(92, false);
		
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.moonBiome);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.alienForest);
		AdvancedRocketryBiomes.instance.registerBiome(AdvancedRocketryBiomes.hotDryBiome);

	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderers();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		/*DimensionManager.registerProviderType(zmaster587.advancedRocketry.util.Configuration.MoonId, ProviderMoon.class, true);
		DimensionManager.registerDimension(zmaster587.advancedRocketry.util.Configuration.MoonId, zmaster587.advancedRocketry.util.Configuration.MoonId);


		//TEMPWORLD
		DimensionManager.registerProviderType(3, ProviderMoon.class, true);
		DimensionManager.registerDimension(3, 3);*/

		GameRegistry.addShapelessRecipe(new ItemStack(AdvRocketryBlocks.blockBlastBrick,4), new ItemStack(Items.potionitem,1,8195), new ItemStack(Items.potionitem,1,8201), Blocks.brick_block, Blocks.brick_block, Blocks.brick_block, Blocks.brick_block);
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockBlastFurnace), "aba","bcb", "aba", Character.valueOf('a'), Items.brick, Character.valueOf('b'), new ItemStack(Items.dye,1,15), Character.valueOf('c'), AdvRocketryBlocks.blockBlastBrick);
		GameRegistry.addShapedRecipe(new ItemStack(AdvancedRocketryItems.quartzCrucible), " a ", "aba", " a ", Character.valueOf('a'), Items.quartz, Character.valueOf('b'), Items.cauldron);

		//MACHINES
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockPrecisionAssembler), " a ", "bcd", "ef ", 'a', Blocks.dropper, 'b', Items.repeater, 'c', Blocks.iron_block, 'd', Blocks.furnace, 'e', Blocks.heavy_weighted_pressure_plate, 'f', Items.diamond);
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockCrystallizer), "aba", "bcb","ada", 'a', Items.quartz, 'b', Items.repeater, 'c', Blocks.iron_block, 'd', Blocks.obsidian);
		GameRegistry.addShapedRecipe(new ItemStack(AdvRocketryBlocks.blockCuttingMachine), "aba", "cdc", "a a", 'a', Items.diamond, 'b', Blocks.torch, 'c', Blocks.obsidian, 'd', Blocks.iron_block);

		//TEMP RECIPES
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemNugget,9,0), new ItemStack(AdvancedRocketryItems.itemIngot,1,0));
		GameRegistry.addShapelessRecipe(new ItemStack(AdvancedRocketryItems.itemSatelliteIdChip), new ItemStack(AdvancedRocketryItems.itemIC, 1, 0));
		
		//Cutting Machine
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemWafer, 6, 0), 600, 100, new ItemStack(AdvancedRocketryItems.itemBoule,1,0));
		RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(AdvancedRocketryItems.itemIC, 4, 0), 300, 100, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0));

		//Precision Assembler recipes
		RecipesMachine.getInstance().addRecipe(TilePrecisionAssembler.class, new ItemStack(AdvancedRocketryItems.itemCircuitPlate,1,0), 900, 10000, Items.gold_ingot, Items.redstone, "waferSilicon");


		//BlastFurnace
		RecipesBlastFurnace.getInstance().addFuel(Blocks.coal_block, 3000);
		RecipesBlastFurnace.getInstance().addRecipe(new ItemStack(AdvancedRocketryItems.itemIngot,1,0), 12000, Blocks.sand, 1);

		//Crystallizer
		RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, new ItemStack(AdvancedRocketryItems.itemBoule,1,0), 300, 200, new ItemStack(AdvancedRocketryItems.itemNugget,1,0), new ItemStack(AdvancedRocketryItems.itemIngot,1,0));


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
		/*ForgeChunkManager.setForcedChunkLoadingCallback(instance, new WorldEvents());

		proxy.registerKeyBinds();*/
	}
	
	@EventHandler
	public void serverStarted(FMLServerStartingEvent event) {
		event.registerServerCommand(new WorldCommand());
		zmaster587.advancedRocketry.world.DimensionManager.getInstance().loadDimensions(zmaster587.advancedRocketry.world.DimensionManager.filePath);
	}
	
	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		zmaster587.advancedRocketry.world.DimensionManager.getInstance().unregisterAllDimensions();
	}
}
