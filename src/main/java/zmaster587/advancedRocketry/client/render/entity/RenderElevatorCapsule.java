package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class RenderElevatorCapsule extends EntityRenderer<EntityElevatorCapsule> implements IRenderFactory<EntityElevatorCapsule> {

	private static final WavefrontObject sphere;
	public ResourceLocation capsuleTexture =  new ResourceLocation("advancedrocketry","textures/models/spaceelevatorcapsule.png");

	
	static {

		try {
			sphere = new WavefrontObject(new ResourceLocation("advancedrocketry","models/spaceelevator.obj"));
		} catch(ModelFormatException e) {
			throw new RuntimeException(e);
		}
	}

	@ParametersAreNonnullByDefault
	public RenderElevatorCapsule(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public EntityRenderer<? super EntityElevatorCapsule> createRenderFor(
			EntityRendererManager manager) {
		return new RenderElevatorCapsule(manager);
	}
	
	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public ResourceLocation getEntityTexture(EntityElevatorCapsule entity) {
		return capsuleTexture;
	}
	
	
	@Override
	@ParametersAreNonnullByDefault
	public boolean shouldRender(EntityElevatorCapsule livingEntity, ClippingHelper camera, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void render(EntityElevatorCapsule entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrix.push();
		matrix.translate(0, 1, 0);
		matrix.rotate(new Quaternion(0, entityYaw, 0, true));
        
		IVertexBuilder builder = bufferIn.getBuffer(zmaster587.libVulpes.render.RenderHelper.getTranslucentEntityModelRenderType(getEntityTexture(entity)));
		sphere.renderOnly(matrix,packedLightIn,OverlayTexture.NO_OVERLAY, builder, "Capsule");

		if(entity.isInMotion())
			sphere.renderOnly(matrix,packedLightIn,OverlayTexture.NO_OVERLAY, builder, "Door");

		matrix.pop();
	}
}
