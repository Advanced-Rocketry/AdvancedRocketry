package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.lwjgl.opengl.GL11;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderTank extends TileEntitySpecialRenderer {

    @Override
    public void render(TileEntity tile, double x,
                       double y, double z, float f, int damage, float a) {

        IFluidHandler fluidTile = (IFluidHandler) tile;
        FluidStack fluid = fluidTile.getTankProperties()[0].getContents();
        ResourceLocation fluidIcon = new ResourceLocation("advancedrocketry:textures/blocks/fluid/oxygen_flow.png");

        if (fluid != null && fluid.getFluid() != null) {
            GL11.glPushMatrix();

            GL11.glTranslatef((float) x, (float) y, (float) z);

            double minU = 0, maxU = 1, minV = 0, maxV = 1;
            TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
            TextureAtlasSprite sprite = map.getTextureExtry(fluid.getFluid().getStill().toString());
            if (sprite != null) {
                minU = sprite.getMinU();
                maxU = sprite.getMaxU();
                minV = sprite.getMinV();
                maxV = sprite.getMaxV();
                GlStateManager.bindTexture(map.getGlTextureId());
            } else {
                int color = fluid.getFluid().getColor();
                GL11.glColor4f(((color >>> 16) & 0xFF) / 255f, ((color >>> 8) & 0xFF) / 255f, ((color & 0xFF) / 255f), 1f);

                bindTexture(fluidIcon);
            }


            Block block = tile.getBlockType();
            Tessellator tess = Tessellator.getInstance();

            float amt = fluid.amount / (float) fluidTile.getTankProperties()[0].getCapacity();

            GlStateManager.disableLighting();
            GlStateManager.enableBlend();

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            AxisAlignedBB bb = block.getDefaultState().getBoundingBox(tile.getWorld(), tile.getPos());

            tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            RenderHelper.renderCubeWithUV(tess.getBuffer(), bb.minX + 0.01, bb.minY + 0.01, bb.minZ + 0.01, bb.maxX - 0.01, bb.maxY * amt - 0.01, bb.maxZ - 0.01, minU, maxU, minV, maxV);
            tess.draw();

            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GL11.glPopMatrix();
            GlStateManager.color(1f, 1f, 1f);
        }
    }
}


