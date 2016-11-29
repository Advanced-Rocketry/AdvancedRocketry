package zmaster587.advancedRocketry.entity.fx;

import org.lwjgl.opengl.GL11;

import zmaster587.libVulpes.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FxLaser extends Particle {
	
	Entity entityFrom;
	
	public FxLaser(World world, double x,
			double y, double z, Entity entityFrom) {
		super(world, x, y, z, 0, 0, 0);

		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ  = z;
		this.particleMaxAge = (int)(1.0D);
		this.entityFrom = entityFrom;
	}

	@Override
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn,
			float partialTicks, float rotationX, float rotationZ,
			float rotationYZ, float rotationXY, float rotationXZ) {
		//worldRendererIn.finishDrawing();
		
		float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		
		int i = this.getBrightnessForRender(0);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		
		double radius = .3f;
		double fwdOffset = 0.075f;
		double entityOffX = entityFrom.posX - MathHelper.cos((float) (entityFrom.rotationYaw * Math.PI/180f))*radius + fwdOffset*MathHelper.sin((float) (entityFrom.rotationYaw * Math.PI/180f));
		double entityOffY = entityFrom.posY + (entityFrom.getEntityId() == entityIn.getEntityId() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 ? entityIn.getEyeHeight() - 0.12f : 1.15f);
		double entityOffZ = entityFrom.posZ - MathHelper.sin((float) (entityFrom.rotationYaw * Math.PI/180f))*radius - fwdOffset*MathHelper.cos((float) (entityFrom.rotationYaw * Math.PI/180f));
		
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 0, 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		
		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		GL11.glLineWidth(5);
		GlStateManager.color(0.8f, 0.2f, 0.2f, .4f);
		
		buffer.pos(entityOffX - entityIn.posX, entityOffY - entityIn.posY, entityOffZ - entityIn.posZ).endVertex();
		buffer.pos(x, y, z).endVertex();
		
		
		Tessellator.getInstance().draw();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
		GlStateManager.color(1, 1, 1, 1);
		GL11.glLineWidth(1);
	}

	
	@Override
	public int getFXLayer() {
		return 3;
	}


	@Override
	public void onUpdate() {

		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setExpired();
		}
	}
	
}
