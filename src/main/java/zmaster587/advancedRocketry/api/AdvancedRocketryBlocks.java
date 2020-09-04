package zmaster587.advancedRocketry.api;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import zmaster587.advancedRocketry.block.BlockAdvancedRocketMotor;
import zmaster587.advancedRocketry.block.BlockAstroBed;
import zmaster587.advancedRocketry.block.BlockBeacon;
import zmaster587.advancedRocketry.block.BlockElectricMushroom;
import zmaster587.advancedRocketry.block.BlockEnrichedLava;
import zmaster587.advancedRocketry.block.BlockFluid;
import zmaster587.advancedRocketry.block.BlockForceField;
import zmaster587.advancedRocketry.block.BlockForceFieldProjector;
import zmaster587.advancedRocketry.block.BlockFuelTank;
import zmaster587.advancedRocketry.block.BlockHalfTile;
import zmaster587.advancedRocketry.block.BlockIntake;
import zmaster587.advancedRocketry.block.BlockLandingPad;
import zmaster587.advancedRocketry.block.BlockLaser;
import zmaster587.advancedRocketry.block.BlockLightSource;
import zmaster587.advancedRocketry.block.BlockLinkedHorizontalTexture;
import zmaster587.advancedRocketry.block.BlockMiningDrill;
import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.block.BlockPressurizedFluidTank;
import zmaster587.advancedRocketry.block.BlockQuartzCrucible;
import zmaster587.advancedRocketry.block.BlockRedstoneEmitter;
import zmaster587.advancedRocketry.block.BlockRocketMotor;
import zmaster587.advancedRocketry.block.BlockSeal;
import zmaster587.advancedRocketry.block.BlockSeat;
import zmaster587.advancedRocketry.block.BlockStationModuleDockingPort;
import zmaster587.advancedRocketry.block.BlockSuitWorkstation;
import zmaster587.advancedRocketry.block.BlockTileNeighborUpdate;
import zmaster587.advancedRocketry.block.BlockTileRedstoneEmitter;
import zmaster587.advancedRocketry.block.BlockTileWithMultitooltip;
import zmaster587.advancedRocketry.block.BlockTorchUnlit;
import zmaster587.advancedRocketry.block.BlockTransciever;
import zmaster587.advancedRocketry.block.BlockWarpCore;
import zmaster587.advancedRocketry.block.BlockWarpShipMonitor;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.block.plant.BlockAlienLeaves;
import zmaster587.advancedRocketry.item.ItemBlockFluidTank;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.TilePump;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.TileSolarPanel;
import zmaster587.advancedRocketry.tile.TileStationBuilder;
import zmaster587.advancedRocketry.tile.TileStationDeployedAssembler;
import zmaster587.advancedRocketry.tile.TileSuitWorkStation;
import zmaster587.advancedRocketry.tile.Satellite.TileEntitySatelliteControlCenter;
import zmaster587.advancedRocketry.tile.Satellite.TileSatelliteBuilder;
import zmaster587.advancedRocketry.tile.cables.TileWirelessTransciever;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityFuelingStation;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityMoniteringStation;
import zmaster587.advancedRocketry.tile.multiblock.TileAstrobodyDataProcessor;
import zmaster587.advancedRocketry.tile.multiblock.TileAtmosphereTerraformer;
import zmaster587.advancedRocketry.tile.multiblock.TileBeacon;
import zmaster587.advancedRocketry.tile.multiblock.TileBiomeScanner;
import zmaster587.advancedRocketry.tile.multiblock.TileGravityController;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetSelector;
import zmaster587.advancedRocketry.tile.multiblock.TileRailgun;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceElevator;
import zmaster587.advancedRocketry.tile.multiblock.TileWarpCore;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileBlackHoleGenerator;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.advancedRocketry.tile.oxygen.TileOxygenCharger;
import zmaster587.advancedRocketry.tile.oxygen.TileOxygenVent;
import zmaster587.advancedRocketry.tile.station.TilePlanetaryHologram;
import zmaster587.advancedRocketry.tile.station.TileStationAltitudeController;
import zmaster587.advancedRocketry.tile.station.TileStationGravityController;
import zmaster587.advancedRocketry.tile.station.TileStationOrientationControl;
import zmaster587.advancedRocketry.tile.station.TileWarpShipMonitor;
import zmaster587.advancedRocketry.world.gen.WorldGenAlienTree;
import zmaster587.advancedRocketry.world.tree.AlienTree;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.BlockMotor;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.items.ItemBlockMeta;
import zmaster587.libVulpes.util.FluidUtils;

/**
 * Stores references to Advanced Rocketry's blocks
 *
 */
public class AdvancedRocketryBlocks {
	public static Block blockMissionComp;
	public static Block blockSpaceLaser;
	public static Block blockPrecisionAssembler;
	public static Block blockArcFurnace;
	public static Block blockBlastBrick;
	public static Block blockQuartzCrucible;
	public static Block blockCrystallizer;
	public static Block blockLathe;
	public static Block blockCuttingMachine;
	public static Block blockObservatory;
	public static Block blockPlanetAnalyser;
	public static Block blockLaunchpad;
	public static Block blockStructureTower;
	public static Block blockRocketBuilder;
	public static Block blockGenericSeat;
	public static Block blockEngine;
	public static Block blockFuelTank;
	public static Block blockFuelingStation;
	public static Block blockMonitoringStation, blockSatelliteBuilder, blockSatelliteControlCenter;
	public static Block blockChipStorage;
	public static Block blockMoonTurf, blockHotTurf;
	public static Block blockMultiMineOre;
	public static Block blockLightSource;
	public static Block blockAlienWood, blockAlienLeaves, blockAlienSapling;
	public static Block blockGuidanceComputer;
	public static Block blockLunarAnalyser;
	public static Block blockPlanetSelector;
	public static Block blockSawBlade;
	public static Block blockConcrete;
	public static Block blockRollingMachine;
	public static Block blockPlatePress;
	public static Block blockStationBuilder;
	public static Block blockElectrolyser;
	public static FlowingFluidBlock blockOxygenFluid;
	public static FlowingFluidBlock blockHydrogenFluid;
	public static Block blockChemicalReactor;
	public static Block blockFuelFluid;
	public static Block test;
	public static Block blockOxygenVent;
	public static Block blockOxygenScrubber;
	public static Block blockOxygenCharger;
	public static Block blockAirLock;
	public static Block blockLandingPad;
	public static Block blockWarpCore;
	public static Block blockWarpShipMonitor;
	public static Block blockOxygenDetection;
	public static Block blockUnlitTorch;
	public static Block blocksGeode;
	public static Block blockVitrifiedSand;
	public static Block blockCharcoalLog;
	public static Block blockElectricMushroom;
	public static Block blockCrystal;
	public static Block blockOrientationController;
	public static Block blockGravityController;
	public static Block blockDrill;
	public static Block blockFluidPipe;
	public static Block blockDataPipe;
	public static Block blockMicrowaveReciever;
	public static Block blockSolarPanel;
	public static Block blockSuitWorkStation;
	public static Block blockLoader;
	public static Block blockDataBus;
	public static Block blockFluidLoader;
	public static Block blockFluidUnloader;
	public static Block blockRocketLoader;
	public static Block blockRocketUnloader;
	public static Block blockBiomeScanner;
	public static Block blockAtmosphereTerraformer;
	public static Block blockDeployableRocketBuilder;
	public static Block blockPressureTank;
	public static Block blockIntake;
	public static FlowingFluidBlock blockNitrogenFluid;
	public static Block blockCircleLight;
	public static Block blockEnergyPipe;
	public static Block blockSolarGenerator;
	public static Block blockDockingPort;
	public static Block blockAltitudeController;
	public static Block blockRailgun;
	public static Block blockAstroBed;
	public static Block blockAdvEngine;
	public static Block blockPlanetHoloSelector;
	public static Block blockLens;
	public static Block blockForceField;
	public static Block blockForceFieldProjector;
	public static Block blockGravityMachine;
	public static Block blockPipeSealer;
	public static Block blockSpaceElevatorController;
	public static Block blockBeacon;
	public static Block blockAlienPlanks;
	public static Block blockThermiteTorch;
	public static Block blockTransciever;
	public static Block blockMoonTurfDark;
	public static Block blockBlackHoleGenerator;
	public static FlowingFluidBlock blockEnrichedLavaFluid;
	public static Block blockPump;
	public static Block blockCentrifuge;
	public static Block blockBasalt;
	public static Block blockLandingFloat;

	public static Block[] crystalBlocks;


	@SubscribeEvent(priority=EventPriority.HIGH)
	public static void registerBlocks(RegistryEvent.Register<Block> evt)
	{
		//Blocks -------------------------------------------------------------------------------------
		AbstractBlock.Properties machineLineProperties = AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f);
		AbstractBlock.Properties crystalProperties = AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f);

		AdvancedRocketryBlocks.blocksGeode = new Block(AbstractBlock.Properties.create(MaterialGeode.geode).hardnessAndResistance(6f, 2000F).harvestTool(ToolType.get("jackhammer"))).setRegistryName("geode");
		AdvancedRocketryBlocks.blockLaunchpad = new BlockLinkedHorizontalTexture(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f, 10f)).setRegistryName("pad");
		AdvancedRocketryBlocks.blockStructureTower = new BlockAlphaTexture(machineLineProperties).setRegistryName("structuretower");
		AdvancedRocketryBlocks.blockGenericSeat = new BlockSeat(AbstractBlock.Properties.create(Material.WOOL).hardnessAndResistance(0.5f)).setRegistryName("seat");
		AdvancedRocketryBlocks.blockEngine = new BlockRocketMotor(machineLineProperties).setRegistryName("rocket");
		AdvancedRocketryBlocks.blockAdvEngine = new BlockAdvancedRocketMotor(machineLineProperties).setRegistryName("advRocket");
		AdvancedRocketryBlocks.blockFuelTank = new BlockFuelTank(machineLineProperties).setRegistryName("fuelTank");
		AdvancedRocketryBlocks.blockSawBlade = new BlockMotor(machineLineProperties,1f).setRegistryName("sawBladeAssbly");

		AdvancedRocketryBlocks.blockConcrete = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f, 16f)).setRegistryName("concrete");
		AdvancedRocketryBlocks.blockPlatePress = new BlockPress(machineLineProperties).setRegistryName("blockHandPress");
		AdvancedRocketryBlocks.blockAirLock = new DoorBlock(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f, 8f)).setRegistryName("smallAirlockDoor");
		AdvancedRocketryBlocks.blockLandingPad = new BlockLandingPad(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f,3f)).setRegistryName("dockingPad");
		AdvancedRocketryBlocks.blockOxygenDetection = new BlockRedstoneEmitter(machineLineProperties,"advancedrocketry:atmosphereDetector_active").setRegistryName("atmosphereDetector");
		AdvancedRocketryBlocks.blockOxygenScrubber = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CO2_SCRUBBER).setRegistryName("scrubber");
		AdvancedRocketryBlocks.blockUnlitTorch = new BlockTorchUnlit(AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0)).setRegistryName("unlittorch");
		AdvancedRocketryBlocks.blockVitrifiedSand = new Block(AbstractBlock.Properties.create(Material.SAND).hardnessAndResistance(0.5f)).setRegistryName("vitrifiedSand");
		AdvancedRocketryBlocks.blockCharcoalLog = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD)).setRegistryName("charcoallog");
		AdvancedRocketryBlocks.blockElectricMushroom = new BlockElectricMushroom(AbstractBlock.Properties.create(Material.PLANTS).hardnessAndResistance(0.0f)).setRegistryName("electricMushroom");
		AdvancedRocketryBlocks.blockCrystal = new Block(crystalProperties).setRegistryName("crystal");
		crystalBlocks = new Block[] {AdvancedRocketryBlocks.blockCrystal};

		AdvancedRocketryBlocks.blockOrientationController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ORIENTATION_CONTROLLER).setRegistryName("orientationControl");
		AdvancedRocketryBlocks.blockGravityController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_GRAVITY_CONTROLLER).setRegistryName("gravityControl");
		AdvancedRocketryBlocks.blockAltitudeController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ALT_CONTROLLER).setRegistryName("altitudeController");
		AdvancedRocketryBlocks.blockOxygenCharger = new BlockHalfTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_OXYGEN_CHARGER).setRegistryName("oxygenCharger");
		AdvancedRocketryBlocks.blockOxygenVent = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_OXYGEN_VENT).setRegistryName("oxygenVent");
		AdvancedRocketryBlocks.blockCircleLight = new Block(AbstractBlock.Properties.create(Material.ROCK).func_235838_a_((p_235470_0_) -> {
			return 14;
		})).setRegistryName("circleLight");
		AdvancedRocketryBlocks.blockLens = new Block(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(0.3f)).setRegistryName("lens");
		AdvancedRocketryBlocks.blockRocketBuilder = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ROCKET_BUILDER).setRegistryName("rocketAssembler");
		AdvancedRocketryBlocks.blockForceField = new BlockForceField(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 3600000.0F).noDrops()).setRegistryName("forceField");
		AdvancedRocketryBlocks.blockForceFieldProjector = new BlockForceFieldProjector(machineLineProperties).setRegistryName("forceFieldProjector");
		AdvancedRocketryBlocks.blockDeployableRocketBuilder = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_STATION_DEPLOYED_ASSEMBLER).setRegistryName("deployableRocketAssembler");
		AdvancedRocketryBlocks.blockStationBuilder = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_STATION_BUILDER).setRegistryName("stationAssembler");
		AdvancedRocketryBlocks.blockFuelingStation = new BlockTileRedstoneEmitter(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_FUELING_STATION).setRegistryName("fuelStation");

		AdvancedRocketryBlocks.blockMonitoringStation = new BlockTileNeighborUpdate(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_MONITORIING_STATION).setRegistryName("monitoringstation");

		AdvancedRocketryBlocks.blockWarpShipMonitor = new BlockWarpShipMonitor(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_WARP_SHIP_CONTROLLER).setRegistryName("stationmonitor");

		AdvancedRocketryBlocks.blockSatelliteBuilder = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_SAT_BUILDER).setRegistryName("satelliteBuilder");

		AdvancedRocketryBlocks.blockSatelliteControlCenter = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_SAT_CONTROL).setRegistryName("satelliteMonitor");

		AdvancedRocketryBlocks.blockMicrowaveReciever = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_MICROWAVE_RECIEVER).setRegistryName("microwaveReciever");

		AdvancedRocketryBlocks.blockCentrifuge = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CENTRIFUGE).setRegistryName("centrifuge");

		//Arcfurnace
		AdvancedRocketryBlocks.blockArcFurnace = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ARC_FURNACE).setRegistryName("electricArcFurnace");

		AdvancedRocketryBlocks.blockMoonTurf = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.SNOW).hardnessAndResistance(0.5f)).setRegistryName("turf");
		AdvancedRocketryBlocks.blockMoonTurfDark = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.BROWN).hardnessAndResistance(0.5f)).setRegistryName("turfDark");
		AdvancedRocketryBlocks.blockHotTurf = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.NETHERRACK).hardnessAndResistance(0.5f)).setRegistryName("hotDryturf");

		AdvancedRocketryBlocks.blockFluidLoader = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_FLUID_LOADER).setRegistryName("floader");
		AdvancedRocketryBlocks.blockFluidUnloader = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_FLUID_UNLOADER).setRegistryName("funloader");
		AdvancedRocketryBlocks.blockRocketLoader = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_ROCKET_LOADER).setRegistryName("rloader");
		AdvancedRocketryBlocks.blockRocketUnloader = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_ROCKET_UNLOADER).setRegistryName("runloader");

		AdvancedRocketryBlocks.blockAlienWood = registerLog(MaterialColor.BLUE, MaterialColor.LIGHT_BLUE).setRegistryName("alien_log");
		AdvancedRocketryBlocks.blockAlienLeaves = registerLeaves().setRegistryName("alien_leaves");
		AdvancedRocketryBlocks.blockAlienSapling = new SaplingBlock(new AlienTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT)).setRegistryName("alien_sapling");
		AdvancedRocketryBlocks.blockAlienPlanks = new Block(AbstractBlock.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD)).setRegistryName("alien_planks");

		AdvancedRocketryBlocks.blockLightSource = new BlockLightSource(AbstractBlock.Properties.create(Material.ROCK).func_235838_a_((p_235470_0_) -> {
			return 14;
		}));
		AdvancedRocketryBlocks.blockBlastBrick = new BlockMultiBlockComponentVisible(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3, 15)).setRegistryName("blastBrick");
		AdvancedRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible(AbstractBlock.Properties.create(Material.ROCK)).setRegistryName("qcrucible");
		//AdvancedRocketryBlocks.blockAstroBed = new BlockAstroBed(DyeColor.WHITE, ).setHardness(0.2F).setRegistryName("astroBed");

		AdvancedRocketryBlocks.blockPrecisionAssembler = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_PREC_ASS).setRegistryName("precisionAssemblingMachine");
		AdvancedRocketryBlocks.blockCuttingMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CUTTING_MACHINE).setRegistryName("cuttingMachine");
		AdvancedRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CRYSTALLIZER).setRegistryName("Crystallizer");
		AdvancedRocketryBlocks.blockWarpCore = new BlockWarpCore(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_WARP_CORE).setRegistryName("warpCore");
		AdvancedRocketryBlocks.blockChemicalReactor = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CHEMICAL_REACTOR).setRegistryName("chemreactor");
		AdvancedRocketryBlocks.blockLathe = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_LATHE).setRegistryName("lathe");
		AdvancedRocketryBlocks.blockRollingMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ROLLING).setRegistryName("rollingMachine");
		AdvancedRocketryBlocks.blockElectrolyser = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ELECTROLYSER).setRegistryName("electrolyser");
		AdvancedRocketryBlocks.blockAtmosphereTerraformer = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_TERRAFORMER).setRegistryName("atmosphereTerraformer");
		AdvancedRocketryBlocks.blockPlanetAnalyser = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ASTROBODY_DATA).setRegistryName("planetanalyser");
		AdvancedRocketryBlocks.blockObservatory = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_OBSERVATORY).setRegistryName("observatory");
		AdvancedRocketryBlocks.blockBlackHoleGenerator = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_BLACK_HOLE_GENERATOR).setRegistryName("blackholegenerator");
		AdvancedRocketryBlocks.blockPump = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_PUMP).setRegistryName("pump");

		AdvancedRocketryBlocks.blockGuidanceComputer = new BlockTile(machineLineProperties,GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_GUIDANCE_COMPUTER).setRegistryName("guidanceComputer");
		AdvancedRocketryBlocks.blockPlanetSelector = new BlockTile(machineLineProperties,GuiHandler.guiId.MODULARFULLSCREEN.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_PLANET_SELECTOR).setRegistryName("planetSelector");
		AdvancedRocketryBlocks.blockPlanetHoloSelector = new BlockHalfTile(machineLineProperties,GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_HOLOGRAM).setRegistryName("planetHoloSelector");
		AdvancedRocketryBlocks.blockBiomeScanner = new BlockMultiblockMachine(machineLineProperties,GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_BIOME_SCANNER).setRegistryName("biomeScanner");
		AdvancedRocketryBlocks.blockDrill = new BlockMiningDrill(machineLineProperties).setRegistryName("drill");
		AdvancedRocketryBlocks.blockSuitWorkStation = new BlockSuitWorkstation(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_WORK_STATION).setRegistryName("suitWorkStation");
		AdvancedRocketryBlocks.blockRailgun = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_RAILGUN).setRegistryName("railgun");
		AdvancedRocketryBlocks.blockSpaceElevatorController = new BlockMultiblockMachine(machineLineProperties,  GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_SPACE_ELEVATOR).setRegistryName("spaceElevatorController");
		AdvancedRocketryBlocks.blockBeacon = new BlockBeacon(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_BEACON).setRegistryName("beacon");
		AdvancedRocketryBlocks.blockIntake = new BlockIntake(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f)).setRegistryName("gasIntake");
		AdvancedRocketryBlocks.blockPressureTank = new BlockPressurizedFluidTank(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f)).setRegistryName("pressurizedTank");
		AdvancedRocketryBlocks.blockSolarPanel = new Block(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f)).setRegistryName("solarPanel");
		AdvancedRocketryBlocks.blockSolarGenerator = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_SOLAR_PANEL).setRegistryName("solarGenerator");
		AdvancedRocketryBlocks.blockDockingPort = new BlockStationModuleDockingPort(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f)).setRegistryName("stationMarker");
		AdvancedRocketryBlocks.blockPipeSealer = new BlockSeal(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(0.5f)).setRegistryName("pipeSeal");
		AdvancedRocketryBlocks.blockThermiteTorch = new TorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().func_235838_a_((p_235470_0_) -> {
			return 14;
		}).sound(SoundType.WOOD), ParticleTypes.FLAME).setRegistryName("thermiteTorch");
		AdvancedRocketryBlocks.blockBasalt = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(5, 15)).setRegistryName("basalt");
		AdvancedRocketryBlocks.blockLandingFloat = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(1,1)).setRegistryName("landingfloat");
		AdvancedRocketryBlocks.blockTransciever = new BlockTransciever(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_WIRELESS_TRANSCIEVER).setRegistryName("wirelessTransciever");

		//Configurable stuff
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController.get())
			AdvancedRocketryBlocks.blockGravityMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_GRAVITY_CONTROLLER).setRegistryName("gravityMachine");


		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableLaserDrill.get()) {
			AdvancedRocketryBlocks.blockSpaceLaser = new BlockLaser(machineLineProperties).setRegistryName("laser_drill");
		}

		AdvancedRocketryBlocks.blockOxygenFluid = (FlowingFluidBlock)new FlowingFluidBlock(AdvancedRocketryFluids.oxygenStill.get(), AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()).setRegistryName("oxygenFluidBlock");
		AdvancedRocketryBlocks.blockHydrogenFluid = (FlowingFluidBlock)new FlowingFluidBlock(AdvancedRocketryFluids.hydrogenStill.get(), AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()).setRegistryName("hydrogenFluidBlock");
		AdvancedRocketryBlocks.blockFuelFluid = (FlowingFluidBlock)new FlowingFluidBlock(AdvancedRocketryFluids.rocketFuelStill.get(), AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()).setRegistryName("rocketFuelBlock");
		AdvancedRocketryBlocks.blockNitrogenFluid = (FlowingFluidBlock)new FlowingFluidBlock(AdvancedRocketryFluids.nitrogenStill.get(), AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()).setRegistryName("nitrogenFluidBlock");
		AdvancedRocketryBlocks.blockEnrichedLavaFluid = (FlowingFluidBlock)new BlockEnrichedLava(AdvancedRocketryFluids.enrichedLavaStill.get(), AbstractBlock.Properties.create(Material.LAVA).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops().func_235838_a_((p_235470_0_) -> {
			return 14;
		})).setRegistryName("enrichedLavaBlock");

		//Cables
		//TODO: add back after fixing the cable network
		//AdvancedRocketryBlocks.blockFluidPipe = new BlockLiquidPipe(Material.IRON).setRegistryName("liquidPipe").setHardness(1f);
		//AdvancedRocketryBlocks.blockDataPipe = new BlockDataCable(Material.IRON).setRegistryName("dataPipe").setHardness(1f);
		//AdvancedRocketryBlocks.blockEnergyPipe = new BlockEnergyCable(Material.IRON).setRegistryName("energyPipe").setHardness(1f);

		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDataPipe.setRegistryName("dataPipe"));
		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEnergyPipe.setRegistryName("energyPipe"));
		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFluidPipe.setRegistryName("liquidPipe"));
		evt.getRegistry().registerAll(AdvancedRocketryBlocks.blockLaunchpad.setRegistryName("launchpad"),
				AdvancedRocketryBlocks.blockRocketBuilder.setRegistryName("rocketBuilder"),
				AdvancedRocketryBlocks.blockStructureTower.setRegistryName("structureTower"),
				AdvancedRocketryBlocks.blockGenericSeat.setRegistryName("seat"),
				AdvancedRocketryBlocks.blockEngine.setRegistryName("rocketmotor"),
				AdvancedRocketryBlocks.blockAdvEngine.setRegistryName("advRocketmotor"),
				AdvancedRocketryBlocks.blockFuelTank.setRegistryName("fuelTank"),
				AdvancedRocketryBlocks.blockFuelingStation.setRegistryName("fuelingStation"),
				AdvancedRocketryBlocks.blockMonitoringStation.setRegistryName("monitoringStation"),
				AdvancedRocketryBlocks.blockSatelliteBuilder.setRegistryName("satelliteBuilder"),
				AdvancedRocketryBlocks.blockMoonTurf.setRegistryName("moonTurf"),
				AdvancedRocketryBlocks.blockMoonTurfDark.setRegistryName("moonTurf_dark"),
				AdvancedRocketryBlocks.blockHotTurf.setRegistryName("hotTurf"),
				AdvancedRocketryBlocks.blockLoader.setRegistryName("loader"),
				AdvancedRocketryBlocks.blockPrecisionAssembler.setRegistryName("precisionassemblingmachine"),
				AdvancedRocketryBlocks.blockBlastBrick.setRegistryName("blastbrick"),
				AdvancedRocketryBlocks.blockQuartzCrucible.setRegistryName("quartzcrucible"),
				AdvancedRocketryBlocks.blockCrystallizer.setRegistryName("crystallizer"),
				AdvancedRocketryBlocks.blockCuttingMachine.setRegistryName("cuttingMachine"),
				AdvancedRocketryBlocks.blockAlienWood.setRegistryName("alienWood"),
				AdvancedRocketryBlocks.blockAlienLeaves.setRegistryName("alienLeaves"),
				AdvancedRocketryBlocks.blockAlienSapling.setRegistryName("alienSapling"),
				AdvancedRocketryBlocks.blockObservatory.setRegistryName("observatory"),
				AdvancedRocketryBlocks.blockBlackHoleGenerator.setRegistryName("blackholegenerator"),
				AdvancedRocketryBlocks.blockConcrete.setRegistryName("concrete"),
				AdvancedRocketryBlocks.blockPlanetSelector.setRegistryName("planetSelector"),
				AdvancedRocketryBlocks.blockSatelliteControlCenter.setRegistryName("satelliteControlCenter"),
				AdvancedRocketryBlocks.blockPlanetAnalyser.setRegistryName("planetAnalyser"),
				AdvancedRocketryBlocks.blockGuidanceComputer.setRegistryName("guidanceComputer"),
				AdvancedRocketryBlocks.blockArcFurnace.setRegistryName("arcfurnace"),
				AdvancedRocketryBlocks.blockSawBlade.setRegistryName("sawBlade"),
				AdvancedRocketryBlocks.blockLathe.setRegistryName("lathe"),
				AdvancedRocketryBlocks.blockRollingMachine.setRegistryName("rollingMachine"),
				AdvancedRocketryBlocks.blockPlatePress.setRegistryName("platepress"),
				AdvancedRocketryBlocks.blockStationBuilder.setRegistryName("stationBuilder"),
				AdvancedRocketryBlocks.blockElectrolyser.setRegistryName("electrolyser"),
				AdvancedRocketryBlocks.blockChemicalReactor.setRegistryName("chemicalReactor"),
				AdvancedRocketryBlocks.blockOxygenScrubber.setRegistryName("oxygenScrubber"),
				AdvancedRocketryBlocks.blockOxygenVent.setRegistryName("oxygenVent"),
				AdvancedRocketryBlocks.blockOxygenCharger.setRegistryName("oxygenCharger"),
				AdvancedRocketryBlocks.blockAirLock.setRegistryName("airlock_door"),
				AdvancedRocketryBlocks.blockLandingPad.setRegistryName("landingPad"),
				AdvancedRocketryBlocks.blockWarpCore.setRegistryName("warpCore"),
				AdvancedRocketryBlocks.blockWarpShipMonitor.setRegistryName("warpMonitor"),
				AdvancedRocketryBlocks.blockOxygenDetection.setRegistryName("oxygenDetection"),
				AdvancedRocketryBlocks.blockUnlitTorch.setRegistryName("unlitTorch"),
				AdvancedRocketryBlocks.blocksGeode.setRegistryName("geode"),
				AdvancedRocketryBlocks.blockOxygenFluid.setRegistryName("oxygenFluid"),
				AdvancedRocketryBlocks.blockHydrogenFluid.setRegistryName("hydrogenFluid"),
				AdvancedRocketryBlocks.blockFuelFluid.setRegistryName("rocketFuel"),
				AdvancedRocketryBlocks.blockNitrogenFluid.setRegistryName("nitrogenFluid"),
				AdvancedRocketryBlocks.blockEnrichedLavaFluid.setRegistryName("enrichedLavaFluid"),
				AdvancedRocketryBlocks.blockVitrifiedSand.setRegistryName("vitrifiedSand"),
				AdvancedRocketryBlocks.blockCharcoalLog.setRegistryName("charcoalLog"),
				AdvancedRocketryBlocks.blockElectricMushroom.setRegistryName("electricMushroom"),
				AdvancedRocketryBlocks.blockCrystal.setRegistryName("crystal"),
				AdvancedRocketryBlocks.blockOrientationController.setRegistryName("orientationController"),
				AdvancedRocketryBlocks.blockGravityController.setRegistryName("gravityController"),
				AdvancedRocketryBlocks.blockDrill.setRegistryName("drill"),
				AdvancedRocketryBlocks.blockMicrowaveReciever.setRegistryName("microwaveReciever"),
				AdvancedRocketryBlocks.blockLightSource.setRegistryName("lightSource"),
				AdvancedRocketryBlocks.blockSolarPanel.setRegistryName("solarPanel"),
				AdvancedRocketryBlocks.blockSuitWorkStation.setRegistryName("suitWorkStation"),
				AdvancedRocketryBlocks.blockBiomeScanner.setRegistryName("biomeScanner"),
				AdvancedRocketryBlocks.blockAtmosphereTerraformer.setRegistryName("terraformer"),
				AdvancedRocketryBlocks.blockDeployableRocketBuilder.setRegistryName("deployableRocketBuilder"),
				AdvancedRocketryBlocks.blockPressureTank.setRegistryName("liquidTank"), 
				AdvancedRocketryBlocks.blockIntake.setRegistryName("intake"),
				AdvancedRocketryBlocks.blockCircleLight.setRegistryName("circleLight"),
				AdvancedRocketryBlocks.blockSolarGenerator.setRegistryName("solarGenerator"),
				AdvancedRocketryBlocks.blockDockingPort.setRegistryName("stationMarker"),
				AdvancedRocketryBlocks.blockAltitudeController.setRegistryName("altitudeController"),
				AdvancedRocketryBlocks.blockRailgun .setRegistryName("railgun"),
				AdvancedRocketryBlocks.blockAstroBed .setRegistryName("astroBed"),
				AdvancedRocketryBlocks.blockPlanetHoloSelector.setRegistryName("planetHoloSelector"),
				AdvancedRocketryBlocks.blockLens.setRegistryName("blockLens"),
				AdvancedRocketryBlocks.blockForceField.setRegistryName("forceField"),
				AdvancedRocketryBlocks.blockForceFieldProjector.setRegistryName("forceFieldProjector"),
				AdvancedRocketryBlocks.blockPipeSealer.setRegistryName("pipeSealer"),
				AdvancedRocketryBlocks.blockSpaceElevatorController.setRegistryName("spaceElevatorController"),
				AdvancedRocketryBlocks.blockBeacon.setRegistryName("beacon"),
				AdvancedRocketryBlocks.blockAlienPlanks.setRegistryName("planks"),
				AdvancedRocketryBlocks.blockThermiteTorch.setRegistryName("thermiteTorch"),
				AdvancedRocketryBlocks.blockTransciever.setRegistryName("wirelessTransciever"),
				AdvancedRocketryBlocks.blockPump.setRegistryName("blockPump"),
				AdvancedRocketryBlocks.blockCentrifuge.setRegistryName("centrifuge"),
				AdvancedRocketryBlocks.blockBasalt.setRegistryName("basalt"),
				AdvancedRocketryBlocks.blockLandingFloat.setRegistryName("landingfloat"));

		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController.get())
			evt.getRegistry().register(AdvancedRocketryBlocks.blockGravityMachine.setRegistryName("gravityMachine"));

		//TODO, use different mechanism to enable/disable drill
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableLaserDrill.get())
			evt.getRegistry().register(AdvancedRocketryBlocks.blockSpaceLaser.setRegistryName("spaceLaser"));
	}

	private static RotatedPillarBlock registerLog(MaterialColor p_235430_0_, MaterialColor p_235430_1_) {
		return new RotatedPillarBlock(AbstractBlock.Properties.func_235836_a_(Material.WOOD, (p_235431_2_) -> {
			return p_235431_2_.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? p_235430_0_ : p_235430_1_;
		}).hardnessAndResistance(2.0F).sound(SoundType.WOOD));
	}
	private static LeavesBlock registerLeaves() {
		return new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid());
	}
}
