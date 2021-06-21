package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import zmaster587.libVulpes.render.RenderHelper;

public abstract class GuiProgressBarContainer extends GuiContainer {
	
	public GuiProgressBarContainer(Container container) {
		super(container);
	}
	
	public void drawProgressBar(int xLoc, int yLoc, int textureOffsetX, int textureOffsetY, int xSize, int ySize,float percent, EnumFacing direction) {
		if(EnumFacing.WEST == direction) {
			drawProgressBarHorizontal(xLoc, yLoc, textureOffsetX, textureOffsetY, xSize, ySize, percent);
		}
		else if(EnumFacing.UP == direction) {
			drawProgressBarVertical(xLoc, yLoc, textureOffsetX, textureOffsetY, xSize, ySize, percent);
		}
		else if(EnumFacing.DOWN == direction) {
			this.drawTexturedModalRect(xLoc, yLoc, textureOffsetX, textureOffsetY, xSize, (int)(percent*ySize));
		}
		else if (EnumFacing.EAST == direction) {
			this.drawTexturedModalRect(xLoc, yLoc, textureOffsetX, textureOffsetY, (int)(percent*xSize), ySize);
		}
	}
	
	public void drawProgressBarVertical(int xLoc, int yLoc, int textureOffsetX, int textureOffsetY, int xSize, int ySize,float percent) {
		this.drawTexturedModalRect(xLoc, yLoc + (ySize-(int)(percent*ySize)), textureOffsetX, ySize- (int)(percent*ySize) + textureOffsetY, xSize, (int)(percent*ySize));
	}
	
	public void drawProgressBarHorizontal(int xLoc, int yLoc, int textureOffsetX, int textureOffsetY, int xSize, int ySize,float percent) {
		this.drawTexturedModalRect(xLoc + (xSize-(int)(percent*xSize)), yLoc, xSize- (int)(percent*xSize) + textureOffsetX, textureOffsetY, (int)(percent*xSize), ySize);
	}
	
	public void drawProgressBarIconVertical(int xLoc, int yLoc, TextureAtlasSprite icon, int xSize, int ySize,float percent) {
		this.drawTexturedModalRect(xLoc, yLoc + (ySize-(int)(percent*ySize)), icon, xSize, (int)(percent*ySize));
	}
	
    /**
     * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
     */
    public void drawTexturedModalRectWithCustomSize(int x, int y, int u, int v, int width, int height)
    {
    	BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        buffer.begin(GL11.GL_QUADS, buffer.getVertexFormat());
        RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel, x, y, x + width, y + height, (u /*+ 0*/) * f, (u + width) * f, (v /*+ 0*/) * f1, (v + height) * f1);
        buffer.finishDrawing();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
