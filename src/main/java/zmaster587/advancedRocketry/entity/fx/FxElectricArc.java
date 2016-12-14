package zmaster587.advancedRocketry.entity.fx;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FxElectricArc  extends EntityFX {
	public static final ResourceLocation icon = new ResourceLocation("advancedrocketry:textures/particle/hardSquare.png");
	int numRecursions;
	
	public FxElectricArc(World world, double x,
			double y, double z, double sizeMultiplier) {
		super(world, x, y, z, 0, 0, 0);
		
		this.prevPosX = this.posX = this.lastTickPosX = x;
		this.prevPosY = this.posY = this.lastTickPosY = y;
		this.prevPosZ = this.posZ = this.lastTickPosZ = z;
		
        this.particleRed = 1f;
        this.particleGreen = 1f;
        this.particleBlue = 1f;
        this.setSize(0.12F, 0.12F);
        this.particleScale *= sizeMultiplier;
        this.particleMaxAge = 5;
	}

	@Override
	public void renderParticle(Tessellator tess, float x1,
			float rotX, float rotXZ, float rotZ,
			float rotYZ, float rotXY) {
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
		
		
		
        float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)x1 - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)x1 - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)x1 - interpPosZ);
        float f10 = 0.1F * this.particleScale;
        
        
        
        tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        
        //GL11.glEnable(GL11.GL_BLEND);
        render(tess,x,y+ f10 * 2,z, f10, rotX, rotXZ, rotZ, rotYZ, rotXY, 0);
        render(tess,x,y,z, f10, rotX, rotXZ, rotZ, rotYZ, rotXY, 0);
	}
	
	private void render(Tessellator tess,float x, float y, float z, float scale,
			float rotX, float rotXZ, float rotZ,
			float rotYZ, float rotXY, float shearX) {
		
        tess.addVertexWithUV((double)( x - scale * (rotX + rotYZ)), (double)(y - rotXZ * scale), (double)(z - rotZ * scale - rotXY * scale), 1, 1);
        tess.addVertexWithUV((double)( x + scale * (rotYZ - rotX)), (double)(y + rotXZ * scale), (double)(z - rotZ * scale + rotXY * scale), 1, 0);
        tess.addVertexWithUV((double)( x + scale * (rotX + rotYZ)), (double)(y + rotXZ * scale), (double)(z + rotZ * scale + rotXY * scale), 0, 0);
        tess.addVertexWithUV((double)( x + scale * (rotX - rotYZ)), (double)(y - rotXZ * scale), (double)(z + rotZ * scale - rotXY * scale), 0, 1);
	}
	
	@Override
	public int getFXLayer() {
		return 0;
	}
	
	@Override
	public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        //Change color and alpha over lifespan
        this.particleAlpha = 1- this.particleAge/ (float)this.particleMaxAge;
        //this.particleGreen -= this.particleGreen * this.particleAge/ ((float)this.particleMaxAge*20);
        //this.particleRed -= this.particleRed * this.particleAge/ ((float)this.particleMaxAge*20);
        
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
        
	}
}
