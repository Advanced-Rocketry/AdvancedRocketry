//Handles all rocket functionality
//include, destination setting

package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.entity.EntityRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerRocket extends Container {

	
	EntityRocket rocket;
	
	public ContainerRocket(EntityPlayer player, EntityRocket rocket, int startSlot, int endSlot) {
		super();
		
		this.rocket = rocket;
		
		//Used for variable number inventories
		for(int i = 0; i + startSlot < endSlot; i++) {
			addSlotToContainer(new Slot(rocket.storage, i+startSlot, 8 + 18* (i % 9), 8 + 18*(i/9)));
		}


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
		return rocket != null && !rocket.isDead && player.getDistanceToEntity(rocket) < 64;
	}
}
