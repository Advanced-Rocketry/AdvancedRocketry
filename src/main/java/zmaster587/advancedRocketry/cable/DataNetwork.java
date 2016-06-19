package zmaster587.advancedRocketry.cable;

import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.util.IDataInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class DataNetwork extends CableNetwork  {
	/**
	 * Create a new network and get an ID
	 * @return ID of this new network
	 */
	public static DataNetwork initNetwork() {
		Random random = new Random(System.currentTimeMillis());

		int id = random.nextInt();

		while(usedIds.contains(id)){ id = random.nextInt(); };

		DataNetwork net = new DataNetwork();

		usedIds.add(id);
		net.networkID = id;

		return net;
	}

	//TODO: balance tanks
	@Override
	public void tick() {
		int amount = 1;

		//Return if there is nothing to do
		if(sinks.isEmpty() || sources.isEmpty())
			return;

		

		//Go through all sinks, if one is not full attempt to fill it
		for(DataType data : DataType.values()) {
			if(data == DataType.UNDEFINED)
				continue;
			
			int demand = 0;
			int supply = 0;
			Iterator<Entry<TileEntity,ForgeDirection>> sinkItr = sinks.iterator();
			Iterator<Entry<TileEntity,ForgeDirection>> sourceItr = sources.iterator();
			
			while(sinkItr.hasNext()) {
				//Get tile and key
				Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sinkItr.next();
				IDataHandler dataHandlerSink = (IDataHandler)obj.getKey();

				demand += dataHandlerSink.addData(amount, data, obj.getValue(), false);
			}
			
			while(sourceItr.hasNext()) {
				//Get tile and key
				Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sourceItr.next();
				IDataHandler dataHandlerSink = (IDataHandler)obj.getKey();
				
				supply += dataHandlerSink.extractData(amount, data, obj.getValue(), false);
			}
			int amountMoved, amountToMove;
			amountMoved = amountToMove = Math.min(supply, demand);
			
			sinkItr = sinks.iterator();
			while(sinkItr.hasNext()) {


				//Get tile and key
				Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sinkItr.next();
				IDataHandler dataHandlerSink = (IDataHandler)obj.getKey();


				amountToMove -= dataHandlerSink.addData(amountToMove, data, obj.getValue(), true);
			}
			
			sourceItr = sources.iterator();
			while(sourceItr.hasNext()) {


				//Get tile and key
				Entry<TileEntity,ForgeDirection> obj = (Entry<TileEntity, ForgeDirection>)sourceItr.next();
				IDataHandler dataHandlerSink = (IDataHandler)obj.getKey();

				amountMoved -= dataHandlerSink.extractData(amountMoved, data, obj.getValue(), true);
			}
		}
	}
}
