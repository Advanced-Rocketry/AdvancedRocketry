package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Optional;

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

		float r = 1, g = 1, b = 1, a = 1;
		if(fluid != null && fluid.getFluid() != null)
		{
			matrix.push();

			float minU = 0, maxU = 1, minV = 0, maxV = 1;
			Optional<RenderMaterial> mat = ForgeHooksClient.getFluidMaterials(fluid.getFluid()).findFirst();

			if(mat.isPresent()) {
				TextureAtlasSprite sprite = mat.get().getSprite();
				fluidIcon = mat.get().getAtlasLocation();
				minU = sprite.getMinU();
				maxU = sprite.getMaxU();
				minV = sprite.getMinV();
				maxV = sprite.getMaxV();
			}
			int color = fluid.getFluid().getAttributes().getColor();
			r = ((color >> 16) & 0xff)/255f;
			g = ((color >> 8) & 0xff)/255f;
			b = (color & 0xff)/255f;
			
			
			

			
			BlockState block = tile.getBlockState();
			Tessellator tess = Tessellator.getInstance();

			float amt = fluid.getAmount() / (float)fluidTile.getTankCapacity(0);
			
			AxisAlignedBB bb = block.getShape(tile.getWorld(), tile.getPos()).getBoundingBox();
			
			IVertexBuilder tileBuffer = buffer.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(fluidIcon));
			RenderHelper.renderCubeWithUV(matrix, tileBuffer, bb.minX + 0.01, bb.minY + 0.01, bb.minZ + 0.01, bb.maxX - 0.01, bb.maxY*amt - 0.01, bb.maxZ - 0.01, minU, maxU, minV, maxV, r,g,b,a);
			
			matrix.pop();
		}
	}
}


