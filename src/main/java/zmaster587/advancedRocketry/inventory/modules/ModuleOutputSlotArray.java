package zmaster587.advancedRocketry.inventory.modules;

import java.util.List;

import zmaster587.libVulpes.gui.SlotMachineOutput;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ModuleOutputSlotArray extends ModuleSlotArray {

	public ModuleOutputSlotArray(int offsetX, int offsetY,
			IInventory container, int startSlot, int endSlot) {
		super(offsetX, offsetY, container, startSlot, endSlot);
	}

	
	@Override
	public List<Slot> getSlots(Container container) {
		//this.container = container;
		
		for(int i = 0; i + startSlot < endSlot; i++) {
			slotList.add(new SlotMachineOutput(inventory, i+startSlot, offsetX + 18* (i % 9), offsetY + 18*(i/9)));
		}
		return slotList;
	}
}
