package zmaster587.advancedRocketry.Inventory.modules;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;

public class ModuleRadioButton  extends ModuleBase {

	IToggleButton tile;
	List<ModuleToggleSwitch> buttons;
	int previousSelection;
	int enabledColor, disabledColor;
	boolean enabled = true;

	public ModuleRadioButton(IToggleButton tile, List<ModuleToggleSwitch> buttons) {
		super(0, 0);
		this.buttons = buttons;
		this.tile = tile;

		enabledColor = 0xFF22FF22;
		disabledColor = 0xFFFF2222;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getOptionSelected() {
		for(int i = 0; i < buttons.size(); i++){
			if(buttons.get(i).getState())
				return i;
		}

		return -1;
	}

	public void setOptionSeleted(int option) {
		for(int i = 0; i < buttons.size(); i++){
			if(i == option) {
				buttons.get(i).setToggleState(true);
				buttons.get(i).setColor(enabledColor);
			}
			else {
				buttons.get(i).setToggleState(false);
				buttons.get(i).setColor(disabledColor);
			}
		}
	}

	@Override
	public int numberOfChangesToSend() {
		return 1;
	}

	
	@Override
	protected boolean needsUpdate(int localId) {
		return previousSelection != getOptionSelected();
	}
	
	@Override
	protected void updatePreviousState(int localId) {
		previousSelection = getOptionSelected();
	}

	@Override
	public void sendChanges(Container container, ICrafting crafter,
			int variableId, int localId) {
		crafter.sendProgressBarUpdate(container, variableId, getOptionSelected());
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		setOptionSeleted(value);
	}

	@Override
	public List<GuiButton> addButtons(int x, int y) {

		List<GuiButton> buttonList = super.addButtons(x, y);

		for(ModuleToggleSwitch button : buttons) {
			buttonList.addAll(button.addButtons(x, y));
		}

		return buttonList;
	}

	@Override
	public void actionPerform(GuiButton buttonObj) {

		if(enabled) {
			super.actionPerform(buttonObj);

			boolean isNotOurRadio = true;

			for(ModuleToggleSwitch button : buttons) {
				if(button.isButton(buttonObj)) {
					isNotOurRadio = false;
					break;
				}
			}

			if(isNotOurRadio)
				return;

			for(ModuleToggleSwitch button : buttons) {
				button.setToggleState(button.isButton(buttonObj));
				if(button.isButton(buttonObj))
					tile.onInventoryButtonPressed(button.buttonId);
			}
		}
	}

	@Override
	public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel,
			GuiContainer gui, FontRenderer font) {
		super.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);

		for(ModuleToggleSwitch button : buttons) {
			button.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui, font);
		}
	}

	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY, 
			FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY, font);

		for(ModuleToggleSwitch button : buttons) {
			button.renderBackground(gui, x, y, mouseX, mouseY, font);
		}
	}

}
