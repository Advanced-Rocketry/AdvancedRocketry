package zmaster587.advancedRocketry.Inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.tile.multiblock.TileEntityBlastFurnace;
import zmaster587.libVulpes.gui.IlimitedItemSlotEntity;
import zmaster587.libVulpes.gui.SlotLimitedItem;
import zmaster587.libVulpes.gui.SlotMachineOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBlastFurnace extends Container {
	private int prevProgress, prevHeat, prevMaxHeat;
	TileEntityBlastFurnace tile;

	ContainerBlastFurnace(InventoryPlayer inventoryPlayer, TileEntityBlastFurnace tile) {
		super();
		this.tile = tile;
		for(int i=0; i < 6; i++) {
			addSlotToContainer(new SlotLimitedItem(tile, i, 8 + 18*(i % 2), 19 + 18*(i/2), (IlimitedItemSlotEntity) tile));}
		for(int i=0; i < 6; i++) {
			addSlotToContainer(new SlotLimitedItem(tile, i+6, 54 + 18*(i % 2), 19 + 18*(i/2), (IlimitedItemSlotEntity) tile));
		}
		for(int i=0; i < 6; i++) {
			addSlotToContainer(new SlotMachineOutput(tile, i+12, 136 + 18*(i % 2), 19 + 18*(i/2)));
		}

		// Player inventory
		for (int i1 = 0; i1 < 3; i1++) {
			for (int l1 = 0; l1 < 9; l1++) {
				addSlotToContainer(new Slot(inventoryPlayer, l1 + i1 * 9 + 9, 8 + l1 * 18, 89 + i1 * 18));
			}
		}
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(inventoryPlayer, j1, 8 + j1 * 18, 147));
		}

		prevMaxHeat = 0;
		prevProgress = 0;
		prevHeat = 0;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tile.isUseableByPlayer(entityplayer);
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
		ItemStack t = super.slotClick(par1, par2, par3, par4EntityPlayer);
		tile.onInventoryUpdate();
		return t;
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		if(tile.getMaxHeat() != prevMaxHeat) {
			prevMaxHeat = tile.getMaxHeat();
			for (int j = 0; j < this.crafters.size(); ++j)
			{
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, 2, prevMaxHeat);
			}
		}
		if(tile.getProgress() != prevProgress) {
			prevProgress = tile.getProgress();
			for (int j = 0; j < this.crafters.size(); ++j)
			{
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, 0, prevProgress);
			}
		}
		if(tile.getHeat() != prevHeat) {
			prevHeat = tile.getHeat();
			for (int j = 0; j < this.crafters.size(); ++j)
			{
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, 1, prevHeat);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		if(id == 0)
			tile.setProgress(value);
		if(id == 1)
			tile.setHeat(value);
		if(id == 2)
			tile.setMaxHeat(value);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		//null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {

			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			//merges the item into player inventory since its in the tileEntity
			if (slot < 18) {
				if (!this.mergeItemStack(stackInSlot, 18, 38, true)) {
					return null;
				}
			}
			//placing it into the tileEntity is possible since its in the player inventory
			//check to make sure it's valid for the slot
			else if ((tile.isItemValidForSlot(0, stack) && !this.mergeItemStack(stackInSlot, 0, 6, false)) ||
					(tile.isItemValidForSlot(6, stack) && !this.mergeItemStack(stackInSlot, 6,12, false)))
					return null;

			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}

		return stack;
	}
}
