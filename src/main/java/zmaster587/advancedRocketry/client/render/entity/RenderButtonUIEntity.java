package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityUIButton;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderButtonUIEntity extends EntityRenderer<EntityUIButton> implements IRenderFactory<EntityUIButton> {

	public RenderButtonUIEntity(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public EntityRenderer<? super EntityUIButton> createRenderFor(
			EntityRendererManager manager) {
		return new RenderButtonUIEntity(manager);
	}

	@Override
	public ResourceLocation getEntityTexture(EntityUIButton entity) {
		return DimensionProperties.PlanetIcons.EARTHLIKE.getResource();
	}
	@Override
	public void render(EntityUIButton entity, float entityYaw, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer bufferIn, int packedLightIn) {

		matrix.push();
		matrix.translate(0, 0.25, 0);
		

		RenderHelper.renderTag(matrix, bufferIn, Minecraft.getInstance().player.getDistanceSq(entity), "Up a level", packedLightIn, 1);
		matrix.pop();

		//Clean up and make player not transparent
	}
}
