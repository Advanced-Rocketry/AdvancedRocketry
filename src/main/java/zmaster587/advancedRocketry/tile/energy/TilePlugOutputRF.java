package zmaster587.advancedRocketry.tile.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;

public class TilePlugOutputRF extends TilePlugBase implements IEnergyHandler {

	public TilePlugOutputRF() {
		super(1);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}


	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);

			if(tile instanceof IEnergyReceiver) {
				IEnergyReceiver handle = (IEnergyReceiver)tile;
				storage.getEnergyStored();
				storage.extractEnergy(handle.receiveEnergy(dir.getOpposite(), storage.getEnergyStored(), false), false);
			}
		}
	}

	@Override
	public String getModularInventoryName() {
		return "tile.rfOutputPlug.name";
	}

	@Override
	public String getInventoryName() {
		return "";
	}

	@Override
	public int extractEnergy(ForgeDirection dir, int maxExtract, boolean simulate) {
		return storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection dir) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(ForgeDirection dir, int amt, boolean simulate) {
		if(dir == ForgeDirection.UNKNOWN)
			return storage.acceptEnergy(amt, simulate);
		return 0;
	}

}
