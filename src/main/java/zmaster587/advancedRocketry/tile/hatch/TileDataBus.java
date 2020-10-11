package zmaster587.advancedRocketry.tile.hatch;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.inventory.modules.ModuleData;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.util.INetworkMachine;

import java.util.LinkedList;
import java.util.List;

//TODO: allow dataCable connections
public class TileDataBus extends TileInventoryHatch implements IDataInventory, INetworkMachine {

	int maxData;
	DataStorage data;

	public TileDataBus() {
		super(AdvancedRocketryTileEntityType.TILE_DATA_BUS);
		data = new DataStorage(DataStorage.DataType.UNDEFINED);
		data.setMaxData(2000);
	}

	public TileDataBus(int number) {
		super(AdvancedRocketryTileEntityType.TILE_DATA_BUS, number);
		data = new DataStorage(DataStorage.DataType.UNDEFINED);

		data.setMaxData(2000);
	}

	@Override
	public void loadData(int id) {
		
		ItemStack itemStack = inventory.getStackInSlot(0);
		
		if(itemStack != null && itemStack.getItem() instanceof ItemData) {
			ItemData itemData = (ItemData)itemStack.getItem();
			
			itemData.removeData(itemStack, 
					this.data.addData(itemData.getData(itemStack), itemData.getDataType(itemStack), true), DataStorage.DataType.UNDEFINED);

			//this.data.removeData(itemData.removeData(inv[0], this.data.getData(), this.data.getDataType()));
		}

		if(world.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)-2));
		}
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.databus";
	}

	@Override
	public void storeData(int id) {
		ItemStack itemStack = inventory.getStackInSlot(0);
		
		if(itemStack != null && itemStack.getItem() instanceof ItemData && itemStack.getCount() == 1) {
			ItemData itemData = (ItemData)itemStack.getItem();

			this.data.removeData(itemData.addData(itemStack, this.data.getData(), this.data.getDataType()), true);
		}

		if(world.isRemote) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)-1));
		}
	}

	public void setData(int data, DataStorage.DataType dataType) {
		this.data.setData(data, dataType);
	}

	@Override
	public int addData(int data, DataStorage.DataType dataType, Direction dir, boolean commit) {
		return this.data.addData(data, dataType, commit);
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
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModuleData(40, 20, 0, this, data));
		return modules;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		
		return nbt;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);
	}
	
	@Override
	protected CompoundNBT writeToNBTHelper(CompoundNBT nbtTagCompound) {
		super.writeToNBTHelper(nbtTagCompound);
		data.writeToNBT(nbtTagCompound);
		return nbtTagCompound;
	}
	
	@Override
	protected void readFromNBTHelper(CompoundNBT nbtTagCompound) {
		super.readFromNBTHelper(nbtTagCompound);
		data.readFromNBT(nbtTagCompound);
	}
	
	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {

	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {

	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {

		if(id == -1) {
			storeData(0);
		}
		else if(id == -2)
			loadData(0);
	}

	@Override
	public int extractData(int maxAmount, DataType type, Direction dir, boolean commit) {
		if(type == DataStorage.DataType.UNDEFINED || this.data.getDataType() == type)
			return this.data.removeData(maxAmount, commit);
		return 0;
	}
}
