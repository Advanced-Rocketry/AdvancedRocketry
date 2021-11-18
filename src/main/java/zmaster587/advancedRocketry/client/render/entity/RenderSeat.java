package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import com.mojang.blaze3d.matrix.MatrixStack;

import zmaster587.advancedRocketry.entity.EntityDummy;

import javax.annotation.ParametersAreNonnullByDefault;

public class RenderSeat extends EntityRenderer<EntityDummy> implements IRenderFactory<EntityDummy> {


	public RenderSeat(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public EntityRenderer<? super EntityDummy> createRenderFor(
			EntityRendererManager manager) {
		return new RenderSeat(manager);
	}

	@Override
	@ParametersAreNonnullByDefault
	public ResourceLocation getEntityTexture(EntityDummy entity) {
		return null;
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public boolean shouldRender(EntityDummy livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
		return false;
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(EntityDummy entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn) {

		// Do nothing, don't render a thing
	}
}
