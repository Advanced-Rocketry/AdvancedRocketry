package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.client.ClientProxy;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.util.ZUtils;

public abstract class SatelliteData extends SatelliteBase {
	DataStorage data;
	long lastActionTime, prevLastActionTime;

	@Override
	public String getInfo(World world) {
		//tiles dont update unless ppl reopen
		return "Power: " + satelliteProperties.getPowerStorage() + "\nData Storage: " + ZUtils.formatNumber(data.getMaxData()) +
				"\nData: " + ZUtils.formatNumber(data.getData());
	}

	@Override
	public boolean acceptsItemInConstruction(ItemStack item) {
		int flag = SatelliteRegistry.getSatelliteProperty(item).getPropertyFlag();

		return super.acceptsItemInConstruction(item) || SatelliteProperties.Property.DATA.isOfType(flag);
	}

	@Override
	public void setProperties(ItemStack satelliteProperties) {
		super.setProperties(satelliteProperties);
		data.setMaxData(this.satelliteProperties.getMaxDataStorage());
	}


	@Override
	public boolean performAction(EntityPlayer player, World world, BlockPos pos) {
		//Grab the tile to sync with
		TileEntity tile = world.getTileEntity(pos);
        //Remove data from the satellite and add it to the buffer of said tile, provided the tile can accept it
		if(tile instanceof IDataHandler) {
			IDataInventory dataInv = (IDataInventory)tile;

			data.removeData(dataInv.addData(data.getData(), data.getDataType(), EnumFacing.DOWN, true), true);
		}

		return false;
	}

	private int getDataCreated() {
		//Provided the satellite has enough power, produce some data every 200t (10 seconds)
		if (AdvancedRocketry.proxy.getWorldTimeUniversal(0) % 200 == 0 && battery.extractEnergy(10, true) == 10) {
			return 1;
		}
		return 0;
	}

	@Override
	public void tickEntity() {
		//Add data to the buffer, if the satellite has enough power
		data.addData(getDataCreated(), data.getDataType(), true);
		//Standard power stuff
		super.tickEntity();
	}

	@Override
	public void setDimensionId(World world) {
		//TODO: send packet on orbit reached
		super.setDimensionId(world);

		lastActionTime = world.getTotalWorldTime();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.data.readFromNBT(nbt.getCompoundTag("data"));
		lastActionTime = nbt.getLong("lastActionTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagCompound data = new NBTTagCompound();
		this.data.writeToNBT(data);
		nbt.setTag("data", data);

		nbt.setLong("lastActionTime",lastActionTime);
	}

	@Override
	public int numberChangesToSend() {
		return 4;
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		lastActionTime = ( lastActionTime & ( ~(0xffffl << (slot*16) ) ) ) | ( ( long )value << (slot*16) );
	}

	@Override
	public void sendChanges(Container container, IContainerListener crafter, int variableId, int localId) {
		crafter.sendWindowProperty(container, variableId, (short)(( lastActionTime >>> (localId*16) ) & 0xffff));

		if(localId == 3)
			prevLastActionTime=lastActionTime;
	}

	@Override
	public boolean isUpdateRequired(int localId) {
		return lastActionTime != prevLastActionTime;
	}
}
