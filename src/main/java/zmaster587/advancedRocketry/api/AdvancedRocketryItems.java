package zmaster587.advancedRocketry.api;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
/**
 * Stores references to Advanced Rocketry's items
 *
 */
public class AdvancedRocketryItems {

	//TODO: fix
	public static final ArmorMaterial spaceSuit = EnumHelper.addArmorMaterial("spaceSuit", "", ArmorMaterial.DIAMOND.getDurability(EntityEquipmentSlot.CHEST), new int[] {1,1,1,1}, 0, new SoundEvent(new ResourceLocation("")), 0);
	
	public static Item itemWafer;
	public static Item itemCircuitPlate;
	public static Item itemIC;
	public static Item itemSatellitePowerSource;
	public static Item itemSatellitePrimaryFunction;
	public static Item itemOreScanner;
	public static Item itemQuartzCrucible;
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
	public static Item itemSmallAirlockDoor;
	public static Item itemCarbonScrubberCartridge;
	public static Item itemSealDetector;
	public static Item itemJackhammer;
	public static Item itemAsteroidChip;
	public static Item itemLens;
	public static Item itemJetpack;
	public static Item itemPressureTank;
	public static Item itemUpgrade;
	public static Item itemAtmAnalyser;
	public static Item itemBiomeChanger;
	public static Item itemBasicLaserGun;
	public static Item itemSpaceElevatorChip;
	public static Item itemBeaconFinder;
	public static Item itemThermite;
	public static Item itemHovercraft;
}
