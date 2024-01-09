package zmaster587.advancedRocketry.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public class EnchantmentSpaceBreathing extends Enchantment {

	public EnchantmentSpaceBreathing() {
		super(Rarity.COMMON, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[] {EntityEquipmentSlot.CHEST, EntityEquipmentSlot.FEET, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.LEGS});
		this.setName("spaceBreathing");
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public boolean canApply(@NotNull ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemArmor;
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
