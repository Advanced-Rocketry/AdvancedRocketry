package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RocketFx extends SpriteTexturedParticle {

	public static final ResourceLocation icon = new ResourceLocation("advancedrocketry:textures/particle/soft.png");

	
	public RocketFx(World world, double x,
			double y, double z, double motx, double moty, double motz, float scale) {
		super((ClientWorld)world, x, y, z, motx, moty, motz);
		
		
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
        this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.6D));
	}
	public RocketFx(World world, double x,
			double y, double z, double motx, double moty, double motz) {
		this(world, x, y,z, motx, moty, motz, 1.0f);
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	
	@Override
	public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        //Change color and alpha over lifespan
        this.particleAlpha = 1- this.age/ (float)this.maxAge;
        this.particleGreen -= this.particleGreen * this.age/ ((float)this.maxAge*2);
        
        if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        }
        
        this.setPosition(posX + this.motionX, posY + this.motionY, posZ  + this.motionZ);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite p_i50630_1_) {
			this.spriteSet = p_i50630_1_;
		}

		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			RocketFx arc = new RocketFx(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			arc.selectSpriteWithAge(spriteSet);
			return arc;
		}
	}
}
