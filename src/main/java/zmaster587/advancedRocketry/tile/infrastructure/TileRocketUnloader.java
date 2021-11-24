package zmaster587.advancedRocketry.tile.infrastructure;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import java.util.List;

public class TileRocketUnloader extends TileRocketLoader implements IInfrastructure, ITickableTileEntity, IButtonInventory, INetworkMachine   {

	public TileRocketUnloader() {
		super(AdvancedRocketryTileEntityType.TILE_ROCKET_UNLOADER);
	}

	public TileRocketUnloader(int size) {
		super(AdvancedRocketryTileEntityType.TILE_ROCKET_UNLOADER, size);
		inventory.setCanInsertSlot(0, false);
		inventory.setCanInsertSlot(1, false);
		inventory.setCanInsertSlot(2, false);
		inventory.setCanInsertSlot(3, false);
		inventory.setCanExtractSlot(0, true);
		inventory.setCanExtractSlot(1, true);
		inventory.setCanExtractSlot(2, true);
		inventory.setCanExtractSlot(3, true);
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.rocketunloader";
	}


	@Override
	public void tick() {
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

