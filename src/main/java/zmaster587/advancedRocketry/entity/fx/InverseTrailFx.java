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

public class InverseTrailFx extends SpriteTexturedParticle {
	protected ResourceLocation icon = new ResourceLocation("advancedrocketry:textures/particle/soft.png");
    
	public InverseTrailFx(World world, double x,
			double y, double z, double motx, double moty, double motz) {
		super((ClientWorld) world, x, y, z);
		
		float chroma = this.rand.nextFloat()*0.2f;
        this.particleRed = .8F + chroma;
        this.particleGreen = .8F + chroma;
        this.particleBlue = .8F + chroma;
        this.setSize(0.12F, 0.12F);
        this.maxAge = (int)(100.0D);
        this.particleScale = (float) (this.rand.nextFloat() * 0.6F + 6F + Math.pow(1.04f, this.maxAge));
        this.motionX = -motx;
        this.motionY = -moty;
        this.motionZ = -motz;
        

        
        
		this.prevPosX = this.posX = x + motx*this.maxAge;
		this.prevPosY = this.posY = y + moty*this.maxAge;
		this.prevPosZ = this.posZ = z + motz*this.maxAge;
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
        this.particleAlpha =  0.25f*this.age/ (float)this.maxAge;
        this.particleScale /= 1.02f;
        
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
			InverseTrailFx arc = new InverseTrailFx(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			arc.selectSpriteWithAge(spriteSet);
			return arc;
		}
	}
}