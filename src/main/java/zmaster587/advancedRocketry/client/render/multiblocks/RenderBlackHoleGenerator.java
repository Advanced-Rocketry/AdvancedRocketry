package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileBlackHoleGenerator;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;

public class RenderBlackHoleGenerator extends TileEntityRenderer<TileBlackHoleGenerator> {

	WavefrontObject model;
	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/blackholegenerator.jpg");

	public RenderBlackHoleGenerator(TileEntityRendererDispatcher tile){
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/blackholegenerator.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(TileBlackHoleGenerator tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		matrix.push();

		//Initial setup

		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.translate(0.5, 0.5,0.5);

		matrix.rotate(new Quaternion(0, (front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f, 0, true));
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		
		
		model.tessellateAll(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder);
		
		if(tile.isProducingPower()) {
			IVertexBuilder entityTransparentBuilder = buffer.getBuffer(RenderHelper.getTranslucentManualRenderType());
			
			matrix.push();
			matrix.translate(0, (float)Math.sin(System.currentTimeMillis() / 128.0)*.3f, 0);
			
			RenderHelper.renderCube(matrix, entityTransparentBuilder, -0.45, 0.95, 0.55, 0.45, 1.05, 1.45, 1f, 1f, 0.5f, 0.5f);
			
			matrix.pop();
			
			matrix.push();
			matrix.translate(0, -(float)Math.sin(System.currentTimeMillis() / 128.0)*.3f, 0);
			
			RenderHelper.renderCube(matrix, entityTransparentBuilder, -0.45, 0.95, 0.55, 0.45, 1.05, 1.45, 1f, 1f, 0.5f, 0.5f);
			matrix.pop();
			
		}
		
		
		matrix.pop();
	}

}
