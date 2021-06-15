package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityUIStar;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderStarUIEntity extends Render<EntityUIStar> implements IRenderFactory<EntityUIStar> {

	public RenderStarUIEntity(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public Render<? super EntityUIStar> createRenderFor(
			RenderManager manager) {
		return new RenderStarUIEntity(manager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityUIStar entity) {
		return DimensionProperties.PlanetIcons.EARTHLIKE.getResource();
	}

	@Override
	public void doRender(EntityUIStar entity, double x, double y, double z,
			float entityYaw, float partialTicks) {
		
		StellarBody body = entity.getStarProperties();
		if(body == null)
			return;
		float sizeScale = entity.getScale();
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		GL11.glScalef(sizeScale,sizeScale,sizeScale);
		
		RenderHelper.setupPlayerFacingMatrix(Minecraft.getMinecraft().player.getDistanceSqToEntity(entity), 0,-.45,0);
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.locationSunNew);
		
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glColor3d(body.getColor()[0], body.getColor()[1], body.getColor()[2]);
		//GL11.glColor3ub((byte)(body.getColorRGB8() & 0xff), (byte)((body.getColorRGB8() >>> 8) & 0xff), (byte)((body.getColorRGB8() >>> 16) & 0xff));
		//GlStateManager.color();
		
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.renderNorthFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
		Tessellator.getInstance().draw();
		
		
		RenderHelper.cleanupPlayerFacingMatrix();
		
		
		//Render hololines
		GL11.glPushMatrix();
		GL11.glScaled(.1, .1, .1);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		GlStateManager.disableTexture2D();

		float myTime;
		
		for(int i = 0; i < 4; i++ ) {
			myTime = ((i*4 + entity.world.getTotalWorldTime() & 0xF)/16f);

			GlStateManager.color(0, 1f, 1f, .2f*(1-myTime));
			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
			RenderHelper.renderTopFace(buf, myTime, -.5f, -.5f, .5f, .5f);
			RenderHelper.renderBottomFace(buf, myTime - 0.5, -.5f, -.5f, .5f, .5f);
			Tessellator.getInstance().draw();
		}
		GlStateManager.alphaFunc(GL11.GL_GREATER, .1f);
		GlStateManager.enableTexture2D();
	
		
		//RenderSelection
		if(entity.isSelected()) {
			GlStateManager.disableTexture2D();
			double speedRotate = 0.025d;
			GlStateManager.color(0.4f, 0.4f, 1f, 0.6f);
			GL11.glTranslated(0, -.75f, 0);
			GL11.glPushMatrix();
			GL11.glRotated(speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
			RendererWarpCore.model.renderOnly("Rotate1");
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glRotated(180 + speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
			RendererWarpCore.model.renderOnly("Rotate1");
			GL11.glPopMatrix();
			GlStateManager.enableTexture2D();
		}
		
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		
		RayTraceResult hitObj = Minecraft.getMinecraft().objectMouseOver;
		if(hitObj != null && hitObj.entityHit == entity) {
			
			GL11.glPushMatrix();
			GlStateManager.color(1, 1, 1);
			GL11.glTranslated(x, y + sizeScale*0.03f, z);
			sizeScale = .1f*sizeScale;
			GL11.glScaled(sizeScale,sizeScale,sizeScale);
			
			//Render atmosphere UI/planet info
			
			RenderHelper.setupPlayerFacingMatrix(Minecraft.getMinecraft().player.getDistanceSq(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z), 0, 0, 0);
			buffer = Tessellator.getInstance().getBuffer();
			
			//Draw Mass indicator
			Minecraft.getMinecraft().renderEngine.bindTexture(RenderPlanetUIEntity.planetUIFG);
			GlStateManager.color(1, 1, 1,0.8f);
			renderMassIndicator(buffer, body.getTemperature()/200f);
			
			//Draw background
			GlStateManager.color(1, 1, 1,1);
			Minecraft.getMinecraft().renderEngine.bindTexture(RenderPlanetUIEntity.planetUIBG);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, 1, -40, -25, 40, 55, 1, 0, 1, 0);
			Tessellator.getInstance().draw();
			
			//Render planet name
			RenderHelper.cleanupPlayerFacingMatrix();
			RenderHelper.renderTag(Minecraft.getMinecraft().player.getDistanceSq(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z), body.getName(), 0, .9, 0, 5);
			RenderHelper.renderTag(Minecraft.getMinecraft().player.getDistanceSq(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z), "Num Planets: " + body.getNumPlanets(), 0, .6, 0, 5);

			GL11.glPopMatrix();
		}

		//Clean up and make player not transparent
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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
