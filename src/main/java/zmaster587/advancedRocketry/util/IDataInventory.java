package zmaster587.advancedRocketry.util;

import net.minecraft.inventory.IInventory;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;

public interface IDataInventory extends IInventory, IDataHandler {
	
	/**
	 * stores from external into this
	 */
	public void loadData(int id);
	
	
	/**
	 * Stores in external
	 * @param storeTo IDataInventory to store data to
	 */
	public void storeData(int id);
}
