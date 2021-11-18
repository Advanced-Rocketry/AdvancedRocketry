package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class FxLaser extends SpriteTexturedParticle {
	
	Entity entityFrom;
	
	public FxLaser(World world, double x,
			double y, double z, Entity entityFrom) {
		super((ClientWorld) world, x, y, z);

		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ  = z;
		this.maxAge = (int)(1.0D);
		this.entityFrom = entityFrom;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void renderParticle(IVertexBuilder buffer2, ActiveRenderInfo renderInfo, float partialTicks) {
		//worldRendererIn.finishDrawing();
		float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks);
		float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks);
		float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks);
		
		int i = this.getBrightnessForRender(0);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		
		double radius = .3f;
		double fwdOffset = 0.075f;
		double entityOffX = entityFrom.getPosX() - MathHelper.cos((float) (entityFrom.rotationYaw * Math.PI/180f))*radius + fwdOffset*MathHelper.sin((float) (entityFrom.rotationYaw * Math.PI/180f));
		double entityOffY = entityFrom.getPosY() + (entityFrom.getEntityId() == renderInfo.getRenderViewEntity().getEntityId() && renderInfo.isThirdPerson() ? renderInfo.getRenderViewEntity().getEyeHeight() - 0.12f : 1.15f);
		double entityOffZ = entityFrom.getPosZ() - MathHelper.sin((float) (entityFrom.rotationYaw * Math.PI/180f))*radius - fwdOffset*MathHelper.cos((float) (entityFrom.rotationYaw * Math.PI/180f));
		
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		GL11.glLineWidth(5);
		GlStateManager.color4f(0.8f, 0.2f, 0.2f, .4f);
		
		buffer.pos(entityOffX - renderInfo.getRenderViewEntity().getPosX(), entityOffY - renderInfo.getRenderViewEntity().getPosY(), entityOffZ - renderInfo.getRenderViewEntity().getPosZ()).endVertex();
		buffer.pos(x, y, z).endVertex();
		
		
		Tessellator.getInstance().draw();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1, 1, 1, 1);
		GL11.glLineWidth(1);
	}

	@Nonnull
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
