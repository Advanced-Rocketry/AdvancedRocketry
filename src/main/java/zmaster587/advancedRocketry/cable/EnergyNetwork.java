package zmaster587.advancedRocketry.cable;

import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

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
		Iterator<Entry<TileEntity, ForgeDirection>> sinkItr = sinks.iterator();
		Iterator<Entry<TileEntity,ForgeDirection>> sourceItr = sources.iterator();

		while(sinkItr.hasNext()) {
			//Get tile and key
			Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sinkItr.next();
			IEnergyHandler dataHandlerSink = (IEnergyHandler) obj.getKey();

			demand += dataHandlerSink.receiveEnergy(obj.getValue(), amount, true);
		}

		while(sourceItr.hasNext()) {
			//Get tile and key
			Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sourceItr.next();
			IEnergyHandler dataHandlerSink = (IEnergyHandler) obj.getKey();

			supply += dataHandlerSink.extractEnergy(obj.getValue(),amount, true);
		}
		int amountMoved, amountToMove;
		amountMoved = amountToMove = Math.min(supply, demand);

		sinkItr = sinks.iterator();
		while(sinkItr.hasNext()) {


			//Get tile and key
			Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sinkItr.next();
			IEnergyHandler dataHandlerSink = (IEnergyHandler) obj.getKey();


			amountToMove -= dataHandlerSink.receiveEnergy(obj.getValue(), amountToMove, false);
		}

		sourceItr = sources.iterator();
		while(sourceItr.hasNext()) {
			//Get tile and key
			Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sourceItr.next();
			IEnergyHandler dataHandlerSink = (IEnergyHandler) obj.getKey();

			amountMoved -= dataHandlerSink.extractEnergy(obj.getValue(),amountMoved, false);
		}
	}
}