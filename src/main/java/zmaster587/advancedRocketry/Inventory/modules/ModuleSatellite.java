package zmaster587.advancedRocketry.Inventory.modules;

import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ModuleSatellite extends ModuleSlotArray {

	SatelliteBase satellite;
	IconResource satelliteSlot = TextureResources.idChip;
	boolean needsUpdate = false;

	public ModuleSatellite(int offsetX, int offsetY, IInventory container,
			int slot) {
		super(offsetX, offsetY, container, slot, slot + 1);

	}

	@Override
	public void renderBackground(GuiContainer gui, int x, int y,
			FontRenderer font) {
		
		for(Slot slot : slotList) {
			gui.drawTexturedModalRect(x + slot.xDisplayPosition - 1, y + slot.yDisplayPosition - 1, satelliteSlot.getxLoc(), satelliteSlot.getyLoc(), satelliteSlot.getxSize(), satelliteSlot.getySize());
		}
	}
	
	@Override
	public int numberOfChangesToSend() {		
		if(satellite != null)
			return satellite.numberChangesToSend();

		return 0;
	}

	@Override
	public void sendChanges(Container container, ICrafting crafter,
			int variableId, int localId) {
		if(satellite != null)
			satellite.sendChanges(container, crafter, variableId, localId);
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		if(satellite != null) {
			satellite.onChangeRecieved(slot, value);
		}
	}
	
	@Override
	protected boolean needsUpdate(int localId) {
		if(satellite != null)
			return satellite.isUpdateRequired(localId);
		
		return super.isUpdateRequired(localId);
	}

	public void setSatellite(SatelliteBase satellite) {
		this.satellite = satellite;
	}
	
	public SatelliteBase getSatellite() {
		return this.satellite;
	}

}
