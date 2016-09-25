package zmaster587.advancedRocketry.api.satellite;

import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.api.DataStorage;

public interface IDataHandler {
	
	/**
	 * @param maxAmount Maximum amount to extract
	 * @param type the {@link DataStorage.DataType} to extract, UNDEFINED if it does not matter
	 * @param dir the direction from which the data is being extracted, UNKNOWN for internal use
	 * @param commit true if the change is to be commited, false if simulated
	 * @return amount of data actually extracted
	 */
	public int extractData(int maxAmount, DataStorage.DataType type, EnumFacing dir, boolean commit);
	
	/**
	 * @param maxAmount Maximum amount to add
	 * @param type the {@link DataStorage.DataType} to extract, UNDEFINED if it does not matter
	 * @param dir the direction from which the data is being extracted, UNKNOWN for internal use
	 * @param commit true if the change is to be commited, false if simulated
	 * @return Amount of data actually added
	 */
	public int addData(int maxAmount, DataStorage.DataType type, EnumFacing dir, boolean commit);
	
	/**
	 * 
	 * @param type data type
	 * @return the amount of data on the object
	 */
	//public int getDataAmount(DataStorage.DataType type);
}
