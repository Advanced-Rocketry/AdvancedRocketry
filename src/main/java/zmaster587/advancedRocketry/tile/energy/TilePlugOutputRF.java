package zmaster587.advancedRocketry.tile.energy;

import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyHandler;

public class TilePlugOutputRF extends TilePlugBase implements IEnergyHandler {

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
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
	public int receiveEnergy(ForgeDirection arg0, int arg1, boolean arg2) {
		return 0;
	}

}
