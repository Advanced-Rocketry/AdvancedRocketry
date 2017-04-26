package zmaster587.advancedRocketry.api.armor;

import net.minecraft.item.ItemStack;

public interface IFillableArmor {
	/**
	 * gets the amount of air remaining in the suit.
	 * @param stack stack from which to get an amount of air
	 * @return the amount of air in the stack
	 */
	public int getAirRemaining(ItemStack stack);
	
	/**
	 * Sets the amount of air remaining in the suit
	 * @param stack the stack to operate on
	 * @param amt amount of air to set the suit to
	 */
	public void setAirRemaining(ItemStack stack, int amt);
	
	/**
	 * Decrements air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air extracted from the suit
	 */
	public int decrementAir(ItemStack stack, int amt);
	
	/**
	 * Increments air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air inserted into the suit
	 */
	public int increment(ItemStack stack, int amt);
	
	/**
	 * @return the maximum amount of air allowed in this suit
	 */
	public int getMaxAir(ItemStack stack);
	
}
