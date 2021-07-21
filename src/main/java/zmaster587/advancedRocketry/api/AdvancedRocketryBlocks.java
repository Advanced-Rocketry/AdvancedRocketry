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
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import zmaster587.advancedRocketry.block.*;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.world.tree.AlienTree;
import zmaster587.libVulpes.block.BlockAlphaTexture;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import zmaster587.libVulpes.block.BlockMotor;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;

/**
 * Stores references to Advanced Rocketry's blocks
 *
 */
public class AdvancedRocketryBlocks {
	public static RegistryObject<FlowingFluidBlock> blockOxygenFluid;
	public static RegistryObject<FlowingFluidBlock> blockHydrogenFluid;
	public static RegistryObject<FlowingFluidBlock> blockFuelFluid;
	public static Block test;
	public static RegistryObject<FlowingFluidBlock> blockNitrogenFluid;
	public static RegistryObject<FlowingFluidBlock> blockEnrichedLavaFluid;

	//Blocks -------------------------------------------------------------------------------------
	static AbstractBlock.Properties machineLineProperties = AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f);
	static AbstractBlock.Properties crystalProperties = AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f).sound(SoundType.GLASS);

	public static Block blocksGeode = new Block(AbstractBlock.Properties.create(MaterialGeode.geode).hardnessAndResistance(6f, 2000F).harvestTool(ToolType.get("jackhammer")));
	public static Block blockLaunchpad = new BlockLinkedHorizontalTexture(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f, 10f));
	public static Block blockStructureTower = new BlockAlphaTexture(machineLineProperties);


	public static Block blockGenericSeat = new BlockSeat(AbstractBlock.Properties.create(Material.WOOL).hardnessAndResistance(0.5f));
	public static Block blockEngine = new BlockRocketMotor(machineLineProperties);
	public static Block blockBipropellantEngine = new BlockBipropellantRocketMotor(machineLineProperties);
	public static Block blockAdvEngine = new BlockAdvancedRocketMotor(machineLineProperties);
	public static Block blockFuelTank = new BlockFuelTank(machineLineProperties);
	public static Block blockBipropellantFuelTank = new BlockBipropellantFuelTank(machineLineProperties);
	public static Block blockSawBlade = new BlockMotor(machineLineProperties,1f);

	public static Block blockConcrete = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f, 16f));
	public static Block blockPlatePress = new BlockSmallPlatePress(machineLineProperties);
	public static Block blockAirLock = new DoorBlock(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f, 8f));
	public static Block blockLandingPad = new BlockLandingPad(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f,3f));
	public static Block blockOxygenDetection = new BlockRedstoneEmitter(machineLineProperties,"advancedrocketry:atmosphereDetector_active");
	public static Block blockCO2Scrubber = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockUnlitTorch = new BlockTorchUnlit(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().hardnessAndResistance(0));
	public static Block blockUnlitTorchWall = new BlockTorchUnlitWall(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().hardnessAndResistance(0));

	public static Block blockVitrifiedSand = new Block(AbstractBlock.Properties.create(Material.SAND).hardnessAndResistance(0.5f));
	public static Block blockCharcoalLog = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD));
	public static Block blockElectricMushroom = new BlockElectricMushroom(AbstractBlock.Properties.create(Material.PLANTS).hardnessAndResistance(0.0f));
	public static Block blockCrystal = new BlockAlphaTexture(crystalProperties);
	public static Block blockCrystalRed = new BlockAlphaTexture(crystalProperties);
	public static Block blockCrystalOrange = new BlockAlphaTexture(crystalProperties);
	public static Block blockCrystalYellow = new BlockAlphaTexture(crystalProperties);
	public static Block blockCrystalGreen = new BlockAlphaTexture(crystalProperties);
	public static Block blockCrystalCyan = new BlockAlphaTexture(crystalProperties);
	public static Block blockCrystalBlue = new BlockAlphaTexture(crystalProperties);
	public static Block blockCrystalPurple = new BlockAlphaTexture(crystalProperties);

	public static Block[] crystalBlocks = new Block[] {
			blockCrystal,
			blockCrystalRed,
			blockCrystalOrange,
			blockCrystalYellow,
			blockCrystalGreen,
			blockCrystalCyan,
			blockCrystalBlue,
			blockCrystalPurple};

	public static Block blockOrientationController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR);
	public static Block blockGravityController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR);
	public static Block blockAltitudeController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR);
	public static Block blockOxygenCharger = new BlockHalfTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockOxygenVent = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockCircleLight = new Block(AbstractBlock.Properties.create(Material.ROCK).setLightLevel((p_235470_0_) -> {
		return 14;
	}));
	public static Block blockLens = new BlockAlphaTexture(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(0.3f).notSolid());
	public static Block blockRocketBuilder = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockForceField = new BlockForceField(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(-1.0F, 3600000.0F).noDrops().notSolid());
	public static Block blockForceFieldProjector = new BlockForceFieldProjector(machineLineProperties);
	public static Block blockDeployableRocketBuilder = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockStationBuilder = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockFuelingStation = new BlockTileRedstoneEmitter(machineLineProperties, GuiHandler.guiId.MODULAR);

	public static Block blockMonitoringStation = new BlockTileNeighborUpdate(machineLineProperties, GuiHandler.guiId.MODULARNOINV);

	public static Block blockWarpShipMonitor = new BlockWarpController(machineLineProperties, GuiHandler.guiId.MODULARNOINV);

	public static Block blockSatelliteBuilder = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);

	public static Block blockSatelliteControlCenter = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);

	public static Block blockMicrowaveReciever = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);

	public static Block blockCentrifuge = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);

	//Arcfurnace
	public static Block blockArcFurnace = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	
	public static Block blockPrecisionLaserEtcher = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);

	public static Block blockMoonTurf = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.SNOW).hardnessAndResistance(0.5f));
	public static Block blockMoonTurfDark = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.BROWN).hardnessAndResistance(0.5f));
	public static Block blockHotTurf = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.NETHERRACK).hardnessAndResistance(0.5f));

	public static Block blockDataBus = new BlockARHatch(machineLineProperties);
	public static Block blockSatelliteHatch = new BlockARHatch(machineLineProperties);
	public static Block blockFluidLoader = new BlockARHatch(machineLineProperties);
	public static Block blockFluidUnloader = new BlockARHatch(machineLineProperties);
	public static Block blockRocketLoader = new BlockARHatch(machineLineProperties);
	public static Block blockRocketUnloader = new BlockARHatch(machineLineProperties);
	public static Block blockguidanceHatch = new BlockARHatch(machineLineProperties);

	public static Block blockAlienWood = registerLog(MaterialColor.BLUE, MaterialColor.LIGHT_BLUE);
	public static Block blockAlienLeaves = registerLeaves();
	public static Block blockAlienSapling = new SaplingBlock(new AlienTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT));
	public static Block blockAlienPlanks = new Block(AbstractBlock.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD));

	public static Block blockLightSource = new BlockLightSource(AbstractBlock.Properties.create(Material.ROCK).setLightLevel((p_235470_0_) -> {
		return 14;
	}));
	public static Block blockBlastBrick = new BlockMultiBlockComponentVisible(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3, 15));
	public static Block blockQuartzCrucible = new BlockQuartzCrucible(AbstractBlock.Properties.create(Material.ROCK));
	//public static Block blockAstroBed = new BlockAstroBed(DyeColor.WHITE, ).setHardness(0.2F);

	public static Block blockPrecisionAssembler = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockCuttingMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockCrystallizer = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockWarpCore = new BlockWarpCore(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockChemicalReactor = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockLathe = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockRollingMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockElectrolyser = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockAtmosphereTerraformer = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockPlanetAnalyser = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockObservatory = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockBlackHoleGenerator = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockPump = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);

	public static Block blockGuidanceComputer = new BlockTile(machineLineProperties,GuiHandler.guiId.MODULAR);
	public static Block blockPlanetSelector = new BlockTile(machineLineProperties,GuiHandler.guiId.MODULARFULLSCREEN);
	public static Block blockPlanetHoloSelector = new BlockHalfTile(machineLineProperties,GuiHandler.guiId.MODULAR);
	public static Block blockBiomeScanner = new BlockMultiblockMachine(machineLineProperties,GuiHandler.guiId.MODULARNOINV);
	public static Block blockDrill = new BlockMiningDrill(machineLineProperties);
	public static Block blockSuitWorkStation = new BlockSuitWorkstation(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockRailgun = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockSpaceElevatorController = new BlockMultiblockMachine(machineLineProperties,  GuiHandler.guiId.MODULAR);
	public static Block blockBeacon = new BlockBeacon(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockIntake = new BlockIntake(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
	public static Block blockPressureTank = new BlockPressurizedFluidTank(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
	public static Block blockSolarPanel = new Block(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
	public static Block blockSolarGenerator = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockDockingPort = new BlockStationModuleDockingPort(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
	public static Block blockPipeSealer = new BlockSeal(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(0.5f));
	public static Block blockThermiteTorch = new TorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().setLightLevel((p_235470_0_) -> {
		return 14;
	}).sound(SoundType.WOOD), ParticleTypes.FLAME);
	public static Block blockThermiteTorchWall = new WallTorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().setLightLevel((p_235470_0_) -> {
		return 14;
	}).sound(SoundType.WOOD), ParticleTypes.FLAME);
	public static Block blockBasalt = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(5, 15));
	public static Block blockLandingFloat = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(1,1));
	public static Block blockTransciever = new BlockTransciever(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockVacuumLaser = new BlockFullyRotatable(machineLineProperties);

	public static Block blockRocketFire = new BlockRocketFire();
	public static Block blockNuclearEngine = new BlockNuclearRocketMotor(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(2f));
	public static Block blockNuclearFuelTank = new BlockNuclearFuelTank(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(2f));
	public static Block blockNuclearCore = new BlockNuclearCore(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(2f));
	
	
	//Configurable stuff
	public static Block blockGravityMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV);


	public static Block blockSpaceLaser = new BlockOrbitalLaserDrill(machineLineProperties);
	public static Block blockSolarArray = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockSolarArrayPanel = new Block(machineLineProperties);


	@SubscribeEvent(priority=EventPriority.HIGH)
	public static void registerBlocks(RegistryEvent.Register<Block> evt)
	{

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
				AdvancedRocketryBlocks.blockBipropellantEngine.setRegistryName("bipropellantrocketmotor"),
				AdvancedRocketryBlocks.blockFuelTank.setRegistryName("fueltank"),
				AdvancedRocketryBlocks.blockBipropellantFuelTank.setRegistryName("bipropellantfueltank"),
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
				AdvancedRocketryBlocks.blockPrecisionLaserEtcher.setRegistryName("precisionlaseretcher"),
				AdvancedRocketryBlocks.blockSawBlade.setRegistryName("sawbladeassbly"),
				AdvancedRocketryBlocks.blockLathe.setRegistryName("lathe"),
				AdvancedRocketryBlocks.blockRollingMachine.setRegistryName("rollingmachine"),
				AdvancedRocketryBlocks.blockPlatePress.setRegistryName("platepress"),
				AdvancedRocketryBlocks.blockStationBuilder.setRegistryName("stationbuilder"),
				AdvancedRocketryBlocks.blockElectrolyser.setRegistryName("electrolyser"),
				AdvancedRocketryBlocks.blockChemicalReactor.setRegistryName("chemicalreactor"),
				AdvancedRocketryBlocks.blockCO2Scrubber.setRegistryName("oxygenscrubber"),
				AdvancedRocketryBlocks.blockOxygenVent.setRegistryName("oxygenvent"),
				AdvancedRocketryBlocks.blockOxygenCharger.setRegistryName("oxygencharger"),
				AdvancedRocketryBlocks.blockAirLock.setRegistryName("airlock_door"),
				AdvancedRocketryBlocks.blockLandingPad.setRegistryName("landingpad"),
				AdvancedRocketryBlocks.blockWarpCore.setRegistryName("warpcore"),
				AdvancedRocketryBlocks.blockWarpShipMonitor.setRegistryName("stationmonitor"),
				AdvancedRocketryBlocks.blockOxygenDetection.setRegistryName("atmospheredetector"),
				AdvancedRocketryBlocks.blockUnlitTorch.setRegistryName("unlittorch"),
				AdvancedRocketryBlocks.blockUnlitTorchWall.setRegistryName("unlittorch_wall"),
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
				AdvancedRocketryBlocks.blockCrystalRed.setRegistryName("crystal_red"),
				AdvancedRocketryBlocks.blockCrystalOrange.setRegistryName("crystal_orange"),
				AdvancedRocketryBlocks.blockCrystalYellow.setRegistryName("crystal_yellow"),
				AdvancedRocketryBlocks.blockCrystalGreen.setRegistryName("crystal_green"),
				AdvancedRocketryBlocks.blockCrystalCyan.setRegistryName("crystal_cyan"),
				AdvancedRocketryBlocks.blockCrystalBlue.setRegistryName("crystal_blue"),
				AdvancedRocketryBlocks.blockCrystalPurple.setRegistryName("crystal_purple"),
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
				AdvancedRocketryBlocks.blockThermiteTorchWall.setRegistryName("thermitetorch_wall"),
				AdvancedRocketryBlocks.blockTransciever.setRegistryName("wirelesstransciever"),
				AdvancedRocketryBlocks.blockVacuumLaser.setRegistryName("vacuumlaser"),
				AdvancedRocketryBlocks.blockPump.setRegistryName("blockpump"),
				AdvancedRocketryBlocks.blockCentrifuge.setRegistryName("centrifuge"),
				AdvancedRocketryBlocks.blockBasalt.setRegistryName("basalt"),
				AdvancedRocketryBlocks.blockLandingFloat.setRegistryName("landingfloat"),
				AdvancedRocketryBlocks.blockSolarArray.setRegistryName("solararray"),
				AdvancedRocketryBlocks.blockSolarArrayPanel.setRegistryName("solararraypanel"),
				AdvancedRocketryBlocks.blockRocketFire.setRegistryName("rocketfire"),
				AdvancedRocketryBlocks.blockNuclearCore.setRegistryName("nuclearcore"),
				AdvancedRocketryBlocks.blockNuclearEngine.setRegistryName("nuclearengine"),
				AdvancedRocketryBlocks.blockNuclearFuelTank.setRegistryName("nucleartank"));

		//if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableGravityController.get())
		evt.getRegistry().register(AdvancedRocketryBlocks.blockGravityMachine.setRegistryName("gravitymachine"));

		//TODO, use different mechanism to enable/disable drill
		//if(zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().enableLaserDrill.get())
		evt.getRegistry().register(AdvancedRocketryBlocks.blockSpaceLaser.setRegistryName("spacelaser"));
	}

	private static RotatedPillarBlock registerLog(MaterialColor p_235430_0_, MaterialColor p_235430_1_) {
		return new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, (p_235431_2_) -> {
			return p_235431_2_.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? p_235430_0_ : p_235430_1_;
		}).hardnessAndResistance(2.0F).sound(SoundType.WOOD));
	}
	private static LeavesBlock registerLeaves() {
		return new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid());
	}
}
