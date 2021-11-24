package zmaster587.advancedRocketry.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Optional;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.tile.TilePressureTank;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderTank extends TileEntityRenderer<TilePressureTank> {

	public RenderTank(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(TilePressureTank tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

        FluidStack fluid = ((IFluidHandler) tile).getFluidInTank(0);
		ResourceLocation fluidIcon = new ResourceLocation("advancedrocketry:textures/blocks/fluid/oxygen_flow.png");

		if(!fluid.isEmpty()) {
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
			float r = ((color >> 16) & 0xff)/255f;
			float g = ((color >> 8) & 0xff)/255f;
			float b = (color & 0xff)/255f;

			BlockState block = tile.getBlockState();

			float amt = fluid.getAmount() / (float) ((IFluidHandler) tile).getTankCapacity(0);
			
			AxisAlignedBB bb = block.getShape(tile.getWorld(), tile.getPos()).getBoundingBox();
			
			IVertexBuilder tileBuffer = buffer.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(fluidIcon));
			RenderHelper.renderCubeWithUV(matrix, tileBuffer, bb.minX + 0.01, bb.minY + 0.01, bb.minZ + 0.01, bb.maxX - 0.01, bb.maxY*amt - 0.01, bb.maxZ - 0.01, minU, maxU, minV, maxV, r,g,b,1);
			
			matrix.pop();
		}
	}
}


