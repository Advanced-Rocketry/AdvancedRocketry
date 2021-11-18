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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class FxLaserHeat extends SpriteTexturedParticle {

	
	double size;
	
	public FxLaserHeat(World world, double x,
			double y, double z, double size) {
		super((ClientWorld) world, x, y, z);

		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ  = z;
		this.maxAge = (int)(20.0D);
		this.size = size;
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
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		
	
		double size = this.size*particleAlpha;
		RenderHelper.renderCube(new MatrixStack(), buffer, x - size, y - size, z - size, x + size, y + size, z + size,0.8f, 0.2f, 0.2f, particleAlpha);
		
		
		Tessellator.getInstance().draw();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glLineWidth(1);
	}

	@Nonnull
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
