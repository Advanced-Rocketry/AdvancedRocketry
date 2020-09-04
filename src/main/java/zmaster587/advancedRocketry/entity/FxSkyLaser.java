package zmaster587.advancedRocketry.entity;


import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.client.render.RenderLaser;

public class FxSkyLaser extends SpriteTexturedParticle {
	
	
	static RenderLaser render = new RenderLaser(0.75, new float[] { 0.2f, 0.2f, 0.8f, 0.0f}, new float[] { 0.2f, 0.2f, 0.8f, 0.2f});
	
	public FxSkyLaser(World world, double x,
			double y, double z) {
		super((ClientWorld)world, x, y, z, 0, 0, 0);
		
		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ  = z;
		this.maxAge = (int)(10.0D);
	}
	
	@Override
	public void renderParticle(IVertexBuilder buffer2, ActiveRenderInfo renderInfo, float partialTicks) {
		//Will this break rendering?
		PlayerEntity player  = Minecraft.getInstance().player;
		//worldRendererIn.finishDrawing();
		GL11.glPushMatrix();
		GL11.glTranslated(this.posX - player.getPosX(), this.posY - player.getPosY(), this.posZ - player.getPosZ());
		//render.doRender( buffer2 );
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		//worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}


	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.CUSTOM;
	}

	@Override
	public void tick() {

		if (this.age++ >= this.maxAge)
		{
			this.setExpired();
		}
	}
}
