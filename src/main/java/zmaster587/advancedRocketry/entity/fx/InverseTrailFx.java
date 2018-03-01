package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class InverseTrailFx extends Particle {
	public static final ResourceLocation icon = new ResourceLocation("advancedrocketry:textures/particle/soft.png");

	
	public InverseTrailFx(World world, double x,
			double y, double z, double motx, double moty, double motz) {
		super(world, x, y, z, motx, moty, motz);
		
		float chroma = this.rand.nextFloat()*0.2f;
        this.particleRed = .8F + chroma;
        this.particleGreen = .8F + chroma;
        this.particleBlue = .8F + chroma;
        this.setSize(0.12F, 0.12F);
        this.particleMaxAge = (int)(100.0D);
        this.particleScale = (float) (this.rand.nextFloat() * 0.6F + 6F + Math.pow(1.04f, this.particleMaxAge));
        this.motionX = -motx;
        this.motionY = -moty;
        this.motionZ = -motz;
        
        
		this.prevPosX = this.posX = x + motx*this.particleMaxAge;
		this.prevPosY = this.posY = y + moty*this.particleMaxAge;
		this.prevPosZ = this.posZ = z + motz*this.particleMaxAge;
	}

	@Override
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn,
			float partialTicks, float rotationX, float rotationZ,
			float rotationYZ, float rotationXY, float rotationXZ) {
		//super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX,
				//rotationZ, rotationYZ, rotationXY, rotationXZ);
		
		float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		float f10 = 0.25F * this.particleScale;
		
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
        worldRendererIn.finishDrawing();
        worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        
		worldRendererIn.pos((double)(f11 - rotationX * f10 - rotationXY * f10), (double)(f12 - rotationZ * f10), (double)(f13 - rotationYZ * f10 - rotationXZ * f10)).tex(1, 1).color(this.particleRed, this.particleGreen, this.particleBlue, 1f).lightmap(j, k).endVertex();
		worldRendererIn.pos((double)(f11 - rotationX * f10 + rotationXY * f10), (double)(f12 + rotationZ * f10), (double)(f13 - rotationYZ * f10 + rotationXZ * f10)).tex(1, 0).color(this.particleRed, this.particleGreen, this.particleBlue, 1f).lightmap(j, k).endVertex();
		worldRendererIn.pos((double)(f11 + rotationX * f10 + rotationXY * f10), (double)(f12 + rotationZ * f10), (double)(f13 + rotationYZ * f10 + rotationXZ * f10)).tex(0, 0).color(this.particleRed, this.particleGreen, this.particleBlue, 1f).lightmap(j, k).endVertex();
		worldRendererIn.pos((double)(f11 + rotationX * f10 - rotationXY * f10), (double)(f12 - rotationZ * f10), (double)(f13 + rotationYZ * f10 - rotationXZ * f10)).tex(0, 1).color(this.particleRed, this.particleGreen, this.particleBlue, 1f).lightmap(j, k).endVertex();
		Tessellator.getInstance().draw();
		worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}

	
	
	
	@Override
	public int getFXLayer() {
		return 2;
	}
	
    public boolean shouldDisableDepth()
    {
        return true;
    }
	
	@Override
	public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        //Change color and alpha over lifespan
        this.particleAlpha =  0.25f*this.particleAge/ (float)this.particleMaxAge;
        this.particleScale /= 1.02f;
        
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }
        
        this.setPosition(posX + this.motionX, posY + this.motionY, posZ  + this.motionZ);
	}
}