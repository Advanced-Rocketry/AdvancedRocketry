package zmaster587.advancedRocketry.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class EnchantmentSpaceBreathing extends Enchantment {

	public EnchantmentSpaceBreathing() {
		super(Rarity.COMMON, EnchantmentType.ARMOR, new EquipmentSlotType[] {EquipmentSlotType.CHEST, EquipmentSlotType.FEET, EquipmentSlotType.HEAD, EquipmentSlotType.LEGS});
		this.setRegistryName("spaceBreathing");
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canApply(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ArmorItem;
	}
	
	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean isAllowedOnBooks() {
		return false;
	}
}
