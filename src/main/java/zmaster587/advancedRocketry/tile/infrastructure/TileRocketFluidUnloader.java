package zmaster587.advancedRocketry.tile.infrastructure;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import java.util.List;

public class TileRocketFluidUnloader extends TileRocketFluidLoader implements IInfrastructure, ITickableTileEntity,  IButtonInventory, INetworkMachine  {

	public TileRocketFluidUnloader() {
		super(AdvancedRocketryTileEntityType.TILE_FLUID_UNLOADER);
	}

	public TileRocketFluidUnloader(int size) {
		super(AdvancedRocketryTileEntityType.TILE_FLUID_UNLOADER, size);
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.funloader";
	}


	@Override
	public void tick() {

		//Move a stack of items
		if( !world.isRemote && rocket != null ) {

			boolean isAllowToOperate = (inputstate == RedstoneState.OFF || isStateActive(inputstate, getStrongPowerForSides(world, getPos())));

			List<TileEntity> tiles = rocket.storage.getFluidTiles();
			boolean rocketContainsItems = false;
			//Function returns if something can be moved
			for(TileEntity tile : tiles) {
				IFluidHandler handler = (IFluidHandler)tile;

				if(handler.drain( 1, FluidAction.SIMULATE) != null)
					rocketContainsItems = true;

				if(isAllowToOperate) {
					FluidStack stack = fluidTank.getFluid();
					if(stack == null) {
						this.fill(handler.drain(fluidTank.getCapacity(), FluidAction.EXECUTE), FluidAction.EXECUTE);
					}
					else {
						stack = stack.copy();
						stack.setAmount(fluidTank.getCapacity() - fluidTank.getFluidAmount());

						if(stack.getAmount() != 0) {
							this.fill(handler.drain( stack, FluidAction.EXECUTE), FluidAction.EXECUTE);
						}
					}
				}
			}

			//Update redstone state
			setRedstoneState(!rocketContainsItems);

		}
	}

}
