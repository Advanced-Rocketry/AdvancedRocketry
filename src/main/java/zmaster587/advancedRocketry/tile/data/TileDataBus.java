package zmaster587.advancedRocketry.tile.data;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleData;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.network.PacketHandler;
import zmaster587.advancedRocketry.api.network.PacketMachine;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.tile.multiblock.TileInventoryHatch;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.util.INetworkMachine;

//TODO: allow dataCable connections
public class TileDataBus extends TileInventoryHatch implements IDataInventory, INetworkMachine {

	int maxData;
	DataStorage data;

	public TileDataBus() {
		data = new DataStorage(DataStorage.DataType.UNDEFINED);
		data.setMaxData(500);
		data.setData(500, DataStorage.DataType.DISTANCE);
	}

	public TileDataBus(int number) {
		super(number);
		data = new DataStorage(DataStorage.DataType.UNDEFINED);

		data.setMaxData(500);
	}

	@Override
	public void loadData() {
		
		ItemStack itemStack = inventory.getStackInSlot(0);
		
		if(itemStack != null && itemStack.getItem() instanceof ItemData) {
			ItemData itemData = (ItemData)itemStack.getItem();
			
			itemData.removeData(itemStack, 
					this.data.addData(itemData.getData(itemStack), itemData.getDataType(itemStack)), DataStorage.DataType.UNDEFINED);

			//this.data.removeData(itemData.removeData(inv[0], this.data.getData(), this.data.getDataType()));
		}

		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)-2));
		}
	}

	@Override
	public String getModularInventoryName() {
		return "tile.hatch.2.name";
	}

	@Override
	public void storeData() {
		ItemStack itemStack = inventory.getStackInSlot(0);
		
		if(itemStack != null && itemStack.getItem() instanceof ItemData && itemStack.stackSize == 1) {
			ItemData itemData = (ItemData)itemStack.getItem();

			this.data.removeData(itemData.addData(itemStack, this.data.getData(), this.data.getDataType()));
		}

		if(worldObj.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)-1));
		}
	}

	public void setData(int data, DataStorage.DataType dataType) {
		this.data.setData(data, dataType);
	}

	public int addData(int data, DataStorage.DataType dataType) {
		return this.data.addData(data, dataType);
	}

	public int getData() {
		return data.getData();
	}

	public final DataStorage getDataObject() {
		return data;
	}

	public int setMaxData() {
		return data.getMaxData();
	}

	public void setMaxData(int maxData) {
		data.setMaxData(maxData);
	}
	
	/**
	 * @param type the datatype to lock the tile to or null to unlock
	 * @see DataStorage
	 */
	public void lockData(DataStorage.DataType type) {
		data.lockDataType(type);
	}

	@Override
	public List<ModuleBase> getModules() {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModuleData(40, 20, 0, this, data));
		return modules;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		data.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		data.readFromNBT(nbt);
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

		if(id == -1) {
			storeData();
		}
		else if(id == -2)
			loadData();
	}

	@Override
	public int extractData(int maxAmount, DataType type) {
		if(type == DataStorage.DataType.UNDEFINED || this.data.getDataType() == type)
			return this.data.removeData(maxAmount);
		return 0;
	}
}
