package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;

public class RenderElevatorCapsule extends EntityRenderer<EntityElevatorCapsule> implements IRenderFactory<EntityElevatorCapsule> {

	private static WavefrontObject sphere;
	public ResourceLocation capsuleTexture =  new ResourceLocation("advancedrocketry","textures/models/spaceelevatorcapsule.png");

	
	static {

		try {
			sphere = new WavefrontObject(new ResourceLocation("advancedrocketry","models/spaceelevator.obj"));
		} catch(ModelFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public RenderElevatorCapsule(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public EntityRenderer<? super EntityElevatorCapsule> createRenderFor(
			EntityRendererManager manager) {
		return new RenderElevatorCapsule(manager);
	}
	
	@Override
	public ResourceLocation getEntityTexture(EntityElevatorCapsule entity) {
		return capsuleTexture;
	}
	
	
	@Override
	public boolean shouldRender(EntityElevatorCapsule livingEntity,
			ClippingHelper camera, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public void render(EntityElevatorCapsule entity, float entityYaw, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer bufferIn, int packedLightIn) {
		matrix.push();
		matrix.translate(0, 1, 0);
		matrix.rotate(new Quaternion(0, entityYaw, 0, true));
        int j = packedLightIn;
        int k = OverlayTexture.NO_OVERLAY;
        
		IVertexBuilder builder = bufferIn.getBuffer(zmaster587.libVulpes.render.RenderHelper.getTranslucentEntityModelRenderType(getEntityTexture(entity)));
		sphere.renderOnly(matrix,j,k, builder, "Capsule");

		if(entity.isInMotion())
			sphere.renderOnly(matrix,j,k, builder, "Door");

		matrix.pop();
	}
}
