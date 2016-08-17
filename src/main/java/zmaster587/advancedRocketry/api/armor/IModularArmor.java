package zmaster587.advancedRocketry.api.armor;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IModularArmor {
	
	public void addArmorComponent(World world, ItemStack armor, ItemStack componentStack, int slot);
	
	public ItemStack removeComponent(World world, ItemStack armor, int index);
	
	public List<ItemStack> getComponents(ItemStack armor);

	public ItemStack getComponentInSlot(ItemStack stack, int slot);
	
	public int getNumSlots(ItemStack stack);
}
