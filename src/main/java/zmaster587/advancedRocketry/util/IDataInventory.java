package zmaster587.advancedRocketry.util;

import net.minecraft.inventory.IInventory;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;

public interface IDataInventory extends IInventory, IDataHandler {
	
	/**
	 * stores from external into this
	 */
	void loadData(int id);
	
	
	/**
	 * Stores in external
	 * @param id IDataInventory to store data to
	 */
	void storeData(int id);
}
