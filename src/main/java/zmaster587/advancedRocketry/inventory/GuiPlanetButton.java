package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.vector.Quaternion;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

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
	
	public void renderButton(MatrixStack p_230431_1_, int par2, int par3, float p_230431_4_) {

	}
	
	// Draw button
	@Override
	public void render(MatrixStack matrix, int par2, int par3, float p_230431_4_) {
		if (this.visible)
		{
			//
			this.hovered = par2 >= this.x && par3 >= this.y && par2 < this.x + this.width && par3 < this.y + this.height;
			int hoverState = this.getYImage(this.hovered);
	
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder vertexbuffer = tessellator.getBuffer();
			matrix.push();
			matrix.rotate(new Quaternion(-90, 0, 0, true));
			//matrix.translate(offsetX, 100, offsetY);
	        //GL11.glTranslatef(xPosition, 100 + this.zLevel, yPosition);
	        float newWidth = width/2f;
	        
	        RenderPlanetarySky.renderPlanetPubHelper(vertexbuffer, matrix, properties.getPlanetIcon(), (int)(x + newWidth), (int)(y + newWidth), (double)this.height, newWidth, 1f, properties.getSolarTheta(), properties.hasAtmosphere(), properties.skyColor, properties.ringColor, properties.isGasGiant(), properties.hasRings(),properties.hasDecorators());
            matrix.pop();
	        
			
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			// draw button
			//this.render(matrix, (int) par2, (int) par3, 0);
		}
	}
}
