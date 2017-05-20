package zmaster587.advancedRocketry.cable;

import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.util.UniversalBattery;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class EnergyNetwork extends CableNetwork implements IUniversalEnergy {


	UniversalBattery battery;

	public EnergyNetwork() {
		battery = new UniversalBattery(500);
	}

	/**
	 * Create a new network and get an ID
	 * @return ID of this new network
	 */
	public static EnergyNetwork initNetwork() {
		Random random = new Random(System.currentTimeMillis());

		int id = random.nextInt();

		while(usedIds.contains(id)){ id = random.nextInt(); };

		EnergyNetwork net = new EnergyNetwork();
		usedIds.add(id);
		net.networkID = id;

		return net;
	}

	@Override
	public boolean merge(CableNetwork cableNetwork) {
		//Try not to lose power
		if(super.merge(cableNetwork)) {
			battery.acceptEnergy(((EnergyNetwork)cableNetwork).battery.getEnergyStored(), false);
			return true;
		}
		
		return false;
	}
	
	//TODO: balance tanks
	@Override
	public void tick() {
		int amount = 1000;
		//Return if there is nothing to do
		if(sinks.isEmpty() || (sources.isEmpty() && battery.getEnergyStored() != 0))
			return;



		//Go through all sinks, if one is not full attempt to fill it

		int demand = 0;
		int supply = battery.getEnergyStored();
		Iterator<Entry<TileEntity, ForgeDirection>> sinkItr = sinks.iterator();
		Iterator<Entry<TileEntity,ForgeDirection>> sourceItr = sources.iterator();

		while(sinkItr.hasNext()) {
			//Get tile and key
			Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sinkItr.next();
			IEnergyReceiver dataHandlerSink = (IEnergyReceiver) obj.getKey();

			demand += dataHandlerSink.receiveEnergy(obj.getValue(), amount, true);
		}

		while(sourceItr.hasNext()) {
			//Get tile and key
			Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sourceItr.next();
			IEnergyProvider dataHandlerSink = (IEnergyProvider) obj.getKey();

			supply += dataHandlerSink.extractEnergy(obj.getValue(),amount, true);
		}
		int amountMoved, amountToMove;
		amountMoved = amountToMove = Math.min(supply, demand);

		sinkItr = sinks.iterator();
		while(sinkItr.hasNext()) {


			//Get tile and key
			Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sinkItr.next();
			IEnergyReceiver dataHandlerSink = (IEnergyReceiver) obj.getKey();


			amountToMove -= dataHandlerSink.receiveEnergy(obj.getValue(), amountToMove, false);
		}

		//Try to drain from internal battery first
		amountMoved -= battery.extractEnergy(amountMoved, false);
		
		sourceItr = sources.iterator();
		while(sourceItr.hasNext()) {
			//Get tile and key
			Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sourceItr.next();
			IEnergyProvider dataHandlerSink = (IEnergyProvider) obj.getKey();

			amountMoved -= dataHandlerSink.extractEnergy(obj.getValue(),amountMoved, false);
		}
	}

	@Override
	public void setEnergyStored(int amt) {
		
	}

	@Override
	public int extractEnergy(int amt, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return 0;
	}

	@Override
	public int getMaxEnergyStored() {
		return 0;
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		return battery.acceptEnergy(amt, simulate);
	}

	@Override
	public void setMaxEnergyStored(int max) {
		
	}
}