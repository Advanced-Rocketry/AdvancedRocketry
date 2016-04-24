package zmaster587.advancedRocketry.inventory.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IButtonInventory {

	/**
	 * Called on the client when a user presses a button
	 * @param buttonId id of the button pressed
	 */
	@SideOnly(Side.CLIENT)
	public void onInventoryButtonPressed(int buttonId);
	
}
