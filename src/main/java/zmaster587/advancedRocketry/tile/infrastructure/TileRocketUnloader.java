package zmaster587.advancedRocketry.tile.infrastructure;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TileRocketUnloader extends TileRocketLoader implements IInfrastructure, IButtonInventory, INetworkMachine {



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
	public void updateEntity() {
		super.updateEntity();

		//Move a stack of items
		if(!worldObj.isRemote && rocket != null ) {
			boolean isAllowedToOperate = (inputstate == RedstoneState.OFF || isStateActive(inputstate, getStrongPowerForSides(worldObj, xCoord,yCoord,zCoord)));

			List<TileEntity> tiles = rocket.storage.getInventoryTiles();
			boolean foundStack = false;
			boolean rocketContainsNoItems = true;
			out:
				//Function returns if something can be moved
				for(TileEntity tile : tiles) {
					if(tile instanceof IInventory && !(tile instanceof TileGuidanceComputer)) {
						IInventory inv = ((IInventory)tile);
						for(int i = 0; i < inv.getSizeInventory(); i++) {
							if(inv.getStackInSlot(i) != null) {
								rocketContainsNoItems = false;
								//Loop though this inventory's slots and find a suitible one
								for(int j = 0; j < getSizeInventory(); j++) {
									if(getStackInSlot(j) == null) {
										if(isAllowedToOperate) {
											inventory.setInventorySlotContents(j, inv.getStackInSlot(i));
											inv.setInventorySlotContents(i,null);
										}
										break out;
									}
									else if(inv.getStackInSlot(i) != null && isItemValidForSlot(j, inv.getStackInSlot(i))) {
										if(isAllowedToOperate) {
											ItemStack stack2 = inv.decrStackSize(i, getStackInSlot(j).getMaxStackSize() - getStackInSlot(j).stackSize);
											getStackInSlot(j).stackSize += stack2.stackSize;
										}
										if(inv.getStackInSlot(i) == null)
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
