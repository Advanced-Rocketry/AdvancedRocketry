package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;

public class FxLaserSpark extends SpriteTexturedParticle {
	
	double length;
	
	public FxLaserSpark(World world, double x,
			double y, double z, double velX, double velY, double velZ, double length) {
		super((ClientWorld) world, x, y, z);

		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ  = z;
		this.maxAge = (int)(10.0D);
		this.motionX = velX;
		this.motionY = velY;
		this.motionZ = velZ;
		this.length = length;
	}

	@Override
	public void renderParticle(IVertexBuilder buffer2, ActiveRenderInfo renderInfo, float partialTicks) {
		//worldRendererIn.finishDrawing();
		
		float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks);
		float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks);
		float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks);
		
		int i = this.getBrightnessForRender(0);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		GL11.glLineWidth(1);
		GlStateManager.color4f(0.8f, 0.2f, 0.2f, particleAlpha);
		
		x += motionX*age;
		y += motionY*age;
		z += motionZ*age;
				
		buffer.pos(x,y,z).endVertex();
		buffer.pos(x + motionX*length,y + motionY*length,z + motionZ*length).endVertex();
		
		
		Tessellator.getInstance().draw();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1, 1, 1, 1);
		GL11.glLineWidth(1);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.CUSTOM;
	}

	@Override
	public void tick() {

		this.particleAlpha = 1 - (age/(float)maxAge);
		
		if (this.age++ >= this.maxAge)
		{
			this.setExpired();
		}
	}
}
