package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.tile.TileFluidTank;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderTank extends TileEntityRenderer<TileFluidTank> {

	public RenderTank(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(TileFluidTank tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		IFluidHandler fluidTile = (IFluidHandler)tile;
		FluidStack fluid = fluidTile.getFluidInTank(0);
		ResourceLocation fluidIcon = new ResourceLocation("advancedrocketry:textures/blocks/fluid/oxygen_flow.png");

		if(fluid != null && fluid.getFluid() != null)
		{
			matrix.push();

			float minU = 0, maxU = 1, minV = 0, maxV = 1;
			/*TextureMap map = Minecraft.getInstance().getTextureMapBlocks();
			TextureAtlasSprite sprite = map.getTextureExtry(fluid.getFluid().getStill().toString());
			if(sprite != null) {
				minU = sprite.getMinU();
				maxU = sprite.getMaxU();
				minV = sprite.getMinV();
				maxV = sprite.getMaxV();
				GlStateManager.bindTexture(map.getGlTextureId());
			}
			else {
				int color = fluid.getFluid().getColor();
				GL11.glColor4f(((color >>> 16) & 0xFF)/255f, ((color >>> 8) & 0xFF)/255f, ((color& 0xFF)/255f),1f);
				
				bindTexture(fluidIcon);
			}*/
			

			
			BlockState block = tile.getBlockState();
			Tessellator tess = Tessellator.getInstance();

			float amt = fluid.getAmount() / (float)fluidTile.getTankCapacity(0);
			
			AxisAlignedBB bb = block.getShape(tile.getWorld(), tile.getPos()).getBoundingBox();
			
			IVertexBuilder tileBuffer = buffer.getBuffer(RenderHelper.getTranslucentBlock());
			
			tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderCubeWithUV(matrix, tileBuffer, bb.minX + 0.01, bb.minY + 0.01, bb.minZ + 0.01, bb.maxX - 0.01, bb.maxY*amt - 0.01, bb.maxZ - 0.01, minU, maxU, minV, maxV,1f,1f,1f,1f);
			tess.draw();
			matrix.pop();
		}
	}
}


