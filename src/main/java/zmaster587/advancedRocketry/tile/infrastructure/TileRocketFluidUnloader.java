package zmaster587.advancedRocketry.tile.infrastructure;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.FluidStack;
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

		//Move a stack of items
		if( !world.isRemote && rocket != null ) {

			boolean isAllowToOperate = (inputstate == RedstoneState.OFF || isStateActive(inputstate, getStrongPowerForSides(world, getPos())));

			List<TileEntity> tiles = rocket.storage.getFluidTiles();
			boolean rocketContainsItems = false;
			//Function returns if something can be moved
			for(TileEntity tile : tiles) {
				IFluidHandler handler = (IFluidHandler)tile;

				if(handler.drain( 1, false) != null)
					rocketContainsItems = true;

				if(isAllowToOperate) {
					FluidStack stack = fluidTank.getFluid();
					if(stack == null) {
						this.fill(handler.drain(fluidTank.getCapacity(), true), true);
					}
					else {
						stack = stack.copy();
						stack.amount = fluidTank.getCapacity() - fluidTank.getFluidAmount();

						if(stack.amount != 0) {
							this.fill(handler.drain( stack, true), true);
						}
					}
				}
			}

			//Update redstone state
			setRedstoneState(!rocketContainsItems);

		}
	}

}
