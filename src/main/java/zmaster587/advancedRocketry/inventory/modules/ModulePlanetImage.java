package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.inventory.modules.ModuleBase;

public class ModulePlanetImage extends ModuleBase {

	DimensionProperties properties;
	float width;

	public ModulePlanetImage(int locX, int locY, float size, DimensionProperties icon) {
		super(locX, locY);
		width = size;
	}


	@Override
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack matrix, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		super.renderBackground(gui, matrix, x, y, mouseX, mouseY, font);

		if(Constants.INVALID_STAR.equals(properties.getStarId()))
			return;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		matrix.push();
		matrix.rotate(new Quaternion(-90, 0, 0, true));
		//matrix.translate(offsetX, 100, offsetY);
		//GL11.glTranslatef(xPosition, 100 + this.zLevel, yPosition);
		float newWidth = width/2f;

		RenderPlanetarySky.renderPlanetPubHelper(vertexbuffer, matrix, properties.getPlanetIcon(), (int)(x + this.offsetX + newWidth), (int)(y + this.offsetY + newWidth), -0.1, newWidth, 1f, properties.getSolarTheta(), properties.hasAtmosphere(), properties.skyColor, properties.ringColor, properties.isGasGiant(), properties.hasRings(), properties.hasDecorators(), new float[]{0, 0, 0}, 1f);
		matrix.pop();
	}
	
	public void setDimProperties(DimensionProperties location) {
		properties = location;
	}
}
