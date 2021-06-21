package zmaster587.advancedRocketry.world.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;

import java.util.HashMap;
/**
 * Object to store "data" and can track multiple different types
 *
 */
public class MultiData implements IDataHandler {
	private HashMap<DataStorage.DataType, DataStorage> dataStorages;

	public MultiData() {
		dataStorages = new HashMap<>();
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
	public int extractData(int maxAmount, DataType type, Direction dir, boolean commit) {

		DataStorage storage = dataStorages.get(type);
		
		if(storage == null)
			return 0;

		return storage.removeData(maxAmount, commit);
	}

	@Override
	public int addData(int maxAmount, DataType type, Direction dir, boolean commit) {
		DataStorage storage = dataStorages.get(type);

		if(storage == null)
			return 0;
		
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
	
	public void writeToNBT(CompoundNBT nbt) {
		for(DataStorage.DataType type : DataStorage.DataType.values()) {
			if(type != DataStorage.DataType.UNDEFINED) {
				CompoundNBT dataNBT = new CompoundNBT(); 
				
				dataStorages.get(type).writeToNBT(dataNBT);
				nbt.put(type.name(), dataNBT);
			}
		}
	}
	
	public void readFromNBT(CompoundNBT nbt) {
		for(DataStorage.DataType type : DataStorage.DataType.values()) {
			if(type != DataStorage.DataType.UNDEFINED) {
				CompoundNBT dataNBT = nbt.getCompound(type.name());
				dataStorages.get(type).readFromNBT(dataNBT);
				
			}
		}
	}
}