package zmaster587.advancedRocketry.client.render.multiblocks;


import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;

public class RendererElectrolyser extends TileEntityRenderer<TileElectrolyser> {

	WavefrontObject model;
	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/electrolyser.png");

	public RendererElectrolyser(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new  WavefrontObject(new ResourceLocation("advancedrocketry","models/electrolyser.obj"));
		} catch (ModelFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(TileElectrolyser tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn){

		if(!tile.canRender())
			return;

		if (tile.getWorld() != null) {
			combinedLightIn = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos().add(0, 1, 0));
		} else {
			combinedLightIn = 15728880;
		}
		
		matrix.push();

		//Rotate and move the model into position
		matrix.translate(.5f, 0, 0.5f);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.rotate(new Quaternion(0,(front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f, 0, true ));
		matrix.translate(1.5f, 0f, -0.5f);

		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		model.tessellateAll(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder);

		//Lightning effect

		if(tile.isRunning()) {
			matrix.push();
			matrix.translate(-1.5, 0.05f, 0.5f);
			double width = 0.01;

			//Isn't precision fun?
			double ySkew = 0.1*MathHelper.sin((tile.getWorld().getGameTime() & 0xffff)*2f);
			double xSkew = 0.1*MathHelper.sin((200 + tile.getWorld().getGameTime() & 0xffff)*3f);
			double yPos = 1.4;
			
			IVertexBuilder entityTransparentBuilder = buffer.getBuffer(RenderHelper.getTranslucentManualRenderType());
			float r = .64f, g = 0.64f, b = 1f, a= 0.4f;
			
			double xMin = -0.3f;
			double xMax = -.15f;
			double zMin = 1f;
			double zMax = 1;
			RenderHelper.renderCrossXZ(matrix, entityTransparentBuilder, width, xMin, yPos, zMin, xMax, yPos + ySkew, zMax  + xSkew, r,g,b,a);

			//tess.addVertex(xMin, yMax, zMin);
			//tess.addVertex(xMax, yMax + ySkew, zMin);
			//tess.addVertex(xMax, yMin + ySkew, zMin);
			//tess.addVertex(xMin, yMin, zMin);

			xMax += 0.15;
			xMin += 0.15;

			RenderHelper.renderCrossXZ(matrix, entityTransparentBuilder, width, xMin, yPos + ySkew, zMin + xSkew, xMax, yPos - ySkew, zMax - xSkew, r,g,b,a);

			xMax += 0.15;
			xMin += 0.15;

			RenderHelper.renderCrossXZ(matrix, entityTransparentBuilder, width, xMin, yPos - ySkew, zMin - xSkew, xMax, yPos + ySkew, zMax + xSkew, r,g,b,a);

			xMax += 0.15;
			xMin += 0.15;

			RenderHelper.renderCrossXZ(matrix, entityTransparentBuilder, width, xMin, yPos + ySkew, zMin + xSkew, xMax, yPos, zMax, r,g,b,a);
			matrix.pop();
		}
		matrix.pop();
	}

}
