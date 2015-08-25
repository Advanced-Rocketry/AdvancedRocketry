package zmaster587.advancedRocketry.Inventory.modules;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public interface IModularInventory {
	
	/**
	 * @return a list of modules to add to the inventory
	 */
	public List<ModuleBase> getModules();
	
	public String getModularInventoryName();
	
	public boolean canInteractWithContainer(EntityPlayer entity);
	
}
