package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.cable.EnergyNetwork;
import zmaster587.advancedRocketry.cable.HandlerCableNetwork;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.cap.ForgePowerCapability;
import zmaster587.libVulpes.cap.TeslaHandler;

public class TileEnergyPipe extends TilePipe implements IUniversalEnergy {
	
	public TileEnergyPipe() {
		super(AdvancedRocketryTileEntityType.TILE_POWER_PIPE);
	}
	
	@Override
	public boolean canExtract(Direction dir, TileEntity e) {
		LazyOptional<IEnergyStorage> cap = e.getCapability(CapabilityEnergy.ENERGY, dir);
		return cap.isPresent() && cap.orElse(null).canExtract() && !(e instanceof TileEnergyPipe);
	}

	@Override
	public boolean canInject(Direction dir, TileEntity e) {
		LazyOptional<IEnergyStorage> cap = e.getCapability(CapabilityEnergy.ENERGY, dir);
		return cap.isPresent() && cap.orElse(null).canReceive() && !(e instanceof TileEnergyPipe);
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {

		if(capability == CapabilityEnergy.ENERGY )
			return LazyOptional.of(() -> new ForgePowerCapability(this)).cast();
		
		return super.getCapability(capability, facing);
	}
	
	public HandlerCableNetwork getNetworkHandler() {
		return NetworkRegistry.energyNetwork;
	}
	
	protected void attemptLink(Direction dir, TileEntity tile) {
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
