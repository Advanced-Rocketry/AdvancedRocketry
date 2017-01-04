package zmaster587.advancedRocketry.tile.cables;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.cable.EnergyNetwork;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;

public class TileEnergyPipe extends TilePipe implements IEnergyReceiver {
	
	@Override
	public boolean canExtract(ForgeDirection dir, TileEntity e) {
		
		return e instanceof IEnergyProvider;
	}
	
	protected void attemptLink(ForgeDirection dir, TileEntity tile) {
		//If the pipe can inject or extract, add to the cache
		//if(!(tile instanceof IFluidHandler))
			//return;
		if(canExtract(dir, tile)) {
			if(worldObj.isRemote)
				connectedSides[dir.ordinal()]=true;
			else {
				getNetworkHandler().removeFromAllTypes(this, tile);
				getNetworkHandler().addSource(this,tile,dir);
			}
		}

		if(canInject(dir, tile)) {
			if(worldObj.isRemote)
				connectedSides[dir.ordinal()]=true;
			else {
				getNetworkHandler().removeFromAllTypes(this, tile);
				getNetworkHandler().addSink(this, tile,dir);
			}
		}
	}
	

	@Override
	public boolean canInject(ForgeDirection dir, TileEntity e) {
		return e instanceof IEnergyReceiver && !(e instanceof TileEnergyPipe);
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.energyNetwork;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}

	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		return 0;
	}

	@Override
	public int receiveEnergy(ForgeDirection arg0, int arg1, boolean arg2) {
		if(isInitialized()) {
			EnergyNetwork network = (EnergyNetwork)getNetworkHandler().getNetwork(getNetworkID());
			return network.acceptEnergy(arg1, arg2);
		}
		return 0;
	}
}
