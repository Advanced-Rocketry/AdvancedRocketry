package zmaster587.advancedRocketry.tile.infrastructure;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TileRocketUnloader extends TileRocketLoader implements IInfrastructure, ITickable, IButtonInventory, INetworkMachine   {
	ModuleRedstoneOutputButton redstoneControl;
	RedstoneState state;

	public TileRocketUnloader() {
		super();
	}

	public TileRocketUnloader(int size) {
		super(size);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.loader.2.name";
	}


	@Override
	public void update() {

		//Move a stack of items
		if(!world.isRemote && rocket != null ) {
			boolean isAllowedToOperate = (inputstate == RedstoneState.OFF || isStateActive(inputstate, getStrongPowerForSides(world, getPos())));

			List<TileEntity> tiles = rocket.storage.getInventoryTiles();
			boolean foundStack = false;
			boolean rocketContainsNoItems = true;
			out:
				//Function returns if something can be moved
				for(TileEntity tile : tiles) {
					if(tile instanceof IInventory && !(tile instanceof TileGuidanceComputer)) {
						IInventory inv = ((IInventory)tile);
						for(int i = 0; i < inv.getSizeInventory(); i++) {
							if(!inv.getStackInSlot(i).isEmpty()) {
								rocketContainsNoItems = false;
								//Loop though this inventory's slots and find a suitible one
								for(int j = 0; j < getSizeInventory(); j++) {
									if(getStackInSlot(j).isEmpty()) {
										if(isAllowedToOperate) {
											inventory.setInventorySlotContents(j, inv.getStackInSlot(i));
											inv.setInventorySlotContents(i,ItemStack.EMPTY);
										}
										break out;
									}
									else if(!inv.getStackInSlot(i).isEmpty() && isItemValidForSlot(j, inv.getStackInSlot(i))) {
										if(isAllowedToOperate) {
											ItemStack stack2 = inv.decrStackSize(i, getStackInSlot(j).getMaxStackSize() - getStackInSlot(j).getCount());
											getStackInSlot(j).setCount(getStackInSlot(j).getCount() + stack2.getCount());
										}
										if(inv.getStackInSlot(i).isEmpty())
											break out;
										foundStack = true;
									}
								}
							}

							if(foundStack)
								break out;
						}
					}
				}

			//Update redstone state
			setRedstoneState(rocketContainsNoItems);

		}
	}
}

