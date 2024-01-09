package zmaster587.advancedRocketry.api.armor;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public interface IFillableArmor {
	/**
	 * gets the amount of air remaining in the suit.
	 * @param stack stack from which to get an amount of air
	 * @return the amount of air in the stack
	 */
	int getAirRemaining(@NotNull ItemStack stack);
	
	/**
	 * Sets the amount of air remaining in the suit
	 * @param stack the stack to operate on
	 * @param amt amount of air to set the suit to
	 */
	void setAirRemaining(@NotNull ItemStack stack, int amt);
	
	/**
	 * Decrements air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air extracted from the suit
	 */
	int decrementAir(@NotNull ItemStack stack, int amt);
	
	/**
	 * Increments air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air inserted into the suit
	 */
	int increment(@NotNull ItemStack stack, int amt);
	
	/**
	 * @return the maximum amount of air allowed in this suit
	 */
	int getMaxAir(@NotNull ItemStack stack);
	
}
