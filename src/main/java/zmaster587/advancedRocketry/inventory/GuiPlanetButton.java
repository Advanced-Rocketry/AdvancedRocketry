package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.gui.GuiImageButton;

public class GuiPlanetButton extends GuiImageButton {

	DimensionProperties properties;
	
	public GuiPlanetButton(int id, int x, int y, int width, int height,
			DimensionProperties properties) {
		super(id, x, y, width, height, null);
		this.properties = properties;
	}

	@Override
	public void drawButton(Minecraft minecraft, int par2, int par3, float f)
	{
		if (this.visible)
		{
			//
			this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
			int hoverState = this.getHoverState(this.hovered);

			/*if(mousePressed(minecraft, par2, par3) && buttonTexture[2] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[2]);*/
			/*if(buttonTexture.length > 1 && hoverState == 2 && buttonTexture[1] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[1]);
			else if(buttonTexture.length > 2 && hoverState == 4 && buttonTexture[3] != null)
				minecraft.getTextureManager().bindTexture(buttonTexture[3]);
			else
				minecraft.getTextureManager().bindTexture(buttonTexture[0]);*/

			//Draw the button...each button should contain 3 images default state, hover, and pressed

			//GlStateManager.enableBlend();
            //GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
           
	
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder vertexbuffer = tessellator.getBuffer();
	        GL11.glPushMatrix();
	        GL11.glRotated(90, -1, 0, 0);
	        //GL11.glTranslatef(xPosition, 100 + this.zLevel, yPosition);
	        float newWidth = width/2f;
	        
	        RenderPlanetarySky.renderPlanetPubHelper(vertexbuffer, properties.getPlanetIcon(), (int)(x + newWidth), (int)(y + newWidth), (double)this.zLevel, newWidth, 1f, properties.getSolarTheta(), properties.hasAtmosphere(), properties.skyColor, properties.ringColor, properties.isGasGiant(), properties.hasRings(),properties.hasDecorators());
            GL11.glPopMatrix();
	        
	        /*vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
	        vertexbuffer.pos(xPosition, yPosition + height, (double)this.zLevel).tex(0, 1).endVertex();
	        vertexbuffer.pos(xPosition + width, yPosition + height, (double)this.zLevel).tex( 1, 1).endVertex();
	        vertexbuffer.pos(xPosition + width, yPosition, (double)this.zLevel).tex(1, 0).endVertex();
	        vertexbuffer.pos(xPosition, yPosition, (double)this.zLevel).tex(0, 0).endVertex();
	        tessellator.draw();*/
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			this.mouseDragged(minecraft, par2, par3);
		}
	}
}
