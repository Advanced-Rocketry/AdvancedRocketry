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

public class OxygenTraceFX extends SpriteTexturedParticle {

	
	public OxygenTraceFX(World world, double x,
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
        this.particleScale = this.rand.nextFloat() * 0.6F + 6F;
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
<<<<<<< HEAD
	public void tick() {
=======
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn,
			float partialTicks, float rotationX, float rotationZ,
			float rotationYZ, float rotationXY, float rotationXZ) {
		//super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX,
				//rotationZ, rotationYZ, rotationXY, rotationXZ);
		
		float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		float f10 = 0.25F * this.particleScale;
		
        int i = 15728640;
        int j = i >> 16 & 65535;
        int k = i & 65535;
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
        worldRendererIn.finishDrawing();
        worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        
		worldRendererIn.pos(f11 - rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 - rotationYZ * f10 - rotationXZ * f10).tex(1, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		worldRendererIn.pos(f11 - rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 - rotationYZ * f10 + rotationXZ * f10).tex(1, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		worldRendererIn.pos(f11 + rotationX * f10 + rotationXY * f10, f12 + rotationZ * f10, f13 + rotationYZ * f10 + rotationXZ * f10).tex(0, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		worldRendererIn.pos(f11 + rotationX * f10 - rotationXY * f10, f12 - rotationZ * f10, f13 + rotationYZ * f10 - rotationXZ * f10).tex(0, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		Tessellator.getInstance().draw();
		worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}
	
    public boolean shouldDisableDepth()
    {
        return true;
    }
	
	@Override
	public void onUpdate() {
>>>>>>> origin/feature/nuclearthermalrockets
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        //Change color and alpha over lifespan
<<<<<<< HEAD
        this.particleAlpha = 1f*MathHelper.sin((float)Math.PI*(this.age)/ (float)(this.maxAge));
        this.particleScale = 0.5f*MathHelper.sin((float)Math.PI*(this.age)/ (float)(this.maxAge));
=======
        this.particleAlpha = MathHelper.sin((float) Math.PI * (this.particleAge) / (float) (this.particleMaxAge));
        this.particleScale = 0.5f*MathHelper.sin((float)Math.PI*(this.particleAge)/ (float)(this.particleMaxAge));
>>>>>>> origin/feature/nuclearthermalrockets
        
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
			OxygenTraceFX arc = new OxygenTraceFX(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			arc.selectSpriteWithAge(spriteSet);
			return arc;
		}
	}
}
