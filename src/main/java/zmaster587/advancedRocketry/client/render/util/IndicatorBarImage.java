package zmaster587.advancedRocketry.client.render.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
/**
 * Used to display an arrow or some other image moving along a bar
 *
 */
public class IndicatorBarImage extends ProgressBarImage {

	public IndicatorBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, int foreWidth, int foreHeight, int insetX, int insetY, ForgeDirection direction, ResourceLocation image) {
		super(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, foreWidth, foreHeight, insetX, insetY, direction,image);
	}
	
	public IndicatorBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, int foreWidth, int foreHeight,ForgeDirection direction, ResourceLocation image) {
		super(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, foreWidth, foreHeight, 0, 0, direction, image);
	}
	
	public IndicatorBarImage(int backOffsetX, int backOffsetY, int backWidth, int backHeight, int foreOffsetX, int foreOffsetY, ForgeDirection direction, ResourceLocation image) {
		super(backOffsetX, backOffsetY, backWidth, backHeight, foreOffsetX, foreOffsetY, backWidth, backHeight, 0, 0, direction, image);
	}
	
	@Override
	public void renderProgressBar(int x, int y, float percent, Gui gui) {
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		
		gui.drawTexturedModalRect(x, y, backOffsetX, backOffsetY, backWidth, backHeight);
		
		int xProgress = 0, yProgress = 0;
		
		if(direction == ForgeDirection.WEST)
			xProgress = (int) (backWidth - insetX - ( ( backWidth - ( insetX*2 ) )*percent)) - foreHeight/2;
		else if(direction == ForgeDirection.EAST)
			xProgress = (int) (insetX - foreWidth + ( ( backWidth - ( insetX*2 ) )*percent));
		else
			xProgress = insetX;
		
		if(direction == ForgeDirection.UP)
			yProgress = (int) (backHeight - insetY - ( ( backHeight - ( insetY*2 ) )*percent)) - foreHeight/2;
		else if(direction == ForgeDirection.DOWN)
			yProgress = (int) (insetY + ( ( backHeight - ( insetY*2 ) )*percent)) + foreHeight/2;
		else
			yProgress = insetY;
		
		gui.drawTexturedModalRect(x + xProgress, y + yProgress, foreOffsetX, foreOffsetY, foreWidth, foreHeight);
	}
}
