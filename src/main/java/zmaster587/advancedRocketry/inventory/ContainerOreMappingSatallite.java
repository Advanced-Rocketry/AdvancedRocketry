package zmaster587.advancedRocketry.inventory;

import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.OreDictionary;

public class ContainerOreMappingSatallite extends Container {


	private SatelliteOreMapping inv;

	ContainerOreMappingSatallite(SatelliteOreMapping inv, InventoryPlayer inventoryPlayer) {
		super();
		this.inv = inv;
		inv.setSelectedSlot(-1);
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(inventoryPlayer, j1, 13 + j1 * 18, 155));
		}
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn,
			EntityPlayer player) {
		//Check if slot exists
				ItemStack stack;
				if(slot != -999)
					stack =  player.inventory.mainInventory[slot];
				else stack = null;

				if(inv != null && dragType == 0)
					//Check if anything is in the slot and set the slot value if it is
					if(stack == null) {
						inv.setSelectedSlot(-1);
					}
					else
						for(int id : OreDictionary.getOreIDs(stack)) {
							if(OreDictionary.getOreName(id).startsWith("ore") || OreDictionary.getOreName(id).startsWith("gem")) {
								inv.setSelectedSlot(slot);
							}

						}

				return stack;
	
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

	//int slot.. slot being taken from
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int p_82846_2_)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stackInSlot = slot.getStack();
			itemstack = stackInSlot.copy();

			//merges the item into player inventory since its in the tileEntity
			if (p_82846_2_ <= 1) {
				if (!this.mergeItemStack(stackInSlot, 0, 35, true)) {
					return null;
				}
			}
			//places it into the tileEntity is possible since its in the player inventory
			else if (!this.mergeItemStack(stackInSlot, 0, 0, false)) {
				return null;
			}


			if (stackInSlot.stackSize == 0)
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
