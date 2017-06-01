package zmaster587.advancedRocketry.util;

import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.libVulpes.util.INetworkMachine;
import net.minecraft.inventory.IInventory;

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
