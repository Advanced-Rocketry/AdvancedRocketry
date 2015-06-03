package zmaster587.advancedRocketry.Inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;

public abstract class GuiProgressBarContainer extends GuiContainer {

	public GuiProgressBarContainer(Container container) {
		super(container);
	}
	
	public void drawProgressBarVertical(int xLoc, int yLoc, int textureOffsetX, int textureOffsetY, int xSize, int ySize,float percent) {
		this.drawTexturedModalRect(xLoc, yLoc + (ySize-(int)(percent*ySize)), textureOffsetX, ySize- (int)(percent*ySize) + textureOffsetY, xSize, (int)(percent*ySize));
	}
	
	public void drawProgressBarIconVertical(int xLoc, int yLoc, IIcon icon, int xSize, int ySize,float percent) {
		this.drawTexturedModelRectFromIcon(xLoc, yLoc + (ySize-(int)(percent*ySize)), icon, xSize, (int)(percent*ySize));
	}
}
