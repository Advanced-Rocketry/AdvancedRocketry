package zmaster587.advancedRocketry.tile.infrastructure;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TileRocketFluidUnloader extends TileRocketFluidLoader implements IInfrastructure, IButtonInventory, INetworkMachine {

	public TileRocketFluidUnloader() {
		super();
	}

	public TileRocketFluidUnloader(int size) {
		super(size);

	}

	@Override
	public String getModularInventoryName() {
		return "tile.loader.4.name";
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		//Move a stack of items
		if(rocket != null ) {
			boolean isAllowedToOperate = (inputstate == RedstoneState.OFF || isStateActive(inputstate, getStrongPowerForSides(worldObj, xCoord,yCoord,zCoord)));

			List<TileEntity> tiles = rocket.storage.getFluidTiles();
			boolean rocketContainsItems = false;
			//Function returns if something can be moved
			for(TileEntity tile : tiles) {
				IFluidHandler handler = (IFluidHandler)tile;

				if(handler.drain(ForgeDirection.DOWN, 1, false) != null)
					rocketContainsItems = true;

				if(isAllowedToOperate) {
					FluidStack stack = fluidTank.getFluid();
					if(stack == null) {
						this.fill(ForgeDirection.UNKNOWN, handler.drain(ForgeDirection.DOWN, fluidTank.getCapacity(), true), true);
					}
					else {
						stack = stack.copy();
						stack.amount = fluidTank.getCapacity() - fluidTank.getFluidAmount();

						if(stack.amount != 0) {
							this.fill(ForgeDirection.UNKNOWN, handler.drain(ForgeDirection.DOWN, stack, true), true);
						}
					}
				}
			}

			//Update redstone state
			setRedstoneState(!rocketContainsItems);

		}
	}
}
