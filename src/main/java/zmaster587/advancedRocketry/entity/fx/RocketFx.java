package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RocketFx extends Particle {

	public static final ResourceLocation icon = new ResourceLocation("advancedrocketry:textures/particle/soft.png");

	
	public RocketFx(World world, double x,
			double y, double z, double motx, double moty, double motz, float scale) {
		super(world, x, y, z, motx, moty, motz);
		
		this.prevPosX = this.posX= x;
		this.prevPosY = this.posY= y;
		this.prevPosZ = this.posZ= z;
		
        this.particleRed = 0.9F + this.rand.nextFloat()/10f;
        this.particleGreen = 0.6F + this.rand.nextFloat()/5f;
        this.particleBlue = 0.0F;
        this.setSize(0.12F*scale, 0.12F*scale);
        this.particleScale *= (this.rand.nextFloat() * 0.6F + 6F)*scale;
        this.motionX = motx;
        this.motionY = moty;
        this.motionZ = motz;
        this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.6D));
	}
	public RocketFx(World world, double x,
			double y, double z, double motx, double moty, double motz) {
		this(world, x, y,z, motx, moty, motz, 1.0f);
	}
	
	@Override
	public int getFXLayer() {
		return 2;
	}
	
	@Override
	public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        //Change color and alpha over lifespan
        this.particleAlpha = 1- this.particleAge/ (float)this.particleMaxAge;
        this.particleGreen -= this.particleGreen * this.particleAge/ ((float)this.particleMaxAge*2);
        
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }
        
        this.setPosition(posX + this.motionX, posY + this.motionY, posZ  + this.motionZ);
	}
}
