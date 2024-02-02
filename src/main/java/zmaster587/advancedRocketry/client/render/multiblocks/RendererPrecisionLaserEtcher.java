package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class RendererPrecisionLaserEtcher extends TileEntitySpecialRenderer {
    WavefrontObject model;

    ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/precisionlaseretcher.png");

    public RendererPrecisionLaserEtcher() {
        try {
            model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/precisionlaseretcher.obj"));
        } catch (ModelFormatException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(TileEntity tile, double x,
                       double y, double z, float f, int damage, float a) {
        TileMultiblockMachine multiBlockTile = (TileMultiblockMachine) tile;

        if (!multiBlockTile.canRender())
            return;

        GL11.glPushMatrix();

        //Rotate and move the model into position
        GL11.glTranslated(x + .5f, y, z + 0.5f);
        EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
        GL11.glRotatef((front.getFrontOffsetX() == 1 ? 0 : 180) + front.getFrontOffsetZ() * 90f, 0, 1, 0);
        GL11.glTranslated(0.5f, 0f, 1.5f);


        if (multiBlockTile.isRunning()) {

            float progress = multiBlockTile.getProgress(0) / (float) multiBlockTile.getTotalProgress(0) + f / (float) multiBlockTile.getTotalProgress(0);

            bindTexture(texture);
            model.renderPart("Hull");

            //Full assembly translation and render
            GL11.glPushMatrix();
            float progress2 = ((16 * progress) - (int) (16 * progress));

            if (progress < 0.875) {
                if (progress2 > 0.875) {
                    GL11.glTranslatef(0f, 0f, (progress2 - 0.875f) / 2f);
                }
                GL11.glTranslatef(0f, 0f, (progress - (progress2 / 16f)));
            } else
                GL11.glTranslatef(0f, 0f, ((1 - progress) / .15f));

            model.renderPart("Mount");

            //Render laser and laser translation
            GL11.glPushMatrix();
            if (progress < 0.875) {
                if (progress2 < 0.875f)
                    GL11.glTranslatef(-progress2, 0f, 0f);
                else
                    GL11.glTranslatef(-((1 - progress2) / .15f), 0f, 0f);
            }
            model.renderPart("Laser");
            GL11.glPopMatrix();
            GL11.glPopMatrix();


        } else {
            bindTexture(texture);
            model.renderAll();
        }
        GL11.glPopMatrix();
    }
}
