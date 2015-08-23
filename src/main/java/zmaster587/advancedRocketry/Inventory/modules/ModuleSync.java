package zmaster587.advancedRocketry.Inventory.modules;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

public class ModuleSync extends ModuleBase {

	IDataSync tile;
	int id, prevData;
	public ModuleSync(int id, IDataSync tile) {
		super(0, 0);
		this.tile = tile;
		this.id = id;
		prevData = -1;
	}
	
	@Override
	protected boolean needsUpdate(int localId) {
		return tile.getData(id) != prevData;
	}
	
	@Override
	protected void updatePreviousState(int localId) {
		prevData = tile.getData(id);
	}
	
	@Override
	public void onChangeRecieved(int slot, int value) {
		tile.setData(id, value);
	}
	
	@Override
	public void sendChanges(Container container, ICrafting crafter,
			int variableId, int localId) {
		crafter.sendProgressBarUpdate(container, variableId, tile.getData(id));
	}
	
	@Override
	public int numberOfChangesToSend() {
		return 1;
	}
}
