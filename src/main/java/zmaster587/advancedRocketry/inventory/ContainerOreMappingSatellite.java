package zmaster587.advancedRocketry.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerOreMappingSatellite extends Container {


	private SatelliteOreMapping inv;

	ContainerOreMappingSatellite(SatelliteOreMapping inv, InventoryPlayer inventoryPlayer) {
		super();
		this.inv = inv;
		inv.setSelectedSlot(-1);
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(inventoryPlayer, j1, 13 + j1 * 18, 155));
		}
	}

	@Override
	@Nonnull
	public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn,
			EntityPlayer player) {
		//Check if slot exists
				ItemStack stack;
				if(slot != -999)
					stack =  player.inventory.getStackInSlot(slot);
				else stack = ItemStack.EMPTY;

				if(inv != null && dragType == 0)
					//Check if anything is in the slot and set the slot value if it is
					if(stack.isEmpty()) {
						inv.setSelectedSlot(-1);
					}
					else
						for(int id : OreDictionary.getOreIDs(stack)) {
							if(OreDictionary.getOreName(id).startsWith("ore") || OreDictionary.getOreName(id).startsWith("gem") || OreDictionary.getOreName(id).startsWith("dust")) {
								inv.setSelectedSlot(slot);
							}

						}

				return stack;
	
	}

	@Override
	public boolean canInteractWith(@Nullable EntityPlayer p_75145_1_) {
		return true;
	}

	//int slot.. slot being taken from
	@Override
	@Nonnull
	public ItemStack transferStackInSlot(EntityPlayer player, int p_82846_2_)
	{
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(p_82846_2_);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stackInSlot = slot.getStack();
			itemstack = stackInSlot.copy();

			//merges the item into player inventory since its in the tileEntity
			if (p_82846_2_ <= 1) {
				if (!this.mergeItemStack(stackInSlot, 0, 35, true)) {
					return ItemStack.EMPTY;
				}
			}
			//places it into the tileEntity is possible since its in the player inventory
			else if (!this.mergeItemStack(stackInSlot, 0, 0, false)) {
				return ItemStack.EMPTY;
			}


			if (stackInSlot.getCount() == 0)
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}

}
