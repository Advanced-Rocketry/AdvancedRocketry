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

public class FxGravityEffect extends SpriteTexturedParticle {
	public FxGravityEffect(World world, double x,
			double y, double z, double motx, double moty, double motz) {
		super((ClientWorld) world, x, y, z);
		
        this.particleRed = .1F;
        this.particleGreen = 1F;
        this.particleBlue = 1F;
        this.particleAlpha = .2F;
        this.setSize(0.12F, 0.12F);
        this.maxAge = (int)(5.0D);
        this.particleScale = 0.2f;
        this.motionX = motx;
        this.motionY = moty;
        this.motionZ = motz;
        
        
		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ = z;
	}


	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@Override
	public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        //Change color and alpha over lifespan
        this.particleAlpha /= 1.1f;
        this.particleScale *= 1.2f;
        
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
			FxGravityEffect grav = new FxGravityEffect(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			grav.selectSpriteWithAge(spriteSet);
			return grav;
		}
	}
}
