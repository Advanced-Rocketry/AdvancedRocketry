package zmaster587.advancedRocketry.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderButtonUIEntity extends Render {


	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return DimensionProperties.PlanetIcons.EARTHLIKE.getResource();
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z,
			float entityYaw, float partialTicks) {

		GL11.glPushMatrix();
		GL11.glTranslated(0, -.25, 0);
		

		RenderHelper.renderTag(Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity(entity), "Up a level", x,y,z, 8);
		GL11.glPopMatrix();

		//Clean up and make player not transparent
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
	}
}
