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
import net.minecraftforge.fml.RegistryObject;
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
	public static RegistryObject<FlowingFluidBlock> blockOxygenFluid;
	public static RegistryObject<FlowingFluidBlock> blockHydrogenFluid;
	public static Block blockChemicalReactor;
	public static RegistryObject<FlowingFluidBlock> blockFuelFluid;
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
	public static Block blockDataBus;
	public static Block blockguidanceHatch;
	public static Block blockSatelliteHatch;
	public static Block blockFluidLoader;
	public static Block blockFluidUnloader;
	public static Block blockRocketLoader;
	public static Block blockRocketUnloader;
	public static Block blockBiomeScanner;
	public static Block blockAtmosphereTerraformer;
	public static Block blockDeployableRocketBuilder;
	public static Block blockPressureTank;
	public static Block blockIntake;
	public static RegistryObject<FlowingFluidBlock> blockNitrogenFluid;
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
	public static RegistryObject<FlowingFluidBlock> blockEnrichedLavaFluid;
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

		AdvancedRocketryBlocks.blocksGeode = new Block(AbstractBlock.Properties.create(MaterialGeode.geode).hardnessAndResistance(6f, 2000F).harvestTool(ToolType.get("jackhammer")));
		AdvancedRocketryBlocks.blockLaunchpad = new BlockLinkedHorizontalTexture(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f, 10f));
		AdvancedRocketryBlocks.blockStructureTower = new BlockAlphaTexture(machineLineProperties);
		AdvancedRocketryBlocks.blockGenericSeat = new BlockSeat(AbstractBlock.Properties.create(Material.WOOL).hardnessAndResistance(0.5f));
		AdvancedRocketryBlocks.blockEngine = new BlockRocketMotor(machineLineProperties);
		AdvancedRocketryBlocks.blockAdvEngine = new BlockAdvancedRocketMotor(machineLineProperties);
		AdvancedRocketryBlocks.blockFuelTank = new BlockFuelTank(machineLineProperties);
		AdvancedRocketryBlocks.blockSawBlade = new BlockMotor(machineLineProperties,1f);

		AdvancedRocketryBlocks.blockConcrete = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f, 16f));
		AdvancedRocketryBlocks.blockPlatePress = new BlockPress(machineLineProperties);
		AdvancedRocketryBlocks.blockAirLock = new DoorBlock(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f, 8f));
		AdvancedRocketryBlocks.blockLandingPad = new BlockLandingPad(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f,3f));
		AdvancedRocketryBlocks.blockOxygenDetection = new BlockRedstoneEmitter(machineLineProperties,"advancedrocketry:atmosphereDetector_active");
		AdvancedRocketryBlocks.blockOxygenScrubber = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CO2_SCRUBBER);
		AdvancedRocketryBlocks.blockUnlitTorch = new BlockTorchUnlit(AbstractBlock.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(0));
		AdvancedRocketryBlocks.blockVitrifiedSand = new Block(AbstractBlock.Properties.create(Material.SAND).hardnessAndResistance(0.5f));
		AdvancedRocketryBlocks.blockCharcoalLog = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD));
		AdvancedRocketryBlocks.blockElectricMushroom = new BlockElectricMushroom(AbstractBlock.Properties.create(Material.PLANTS).hardnessAndResistance(0.0f));
		AdvancedRocketryBlocks.blockCrystal = new Block(crystalProperties);
		crystalBlocks = new Block[] {AdvancedRocketryBlocks.blockCrystal};

		AdvancedRocketryBlocks.blockOrientationController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ORIENTATION_CONTROLLER);
		AdvancedRocketryBlocks.blockGravityController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_GRAVITY_CONTROLLER);
		AdvancedRocketryBlocks.blockAltitudeController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ALT_CONTROLLER);
		AdvancedRocketryBlocks.blockOxygenCharger = new BlockHalfTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_OXYGEN_CHARGER);
		AdvancedRocketryBlocks.blockOxygenVent = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_OXYGEN_VENT);
		AdvancedRocketryBlocks.blockCircleLight = new Block(AbstractBlock.Properties.create(Material.ROCK).func_235838_a_((p_235470_0_) -> {
			return 14;
		}));
		AdvancedRocketryBlocks.blockLens = new Block(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(0.3f));
		AdvancedRocketryBlocks.blockRocketBuilder = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ROCKET_BUILDER);
		AdvancedRocketryBlocks.blockForceField = new BlockForceField(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 3600000.0F).noDrops());
		AdvancedRocketryBlocks.blockForceFieldProjector = new BlockForceFieldProjector(machineLineProperties);
		AdvancedRocketryBlocks.blockDeployableRocketBuilder = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_STATION_DEPLOYED_ASSEMBLER);
		AdvancedRocketryBlocks.blockStationBuilder = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_STATION_BUILDER);
		AdvancedRocketryBlocks.blockFuelingStation = new BlockTileRedstoneEmitter(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_FUELING_STATION);

		AdvancedRocketryBlocks.blockMonitoringStation = new BlockTileNeighborUpdate(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_MONITORIING_STATION);

		AdvancedRocketryBlocks.blockWarpShipMonitor = new BlockWarpShipMonitor(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_WARP_SHIP_CONTROLLER);

		AdvancedRocketryBlocks.blockSatelliteBuilder = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_SAT_BUILDER);

		AdvancedRocketryBlocks.blockSatelliteControlCenter = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_SAT_CONTROL);

		AdvancedRocketryBlocks.blockMicrowaveReciever = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_MICROWAVE_RECIEVER);

		AdvancedRocketryBlocks.blockCentrifuge = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CENTRIFUGE);

		//Arcfurnace
		AdvancedRocketryBlocks.blockArcFurnace = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ARC_FURNACE);

		AdvancedRocketryBlocks.blockMoonTurf = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.SNOW).hardnessAndResistance(0.5f));
		AdvancedRocketryBlocks.blockMoonTurfDark = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.BROWN).hardnessAndResistance(0.5f));
		AdvancedRocketryBlocks.blockHotTurf = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.NETHERRACK).hardnessAndResistance(0.5f));

		AdvancedRocketryBlocks.blockDataBus = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_DATA_BUS);
		AdvancedRocketryBlocks.blockSatelliteHatch = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_SATELLITE_HATCH);
		AdvancedRocketryBlocks.blockFluidLoader = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_FLUID_LOADER);
		AdvancedRocketryBlocks.blockFluidUnloader = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_FLUID_UNLOADER);
		AdvancedRocketryBlocks.blockRocketLoader = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_ROCKET_LOADER);
		AdvancedRocketryBlocks.blockRocketUnloader = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_ROCKET_UNLOADER);
		AdvancedRocketryBlocks.blockguidanceHatch = new BlockARHatch(machineLineProperties)._setTile(AdvancedRocketryTileEntityType.TILE_GUIDANCE_COMPUTER_HATCH);

		AdvancedRocketryBlocks.blockAlienWood = registerLog(MaterialColor.BLUE, MaterialColor.LIGHT_BLUE);
		AdvancedRocketryBlocks.blockAlienLeaves = registerLeaves();
		AdvancedRocketryBlocks.blockAlienSapling = new SaplingBlock(new AlienTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT));
		AdvancedRocketryBlocks.blockAlienPlanks = new Block(AbstractBlock.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD));

		AdvancedRocketryBlocks.blockLightSource = new BlockLightSource(AbstractBlock.Properties.create(Material.ROCK).func_235838_a_((p_235470_0_) -> {
			return 14;
		}));
		AdvancedRocketryBlocks.blockBlastBrick = new BlockMultiBlockComponentVisible(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3, 15));
		AdvancedRocketryBlocks.blockQuartzCrucible = new BlockQuartzCrucible(AbstractBlock.Properties.create(Material.ROCK));
		//AdvancedRocketryBlocks.blockAstroBed = new BlockAstroBed(DyeColor.WHITE, ).setHardness(0.2F);

		AdvancedRocketryBlocks.blockPrecisionAssembler = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_PREC_ASS);
		AdvancedRocketryBlocks.blockCuttingMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CUTTING_MACHINE);
		AdvancedRocketryBlocks.blockCrystallizer = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CRYSTALLIZER);
		AdvancedRocketryBlocks.blockWarpCore = new BlockWarpCore(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_WARP_CORE);
		AdvancedRocketryBlocks.blockChemicalReactor = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_CHEMICAL_REACTOR);
		AdvancedRocketryBlocks.blockLathe = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_LATHE);
		AdvancedRocketryBlocks.blockRollingMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ROLLING);
		AdvancedRocketryBlocks.blockElectrolyser = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ELECTROLYSER);
		AdvancedRocketryBlocks.blockAtmosphereTerraformer = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_TERRAFORMER);
		AdvancedRocketryBlocks.blockPlanetAnalyser = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_ASTROBODY_DATA);
		AdvancedRocketryBlocks.blockObservatory = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_OBSERVATORY);
		AdvancedRocketryBlocks.blockBlackHoleGenerator = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_BLACK_HOLE_GENERATOR);
		AdvancedRocketryBlocks.blockPump = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_PUMP);

		AdvancedRocketryBlocks.blockGuidanceComputer = new BlockTile(machineLineProperties,GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_GUIDANCE_COMPUTER);
		AdvancedRocketryBlocks.blockPlanetSelector = new BlockTile(machineLineProperties,GuiHandler.guiId.MODULARFULLSCREEN.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_PLANET_SELECTOR);
		AdvancedRocketryBlocks.blockPlanetHoloSelector = new BlockHalfTile(machineLineProperties,GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_HOLOGRAM);
		AdvancedRocketryBlocks.blockBiomeScanner = new BlockMultiblockMachine(machineLineProperties,GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_BIOME_SCANNER);
		AdvancedRocketryBlocks.blockDrill = new BlockMiningDrill(machineLineProperties);
		AdvancedRocketryBlocks.blockSuitWorkStation = new BlockSuitWorkstation(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_WORK_STATION);
		AdvancedRocketryBlocks.blockRailgun = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_RAILGUN);
		AdvancedRocketryBlocks.blockSpaceElevatorController = new BlockMultiblockMachine(machineLineProperties,  GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_SPACE_ELEVATOR);
		AdvancedRocketryBlocks.blockBeacon = new BlockBeacon(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_BEACON);
		AdvancedRocketryBlocks.blockIntake = new BlockIntake(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
		AdvancedRocketryBlocks.blockPressureTank = new BlockPressurizedFluidTank(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
		AdvancedRocketryBlocks.blockSolarPanel = new Block(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
		AdvancedRocketryBlocks.blockSolarGenerator = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_SOLAR_PANEL);
		AdvancedRocketryBlocks.blockDockingPort = new BlockStationModuleDockingPort(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
		AdvancedRocketryBlocks.blockPipeSealer = new BlockSeal(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(0.5f));
		AdvancedRocketryBlocks.blockThermiteTorch = new TorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().func_235838_a_((p_235470_0_) -> {
			return 14;
		}).sound(SoundType.WOOD), ParticleTypes.FLAME);
		AdvancedRocketryBlocks.blockBasalt = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(5, 15));
		AdvancedRocketryBlocks.blockLandingFloat = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(1,1));
		AdvancedRocketryBlocks.blockTransciever = new BlockTransciever(machineLineProperties, GuiHandler.guiId.MODULAR.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_WIRELESS_TRANSCIEVER);

		//Configurable stuff
		AdvancedRocketryBlocks.blockGravityMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV.ordinal())._setTile(AdvancedRocketryTileEntityType.TILE_GRAVITY_CONTROLLER);


		AdvancedRocketryBlocks.blockSpaceLaser = new BlockLaser(machineLineProperties);


		//Cables
		//TODO: add back after fixing the cable network
		//AdvancedRocketryBlocks.blockFluidPipe = new BlockLiquidPipe(Material.IRON).setRegistryName("liquidPipe").setHardness(1f);
		//AdvancedRocketryBlocks.blockDataPipe = new BlockDataCable(Material.IRON).setRegistryName("dataPipe").setHardness(1f);
		//AdvancedRocketryBlocks.blockEnergyPipe = new BlockEnergyCable(Material.IRON).setRegistryName("energyPipe").setHardness(1f);

		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockDataPipe.setRegistryName("dataPipe"));
		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockEnergyPipe.setRegistryName("energyPipe"));
		//LibVulpesBlocks.registerBlock(AdvancedRocketryBlocks.blockFluidPipe.setRegistryName("liquidPipe"));
		evt.getRegistry().registerAll(AdvancedRocketryBlocks.blockLaunchpad.setRegistryName("launchpad"),
				AdvancedRocketryBlocks.blockRocketBuilder.setRegistryName("rocketassembler"),
				AdvancedRocketryBlocks.blockStructureTower.setRegistryName("structuretower"),
				AdvancedRocketryBlocks.blockGenericSeat.setRegistryName("seat"),
				AdvancedRocketryBlocks.blockEngine.setRegistryName("rocketmotor"),
				AdvancedRocketryBlocks.blockAdvEngine.setRegistryName("advrocketmotor"),
				AdvancedRocketryBlocks.blockFuelTank.setRegistryName("fueltank"),
				AdvancedRocketryBlocks.blockFuelingStation.setRegistryName("fuelingstation"),
				AdvancedRocketryBlocks.blockMonitoringStation.setRegistryName("monitoringstation"),
				AdvancedRocketryBlocks.blockSatelliteBuilder.setRegistryName("satellitebuilder"),
				AdvancedRocketryBlocks.blockMoonTurf.setRegistryName("moonturf"),
				AdvancedRocketryBlocks.blockMoonTurfDark.setRegistryName("moonturf_dark"),
				AdvancedRocketryBlocks.blockHotTurf.setRegistryName("hotturf"),
				AdvancedRocketryBlocks.blockPrecisionAssembler.setRegistryName("precisionassemblingmachine"),
				AdvancedRocketryBlocks.blockBlastBrick.setRegistryName("blastbrick"),
				AdvancedRocketryBlocks.blockDataBus.setRegistryName("databus"),
				AdvancedRocketryBlocks.blockSatelliteHatch.setRegistryName("satbay"),
				AdvancedRocketryBlocks.blockFluidLoader.setRegistryName("floader"),
				AdvancedRocketryBlocks.blockFluidUnloader.setRegistryName("funloader"),
				AdvancedRocketryBlocks.blockRocketLoader.setRegistryName("rloader"),
				AdvancedRocketryBlocks.blockRocketUnloader.setRegistryName("runloader"),
				AdvancedRocketryBlocks.blockguidanceHatch.setRegistryName("compaccesshatch"),
				AdvancedRocketryBlocks.blockQuartzCrucible.setRegistryName("qcrucible"),
				AdvancedRocketryBlocks.blockCrystallizer.setRegistryName("crystallizer"),
				AdvancedRocketryBlocks.blockCuttingMachine.setRegistryName("cuttingmachine"),
				AdvancedRocketryBlocks.blockAlienWood.setRegistryName("alienwood"),
				AdvancedRocketryBlocks.blockAlienLeaves.setRegistryName("alienleaves"),
				AdvancedRocketryBlocks.blockAlienSapling.setRegistryName("aliensapling"),
				AdvancedRocketryBlocks.blockObservatory.setRegistryName("observatory"),
				AdvancedRocketryBlocks.blockBlackHoleGenerator.setRegistryName("blackholegenerator"),
				AdvancedRocketryBlocks.blockConcrete.setRegistryName("concrete"),
				AdvancedRocketryBlocks.blockPlanetSelector.setRegistryName("planetselector"),
				AdvancedRocketryBlocks.blockSatelliteControlCenter.setRegistryName("satellitecontrolcenter"),
				AdvancedRocketryBlocks.blockPlanetAnalyser.setRegistryName("planetanalyser"),
				AdvancedRocketryBlocks.blockGuidanceComputer.setRegistryName("guidancecomputer"),
				AdvancedRocketryBlocks.blockArcFurnace.setRegistryName("arcfurnace"),
				AdvancedRocketryBlocks.blockSawBlade.setRegistryName("sawbladeassbly"),
				AdvancedRocketryBlocks.blockLathe.setRegistryName("lathe"),
				AdvancedRocketryBlocks.blockRollingMachine.setRegistryName("rollingmachine"),
				AdvancedRocketryBlocks.blockPlatePress.setRegistryName("platepress"),
				AdvancedRocketryBlocks.blockStationBuilder.setRegistryName("stationbuilder"),
				AdvancedRocketryBlocks.blockElectrolyser.setRegistryName("electrolyser"),
				AdvancedRocketryBlocks.blockChemicalReactor.setRegistryName("chemicalreactor"),
				AdvancedRocketryBlocks.blockOxygenScrubber.setRegistryName("oxygenscrubber"),
				AdvancedRocketryBlocks.blockOxygenVent.setRegistryName("oxygenvent"),
				AdvancedRocketryBlocks.blockOxygenCharger.setRegistryName("oxygencharger"),
				AdvancedRocketryBlocks.blockAirLock.setRegistryName("airlock_door"),
				AdvancedRocketryBlocks.blockLandingPad.setRegistryName("landingpad"),
				AdvancedRocketryBlocks.blockWarpCore.setRegistryName("warpcore"),
				AdvancedRocketryBlocks.blockWarpShipMonitor.setRegistryName("stationmonitor"),
				AdvancedRocketryBlocks.blockOxygenDetection.setRegistryName("atmospheredetector"),
				AdvancedRocketryBlocks.blockUnlitTorch.setRegistryName("unlittorch"),
				AdvancedRocketryBlocks.blocksGeode.setRegistryName("geode"),
				//AdvancedRocketryBlocks.blockOxygenFluid.setRegistryName("oxygenfluid"),
				//AdvancedRocketryBlocks.blockHydrogenFluid.setRegistryName("hydrogenfluid"),
				//AdvancedRocketryBlocks.blockFuelFluid.setRegistryName("rocketfuel"),
				//AdvancedRocketryBlocks.blockNitrogenFluid.setRegistryName("nitrogenfluid"),
				//AdvancedRocketryBlocks.blockEnrichedLavaFluid.setRegistryName("enrichedlavafluid"),
				AdvancedRocketryBlocks.blockVitrifiedSand.setRegistryName("vitrifiedsand"),
				AdvancedRocketryBlocks.blockCharcoalLog.setRegistryName("charcoallog"),
				AdvancedRocketryBlocks.blockElectricMushroom.setRegistryName("electricmushroom"),
				AdvancedRocketryBlocks.blockCrystal.setRegistryName("crystal"),
				AdvancedRocketryBlocks.blockOrientationController.setRegistryName("orientationcontroller"),
				AdvancedRocketryBlocks.blockGravityController.setRegistryName("gravitycontroller"),
				AdvancedRocketryBlocks.blockDrill.setRegistryName("drill"),
				AdvancedRocketryBlocks.blockMicrowaveReciever.setRegistryName("microwavereciever"),
				AdvancedRocketryBlocks.blockLightSource.setRegistryName("lightsource"),
				AdvancedRocketryBlocks.blockSolarPanel.setRegistryName("solarpanel"),
				AdvancedRocketryBlocks.blockSuitWorkStation.setRegistryName("suitworkstation"),
				AdvancedRocketryBlocks.blockBiomeScanner.setRegistryName("biomescanner"),
				AdvancedRocketryBlocks.blockAtmosphereTerraformer.setRegistryName("terraformer"),
				AdvancedRocketryBlocks.blockDeployableRocketBuilder.setRegistryName("deployablerocketbuilder"),
				AdvancedRocketryBlocks.blockPressureTank.setRegistryName("liquidtank"), 
				AdvancedRocketryBlocks.blockIntake.setRegistryName("intake"),
				AdvancedRocketryBlocks.blockCircleLight.setRegistryName("circlelight"),
				AdvancedRocketryBlocks.blockSolarGenerator.setRegistryName("solargenerator"),
				AdvancedRocketryBlocks.blockDockingPort.setRegistryName("stationmarker"),
				AdvancedRocketryBlocks.blockAltitudeController.setRegistryName("altitudecontroller"),
				AdvancedRocketryBlocks.blockRailgun .setRegistryName("railgun"),
				//AdvancedRocketryBlocks.blockAstroBed .setRegistryName("astrobed"),
				AdvancedRocketryBlocks.blockPlanetHoloSelector.setRegistryName("planetholoselector"),
				AdvancedRocketryBlocks.blockLens.setRegistryName("blocklens"),
				AdvancedRocketryBlocks.blockForceField.setRegistryName("forcefield"),
				AdvancedRocketryBlocks.blockForceFieldProjector.setRegistryName("forcefieldprojector"),
				AdvancedRocketryBlocks.blockPipeSealer.setRegistryName("pipesealer"),
				AdvancedRocketryBlocks.blockSpaceElevatorController.setRegistryName("spaceelevatorcontroller"),
				AdvancedRocketryBlocks.blockBeacon.setRegistryName("beacon"),
				AdvancedRocketryBlocks.blockAlienPlanks.setRegistryName("planks"),
				AdvancedRocketryBlocks.blockThermiteTorch.setRegistryName("thermitetorch"),
				AdvancedRocketryBlocks.blockTransciever.setRegistryName("wirelesstransciever"),
				AdvancedRocketryBlocks.blockPump.setRegistryName("blockpump"),
				AdvancedRocketryBlocks.blockCentrifuge.setRegistryName("centrifuge"),
				AdvancedRocketryBlocks.blockBasalt.setRegistryName("basalt"),
				AdvancedRocketryBlocks.blockLandingFloat.setRegistryName("landingfloat"));

		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController.get())
			evt.getRegistry().register(AdvancedRocketryBlocks.blockGravityMachine.setRegistryName("gravitymachine"));

		//TODO, use different mechanism to enable/disable drill
		if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableLaserDrill.get())
			evt.getRegistry().register(AdvancedRocketryBlocks.blockSpaceLaser.setRegistryName("spacelaser"));
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
