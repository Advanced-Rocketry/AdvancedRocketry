package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;

public class RendererLathe extends TileEntityRenderer<TileLathe> {
	WavefrontObject model;
	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/lathe.png");

	public RendererLathe(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/lathe.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(TileLathe tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn){

		if(!tile.canRender())
			return;

		matrix.push();
		//Rotate and move the model into position
		matrix.translate(.5f, 0, 0.5f);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.rotate(new Quaternion(0, ((front.getXOffset() == 1 ? 0 : 180) + front.getZOffset()*90f), 0, true));
		matrix.translate(-.5f, -1f, -2.5f);
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getTranslucentEntityModelRenderType(texture));


		ItemStack outputStack;
		if(tile.isRunning()) {

			float progress = tile.getProgress(0)/(float)tile.getTotalProgress(0);
			model.tessellatePart(matrix, combinedLightIn, combinedOverlayIn,  entitySolidBuilder, "Hull");

			matrix.push();

			if(progress < 0.95f)
				matrix.translate(0f, 0f, -progress/.85f);
			else
				matrix.translate(0f, 0f, -(1 - progress)/.05f);

			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Tool");
			matrix.pop();

			matrix.push();
			matrix.translate(0.375f, 0.9375f, 0f);
			matrix.rotate(new Quaternion(0,0, progress*1500, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Shaft");
			matrix.pop();

			int color;
			//Check for rare bug when outputs is null, usually occurs if player opens machine within 1st tick
			if(tile.getOutputs() != null && !(outputStack = tile.getOutputs().get(0)).isEmpty())
				color = MaterialRegistry.getColorFromItemMaterial(outputStack);
			else
				color = 0;
			
			float r = (0xff & color >> 16)/255f, g =  (0xff & color >> 8)/255f, b = (color & 0xff)/255f;
			matrix.push();
			matrix.translate(0.375f, 1.1875f, 0f);
			matrix.rotate(new Quaternion(0,0, progress*1500, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder,r,g,b,1f, "Rod");
			matrix.pop();
			
			//GL11.glColor4f(1f, 1f, 1f, 1f);
		}
		else {
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn,  entitySolidBuilder, "Hull");

			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn,  entitySolidBuilder, "Tool");
			
			matrix.push();
			matrix.translate(0.375f, 0.9375f, 0f);
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn,  entitySolidBuilder, "Sool");
			matrix.pop();
		}
		matrix.pop();
	}
}
