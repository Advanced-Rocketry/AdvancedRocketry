package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderCentrifuge extends TileEntityRenderer<TileCentrifuge> {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/centrifuge.png");

	public RenderCentrifuge(TileEntityRendererDispatcher tile){
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/centrifuge.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void render(TileCentrifuge tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		matrix.push();

		//Initial setup

		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		matrix.translate( 0.5, 0, 0.5);
		matrix.rotate(new Quaternion(0, (front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f, 0 ,true));
		matrix.translate(0, 0, 1);
		
		IVertexBuilder builder = buffer.getBuffer(RenderHelper.getTranslucentEntityModelRenderType(texture)); 

		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, builder, "Frame");


		if(tile.isRunning())
		{
			float lavaheight = tile.getNormallizedProgress(0);
			matrix.rotate(new Quaternion(0, tile.getWorld().getGameTime() * -10f, 0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, builder, "Spinning");


			ResourceLocation fluidIcon = new ResourceLocation("advancedrocketry:textures/blocks/fluid/oxygen_flow.png");
			
			Fluid fluid = AdvancedRocketryFluids.enrichedLavaStill.get();
			if(fluid != null)
			{
				matrix.push();

				if(fluid.getAttributes().getStillTexture() == null)
					return;

				
				float amt = 1.0f;
				
				IVertexBuilder lavaRender;

				AxisAlignedBB bb = new AxisAlignedBB(-1.2, -0.5, -0.5, 1.2, 0.3 - 0.6*lavaheight, 0.5);

				double minU = 0, maxU = 1, minV = 0, maxV = 1;
				float r = 1f, g = 1f, b = 1f, a = 1f;
				TextureAtlasSprite sprite  = null;
				
				if(fluid.getAttributes().getStillTexture() != null)
					sprite = Minecraft.getInstance().getAtlasSpriteGetter(fluid.getAttributes().getStillTexture()).apply(fluid.getAttributes().getStillTexture());
				if(sprite != null) {
					minU = sprite.getMinU();
					maxU = sprite.getMaxU();
					minV = sprite.getMinV();
					maxV = sprite.getMaxV();
					lavaRender = buffer.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(fluid.getAttributes().getStillTexture())); 
				}
				else {
					int color = fluid.getAttributes().getColor();
					r = ((color >>> 16) & 0xFF)/255f;
					g = ((color >>> 8) & 0xFF)/255f;
					b = ((color & 0xFF)/255f);

					Minecraft.getInstance().textureManager.bindTexture(fluidIcon);
					lavaRender = buffer.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(fluidIcon));
				}
				
				for(int i = 0; i < 4; i++)
				{
					RenderHelper.renderCubeWithUV(matrix, lavaRender, bb.minX + 0.01, bb.minY + 0.01, bb.minZ + 0.01, bb.maxX - 0.01, bb.maxY*amt - 0.01, bb.maxZ - 0.01, (float)minU, (float)maxU,(float) minV, (float)maxV, r,g,b,a);
					matrix.rotate(new Quaternion(0, 45f, 0, true));
				}
				matrix.pop();
			}
		}
		else
		{
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, builder, "Spinning");
		}
		matrix.pop();
	}
}
