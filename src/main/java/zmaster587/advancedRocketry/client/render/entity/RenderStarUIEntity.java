package zmaster587.advancedRocketry.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityUIButton;
import zmaster587.advancedRocketry.entity.EntityUIPlanet;
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
		
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		GL11.glScalef(entity.getScale(), entity.getScale(), entity.getScale());
		
		RenderHelper.setupPlayerFacingMatrix(Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity(entity), 0,-.45,0);
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.locationSunNew);
		
		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
		
		GL11.glColor3ub((byte)(body.getColorRGB8() & 0xff), (byte)((body.getColorRGB8() >>> 8) & 0xff), (byte)((body.getColorRGB8() >>> 16) & 0xff));
		//GlStateManager.color();
		
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.renderNorthFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
		Tessellator.getInstance().draw();
		
		
		RenderHelper.cleanupPlayerFacingMatrix();
		
		
		//Render hololines
		GL11.glPushMatrix();
		GL11.glScaled(.1, .1, .1);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);

		VertexBuffer buf = Tessellator.getInstance().getBuffer();
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		float myTime = ((entity.worldObj.getTotalWorldTime() & 0xF)/16f);
		
		for(int i = 0; i < 4; i++ ) {
			myTime = ((i*4 + entity.worldObj.getTotalWorldTime() & 0xF)/16f);

			GlStateManager.color(0, 1f, 1f, .2f*(1-myTime));
			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
			RenderHelper.renderTopFace(buf, myTime, -.5f, -.5f, .5f, .5f);
			Tessellator.getInstance().draw();
		}
		GlStateManager.alphaFunc(GL11.GL_GREATER, .1f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	
		
		//RenderSelection
		if(entity.isSelected()) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
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
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		//Clean up and make player not transparent
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GlStateManager.color(1, 1, 1, 1);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
	}
}
