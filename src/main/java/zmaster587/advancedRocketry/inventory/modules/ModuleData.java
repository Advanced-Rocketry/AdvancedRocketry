package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.util.IconResource;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public class ModuleData extends ModuleBase implements IButtonInventory {

	static final int barYSize = 38;
	static final int barXSize = 6;
	static final int textureOffsetX = 0;
	static final int textureOffsetY = 215;

	DataStorage[] data;
	int[] prevData;
	int prevDataType;
	int slot;
	IDataInventory chipStorage;
	IconResource icon = zmaster587.advancedRocketry.inventory.TextureResources.ioSlot;
	ModuleButton buttonStore, buttonLoad;

	public ModuleData(int offsetX, int offsetY, int slot, IDataInventory chipStorage, DataStorage ... data) {
		super(offsetX, offsetY);
		this.data = data;
		prevData = new int[data.length];
		this.chipStorage = chipStorage;
		this.slot = slot;

		buttonStore = new ModuleButton(10 + offsetX, offsetY, "", this, TextureResources.buttonDown, "Store to Chip", 16, 10);
		buttonLoad = new ModuleButton(12 + offsetX, 36 + offsetY, "", this, TextureResources.buttonLeft, "Store to buffer", 10, 16);
	}

	@Override
	public List<AbstractButton> addButtons(int x, int y) {
		List<AbstractButton> list = buttonLoad.addButtons(x, y);
		list.addAll(buttonStore.addButtons(x, y));
		return list;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(buttonId == buttonStore) {
			chipStorage.storeData(slot);
		}
		else if(buttonId == buttonLoad) {
			chipStorage.loadData(slot);
		}
	}

	@Override
	public void actionPerform(AbstractButton button) {
		buttonStore.actionPerform(button);
		buttonLoad.actionPerform(button);
	}

	@Override
	public List<Slot> getSlots(Container container) {
		slotList.add(new SlotData(chipStorage, slot, offsetX + 10, offsetY + 16));
		return slotList;
	}

	@Override
	public int numberOfChangesToSend() {
		return data.length + 1;
	}

	@Override
	public boolean needsUpdate(int localId) {
		if(localId < data.length)
			return data[localId].getData() != prevData[localId];
		return data[0].getDataType().ordinal() != prevDataType;
	}

	@Override
	protected void updatePreviousState(int localId) {
		if(localId < data.length) 
			prevData[localId] = data[localId].getData();

		else
			prevDataType = data[0].getDataType().ordinal();
	}

	@Override
	public void sendChanges(Container container, IContainerListener crafter,
			int variableId, int localId) {
		if(localId < data.length)
			crafter.sendWindowProperty(container, variableId, data[localId].getData());
		else
			crafter.sendWindowProperty(container, variableId, data[0].getDataType().ordinal());
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		if(slot < data.length)
			data[slot].setData(value, DataStorage.DataType.UNDEFINED);
		else
			data[0].setDataType(DataStorage.DataType.values()[value]);
	}

	@OnlyIn(value=Dist.CLIENT)
	@Override
	public void renderForeground(MatrixStack matrix, int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, ContainerScreen<? extends Container> gui, FontRenderer font) {
		buttonLoad.renderForeground(matrix, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);
		buttonStore.renderForeground(matrix, guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);

		int relativeX = mouseX - offsetX;
		int relativeY = mouseY - offsetY;

		//Handles data tooltip
		if( relativeX > 0 && relativeX < barXSize && relativeY > 0 && relativeY < barYSize) {
			int totalData = 0, totalMaxData = 0;

			for(DataStorage datum : data) {
				totalData += datum.getData();
				totalMaxData += datum.getMaxData();
			}

			List<String> list = new LinkedList<>();
			list.add(totalData + " / " + totalMaxData + " Data");
			list.add("Type: " +  I18n.format(data[0].getDataType().toString()));
			this.drawTooltip(gui, matrix, list, mouseX, mouseY, zLevel, font);
		}

	}
	
	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack matrix, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		buttonLoad.renderBackground(gui, matrix, x, y, mouseX, mouseY, font);
		buttonStore.renderBackground(gui, matrix, x, y, mouseX, mouseY, font);

		for(Slot slot : slotList) {
			gui.blit(matrix, x + slot.xPos - 1, y + slot.yPos - 1, icon.getxLoc(), icon.getyLoc(), icon.getxSize(), icon.getySize());
		}

		int totalData = 0, totalMaxData = 0;

		for(DataStorage datum : data) {
			totalData += datum.getData();
			totalMaxData += datum.getMaxData();
		}

		float percent = totalData/(float)totalMaxData;

		gui.blit(matrix, offsetX + x, offsetY + y, 176, 18, 8, 40);
		gui.blit(matrix, offsetX + x - 1, offsetY + y + barYSize + 4, 19, 171, 10, 10);

		gui.blit(matrix, offsetX + x + 1, 1 + offsetY + y + (barYSize-(int)(percent*barYSize)), textureOffsetX, barYSize- (int)(percent*barYSize) + textureOffsetY, barXSize, (int)(percent*barYSize));
	}
}
