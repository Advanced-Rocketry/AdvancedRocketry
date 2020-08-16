package zmaster587.advancedRocketry.api.satellite;

import net.minecraft.nbt.CompoundNBT;
import zmaster587.advancedRocketry.api.SatelliteRegistry;

public class SatelliteProperties {

	public static enum Property {
		MAIN,
		DATA,
		POWER_GEN,
		BATTERY;

		public int getFlag() {
			return 1 << ordinal();
		}

		public boolean isOfType(int flag) {
			return (flag & getFlag()) != 0;
		}
	}

	private int powerGeneration, powerStorage, maxData;
	private long id;
	private String satType;

	public SatelliteProperties() {
		satType = null;
		id = -1;
	}

	public SatelliteProperties(int powerGeneration, int powerStorage, String satType, int maxData) {
		this();
		this.powerGeneration = powerGeneration;
		this.powerStorage = powerStorage;
		this.satType = satType;
		this.maxData = maxData;
	}


	/**
	 * 
	 * @return a flag containing the abilities of the item
	 */
	public int getPropertyFlag() {

		int flag = 0;
		if(satType != null)
			flag |= Property.MAIN.getFlag();
		if(this.powerGeneration != 0)
			flag |= Property.POWER_GEN.getFlag();
		if(this.powerStorage != 0)
			flag |= Property.BATTERY.getFlag();
		if(this.maxData != 0)
			flag |= Property.DATA.getFlag();

		return flag;
	}

	/**
	 * @return the unique ID of the satellite
	 */
	public long getId() {
		return id;
	}

	/**
	 * Assigns the ID parameter as the new ID of this satellite only if it does not already have one
	 * @param id the new Unique Id of the satellite
	 * @return true if a new id is assigned, false otherwise
	 */
	public boolean setId(long id) {

		if(this.id == -1) {
			this.id = id;
			return true;
		}
		return false;
	}

	/**
	 * @param powerGeneration amount of power this satellite can generate
	 * @return this
	 */
	public SatelliteProperties setPowerGeneration(int powerGeneration) {
		this.powerGeneration = powerGeneration;
		return this;
	}

	/**
	 * @return Amount of power per tick this satellite can generate
	 */
	public int getPowerGeneration() {
		return powerGeneration;
	}

	/**
	 * @param powerStorage The new size of the power buffer
	 * @return this
	 */
	public SatelliteProperties setPowerStorage(int powerStorage) {
		this.powerStorage = powerStorage;
		return this;
	}

	/**
	 * @return the current size of the power buffer
	 */
	public int getPowerStorage() {
		return powerStorage;
	}

	/**
	 * @param maxData Maximum amount of Data this satellite can store
	 * @return this
	 */
	public SatelliteProperties setMaxData(int maxData) {
		this.maxData = maxData;
		return this;
	}

	/**
	 * @return Maximum size of the data buffer
	 */
	public int getMaxDataStorage() {
		return maxData;
	}

	/**
	 * @param type the string identifying satellite Type of this satellite as stored in {@link SatelliteRegistry}
	 * @return this
	 */
	public SatelliteProperties setSatelliteType(String type) {
		this.satType = type;
		return this;
	}

	/**
	 * @return the string identifying the satellite type as stored in {@link SatelliteRegistry}
	 */
	public String getSatelliteType() {
		return satType;
	}


	public void writeToNBT(CompoundNBT nbt) {
		nbt.putInt("powerGeneration", powerGeneration);
		nbt.putInt("powerStorage", powerStorage);
		nbt.putString("dataType", satType);
		nbt.putLong("satId", id);
		nbt.putInt("maxData", maxData);
	}

	public void readFromNBT(CompoundNBT nbt) {
		powerGeneration = nbt.getInt("powerGeneration");
		powerStorage = nbt.getInt("powerStorage");
		satType = nbt.getString("dataType");
		id = nbt.getLong("satId");
		maxData = nbt.getInt("maxData");
	}
}
