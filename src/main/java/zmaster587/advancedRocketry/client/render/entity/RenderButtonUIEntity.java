package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityUIButton;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderButtonUIEntity extends Render<EntityUIButton> implements IRenderFactory<EntityUIButton> {

	public RenderButtonUIEntity(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public Render<? super EntityUIButton> createRenderFor(
			RenderManager manager) {
		return new RenderButtonUIEntity(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityUIButton entity) {
		return DimensionProperties.PlanetIcons.EARTHLIKE.getResource();
	}

	@Override
	public void doRender(EntityUIButton entity, double x, double y, double z,
			float entityYaw, float partialTicks) {

		GL11.glPushMatrix();
		GL11.glTranslated(0, -.25, 0);
		

		RenderHelper.renderTag(Minecraft.getMinecraft().player.getDistanceSq(entity), "Up a level", x,y,z, 8);
		GL11.glPopMatrix();

		//Clean up and make player not transparent
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
	}
}
