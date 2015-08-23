package zmaster587.advancedRocketry.Inventory.modules;

public interface IToggleButton extends IButtonInventory {
	/**
	 * Called when a module is toggled
	 * @param module module that was toggled
	 */
	public void stateUpdated(ModuleBase module);
}
