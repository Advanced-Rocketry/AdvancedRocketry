package zmaster587.advancedRocketry.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.entity.EntityUIPlanet;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderElevatorCapsule extends Render<EntityElevatorCapsule> implements IRenderFactory<EntityElevatorCapsule> {

	private static WavefrontObject sphere;
	public ResourceLocation capsuleTexture =  new ResourceLocation("advancedRocketry:textures/models/spaceElevatorCapsule.png");

	
	static {

		try {
			sphere = new WavefrontObject(new ResourceLocation("advancedrocketry:models/spaceElevator.obj"));
		} catch(ModelFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public RenderElevatorCapsule(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public Render<? super EntityElevatorCapsule> createRenderFor(
			RenderManager manager) {
		return new RenderElevatorCapsule(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityElevatorCapsule entity) {
		return capsuleTexture;
	}
	@Override
	public boolean shouldRender(EntityElevatorCapsule livingEntity,
			ICamera camera, double camX, double camY, double camZ) {
		// TODO Auto-generated method stub
		//return super.shouldRender(livingEntity, camera, camX, camY, camZ);
		return true;
	}

	@Override
	public void doRender(EntityElevatorCapsule entity, double x, double y, double z,
			float entityYaw, float partialTicks) {

		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y + 1, z);
		GL11.glRotated(entityYaw, 0, 1, 0);
		bindTexture(capsuleTexture);
		sphere.renderOnly("Capsule");

		if(entity.isInMotion())
			sphere.renderOnly("Door");

		GL11.glPopMatrix();



	}
}
