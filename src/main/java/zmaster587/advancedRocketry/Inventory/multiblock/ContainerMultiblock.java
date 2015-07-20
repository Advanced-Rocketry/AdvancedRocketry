package zmaster587.advancedRocketry.Inventory.multiblock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.Inventory.ContainerPowered;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMultiblock extends ContainerPowered {
	private TileMultiBlockMachine tile;
	private int prevProgress, prevOpTime;
	private boolean enabled;
	private int prevProgressId, prevOpTimeId, enabledId;
	
	public ContainerMultiblock(InventoryPlayer inventoryPlayer, TileMultiBlockMachine tile) {
		super(inventoryPlayer,tile.getBatteries());
		this.tile = tile;
		
		//Get slot ids
		prevProgressId = nextSlot();
		prevOpTimeId = nextSlot();
		enabledId = nextSlot();
		
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
		prevOpTime = -1;
		prevProgress = -1;
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting crafter) {
		super.addCraftingToCrafters(crafter);
		prevProgress = tile.getProgress();
		prevOpTime = tile.getTotalProgress();
		enabled = tile.getMachineEnabled();
		
		crafter.sendProgressBarUpdate(this, prevProgressId, prevProgress);
		crafter.sendProgressBarUpdate(this, prevOpTimeId, prevOpTime);
		crafter.sendProgressBarUpdate(this, enabledId, enabled ? 1 : 0);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		
		if(prevProgress != tile.getProgress()) {
			prevProgress = tile.getProgress();
			
			for (int j = 0; j < this.crafters.size(); ++j)
			{
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, prevProgressId, prevProgress);
			}
		}
		
		if(prevOpTime != tile.getTotalProgress()) {
			prevOpTime = tile.getTotalProgress();
			
			for (int j = 0; j < this.crafters.size(); ++j)
			{
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, prevOpTimeId, prevOpTime);
			}
		}
		if(enabled != tile.getMachineEnabled()) {
			enabled = tile.getMachineEnabled();
			
			for (int j = 0; j < this.crafters.size(); ++j)
			{
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, enabledId, enabled ? 1 : 0);
			}
		}
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
		super.updateProgressBar(id, value);
		
		if(id == prevOpTimeId)
			tile.setTotalOperationTime(value);
		else if(id == prevProgressId)
			tile.setProgress(value);
		else if(id == enabledId) {
			tile.setMachineEnabled(value == 1);
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;//tile.isUsableByPlayer(entityplayer);
	}
	
	//int slot.. slot being taken from
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		/*ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		//null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {

			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			
			//merges the item into player inventory since its in the tileEntity
			if (slot < 3) {
				if (!this.mergeItemStack(stackInSlot, 3, 38, true)) {
					return null;
				}
			}
			//placing it into the tileEntity is possible since its in the player inventory
			//check to make sure it's valid for the slot
			else if ((tile.isItemValidForSlot(0, stack) && !this.mergeItemStack(stackInSlot, 0, 1, false)) ||
					(tile.isItemValidForSlot(1, stack) && !this.mergeItemStack(stackInSlot, 1, 2, false)))
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

		return stack;*/
		return null;
	}
}
