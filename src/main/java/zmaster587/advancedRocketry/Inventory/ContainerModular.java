package zmaster587.advancedRocketry.Inventory;

import java.util.List;

import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.item.ItemData;
import zmaster587.advancedRocketry.tile.multiblock.TileEntityMultiPowerConsumer;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.advancedRocketry.util.DataStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class ContainerModular extends Container {

	List<ModuleBase> modules;
	int numSlots;
	IModularInventory modularInventory;
	
	
	public ContainerModular(EntityPlayer playerInv, List<ModuleBase> modules, IModularInventory modulularInv, boolean includePlayerInv) {
		this.modularInventory = modulularInv;
		this.modules = modules;
		numSlots = 0;

		for(ModuleBase module : modules)
			for(Slot slot : module.getSlots(this)) {
				addSlotToContainer(slot);
				numSlots++;
			}

		if(includePlayerInv) {
			// Player inventory
			for (int i1 = 0; i1 < 3; i1++) {
				for (int l1 = 0; l1 < 9; l1++) {
					addSlotToContainer(new Slot(playerInv.inventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 89 + i1 * 18));
				}
			}
		}

		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(playerInv.inventory, j1, 8 + j1 * 18, 147));
		}
	}
	
	public Slot addSlotToContainer(Slot slot) {
		return super.addSlotToContainer(slot);
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting crafter) {
		super.addCraftingToCrafters(crafter);

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			//for(int i = 0; i < module.numChangesToSend(); i++) {
			module.sendInitialChanges(this, crafter, moduleIndex);

			moduleIndex+= module.numberOfChangesToSend();
			//}
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			for(int i = 0; i < module.numberOfChangesToSend(); i++) {
				if(module.isUpdateRequired(i)) {
					
					for (int j = 0; j < this.crafters.size(); ++j) {
						module.sendChanges(this, ((ICrafting)this.crafters.get(j)), moduleIndex, i);
					}
				}
				moduleIndex++;
			}
		}
	}

	@Override
	public void updateProgressBar(int slot, int value) {
		super.updateProgressBar(slot, value);

		int moduleIndex = 0;

		for(ModuleBase module : modules) {
			if(slot - moduleIndex < module.numberOfChangesToSend() && slot - moduleIndex >= 0) {
				module.onChangeRecieved(slot - moduleIndex, value);
			}
			moduleIndex += module.numberOfChangesToSend();
		}

	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {

		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			//Try to merge to player inventory
			if (slotId < numSlots)
			{
				if (!this.mergeItemStack(itemstack1, numSlots, this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, slotId, false))
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


	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return modularInventory.canInteractWithContainer(player);
	}
}
