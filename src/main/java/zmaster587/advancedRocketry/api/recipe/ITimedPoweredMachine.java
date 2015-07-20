package zmaster587.advancedRocketry.api.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface ITimedPoweredMachine extends IInventory {
	
	public void removePower(int amt);
	
	public void setHoldingSlot(int slot, ItemStack item);
	
	public void setRecipeTime(int time);
	
	public int getPower();
}
