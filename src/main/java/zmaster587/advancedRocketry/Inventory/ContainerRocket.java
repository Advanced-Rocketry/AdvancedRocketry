//Handles all rocket functionality
//include, destination setting

package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.entity.EntityRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class ContainerRocket extends ContainerVariableSlotNumber {

	
	EntityRocket rocket;
	
	public ContainerRocket(EntityPlayer player, EntityRocket rocket, int startSlot, int endSlot) {
		super(player, (IInventory)rocket.storage, startSlot, endSlot);
		
		this.rocket = rocket;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return rocket != null && !rocket.isDead && player.getDistanceToEntity(rocket) < 64;
	}
}
