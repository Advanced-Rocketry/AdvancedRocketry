package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.TileObservatory;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;

public class RendererObservatory  extends TileEntityRenderer<TileObservatory> {

	WavefrontObject model;
	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/observatory.png");

	public RendererObservatory(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/observatory.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(TileObservatory tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn)  {

		if(!tile.canRender())
			return;


		if (tile.getWorld() != null) {
			combinedLightIn = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos().add(0, 1, 0));
		} else {
			combinedLightIn = 15728880;
		}
		
		//Initial setup
		
		matrix.push();

		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));//tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.translate(.5, 0, .5);
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));

		matrix.translate(2, -1, 0);
		
		IVertexBuilder entityTransparentBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));

		float offset = tile.getOpenProgress();

		if(offset != 0f) {
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransparentBuilder, "Base");

			model.tessellatePart(matrix, combinedLightIn, combinedOverlayIn,  entityTransparentBuilder, "Scope");
			model.tessellatePart(matrix, combinedLightIn, combinedOverlayIn,  entityTransparentBuilder, "Axis");

			matrix.push();
			matrix.translate(0, 0, -offset* 1.125f);
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransparentBuilder, "CasingXMinus");
			matrix.pop();

			matrix.push();
			matrix.translate(0,0,offset * 1.125f);
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransparentBuilder, "CasingXPlus");
			matrix.pop();

		}
		else {
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransparentBuilder, "Base");
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransparentBuilder, "CasingXMinus");
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransparentBuilder, "CasingXPlus");
		}
		matrix.pop();
	}
}
