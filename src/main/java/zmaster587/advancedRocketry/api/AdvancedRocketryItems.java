package zmaster587.advancedRocketry.api;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.RegistryObject;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.armor.ItemSpaceChest;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.tools.ItemAtmosphereAnalyzer;
import zmaster587.advancedRocketry.item.components.ItemBeaconFinder;
import zmaster587.advancedRocketry.item.ItemDataChip;
import zmaster587.advancedRocketry.item.ItemHovercraft;
import zmaster587.advancedRocketry.item.tools.ItemJackhammer;
import zmaster587.advancedRocketry.item.tools.ItemOreScanner;
import zmaster587.advancedRocketry.item.ItemSpaceStationContainer;
import zmaster587.advancedRocketry.item.ItemPlanetChip;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.advancedRocketry.item.ItemSatelliteChip;
import zmaster587.advancedRocketry.item.tools.ItemSealDetector;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.item.components.ItemJetpack;
import zmaster587.advancedRocketry.item.components.ItemPressureTank;
import zmaster587.advancedRocketry.item.components.ItemUpgrade;

/**
 * Stores references to Advanced Rocketry's items
 *
 */
public class AdvancedRocketryItems {

	//TODO: fix
	public static final SpaceSuitArmorMaterial spacesuit = new SpaceSuitArmorMaterial("spacesuit", 480, new int[] {1,1,1,1}, 0, new SoundEvent(new ResourceLocation("")), 0, 0, null);

	public static RegistryObject<Item> itemBucketRocketFuel;
	public static RegistryObject<Item> itemBucketNitrogen;
	public static RegistryObject<Item> itemBucketHydrogen;
	public static RegistryObject<Item> itemBucketOxygen;
	public static RegistryObject<Item> itemBucketEnrichedLava;

	public static Item.Properties typicalProperties = new Item.Properties().group(AdvancedRocketry.tabAdvRocketry);
	public static Item.Properties singleStackSize = new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(1);

	//Misc components
	public static Item itemUserInterface = new Item(typicalProperties).setRegistryName("userinterface");
	public static Item itemCarbonBrick = new Item(typicalProperties).setRegistryName("carbonbrick");
	public static Item itemSawBlade = new Item(typicalProperties).setRegistryName("sawblade");
	public static Item itemCarbonScrubberCartridge = new Item(new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).defaultMaxDamage(Short.MAX_VALUE-1)).setRegistryName("carbonscrubbercartridge"); //Short.MAX_VALUE is forge's wildcard, don't use it
	//Circuit components
	public static Item itemSiliconWafer = new Item(typicalProperties).setRegistryName("siliconwafer");
	public static Item itemBasicCircuitWafer = new Item(typicalProperties).setRegistryName("basiccircuitwafer");
	public static Item itemAdvancedCircuitWafer = new Item(typicalProperties).setRegistryName("advancedcircuitwafer");
	public static Item itemBasicCircuit = new Item(typicalProperties).setRegistryName("basiccircuit");
	public static Item itemAdvancedCircuit = new Item(typicalProperties).setRegistryName("advancedcircuit");
	public static Item itemTrackingCircuit = new Item(typicalProperties).setRegistryName("trackingcircuit");
	public static Item itemControlCircuit = new Item(typicalProperties).setRegistryName("controlcircuit");
	public static Item itemItemIOCircuit = new Item(typicalProperties).setRegistryName("itemiocircuit");
	public static Item itemFluidIOCircuit = new Item(typicalProperties).setRegistryName("fluidiocircuit");
	//Chips!
	public static Item itemSpaceStationChip = new ItemStationChip(singleStackSize).setRegistryName("spacestationchip");
	public static Item itemAsteroidChip = new ItemAsteroidChip(singleStackSize).setRegistryName("asteroidchip");
	public static Item itemSatelliteChip = new ItemSatelliteChip(typicalProperties).setRegistryName("satellitechip");
	public static Item itemPlanetChip = new ItemPlanetChip(typicalProperties).setRegistryName("planetchip");
	public static Item itemSpaceStationContainer = new ItemSpaceStationContainer(singleStackSize).setRegistryName("spacestationcontainer");
	//Satellite stuff
	public static Item itemSolarPanel = new Item(typicalProperties).setRegistryName("solarpanel");
	public static Item itemLargeSolarPanel = new Item(typicalProperties).setRegistryName("largesolarpanel");
	public static Item itemOpticalSensor = new Item(typicalProperties).setRegistryName("opticalsensor");
	public static Item itemCompositionSensor = new Item(typicalProperties).setRegistryName("compositionsensor");
	public static Item itemMassSensor = new Item(typicalProperties).setRegistryName("masssensor");
	public static Item itemMicrowaveTransmitter = new Item(typicalProperties).setRegistryName("microwavetransmitter");
	public static Item itemOreSensor = new Item(typicalProperties).setRegistryName("oresensor");
	public static Item itemDataUnit = new ItemDataChip(singleStackSize).setRegistryName("dataunit");
	public static Item itemSatellite = new ItemSatellite(singleStackSize).setRegistryName("satellite");
	//Suit Component Registration
	public static Item itemJetpack = new ItemJetpack(singleStackSize).setRegistryName("jetpack");
	public static Item itemAluminumPressureTank = new ItemPressureTank(new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(4), 2000).setRegistryName("aluminumpressuretank");
	public static Item itemSteelPressureTank = new ItemPressureTank(new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(4), 4000).setRegistryName("steelpressuretank");
	public static Item itemTitaniumPressureTank = new ItemPressureTank(new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(4), 16000).setRegistryName("titaniumpressuretank");
	public static Item itemHoverUpgrade = new ItemUpgrade(singleStackSize).setRegistryName("hoverupgrade");
	public static Item itemFlightSpeedUpgrade = new ItemUpgrade(singleStackSize).setRegistryName("flightspeedupgrade");
	public static Item itemBionicLegsUpgrade = new ItemUpgrade(singleStackSize).setRegistryName("bioniclegsupgrade");
	public static Item itemPaddedBootsUpgrade = new ItemUpgrade(singleStackSize).setRegistryName("paddedbootsupgrade");
	public static Item itemAntiFogVisorUpgrade = new ItemUpgrade(singleStackSize).setRegistryName("antifogvisorupgrade");
	public static Item itemBeaconFinderUpgrade = new ItemBeaconFinder(singleStackSize).setRegistryName("beaconfinderupgrade");
	//Armor registration
	public static Item itemSpaceSuitHelmet = new ItemSpaceArmor(singleStackSize, net.minecraft.item.ArmorMaterial.LEATHER, EquipmentSlotType.HEAD,4).setRegistryName("spacesuithelmet");
	public static Item itemSpaceSuitChestpiece = new ItemSpaceChest(singleStackSize, net.minecraft.item.ArmorMaterial.LEATHER, EquipmentSlotType.CHEST,6).setRegistryName("spacesuitchestpiece");
	public static Item itemSpaceSuitLeggings = new ItemSpaceArmor(singleStackSize, net.minecraft.item.ArmorMaterial.LEATHER, EquipmentSlotType.LEGS,4).setRegistryName("spacesuitleggings");
	public static Item itemSpaceSuitBoots = new ItemSpaceArmor(singleStackSize, net.minecraft.item.ArmorMaterial.LEATHER, EquipmentSlotType.FEET,4).setRegistryName("spacesuitboots");
	//Tools
	public static Item itemSealDetector = new ItemSealDetector(singleStackSize).setRegistryName("sealdetector");
	public static Item itemAtmosphereAnalyzer = new ItemAtmosphereAnalyzer(singleStackSize).setRegistryName("atmosphereanalyzer");
	public static Item itemOreScanner = new ItemOreScanner(singleStackSize).setRegistryName("orescanner");
	public static Item itemJackhammer = new ItemJackhammer(ItemTier.DIAMOND, new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(1).maxDamage(1500)).setRegistryName("jackhammer");
	public static Item itemHovercraft = new ItemHovercraft(singleStackSize).setRegistryName("hovercraft");
	//public static Item itemBasicLaserGun = new ItemBasicLaserGun(typicalProperties).setRegistryName("basic_laser_gun");
    //Weird block-items
	public static Item itemSmallAirlockDoor = new TallBlockItem(AdvancedRocketryBlocks.blockSmallAirlockDoor, singleStackSize).setRegistryName("smallairlockdoor");



	// register blocks
	public static Item.Properties typicalBlockProperties = new Item.Properties().group(AdvancedRocketry.tabAdvRocketry);

	public static Item itemOrbitalLaserDrill = new BlockItem(AdvancedRocketryBlocks.blockOrbitalLaserDrill, typicalBlockProperties);
	public static Item itemPrecisionAssembler = new BlockItem(AdvancedRocketryBlocks.blockPrecisionAssembler, typicalBlockProperties);
	public static Item itemArcFurnace = new BlockItem(AdvancedRocketryBlocks.blockArcFurnace, typicalBlockProperties);
	public static Item itemPrecisionLaserEtcher = new BlockItem(AdvancedRocketryBlocks.blockPrecisionLaserEtcher, typicalBlockProperties);
	public static Item itemBlastBrick = new BlockItem(AdvancedRocketryBlocks.blockBlastBrick, typicalBlockProperties);
	public static Item itemCrystallizer = new BlockItem(AdvancedRocketryBlocks.blockCrystallizer, typicalBlockProperties);
	public static Item itemLathe = new BlockItem(AdvancedRocketryBlocks.blockLathe, typicalBlockProperties);
	public static Item itemCuttingMachine = new BlockItem(AdvancedRocketryBlocks.blockCuttingMachine, typicalBlockProperties);
	public static Item itemObservatory = new BlockItem(AdvancedRocketryBlocks.blockObservatory, typicalBlockProperties);
	public static Item itemAstrobodyDataProcessor = new BlockItem(AdvancedRocketryBlocks.blockAstrobodyDataProcessor, typicalBlockProperties);
	public static Item itemLaunchpad = new BlockItem(AdvancedRocketryBlocks.blockLaunchpad, typicalBlockProperties);
	public static Item itemStructureTower = new BlockItem(AdvancedRocketryBlocks.blockStructureTower, typicalBlockProperties);
	public static Item itemRocketAssembler = new BlockItem(AdvancedRocketryBlocks.blockRocketAssembler, typicalBlockProperties);
	public static Item itemSeat = new BlockItem(AdvancedRocketryBlocks.blockSeat, typicalBlockProperties);
	public static Item itemMonopropellantEngine = new BlockItem(AdvancedRocketryBlocks.blockMonopropellantEngine, typicalBlockProperties);
	public static Item itemBipropellantEngine = new BlockItem(AdvancedRocketryBlocks.blockBipropellantEngine, typicalBlockProperties);
	public static Item itemAdvancedBipropellantEngine = new BlockItem(AdvancedRocketryBlocks.blockAdvancedBipropellantEngine, typicalBlockProperties);
	public static Item itemNuclearEngine = new BlockItem(AdvancedRocketryBlocks.blockNuclearEngine, typicalBlockProperties);
	public static Item itemNuclearCore = new BlockItem(AdvancedRocketryBlocks.blockNuclearCore, typicalBlockProperties);
	public static Item itemMonopropellantFuelTank = new BlockItem(AdvancedRocketryBlocks.blockMonopropellantFuelTank, typicalBlockProperties);
	public static Item itemBipropellantFuelTank = new BlockItem(AdvancedRocketryBlocks.blockBipropellantFuelTank, typicalBlockProperties);
	public static Item itemOxidizerFuelTank = new BlockItem(AdvancedRocketryBlocks.blockOxidizerFuelTank, typicalBlockProperties);
	public static Item itemNuclearWorkingFluidTank = new BlockItem(AdvancedRocketryBlocks.blockNuclearWorkingFluidTank, typicalBlockProperties);
	public static Item itemFuelingStation = new BlockItem(AdvancedRocketryBlocks.blockFuelingStation, typicalBlockProperties);
	public static Item itemRocketControlCenter = new BlockItem(AdvancedRocketryBlocks.blockRocketControlCenter, typicalBlockProperties);
	public static Item itemSatelliteAssembler = new BlockItem(AdvancedRocketryBlocks.blockSatelliteAssembler, typicalBlockProperties);
	public static Item itemSatelliteControlCenter = new BlockItem(AdvancedRocketryBlocks.blockSatelliteControlCenter, typicalBlockProperties);
	public static Item itemMoonTurf = new BlockItem(AdvancedRocketryBlocks.blockMoonTurf, typicalBlockProperties);
	public static Item itemOxidizedFerricSand = new BlockItem(AdvancedRocketryBlocks.blockOxidizedFerricSand, typicalBlockProperties);
	public static Item itemLightwoodLog = new BlockItem(AdvancedRocketryBlocks.blockLightwoodLog, typicalBlockProperties);
	public static Item itemLightwoodLeaves = new BlockItem(AdvancedRocketryBlocks.blockLightwoodLeaves, typicalBlockProperties);
	public static Item itemLightwoodSapling = new BlockItem(AdvancedRocketryBlocks.blockLightwoodSapling, typicalBlockProperties);
	public static Item itemGuidanceComputer = new BlockItem(AdvancedRocketryBlocks.blockGuidanceComputer, typicalBlockProperties);
	public static Item itemPlanetSelector = new BlockItem(AdvancedRocketryBlocks.blockPlanetSelector, typicalBlockProperties);
	public static Item itemSawbladeAssembly = new BlockItem(AdvancedRocketryBlocks.blockSawBlade, typicalBlockProperties);
	public static Item itemConcrete = new BlockItem(AdvancedRocketryBlocks.blockConcrete, typicalBlockProperties);
	public static Item itemRollingMachine = new BlockItem(AdvancedRocketryBlocks.blockRollingMachine, typicalBlockProperties);
	public static Item itemSmallPlatePress = new BlockItem(AdvancedRocketryBlocks.blockSmallPlatePress, typicalBlockProperties);
	public static Item itemSpaceStationAssembler = new BlockItem(AdvancedRocketryBlocks.blockSpaceStationAssembler, typicalBlockProperties);
	public static Item itemElectrolyzer = new BlockItem(AdvancedRocketryBlocks.blockElectrolyzer, typicalBlockProperties);
	public static Item itemChemicalReactor = new BlockItem(AdvancedRocketryBlocks.blockChemicalReactor, typicalBlockProperties);
	public static Item itemOxygenVent = new BlockItem(AdvancedRocketryBlocks.blockOxygenVent, typicalBlockProperties);
	public static Item itemOxygenScrubber = new BlockItem(AdvancedRocketryBlocks.blockCO2Scrubber, typicalBlockProperties);
	public static Item itemGasChargePad = new BlockItem(AdvancedRocketryBlocks.blockGasChargePad, typicalBlockProperties);
	public static Item itemLandingPad = new BlockItem(AdvancedRocketryBlocks.blockLandingPad, typicalBlockProperties);
	public static Item itemWarpCore = new BlockItem(AdvancedRocketryBlocks.blockWarpCore, typicalBlockProperties);
	public static Item itemWarpController = new BlockItem(AdvancedRocketryBlocks.blockWarpController, typicalBlockProperties);
	public static Item itemAtmosphereDetector = new BlockItem(AdvancedRocketryBlocks.blockAtmosphereDetector, typicalBlockProperties);
	public static Item itemUnlitTorch = new WallOrFloorItem(AdvancedRocketryBlocks.blockUnlitTorch, AdvancedRocketryBlocks.blockUnlitTorchWall, typicalBlockProperties);
	public static Item itemGeode = new BlockItem(AdvancedRocketryBlocks.blockGeode, typicalBlockProperties);
	public static Item itemVitrifiedSand = new BlockItem(AdvancedRocketryBlocks.blockVitrifiedSand, typicalBlockProperties);
	public static Item itemCharcoalLog = new BlockItem(AdvancedRocketryBlocks.blockCharcoalLog, typicalBlockProperties);
	public static Item itemElectricMushroom = new BlockItem(AdvancedRocketryBlocks.blockElectricMushroom, typicalBlockProperties);
	public static Item itemCrystal = new BlockItem(AdvancedRocketryBlocks.blockCrystal, typicalBlockProperties);
	public static Item itemCrystalRed = new BlockItem(AdvancedRocketryBlocks.blockCrystalRed, typicalBlockProperties);
	public static Item itemCrystalOrange = new BlockItem(AdvancedRocketryBlocks.blockCrystalOrange, typicalBlockProperties);
	public static Item itemCrystalYellow = new BlockItem(AdvancedRocketryBlocks.blockCrystalYellow, typicalBlockProperties);
	public static Item itemCrystalGreen = new BlockItem(AdvancedRocketryBlocks.blockCrystalGreen, typicalBlockProperties);
	public static Item itemCrystalCyan = new BlockItem(AdvancedRocketryBlocks.blockCrystalCyan, typicalBlockProperties);
	public static Item itemCrystalBlue = new BlockItem(AdvancedRocketryBlocks.blockCrystalBlue, typicalBlockProperties);
	public static Item itemCrystalPurple = new BlockItem(AdvancedRocketryBlocks.blockCrystalPurple, typicalBlockProperties);
	public static Item itemOrientationController = new BlockItem(AdvancedRocketryBlocks.blockOrientationController, typicalBlockProperties);
	public static Item itemGravityController = new BlockItem(AdvancedRocketryBlocks.blockGravityController, typicalBlockProperties);
	public static Item itemDrill = new BlockItem(AdvancedRocketryBlocks.blockDrill, typicalBlockProperties);
	public static Item itemMicrowaveReceiver = new BlockItem(AdvancedRocketryBlocks.blockMicrowaveReceiver, typicalBlockProperties);
	public static Item itemSuitWorkStation = new BlockItem(AdvancedRocketryBlocks.blockSuitWorkStation, typicalBlockProperties);
	public static Item itemDataBus = new BlockItem(AdvancedRocketryBlocks.blockDataBus, typicalBlockProperties);
	public static Item itemSatelliteBay = new BlockItem(AdvancedRocketryBlocks.blockSatelliteBay, typicalBlockProperties);
	public static Item itemFluidLoader = new BlockItem(AdvancedRocketryBlocks.blockFluidLoader, typicalBlockProperties);
	public static Item itemFluidUnloader = new BlockItem(AdvancedRocketryBlocks.blockFluidUnloader, typicalBlockProperties);
	public static Item itemRocketLoader = new BlockItem(AdvancedRocketryBlocks.blockRocketLoader, typicalBlockProperties);
	public static Item itemRocketUnloader = new BlockItem(AdvancedRocketryBlocks.blockRocketUnloader, typicalBlockProperties);
	public static Item itemGuidanceComputerAccessHatch = new BlockItem(AdvancedRocketryBlocks.blockGuidanceComputerAccessHatch, typicalBlockProperties);
	public static Item itemBiomeScanner = new BlockItem(AdvancedRocketryBlocks.blockBiomeScanner, typicalBlockProperties);
	public static Item itemTerraformer = new BlockItem(AdvancedRocketryBlocks.blockTerraformer, typicalBlockProperties);
	public static Item itemUnmannedRocketAssembler = new BlockItem(AdvancedRocketryBlocks.blockUnmannedRocketAssembler, typicalBlockProperties);
	public static Item itemPressureTank = new BlockItem(AdvancedRocketryBlocks.blockPressureTank, typicalBlockProperties);
	public static Item itemIntake = new BlockItem(AdvancedRocketryBlocks.blockIntake, typicalBlockProperties);
	public static Item itemStationLight = new BlockItem(AdvancedRocketryBlocks.blockStationLight, typicalBlockProperties);
	public static Item itemSolarGenerator = new BlockItem(AdvancedRocketryBlocks.blockSolarGenerator, typicalBlockProperties);
	public static Item itemStationDockingPort = new BlockItem(AdvancedRocketryBlocks.blockStationDockingPort, typicalBlockProperties);
	public static Item itemAltitudeController = new BlockItem(AdvancedRocketryBlocks.blockAltitudeController, typicalBlockProperties);
	public static Item itemRailgun = new BlockItem(AdvancedRocketryBlocks.blockRailgun, typicalBlockProperties);
	public static Item itemAdvancedMonopropellantEngine = new BlockItem(AdvancedRocketryBlocks.blockAdvancedMonopropellantEngine, typicalBlockProperties);
	public static Item itemHolographicPlanetSelector = new BlockItem(AdvancedRocketryBlocks.blockHolographicPlanetSelector, typicalBlockProperties);
	public static Item itemLensBlock = new BlockItem(AdvancedRocketryBlocks.blockLens, typicalBlockProperties);
	public static Item itemForceFieldProjector = new BlockItem(AdvancedRocketryBlocks.blockForceFieldProjector, typicalBlockProperties);
	public static Item itemAreaGravityController = new BlockItem(AdvancedRocketryBlocks.blockAreaGravityController, typicalBlockProperties);
	public static Item itemSeal = new BlockItem(AdvancedRocketryBlocks.blockSeal, typicalBlockProperties);
	public static Item itemSpaceElevator = new BlockItem(AdvancedRocketryBlocks.blockSpaceElevator, typicalBlockProperties);
	public static Item itemBeacon = new BlockItem(AdvancedRocketryBlocks.blockBeacon, typicalBlockProperties);
	public static Item itemLightwoodPlanks = new BlockItem(AdvancedRocketryBlocks.blockLightwoodPlanks, typicalBlockProperties);
	public static Item itemThermiteTorch = new WallOrFloorItem(AdvancedRocketryBlocks.blockThermiteTorch, AdvancedRocketryBlocks.blockThermiteTorchWall, typicalBlockProperties);
	public static Item itemWirelessTransceiver = new BlockItem(AdvancedRocketryBlocks.blockWirelessTransceiver, typicalBlockProperties);
	public static Item itemLaser = new BlockItem(AdvancedRocketryBlocks.blockLaser, typicalBlockProperties);
	public static Item itemMoonTurfDark = new BlockItem(AdvancedRocketryBlocks.blockMoonTurfDark, typicalBlockProperties);
	public static Item itemBlackHoleGenerator = new BlockItem(AdvancedRocketryBlocks.blockBlackHoleGenerator, typicalBlockProperties);
	public static Item itemPump = new BlockItem(AdvancedRocketryBlocks.blockPump, typicalBlockProperties);
	public static Item itemCentrifuge = new BlockItem(AdvancedRocketryBlocks.blockCentrifuge, typicalBlockProperties);
	public static Item itemBasalt = new BlockItem(AdvancedRocketryBlocks.blockBasalt, typicalBlockProperties);
	public static Item itemLandingFloat = new BlockItem(AdvancedRocketryBlocks.blockLandingFloat, typicalBlockProperties);
	public static Item itemSolarArray = new BlockItem(AdvancedRocketryBlocks.blockSolarArray, typicalBlockProperties);
	public static Item itemSolarArrayPanel = new BlockItem(AdvancedRocketryBlocks.blockSolarArrayPanel, typicalBlockProperties);
	public static Item itemQuartzCrucible = new BlockItem(AdvancedRocketryBlocks.blockQuartzCrucible, typicalProperties);

	public static void registerItems(Register<Item> evt) {

		//Item Registration
		evt.getRegistry().registerAll(
				//Misc components
				AdvancedRocketryItems.itemUserInterface,
				AdvancedRocketryItems.itemCarbonBrick,
				AdvancedRocketryItems.itemSawBlade,
				AdvancedRocketryItems.itemCarbonScrubberCartridge,
		        //Circuit components
		        AdvancedRocketryItems.itemSiliconWafer,
	        	AdvancedRocketryItems.itemBasicCircuitWafer,
				AdvancedRocketryItems.itemAdvancedCircuitWafer,
				AdvancedRocketryItems.itemBasicCircuit,
		        AdvancedRocketryItems.itemAdvancedCircuit,
	        	AdvancedRocketryItems.itemTrackingCircuit,
	        	AdvancedRocketryItems.itemControlCircuit,
	        	AdvancedRocketryItems.itemItemIOCircuit,
		        AdvancedRocketryItems.itemFluidIOCircuit,
		        //Chips!
				AdvancedRocketryItems.itemSpaceStationChip,
				AdvancedRocketryItems.itemAsteroidChip,
	         	AdvancedRocketryItems.itemSatelliteChip,
				AdvancedRocketryItems.itemPlanetChip,
	         	AdvancedRocketryItems.itemSpaceStationContainer,
	        	//Satellite stuff
		        AdvancedRocketryItems.itemSolarPanel,
				AdvancedRocketryItems.itemLargeSolarPanel,
				AdvancedRocketryItems.itemOpticalSensor,
				AdvancedRocketryItems.itemCompositionSensor,
				AdvancedRocketryItems.itemMassSensor,
				AdvancedRocketryItems.itemMicrowaveTransmitter,
				AdvancedRocketryItems.itemOreSensor,
				AdvancedRocketryItems.itemDataUnit,
				AdvancedRocketryItems.itemSatellite,
		        //Suit Component Registration
				AdvancedRocketryItems.itemJetpack,
				AdvancedRocketryItems.itemAluminumPressureTank,
				AdvancedRocketryItems.itemSteelPressureTank,
				AdvancedRocketryItems.itemTitaniumPressureTank,
				AdvancedRocketryItems.itemHoverUpgrade,
				AdvancedRocketryItems.itemFlightSpeedUpgrade,
				AdvancedRocketryItems.itemBionicLegsUpgrade,
				AdvancedRocketryItems.itemPaddedBootsUpgrade,
				AdvancedRocketryItems.itemAntiFogVisorUpgrade,
				AdvancedRocketryItems.itemBeaconFinderUpgrade,
		        //Armor registration
		        AdvancedRocketryItems.itemSpaceSuitHelmet,
				AdvancedRocketryItems.itemSpaceSuitChestpiece,
				AdvancedRocketryItems.itemSpaceSuitLeggings,
				AdvancedRocketryItems.itemSpaceSuitBoots,
		       //Tools
				AdvancedRocketryItems.itemSealDetector,
				AdvancedRocketryItems.itemAtmosphereAnalyzer,
		        AdvancedRocketryItems.itemOreScanner,
		        AdvancedRocketryItems.itemJackhammer,
				AdvancedRocketryItems.itemHovercraft,
		        //AdvancedRocketryItems.itemBasicLaserGun,
		        //Weird block-items
		        AdvancedRocketryItems.itemSmallAirlockDoor
				);
		
		evt.getRegistry().registerAll(
				//Lights
				AdvancedRocketryItems.itemUnlitTorch.setRegistryName("unlittorch"),
				AdvancedRocketryItems.itemThermiteTorch.setRegistryName("thermitetorch"),
				AdvancedRocketryItems.itemStationLight.setRegistryName("stationlight"),
				//World generation blocks & plants
				AdvancedRocketryItems.itemCharcoalLog.setRegistryName("charcoallog"),
				AdvancedRocketryItems.itemLightwoodLog.setRegistryName("lightwoodlog"),
				AdvancedRocketryItems.itemLightwoodPlanks.setRegistryName("lightwoodplanks"),
				AdvancedRocketryItems.itemLightwoodLeaves.setRegistryName("lightwoodleaves"),
				AdvancedRocketryItems.itemLightwoodSapling.setRegistryName("lightwoodsapling"),
				AdvancedRocketryItems.itemElectricMushroom.setRegistryName("electricmushroom"),
				AdvancedRocketryItems.itemVitrifiedSand.setRegistryName("vitrifiedsand"),
				AdvancedRocketryItems.itemMoonTurf.setRegistryName("moonturf"),
				AdvancedRocketryItems.itemMoonTurfDark.setRegistryName("moonturf_dark"),
				AdvancedRocketryItems.itemOxidizedFerricSand.setRegistryName("oxidizedferricsand"),
				AdvancedRocketryItems.itemBasalt.setRegistryName("basalt"),
				AdvancedRocketryItems.itemGeode.setRegistryName("geode"),
				AdvancedRocketryItems.itemCrystal.setRegistryName("crystal"),
				AdvancedRocketryItems.itemCrystalRed.setRegistryName("crystal_red"),
				AdvancedRocketryItems.itemCrystalOrange.setRegistryName("crystal_orange"),
				AdvancedRocketryItems.itemCrystalYellow.setRegistryName("crystal_yellow"),
				AdvancedRocketryItems.itemCrystalGreen.setRegistryName("crystal_green"),
				AdvancedRocketryItems.itemCrystalCyan.setRegistryName("crystal_cyan"),
				AdvancedRocketryItems.itemCrystalBlue.setRegistryName("crystal_blue"),
				AdvancedRocketryItems.itemCrystalPurple.setRegistryName("crystal_purple"),
				//Rocket blocks
		        AdvancedRocketryItems.itemMonopropellantEngine.setRegistryName("monopropellantrocketengine"),
				AdvancedRocketryItems.itemAdvancedMonopropellantEngine.setRegistryName("advancedmonopropellantrocketengine"),
				AdvancedRocketryItems.itemBipropellantEngine.setRegistryName("bipropellantrocketengine"),
				AdvancedRocketryItems.itemAdvancedBipropellantEngine.setRegistryName("advancedbipropellantrocketengine"),
				AdvancedRocketryItems.itemNuclearEngine.setRegistryName("nuclearrocketengine"),
				AdvancedRocketryItems.itemMonopropellantFuelTank.setRegistryName("monopropellantfueltank"),
				AdvancedRocketryItems.itemBipropellantFuelTank.setRegistryName("bipropellantfueltank"),
				AdvancedRocketryItems.itemOxidizerFuelTank.setRegistryName("oxidizerfueltank"),
				AdvancedRocketryItems.itemNuclearWorkingFluidTank.setRegistryName("nuclearworkingfluidtank"),
				AdvancedRocketryItems.itemNuclearCore.setRegistryName("nuclearcore"),
				//Rocket auxiliary
				AdvancedRocketryItems.itemSeat.setRegistryName("seat"),
				AdvancedRocketryItems.itemSatelliteBay.setRegistryName("satellitebay"),
				AdvancedRocketryItems.itemGuidanceComputer.setRegistryName("guidancecomputer"),
				AdvancedRocketryItems.itemDrill.setRegistryName("drill"),
				AdvancedRocketryItems.itemIntake.setRegistryName("intake"),
				AdvancedRocketryItems.itemLandingFloat.setRegistryName("landingfloat"),
				//Rocket interaction
				AdvancedRocketryItems.itemFuelingStation.setRegistryName("fuelingstation"),
				AdvancedRocketryItems.itemRocketControlCenter.setRegistryName("rocketcontrolcenter"),
				AdvancedRocketryItems.itemSatelliteControlCenter.setRegistryName("satellitecontrolcenter"),
				AdvancedRocketryItems.itemRocketLoader.setRegistryName("rocketloader"),
				AdvancedRocketryItems.itemRocketUnloader.setRegistryName("rocketunloader"),
				AdvancedRocketryItems.itemFluidLoader.setRegistryName("rocketfluidloader"),
				AdvancedRocketryItems.itemFluidUnloader.setRegistryName("rocketfluidunloader"),
				AdvancedRocketryItems.itemGuidanceComputerAccessHatch.setRegistryName("guidancecomputeraccesshatch"),
				//Rocket pad blocks
				AdvancedRocketryItems.itemLaunchpad.setRegistryName("launchpad"),
				AdvancedRocketryItems.itemLandingPad.setRegistryName("landingpad"),
				AdvancedRocketryItems.itemStructureTower.setRegistryName("structuretower"),
				AdvancedRocketryItems.itemRocketAssembler.setRegistryName("rocketassembler"),
				AdvancedRocketryItems.itemSpaceStationAssembler.setRegistryName("spacestationassembler"),
				AdvancedRocketryItems.itemUnmannedRocketAssembler.setRegistryName("unmannedrocketassembler"),
				//Station interaction blocks
				AdvancedRocketryItems.itemHolographicPlanetSelector.setRegistryName("holographicplanetselector"),
				AdvancedRocketryItems.itemPlanetSelector.setRegistryName("planetselector"),
				AdvancedRocketryItems.itemOrientationController.setRegistryName("orientationcontroller"),
				AdvancedRocketryItems.itemGravityController.setRegistryName("gravitycontroller"),
				AdvancedRocketryItems.itemAltitudeController.setRegistryName("altitudecontroller"),
				AdvancedRocketryItems.itemWarpController.setRegistryName("warpcontroller"),
				AdvancedRocketryItems.itemStationDockingPort.setRegistryName("stationdockingport"),
				//Oxygen system blocks
				AdvancedRocketryItems.itemAtmosphereDetector.setRegistryName("atmospheredetector"),
				AdvancedRocketryItems.itemOxygenVent.setRegistryName("oxygenvent"),
				AdvancedRocketryItems.itemOxygenScrubber.setRegistryName("oxygenscrubber"),
				AdvancedRocketryItems.itemGasChargePad.setRegistryName("gaschargepad"),

				AdvancedRocketryItems.itemSawbladeAssembly.setRegistryName("sawbladeassembly"),
				AdvancedRocketryItems.itemQuartzCrucible.setRegistryName("quartzcrucible"),
				AdvancedRocketryItems.itemLensBlock.setRegistryName("lens"),
				AdvancedRocketryItems.itemConcrete.setRegistryName("concrete"),
				AdvancedRocketryItems.itemBlastBrick.setRegistryName("blastbrick"),
				AdvancedRocketryItems.itemSeal.setRegistryName("seal"),
				AdvancedRocketryItems.itemLaser.setRegistryName("laser"),
				AdvancedRocketryItems.itemForceFieldProjector.setRegistryName("forcefieldprojector"),
				//Misc non-multiblock machines
				AdvancedRocketryItems.itemSuitWorkStation.setRegistryName("suitworkstation"),
				AdvancedRocketryItems.itemPressureTank.setRegistryName("pressuretank"),
				AdvancedRocketryItems.itemPump.setRegistryName("pump"),
				AdvancedRocketryItems.itemSmallPlatePress.setRegistryName("smallplatepress"),
				//MULTIBLOCK MACHINES
				//Item processors
				AdvancedRocketryItems.itemArcFurnace.setRegistryName("arcfurnace"),
				AdvancedRocketryItems.itemRollingMachine.setRegistryName("rollingmachine"),
				AdvancedRocketryItems.itemLathe.setRegistryName("lathe"),
				AdvancedRocketryItems.itemCrystallizer.setRegistryName("crystallizer"),
				AdvancedRocketryItems.itemCuttingMachine.setRegistryName("cuttingmachine"),
				AdvancedRocketryItems.itemPrecisionAssembler.setRegistryName("precisionassembler"),
				AdvancedRocketryItems.itemPrecisionLaserEtcher.setRegistryName("precisionlaseretcher"),
				//Fluid processors
				AdvancedRocketryItems.itemElectrolyzer.setRegistryName("electrolyzer"),
				AdvancedRocketryItems.itemChemicalReactor.setRegistryName("chemicalreactor"),
				AdvancedRocketryItems.itemCentrifuge.setRegistryName("centrifuge"),
				//Data collection
				AdvancedRocketryItems.itemSatelliteAssembler.setRegistryName("satelliteassembler"),
				AdvancedRocketryItems.itemWirelessTransceiver.setRegistryName("wirelesstransceiver"),
				AdvancedRocketryItems.itemDataBus.setRegistryName("databus"),
				AdvancedRocketryItems.itemObservatory.setRegistryName("observatory"),
				AdvancedRocketryItems.itemAstrobodyDataProcessor.setRegistryName("astrobodydataprocessor"),
				//Energy production
				AdvancedRocketryItems.itemSolarGenerator.setRegistryName("solargenerator"),
				AdvancedRocketryItems.itemSolarArrayPanel.setRegistryName("solararraypanel"),
				AdvancedRocketryItems.itemSolarArray.setRegistryName("solararray"),
				AdvancedRocketryItems.itemMicrowaveReceiver.setRegistryName("microwavereceiver"),
				AdvancedRocketryItems.itemBlackHoleGenerator.setRegistryName("blackholegenerator"),
				//Station multiblocks
				AdvancedRocketryItems.itemBiomeScanner.setRegistryName("biomescanner"),
				AdvancedRocketryItems.itemRailgun .setRegistryName("railgun"),
				AdvancedRocketryItems.itemSpaceElevator.setRegistryName("spaceelevator"),
				AdvancedRocketryItems.itemBeacon.setRegistryName("beacon"),
				//Near-future or far-future multiblocks
				AdvancedRocketryItems.itemOrbitalLaserDrill.setRegistryName("orbitallaserdrill"),
				AdvancedRocketryItems.itemWarpCore.setRegistryName("warpcore"),
				AdvancedRocketryItems.itemAreaGravityController.setRegistryName("areagravitycontroller"),
				AdvancedRocketryItems.itemTerraformer.setRegistryName("terraformer")
		);
	}
}
