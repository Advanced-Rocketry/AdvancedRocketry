package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.ModuleBase;

public class ModulePlanetImage extends ModuleBase {

	DimensionProperties properties;
	float width;

	public ModulePlanetImage(int locX, int locY, float size, DimensionProperties icon) {
		super(locX, locY);
		width = size;
	}


	@Override
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY, font);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GL11.glPushMatrix();
		GL11.glRotated(90, -1, 0, 0);
		//GL11.glTranslatef(xPosition, 100 + this.zLevel, yPosition);
		float newWidth = width/2f;

		RenderPlanetarySky.renderPlanetPubHelper(vertexbuffer, (properties.isStar()) ? TextureResources.locationSunPng : properties.getPlanetIcon(), (int)(x + this.offsetX + newWidth), (int)(y + this.offsetY + newWidth), -0.1, newWidth, 1f, properties.getSolarTheta(), properties.hasAtmosphere(), properties.skyColor, properties.ringColor, properties.isGasGiant(), properties.hasRings(), properties.hasDecorators(), new float[]{0, 0, 0});
		GL11.glPopMatrix();
	}
	
	public void setDimProperties(DimensionProperties location) {
		properties = location;
	}
}
