package zmaster587.advancedRocketry.entity;


import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.client.render.RenderLaser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class FxSkyLaser extends Particle {
	
	
	static RenderLaser render = new RenderLaser(0.75, new float[] { 0.2f, 0.2f, 0.8f, 0.0f}, new float[] { 0.2f, 0.2f, 0.8f, 0.9f});
	
	public FxSkyLaser(World world, double x,
			double y, double z) {
		super(world, x, y, z, 0, 0, 0);

		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ  = z;
		this.particleMaxAge = (int)(10.0D);
	}
	
	@Override
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn,
			float partialTicks, float rotationX, float rotationZ,
			float rotationYZ, float rotationXY, float rotationXZ) {
		//Will this break rendering?
		EntityPlayer player  = Minecraft.getMinecraft().thePlayer;
		
		render.doRender(this, this.posX - player.posX, this.posY - player.posY, this.posZ - player.posZ, 0, 0);
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	@Override
	public int getFXLayer() {
		return 0;
	}

	@Override
	public void onUpdate() {

		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setExpired();
		}
	}
}
