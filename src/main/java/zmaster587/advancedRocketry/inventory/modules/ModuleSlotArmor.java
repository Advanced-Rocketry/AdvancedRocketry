package zmaster587.advancedRocketry.inventory.modules;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import zmaster587.advancedRocketry.inventory.ContainerModular;
import zmaster587.advancedRocketry.inventory.slot.SlotArmor;

public class ModuleSlotArmor extends ModuleBase {

	int startSlot, endSlot;
	EntityPlayer player;
	ContainerModular container; //TODO: fix this OOPs (Object Oriented Poops)

	public ModuleSlotArmor(int offsetX, int offsetY, EntityPlayer player) {
		super(offsetX, offsetY);
		this.player = player;
		startSlot = 36;
		endSlot = 40;
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
			final int k = (endSlot - startSlot) - i - 1;
			slot = new SlotArmor(player.inventory, k + startSlot, offsetX + 18 * (i / 9), offsetY + 18*(i%9), player, 3- k);


			slotList.add(slot);
		}
		return slotList;
	}
}
