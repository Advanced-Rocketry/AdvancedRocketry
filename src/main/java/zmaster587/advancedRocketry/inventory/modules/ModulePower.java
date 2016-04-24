package zmaster587.advancedRocketry.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.libVulpes.api.IUniversalEnergy;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraftforge.common.util.ForgeDirection;

public class ModulePower extends ModuleBase {

	IUniversalEnergy tile;
	private static final int barXSize = 6;
	private static final int barYSize = 38;
	private static final int textureOffsetX = 0;
	private static final int textureOffsetY = 171;

	int prevPower = -1;

	public ModulePower(int offsetX, int offsetY, IUniversalEnergy tile) {
		super(offsetX, offsetY);
		this.tile = tile;
	}

	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY, FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY, font);

		//Power bar background
		gui.drawTexturedModalRect(x + offsetX, y + offsetY, 176, 18, 8, 40);

		//Battery Icon
		gui.drawTexturedModalRect(x + offsetX + 2, y + offsetY + barYSize + 5, 15, 171, 4, 9);

		//Power Bar
		float percent = tile.getEnergyStored()/(float)tile.getMaxEnergyStored();

		gui.drawTexturedModalRect(offsetX + x + 1, 1 + offsetY + y + (barYSize-(int)(percent*barYSize)), textureOffsetX, barYSize- (int)(percent*barYSize) + textureOffsetY, barXSize, (int)(percent*barYSize));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderForeground (int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {

		int relativeX = mouseX - offsetX;
		int relativeY = mouseY - offsetY;

		if( relativeX > 0 && relativeX < barXSize && relativeY > 0 && relativeY < barYSize) {
			List<String> list = new LinkedList<String>();
			list.add(tile.getEnergyStored() + " / " + tile.getMaxEnergyStored() + " Power");

			this.drawTooltip(gui, list, mouseX, mouseY, zLevel, font);
		}

	}

	@Override
	public int numberOfChangesToSend() {
		return 2;
	}

	
	//Packets are split due to the fact only a short can be sent
	@Override
	protected boolean needsUpdate(int localId) {
		if(localId == 0)
			return (prevPower & 0xFFFF) != (tile.getEnergyStored() & 0xFFFF);
		else if(localId == 1)
			return ( (prevPower >>> 16 ) & 0xFFFF) != ( ( tile.getEnergyStored()  >>> 16) & 0xFFFF);
		return false;
	}
	
	@Override
	protected void updatePreviousState(int localId) {
		if(localId == 0) {
			int data = (tile.getEnergyStored() & 0xFFFF);
			prevPower = (prevPower & 0xFFFF0000) | data;
		}
		else if(localId == 1) {
			int data = (tile.getEnergyStored() & 0xFFFF0000);
			prevPower = (prevPower & 0xFFFF) | data;
		}
	}

	@Override
	public void sendChanges(Container container, ICrafting crafter, int variableId, int localId) {

		if(localId == 0) {
			int data = (tile.getEnergyStored() & 0xFFFF);
			crafter.sendProgressBarUpdate(container, variableId, data);
		}
		else if(localId == 1) {
			int data = (tile.getEnergyStored() & 0xFFFF0000);
			crafter.sendProgressBarUpdate(container, variableId, data >>> 16);
		}
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		if(slot == 0) {
			int energy = tile.getEnergyStored();
			energy = (energy & 0xFFFF0000) | (value & 0xFFFF);
			tile.setEnergyStored(energy);
		}
		else if(slot == 1) {
			int energy = tile.getEnergyStored();
			energy = (energy & 0x0000FFFF) | (value << 16);
			tile.setEnergyStored(energy);
		}
	}
}
