package zmaster587.advancedRocketry.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class EnchantmentSpaceBreathing extends Enchantment {

	public EnchantmentSpaceBreathing() {
		super(Rarity.COMMON, EnchantmentType.ARMOR, new EquipmentSlotType[] {EquipmentSlotType.CHEST, EquipmentSlotType.FEET, EquipmentSlotType.HEAD, EquipmentSlotType.LEGS});
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public boolean canApply(@Nonnull  ItemStack stack) {
		return stack.getItem() instanceof ArmorItem;
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
