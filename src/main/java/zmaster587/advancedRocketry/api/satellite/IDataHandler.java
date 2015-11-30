package zmaster587.advancedRocketry.api.satellite;

import zmaster587.advancedRocketry.api.DataStorage;

public interface IDataHandler {
	
	/**
	 * @param maxAmount Maximum amount to extract
	 * @param type the {@link DataStorage.DataType} to extract, UNDEFINED if it does not matter
	 * @return amount of data actually extracted
	 */
	public int extractData(int maxAmount, DataStorage.DataType type);
	
	/**
	 * @param maxAmount Maximum amount to add
	 * @param type the {@link DataStorage.DataType} to extract, UNDEFINED if it does not matter
	 * @return Amount of data actually added
	 */
	public int addData(int maxAmount, DataStorage.DataType type);
}
