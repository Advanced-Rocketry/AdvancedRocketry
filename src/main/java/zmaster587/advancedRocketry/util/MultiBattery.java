package zmaster587.advancedRocketry.util;

import java.util.LinkedList;

import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.libVulpes.api.IUniversalEnergy;

public class MultiBattery implements IUniversalEnergy {

	//Note: as of writing there should never be a need to save this
	
	protected LinkedList<IUniversalEnergy> batteries = new LinkedList<IUniversalEnergy>();
	
	public void addBattery(IUniversalEnergy battery) {
		batteries.add(battery);
	}
	
	public boolean removeBattery(IUniversalEnergy battery) {
		return batteries.remove(battery);
	}
	
	public void clear() {
		batteries.clear();
	}
	
	@Override
	public int extractEnergy(ForgeDirection arg0, int amt, boolean simulate) {
		int amtExtracted = 0;
		
		for(IUniversalEnergy battery : batteries)
			amtExtracted += battery.extractEnergy(ForgeDirection.UNKNOWN, amt, simulate);
		
		return amtExtracted;
	}

	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		int energyStored = 0;
		for(IUniversalEnergy battery : batteries)
			energyStored += battery.getEnergyStored(arg0);

		return energyStored;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		int energyStored = 0;
		for(IUniversalEnergy battery : batteries)
			energyStored += battery.getMaxEnergyStored(arg0);

		return energyStored;
	}

	@Override
	public int receiveEnergy(ForgeDirection side, int amt, boolean simulate) {
		int energyRecieved = 0;
		for(IUniversalEnergy battery : batteries)
			energyRecieved += battery.receiveEnergy(side, amt, simulate);
		return energyRecieved;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return false;
	}

	@Override
	public void setEnergyStored(int amt) {
		int difference = amt - getEnergyStored(ForgeDirection.UNKNOWN);
		int amtAdded = 0;
		
		//Possible inf loop
		if(difference > 0)
		while(amtAdded < difference)
			amtAdded += receiveEnergy(ForgeDirection.UNKNOWN, difference, false);
		else if(difference < 0)
			while(amtAdded < -difference)
				amtAdded += extractEnergy(ForgeDirection.UNKNOWN, -difference, false);
	}

}
