package zmaster587.advancedRocketry.Inventory;

import zmaster587.libVulpes.gui.SlotMachineOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class ContainerVariableSlotNumber extends Container {

	IInventory container;
	int numSlots;

	public ContainerVariableSlotNumber(EntityPlayer player, IInventory container, int startSlot, int endSlot) {


		//Used for variable number inventories
		for(int i = 0; i + startSlot < endSlot; i++) {
			Slot slot;
			if(!(container instanceof ISidedInventory) ||  ((ISidedInventory)container).canInsertItem(i, container.getStackInSlot(i), ForgeDirection.UNKNOWN.ordinal()))
				slot = new Slot(container, i+startSlot, 8 + 18* (i % 9), 17 + 18*(i/9));
			else
				slot = new SlotMachineOutput(player, container, i+startSlot, 8 + 18* (i % 9), 17 + 18*(i/9));
			addSlotToContainer(slot);
		}


		this.container = container;
		numSlots = endSlot - startSlot;

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlotToContainer(new Slot(player.inventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 89 + i1 * 18));
			}
		}

		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(player.inventory, j1, 8 + j1 * 18, 147));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		TileEntity tile = (TileEntity)container;
		return player.getDistance(tile.xCoord, tile.yCoord, tile.zCoord) < 64;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotNum)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(slotNum);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotNum < numSlots)
			{
				if (!this.mergeItemStack(itemstack1, numSlots, this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, numSlots, false))
			{
				return null;
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack)null);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}
}
