package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.gui.GuiImageButton;

public class GuiPlanetButton extends GuiImageButton {

	DimensionProperties properties;
	
	public GuiPlanetButton(int x, int y, int width, int height,
			DimensionProperties properties) {
		super(x, y, width, height, null);
		this.properties = properties;
	}

	
	
	// Draw button
	@Override
	public void func_230431_b_(MatrixStack matrix, int par2, int par3, float p_230431_4_) {
		if (this.visible)
		{
			//
			this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
			int hoverState = this.func_230989_a_(this.hovered);
	
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder vertexbuffer = tessellator.getBuffer();
	        GL11.glPushMatrix();
	        GL11.glRotated(90, -1, 0, 0);
	        //GL11.glTranslatef(xPosition, 100 + this.zLevel, yPosition);
	        float newWidth = width/2f;
	        
	        RenderPlanetarySky.renderPlanetPubHelper(vertexbuffer, properties.getPlanetIcon(), (int)(x + newWidth), (int)(y + newWidth), (double)this.field_230689_k_, newWidth, 1f, properties.getSolarTheta(), properties.hasAtmosphere(), properties.skyColor, properties.ringColor, properties.isGasGiant(), properties.hasRings(),properties.hasDecorators());
            GL11.glPopMatrix();
	        
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			// mousedragged
			this.func_230430_a_(matrix, (int) par2, (int) par3, 0);
		}
	}
}
