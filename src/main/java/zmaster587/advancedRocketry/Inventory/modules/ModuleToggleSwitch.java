package zmaster587.advancedRocketry.Inventory.modules;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.libVulpes.gui.GuiToggleButtonImage;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.util.ResourceLocation;

public class ModuleToggleSwitch extends ModuleButton {


	GuiToggleButtonImage enabledButton;
	boolean currentState, prevState;
	IToggleButton tile;
	boolean enabled = true;


	public ModuleToggleSwitch(int offsetX, int offsetY, int buttonId, String text, IToggleButton tile, ResourceLocation[] buttonImages, boolean defaultState) {
		super(offsetX, offsetY, buttonId, text, tile, buttonImages);
		this.tile = tile;
		currentState = defaultState;
	}
	
	public ModuleToggleSwitch(int offsetX, int offsetY, int buttonId, String text, IToggleButton tile, ResourceLocation buttonImages[], String tooltipText, boolean defaultState) {
		super(offsetX, offsetY, buttonId, text, tile, buttonImages, tooltipText);
		this.tile = tile;
		currentState = defaultState;
	}

	public ModuleToggleSwitch(int offsetX, int offsetY, int buttonId, String text, IToggleButton tile, ResourceLocation buttonImages[], int sizeX, int sizeY, boolean defaultState) {
		super(offsetX, offsetY, buttonId, text, tile, buttonImages, sizeX, sizeY);
		this.tile = tile;
		currentState = defaultState;
	}
	
	public ModuleToggleSwitch(int offsetX, int offsetY, int buttonId, String text, IToggleButton tile, ResourceLocation buttonImages[], String tooltipText, int sizeX, int sizeY, boolean defaultState) {
		super(offsetX, offsetY, buttonId, text, tile, buttonImages, tooltipText, sizeX, sizeY);
		this.tile = tile;
		currentState = defaultState;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	

	@SideOnly(Side.CLIENT)
	public List<GuiButton> addButtons(int x, int y) {

		List<GuiButton> list = new LinkedList<>();

		enabledButton = new GuiToggleButtonImage(0, x + offsetX, y + offsetY, sizeX, sizeY, buttonImages);
		enabledButton.setState(currentState);

		list.add(enabledButton);

		return list;
	}

	@SideOnly(Side.CLIENT)
	public void actionPerform(GuiButton button) {
		if(enabled && button == enabledButton) {
			this.currentState = !this.currentState;
			this.tile.onInventoryButtonPressed(buttonId);
		}
	}

	public void setToggleState(boolean state) {
		currentState = state;
	}
	
	public boolean getState() {
		return currentState;
	}
	
	@Override
	public boolean needsUpdate(int localId) {
		// TODO Auto-generated method stub
		return prevState != currentState;
	}
	
	@Override
	public void onChangeRecieved(int slot, int value) {
		this.setToggleState(value == 1);
	}
	
	@Override
	protected void updatePreviousState(int localId) {
		prevState = currentState;
	}
	
	@Override
	public void sendChanges(Container container, ICrafting crafter,
			int variableId, int localId) {
		crafter.sendProgressBarUpdate(container, variableId, currentState ? 1 : 0);
	}
	
	public boolean isButton(GuiButton button) {
		return button == this.enabledButton;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY, FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY, font);
		enabledButton.setState(currentState);
	}
}