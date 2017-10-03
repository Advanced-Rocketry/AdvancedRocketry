package zmaster587.advancedRocketry.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityUIPlanet;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderPlanetUIEntity extends Render<EntityUIPlanet> implements IRenderFactory<EntityUIPlanet> {

	private static WavefrontObject sphere;
	public static ResourceLocation planetUIBG = new ResourceLocation("advancedrocketry:textures/gui/planetUIOverlay.png");
	public static ResourceLocation planetUIFG = new ResourceLocation("advancedrocketry:textures/gui/planetUIOverlayFG.png");

	static {
		try {
			sphere = new WavefrontObject(new ResourceLocation("advancedrocketry:models/atmosphere.obj"));
		} catch(ModelFormatException e) {
			sphere = null;
			e.printStackTrace();
			System.exit(0);
		}
	}

	public RenderPlanetUIEntity(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public Render<? super EntityUIPlanet> createRenderFor(
			RenderManager manager) {
		return new RenderPlanetUIEntity(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityUIPlanet entity) {
		return DimensionProperties.PlanetIcons.EARTHLIKE.getResource();
	}

	@Override
	public void doRender(EntityUIPlanet entity, double x, double y, double z,
			float entityYaw, float partialTicks) {

		DimensionProperties properties = entity.getProperties();
		if(properties == null)
			return;

		float sizeScale = Math.max(properties.gravitationalMultiplier*properties.gravitationalMultiplier*entity.getScale(), .5f);

		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y + sizeScale*0.03f, (float)z);
		//Max because moon was too small to be visible

		GL11.glScalef(.1f*sizeScale, .1f*sizeScale, .1f*sizeScale);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		Minecraft.getMinecraft().renderEngine.bindTexture(properties.getPlanetIconLEO());
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA, 0, 0);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);

		GlStateManager.color(1f, 1, 1f, .5f);

		GL11.glPushMatrix();
		GL11.glRotatef(entity.world.getTotalWorldTime() & 0xFF, 0, 1, 0);
		sphere.renderAll();
		GL11.glPopMatrix();


		//Render shadow
		GL11.glPushMatrix();
		GL11.glScalef(1.1f, 1.1f, 1.1f);
		GL11.glRotatef(90, 0, 0, 1);
		GL11.glRotated( -(properties.orbitTheta * 180/Math.PI), 1, 0, 0);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
		Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.shadow3);
		GlStateManager.color(.1f, .1f, .1f,0.75f);
		sphere.renderAll();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		if(properties.hasRings) {
			//Rotate for rings
			GL11.glRotatef(90, 1, 0, 0);
			GL11.glRotatef(-90, 0, 0, 1);
			
			//Draw ring
			GlStateManager.color(properties.ringColor[0], properties.ringColor[1], properties.ringColor[2],0.5f);
			Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.planetRings);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderTopFaceWithUV(buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			RenderHelper.renderBottomFaceWithUV(buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			Tessellator.getInstance().draw();

			//Draw ring shadow
			Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.planetRingShadow);
			GlStateManager.color(1,1,1,0.5f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderTopFaceWithUV(buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			RenderHelper.renderBottomFaceWithUV(buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			Tessellator.getInstance().draw();
		}

		GL11.glPopMatrix();

		//Render ATM
		if(properties.hasAtmosphere()) {
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
			GlStateManager.color(properties.skyColor[0], properties.skyColor[1], properties.skyColor[2], .1f);

			for(int i = 0; i < 5; i++) {
				GL11.glScalef(1.02f, 1.02f, 1.02f);
				sphere.renderAll();
			}


			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}




		//Render hololines
		GL11.glPushMatrix();
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);

		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		float myTime = ((entity.world.getTotalWorldTime() & 0xF)/16f);

		for(int i = 0; i < 4; i++ ) {
			myTime = ((i*4 + entity.world.getTotalWorldTime() & 0xF)/16f);

			GlStateManager.color(0, 1f, 1f, .2f*(1-myTime));
			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
			RenderHelper.renderTopFace(buf, myTime - 0.5, -.5f, -.5f, .5f, .5f);
			RenderHelper.renderBottomFace(buf, myTime - 0.5, -.5f, -.5f, .5f, .5f);
			Tessellator.getInstance().draw();
		}
		GlStateManager.alphaFunc(GL11.GL_GREATER, .1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();

		GL11.glDepthMask(true);

		//RenderSelection
		if(entity.isSelected()) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			double speedRotate = 0.025d;
			GlStateManager.color(0.4f, 0.4f, 1f, 0.6f);
			GL11.glTranslated(0, -1.25, 0);
			GL11.glPushMatrix();
			GL11.glRotated(speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
			RendererWarpCore.model.renderOnly("Rotate1");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glRotated(180 + speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
			RendererWarpCore.model.renderOnly("Rotate1");
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		GL11.glPopMatrix();

		RayTraceResult hitObj = Minecraft.getMinecraft().objectMouseOver;
		if(hitObj != null && hitObj.entityHit == entity) {

			GL11.glPushMatrix();
			GlStateManager.color(1, 1, 1);
			GL11.glTranslated(x, y + sizeScale*0.03f, z);
			sizeScale = .1f*sizeScale;
			GL11.glScaled(sizeScale,sizeScale,sizeScale);

			//Render atmosphere UI/planet info

			//GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			RenderHelper.setupPlayerFacingMatrix(Minecraft.getMinecraft().player.getDistanceSq(hitObj.hitVec.z, hitObj.hitVec.y, hitObj.hitVec.x), 0, 0, 0);
			buffer = Tessellator.getInstance().getBuffer();

			//Draw Mass indicator
			Minecraft.getMinecraft().renderEngine.bindTexture(planetUIFG);
			GlStateManager.color(1, 1, 1,0.8f);
			renderMassIndicator(buffer, properties.gravitationalMultiplier/2f);

			//Draw background
			GlStateManager.color(1, 1, 1,1);
			Minecraft.getMinecraft().renderEngine.bindTexture(planetUIBG);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, 1, -40, -25, 40, 55, 1, 0, 1, 0);
			Tessellator.getInstance().draw();


			//Render ATM
			Minecraft.getMinecraft().renderEngine.bindTexture(planetUIFG);
			renderATMIndicator(buffer, properties.getAtmosphereDensity()/200f);
			//Render Temp
			renderTemperatureIndicator(buffer, properties.averageTemperature/200f);

			//Render planet name
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			//GL11.glDepthMask(true);
			RenderHelper.cleanupPlayerFacingMatrix();
			RenderHelper.renderTag(Minecraft.getMinecraft().player.getDistanceSq(hitObj.hitVec.z, hitObj.hitVec.y, hitObj.hitVec.x), properties.getName(), 0, .9, 0, 5);
			RenderHelper.renderTag(Minecraft.getMinecraft().player.getDistanceSq(hitObj.hitVec.z, hitObj.hitVec.y, hitObj.hitVec.x), "NumMoons: " + properties.getChildPlanets().size(), 0, .6, 0, 5);

			GL11.glPopMatrix();
		}

		//Clean up and make player not transparent
		GlStateManager.color(1, 1, 1);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
	}

	protected void renderMassIndicator(BufferBuilder buffer, float percent) {
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		float maxUV = (1-percent)*0.5f;

		RenderHelper.renderNorthFaceWithUV(buffer, 0, -20, -5 + 41*(1-percent), 20, 36, .5f, 0f, .5, maxUV);
		Tessellator.getInstance().draw();
	}

	protected void renderATMIndicator(BufferBuilder buffer, float percent) {
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		float maxUV = (1-percent)*0.406f + .578f;
		//Offset by 15 for Y
		RenderHelper.renderNorthFaceWithUV(buffer, 0, 6, 20 + (1-percent)*33, 39, 53, .5624f, .984f, .984f, maxUV);
		Tessellator.getInstance().draw();
	}

	protected void renderTemperatureIndicator(BufferBuilder buffer, float percent) {
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		float maxUV = (1-percent)*0.406f + .578f;
		//Offset by 15 for Y
		RenderHelper.renderNorthFaceWithUV(buffer, 0, -38, 21.4f + (1-percent)*33, -4, 53, .016f, .4376f, .984f, maxUV);
		Tessellator.getInstance().draw();
	}
}
