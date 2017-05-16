package zmaster587.advancedRocketry.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;

public class ItemAirUtils implements IFillableArmor {

	public static final ItemAirUtils INSTANCE = new ItemAirUtils();

	/**
	 * gets the amount of air remaining in the suit.
	 * @param stack stack from which to get an amount of air
	 * @return the amount of air in the stack
	 */
	@Override
	public int getAirRemaining(ItemStack stack) {

		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger("air");
		}
		else {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("air", 0);
			stack.setTagCompound(nbt);
			return getMaxAir(stack);
		}
	}

	/**
	 * Sets the amount of air remaining in the suit (WARNING: DOES NOT BOUNDS CHECK!)
	 * @param stack the stack to operate on
	 * @param amt amount of air to set the suit to
	 */
	@Override
	public void setAirRemaining(ItemStack stack, int amt) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else {
			nbt = new NBTTagCompound();
		}
		nbt.setInteger("air", amt);
		stack.setTagCompound(nbt);
	}

	/**
	 * Decrements air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air extracted from the suit
	 */
	@Override
	public int decrementAir(ItemStack stack, int amt) {

		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else {
			nbt = new NBTTagCompound();
		}

		int prevAmt = nbt.getInteger("air");
		int newAmt = Math.max(prevAmt - amt,0);
		nbt.setInteger("air", newAmt);
		stack.setTagCompound(nbt);

		return prevAmt - newAmt;
	}

	/**
	 * Increments air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air inserted into the suit
	 */
	@Override
	public int increment(ItemStack stack, int amt) {

		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else {
			nbt = new NBTTagCompound();
		}

		int prevAmt = nbt.getInteger("air");
		int newAmt = Math.min(prevAmt + amt, getMaxAir(stack));
		nbt.setInteger("air", newAmt);
		stack.setTagCompound(nbt);

		return newAmt - prevAmt;
	}

	/**
	 * @return the maximum amount of air allowed in this suit
	 */
	@Override
	public int getMaxAir(ItemStack stack) {

		return Configuration.spaceSuitOxygenTime*1200; //30 minutes;
	}

	public boolean isStackValidAirContainer(ItemStack stack) {
		if(stack == null)
			return false;

		//Check for enchantment
		boolean isEnchanted = false;
		NBTTagList enchList = stack.getEnchantmentTagList();
		if(enchList != null) {
			for(int i = 0 ; i < enchList.tagCount(); i++) {
				NBTTagCompound compound = enchList.getCompoundTagAt(i);
				isEnchanted = compound.getShort("id") == Enchantment.getEnchantmentID(AdvancedRocketryAPI.enchantmentSpaceProtection);
				if(isEnchanted)
					break;
			}
		}
		return isEnchanted;
	}
	
	public static class ItemAirWrapper implements IFillableArmor, IProtectiveArmor {
		ItemStack stack;
		
		public ItemAirWrapper(ItemStack myStack) {
			stack = myStack;
		}

		@Override
		public int getAirRemaining(ItemStack stack) {
			return ItemAirUtils.INSTANCE.getAirRemaining(this.stack);
		}

		@Override
		public void setAirRemaining(ItemStack stack, int amt) {
			ItemAirUtils.INSTANCE.setAirRemaining(this.stack,amt);
		}

		@Override
		public int decrementAir(ItemStack stack, int amt) {
			return ItemAirUtils.INSTANCE.decrementAir(this.stack, amt);
		}

		@Override
		public int increment(ItemStack stack, int amt) {
			return ItemAirUtils.INSTANCE.increment(this.stack, amt);
		}

		@Override
		public int getMaxAir(ItemStack stack) {
			return ItemAirUtils.INSTANCE.getMaxAir(this.stack);
		}

		@Override
		public boolean protectsFromSubstance(IAtmosphere atmosphere,
				ItemStack stack, boolean commitProtection) {
			if(stack != null && stack.getItem() instanceof ItemArmor) {
				if(((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.CHEST )
					return decrementAir(stack, 1) == 1;
				
				return true;
			}
			return false;
		}
		
	}
}
