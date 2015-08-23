package zmaster587.advancedRocketry.Inventory.modules;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileEntityMultiPowerConsumer;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;
import zmaster587.libVulpes.gui.GuiToggleButtonImage;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class ModuleToggleSwitch extends ModuleButton {


	GuiToggleButtonImage enabledButton;
	//private ResourceLocation[] buttonToggleImage;
	boolean prevState;
	boolean currentState;
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
	
	public void setToggleState(boolean value) {
		this.currentState = value;
		this.prevState = !this.currentState;
	}

	@Override
	public int numberOfChangesToSend() {
		return 1;
	}

	@Override
	protected boolean needsUpdate(int localId) {
		return prevState != currentState;
	}

	@Override
	protected void updatePreviousState(int localId) {
		prevState = currentState;
	}
	
	@Override
	public void sendChanges(Container container, ICrafting crafter,	int variableId, int localId) {
		crafter.sendProgressBarUpdate(container, variableId, currentState ? 1 : 0);
	}
	
	@Override
	public boolean isUpdateRequired(int localId) {
		return prevState != currentState;
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		tile.stateUpdated(this);
		currentState = (value == 1);
		System.out.println("recieved: " + currentState);
	}

	public void setState(boolean state) {
		currentState = state;
	}
	
	public boolean getState() {
		return currentState;
	}
	
	public boolean isButton(GuiButton button) {
		return button == this.enabledButton;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, FontRenderer font) {
		super.renderBackground(gui, x, y, font);
		enabledButton.setState(currentState);
	}
}