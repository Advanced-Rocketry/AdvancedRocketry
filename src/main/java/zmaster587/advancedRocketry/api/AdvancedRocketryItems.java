package zmaster587.advancedRocketry.api;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
/**
 * Stores references to Advanced Rocketry's items
 *
 */
public class AdvancedRocketryItems {

	//TODO: fix
	public static final ArmorMaterial spaceSuit = new ArmorMaterial("spaceSuit", net.minecraft.item.ArmorMaterial.DIAMOND.getDurability(EquipmentSlotType.CHEST), new int[] {1,1,1,1}, 0, new SoundEvent(new ResourceLocation("")), 0, 0, null);
	
	public static Item itemWafer;
	public static Item itemAnthracene;
	public static Item itemCircuitPlate;
	public static Item itemIC;
	public static Item itemSatellitePowerSource;
	public static Item itemSatellitePrimaryFunction;
	public static Item itemOreScanner;
	public static Item itemQuartzCrucible;
	public static Item itemSaplingBlue; 
	public static Item itemDataUnit;
	public static Item itemSatellite;
	public static Item itemSatelliteIdChip;
	public static Item itemPlanetIdChip;
	public static Item itemMisc;
	public static Item itemSawBlade;
	public static Item itemSpaceStationChip;
	public static Item itemSpaceStation;
	public static Item itemSpaceSuit_Helmet;
	public static Item itemSpaceSuit_Chest;
	public static Item itemSpaceSuit_Leggings;
	public static Item itemSpaceSuit_Boots;
	public static Item itemBucketRocketFuel;
	public static Item itemSmallAirlockDoor;
	public static Item itemCarbonScrubberCartridge;
	public static Item itemSealDetector;
	public static Item itemJackhammer;
	public static Item itemAsteroidChip;
	public static Item itemLens;
	public static Item itemJetpack;
	public static Item itemPressureTank;
	public static Item itemHighPressureTank;
	public static Item itemUpgrade;
	public static Item itemUpgradeHover;
	public static Item itemUpgradeSpeed;
	public static Item itemUpgradeFallBoots;
	public static Item itemAtmAnalyser;
	public static Item itemBiomeChanger;
	public static Item itemBucketNitrogen;
	public static Item itemBucketHydrogen;

	public static Item itemBucketOxygen;
	public static Item itemAstroBed;
	public static Item itemBasicLaserGun;
	public static Item itemSpaceElevatorChip;
	public static Item itemBeaconFinder;
	public static Item itemThermite;
	public static Item itemBucketEnrichedLava;
	public static Item itemHovercraft;
	
	public static Item[] itemUpgrades;
}
