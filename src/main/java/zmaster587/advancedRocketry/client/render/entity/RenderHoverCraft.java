package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderState.CullState;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.entity.EntityHoverCraft;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderHoverCraft extends EntityRenderer<EntityHoverCraft> implements IRenderFactory<EntityHoverCraft> {

	private static WavefrontObject hoverCraft;
	public ResourceLocation hovercraftTexture =  new ResourceLocation("advancedRocketry:textures/models/hoverCraft.png");
	
	static {

		try {
			hoverCraft = new WavefrontObject(new ResourceLocation("advancedrocketry:models/hoverCraft.obj"));
		} catch(ModelFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public RenderHoverCraft(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public EntityRenderer<? super EntityHoverCraft> createRenderFor(
			EntityRendererManager manager) {
		return new RenderHoverCraft(manager);
	}

	@Override
	public ResourceLocation getEntityTexture(EntityHoverCraft entity) {
		return hovercraftTexture;
	}
	
	@Override
	public boolean shouldRender(EntityHoverCraft livingEntityIn, ClippingHelper camera, double camX, double camY,
			double camZ) {
		return true;
	}
	
	@Override
	public void render(EntityHoverCraft entity, float entityYaw, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer bufferIn, int packedLightIn) {

		
		matrix.push();
		matrix.translate(0, 1, 0);
		matrix.rotate(new Quaternion(0, 180-entityYaw, 0, false));
		
		IVertexBuilder entitySolidBuilder = bufferIn.getBuffer(RenderType.getEntitySolid(getEntityTexture(entity)));
		
		hoverCraft.tessellateAll(entitySolidBuilder);
		
		float r = 0.1f, g = 0.1f, b = 1f, a = 0.8f;
		
		IVertexBuilder entityTransparentBuilder = bufferIn.getBuffer(RenderHelper.SEMI_TRANSLUCENT);
		
		final float start = -0.85f - (entity.world.getGameTime() % 10)*0.01f;
		final int count = 5;
		final float offsetX = 0.5f;
		final float offsetZ = 1.9f;
		float offset = -0.1f;
		
		
		for(int i = 0; i < count; i++)
		{
			float newRadius = (offset*(count-i) -0.85f - start)*0.5f;
			
			RenderHelper.renderTopFace(entityTransparentBuilder, start + offset*i, -newRadius, -newRadius, newRadius, newRadius, r,g,b,a);
			RenderHelper.renderBottomFace(entityTransparentBuilder, start + offset*i, -newRadius, -newRadius, newRadius, newRadius, r,g,b,a);
			
			RenderHelper.renderTopFace(entityTransparentBuilder, start + offset*i, -newRadius + offsetX, -newRadius + offsetZ, newRadius + offsetX, newRadius + offsetZ, r,g,b,a);
			RenderHelper.renderBottomFace(entityTransparentBuilder, start + offset*i, -newRadius + offsetX, -newRadius + offsetZ, newRadius + offsetX, newRadius + offsetZ, r,g,b,a);
			
			RenderHelper.renderTopFace(entityTransparentBuilder, start + offset*i, -newRadius - offsetX, -newRadius + offsetZ, newRadius - offsetX, newRadius + offsetZ, r,g,b,a);
			RenderHelper.renderBottomFace(entityTransparentBuilder, start + offset*i, -newRadius - offsetX, -newRadius + offsetZ, newRadius - offsetX, newRadius + offsetZ, r,g,b,a);
		}
		Tessellator.getInstance().draw();

		matrix.pop();
	}
}
