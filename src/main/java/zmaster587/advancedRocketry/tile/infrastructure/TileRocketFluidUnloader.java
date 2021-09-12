package zmaster587.advancedRocketry.tile.infrastructure;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import java.util.List;

public class TileRocketFluidUnloader extends TileRocketFluidLoader implements IInfrastructure, ITickable,  IButtonInventory, INetworkMachine  {

	public TileRocketFluidUnloader() {
		super();
		this.setOutputOnly(true);
	}

	public TileRocketFluidUnloader(int size) {
		super(size);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.loader.4.name";
	}


	@Override
	public void update() {
		//Move fluids
		if(!world.isRemote && rocket != null) {

			boolean isAllowToOperate = (inputstate == RedstoneState.OFF || isStateActive(inputstate, getStrongPowerForSides(world, getPos())));

			List<TileEntity> tiles = rocket.storage.getFluidTiles();
			boolean rocketFluidFull = false;

			//Function returns if something can be moved
			for(TileEntity tile : tiles) {
				IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

				//See if we have anything to fill because redstone output
				FluidStack rocketFluid = handler.drain(1, false);
				if(rocketFluid == null || handler.fill(rocketFluid, false) > 0)
					rocketFluidFull = true;

				if(isAllowToOperate) {
					boolean shouldOperate;
					if (getFluidTank().getFluid() != null)
						shouldOperate = getFluidTank().fill(handler.drain(new FluidStack(getFluidTank().getFluid(), getFluidTank().getCapacity() - getFluidTank().getFluidAmount()), false), false) > 0;
					else
						shouldOperate = getFluidTank().fill(handler.drain(getFluidTank().getCapacity(), false), false) > 0;

					if (shouldOperate)
						getFluidTank().fill(handler.drain(getFluidTank().getCapacity() - getFluidTank().getFluidAmount(), true), true);
				}
			}

			//Update redstone state
			setRedstoneState(!rocketFluidFull);

		}
	}

}
