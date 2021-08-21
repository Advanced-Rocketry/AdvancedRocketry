package zmaster587.advancedRocketry.api;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.RegistryObject;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.armor.ItemSpaceChest;
import zmaster587.advancedRocketry.item.ItemARBucket;
import zmaster587.advancedRocketry.item.ItemAsteroidChip;
import zmaster587.advancedRocketry.item.ItemAstroBed;
import zmaster587.advancedRocketry.item.ItemAtmosphereAnalzer;
import zmaster587.advancedRocketry.item.ItemBeaconFinder;
import zmaster587.advancedRocketry.item.ItemBiomeChanger;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.item.ItemHovercraft;
import zmaster587.advancedRocketry.item.ItemJackHammer;
import zmaster587.advancedRocketry.item.ItemOreScanner;
import zmaster587.advancedRocketry.item.ItemPackedStructure;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.item.ItemSealDetector;
import zmaster587.advancedRocketry.item.ItemSpaceElevatorChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.item.components.ItemJetpack;
import zmaster587.advancedRocketry.item.components.ItemPressureTank;
import zmaster587.advancedRocketry.item.components.ItemUpgrade;
import zmaster587.advancedRocketry.item.tools.ItemBasicLaserGun;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
/**
 * Stores references to Advanced Rocketry's items
 *
 */
public class AdvancedRocketryItems {

	//TODO: fix
	public static final ArmorMaterial spaceSuit = new ArmorMaterial("spaceSuit", net.minecraft.item.ArmorMaterial.DIAMOND.getDurability(EquipmentSlotType.CHEST), new int[] {1,1,1,1}, 0, new SoundEvent(new ResourceLocation("")), 0, 0, null);

	public static Item itemSiliconWafer;
	public static Item itemAnthracene;
	public static Item itemBasicCircuitPlate;
	public static Item itemAdvCircuitPlate;
	public static Item itemICBasic;
	public static Item itemICTracking;
	public static Item itemICAdv;
	public static Item itemICControl;
	public static Item itemICItemIO;
	public static Item itemICFluidIO;
	public static Item itemSatellitePowerSourceBasic;
	public static Item itemSatellitePowerSourceAdvanced;
	public static Item itemSensorOptical;
	public static Item itemSensorComposition;
	public static Item itemSensorMass;
	public static Item itemSensorMicrowave;
	public static Item itemSensorOreMapper;
	public static Item itemSensorBiomeChanger;
	public static Item itemOreScanner;
	public static Item itemQuartzCrucible;
	public static Item itemDataUnit;
	public static Item itemSatellite;
	public static Item itemSatelliteIdChip;
	public static Item itemPlanetIdChip;
	public static Item itemUi;
	public static Item itemCarbonBrick;
	public static Item itemSawBlade;
	public static Item itemSpaceStationChip;
	public static Item itemSpaceStation;
	public static Item itemSpaceSuit_Helmet;
	public static Item itemSpaceSuit_Chest;
	public static Item itemSpaceSuit_Leggings;
	public static Item itemSpaceSuit_Boots;
	public static Item itemSmallAirlockDoor;
	public static Item itemCarbonScrubberCartridge;
	public static Item itemSealDetector;
	public static Item itemJackhammer;
	public static Item itemAsteroidChip;
	public static Item itemLens;
	public static Item itemJetpack;
	public static Item itemPressureTankLow;
	public static Item itemPressureTankMed;
	public static Item itemPressureTankHigh;
	public static Item itemPressureTankSuperHigh;
	public static Item itemUpgradeHover;
	public static Item itemUpgradeFlightSpeed;
	public static Item itemUpgradeLegs;
	public static Item itemUpgradeFallBoots;
	public static Item itemUpgradeFogGoggles;
	public static Item itemAtmAnalyser;
	public static Item itemBiomeChanger;
	public static Item itemBasicLaserGun;
	public static Item itemSpaceElevatorChip;
	public static Item itemBeaconFinder;
	public static Item itemThermite;
	public static Item itemHovercraft;

	public static Item[] itemUpgrades;
	
	public static RegistryObject<Item> itemBucketRocketFuel;
	public static RegistryObject<Item> itemBucketNitrogen;
	public static RegistryObject<Item> itemBucketHydrogen;
	public static RegistryObject<Item> itemBucketOxygen;
	public static RegistryObject<Item> itemBucketEnrichedLava;
	
	
	// block items
	public static Item itemMissionComp;
	public static Item itemSpaceLaser;
	public static Item itemPrecisionAssembler;
	public static Item itemArcFurnace;
	public static Item itemPrecisionLaserEtcher;
	public static Item itemBlastBrick;
	public static Item itemCrystallizer;
	public static Item itemLathe;
	public static Item itemCuttingMachine;
	public static Item itemObservatory;
	public static Item itemPlanetAnalyser;
	public static Item itemLaunchpad;
	public static Item itemStructureTower;
	public static Item itemRocketBuilder;
	public static Item itemGenericSeat;
	public static Item itemEngine;
	public static Item itemBiPropellantEngine;
	public static Item itemFuelTank;
	public static Item itemBiPropellantFuelTank;
	public static Item itemFuelingStation;
	public static Item itemMonitoringStation;
	public static Item itemSatelliteBuilder;
	public static Item itemSatelliteControlCenter;
	public static Item itemChipStorage;
	public static Item itemMoonTurf;
	public static Item itemHotTurf;
	public static Item itemMultiMineOre;
	public static Item itemLightSource;
	public static Item itemAlienWood;
	public static Item itemAlienLeaves;
	public static Item itemAlienSapling;
	public static Item itemGuidanceComputer;
	public static Item itemLunarAnalyser;
	public static Item itemPlanetSelector;
	public static Item itemSawBladeBlock;
	public static Item itemConcrete;
	public static Item itemRollingMachine;
	public static Item itemPlatePress;
	public static Item itemStationBuilder;
	public static Item itemElectrolyser;
	public static Item itemChemicalReactor;
	public static Item itemOxygenVent;
	public static Item itemOxygenScrubber;
	public static Item itemOxygenCharger;
	public static Item itemAirLock;
	public static Item itemLandingPad;
	public static Item itemWarpCore;
	public static Item itemWarpShipMonitor;
	public static Item itemOxygenDetection;
	public static Item itemUnlitTorch;
	public static Item itemsGeode;
	public static Item itemVitrifiedSand;
	public static Item itemCharcoalLog;
	public static Item itemElectricMushroom;
	public static Item itemCrystal;
	public static Item itemCrystalRed;
	public static Item itemCrystalOrange;
	public static Item itemCrystalYellow;
	public static Item itemCrystalGreen;
	public static Item itemCrystalCyan;
	public static Item itemCrystalBlue;
	public static Item itemCrystalPurple;
	public static Item itemOrientationController;
	public static Item itemGravityController;
	public static Item itemDrill;
	public static Item itemFluidPipe;
	public static Item itemDataPipe;
	public static Item itemMicrowaveReciever;
	public static Item itemSolarPanel;
	public static Item itemSuitWorkStation;
	public static Item itemSatelliteHatch;
	public static Item itemDataBus;
	public static Item itemFluidLoader;
	public static Item itemFluidUnloader;
	public static Item itemRocketLoader;
	public static Item itemRocketUnloader;
	public static Item itemguidanceHatch;
	public static Item itemBiomeScanner;
	public static Item itemAtmosphereTerraformer;
	public static Item itemDeployableRocketBuilder;
	public static Item itemPressureTank;
	public static Item itemIntake;
	public static Item itemCircleLight;
	public static Item itemEnergyPipe;
	public static Item itemSolarGenerator;
	public static Item itemDockingPort;
	public static Item itemAltitudeController;
	public static Item itemRailgun;
	public static Item itemAdvEngine;
	public static Item itemPlanetHoloSelector;
	public static Item itemLensBlock;
	public static Item itemForceField;
	public static Item itemForceFieldProjector;
	public static Item itemGravityMachine;
	public static Item itemPipeSealer;
	public static Item itemSpaceElevatorController;
	public static Item itemBeacon;
	public static Item itemAlienPlanks;
	public static Item itemThermiteTorch;
	public static Item itemTransciever;
	public static Item itemVacuumLaser;
	public static Item itemMoonTurfDark;
	public static Item itemBlackHoleGenerator;
	public static Item itemPump;
	public static Item itemCentrifuge;
	public static Item itemBasalt;
	public static Item itemLandingFloat;

	public static void registerItems(Register<Item> evt) {
		//Items -------------------------------------------------------------------------------------
		Item.Properties typicalProperties = new Item.Properties().group(AdvancedRocketry.tabAdvRocketry);
		Item.Properties singleStackSize = new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(1);

		AdvancedRocketryItems.itemSiliconWafer = new Item(typicalProperties).setRegistryName("wafer");
		AdvancedRocketryItems.itemBasicCircuitPlate = new Item(typicalProperties).setRegistryName("basiccircuitplate");
		AdvancedRocketryItems.itemAdvCircuitPlate = new Item(typicalProperties).setRegistryName("advcircuitplate");
		AdvancedRocketryItems.itemICBasic = new Item(typicalProperties).setRegistryName("circuit_ic_basic");
		AdvancedRocketryItems.itemICTracking = new Item(typicalProperties).setRegistryName("circuit_ic_tracking");
		AdvancedRocketryItems.itemICAdv = new Item(typicalProperties).setRegistryName("circuit_ic_adv");
		AdvancedRocketryItems.itemICControl = new Item(typicalProperties).setRegistryName("circuit_ic_control");
		AdvancedRocketryItems.itemICItemIO = new Item(typicalProperties).setRegistryName("circuit_ic_item_io");
		AdvancedRocketryItems.itemICFluidIO = new Item(typicalProperties).setRegistryName("circuit_ic_fluid_io");
		AdvancedRocketryItems.itemUi = new Item(typicalProperties).setRegistryName("item_ui");
		AdvancedRocketryItems.itemCarbonBrick = new Item(typicalProperties).setRegistryName("carbon_brick");
		AdvancedRocketryItems.itemSawBlade = new Item(typicalProperties).setRegistryName("sawbladeiron");
		AdvancedRocketryItems.itemSpaceStationChip = new ItemStationChip(singleStackSize).setRegistryName("chip_station");
		AdvancedRocketryItems.itemSpaceElevatorChip = new ItemSpaceElevatorChip(singleStackSize).setRegistryName("chip_elevator");
		AdvancedRocketryItems.itemAsteroidChip = new ItemAsteroidChip(singleStackSize).setRegistryName("chip_asteroid");
		AdvancedRocketryItems.itemSpaceStation = new ItemPackedStructure(singleStackSize).setRegistryName("spacestationcontainer");
		AdvancedRocketryItems.itemSmallAirlockDoor = new TallBlockItem(AdvancedRocketryBlocks.blockAirLock, singleStackSize).setRegistryName("smallairlock");
		//Short.MAX_VALUE is forge's wildcard, don't use it
		AdvancedRocketryItems.itemCarbonScrubberCartridge = new Item(new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).defaultMaxDamage(Short.MAX_VALUE-1)).setRegistryName("carbon_scrubber_cartridge");
		AdvancedRocketryItems.itemLens = new Item(typicalProperties).setRegistryName("basiclens");
		AdvancedRocketryItems.itemSatellitePowerSourceBasic = new Item(typicalProperties).setRegistryName("satellite_power_source_basic");
		AdvancedRocketryItems.itemSatellitePowerSourceAdvanced = new Item(typicalProperties).setRegistryName("satellite_power_source_adv");
		AdvancedRocketryItems.itemSensorOptical = new Item(typicalProperties).setRegistryName("sensor_optical");
		AdvancedRocketryItems.itemSensorComposition = new Item(typicalProperties).setRegistryName("sensor_composition");
		AdvancedRocketryItems.itemSensorMass = new Item(typicalProperties).setRegistryName("sensor_mass");
		AdvancedRocketryItems.itemSensorMicrowave = new Item(typicalProperties).setRegistryName("sensor_microwave");
		AdvancedRocketryItems.itemSensorOreMapper = new Item(typicalProperties).setRegistryName("sensor_oremapper");
		AdvancedRocketryItems.itemSensorBiomeChanger = new Item(typicalProperties).setRegistryName("sensor_biomechanger");
		AdvancedRocketryItems.itemThermite = new Item(typicalProperties).setRegistryName("thermite");

		//TODO: move registration in the case we have more than one chip type
		AdvancedRocketryItems.itemDataUnit = new ItemData(singleStackSize).setRegistryName("dataunit");
		AdvancedRocketryItems.itemOreScanner = new ItemOreScanner(singleStackSize).setRegistryName("ore_scanner");
		AdvancedRocketryItems.itemQuartzCrucible = new BlockItem(AdvancedRocketryBlocks.blockQuartzCrucible, typicalProperties).setRegistryName("qcrucible");
		AdvancedRocketryItems.itemSatellite = new ItemSatellite(singleStackSize).setRegistryName("satellite");
		AdvancedRocketryItems.itemSatelliteIdChip = new ItemSatelliteIdentificationChip(typicalProperties).setRegistryName("satellite_id_chip");
		AdvancedRocketryItems.itemPlanetIdChip = new ItemPlanetIdentificationChip(typicalProperties).setRegistryName("planet_id_chip");
		AdvancedRocketryItems.itemBiomeChanger = new ItemBiomeChanger(typicalProperties).setRegistryName("biome_changer");
		AdvancedRocketryItems.itemBasicLaserGun = new ItemBasicLaserGun(typicalProperties).setRegistryName("basic_laser_gun");
		AdvancedRocketryItems.itemHovercraft = new ItemHovercraft(singleStackSize).setRegistryName("hovercraft");

		//Suit Component Registration
		AdvancedRocketryItems.itemJetpack = new ItemJetpack(singleStackSize).setRegistryName("jetpack");
		AdvancedRocketryItems.itemPressureTankLow = new ItemPressureTank(new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(4), 1000).setRegistryName("pressure_tank_low");
		AdvancedRocketryItems.itemPressureTankMed = new ItemPressureTank(new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(4), 2000).setRegistryName("pressure_tank_med");
		AdvancedRocketryItems.itemPressureTankHigh = new ItemPressureTank(new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(4), 4000).setRegistryName("pressure_tank_high");
		AdvancedRocketryItems.itemPressureTankSuperHigh = new ItemPressureTank(new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(4), 16000).setRegistryName("pressure_tank_superhigh");
		AdvancedRocketryItems.itemUpgradeHover = new ItemUpgrade(singleStackSize).setRegistryName("upgrade_hover");
		AdvancedRocketryItems.itemUpgradeFlightSpeed = new ItemUpgrade(singleStackSize).setRegistryName("upgrade_flight_speed");
		AdvancedRocketryItems.itemUpgradeLegs = new ItemUpgrade(singleStackSize).setRegistryName("upgrade_legs");
		AdvancedRocketryItems.itemUpgradeFallBoots = new ItemUpgrade(singleStackSize).setRegistryName("upgrade_boots");
		AdvancedRocketryItems.itemUpgradeFogGoggles = new ItemUpgrade(singleStackSize).setRegistryName("upgrade_foggles");
		AdvancedRocketryItems.itemAtmAnalyser = new ItemAtmosphereAnalzer(singleStackSize).setRegistryName("atm_analyser");
		AdvancedRocketryItems.itemBeaconFinder = new ItemBeaconFinder(singleStackSize).setRegistryName("beacon_finder");
		AdvancedRocketryItems.itemUpgrades = new Item[] { itemUpgradeHover, itemUpgradeFlightSpeed, itemUpgradeLegs, itemUpgradeFallBoots, itemUpgradeFogGoggles, itemAtmAnalyser, itemBeaconFinder}; 
		
		
		//Armor registration

		AdvancedRocketryItems.itemSpaceSuit_Helmet = new ItemSpaceArmor(singleStackSize, net.minecraft.item.ArmorMaterial.LEATHER, EquipmentSlotType.HEAD,4).setRegistryName("spacehelmet");
		AdvancedRocketryItems.itemSpaceSuit_Chest = new ItemSpaceChest(singleStackSize, net.minecraft.item.ArmorMaterial.LEATHER, EquipmentSlotType.CHEST,6).setRegistryName("spacechest");
		AdvancedRocketryItems.itemSpaceSuit_Leggings = new ItemSpaceArmor(singleStackSize, net.minecraft.item.ArmorMaterial.LEATHER, EquipmentSlotType.LEGS,4).setRegistryName("spaceleggings");
		AdvancedRocketryItems.itemSpaceSuit_Boots = new ItemSpaceArmor(singleStackSize, net.minecraft.item.ArmorMaterial.LEATHER, EquipmentSlotType.FEET,4).setRegistryName("spaceboots");
		AdvancedRocketryItems.itemSealDetector = new ItemSealDetector(singleStackSize).setRegistryName("sealdetector");

		//Tools
		AdvancedRocketryItems.itemJackhammer = new ItemJackHammer(ItemTier.DIAMOND, new Item.Properties().group(AdvancedRocketry.tabAdvRocketry).maxStackSize(1).maxDamage(1500)).setRegistryName("jackhammer");

		//Note: not registered
		//AdvancedRocketryItems.itemAstroBed = new ItemAstroBed(AdvancedRocketryBlocks.blockAstroBed, singleStackSize).setRegistryName("astrobed");

		//Item Registration
		evt.getRegistry().registerAll(
				AdvancedRocketryItems.itemQuartzCrucible,
				AdvancedRocketryItems.itemOreScanner,
				AdvancedRocketryItems.itemSatellitePowerSourceBasic,
				AdvancedRocketryItems.itemSatellitePowerSourceAdvanced,
				AdvancedRocketryItems.itemSensorOptical,
				AdvancedRocketryItems.itemSensorComposition,
				AdvancedRocketryItems.itemSensorMass,
				AdvancedRocketryItems.itemSensorMicrowave,
				AdvancedRocketryItems.itemSensorOreMapper,
				AdvancedRocketryItems.itemBasicCircuitPlate,
				AdvancedRocketryItems.itemAdvCircuitPlate,
				AdvancedRocketryItems.itemICBasic,
				AdvancedRocketryItems.itemICAdv,
				AdvancedRocketryItems.itemICTracking,
				AdvancedRocketryItems.itemICControl,
				AdvancedRocketryItems.itemICItemIO,
				AdvancedRocketryItems.itemICFluidIO,
				AdvancedRocketryItems.itemSiliconWafer,
				AdvancedRocketryItems.itemDataUnit,
				AdvancedRocketryItems.itemSatellite,
				AdvancedRocketryItems.itemSatelliteIdChip,
				AdvancedRocketryItems.itemPlanetIdChip,
				AdvancedRocketryItems.itemUi,
				AdvancedRocketryItems.itemCarbonBrick,
				AdvancedRocketryItems.itemSawBlade,
				AdvancedRocketryItems.itemSpaceStationChip,
				AdvancedRocketryItems.itemSpaceStation,
				AdvancedRocketryItems.itemSpaceSuit_Helmet,
				AdvancedRocketryItems.itemSpaceSuit_Boots,
				AdvancedRocketryItems.itemSpaceSuit_Chest,
				AdvancedRocketryItems.itemSpaceSuit_Leggings,
				//AdvancedRocketryItems.itemBucketRocketFuel,
				//AdvancedRocketryItems.itemBucketNitrogen,
				//AdvancedRocketryItems.itemBucketHydrogen,
				//AdvancedRocketryItems.itemBucketOxygen,
				//AdvancedRocketryItems.itemBucketEnrichedLava,
				AdvancedRocketryItems.itemSmallAirlockDoor,
				AdvancedRocketryItems.itemCarbonScrubberCartridge,
				AdvancedRocketryItems.itemSealDetector,
				AdvancedRocketryItems.itemJackhammer,
				AdvancedRocketryItems.itemAsteroidChip,
				AdvancedRocketryItems.itemSpaceElevatorChip,
				AdvancedRocketryItems.itemLens,
				AdvancedRocketryItems.itemJetpack,
				AdvancedRocketryItems.itemPressureTankLow,
				AdvancedRocketryItems.itemPressureTankMed,
				AdvancedRocketryItems.itemPressureTankHigh,
				AdvancedRocketryItems.itemPressureTankSuperHigh,
				AdvancedRocketryItems.itemUpgradeFallBoots,
				AdvancedRocketryItems.itemUpgradeFlightSpeed,
				AdvancedRocketryItems.itemUpgradeFogGoggles,
				AdvancedRocketryItems.itemUpgradeHover,
				AdvancedRocketryItems.itemUpgradeLegs,
				AdvancedRocketryItems.itemAtmAnalyser,
				AdvancedRocketryItems.itemBasicLaserGun,
				AdvancedRocketryItems.itemBeaconFinder,
				AdvancedRocketryItems.itemThermite,
				AdvancedRocketryItems.itemHovercraft,
				AdvancedRocketryItems.itemBiomeChanger,
				AdvancedRocketryItems.itemSensorBiomeChanger);
		
		// register blocks
		Item.Properties typicalBlockProperties = new Item.Properties().group(AdvancedRocketry.tabAdvRocketry);
		
		//AdvancedRocketryItems.itemMissionComp = new BlockItem(AdvancedRocketryBlocks.blockMissionComp, typicalBlockProperties);
		AdvancedRocketryItems.itemSpaceLaser = new BlockItem(AdvancedRocketryBlocks.blockSpaceLaser, typicalBlockProperties);
		AdvancedRocketryItems.itemPrecisionAssembler = new BlockItem(AdvancedRocketryBlocks.blockPrecisionAssembler, typicalBlockProperties);
		AdvancedRocketryItems.itemArcFurnace = new BlockItem(AdvancedRocketryBlocks.blockArcFurnace, typicalBlockProperties);
		AdvancedRocketryItems.itemPrecisionLaserEtcher = new BlockItem(AdvancedRocketryBlocks.blockPrecisionLaserEtcher, typicalBlockProperties);
		AdvancedRocketryItems.itemBlastBrick = new BlockItem(AdvancedRocketryBlocks.blockBlastBrick, typicalBlockProperties);
		AdvancedRocketryItems.itemCrystallizer = new BlockItem(AdvancedRocketryBlocks.blockCrystallizer, typicalBlockProperties);
		AdvancedRocketryItems.itemLathe = new BlockItem(AdvancedRocketryBlocks.blockLathe, typicalBlockProperties);
		AdvancedRocketryItems.itemCuttingMachine = new BlockItem(AdvancedRocketryBlocks.blockCuttingMachine, typicalBlockProperties);
		AdvancedRocketryItems.itemObservatory = new BlockItem(AdvancedRocketryBlocks.blockObservatory, typicalBlockProperties);
		AdvancedRocketryItems.itemPlanetAnalyser = new BlockItem(AdvancedRocketryBlocks.blockPlanetAnalyser, typicalBlockProperties);
		AdvancedRocketryItems.itemLaunchpad = new BlockItem(AdvancedRocketryBlocks.blockLaunchpad, typicalBlockProperties);
		AdvancedRocketryItems.itemStructureTower = new BlockItem(AdvancedRocketryBlocks.blockStructureTower, typicalBlockProperties);
		AdvancedRocketryItems.itemRocketBuilder = new BlockItem(AdvancedRocketryBlocks.blockRocketBuilder, typicalBlockProperties);
		AdvancedRocketryItems.itemGenericSeat = new BlockItem(AdvancedRocketryBlocks.blockGenericSeat, typicalBlockProperties);
		AdvancedRocketryItems.itemEngine = new BlockItem(AdvancedRocketryBlocks.blockEngine, typicalBlockProperties);
		AdvancedRocketryItems.itemBiPropellantEngine = new BlockItem(AdvancedRocketryBlocks.blockBipropellantEngine, typicalBlockProperties);
		AdvancedRocketryItems.itemFuelTank = new BlockItem(AdvancedRocketryBlocks.blockFuelTank, typicalBlockProperties);
		AdvancedRocketryItems.itemBiPropellantFuelTank = new BlockItem(AdvancedRocketryBlocks.blockBipropellantFuelTank, typicalBlockProperties);
		AdvancedRocketryItems.itemFuelingStation = new BlockItem(AdvancedRocketryBlocks.blockFuelingStation, typicalBlockProperties);
		AdvancedRocketryItems.itemMonitoringStation = new BlockItem(AdvancedRocketryBlocks.blockMonitoringStation, typicalBlockProperties);
		AdvancedRocketryItems.itemSatelliteBuilder = new BlockItem(AdvancedRocketryBlocks.blockSatelliteBuilder, typicalBlockProperties);
		AdvancedRocketryItems.itemSatelliteControlCenter = new BlockItem(AdvancedRocketryBlocks.blockSatelliteControlCenter, typicalBlockProperties);
		//AdvancedRocketryItems.itemChipStorage = new BlockItem(AdvancedRocketryBlocks.blockChipStorage, typicalBlockProperties);
		AdvancedRocketryItems.itemMoonTurf = new BlockItem(AdvancedRocketryBlocks.blockMoonTurf, typicalBlockProperties);
		AdvancedRocketryItems.itemHotTurf = new BlockItem(AdvancedRocketryBlocks.blockHotTurf, typicalBlockProperties);
		//AdvancedRocketryItems.itemMultiMineOre = new BlockItem(AdvancedRocketryBlocks.blockMultiMineOre, typicalBlockProperties);
		AdvancedRocketryItems.itemLightSource = new BlockItem(AdvancedRocketryBlocks.blockLightSource, typicalBlockProperties);
		AdvancedRocketryItems.itemAlienWood = new BlockItem(AdvancedRocketryBlocks.blockAlienWood, typicalBlockProperties);
		AdvancedRocketryItems.itemAlienLeaves = new BlockItem(AdvancedRocketryBlocks.blockAlienLeaves, typicalBlockProperties);
		AdvancedRocketryItems.itemAlienSapling = new BlockItem(AdvancedRocketryBlocks.blockAlienSapling, typicalBlockProperties);
		AdvancedRocketryItems.itemGuidanceComputer = new BlockItem(AdvancedRocketryBlocks.blockGuidanceComputer, typicalBlockProperties);
		//AdvancedRocketryItems.itemLunarAnalyser = new BlockItem(AdvancedRocketryBlocks.blockLunarAnalyser, typicalBlockProperties);
		AdvancedRocketryItems.itemPlanetSelector = new BlockItem(AdvancedRocketryBlocks.blockPlanetSelector, typicalBlockProperties);
		AdvancedRocketryItems.itemSawBladeBlock = new BlockItem(AdvancedRocketryBlocks.blockSawBlade, typicalBlockProperties);
		AdvancedRocketryItems.itemConcrete = new BlockItem(AdvancedRocketryBlocks.blockConcrete, typicalBlockProperties);
		AdvancedRocketryItems.itemRollingMachine = new BlockItem(AdvancedRocketryBlocks.blockRollingMachine, typicalBlockProperties);
		AdvancedRocketryItems.itemPlatePress = new BlockItem(AdvancedRocketryBlocks.blockPlatePress, typicalBlockProperties);
		AdvancedRocketryItems.itemStationBuilder = new BlockItem(AdvancedRocketryBlocks.blockStationBuilder, typicalBlockProperties);
		AdvancedRocketryItems.itemElectrolyser = new BlockItem(AdvancedRocketryBlocks.blockElectrolyser, typicalBlockProperties);
		AdvancedRocketryItems.itemChemicalReactor = new BlockItem(AdvancedRocketryBlocks.blockChemicalReactor, typicalBlockProperties);
		AdvancedRocketryItems.itemOxygenVent = new BlockItem(AdvancedRocketryBlocks.blockOxygenVent, typicalBlockProperties);
		AdvancedRocketryItems.itemOxygenScrubber = new BlockItem(AdvancedRocketryBlocks.blockCO2Scrubber, typicalBlockProperties);
		AdvancedRocketryItems.itemOxygenCharger = new BlockItem(AdvancedRocketryBlocks.blockOxygenCharger, typicalBlockProperties);
		AdvancedRocketryItems.itemAirLock = new BlockItem(AdvancedRocketryBlocks.blockAirLock, typicalBlockProperties);
		AdvancedRocketryItems.itemLandingPad = new BlockItem(AdvancedRocketryBlocks.blockLandingPad, typicalBlockProperties);
		AdvancedRocketryItems.itemWarpCore = new BlockItem(AdvancedRocketryBlocks.blockWarpCore, typicalBlockProperties);
		AdvancedRocketryItems.itemWarpShipMonitor = new BlockItem(AdvancedRocketryBlocks.blockWarpShipMonitor, typicalBlockProperties);
		AdvancedRocketryItems.itemOxygenDetection = new BlockItem(AdvancedRocketryBlocks.blockOxygenDetection, typicalBlockProperties);
		AdvancedRocketryItems.itemUnlitTorch = new WallOrFloorItem(AdvancedRocketryBlocks.blockUnlitTorch, AdvancedRocketryBlocks.blockUnlitTorchWall, typicalBlockProperties);
		AdvancedRocketryItems.itemsGeode = new BlockItem(AdvancedRocketryBlocks.blocksGeode, typicalBlockProperties);
		AdvancedRocketryItems.itemVitrifiedSand = new BlockItem(AdvancedRocketryBlocks.blockVitrifiedSand, typicalBlockProperties);
		AdvancedRocketryItems.itemCharcoalLog = new BlockItem(AdvancedRocketryBlocks.blockCharcoalLog, typicalBlockProperties);
		AdvancedRocketryItems.itemElectricMushroom = new BlockItem(AdvancedRocketryBlocks.blockElectricMushroom, typicalBlockProperties);
		AdvancedRocketryItems.itemCrystal = new BlockItem(AdvancedRocketryBlocks.blockCrystal, typicalBlockProperties);
		AdvancedRocketryItems.itemCrystalRed = new BlockItem(AdvancedRocketryBlocks.blockCrystalRed, typicalBlockProperties);
		AdvancedRocketryItems.itemCrystalOrange = new BlockItem(AdvancedRocketryBlocks.blockCrystalOrange, typicalBlockProperties);
		AdvancedRocketryItems.itemCrystalYellow = new BlockItem(AdvancedRocketryBlocks.blockCrystalYellow, typicalBlockProperties);
		AdvancedRocketryItems.itemCrystalGreen = new BlockItem(AdvancedRocketryBlocks.blockCrystalGreen, typicalBlockProperties);
		AdvancedRocketryItems.itemCrystalCyan = new BlockItem(AdvancedRocketryBlocks.blockCrystalCyan, typicalBlockProperties);
		AdvancedRocketryItems.itemCrystalBlue = new BlockItem(AdvancedRocketryBlocks.blockCrystalBlue, typicalBlockProperties);
		AdvancedRocketryItems.itemCrystalPurple = new BlockItem(AdvancedRocketryBlocks.blockCrystalPurple, typicalBlockProperties);
		AdvancedRocketryItems.itemOrientationController = new BlockItem(AdvancedRocketryBlocks.blockOrientationController, typicalBlockProperties);
		AdvancedRocketryItems.itemGravityController = new BlockItem(AdvancedRocketryBlocks.blockGravityController, typicalBlockProperties);
		AdvancedRocketryItems.itemDrill = new BlockItem(AdvancedRocketryBlocks.blockDrill, typicalBlockProperties);
		//AdvancedRocketryItems.itemFluidPipe = new BlockItem(AdvancedRocketryBlocks.blockFluidPipe, typicalBlockProperties);
		//AdvancedRocketryItems.itemDataPipe = new BlockItem(AdvancedRocketryBlocks.blockDataPipe, typicalBlockProperties);
		AdvancedRocketryItems.itemMicrowaveReciever = new BlockItem(AdvancedRocketryBlocks.blockMicrowaveReciever, typicalBlockProperties);
		AdvancedRocketryItems.itemSolarPanel = new BlockItem(AdvancedRocketryBlocks.blockSolarPanel, typicalBlockProperties);
		AdvancedRocketryItems.itemSuitWorkStation = new BlockItem(AdvancedRocketryBlocks.blockSuitWorkStation, typicalBlockProperties);
		AdvancedRocketryItems.itemDataBus = new BlockItem(AdvancedRocketryBlocks.blockDataBus, typicalBlockProperties);
		AdvancedRocketryItems.itemSatelliteHatch = new BlockItem(AdvancedRocketryBlocks.blockSatelliteHatch, typicalBlockProperties);
		AdvancedRocketryItems.itemFluidLoader = new BlockItem(AdvancedRocketryBlocks.blockFluidLoader, typicalBlockProperties);
		AdvancedRocketryItems.itemFluidUnloader = new BlockItem(AdvancedRocketryBlocks.blockFluidUnloader, typicalBlockProperties);
		AdvancedRocketryItems.itemRocketLoader = new BlockItem(AdvancedRocketryBlocks.blockRocketLoader, typicalBlockProperties);
		AdvancedRocketryItems.itemRocketUnloader = new BlockItem(AdvancedRocketryBlocks.blockRocketUnloader, typicalBlockProperties);
		AdvancedRocketryItems.itemguidanceHatch = new BlockItem(AdvancedRocketryBlocks.blockguidanceHatch, typicalBlockProperties);
		AdvancedRocketryItems.itemBiomeScanner = new BlockItem(AdvancedRocketryBlocks.blockBiomeScanner, typicalBlockProperties);
		AdvancedRocketryItems.itemAtmosphereTerraformer = new BlockItem(AdvancedRocketryBlocks.blockAtmosphereTerraformer, typicalBlockProperties);
		AdvancedRocketryItems.itemDeployableRocketBuilder = new BlockItem(AdvancedRocketryBlocks.blockDeployableRocketBuilder, typicalBlockProperties);
		AdvancedRocketryItems.itemPressureTank = new BlockItem(AdvancedRocketryBlocks.blockPressureTank, typicalBlockProperties);
		AdvancedRocketryItems.itemIntake = new BlockItem(AdvancedRocketryBlocks.blockIntake, typicalBlockProperties);
		AdvancedRocketryItems.itemCircleLight = new BlockItem(AdvancedRocketryBlocks.blockCircleLight, typicalBlockProperties);
		//AdvancedRocketryItems.itemEnergyPipe = new BlockItem(AdvancedRocketryBlocks.blockEnergyPipe, typicalBlockProperties);
		AdvancedRocketryItems.itemSolarGenerator = new BlockItem(AdvancedRocketryBlocks.blockSolarGenerator, typicalBlockProperties);
		AdvancedRocketryItems.itemDockingPort = new BlockItem(AdvancedRocketryBlocks.blockDockingPort, typicalBlockProperties);
		AdvancedRocketryItems.itemAltitudeController = new BlockItem(AdvancedRocketryBlocks.blockAltitudeController, typicalBlockProperties);
		AdvancedRocketryItems.itemRailgun = new BlockItem(AdvancedRocketryBlocks.blockRailgun, typicalBlockProperties);
		AdvancedRocketryItems.itemAdvEngine = new BlockItem(AdvancedRocketryBlocks.blockAdvEngine, typicalBlockProperties);
		AdvancedRocketryItems.itemPlanetHoloSelector = new BlockItem(AdvancedRocketryBlocks.blockPlanetHoloSelector, typicalBlockProperties);
		AdvancedRocketryItems.itemLensBlock = new BlockItem(AdvancedRocketryBlocks.blockLens, typicalBlockProperties);
		AdvancedRocketryItems.itemForceField = new BlockItem(AdvancedRocketryBlocks.blockForceField, typicalBlockProperties);
		AdvancedRocketryItems.itemForceFieldProjector = new BlockItem(AdvancedRocketryBlocks.blockForceFieldProjector, typicalBlockProperties);
		AdvancedRocketryItems.itemGravityMachine = new BlockItem(AdvancedRocketryBlocks.blockGravityMachine, typicalBlockProperties);
		AdvancedRocketryItems.itemPipeSealer = new BlockItem(AdvancedRocketryBlocks.blockPipeSealer, typicalBlockProperties);
		AdvancedRocketryItems.itemSpaceElevatorController = new BlockItem(AdvancedRocketryBlocks.blockSpaceElevatorController, typicalBlockProperties);
		AdvancedRocketryItems.itemBeacon = new BlockItem(AdvancedRocketryBlocks.blockBeacon, typicalBlockProperties);
		AdvancedRocketryItems.itemAlienPlanks = new BlockItem(AdvancedRocketryBlocks.blockAlienPlanks, typicalBlockProperties);
		AdvancedRocketryItems.itemThermiteTorch = new WallOrFloorItem(AdvancedRocketryBlocks.blockThermiteTorch, AdvancedRocketryBlocks.blockThermiteTorchWall, typicalBlockProperties);
		AdvancedRocketryItems.itemTransciever = new BlockItem(AdvancedRocketryBlocks.blockTransciever, typicalBlockProperties);
		AdvancedRocketryItems.itemVacuumLaser = new BlockItem(AdvancedRocketryBlocks.blockVacuumLaser, typicalBlockProperties);
		AdvancedRocketryItems.itemMoonTurfDark = new BlockItem(AdvancedRocketryBlocks.blockMoonTurfDark, typicalBlockProperties);
		AdvancedRocketryItems.itemBlackHoleGenerator = new BlockItem(AdvancedRocketryBlocks.blockBlackHoleGenerator, typicalBlockProperties);
		AdvancedRocketryItems.itemPump = new BlockItem(AdvancedRocketryBlocks.blockPump, typicalBlockProperties);
		AdvancedRocketryItems.itemCentrifuge = new BlockItem(AdvancedRocketryBlocks.blockCentrifuge, typicalBlockProperties);
		AdvancedRocketryItems.itemBasalt = new BlockItem(AdvancedRocketryBlocks.blockBasalt, typicalBlockProperties);
		AdvancedRocketryItems.itemLandingFloat = new BlockItem(AdvancedRocketryBlocks.blockLandingFloat, typicalBlockProperties);
		
		evt.getRegistry().registerAll(AdvancedRocketryItems.itemLaunchpad.setRegistryName("launchpad"),
				AdvancedRocketryItems.itemRocketBuilder.setRegistryName("rocketassembler"),
				AdvancedRocketryItems.itemStructureTower.setRegistryName("structuretower"),
				AdvancedRocketryItems.itemGenericSeat.setRegistryName("seat"),
				AdvancedRocketryItems.itemEngine.setRegistryName("rocketmotor"),
				AdvancedRocketryItems.itemBiPropellantEngine.setRegistryName("bipropellantrocketmotor"),
				AdvancedRocketryItems.itemAdvEngine.setRegistryName("advrocketmotor"),
				AdvancedRocketryItems.itemFuelTank.setRegistryName("fueltank"),
				AdvancedRocketryItems.itemBiPropellantFuelTank.setRegistryName("bipropellantfueltank"),
				AdvancedRocketryItems.itemFuelingStation.setRegistryName("fuelingstation"),
				AdvancedRocketryItems.itemMonitoringStation.setRegistryName("monitoringstation"),
				AdvancedRocketryItems.itemSatelliteBuilder.setRegistryName("satellitebuilder"),
				AdvancedRocketryItems.itemMoonTurf.setRegistryName("moonturf"),
				AdvancedRocketryItems.itemMoonTurfDark.setRegistryName("moonturf_dark"),
				AdvancedRocketryItems.itemHotTurf.setRegistryName("hotturf"),
				AdvancedRocketryItems.itemPrecisionAssembler.setRegistryName("precisionassemblingmachine"),
				AdvancedRocketryItems.itemBlastBrick.setRegistryName("blastbrick"),
				AdvancedRocketryItems.itemCrystallizer.setRegistryName("crystallizer"),
				AdvancedRocketryItems.itemCuttingMachine.setRegistryName("cuttingmachine"),
				AdvancedRocketryItems.itemAlienWood.setRegistryName("alienwood"),
				AdvancedRocketryItems.itemAlienLeaves.setRegistryName("alienleaves"),
				AdvancedRocketryItems.itemAlienSapling.setRegistryName("aliensapling"),
				AdvancedRocketryItems.itemObservatory.setRegistryName("observatory"),
				AdvancedRocketryItems.itemBlackHoleGenerator.setRegistryName("blackholegenerator"),
				AdvancedRocketryItems.itemConcrete.setRegistryName("concrete"),
				AdvancedRocketryItems.itemPlanetSelector.setRegistryName("planetselector"),
				AdvancedRocketryItems.itemSatelliteControlCenter.setRegistryName("satellitecontrolcenter"),
				AdvancedRocketryItems.itemPlanetAnalyser.setRegistryName("planetanalyser"),
				AdvancedRocketryItems.itemGuidanceComputer.setRegistryName("guidancecomputer"),
				AdvancedRocketryItems.itemArcFurnace.setRegistryName("arcfurnace"),
				AdvancedRocketryItems.itemPrecisionLaserEtcher.setRegistryName("precisionlaseretcher"),
				AdvancedRocketryItems.itemSawBladeBlock.setRegistryName("sawbladeassbly"),
				AdvancedRocketryItems.itemLathe.setRegistryName("lathe"),
				AdvancedRocketryItems.itemRollingMachine.setRegistryName("rollingmachine"),
				AdvancedRocketryItems.itemPlatePress.setRegistryName("platepress"),
				AdvancedRocketryItems.itemStationBuilder.setRegistryName("stationbuilder"),
				AdvancedRocketryItems.itemElectrolyser.setRegistryName("electrolyser"),
				AdvancedRocketryItems.itemChemicalReactor.setRegistryName("chemicalreactor"),
				AdvancedRocketryItems.itemOxygenScrubber.setRegistryName("oxygenscrubber"),
				AdvancedRocketryItems.itemOxygenVent.setRegistryName("oxygenvent"),
				AdvancedRocketryItems.itemOxygenCharger.setRegistryName("oxygencharger"),
				AdvancedRocketryItems.itemAirLock.setRegistryName("airlock_door"),
				AdvancedRocketryItems.itemLandingPad.setRegistryName("landingpad"),
				AdvancedRocketryItems.itemWarpCore.setRegistryName("warpcore"),
				AdvancedRocketryItems.itemWarpShipMonitor.setRegistryName("stationmonitor"),
				AdvancedRocketryItems.itemOxygenDetection.setRegistryName("atmospheredetector"),
				AdvancedRocketryItems.itemUnlitTorch.setRegistryName("unlittorch"),
				AdvancedRocketryItems.itemsGeode.setRegistryName("geode"),
				AdvancedRocketryItems.itemVitrifiedSand.setRegistryName("vitrifiedsand"),
				AdvancedRocketryItems.itemCharcoalLog.setRegistryName("charcoallog"),
				AdvancedRocketryItems.itemElectricMushroom.setRegistryName("electricmushroom"),
				AdvancedRocketryItems.itemCrystal.setRegistryName("crystal"),
				AdvancedRocketryItems.itemCrystalRed.setRegistryName("crystal_red"),
				AdvancedRocketryItems.itemCrystalOrange.setRegistryName("crystal_orange"),
				AdvancedRocketryItems.itemCrystalYellow.setRegistryName("crystal_yellow"),
				AdvancedRocketryItems.itemCrystalGreen.setRegistryName("crystal_green"),
				AdvancedRocketryItems.itemCrystalCyan.setRegistryName("crystal_cyan"),
				AdvancedRocketryItems.itemCrystalBlue.setRegistryName("crystal_blue"),
				AdvancedRocketryItems.itemCrystalPurple.setRegistryName("crystal_purple"),
				AdvancedRocketryItems.itemOrientationController.setRegistryName("orientationcontroller"),
				AdvancedRocketryItems.itemGravityController.setRegistryName("gravitycontroller"),
				AdvancedRocketryItems.itemDrill.setRegistryName("drill"),
				AdvancedRocketryItems.itemMicrowaveReciever.setRegistryName("microwavereciever"),
				AdvancedRocketryItems.itemLightSource.setRegistryName("lightsource"),
				AdvancedRocketryItems.itemSolarPanel.setRegistryName("solarpanel"),
				AdvancedRocketryItems.itemSuitWorkStation.setRegistryName("suitworkstation"),
				AdvancedRocketryItems.itemDataBus.setRegistryName("databus"),
				AdvancedRocketryItems.itemSatelliteHatch.setRegistryName("satbay"),
				AdvancedRocketryItems.itemFluidLoader.setRegistryName("floader"),
				AdvancedRocketryItems.itemFluidUnloader.setRegistryName("funloader"),
				AdvancedRocketryItems.itemRocketLoader.setRegistryName("rloader"),
				AdvancedRocketryItems.itemRocketUnloader.setRegistryName("runloader"),
				AdvancedRocketryItems.itemguidanceHatch.setRegistryName("compaccesshatch"),
				AdvancedRocketryItems.itemBiomeScanner.setRegistryName("biomescanner"),
				AdvancedRocketryItems.itemAtmosphereTerraformer.setRegistryName("terraformer"),
				AdvancedRocketryItems.itemDeployableRocketBuilder.setRegistryName("deployablerocketbuilder"),
				AdvancedRocketryItems.itemPressureTank.setRegistryName("liquidtank"), 
				AdvancedRocketryItems.itemIntake.setRegistryName("intake"),
				AdvancedRocketryItems.itemCircleLight.setRegistryName("circlelight"),
				AdvancedRocketryItems.itemSolarGenerator.setRegistryName("solargenerator"),
				AdvancedRocketryItems.itemDockingPort.setRegistryName("stationmarker"),
				AdvancedRocketryItems.itemAltitudeController.setRegistryName("altitudecontroller"),
				AdvancedRocketryItems.itemRailgun.setRegistryName("railgun"),
				//AdvancedRocketryItems.itemAstroBed .setRegistryName("astrobed"),
				AdvancedRocketryItems.itemPlanetHoloSelector.setRegistryName("planetholoselector"),
				AdvancedRocketryItems.itemLensBlock.setRegistryName("blocklens"),
				AdvancedRocketryItems.itemForceField.setRegistryName("forcefield"),
				AdvancedRocketryItems.itemForceFieldProjector.setRegistryName("forcefieldprojector"),
				AdvancedRocketryItems.itemGravityMachine.setRegistryName("gravitymachine"),
				AdvancedRocketryItems.itemPipeSealer.setRegistryName("pipesealer"),
				AdvancedRocketryItems.itemSpaceElevatorController.setRegistryName("spaceelevatorcontroller"),
				AdvancedRocketryItems.itemBeacon.setRegistryName("beacon"),
				AdvancedRocketryItems.itemAlienPlanks.setRegistryName("planks"),
				AdvancedRocketryItems.itemThermiteTorch.setRegistryName("thermitetorch"),
				AdvancedRocketryItems.itemTransciever.setRegistryName("wirelesstransciever"),
				AdvancedRocketryItems.itemVacuumLaser.setRegistryName("vacuumlaser"),
				AdvancedRocketryItems.itemPump.setRegistryName("blockpump"),
				AdvancedRocketryItems.itemCentrifuge.setRegistryName("centrifuge"),
				AdvancedRocketryItems.itemBasalt.setRegistryName("basalt"),
				AdvancedRocketryItems.itemLandingFloat.setRegistryName("landingfloat"),
				AdvancedRocketryItems.itemSpaceLaser.setRegistryName("spacelaser"));
	}
}
