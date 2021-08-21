package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import zmaster587.advancedRocketry.cable.EnergyNetwork;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.cap.ForgePowerCapability;
import zmaster587.libVulpes.cap.TeslaHandler;

import javax.annotation.Nullable;

public class TileEnergyPipe extends TilePipe implements IUniversalEnergy {
	
	@Override
	public boolean canExtract(EnumFacing dir, TileEntity e) {
		
		return e.hasCapability(CapabilityEnergy.ENERGY, dir) && e.getCapability(CapabilityEnergy.ENERGY, dir).canExtract() && !(e instanceof TileEnergyPipe);
	}

	@Override
	public boolean canInject(EnumFacing dir, TileEntity e) {
		return e.hasCapability(CapabilityEnergy.ENERGY, dir) && e.getCapability(CapabilityEnergy.ENERGY, dir).canReceive() && !(e instanceof TileEnergyPipe);
	}
	
	@Override
	public boolean hasCapability(@Nullable Capability<?> capability, EnumFacing facing) {

		return capability == CapabilityEnergy.ENERGY || TeslaHandler.hasTeslaCapability(this, capability);
	}

	@Override
	public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {

		if(capability == CapabilityEnergy.ENERGY )
			return (T)(new ForgePowerCapability(this));
		else if(TeslaHandler.hasTeslaCapability(this, capability))
			return (T)(TeslaHandler.getHandler(this));
		
		return super.getCapability(capability, facing);
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.energyNetwork;
	}
	
	protected void attemptLink(EnumFacing dir, TileEntity tile) {
		//If the pipe can inject or extract, add to the cache
		//if(!(tile instanceof IFluidHandler))
		//return;
		//if(world.isRemote && tile instanceof TileEnergyPipe)
		//	connectedSides[dir.ordinal()]=true;

		if(canExtract(dir, tile)) {
			if(!world.isRemote) {
				connectedSides[dir.ordinal()]=true;
				getNetworkHandler().removeFromAllTypes(this, tile);
				getNetworkHandler().addSource(this,tile,dir);
			}
		}

		if(canInject(dir, tile)) {
			if(!world.isRemote) {
				connectedSides[dir.ordinal()]=true;
				getNetworkHandler().removeFromAllTypes(this, tile);
				getNetworkHandler().addSink(this, tile,dir);
			}
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
	public int getUniversalEnergyStored() {
		return 0;
	}

	@Override
	public int getMaxEnergyStored() {
		return 0;
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		if(isInitialized()) {
			EnergyNetwork network = (EnergyNetwork)getNetworkHandler().getNetwork(getNetworkID());
			return network.acceptEnergy(amt, simulate);
		}
		return 0;
	}

	@Override
	public void setMaxEnergyStored(int max) {
		
	}

	@Override
	public boolean canReceive() {
		return true;
	}

	@Override
	public boolean canExtract() {
		return false;
	}
}
