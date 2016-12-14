package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import zmaster587.libVulpes.render.RenderHelper;

public class FxLaserHeat extends EntityFX {

	
	double size;
	
	public FxLaserHeat(World world, double x,
			double y, double z, double size) {
		super(world, x, y, z, 0, 0, 0);

		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ  = z;
		this.particleMaxAge = (int)(20.0D);
		this.size = size;
	}

	@Override
	public void renderParticle(Tessellator worldRendererIn,
			float partialTicks, float rotationX, float rotationZ,
			float rotationYZ, float rotationXY, float rotationXZ) {

		Entity entityIn = Minecraft.getMinecraft().thePlayer;
		
		float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		
		int i = this.getBrightnessForRender(0);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 0, 0);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		
		worldRendererIn.startDrawing(GL11.GL_QUADS);
		GL11.glColor4f(0.8f, 0.2f, 0.2f, particleAlpha);
		
	
		double size = this.size*particleAlpha;
		RenderHelper.renderCubeWithUV(worldRendererIn, x - size, y - size, z - size, x + size, y + size, z + size,0,0,0,0);
		
		
		worldRendererIn.draw();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
		GL11.glColor4f(1, 1, 1, 1);
	}

	
	@Override
	public int getFXLayer() {
		return 3;
	}


	@Override
	public void onUpdate() {

		this.particleAlpha = 1 - (particleAge/(float)particleMaxAge);
		
		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setDead();
		}
	}
}
