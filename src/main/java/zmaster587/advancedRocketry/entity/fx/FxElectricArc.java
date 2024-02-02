package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FxElectricArc extends Particle {
    public static final ResourceLocation icon = new ResourceLocation("advancedrocketry:textures/particle/hardSquare.png");
    int numRecursions;

    public FxElectricArc(World world, double x,
                         double y, double z, double sizeMultiplier) {
        super(world, x, y, z, 0, 0, 0);

        this.prevPosX = this.posX = x;
        this.prevPosY = this.posY = y;
        this.prevPosZ = this.posZ = z;

        this.particleRed = 1f;
        this.particleGreen = 1f;
        this.particleBlue = 1f;
        this.setSize(0.12F, 0.12F);
        this.particleScale *= sizeMultiplier;
        this.particleMaxAge = 5;
    }

    @Override
    public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn,
                               float x1,
                               float rotX, float rotXZ, float rotZ,
                               float rotYZ, float rotXY) {

        Minecraft.getMinecraft().getTextureManager().bindTexture(icon);

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) x1 - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) x1 - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) x1 - interpPosZ);
        float f10 = 0.1F * this.particleScale;


        //GL11.glEnable(GL11.GL_BLEND);
        render(worldRendererIn, x, y + f10 * 2, z, f10, rotX, rotXZ, rotZ, rotYZ, rotXY, 0);
        render(worldRendererIn, x, y, z, f10, rotX, rotXZ, rotZ, rotYZ, rotXY, 0);
    }

    private void render(BufferBuilder tess, float x, float y, float z, float scale,
                        float rotX, float rotXZ, float rotZ,
                        float rotYZ, float rotXY, float shearX) {

        int i = this.getBrightnessForRender(0);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        tess.pos(x - scale * (rotX + rotYZ), y - rotXZ * scale, z - rotZ * scale - rotXY * scale).tex(1, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        tess.pos(x + scale * (rotYZ - rotX), y + rotXZ * scale, z - rotZ * scale + rotXY * scale).tex(1, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        tess.pos(x + scale * (rotX + rotYZ), y + rotXZ * scale, z + rotZ * scale + rotXY * scale).tex(0, 0).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        tess.pos(x + scale * (rotX - rotYZ), y - rotXZ * scale, z + rotZ * scale - rotXY * scale).tex(0, 1).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();

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
        this.particleAlpha = 1 - this.particleAge / (float) this.particleMaxAge;
        //this.particleGreen -= this.particleGreen * this.particleAge/ ((float)this.particleMaxAge*20);
        //this.particleRed -= this.particleRed * this.particleAge/ ((float)this.particleMaxAge*20);

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

    }
}
