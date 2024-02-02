package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class RendererRollingMachine extends TileEntitySpecialRenderer {
    WavefrontObject model;

    ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/rollingMachine.png");

    public RendererRollingMachine() {
        try {
            model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/rollingMachine.obj"));
        } catch (ModelFormatException e) {
            e.printStackTrace();
        }
        model.renderOnly("Hull");
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
        EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
        GL11.glRotatef((front.getFrontOffsetX() == 1 ? 180 : 0) + front.getFrontOffsetZ() * 90f, 0, 1, 0);
        GL11.glTranslated(-.5f, 0f, -1.5f);


        ItemStack outputStack;
        if (multiBlockTile.isRunning()) {
            float progress = multiBlockTile.getProgress(0) / (float) multiBlockTile.getTotalProgress(0);

            bindTexture(texture);
            model.renderOnly("Hull");

            GL11.glPushMatrix();
            GL11.glTranslatef(1.375f, 0.6875f, 0);
            GL11.glRotatef(-progress * 720, 0, 0, 1);
            model.renderOnly("Roller_1");
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(1.9375f, 0.6875f, 0f);
            GL11.glRotatef(-progress * 720, 0, 0, 1);
            model.renderOnly("Roller_2");
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(1.625f + 0.03125f, 1.125f, 0f);
            GL11.glRotatef(progress * 720, 0, 0, 1);
            model.renderOnly("Roller_2");
            GL11.glPopMatrix();

        } else {
            bindTexture(texture);
            model.renderOnly("Hull");

            GL11.glPushMatrix();
            GL11.glTranslatef(1.375f, 0.6875f, 0);
            model.renderOnly("Roller_1");
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(1.9375f, 0.6875f, 0f);
            model.renderOnly("Roller_2");
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(1.625f + 0.03125f, 1.125f, 0f);
            model.renderOnly("Roller_2");
            GL11.glPopMatrix();

        }
        GL11.glPopMatrix();
    }
}
