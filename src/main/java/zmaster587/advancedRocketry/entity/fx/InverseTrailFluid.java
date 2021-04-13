package zmaster587.advancedRocketry.entity.fx;


import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class InverseTrailFluid extends InverseTrailFx {
	double initX, initZ, distX, distZ;
	
	public InverseTrailFluid(World world, double x,
			double y, double z, double toX, double toY, double toZ, int color, int time) {
		super(world, x, y, z, 0, 0, 0);
		initX = toX;
		initZ = toZ;
		distX = (toX - x);
		distZ = (toZ - z);
		this.motionY = (toY - y)/(float)time;
		icon = new ResourceLocation("advancedrocketry:textures/particle/softrounddistorted.png");
		
		float intensity = ((float)(Math.random())*0.3f) + 0.7f;
		
		this.particleRed = (float) (((color >> 16) & 0xFF)/255f) *intensity;
		this.particleGreen = (float) (((color >> 8) & 0xFF)/255f)*intensity;
		this.particleBlue = (float) (((color& 0xFF)/255f))*intensity;
		
        this.maxAge = time;
        this.particleScale = (float) (this.rand.nextFloat() * 0.25F) + 0.75f;
	}
	
	@Override
	public void tick() {
		this.motionX = this.posX - this.prevPosX;
		this.motionZ = this.posZ - this.prevPosZ;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        //Change color and alpha over lifespan
        this.particleAlpha =  0.25f*this.age/ (float)this.maxAge;
        
        double normalizedAge = 1-(this.age/(double)this.maxAge);
        
        double newPosX = initX + distX*normalizedAge*normalizedAge*normalizedAge*normalizedAge;
        double newPosZ = initZ + distZ*normalizedAge*normalizedAge*normalizedAge*normalizedAge;
        
        if (this.age++ >= this.maxAge)
        {
            this.setExpired();
        }
        
        this.setPosition(newPosX, posY + this.motionY, newPosZ);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;
		
		public static IAnimatedSprite spriteSet2;

		public Factory(IAnimatedSprite p_i50630_1_) {
			this.spriteSet = p_i50630_1_;
			spriteSet2 = p_i50630_1_;
		}

		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			InverseTrailFluid arc = new InverseTrailFluid(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, 0xFFFFFF, 0xFFF);
			arc.selectSpriteWithAge(spriteSet);
			return arc;
		}
	}
}