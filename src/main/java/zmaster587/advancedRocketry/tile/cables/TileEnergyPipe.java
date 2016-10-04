package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;

public class TileEnergyPipe extends TilePipe {
	
	@Override
	public boolean canExtract(EnumFacing dir, TileEntity e) {
		
		return e instanceof IEnergyStorage && ((IEnergyStorage)e).canExtract();
	}

	@Override
	public boolean canInject(EnumFacing dir, TileEntity e) {
		return e instanceof IEnergyStorage && ((IEnergyStorage)e).canReceive();
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.energyNetwork;
	}
	
	protected void attemptLink(EnumFacing dir, TileEntity tile) {
		//If the pipe can inject or extract, add to the cache
		//if(!(tile instanceof IFluidHandler))
		//return;
		if(worldObj.isRemote && tile instanceof TileEnergyPipe)
			connectedSides[dir.ordinal()]=true;

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
}
