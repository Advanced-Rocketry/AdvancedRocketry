package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.entity.EntityHoverCraft;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderHoverCraft extends Render<EntityHoverCraft> implements IRenderFactory<EntityHoverCraft> {

    private static WavefrontObject hoverCraft;

    static {

        try {
            hoverCraft = new WavefrontObject(new ResourceLocation("advancedrocketry:models/hoverCraft.obj"));
        } catch (ModelFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public ResourceLocation hovercraftTexture = new ResourceLocation("advancedRocketry:textures/models/hoverCraft.png");

    public RenderHoverCraft(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public Render<? super EntityHoverCraft> createRenderFor(
            RenderManager manager) {
        return new RenderHoverCraft(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHoverCraft entity) {
        return hovercraftTexture;
    }

    @Override
    public boolean shouldRender(EntityHoverCraft livingEntity,
                                ICamera camera, double camX, double camY, double camZ) {
        // TODO Auto-generated method stub
        //return super.shouldRender(livingEntity, camera, camX, camY, camZ);
        return true;
    }

    @Override
    public void doRender(EntityHoverCraft entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {


        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 1, z);
        GL11.glRotated(180 - entityYaw, 0, 1, 0);
        bindTexture(hovercraftTexture);
        hoverCraft.renderAll();

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GlStateManager.color(0.1f, 0.1f, 1f, 0.8f);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);

        final float start = -0.85f - (entity.world.getTotalWorldTime() % 10) * 0.01f;
        final int count = 5;
        final float offsetX = 0.5f;
        final float offsetZ = 1.9f;
        float offset = -0.1f;


        for (int i = 0; i < count; i++) {
            float newRadius = (offset * (count - i) - 0.85f - start) * 0.5f;

            RenderHelper.renderTopFace(buffer, start + offset * i, -newRadius, -newRadius, newRadius, newRadius);
            RenderHelper.renderBottomFace(buffer, start + offset * i, -newRadius, -newRadius, newRadius, newRadius);

            RenderHelper.renderTopFace(buffer, start + offset * i, -newRadius + offsetX, -newRadius + offsetZ, newRadius + offsetX, newRadius + offsetZ);
            RenderHelper.renderBottomFace(buffer, start + offset * i, -newRadius + offsetX, -newRadius + offsetZ, newRadius + offsetX, newRadius + offsetZ);

            RenderHelper.renderTopFace(buffer, start + offset * i, -newRadius - offsetX, -newRadius + offsetZ, newRadius - offsetX, newRadius + offsetZ);
            RenderHelper.renderBottomFace(buffer, start + offset * i, -newRadius - offsetX, -newRadius + offsetZ, newRadius - offsetX, newRadius + offsetZ);
        }
        Tessellator.getInstance().draw();


        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
}
