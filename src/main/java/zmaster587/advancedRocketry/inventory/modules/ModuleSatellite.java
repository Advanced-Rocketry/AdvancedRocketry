package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.util.IconResource;

public class ModuleSatellite extends ModuleSlotArray {

	SatelliteBase satellite;
	IconResource satelliteSlot = TextureResources.idChip;
	boolean needsUpdate = false;

	public ModuleSatellite(int offsetX, int offsetY, IInventory container,
			int slot) {
		super(offsetX, offsetY, container, slot, slot + 1);

	}

	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		
		for(Slot slot : slotList) {
			gui.drawTexturedModalRect(x + slot.xPos - 1, y + slot.yPos - 1, satelliteSlot.getxLoc(), satelliteSlot.getyLoc(), satelliteSlot.getxSize(), satelliteSlot.getySize());
		}
	}
	
	@Override
	public int numberOfChangesToSend() {		
		if(satellite != null)
			return satellite.numberChangesToSend();

		return 0;
	}

	@Override
	public void sendChanges(Container container, IContainerListener crafter,
			int variableId, int localId) {
		if(satellite != null)
			satellite.sendChanges(container, crafter, variableId, localId);
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		if(satellite != null) {
			satellite.onChangeReceived(slot, value);
		}
	}
	
	@Override
	public boolean needsUpdate(int localId) {
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
