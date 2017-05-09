package zmaster587.advancedRocketry.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.WavefrontObject;
import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.entity.EntityUIPlanet;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderElevatorCapsule extends Render {

	private static WavefrontObject sphere;
	public ResourceLocation capsuleTexture =  new ResourceLocation("advancedRocketry:textures/models/spaceElevatorCapsule.png");

	RenderLaser laser;
	static {

		try {
			sphere = new WavefrontObject(new ResourceLocation("advancedrocketry:models/spaceElevator.obj"));
		} catch(ModelFormatException e) {
			sphere = null;
			e.printStackTrace();
			System.exit(0);
		}
	}

	public RenderElevatorCapsule() {
		laser = new RenderLaser(1, new float[] { 0,0 , 0, 0}, new float[] { 1, 1 , 0, 0.11f} );
	}

	

	@Override
	public void doRender(Entity entityIn, double x, double y, double z,
			float entityYaw, float partialTicks) {
		laser.doRender(entityIn, x - 0.5, y+2.5, z - 0.5, entityYaw, partialTicks);

		EntityElevatorCapsule entity = (EntityElevatorCapsule) entityIn;
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y + 1, z);
		GL11.glRotated(entityYaw, 0, 1, 0);
		bindTexture(capsuleTexture);
		sphere.renderOnly("Capsule");

		if(entity.isInMotion())
			sphere.renderOnly("Door");


		//Render Beads
		Tessellator buffer = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		buffer.startDrawingQuads();
		GL11.glColor4f(1, 1 , 1 , 0.11f);

		double position = (System.currentTimeMillis() % 16000)/200f;

		for(int i = 0 ; i < 10; i++) {
			for(float radius = 0.25F; radius < 1.25; radius += .25F) {

				RenderHelper.renderCubeWithUV(buffer, -radius, -radius + position + i*80 + 4f, -radius, radius, radius + position + i*80 + 4f, radius,0,0,0,0);

			}
		}
		for(int i = 1 ; i < 11; i++) {
			for(float radius = 0.25F; radius < 1.25; radius += .25F) {

				RenderHelper.renderCubeWithUV(buffer, -radius, -radius - position + i*80 + 4, -radius, radius, radius - position + i*80 + 4, radius, 0,0,0,0);

			}
		}

		buffer.draw();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glDepthMask(true);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPopMatrix();



	}



	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return capsuleTexture;
	}
}
