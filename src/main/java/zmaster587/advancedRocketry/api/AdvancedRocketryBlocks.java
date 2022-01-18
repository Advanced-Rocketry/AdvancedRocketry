package zmaster587.advancedRocketry.api;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.*;
import zmaster587.advancedRocketry.block.multiblock.*;
import zmaster587.advancedRocketry.block.plant.BlockElectricMushroom;
import zmaster587.advancedRocketry.world.tree.AlienTree;
import zmaster587.libVulpes.block.*;
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
	public static RegistryObject<FlowingFluidBlock> blockNitrogenFluid;
	public static RegistryObject<FlowingFluidBlock> blockEnrichedLavaFluid;

	//Blocks -------------------------------------------------------------------------------------
	static AbstractBlock.Properties machineLineProperties = AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(2f);
	static AbstractBlock.Properties crystalProperties = AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f).sound(SoundType.GLASS);

	//Lights
	public static Block blockUnlitTorch = new BlockTorchUnlit(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().hardnessAndResistance(0));
	public static Block blockUnlitTorchWall = new BlockTorchUnlitWall(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().hardnessAndResistance(0));
	public static Block blockThermiteTorch = new TorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().setLightLevel((p_235470_0_) -> 15).sound(SoundType.NETHER_BRICK), ParticleTypes.FLAME);
	public static Block blockThermiteTorchWall = new WallTorchBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().zeroHardnessAndResistance().setLightLevel((p_235470_0_) -> 15).sound(SoundType.NETHER_BRICK), ParticleTypes.FLAME);
	public static Block blockStationLight = new Block(AbstractBlock.Properties.create(Material.ROCK).setLightLevel((p_235470_0_) -> 15));
	//World Generation blocks & plants
	public static Block blockCharcoalLog = new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD));
	public static Block blockLightwoodLog = registerLog(MaterialColor.BLUE, MaterialColor.LIGHT_BLUE);
	public static Block blockLightwoodPlanks = new Block(AbstractBlock.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(2.0F, 3.0F).sound(SoundType.WOOD));
	public static Block blockLightwoodLeaves = registerLeaves();
	public static Block blockLightwoodSapling = new SaplingBlock(new AlienTree(), AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.PLANT));
	public static Block blockElectricMushroom = new BlockElectricMushroom(AbstractBlock.Properties.create(Material.PLANTS).hardnessAndResistance(0.0f));
	public static Block blockVitrifiedSand = new Block(AbstractBlock.Properties.create(Material.SAND).hardnessAndResistance(0.5f));
	public static Block blockMoonTurf = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.SNOW).hardnessAndResistance(0.5f));
	public static Block blockMoonTurfDark = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.BROWN).hardnessAndResistance(0.5f));
	public static Block blockOxidizedFerricSand = new Block(AbstractBlock.Properties.create(Material.EARTH, MaterialColor.NETHERRACK).hardnessAndResistance(0.5f));
	public static Block blockBasalt = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(5, 15));
	public static Block blockGeode = new Block(AbstractBlock.Properties.create(MaterialGeode.geode).hardnessAndResistance(6f, 2000F).harvestTool(ToolType.get("jackhammer")));
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
	//Rocket blocks
	public static Block blockMonopropellantEngine = new BlockGenericRocketMotor(10, 1, FuelType.LIQUID_MONOPROPELLANT, machineLineProperties.notSolid());
	public static Block blockAdvancedMonopropellantEngine = new BlockGenericRocketMotor(50, 3, FuelType.LIQUID_MONOPROPELLANT, machineLineProperties.notSolid());
	public static Block blockBipropellantEngine = new BlockGenericRocketMotor(10, 1, FuelType.LIQUID_BIPROPELLANT, machineLineProperties.notSolid());
	public static Block blockAdvancedBipropellantEngine = new BlockGenericRocketMotor(50, 3, FuelType.LIQUID_BIPROPELLANT, machineLineProperties.notSolid());
	public static Block blockNuclearEngine = new BlockGenericRocketMotor(35, 1, FuelType.NUCLEAR_WORKING_FLUID, machineLineProperties.notSolid());
	public static Block blockMonopropellantFuelTank = new BlockGenericFuelTank(1000, FuelType.LIQUID_MONOPROPELLANT, machineLineProperties.notSolid().setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false));
	public static Block blockBipropellantFuelTank = new BlockGenericFuelTank(1000, FuelType.LIQUID_BIPROPELLANT, machineLineProperties.notSolid().setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false));
	public static Block blockOxidizerFuelTank = new BlockGenericFuelTank(1000, FuelType.LIQUID_OXIDIZER, machineLineProperties.notSolid().setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false));
	public static Block blockNuclearWorkingFluidTank = new BlockGenericFuelTank(1000, FuelType.NUCLEAR_WORKING_FLUID, machineLineProperties.notSolid().setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false));
	public static Block blockNuclearCore = new BlockNuclearCore(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(2f));
	//Rocket auxiliary
	public static Block blockSeat = new BlockSeat(AbstractBlock.Properties.create(Material.WOOL).hardnessAndResistance(0.5f));
	public static Block blockSatelliteBay = new BlockARHatch(machineLineProperties);
	public static Block blockGuidanceComputer = new BlockTile(machineLineProperties,GuiHandler.guiId.MODULAR);
	public static Block blockDrill = new BlockMiningDrill(machineLineProperties);
	public static Block blockIntake = new BlockIntake(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
	public static Block blockLandingFloat = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(1,1));
	//Rocket interaction blocks
	public static Block blockFuelingStation = new BlockTileRedstoneEmitter(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockRocketControlCenter = new BlockTileNeighborUpdate(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockSatelliteControlCenter = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockRocketLoader = new BlockARHatch(machineLineProperties);
	public static Block blockRocketUnloader = new BlockARHatch(machineLineProperties);
	public static Block blockFluidLoader = new BlockARHatch(machineLineProperties);
	public static Block blockFluidUnloader = new BlockARHatch(machineLineProperties);
	public static Block blockGuidanceComputerAccessHatch = new BlockARHatch(machineLineProperties);
	//Rocket pad blocks
	public static Block blockLaunchpad = new BlockLinkedHorizontalTexture(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f, 10f));
	public static Block blockLandingPad = new BlockLandingPad(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f,3f));
	public static Block blockStructureTower = new BlockAlphaTexture(machineLineProperties.notSolid().setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false));
	public static Block blockRocketAssembler = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockSpaceStationAssembler = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockUnmannedRocketAssembler = new BlockTileWithMultitooltip(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
    //Station interaction blocks
	public static Block blockHolographicPlanetSelector = new BlockHalfTile(machineLineProperties,GuiHandler.guiId.MODULAR);
	public static Block blockPlanetSelector = new BlockTile(machineLineProperties,GuiHandler.guiId.MODULARFULLSCREEN);
	public static Block blockOrientationController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR);
	public static Block blockGravityController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR);
	public static Block blockAltitudeController = new BlockTile(machineLineProperties,  GuiHandler.guiId.MODULAR);
	public static Block blockWarpController = new BlockWarpController(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockStationDockingPort = new BlockStationDockingPort(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f));
    //Oxygen system blocks
	public static Block blockAtmosphereDetector = new BlockRedstoneEmitter(machineLineProperties,"advancedrocketry:atmospheredetector_on");
	public static Block blockOxygenVent = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockCO2Scrubber = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockGasChargePad = new BlockHalfTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockSmallAirlockDoor = new DoorBlock(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3f, 8f));

	public static Block blockRocketFire = new BlockRocketFire(AbstractBlock.Properties.create(Material.FIRE, MaterialColor.TNT).doesNotBlockMovement().zeroHardnessAndResistance().setLightLevel((state) -> 15));
	public static Block blockSawBlade = new BlockMotor(machineLineProperties.notSolid().setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false),1f);
	public static Block blockQuartzCrucible = new BlockQuartzCrucible(AbstractBlock.Properties.create(Material.ROCK));
	public static Block blockLens = new BlockAlphaTexture(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(0.3f).notSolid().setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false));
	public static Block blockConcrete = new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2f, 16f));
	public static Block blockBlastBrick = new BlockMultiBlockComponentVisible(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(3, 15));
	public static Block blockSeal = new BlockSeal(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(0.5f));
	public static Block blockLaser = new BlockFullyRotatable(machineLineProperties);
	public static Block blockForceField = new BlockForceField(AbstractBlock.Properties.create(Material.GLASS).hardnessAndResistance(-1.0F, 3600000.0F).noDrops().notSolid());
	public static Block blockForceFieldProjector = new BlockForceFieldProjector(machineLineProperties);
	//Misc non-multiblock machines
	public static Block blockSuitWorkStation = new BlockSuitWorkstation(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockPressureTank = new BlockPressurizedFluidTank(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3f).notSolid().setOpaque((p_test_1_, p_test_2_, p_test_3_) -> false));
	public static Block blockPump = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockSmallPlatePress = new BlockSmallPlatePress(machineLineProperties);
	//MULTIBLOCK MACHINES
	//Item processors
	public static Block blockArcFurnace = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockRollingMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockLathe = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockCrystallizer = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockCuttingMachine = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockPrecisionAssembler = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockPrecisionLaserEtcher = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	//Fluid processors
	public static Block blockElectrolyzer = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockChemicalReactor = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockCentrifuge = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	//Data collection
	public static Block blockSatelliteAssembler = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockWirelessTransceiver = new BlockTransciever(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockDataBus = new BlockARHatch(machineLineProperties);
	public static Block blockObservatory = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockAstrobodyDataProcessor = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	//Energy production
	public static Block blockSolarGenerator = new BlockTile(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockSolarArrayPanel = new BlockSolarArrayPanel(machineLineProperties);
	public static Block blockSolarArray = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockMicrowaveReceiver = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockBlackHoleGenerator = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
    //Station multiblocks
	public static Block blockBiomeScanner = new BlockMultiblockMachine(machineLineProperties,GuiHandler.guiId.MODULARNOINV);
	public static Block blockRailgun = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockSpaceElevator = new BlockMultiblockMachine(machineLineProperties,  GuiHandler.guiId.MODULAR);
	public static Block blockBeacon = new BlockBeacon(machineLineProperties, GuiHandler.guiId.MODULAR);
	//Near-future or far-future multiblocks
	public static Block blockOrbitalLaserDrill = new BlockOrbitalLaserDrill(machineLineProperties);
	public static Block blockWarpCore = new BlockWarpCore(machineLineProperties, GuiHandler.guiId.MODULAR);
	public static Block blockAreaGravityController = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV);
	public static Block blockTerraformer = new BlockMultiblockMachine(machineLineProperties, GuiHandler.guiId.MODULARNOINV);



	@SubscribeEvent(priority=EventPriority.HIGH)
	public static void registerBlocks(RegistryEvent.Register<Block> evt) {
		evt.getRegistry().registerAll(
				//Lights
				AdvancedRocketryBlocks.blockUnlitTorch.setRegistryName("unlittorch"),
				AdvancedRocketryBlocks.blockUnlitTorchWall.setRegistryName("unlittorch_wall"),
				AdvancedRocketryBlocks.blockThermiteTorch.setRegistryName("thermitetorch"),
				AdvancedRocketryBlocks.blockThermiteTorchWall.setRegistryName("thermitetorch_wall"),
				AdvancedRocketryBlocks.blockStationLight.setRegistryName("stationlight"),
				//World generation blocks & plants
				AdvancedRocketryBlocks.blockCharcoalLog.setRegistryName("charcoallog"),
				AdvancedRocketryBlocks.blockLightwoodLog.setRegistryName("lightwoodlog"),
				AdvancedRocketryBlocks.blockLightwoodPlanks.setRegistryName("lightwoodplanks"),
				AdvancedRocketryBlocks.blockLightwoodLeaves.setRegistryName("lightwoodleaves"),
				AdvancedRocketryBlocks.blockLightwoodSapling.setRegistryName("lightwoodsapling"),
				AdvancedRocketryBlocks.blockElectricMushroom.setRegistryName("electricmushroom"),
				AdvancedRocketryBlocks.blockVitrifiedSand.setRegistryName("vitrifiedsand"),
				AdvancedRocketryBlocks.blockMoonTurf.setRegistryName("moonturf"),
				AdvancedRocketryBlocks.blockMoonTurfDark.setRegistryName("moonturf_dark"),
				AdvancedRocketryBlocks.blockOxidizedFerricSand.setRegistryName("oxidizedferricsand"),
				AdvancedRocketryBlocks.blockBasalt.setRegistryName("basalt"),
				AdvancedRocketryBlocks.blockGeode.setRegistryName("geode"),
				AdvancedRocketryBlocks.blockCrystal.setRegistryName("crystal"),
				AdvancedRocketryBlocks.blockCrystalRed.setRegistryName("crystal_red"),
				AdvancedRocketryBlocks.blockCrystalOrange.setRegistryName("crystal_orange"),
				AdvancedRocketryBlocks.blockCrystalYellow.setRegistryName("crystal_yellow"),
				AdvancedRocketryBlocks.blockCrystalGreen.setRegistryName("crystal_green"),
				AdvancedRocketryBlocks.blockCrystalCyan.setRegistryName("crystal_cyan"),
				AdvancedRocketryBlocks.blockCrystalBlue.setRegistryName("crystal_blue"),
				AdvancedRocketryBlocks.blockCrystalPurple.setRegistryName("crystal_purple"),
				//Rocket blocks
				AdvancedRocketryBlocks.blockMonopropellantEngine.setRegistryName("monopropellantrocketengine"),
				AdvancedRocketryBlocks.blockAdvancedMonopropellantEngine.setRegistryName("advancedmonopropellantrocketengine"),
				AdvancedRocketryBlocks.blockBipropellantEngine.setRegistryName("bipropellantrocketengine"),
				AdvancedRocketryBlocks.blockAdvancedBipropellantEngine.setRegistryName("advancedbipropellantrocketengine"),
				AdvancedRocketryBlocks.blockNuclearEngine.setRegistryName("nuclearrocketengine"),
				AdvancedRocketryBlocks.blockMonopropellantFuelTank.setRegistryName("monopropellantfueltank"),
				AdvancedRocketryBlocks.blockBipropellantFuelTank.setRegistryName("bipropellantfueltank"),
				AdvancedRocketryBlocks.blockOxidizerFuelTank.setRegistryName("oxidizerfueltank"),
				AdvancedRocketryBlocks.blockNuclearWorkingFluidTank.setRegistryName("nuclearworkingfluidtank"),
				AdvancedRocketryBlocks.blockNuclearCore.setRegistryName("nuclearcore"),
				//Rocket auxiliary
				AdvancedRocketryBlocks.blockSeat.setRegistryName("seat"),
				AdvancedRocketryBlocks.blockSatelliteBay.setRegistryName("satellitebay"),
				AdvancedRocketryBlocks.blockGuidanceComputer.setRegistryName("guidancecomputer"),
				AdvancedRocketryBlocks.blockDrill.setRegistryName("drill"),
				AdvancedRocketryBlocks.blockIntake.setRegistryName("intake"),
				AdvancedRocketryBlocks.blockLandingFloat.setRegistryName("landingfloat"),
                //Rocket interaction
				AdvancedRocketryBlocks.blockFuelingStation.setRegistryName("fuelingstation"),
				AdvancedRocketryBlocks.blockRocketControlCenter.setRegistryName("rocketcontrolcenter"),
				AdvancedRocketryBlocks.blockSatelliteControlCenter.setRegistryName("satellitecontrolcenter"),
				AdvancedRocketryBlocks.blockRocketLoader.setRegistryName("rocketloader"),
				AdvancedRocketryBlocks.blockRocketUnloader.setRegistryName("rocketunloader"),
				AdvancedRocketryBlocks.blockFluidLoader.setRegistryName("rocketfluidloader"),
				AdvancedRocketryBlocks.blockFluidUnloader.setRegistryName("rocketfluidunloader"),
				AdvancedRocketryBlocks.blockGuidanceComputerAccessHatch.setRegistryName("guidancecomputeraccesshatch"),
                //Rocket pad blocks
				AdvancedRocketryBlocks.blockLaunchpad.setRegistryName("launchpad"),
				AdvancedRocketryBlocks.blockLandingPad.setRegistryName("landingpad"),
				AdvancedRocketryBlocks.blockStructureTower.setRegistryName("structuretower"),
				AdvancedRocketryBlocks.blockRocketAssembler.setRegistryName("rocketassembler"),
				AdvancedRocketryBlocks.blockSpaceStationAssembler.setRegistryName("spacestationassembler"),
				AdvancedRocketryBlocks.blockUnmannedRocketAssembler.setRegistryName("unmannedrocketassembler"),
                //Station interaction blocks
				AdvancedRocketryBlocks.blockHolographicPlanetSelector.setRegistryName("holographicplanetselector"),
				AdvancedRocketryBlocks.blockPlanetSelector.setRegistryName("planetselector"),
				AdvancedRocketryBlocks.blockOrientationController.setRegistryName("orientationcontroller"),
				AdvancedRocketryBlocks.blockGravityController.setRegistryName("gravitycontroller"),
				AdvancedRocketryBlocks.blockAltitudeController.setRegistryName("altitudecontroller"),
				AdvancedRocketryBlocks.blockWarpController.setRegistryName("warpcontroller"),
				AdvancedRocketryBlocks.blockStationDockingPort.setRegistryName("stationdockingport"),
                //Oxygen system blocks
				AdvancedRocketryBlocks.blockAtmosphereDetector.setRegistryName("atmospheredetector"),
				AdvancedRocketryBlocks.blockOxygenVent.setRegistryName("oxygenvent"),
				AdvancedRocketryBlocks.blockCO2Scrubber.setRegistryName("oxygenscrubber"),
				AdvancedRocketryBlocks.blockGasChargePad.setRegistryName("gaschargepad"),
				AdvancedRocketryBlocks.blockSmallAirlockDoor.setRegistryName("airlock_door"),

				AdvancedRocketryBlocks.blockRocketFire.setRegistryName("rocketfire"),
				AdvancedRocketryBlocks.blockSawBlade.setRegistryName("sawbladeassembly"),
				AdvancedRocketryBlocks.blockQuartzCrucible.setRegistryName("quartzcrucible"),
				AdvancedRocketryBlocks.blockLens.setRegistryName("lens"),
				AdvancedRocketryBlocks.blockConcrete.setRegistryName("concrete"),
				AdvancedRocketryBlocks.blockBlastBrick.setRegistryName("blastbrick"),
				AdvancedRocketryBlocks.blockSeal.setRegistryName("seal"),
				AdvancedRocketryBlocks.blockLaser.setRegistryName("laser"),
				AdvancedRocketryBlocks.blockForceField.setRegistryName("forcefield"),
				AdvancedRocketryBlocks.blockForceFieldProjector.setRegistryName("forcefieldprojector"),
				//Misc non-multiblock machines
				AdvancedRocketryBlocks.blockSuitWorkStation.setRegistryName("suitworkstation"),
				AdvancedRocketryBlocks.blockPressureTank.setRegistryName("pressuretank"),
				AdvancedRocketryBlocks.blockPump.setRegistryName("pump"),
				AdvancedRocketryBlocks.blockSmallPlatePress.setRegistryName("smallplatepress"),
				//MULTIBLOCK MACHINES
	 			//Item processors
				AdvancedRocketryBlocks.blockArcFurnace.setRegistryName("arcfurnace"),
				AdvancedRocketryBlocks.blockRollingMachine.setRegistryName("rollingmachine"),
				AdvancedRocketryBlocks.blockLathe.setRegistryName("lathe"),
				AdvancedRocketryBlocks.blockCrystallizer.setRegistryName("crystallizer"),
				AdvancedRocketryBlocks.blockCuttingMachine.setRegistryName("cuttingmachine"),
				AdvancedRocketryBlocks.blockPrecisionAssembler.setRegistryName("precisionassembler"),
				AdvancedRocketryBlocks.blockPrecisionLaserEtcher.setRegistryName("precisionlaseretcher"),
				//Fluid processors
				AdvancedRocketryBlocks.blockElectrolyzer.setRegistryName("electrolyzer"),
				AdvancedRocketryBlocks.blockChemicalReactor.setRegistryName("chemicalreactor"),
				AdvancedRocketryBlocks.blockCentrifuge.setRegistryName("centrifuge"),
				//Data collection
				AdvancedRocketryBlocks.blockSatelliteAssembler.setRegistryName("satelliteassembler"),
				AdvancedRocketryBlocks.blockWirelessTransceiver.setRegistryName("wirelesstransceiver"),
				AdvancedRocketryBlocks.blockDataBus.setRegistryName("databus"),
				AdvancedRocketryBlocks.blockObservatory.setRegistryName("observatory"),
				AdvancedRocketryBlocks.blockAstrobodyDataProcessor.setRegistryName("astrobodydataprocessor"),
				//Energy production
				AdvancedRocketryBlocks.blockSolarGenerator.setRegistryName("solargenerator"),
				AdvancedRocketryBlocks.blockSolarArrayPanel.setRegistryName("solararraypanel"),
				AdvancedRocketryBlocks.blockSolarArray.setRegistryName("solararray"),
				AdvancedRocketryBlocks.blockMicrowaveReceiver.setRegistryName("microwavereceiver"),
				AdvancedRocketryBlocks.blockBlackHoleGenerator.setRegistryName("blackholegenerator"),
                //Station multiblocks
				AdvancedRocketryBlocks.blockBiomeScanner.setRegistryName("biomescanner"),
				AdvancedRocketryBlocks.blockRailgun .setRegistryName("railgun"),
				AdvancedRocketryBlocks.blockSpaceElevator.setRegistryName("spaceelevator"),
				AdvancedRocketryBlocks.blockBeacon.setRegistryName("beacon"),
				//Near-future or far-future multiblocks
				AdvancedRocketryBlocks.blockOrbitalLaserDrill.setRegistryName("orbitallaserdrill"),
				AdvancedRocketryBlocks.blockWarpCore.setRegistryName("warpcore"),
				AdvancedRocketryBlocks.blockAreaGravityController.setRegistryName("areagravitycontroller"),
				AdvancedRocketryBlocks.blockTerraformer.setRegistryName("terraformer")
				);
	}

	private static RotatedPillarBlock registerLog(MaterialColor p_235430_0_, MaterialColor p_235430_1_) {
		return new RotatedPillarBlock(AbstractBlock.Properties.create(Material.WOOD, (p_235431_2_) -> p_235431_2_.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? p_235430_0_ : p_235430_1_).hardnessAndResistance(2.0F).sound(SoundType.WOOD));
	}
	private static LeavesBlock registerLeaves() {
		return new LeavesBlock(AbstractBlock.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid());
	}
}
