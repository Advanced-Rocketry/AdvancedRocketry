package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.client.render.RenderLaser;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceElevator;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererSpaceElevator extends TileEntitySpecialRenderer {

    public ResourceLocation baseTexture = new ResourceLocation("advancedRocketry:textures/models/spaceelevator.png");
    WavefrontObject model;
    RenderLaser laser;

    public RendererSpaceElevator() {
        laser = new RenderLaser(1, new float[]{0, 0, 0, 0}, new float[]{1, 1, 0, 0.11f});
        try {
            model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/spaceelevator.obj"));
        } catch (ModelFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(TileEntity tile, double x,
                       double y, double z, float f, int damage, float a) {
        TileSpaceElevator multiBlockTile = (TileSpaceElevator) tile;

        if (!multiBlockTile.canRender())
            return;


        GL11.glPushMatrix();

        //Initial setup

        GL11.glTranslated(x + 0.5, y, z + .5);
        //Rotate and move the model into position
        EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));

        float rotationAmount = (multiBlockTile.isAnchorOnSpaceStation()) ? 180f : 0;
        if (front.getAxis() == EnumFacing.Axis.X) {
            GL11.glRotatef(rotationAmount, 1, 0, 0);
        } else {
            GL11.glRotatef(rotationAmount, 0, 0, 1);
        }
        GL11.glRotatef((front.getFrontOffsetX() == 1 ? 180 : 0) + front.getFrontOffsetZ() * 90f, 0, 1, 0);
        float yOffset = (multiBlockTile.isAnchorOnSpaceStation()) ? -1f : 0;
        GL11.glTranslated(4.5f, yOffset, 0.5f);

        //GL11.glTranslated(2f, 0, 0f);
        bindTexture(baseTexture);
        model.renderOnly("Anchor");
        if (multiBlockTile.isTetherConnected()) {
            model.renderOnly("Tether");
        }
        GL11.glPopMatrix();

        if (multiBlockTile.isTetherConnected() && !multiBlockTile.isAnchorOnSpaceStation()) {
            //Render Beads

            double renderX = x + multiBlockTile.getLandingLocationX() - multiBlockTile.getPos().getX() - ((front.getAxis() == EnumFacing.Axis.X) ? 0.5 : 2.5);
            double renderZ = z + multiBlockTile.getLandingLocationZ() - multiBlockTile.getPos().getZ() - ((front.getAxis() == EnumFacing.Axis.X) ? -1.5 : 0.5);

            laser.doRender((Entity) null, renderX, y + 4f, renderZ, 0, f);

            GL11.glPushMatrix();
            GL11.glTranslated(renderX + 0.5f, y + 4, renderZ + 0.5f);
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.enableBlend();
            GlStateManager.depthMask(false);

            GlStateManager.disableTexture2D();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
            GlStateManager.color(1, 1, 1, 0.11f);

            double position = (System.currentTimeMillis() % 16000) / 200f;

            for (int i = 0; i < 10; i++) {
                for (float radius = 0.25F; radius < 1.25; radius += .25F) {

                    RenderHelper.renderCube(buffer, -radius, -radius + position + i * 80 + 4, -radius, radius, radius + position + i * 80 + 4, radius);

                }
            }
            for (int i = 1; i < 11; i++) {
                for (float radius = 0.25F; radius < 1.25; radius += .25F) {

                    RenderHelper.renderCube(buffer, -radius, -radius - position + i * 80 + 4, -radius, radius, radius - position + i * 80 + 4, radius);

                }
            }

            Tessellator.getInstance().draw();

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableFog();
            GlStateManager.depthMask(true);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glPopMatrix();
        }


    }
}
