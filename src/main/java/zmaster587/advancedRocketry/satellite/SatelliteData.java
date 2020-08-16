package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.util.ZUtils;

public abstract class SatelliteData extends SatelliteBase {
	DataStorage data;
	long lastActionTime, prevLastActionTime;

	@Override
	public String getInfo(World world) {
		//tiles dont update unless ppl reopen
		return "Power: " + satelliteProperties.getPowerStorage() + "\nData Storage: " + ZUtils.formatNumber(data.getMaxData()) +
				"\nData: " + ZUtils.formatNumber((data.getData() + dataCreated(world)));
	}

	private int dataCreated(World world) {
		return Math.min(data.getMaxData() - data.getData() , (int)Math.max(0,  (world.getGameTime() - lastActionTime)/200)); //TODO: change from 10 seconds
	}

	@Override
	public boolean acceptsItemInConstruction(ItemStack item) {
		int flag = SatelliteRegistry.getSatelliteProperty(item).getPropertyFlag();

		return super.acceptsItemInConstruction(item) || SatelliteProperties.Property.DATA.isOfType(flag) || SatelliteProperties.Property.POWER_GEN.isOfType(flag);
	}

	@Override
	public void setProperties(ItemStack satelliteProperties) {
		super.setProperties(satelliteProperties);
		data.setMaxData(this.satelliteProperties.getMaxDataStorage());
	}


	@Override
	public boolean performAction(PlayerEntity player, World world, BlockPos pos) {

		//Calculate Data Recieved
		//TODO: pay attn to power
		int dataCreated = dataCreated(world);
		if(dataCreated > 0) {
			data.addData(dataCreated(world), data.getDataType(), true);
			lastActionTime = world.getGameTime();
		}

		TileEntity tile = world.getTileEntity(pos);

		if(tile instanceof IDataHandler) {
			IDataInventory dataInv = (IDataInventory)tile;

			data.removeData(dataInv.addData(data.getData(), data.getDataType(), Direction.DOWN, true), true);
		}

		return false;
	}

	@Override
	public void setDimensionId(World world) {
		//TODO: send packet on orbit reached
		super.setDimensionId(world);

		lastActionTime = world.getGameTime();
	}

	@Override
	public void readFromNBT(CompoundNBT nbt) {
		super.readFromNBT(nbt);

		this.data.readFromNBT(nbt.getCompound("data"));
		lastActionTime = nbt.getLong("lastActionTime");
	}

	@Override
	public void writeToNBT(CompoundNBT nbt) {
		super.writeToNBT(nbt);

		CompoundNBT data = new CompoundNBT();
		this.data.writeToNBT(data);
		nbt.put("data", data);

		nbt.putLong("lastActionTime",lastActionTime);
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
