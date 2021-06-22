package zmaster587.advancedRocketry.api;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Locale;

public class DataStorage {

	public enum DataType {
		UNDEFINED,
		DISTANCE,
		HUMIDITY,
		TEMPERATURE,
		COMPOSITION,
		ATMOSPHEREDENSITY,
		MASS;

		public String toString() {
			return "data." + name().toLowerCase(Locale.ENGLISH);
		}
	}

	private int data, maxData;
	private DataType dataType;
	private boolean locked;

	public DataStorage() {
		dataType = DataType.UNDEFINED;
		locked = false;
	}

	public DataStorage(DataType data) {
		dataType = data;
		locked = false;
	}

	public boolean setData(int data, DataType dataType) {
		if(this.dataType == DataStorage.DataType.UNDEFINED)
			this.dataType = dataType;
		
		if(dataType == DataStorage.DataType.UNDEFINED || dataType == this.dataType) {
			this.data = Math.min(data, maxData);
			return true;
		}
		return false;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public int getData() {
		return data;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setMaxData(int maxData) {
		this.maxData = maxData;
		if(data > maxData)
			data = maxData;
	}

	public int getMaxData() {
		return maxData;
	}

	/**
	 * Locks the DataStorage to only accept the specified type and will delete any data that does not match the type
	 * @param type the type of data to lock to, null to unlock
	 */
	public void lockDataType(DataType type) {
		this.locked = type != null;

		if(this.locked) {
			if(this.dataType != type)
				this.data = 0;

			this.dataType = type;
		}
		else if(this.data == 0)
			this.dataType = DataType.UNDEFINED;
	}

	public boolean isLocked() { 
		return this.locked;
	}

	/**
	 * 
	 * @param data amount to add
	 * @param dataType type to add
	 * @return data amount added
	 */
	public int addData(int data, DataType dataType, boolean commit) {
		if((!this.locked && (dataType == DataStorage.DataType.UNDEFINED)) || dataType == this.dataType || this.dataType == DataStorage.DataType.UNDEFINED) {

			if(this.dataType == DataStorage.DataType.UNDEFINED)
				this.dataType = dataType;

			int amountToAdd = Math.min(data, this.maxData - this.data);
			if(commit)
				this.data += amountToAdd;
			return amountToAdd;
		}
		return 0;
	}

	/**
	 * @param data max amount of data to remove
	 * @return amount of data removed
	 */
	public int removeData(int data, boolean commit) {
		int dataRemoved = Math.min(data, this.data);
		if(commit)
			this.data -= dataRemoved;
		if(!locked && this.data == 0)
			this.dataType = DataType.UNDEFINED;

		return dataRemoved;
	}

	public void writeToNBT(CompoundNBT nbt) {
		nbt.putInt("Data", data);
		nbt.putInt("maxData", maxData);
		nbt.putInt("DataType", dataType.ordinal());
		nbt.putBoolean("locked", locked);
	}

	public void readFromNBT(@Nonnull CompoundNBT nbt) {
		data = nbt.getInt("Data");
		maxData = nbt.getInt("maxData");
		try {
			dataType = DataType.values()[nbt.getInt("DataType")];
		} catch(ArrayIndexOutOfBoundsException e)
		{
			dataType = DataType.UNDEFINED;
		}


		///TODO: dev compat
		if(nbt.contains("locked"))
			locked = nbt.getBoolean("locked");
		else
			locked = false;
	}
}
