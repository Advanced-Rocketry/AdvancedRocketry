package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class FxLaserSpark extends EntityFX {
	
	double length;
	
	public FxLaserSpark(World world, double x,
			double y, double z, double velX, double velY, double velZ, double length) {
		super(world, x, y, z, 0, 0, 0);

		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ  = z;
		this.particleMaxAge = (int)(10.0D);
		this.motionX = velX;
		this.motionY = velY;
		this.motionZ = velZ;
		this.length = length;
	}

	@Override
	public void renderParticle(Tessellator worldRendererIn,
			float partialTicks, float rotationX, float rotationZ,
			float rotationYZ, float rotationXY, float rotationXZ) {
		//worldRendererIn.finishDrawing();
		
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
		
		worldRendererIn.startDrawing(GL11.GL_LINES);
		GL11.glLineWidth(1);
		GL11.glColor4f(0.8f, 0.2f, 0.2f, particleAlpha);
		
		x += motionX*particleAge;
		y += motionY*particleAge;
		z += motionZ*particleAge;
				
		worldRendererIn.addVertex(x,y,z);
		worldRendererIn.addVertex(x + motionX*length,y + motionY*length,z + motionZ*length);
		
		
		worldRendererIn.draw();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glLineWidth(1);
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
