package zmaster587.advancedRocketry.api;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.tile.TileDrill;
import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.advancedRocketry.tile.TileForceFieldProjector;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.TileMachineChipInfo;
import zmaster587.advancedRocketry.tile.TilePump;
import zmaster587.advancedRocketry.tile.TileRocketAssemblingMachine;
import zmaster587.advancedRocketry.tile.TileSolarPanel;
import zmaster587.advancedRocketry.tile.TileStationAssembler;
import zmaster587.advancedRocketry.tile.TileSuitWorkStation;
import zmaster587.advancedRocketry.tile.TileUnmannedVehicleAssembler;
import zmaster587.advancedRocketry.tile.atmosphere.TileAtmosphereDetector;
import zmaster587.advancedRocketry.tile.atmosphere.TileCO2Scrubber;
import zmaster587.advancedRocketry.tile.atmosphere.TileGasChargePad;
import zmaster587.advancedRocketry.tile.atmosphere.TileOxygenVent;
import zmaster587.advancedRocketry.tile.atmosphere.TileSeal;
import zmaster587.advancedRocketry.tile.cables.TilePipe;
import zmaster587.advancedRocketry.tile.cables.TileWirelessTransciever;
import zmaster587.advancedRocketry.tile.data.TileDataProgrammer;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.advancedRocketry.tile.infrastructure.TileFuelingStation;
import zmaster587.advancedRocketry.tile.infrastructure.TileGuidanceComputerAccessHatch;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketLoader;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketMonitoringStation;
import zmaster587.advancedRocketry.tile.infrastructure.TileRocketUnloader;
import zmaster587.advancedRocketry.tile.multiblock.TileAreaGravityController;
import zmaster587.advancedRocketry.tile.multiblock.TileAstrobodyDataProcessor;
import zmaster587.advancedRocketry.tile.multiblock.TileAtmosphereTerraformer;
import zmaster587.advancedRocketry.tile.multiblock.TileBeacon;
import zmaster587.advancedRocketry.tile.multiblock.TileBiomeScanner;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.tile.multiblock.TilePlanetSelector;
import zmaster587.advancedRocketry.tile.multiblock.TileRailgun;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceElevator;
import zmaster587.advancedRocketry.tile.multiblock.TileWarpCore;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileBlackHoleGenerator;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileSolarArray;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionLaserEtcher;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill.TileOrbitalLaserDrill;
import zmaster587.advancedRocketry.tile.satellite.TileSatelliteBuilder;
import zmaster587.advancedRocketry.tile.satellite.TileSatelliteTerminal;
import zmaster587.advancedRocketry.tile.station.TileDockingPort;
import zmaster587.advancedRocketry.tile.station.TileHolographicPlanetSelector;
import zmaster587.advancedRocketry.tile.station.TileLandingPad;
import zmaster587.advancedRocketry.tile.station.TileStationAltitudeController;
import zmaster587.advancedRocketry.tile.station.TileStationGravityController;
import zmaster587.advancedRocketry.tile.station.TileStationOrientationController;
import zmaster587.advancedRocketry.tile.station.TileWarpController;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;

public class AdvancedRocketryTileEntityType {
	public static TileEntityType<TilePipe> TILE_POWER_PIPE;
	public static TileEntityType<TilePipe> TILE_FLUID_PIPE;
	public static TileEntityType<TilePipe> TILE_DATA_PIPE;
	public static TileEntityType<TileWirelessTransciever> TILE_WIRELESS_TRANSCIEVER;
	public static TileEntityType<TileDataProgrammer> TILE_DATA_PROGRAMMER;
	public static TileEntityType<TileDataBus> TILE_DATA_BUS;
	public static TileEntityType<TileFuelingStation> TILE_FUELING_STATION;
	public static TileEntityType<TileRocketMonitoringStation> TILE_MONITORIING_STATION;
	public static TileEntityType<TileGuidanceComputerAccessHatch> TILE_GUIDANCE_COMPUTER_HATCH;
	public static TileEntityType<TileFluidHatch> TILE_FLUID_LOADER;
	public static TileEntityType<TileFluidHatch> TILE_FLUID_UNLOADER;
	public static TileEntityType<TileRocketLoader> TILE_ROCKET_LOADER;
	public static TileEntityType<TileRocketUnloader> TILE_ROCKET_UNLOADER;
	public static TileEntityType<TileBlackHoleGenerator> TILE_BLACK_HOLE_GENERATOR;
	public static TileEntityType<TileMicrowaveReciever> TILE_MICROWAVE_RECIEVER;
	public static TileEntityType<TileCentrifuge> TILE_CENTRIFUGE;
	public static TileEntityType<TileChemicalReactor> TILE_CHEMICAL_REACTOR;
	public static TileEntityType<TileCrystallizer> TILE_CRYSTALLIZER;
	public static TileEntityType<TileCuttingMachine> TILE_CUTTING_MACHINE;
	public static TileEntityType<TileElectricArcFurnace> TILE_ARC_FURNACE;
	public static TileEntityType<TilePrecisionLaserEtcher> TILE_PREC_LASER_ETCHER;
	public static TileEntityType<TileSolarArray> TILE_SOLAR_ARRAY;
	public static TileEntityType<TileElectrolyser> TILE_ELECTROLYSER;
	public static TileEntityType<TileLathe> TILE_LATHE;
	public static TileEntityType<TilePrecisionAssembler> TILE_PREC_ASS;
	public static TileEntityType<TileRollingMachine> TILE_ROLLING;
	public static TileEntityType<TileAstrobodyDataProcessor> TILE_ASTROBODY_DATA;
	public static TileEntityType<TileAtmosphereTerraformer> TILE_TERRAFORMER;
	public static TileEntityType<TileBeacon> TILE_BEACON;
	public static TileEntityType<TileBiomeScanner> TILE_BIOME_SCANNER;
	public static TileEntityType<TileAreaGravityController> TILE_GRAVITY_CONTROLLER;
	public static TileEntityType<TileObservatory> TILE_OBSERVATORY;
	public static TileEntityType<TilePlanetSelector> TILE_PLANET_SELECTOR;
	public static TileEntityType<TileRailgun> TILE_RAILGUN;
	public static TileEntityType<TileSpaceElevator> TILE_SPACE_ELEVATOR;
	public static TileEntityType<TileOrbitalLaserDrill> TILE_SPACE_LASER;
	public static TileEntityType<TileWarpCore> TILE_WARP_CORE;
	public static TileEntityType<TileCO2Scrubber> TILE_CO2_SCRUBBER;
	public static TileEntityType<TileGasChargePad> TILE_OXYGEN_CHARGER;
	public static TileEntityType<TileOxygenVent> TILE_OXYGEN_VENT;
	public static TileEntityType<TileSatelliteTerminal> TILE_SAT_CONTROL;
	public static TileEntityType<TileSatelliteBuilder> TILE_SAT_BUILDER;
	public static TileEntityType<TileDockingPort> TILE_DOCKING_PORT;
	public static TileEntityType<TileLandingPad> TILE_LANDING_PAD;
	public static TileEntityType<TileHolographicPlanetSelector> TILE_HOLOGRAM;
	public static TileEntityType<TileStationAltitudeController> TILE_ALT_CONTROLLER;
	public static TileEntityType<TileStationOrientationController> TILE_ORIENTATION_CONTROLLER;
	public static TileEntityType<TileStationGravityController> TILE_STATION_GRAVITY_CONTROLLER;
	public static TileEntityType<TileWarpController> TILE_WARP_SHIP_CONTROLLER;
	public static TileEntityType<TileAtmosphereDetector> TILE_ATM_DETECTOR;
	public static TileEntityType<TileDrill> TILE_DRILL;
	public static TileEntityType<TileFluidTank> TILE_FLUID_TANK;
	public static TileEntityType<TileForceFieldProjector> TILE_FORCE_FIELD_PROJECTOR;
	public static TileEntityType<TileMachineChipInfo> TILE_CHIP_MACHINE;
	public static TileEntityType<TilePump> TILE_PUMP;
	public static TileEntityType<TileRocketAssemblingMachine> TILE_ROCKET_BUILDER;
	public static TileEntityType<TileSolarPanel> TILE_SOLAR_PANEL;
	public static TileEntityType<TileStationAssembler> TILE_STATION_BUILDER;
	public static TileEntityType<TileSuitWorkStation> TILE_WORK_STATION;
	public static TileEntityType<TileSeal> TILE_SEAL;
	public static TileEntityType<TileUnmannedVehicleAssembler> TILE_STATION_DEPLOYED_ASSEMBLER;
	public static TileEntityType<TileGuidanceComputer> TILE_GUIDANCE_COMPUTER;
	public static TileEntityType<TileSatelliteHatch> TILE_SATELLITE_HATCH;
	
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> evt)
	{
		TILE_WIRELESS_TRANSCIEVER = TileEntityType.Builder.create(TileWirelessTransciever::new, AdvancedRocketryBlocks.blockTransciever).build(null);
		TILE_DATA_BUS = TileEntityType.Builder.create(TileDataBus::new, AdvancedRocketryBlocks.blockDataBus).build(null);
		TILE_FUELING_STATION = TileEntityType.Builder.create(TileFuelingStation::new, AdvancedRocketryBlocks.blockFuelingStation).build(null);
		TILE_MONITORIING_STATION = TileEntityType.Builder.create(TileRocketMonitoringStation::new, AdvancedRocketryBlocks.blockMonitoringStation).build(null);
		TILE_GUIDANCE_COMPUTER_HATCH = TileEntityType.Builder.create(TileGuidanceComputerAccessHatch::new, AdvancedRocketryBlocks.blockguidanceHatch).build(null);
		TILE_FLUID_LOADER = TileEntityType.Builder.create(TileFluidHatch::new, AdvancedRocketryBlocks.blockFluidLoader).build(null);
		TILE_FLUID_UNLOADER = TileEntityType.Builder.create(TileFluidHatch::new, AdvancedRocketryBlocks.blockFluidUnloader).build(null);
		TILE_ROCKET_LOADER = TileEntityType.Builder.create(TileRocketLoader::new, AdvancedRocketryBlocks.blockRocketLoader).build(null);
		TILE_ROCKET_UNLOADER = TileEntityType.Builder.create(TileRocketUnloader::new, AdvancedRocketryBlocks.blockRocketUnloader).build(null);
		TILE_BLACK_HOLE_GENERATOR = TileEntityType.Builder.create(TileBlackHoleGenerator::new, AdvancedRocketryBlocks.blockBlackHoleGenerator).build(null);
		TILE_MICROWAVE_RECIEVER = TileEntityType.Builder.create(TileMicrowaveReciever::new, AdvancedRocketryBlocks.blockMicrowaveReciever).build(null);
		TILE_CENTRIFUGE = TileEntityType.Builder.create(TileCentrifuge::new, AdvancedRocketryBlocks.blockCentrifuge).build(null);
		TILE_CHEMICAL_REACTOR = TileEntityType.Builder.create(TileChemicalReactor::new, AdvancedRocketryBlocks.blockChemicalReactor).build(null);
		TILE_CRYSTALLIZER = TileEntityType.Builder.create(TileCrystallizer::new, AdvancedRocketryBlocks.blockCrystallizer).build(null);
		TILE_CUTTING_MACHINE = TileEntityType.Builder.create(TileCuttingMachine::new, AdvancedRocketryBlocks.blockCuttingMachine).build(null);
		TILE_ARC_FURNACE = TileEntityType.Builder.create(TileElectricArcFurnace::new, AdvancedRocketryBlocks.blockArcFurnace).build(null);
		TILE_PREC_LASER_ETCHER = TileEntityType.Builder.create(TilePrecisionLaserEtcher::new, AdvancedRocketryBlocks.blockPrecisionLaserEtcher).build(null);
		TILE_SOLAR_ARRAY = TileEntityType.Builder.create(TileSolarArray::new, AdvancedRocketryBlocks.blockSolarArray).build(null);
		TILE_ELECTROLYSER = TileEntityType.Builder.create(TileElectrolyser::new, AdvancedRocketryBlocks.blockElectrolyser).build(null);
		TILE_LATHE = TileEntityType.Builder.create(TileLathe::new, AdvancedRocketryBlocks.blockLathe).build(null);
		TILE_PREC_ASS = TileEntityType.Builder.create(TilePrecisionAssembler::new, AdvancedRocketryBlocks.blockPrecisionAssembler).build(null);
		TILE_ROLLING = TileEntityType.Builder.create(TileRollingMachine::new, AdvancedRocketryBlocks.blockRollingMachine).build(null);
		TILE_ASTROBODY_DATA = TileEntityType.Builder.create(TileAstrobodyDataProcessor::new, AdvancedRocketryBlocks.blockPlanetAnalyser).build(null);
		TILE_TERRAFORMER = TileEntityType.Builder.create(TileAtmosphereTerraformer::new, AdvancedRocketryBlocks.blockAtmosphereTerraformer).build(null);
		TILE_BEACON = TileEntityType.Builder.create(TileBeacon::new, AdvancedRocketryBlocks.blockBeacon).build(null);
		TILE_BIOME_SCANNER = TileEntityType.Builder.create(TileBiomeScanner::new, AdvancedRocketryBlocks.blockBiomeScanner).build(null);
		TILE_GRAVITY_CONTROLLER = TileEntityType.Builder.create(TileAreaGravityController::new, AdvancedRocketryBlocks.blockGravityMachine).build(null);
		TILE_OBSERVATORY = TileEntityType.Builder.create(TileObservatory::new, AdvancedRocketryBlocks.blockObservatory).build(null);
		TILE_PLANET_SELECTOR = TileEntityType.Builder.create(TilePlanetSelector::new, AdvancedRocketryBlocks.blockPlanetSelector).build(null);
		TILE_RAILGUN = TileEntityType.Builder.create(TileRailgun::new, AdvancedRocketryBlocks.blockRailgun).build(null);
		TILE_SPACE_ELEVATOR = TileEntityType.Builder.create(TileSpaceElevator::new, AdvancedRocketryBlocks.blockSpaceElevatorController).build(null);
		TILE_SPACE_LASER = TileEntityType.Builder.create(TileOrbitalLaserDrill::new, AdvancedRocketryBlocks.blockSpaceLaser).build(null);
		TILE_WARP_CORE = TileEntityType.Builder.create(TileWarpCore::new, AdvancedRocketryBlocks.blockWarpCore).build(null);
		TILE_CO2_SCRUBBER = TileEntityType.Builder.create(TileCO2Scrubber::new, AdvancedRocketryBlocks.blockOxygenScrubber).build(null);
		TILE_OXYGEN_CHARGER = TileEntityType.Builder.create(TileGasChargePad::new, AdvancedRocketryBlocks.blockOxygenCharger).build(null);
		TILE_OXYGEN_VENT = TileEntityType.Builder.create(TileOxygenVent::new, AdvancedRocketryBlocks.blockOxygenVent).build(null);
		//TILE_CHIP_STORAGE = TileEntityType.Builder.create(TileChipStorage::new, AdvancedRocketryBlocks.blockOxygenVent).build(null);
		TILE_SAT_CONTROL = TileEntityType.Builder.create(TileSatelliteTerminal::new, AdvancedRocketryBlocks.blockSatelliteControlCenter).build(null);
		TILE_SAT_BUILDER = TileEntityType.Builder.create(TileSatelliteBuilder::new, AdvancedRocketryBlocks.blockSatelliteBuilder).build(null);
		TILE_DOCKING_PORT = TileEntityType.Builder.create(TileDockingPort::new, AdvancedRocketryBlocks.blockDockingPort).build(null);
		TILE_LANDING_PAD = TileEntityType.Builder.create(TileLandingPad::new, AdvancedRocketryBlocks.blockLandingPad).build(null);
		TILE_HOLOGRAM = TileEntityType.Builder.create(TileHolographicPlanetSelector::new, AdvancedRocketryBlocks.blockPlanetHoloSelector).build(null);
		TILE_ALT_CONTROLLER = TileEntityType.Builder.create(TileStationAltitudeController::new, AdvancedRocketryBlocks.blockAltitudeController).build(null);
		TILE_ORIENTATION_CONTROLLER = TileEntityType.Builder.create(TileStationOrientationController::new, AdvancedRocketryBlocks.blockOrientationController).build(null);
		TILE_STATION_GRAVITY_CONTROLLER = TileEntityType.Builder.create(TileStationGravityController::new, AdvancedRocketryBlocks.blockGravityController).build(null);
		TILE_WARP_SHIP_CONTROLLER = TileEntityType.Builder.create(TileWarpController::new, AdvancedRocketryBlocks.blockWarpShipMonitor).build(null);
		TILE_ATM_DETECTOR = TileEntityType.Builder.create(TileAtmosphereDetector::new, AdvancedRocketryBlocks.blockOxygenDetection).build(null);
		TILE_DRILL = TileEntityType.Builder.create(TileDrill::new, AdvancedRocketryBlocks.blockDrill).build(null);
		TILE_FLUID_TANK = TileEntityType.Builder.create(TileFluidTank::new, AdvancedRocketryBlocks.blockPressureTank).build(null);
		TILE_FORCE_FIELD_PROJECTOR = TileEntityType.Builder.create(TileForceFieldProjector::new, AdvancedRocketryBlocks.blockForceFieldProjector).build(null);
		//TILE_CHIP_MACHINE = TileEntityType.Builder.create(TileMachineChipInfo::new, AdvancedRocketryBlocks.blockDrill).build(null);
		TILE_PUMP = TileEntityType.Builder.create(TilePump::new, AdvancedRocketryBlocks.blockPump).build(null);
		TILE_ROCKET_BUILDER = TileEntityType.Builder.create(TileRocketAssemblingMachine::new, AdvancedRocketryBlocks.blockRocketBuilder).build(null);
		TILE_SOLAR_PANEL = TileEntityType.Builder.create(TileSolarPanel::new, AdvancedRocketryBlocks.blockSolarGenerator).build(null);
		TILE_STATION_BUILDER = TileEntityType.Builder.create(TileStationAssembler::new, AdvancedRocketryBlocks.blockStationBuilder).build(null);
		TILE_WORK_STATION = TileEntityType.Builder.create(TileSuitWorkStation::new, AdvancedRocketryBlocks.blockSuitWorkStation).build(null);
		TILE_SEAL = TileEntityType.Builder.create(TileSeal::new, AdvancedRocketryBlocks.blockPipeSealer).build(null);
		TILE_STATION_DEPLOYED_ASSEMBLER = TileEntityType.Builder.create(TileUnmannedVehicleAssembler::new, AdvancedRocketryBlocks.blockDeployableRocketBuilder).build(null);
		TILE_GUIDANCE_COMPUTER = TileEntityType.Builder.create(TileGuidanceComputer::new, AdvancedRocketryBlocks.blockGuidanceComputer).build(null);
		TILE_SATELLITE_HATCH = TileEntityType.Builder.create(TileSatelliteHatch::new, AdvancedRocketryBlocks.blockSatelliteHatch).build(null);
		
		IForgeRegistry<TileEntityType<?>> r = evt.getRegistry();
		r.registerAll(
				TILE_WIRELESS_TRANSCIEVER.setRegistryName("wireless_transciever"),
				TILE_DATA_BUS.setRegistryName("data_bus"),
				TILE_FUELING_STATION.setRegistryName("fueling_station"),
				TILE_MONITORIING_STATION.setRegistryName("monitoring_station"),
				TILE_GUIDANCE_COMPUTER_HATCH.setRegistryName("guidance_computer_hatch"),
				TILE_FLUID_LOADER.setRegistryName("fluid_loader"),
				TILE_FLUID_UNLOADER.setRegistryName("fluid_unloader"),
				TILE_ROCKET_LOADER.setRegistryName("rocket_loader"),
				TILE_ROCKET_UNLOADER.setRegistryName("rocket_unloader"),
				TILE_BLACK_HOLE_GENERATOR.setRegistryName("black_hole_generator"),
				TILE_MICROWAVE_RECIEVER.setRegistryName("microwave_reciever"),
				TILE_CENTRIFUGE.setRegistryName("centrifuge"),
				TILE_CHEMICAL_REACTOR.setRegistryName("chemical_reactor"),
				TILE_CRYSTALLIZER.setRegistryName("crystallizer"),
				TILE_CUTTING_MACHINE.setRegistryName("cutting_machine"),
				TILE_ARC_FURNACE.setRegistryName("arc_furnace"),
				TILE_PREC_LASER_ETCHER.setRegistryName("precisionlaseretcher"),
				TILE_ELECTROLYSER.setRegistryName("electrolyser"),
				TILE_LATHE.setRegistryName("lathe"),
				TILE_PREC_ASS.setRegistryName("prec_ass"),
				TILE_ROLLING.setRegistryName("rolling"),
				TILE_ASTROBODY_DATA.setRegistryName("astrobody_data"),
				TILE_TERRAFORMER.setRegistryName("terraformer"),
				TILE_BEACON.setRegistryName("beacon"),
				TILE_BIOME_SCANNER.setRegistryName("biome_scanner"),
				TILE_GRAVITY_CONTROLLER.setRegistryName("gravity_controller"),
				TILE_OBSERVATORY.setRegistryName("observatory"),
				TILE_PLANET_SELECTOR.setRegistryName("planet_selector"),
				TILE_RAILGUN.setRegistryName("railgun"),
				TILE_SPACE_ELEVATOR.setRegistryName("space_elevator"),
				TILE_SPACE_LASER.setRegistryName("space_laser"),
				TILE_WARP_CORE.setRegistryName("warp_core"),
				TILE_CO2_SCRUBBER.setRegistryName("co2_scrubber"),
				TILE_OXYGEN_CHARGER.setRegistryName("oxygen_charger"),
				TILE_OXYGEN_VENT.setRegistryName("oxygen_vent"),
				//TILE_CHIP_STORAGE.setRegistryName("//chip_storage"),
				TILE_SAT_CONTROL.setRegistryName("sat_control"),
				TILE_SAT_BUILDER.setRegistryName("sat_builder"),
				TILE_DOCKING_PORT.setRegistryName("docking_port"),
				TILE_LANDING_PAD.setRegistryName("landing_pad"),
				TILE_HOLOGRAM.setRegistryName("hologram"),
				TILE_ALT_CONTROLLER.setRegistryName("alt_controller"),
				TILE_ORIENTATION_CONTROLLER.setRegistryName("orientation_controller"),
				TILE_WARP_SHIP_CONTROLLER.setRegistryName("warp_ship_controller"),
				TILE_STATION_GRAVITY_CONTROLLER.setRegistryName("station_gravity_controller"),
				TILE_ATM_DETECTOR.setRegistryName("atm_detector"),
				TILE_DRILL.setRegistryName("drill"),
				TILE_FLUID_TANK.setRegistryName("fluid_tank"),
				TILE_FORCE_FIELD_PROJECTOR.setRegistryName("force_field_projector"),
				//TILE_CHIP_MACHINE.setRegistryName("//chip_machine"),
				TILE_PUMP.setRegistryName("pump"),
				TILE_ROCKET_BUILDER.setRegistryName("rocket_builder"),
				TILE_SOLAR_PANEL.setRegistryName("solar_panel"),
				TILE_STATION_BUILDER.setRegistryName("station_builder"),
				TILE_WORK_STATION.setRegistryName("work_station"),
				TILE_SEAL.setRegistryName("seal"),
				TILE_STATION_DEPLOYED_ASSEMBLER.setRegistryName("deployed_assembler"),
				TILE_GUIDANCE_COMPUTER.setRegistryName("guidance_computer"),
				TILE_SATELLITE_HATCH.setRegistryName("satellite_hatch")
				);
		
		
		registerTileEntityTypesToBlocks();
	}
	
	
	public static void registerTileEntityTypesToBlocks()
	{
		((BlockTile)AdvancedRocketryBlocks.blockOxygenScrubber)._setTile(AdvancedRocketryTileEntityType.TILE_CO2_SCRUBBER);

		((BlockTile)AdvancedRocketryBlocks.blockOrientationController)._setTile(AdvancedRocketryTileEntityType.TILE_ORIENTATION_CONTROLLER);
		((BlockTile)AdvancedRocketryBlocks.blockGravityController)._setTile(AdvancedRocketryTileEntityType.TILE_STATION_GRAVITY_CONTROLLER);
		((BlockTile)AdvancedRocketryBlocks.blockAltitudeController)._setTile(AdvancedRocketryTileEntityType.TILE_ALT_CONTROLLER);
		((BlockTile)AdvancedRocketryBlocks.blockOxygenCharger)._setTile(AdvancedRocketryTileEntityType.TILE_OXYGEN_CHARGER);
		((BlockTile)AdvancedRocketryBlocks.blockOxygenVent)._setTile(AdvancedRocketryTileEntityType.TILE_OXYGEN_VENT);
		((BlockTile)AdvancedRocketryBlocks.blockRocketBuilder)._setTile(AdvancedRocketryTileEntityType.TILE_ROCKET_BUILDER);
		((BlockTile)AdvancedRocketryBlocks.blockDeployableRocketBuilder)._setTile(AdvancedRocketryTileEntityType.TILE_STATION_DEPLOYED_ASSEMBLER);
		((BlockTile)AdvancedRocketryBlocks.blockStationBuilder)._setTile(AdvancedRocketryTileEntityType.TILE_STATION_BUILDER);
		((BlockTile)AdvancedRocketryBlocks.blockFuelingStation)._setTile(AdvancedRocketryTileEntityType.TILE_FUELING_STATION);

		((BlockTile)AdvancedRocketryBlocks.blockMonitoringStation)._setTile(AdvancedRocketryTileEntityType.TILE_MONITORIING_STATION);

		((BlockTile)AdvancedRocketryBlocks.blockWarpShipMonitor)._setTile(AdvancedRocketryTileEntityType.TILE_WARP_SHIP_CONTROLLER);

		((BlockTile)AdvancedRocketryBlocks.blockSatelliteBuilder)._setTile(AdvancedRocketryTileEntityType.TILE_SAT_BUILDER);

		((BlockTile)AdvancedRocketryBlocks.blockSatelliteControlCenter)._setTile(AdvancedRocketryTileEntityType.TILE_SAT_CONTROL);

		((BlockTile)AdvancedRocketryBlocks.blockMicrowaveReciever)._setTile(AdvancedRocketryTileEntityType.TILE_MICROWAVE_RECIEVER);

		((BlockTile)AdvancedRocketryBlocks.blockCentrifuge)._setTile(AdvancedRocketryTileEntityType.TILE_CENTRIFUGE);

		//Arcfurnace
		((BlockTile)AdvancedRocketryBlocks.blockArcFurnace)._setTile(AdvancedRocketryTileEntityType.TILE_ARC_FURNACE);
		((BlockTile)AdvancedRocketryBlocks.blockPrecisionLaserEtcher)._setTile(AdvancedRocketryTileEntityType.TILE_PREC_LASER_ETCHER);
		((BlockARHatch)AdvancedRocketryBlocks.blockDataBus)._setTile(AdvancedRocketryTileEntityType.TILE_DATA_BUS);
		((BlockARHatch)AdvancedRocketryBlocks.blockSatelliteHatch)._setTile(AdvancedRocketryTileEntityType.TILE_SATELLITE_HATCH);
		((BlockARHatch)AdvancedRocketryBlocks.blockFluidLoader)._setTile(AdvancedRocketryTileEntityType.TILE_FLUID_LOADER);
		((BlockARHatch)AdvancedRocketryBlocks.blockFluidUnloader)._setTile(AdvancedRocketryTileEntityType.TILE_FLUID_UNLOADER);
		((BlockARHatch)AdvancedRocketryBlocks.blockRocketLoader)._setTile(AdvancedRocketryTileEntityType.TILE_ROCKET_LOADER);
		((BlockARHatch)AdvancedRocketryBlocks.blockRocketUnloader)._setTile(AdvancedRocketryTileEntityType.TILE_ROCKET_UNLOADER);
		((BlockARHatch)AdvancedRocketryBlocks.blockguidanceHatch)._setTile(AdvancedRocketryTileEntityType.TILE_GUIDANCE_COMPUTER_HATCH);

		((BlockTile)AdvancedRocketryBlocks.blockPrecisionAssembler)._setTile(AdvancedRocketryTileEntityType.TILE_PREC_ASS);
		((BlockTile)AdvancedRocketryBlocks.blockCuttingMachine)._setTile(AdvancedRocketryTileEntityType.TILE_CUTTING_MACHINE);
		((BlockTile)AdvancedRocketryBlocks.blockCrystallizer)._setTile(AdvancedRocketryTileEntityType.TILE_CRYSTALLIZER);
		((BlockTile)AdvancedRocketryBlocks.blockWarpCore)._setTile(AdvancedRocketryTileEntityType.TILE_WARP_CORE);
		((BlockTile)AdvancedRocketryBlocks.blockChemicalReactor)._setTile(AdvancedRocketryTileEntityType.TILE_CHEMICAL_REACTOR);
		((BlockTile)AdvancedRocketryBlocks.blockLathe)._setTile(AdvancedRocketryTileEntityType.TILE_LATHE);
		((BlockTile)AdvancedRocketryBlocks.blockRollingMachine)._setTile(AdvancedRocketryTileEntityType.TILE_ROLLING);
		((BlockTile)AdvancedRocketryBlocks.blockElectrolyser)._setTile(AdvancedRocketryTileEntityType.TILE_ELECTROLYSER);
		((BlockTile)AdvancedRocketryBlocks.blockAtmosphereTerraformer)._setTile(AdvancedRocketryTileEntityType.TILE_TERRAFORMER);
		((BlockTile)AdvancedRocketryBlocks.blockPlanetAnalyser)._setTile(AdvancedRocketryTileEntityType.TILE_ASTROBODY_DATA);
		((BlockTile)AdvancedRocketryBlocks.blockObservatory)._setTile(AdvancedRocketryTileEntityType.TILE_OBSERVATORY);
		((BlockTile)AdvancedRocketryBlocks.blockBlackHoleGenerator)._setTile(AdvancedRocketryTileEntityType.TILE_BLACK_HOLE_GENERATOR);
		((BlockTile)AdvancedRocketryBlocks.blockPump)._setTile(AdvancedRocketryTileEntityType.TILE_PUMP);

		((BlockTile)AdvancedRocketryBlocks.blockGuidanceComputer)._setTile(AdvancedRocketryTileEntityType.TILE_GUIDANCE_COMPUTER);
		((BlockTile)AdvancedRocketryBlocks.blockPlanetSelector)._setTile(AdvancedRocketryTileEntityType.TILE_PLANET_SELECTOR);
		((BlockTile)AdvancedRocketryBlocks.blockPlanetHoloSelector)._setTile(AdvancedRocketryTileEntityType.TILE_HOLOGRAM);
		((BlockTile)AdvancedRocketryBlocks.blockBiomeScanner)._setTile(AdvancedRocketryTileEntityType.TILE_BIOME_SCANNER);
		((BlockTile)AdvancedRocketryBlocks.blockSuitWorkStation)._setTile(AdvancedRocketryTileEntityType.TILE_WORK_STATION);
		((BlockTile)AdvancedRocketryBlocks.blockRailgun)._setTile(AdvancedRocketryTileEntityType.TILE_RAILGUN);
		((BlockTile)AdvancedRocketryBlocks.blockSpaceElevatorController)._setTile(AdvancedRocketryTileEntityType.TILE_SPACE_ELEVATOR);
		((BlockTile)AdvancedRocketryBlocks.blockBeacon)._setTile(AdvancedRocketryTileEntityType.TILE_BEACON);
		((BlockTile)AdvancedRocketryBlocks.blockSolarGenerator)._setTile(AdvancedRocketryTileEntityType.TILE_SOLAR_PANEL);
		((BlockTile)AdvancedRocketryBlocks.blockTransciever)._setTile(AdvancedRocketryTileEntityType.TILE_WIRELESS_TRANSCIEVER);

		//Configurable stuff
		((BlockTile)AdvancedRocketryBlocks.blockGravityMachine)._setTile(AdvancedRocketryTileEntityType.TILE_GRAVITY_CONTROLLER);
		
		((BlockTile)AdvancedRocketryBlocks.blockSpaceLaser)._setTile(AdvancedRocketryTileEntityType.TILE_SPACE_LASER);
	}
}
