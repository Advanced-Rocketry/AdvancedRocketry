package zmaster587.advancedRocketry.api.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IJetPack {
	public boolean isActive(ItemStack stack, EntityPlayer player);
	
	public boolean isEnabled(ItemStack stack);
	
	public void setEnabledState(ItemStack stack, boolean state);
	
	public void onAccelerate(ItemStack stack, IInventory inv, EntityPlayer player);
	
	public void changeMode(ItemStack stack, IInventory modules, EntityPlayer player);
}
