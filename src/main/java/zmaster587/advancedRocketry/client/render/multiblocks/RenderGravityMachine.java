package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.TileGravityController;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderGravityMachine extends TileEntityRenderer<TileGravityController> {
	
	WavefrontObject model;

	ResourceLocation texture =  new ResourceLocation("advancedrocketry","textures/models/gravitymachine.png");
	
	public RenderGravityMachine(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/gravitymachine.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileGravityController tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		matrix.push();

		//Initial setup

		matrix.translate(0.5f, 0.5f, 0.5f);
		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos()));
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		IVertexBuilder entityTransBuilder = buffer.getBuffer(RenderHelper.getTranslucentEntityModelRenderType(texture));
		
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Base");
		GL11.glDisable(GL11.GL_LIGHTING);
		int maxSize = 5;
		
		//Render blur
		/*GL11.glDisable(GL11.GL_TEXTURE_2D);
		GlStateManager.color4f(0f, 1f, 1f, Math.max(((float)tile.getGravityMultiplier() - 0.1f)*0.2f,0f));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0f);*/
		
		matrix.push();
		matrix.scale(1.1f, 1f, 1.1f);
		for(int i = 0; i < 4; i++) {
			matrix.scale(.93f, 1f, .93f);
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityTransBuilder, "Blur");
		}
		matrix.pop();
		// END render blur
		
		matrix.rotate(new Quaternion(0, (float) tile.getArmRotation(), 0, true));
		for(int i = 0; i < maxSize; i++) {
			matrix.rotate(new Quaternion(0, (float) 360/maxSize, 0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Arm");
		}
		matrix.pop();
	}
}
