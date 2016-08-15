package zmaster587.advancedRocketry.inventory.modules;

public interface IGuiCallback {
	/**
	 * Some Modules alert the sending host for callbacks
	 * @param module module sending a callback
	 */
	void onModuleUpdated(ModuleBase module);
}
