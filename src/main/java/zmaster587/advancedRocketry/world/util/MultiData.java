package zmaster587.advancedRocketry.world.util;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
/**
 * Object to store "data" and can track multiple different types
 *
 */
public class MultiData implements IDataHandler {
	HashMap<DataStorage.DataType, DataStorage> dataStorages;

	public MultiData() {
		dataStorages = new HashMap<DataStorage.DataType, DataStorage>();
		reset();
	}

	public void reset() {
		for(DataStorage.DataType type : DataStorage.DataType.values()) {
			if(type != DataStorage.DataType.UNDEFINED)
				dataStorages.put(type, new DataStorage(type));
		}
	}

	public int getDataAmount(DataType type) {
		return dataStorages.get(type).getData();
	}

	@Override
	public int extractData(int maxAmount, DataType type, ForgeDirection dir, boolean commit) {

		DataStorage storage = dataStorages.get(type);

		return storage.removeData(maxAmount, commit);
	}

	@Override
	public int addData(int maxAmount, DataType type, ForgeDirection dir, boolean commit) {
		DataStorage storage = dataStorages.get(type);

		return storage.addData(maxAmount, type, commit);
	}

	
	
	public void setMaxData(int amount) {
		for(DataStorage.DataType type : DataStorage.DataType.values()) {
			if(type != DataStorage.DataType.UNDEFINED)
				dataStorages.get(type).setMaxData(amount);
		}
	}
	
	public int getMaxData() {
		return dataStorages.get(DataStorage.DataType.ATMOSPHEREDENSITY).getMaxData();
	}
	
	public DataStorage getDataStorageForType(DataStorage.DataType dataType) {
		return dataStorages.get(dataType);
	}
	
	public void setDataAmount(int amount, DataType dataType) {
		if(dataType != DataType.UNDEFINED)
			dataStorages.get(dataType).setData(amount,dataType);
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		for(DataStorage.DataType type : DataStorage.DataType.values()) {
			if(type != DataStorage.DataType.UNDEFINED) {
				NBTTagCompound dataNBT = new NBTTagCompound(); 
				
				dataStorages.get(type).writeToNBT(dataNBT);
				nbt.setTag(type.name(), dataNBT);
			}
		}
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		for(DataStorage.DataType type : DataStorage.DataType.values()) {
			if(type != DataStorage.DataType.UNDEFINED) {
				NBTTagCompound dataNBT = nbt.getCompoundTag(type.name());
				dataStorages.get(type).readFromNBT(dataNBT);
				
			}
		}
	}
}