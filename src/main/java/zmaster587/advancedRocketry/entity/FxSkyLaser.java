package zmaster587.advancedRocketry.entity;


import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.client.render.RenderLaser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class FxSkyLaser extends EntityFX {
	
	
	static RenderLaser render = new RenderLaser(0.75, new float[] { 0.2f, 0.2f, 0.8f, 0.0f}, new float[] { 0.2f, 0.2f, 0.8f, 0.9f});
	
	public FxSkyLaser(World world, double x,
			double y, double z) {
		super(world, x, y, z, 0, 0, 0);

		this.prevPosX = this.posX = this.lastTickPosX = x;
		this.prevPosY = this.posY = this.lastTickPosY = y;
		this.prevPosZ = this.posZ = this.lastTickPosZ = z;
		this.particleMaxAge = (int)(10.0D);
	}

	@Override
	public void renderParticle(Tessellator tess, float x1,
			float y1, float z1, float x2,
			float y2, float z2) {

		//Will this break rendering?
		EntityPlayer player  = Minecraft.getMinecraft().thePlayer;
		Tessellator.instance.draw();
		render.doRender(this, this.posX - player.posX, this.posY - player.posY, this.posZ - player.posZ, 0, 0);
		Tessellator.instance.startDrawingQuads();
	}

	@Override
	public int getFXLayer() {
		return 0;
	}

	@Override
	public void onUpdate() {

		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setDead();
		}
	}
}
