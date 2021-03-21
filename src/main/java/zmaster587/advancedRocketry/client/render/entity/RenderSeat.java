package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderState.CullState;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.entity.EntityDummy;
import zmaster587.advancedRocketry.entity.EntityHoverCraft;
import zmaster587.libVulpes.render.RenderHelper;

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
	public ResourceLocation getEntityTexture(EntityDummy entity) {
		return null;
	}
	
	@Override
	public boolean shouldRender(EntityDummy livingEntityIn, ClippingHelper camera, double camX, double camY,
			double camZ) {
		return false;
	}
	
	@Override
	public void render(EntityDummy entity, float entityYaw, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer bufferIn, int packedLightIn) {

		// Do nothing, don't render a thing
	}
}
