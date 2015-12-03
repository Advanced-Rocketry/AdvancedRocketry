package zmaster587.advancedRocketry.tile;

import cofh.api.energy.IEnergyHandler;
import zmaster587.libVulpes.util.UniversalBattery;
import net.minecraftforge.common.util.ForgeDirection;

public class TileRFPlug extends TilePlugBase implements IEnergyHandler {

	UniversalBattery storage;
	int teir;
	
	public TileRFPlug() {
		super(1);
	}

	@Override
	public int extractEnergy(ForgeDirection dir, int amt, boolean sim) {
		return extractEnergy(amt, sim);
	}

	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		return getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		return getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(ForgeDirection arg0, int maxReceive, boolean simulate) {
		return acceptEnergy(maxReceive, simulate);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}


	@Override
	public String getModularInventoryName() {
		return "RF Storage Box";
	}
	
	@Override
	public String getInventoryName() {
		return null;
	}

}
