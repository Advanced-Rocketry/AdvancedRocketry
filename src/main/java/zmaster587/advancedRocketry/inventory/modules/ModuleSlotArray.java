package zmaster587.advancedRocketry.inventory.modules;

import java.util.List;

import zmaster587.advancedRocketry.inventory.ContainerModular;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ModuleSlotArray extends ModuleBase {

	int startSlot, endSlot;
	IInventory inventory;
	ContainerModular container; //TODO: fix this OOPs (Object Oriented Poops)

	public ModuleSlotArray(int offsetX, int offsetY, IInventory container, int startSlot, int endSlot) {
		super(offsetX, offsetY);
		this.startSlot = startSlot;
		this.endSlot = endSlot;
		this.inventory = container;
	}

	public void setSlotBounds(int a, int b) {

		for(int i = startSlot; i < endSlot; i++) {
			container.inventorySlots.remove(i);
			container.inventoryItemStacks.remove(i);
		}
		startSlot = a;
		endSlot = b;
		
		for(Slot slot : getSlots(container)) {
			container.addSlotToContainer(slot);
		}
	}

	@Override
	public List<Slot> getSlots(Container container) {	

		//Get the reference to the container
		this.container = (ContainerModular)container;

		for(int i = 0; i + startSlot < endSlot; i++) {
			Slot slot;
			slot = new Slot(inventory, i+startSlot, offsetX + 18* (i % 9), offsetY + 18*(i/9));

			slotList.add(slot);
		}
		return slotList;
	}
}
