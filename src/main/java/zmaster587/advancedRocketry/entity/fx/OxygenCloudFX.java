package zmaster587.advancedRocketry.entity.fx;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class OxygenCloudFX extends EntityFX {
	public static final ResourceLocation icon = new ResourceLocation("advancedrocketry:textures/particle/soft.png");

	
	public OxygenCloudFX(World world, double x,
			double y, double z, double motx, double moty, double motz) {
		super(world, x, y, z, motx, moty, motz);
		
		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ = z;
		
		float chroma = this.rand.nextFloat()*0.2f;
        this.particleRed = .7F + chroma;
        this.particleGreen = .7F + chroma;
        this.particleBlue = .9f;
        this.setSize(0.001F, 0.001F);
        this.particleScale = (float)(this.rand.nextFloat() * 0.6F + 6F);
        this.motionX = motx;
        this.motionY = moty;
        this.motionZ = motz;
        this.particleMaxAge = (int)(100.0D);
        this.particleAlpha  = 0;
        this.particleScale=0.01f;
	}
	
	@Override
	public int getFXLayer() {
		return 2;
	}
	
	@Override
	public void renderParticle(Tessellator tess, float x1,
			float y1, float z1, float x2,
			float y2, float z2) {
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
		
		GL11.glPushMatrix();
		//GL11.glBlendFunc( GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_ALPHA );
		//tess.setBrightness(0);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)x1 - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)x1 - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)x1 - interpPosZ);
        float f10 = 0.1F * this.particleScale;
        
        
        
        tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        
        tess.addVertexWithUV((double)(f11 - y1 * f10 - y2 * f10), (double)(f12 - z1 * f10), (double)(f13 - x2 * f10 - z2 * f10), 1, 1);
        tess.addVertexWithUV((double)(f11 - y1 * f10 + y2 * f10), (double)(f12 + z1 * f10), (double)(f13 - x2 * f10 + z2 * f10), 1, 0);
        tess.addVertexWithUV((double)(f11 + y1 * f10 + y2 * f10), (double)(f12 + z1 * f10), (double)(f13 + x2 * f10 + z2 * f10), 0, 0);
        tess.addVertexWithUV((double)(f11 + y1 * f10 - y2 * f10), (double)(f12 - z1 * f10), (double)(f13 + x2 * f10 - z2 * f10), 0, 1);
        //GL11.glEnable(GL11.GL_BLEND);
        
		GL11.glPopMatrix();
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
        this.particleAlpha = 0.2f*MathHelper.sin((float)Math.PI*(this.particleAge)/ (float)(this.particleMaxAge));
        this.particleScale = 10f*MathHelper.sin((float)Math.PI*(this.particleAge)/ (float)(this.particleMaxAge));
        
        this.motionX *= 1.01;
        this.motionY *= 1.01;
        this.motionZ *= 1.01;
        
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
        
        this.setPosition(posX + this.motionX, posY + this.motionY, posZ  + this.motionZ);
	}
}
