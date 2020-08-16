package zmaster587.advancedRocketry.entity.fx;

import org.lwjgl.util.Color;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class InverseTrailFluid extends InverseTrailFx {
	double initX, initZ, distX, distZ;
	
	public InverseTrailFluid(World world, double x,
			double y, double z, double toX, double toY, double toZ, int color, int time) {
		super(world, x, y, z, 0, 0, 0);
		initX = toX;
		initZ = toZ;
		distX = (toX - x);
		distZ = (toZ - z);
		this.getMotion().y = (toY - y)/(float)time;
		icon = new ResourceLocation("advancedrocketry:textures/particle/softrounddistorted.png");
		
		float intensity = ((float)(Math.random())*0.3f) + 0.7f;
		
		this.particleRed = (float) (((color >> 16) & 0xFF)/255f) *intensity;
		this.particleGreen = (float) (((color >> 8) & 0xFF)/255f)*intensity;
		this.particleBlue = (float) (((color& 0xFF)/255f))*intensity;
		
        this.particleMaxAge = time;
        this.particleScale = (float) (this.rand.nextFloat() * 0.25F) + 0.75f;
	}
	
	@Override
	public void onUpdate() {
		this.motionX = this.posX - this.prevPosX;
		this.motionZ = this.posZ - this.prevPosZ;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        //Change color and alpha over lifespan
        this.particleAlpha =  0.25f*this.particleAge/ (float)this.particleMaxAge;
        
        double normalizedAge = 1-(this.particleAge/(double)this.particleMaxAge);
        
        double newPosX = initX + distX*normalizedAge*normalizedAge*normalizedAge*normalizedAge;
        double newPosZ = initZ + distZ*normalizedAge*normalizedAge*normalizedAge*normalizedAge;
        
        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }
        
        this.setPosition(newPosX, posY + this.getMotion().y, newPosZ);
	}
}