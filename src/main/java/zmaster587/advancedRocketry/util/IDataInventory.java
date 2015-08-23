package zmaster587.advancedRocketry.util;

import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.libVulpes.util.INetworkMachine;
import net.minecraft.inventory.IInventory;

public interface IDataInventory extends IInventory, IDataHandler {
	
	/**
	 * stores from external in this
	 */
	public void loadData();
	
	
	/**
	 * Stores in external
	 * @param storeTo IDataInventory to store data to
	 */
	public void storeData();
}
