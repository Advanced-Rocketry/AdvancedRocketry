package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OxygenCloudFX extends SpriteTexturedParticle {

	
	public OxygenCloudFX(World world, double x,
			double y, double z, double motx, double moty, double motz) {
		super((ClientWorld)world, x, y, z, motx, moty, motz);

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
        this.maxAge = (int)(100.0D);
        this.particleAlpha  = 0;
        this.particleScale=0.01f;
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
        this.particleAlpha = 0.2f*MathHelper.sin((float)Math.PI*(this.age)/ (float)(this.maxAge));
        this.particleScale = 10f*MathHelper.sin((float)Math.PI*(this.age)/ (float)(this.maxAge));
        
        this.motionX *= 1.01;
        this.motionY *= 1.01;
        this.motionZ *= 1.01;
        
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
			OxygenCloudFX arc = new OxygenCloudFX(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			arc.selectSpriteWithAge(spriteSet);
			return arc;
		}
	}
}
