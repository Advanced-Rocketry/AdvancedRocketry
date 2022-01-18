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
import zmaster587.advancedRocketry.tile.multiblock.TileAreaGravityController;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;


public class RenderAreaGravityController extends TileEntityRenderer<TileAreaGravityController> {
	
	WavefrontObject model;
	ResourceLocation texture =  new ResourceLocation("advancedrocketry:textures/models/areagravitycontroller.png");
	
	public RenderAreaGravityController(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/areagravitycontroller.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(TileAreaGravityController tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		if (tile.getWorld() != null) {
			combinedLightIn = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos().add(0, 1, 0));
		} else {
			combinedLightIn = 15728880;
		}
		
		matrix.push();
		

		//Initial setup

		matrix.translate(0.5f, -0.5f, 0.5f);
		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		IVertexBuilder entityTransBuilder;
		
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Hull");
		int maxSize = 5;
		
		//Render blur
		/*GL11.glDisable(GL11.GL_TEXTURE_2D);
		GlStateManager.color4f(0f, 1f, 1f, Math.max(((float)tile.getGravityMultiplier() - 0.1f)*0.2f,0f));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0f);*/
		
		
		// END render blur
		matrix.push();
		entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		matrix.rotate(new Quaternion(0, (float) tile.getArmRotation(), 0, true));
		for(int i = 0; i < maxSize; i++) {
			matrix.rotate(new Quaternion(0, (float) 360/maxSize, 0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Arm");
		}
		matrix.pop();
		
		matrix.push();
		matrix.scale(1.11f, 1f, 1.11f);
		entityTransBuilder = buffer.getBuffer(RenderHelper.getLightningTranslucencyNoTexEntityModelRenderType());
		for(int i = 0; i < 4; i++) {
			matrix.scale(.93f, 1f, .93f);
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransBuilder, 0f, 1f, 1f, Math.max(((float)tile.getGravityMultiplier() - 0.1f)*0.2f,0f), "Blur");
		}
		matrix.pop();
		matrix.pop();
	}
}
