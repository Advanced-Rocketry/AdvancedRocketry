package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.world.World;

public class TrailFx extends InverseTrailFx {
	//public static final ResourceLocation icon = new ResourceLocation("advancedrocketry:textures/particle/soft.png");

	
	public TrailFx(World world, double x,
			double y, double z, double motx, double moty, double motz) {
		super(world, x, y, z, motx, moty, motz);
		
		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ = z;
		
		float chroma = this.rand.nextFloat()*0.2f;
        this.particleRed = .4F + chroma;
        this.particleGreen = .4F + chroma;
        this.particleBlue = .4F + chroma;
        this.setSize(0.12F, 0.12F);
        this.particleScale = (float)(this.rand.nextFloat() * 0.6F + 6F);
        this.motionX = motx;
        this.motionY = moty;
        this.motionZ = motz;
        this.particleMaxAge = (int)(1000.0D);
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
        this.particleAlpha = 1 - this.particleAge/ (float)this.particleMaxAge;
        this.particleScale *= 1.002f;
        
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }
        
        this.setPosition(posX + this.motionX, posY + this.motionY, posZ  + this.motionZ);
	}
}
