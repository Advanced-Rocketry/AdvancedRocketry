package zmaster587.advancedRocketry.Inventory.modules;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ModuleProgress extends ModuleBase {

	ProgressBarImage progressBar;
	IProgressBar progress;
	int prevProgress;
	int prevTotalProgress;
	int id;
	
	public ModuleProgress(int offsetX, int offsetY, int id, ProgressBarImage progressBar, IProgressBar progress) {
		super(offsetX, offsetY);
		this.progressBar = progressBar;
		this.progress = progress;
		this.id = id;
	}

	@Override
	protected boolean needsUpdate(int localId) {
		switch(localId) {
		case 0:
			return prevProgress != progress.getProgress(id);
		case 1:
			return prevTotalProgress != progress.getTotalProgress(id);
		}
		return false;
	}
	
	@Override
	protected void updatePreviousState(int localId) {
		switch(localId) {
		case 0:
			prevProgress = progress.getProgress(id);
			break;
		case 1:
			prevTotalProgress = progress.getTotalProgress(id);
		}
	}
	
	@Override
	public void sendChanges(Container container, ICrafting crafter, int variableId, int localId) {
		switch(localId) {
		case 0:
			crafter.sendProgressBarUpdate(container, variableId, progress.getProgress(id));
			break;
		case 1:
			crafter.sendProgressBarUpdate(container, variableId, progress.getTotalProgress(id));
		}
	}
	
	@Override
	public void onChangeRecieved(int slot, int value) {
		switch(slot) {
		case 0:
			progress.setProgress(id, value);
			break;
		case 1:
			progress.setTotalProgress(id, value);
		}
	}
	
	@Override
	public int numberOfChangesToSend() {
		return 2;
	}
	
	protected float getProgress() {
		return progress.getNormallizedProgress(id);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, FontRenderer font) {
		progressBar.renderProgressBar(x + offsetX, y + offsetY,getProgress(), gui);
	}
}
