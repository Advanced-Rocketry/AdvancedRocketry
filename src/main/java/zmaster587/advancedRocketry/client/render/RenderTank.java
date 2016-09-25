package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class RenderTank extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f, int damage) {

		IFluidHandler fluidTile = (IFluidHandler)tile;
		FluidStack fluid = fluidTile.getTankProperties()[0].getContents();


		if(fluid != null && fluid.getFluid() != null)
		{
			GL11.glPushMatrix();

			GL11.glTranslatef((float)x, (float)y, (float)z);

			IIcon icon = fluid.getFluid().getIcon();
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			
			int color = fluid.getFluid().getColor();
			GL11.glColor4f(((color >>> 16) & 0xFF)/255f, ((color >>> 8) & 0xFF)/255f, ((color& 0xFF)/255f),1f);
			
			Block block = tile.getBlockType();
			Tessellator tess = Tessellator.instance;

			float amt = fluid.amount / (float)fluidTile.getTankInfo(ForgeDirection.UNKNOWN)[0].capacity;
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LIGHTING);
			
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			tess.startDrawingQuads();
			RenderHelper.renderCubeWithUV(tess, block.getBlockBoundsMinX() + 0.01, block.getBlockBoundsMinY() + 0.01, block.getBlockBoundsMinZ() + 0.01, block.getBlockBoundsMaxX() - 0.01, block.getBlockBoundsMaxY()*amt - 0.01, block.getBlockBoundsMaxZ() - 0.01, icon.getMinU(), icon.getMaxU(), icon.getMinV(), icon.getMaxV());
			tess.draw();

			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
			GL11.glColor3f(1f, 1f, 1f);
		}
	}*/
}


