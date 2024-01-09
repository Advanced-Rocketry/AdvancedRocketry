package zmaster587.advancedRocketry.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.libVulpes.render.RenderHelper;

import org.jetbrains.annotations.Nullable;

public class RenderLaser extends Render implements IRenderFactory<EntityLaserNode> {

	private static final ResourceLocation flare = new ResourceLocation("advancedrocketry", "textures/entity/Flare.png");

	float[] color;
	float[] flareColor;
	//float flareColo
	private double size;
	
	public RenderLaser(double size, float[] flarecolor, float[] color) {
		super(Minecraft.getMinecraft().getRenderManager());
		this.size = size;
		this.flareColor = flarecolor;
		this.color = color;
	}
	
	@Override
	public void doRender(@Nullable Entity entity, double x, double y, double z,
			float f, float f1) {

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Minecraft.getMinecraft().renderEngine.bindTexture(flare);
		//bindTexture(flare);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

		buffer.color(flareColor[0],flareColor[1],flareColor[2],flareColor[3]);

		for(int i = 0; i < 4; i++) {
			RenderHelper.renderBottomFaceWithUV(buffer, -y + 200, -(i*6) - x, -(i*6) - z, (i*6) - x, (i*6) - z, 0, 1, 0, 1);
		}

		buffer.finishDrawing();

		GlStateManager.disableTexture2D();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		GlStateManager.color(color[0], color[1], color[2], color[3]);//0.9F, 0.2F, 0.3F, 0.5F);

		for(float radius = 0.25F; radius < size; radius += .25F) {

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				buffer.pos(- x , -y + 200,  - z).endVertex();
				buffer.pos(- x, -y + 200, - z).endVertex();
				buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F).endVertex();
				buffer.pos((radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F).endVertex();
			}

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				buffer.pos(- x, -y + 200,- z).endVertex();
				buffer.pos(- x, -y + 200, - z).endVertex();
				buffer.pos((radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F).endVertex();
				buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F).endVertex();
			}
		}

		Tessellator.getInstance().draw();

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableFog();
		GlStateManager.depthMask(true);
		GL11.glPopMatrix();
		GlStateManager.color(1, 1, 1,1);

	}
	
	public void doRender(Particle entity, double x, double y, double z,
			float f, float f1) {

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0, 0);
		Minecraft.getMinecraft().renderEngine.bindTexture(flare);
		//bindTexture(flare);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		GlStateManager.color(flareColor[0],flareColor[1],flareColor[2],flareColor[3]);

		for(int i = 0; i < 4; i++) {
			RenderHelper.renderBottomFaceWithUV(buffer, -y + 200, -(i*6) - x, -(i*6) - z, (i*6) - x, (i*6) - z, 0, 1, 0, 1);
		}

		Tessellator.getInstance().draw();

		GlStateManager.disableTexture2D();
		OpenGlHelper.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE, 0, 0);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		GlStateManager.color(color[0], color[1], color[2], color[3]);//0.9F, 0.2F, 0.3F, 0.5F);

		for(float radius = 0.25F; radius < size; radius += .25F) {

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				buffer.pos(- x , -y + 200,  - z).endVertex();
				buffer.pos(- x, -y + 200, - z).endVertex();
				buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F).endVertex();
				buffer.pos((radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F).endVertex();
			}

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				buffer.pos(- x, -y + 200,- z).endVertex();
				buffer.pos(- x, -y + 200, - z).endVertex();
				buffer.pos((radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F).endVertex();
				buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F).endVertex();
			}
		}

		Tessellator.getInstance().draw();

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableFog();
		GlStateManager.depthMask(true);
		GL11.glPopMatrix();
		
		GlStateManager.color(1f, 1f, 1f,1f);
		//Clean up and make player not transparent
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);

	}

	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y,
			double z, float yaw, float partialTicks) {
		//Do not render shadow or fire
	}
	
	@Override
	protected ResourceLocation getEntityTexture(@Nullable Entity entity) {
		return null;
	}

	@Override
	public Render<? super Entity> createRenderFor(RenderManager manager) {
		return this;
	}

}
