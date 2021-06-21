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

public class FxElectricArc extends SpriteTexturedParticle {
	int numRecursions;

	public FxElectricArc(World world, double x,
			double y, double z, float sizeMultiplier) {
		super((ClientWorld) world, x, y, z);

		this.prevPosX = this.posX = x;
		this.prevPosY = this.posY = y;
		this.prevPosZ = this.posZ = z;

		this.particleRed = 1f;
		this.particleGreen = 1f;
		this.particleBlue = 1f;
		this.setSize(0.12F*sizeMultiplier, 0.12F*sizeMultiplier);
		this.maxAge = 5;
	}

<<<<<<< HEAD
=======
	@Override
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn,
			float x1,
			float rotX, float rotXZ, float rotZ,
			float rotYZ, float rotXY) {

		Minecraft.getMinecraft().getTextureManager().bindTexture(icon);

		float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)x1 - interpPosX);
		float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)x1 - interpPosY);
		float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)x1 - interpPosZ);
		float f10 = 0.1F * this.particleScale;


		//GL11.glEnable(GL11.GL_BLEND);
		render(worldRendererIn,x,y+ f10 * 2,z, f10, rotX, rotXZ, rotZ, rotYZ, rotXY, 0);
		render(worldRendererIn,x,y,z, f10, rotX, rotXZ, rotZ, rotYZ, rotXY, 0);
	}

	private void render(BufferBuilder tess, float x, float y, float z, float scale,
			float rotX, float rotXZ, float rotZ,
			float rotYZ, float rotXY, float shearX) {
		
		int i = this.getBrightnessForRender(0);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		tess.pos(x - scale * (rotX + rotYZ), y - rotXZ * scale, z - rotZ * scale - rotXY * scale).tex(1,1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		tess.pos(x + scale * (rotYZ - rotX), y + rotXZ * scale, z - rotZ * scale + rotXY * scale).tex(1,0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		tess.pos(x + scale * (rotX + rotYZ), y + rotXZ * scale, z + rotZ * scale + rotXY * scale).tex(0,0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		tess.pos(x + scale * (rotX - rotYZ), y - rotXZ * scale, z + rotZ * scale - rotXY * scale).tex(0,1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
>>>>>>> origin/feature/nuclearthermalrockets

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
		//this.particleGreen -= this.particleGreen * this.particleAge/ ((float)this.particleMaxAge*20);
		//this.particleRed -= this.particleRed * this.particleAge/ ((float)this.particleMaxAge*20);

		if (this.age++ >= this.maxAge)
		{
			this.setExpired();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite p_i50630_1_) {
			this.spriteSet = p_i50630_1_;
		}

		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			FxElectricArc arc = new FxElectricArc(worldIn, x, y, z, 1f);
			arc.selectSpriteWithAge(spriteSet);
			return arc;
		}
	}
}
