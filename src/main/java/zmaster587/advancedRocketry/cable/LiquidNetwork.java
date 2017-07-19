package zmaster587.advancedRocketry.cable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.util.FluidUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class LiquidNetwork extends CableNetwork {

	private final int MAX_TRANSFER = 100;

	/**
	 * Create a new network and get an ID
	 * @return ID of this new network
	 */
	public static LiquidNetwork initNetwork() {
		Random random = new Random(System.currentTimeMillis());

		int id = random.nextInt();

		while(usedIds.contains(id)){ id = random.nextInt(); };

		LiquidNetwork net = new LiquidNetwork();

		usedIds.add(id);
		net.networkID = id;

		return net;
	}

	//TODO: balance tanks
	@Override
	public void tick() {

		int amount = MAX_TRANSFER;

		//Return if there is nothing to do
		if(sinks.isEmpty() || sources.isEmpty())
			return;

		Iterator<Entry<TileEntity,EnumFacing>> sinkItr = sinks.iterator();

		//Go through all sinks, if one is not full attempt to fill it
		
		while(sinkItr.hasNext()) {

			//Get tile and key
			Entry<TileEntity,EnumFacing> obj = (Entry<TileEntity, EnumFacing>)sinkItr.next();
			IFluidHandler fluidHandleSink = (IFluidHandler)obj.getKey().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, obj.getValue());
			EnumFacing dir = obj.getValue();

			Iterator<Entry<TileEntity,EnumFacing>> sourceItr = sources.iterator();

			Fluid fluid = null;

			if(fluidHandleSink == null) {
				sinkItr.remove();
				AdvancedRocketry.logger.info("Tile at " + obj.getKey().getPos().toString() + " is added as a sink but has no fluid capabilities on the side connected");
				continue;
			}
			
			//If the sink already has fluid in it then lets only try to fill it with that particular fluid
			for(IFluidTankProperties info : fluidHandleSink.getTankProperties()) {
				if(info != null && info.getContents() != null) {
					fluid = info.getContents().getFluid();
					break;
				}
			}

			//If no fluid can be found then find the first source with a fluid in it
			if(fluid == null) {
				out:
					while(sourceItr.hasNext()) {
						Entry<TileEntity,EnumFacing> objSource = (Entry<TileEntity, EnumFacing>)sourceItr.next();
						IFluidHandler fluidHandleSource = (IFluidHandler)objSource.getKey().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, obj.getValue());

						if(fluidHandleSource == null) {
							sourceItr.remove();
							AdvancedRocketry.logger.info("Tile at " + obj.getKey().getPos().toString() + " is added as a source but has no fluid capabilities on the side connected");
							continue;
						}
						
						for(IFluidTankProperties srcInfo : fluidHandleSource.getTankProperties()) {
							if(srcInfo != null && srcInfo.getContents() != null) {
								fluid = srcInfo.getContents().getFluid();
								break out;
							}
						}
					}

			}

			//No fluids can be moved
			if(fluid == null)
				break;

			if(fluidHandleSink.fill(new FluidStack(fluid, 1), false) > 0) {
				//Distribute? and drain tanks
				//Get the max the tank can take this tick then iterate through all sources until it's been filled
				sourceItr = sources.iterator();

				int maxFill = Math.min(fluidHandleSink.fill(new FluidStack(fluid, amount), false), amount);
				int actualFill = 0;
				while(sourceItr.hasNext()) {
					Entry<TileEntity,EnumFacing> objSource = (Entry<TileEntity, EnumFacing>)sourceItr.next();
					IFluidHandler fluidHandleSource = (IFluidHandler)objSource.getKey().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, obj.getValue());
					
					FluidStack fluid2;
					if((fluid2 = fluidHandleSource.drain(maxFill, false)) != null) {
						int buffer;
						
						//drain sometimes returns a null value even when canDrain returns true
						if(fluid2 == null || !FluidUtils.areFluidsSameType(fluid, fluid2.getFluid()))
							buffer = 0;
						else {
							fluidHandleSource.drain(maxFill, true);
							buffer=fluid2.amount;
						}

						maxFill -= buffer;
						actualFill += buffer;
					}

					if(maxFill == 0)
						break;
				}

				fluidHandleSink.fill(new FluidStack(fluid, actualFill), true);
			}
		}
	}
}
