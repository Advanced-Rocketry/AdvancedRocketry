package zmaster587.advancedRocketry.cable;

import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyNetwork extends CableNetwork {
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

	//TODO: balance tanks
	@Override
	public void tick() {
		int amount = 1000;
		//Return if there is nothing to do
		if(sinks.isEmpty() || sources.isEmpty())
			return;



		//Go through all sinks, if one is not full attempt to fill it

		int demand = 0;
		int supply = 0;
		Iterator<Entry<TileEntity,EnumFacing>> sinkItr = sinks.iterator();
		Iterator<Entry<TileEntity,EnumFacing>> sourceItr = sources.iterator();

		while(sinkItr.hasNext()) {
			//Get tile and key
			Entry<TileEntity,EnumFacing> obj = (Entry<TileEntity, EnumFacing>)sinkItr.next();
			IEnergyStorage dataHandlerSink = (IEnergyStorage)obj.getKey();

			demand += dataHandlerSink.receiveEnergy(amount, true);
		}

		while(sourceItr.hasNext()) {
			//Get tile and key
			Entry<TileEntity,EnumFacing> obj = (Entry<TileEntity, EnumFacing>)sourceItr.next();
			IEnergyStorage dataHandlerSink = (IEnergyStorage)obj.getKey();

			supply += dataHandlerSink.extractEnergy(amount, true);
		}
		int amountMoved, amountToMove;
		amountMoved = amountToMove = Math.min(supply, demand);

		sinkItr = sinks.iterator();
		while(sinkItr.hasNext()) {


			//Get tile and key
			Entry<TileEntity,EnumFacing> obj = (Entry<TileEntity, EnumFacing>)sinkItr.next();
			IEnergyStorage dataHandlerSink = (IEnergyStorage)obj.getKey();


			amountToMove -= dataHandlerSink.receiveEnergy(amountToMove, false);
		}

		sourceItr = sources.iterator();
		while(sourceItr.hasNext()) {
			//Get tile and key
			Entry<TileEntity,EnumFacing> obj = (Entry<TileEntity, EnumFacing>)sourceItr.next();
			IEnergyStorage dataHandlerSink = (IEnergyStorage)obj.getKey();

			amountMoved -= dataHandlerSink.extractEnergy(amountMoved, false);
		}
	}
}