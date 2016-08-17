package zmaster587.advancedRocketry.inventory.modules;

import java.util.List;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import zmaster587.advancedRocketry.inventory.ContainerModular;
import zmaster587.libVulpes.gui.SlotLimitedItem;

public class ModuleLimitedSlotArray extends ModuleSlotArray {

	public ModuleLimitedSlotArray(int offsetX, int offsetY,
			IInventory container, int startSlot, int endSlot) {
		super(offsetX, offsetY, container, startSlot, endSlot);
	}

	@Override
	public List<Slot> getSlots(Container container) {	

		//Get the reference to the container
		this.container = (ContainerModular)container;

		for(int i = 0; i + startSlot < endSlot; i++) {
			Slot slot;
			slot = new SlotLimitedItem(inventory, i+startSlot, offsetX + 18* (i % 9), offsetY + 18*(i/9));

			slotList.add(slot);
		}
		return slotList;
	}
}
