package zmaster587.advancedRocketry.Inventory.modules;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.gui.GuiImageButton;
import zmaster587.libVulpes.gui.GuiToggleButtonImage;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.IconResource;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModuleButton extends ModuleBase {

	@SideOnly(Side.CLIENT)
	GuiImageButton button;

	IButtonInventory tile;
	boolean prevState;
	int buttonId, sizeX, sizeY, color, bgColor;
	String text, tooltipText;
	ResourceLocation buttonImages[];
	boolean visible = true, enabled = true;
	String sound;

	public ModuleButton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, ResourceLocation buttonImages[]) {
		super(offsetX, offsetY);
		this.tile = tile;
		this.buttonImages = buttonImages;
		this.text = text;
		this.buttonId = buttonId;

		sound = "";
		sizeX = 52;
		sizeY = 16;

		bgColor = 0xFFFFFFFF;
		color = 0xFF22FF22; // Lime green
	}

	public ModuleButton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, ResourceLocation buttonImages[], String tooltipText) {
		this(offsetX, offsetY, buttonId, text, tile, buttonImages);
		this.tooltipText = tooltipText;
	}


	public ModuleButton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, ResourceLocation buttonImages[], int sizeX, int sizeY) {
		this(offsetX, offsetY, buttonId, text, tile, buttonImages);
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	public ModuleButton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, ResourceLocation buttonImages[], String tooltipText, int sizeX, int sizeY) {
		this(offsetX, offsetY, buttonId, text, tile, buttonImages,sizeX, sizeY);
		this.tooltipText = tooltipText;
	}

	/*public ModuleButtton(int offsetX, int offsetY, int buttonId, String text, IButtonInventory tile, IconResource buttonImage) {

		this(offsetX, offsetY, buttonId, text, tile, new Re);
		this.sizeX = buttonImage.getxSize();
		this.sizeY = buttonImage.getySize();
	}*/

	public void setImage(ResourceLocation[] images) {
		((GuiImageButton)button).setButtonTexture(images);
	}


	public void setSound(String str) {
		if(FMLCommonHandler.instance().getSide().isClient())
			if(button == null)
				sound = str;
			else
				button.setSound(str);
	}

	/**
	 * Sets the text of this button
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the text displayed on the button
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param color sets the color of the button ARGB8
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * 
	 * @return the color of this button ARGB8
	 */
	public int getColor() {
		return this.color;
	}

	public void setBGColor(int color) {
		if(button == null)
			this.bgColor = color;
		else
			button.setBackgroundColor(color);
	}

	/**
	 * Sets the button to be (in)visible
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		if(button != null)
			button.visible = visible;
	}

	/**
	 * 
	 * @return true if the button can be rendered
	 */
	public boolean isVisible() {
		return button.visible;
	}

	/**
	 * dis/enables the button
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if(button != null)
			this.enabled = button.enabled = enabled;
	}

	/**
	 * 
	 * @return true if the button is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	@SideOnly(Side.CLIENT)
	public List<GuiButton> addButtons(int x, int y) {

		List<GuiButton> list = new LinkedList<>();

		button = new GuiImageButton(buttonId, x + offsetX, y + offsetY, sizeX, sizeY, buttonImages);

		button.visible = visible;

		if(!sound.isEmpty()) {
			button.setSound(sound);
			sound = "";
		}

		button.setBackgroundColor(bgColor);
		list.add(button);

		return list;
	}

	@SideOnly(Side.CLIENT)
	public void actionPerform(GuiButton button) {
		if(enabled && button == this.button) {
			tile.onInventoryButtonPressed(buttonId);
		}
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		int relativeX = mouseX - offsetX;
		int relativeY = mouseY - offsetY;

		return relativeX > 0 && relativeX < sizeX && relativeY > 0 && relativeY < sizeY;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel,
			GuiContainer gui, FontRenderer font) {

		//if(visible) {
		gui.drawCenteredString(font, text, offsetX + sizeX / 2, offsetY + sizeY / 2  - font.FONT_HEIGHT/2, color);

		if(tooltipText != null) {

			if( isMouseOver(mouseX, mouseY) ) {
				List<String> list = new LinkedList<String>();
				for(String str : tooltipText.split("\n")) {

					list.add(str);

				}
				this.drawTooltip(gui, list, mouseX, mouseY, zLevel, font);
			}
			//}
		}
	}
}
