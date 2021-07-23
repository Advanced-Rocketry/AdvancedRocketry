package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class RendererRollingMachine extends TileEntityRenderer<TileRollingMachine> {
	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/rollingmachine.png");
	private static int bodyList;

	public RendererRollingMachine(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/rollingmachine.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(TileRollingMachine tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		matrix.push();
		//Rotate and move the model into position
		matrix.translate(0.5f, 0, 0.5f);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));

		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		matrix.translate(-.5f, -1f, -1.5f);
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		
		IVertexBuilder entityNoTex;
		
		ItemStack outputStack;
		if(tile.isRunning()) {
			float progress = tile.getProgress(0)/(float)tile.getTotalProgress(0);

			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Hull");
			matrix.push();
			matrix.translate(1.375f, 0.6875f, 0);
			matrix.rotate(new Quaternion(-progress*720,0,0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller1");
			matrix.pop();

			matrix.push();
			matrix.translate(1.9375f, 0.6875f, 0f);
			matrix.rotate(new Quaternion(-progress*720,0,0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller2");
			matrix.pop();

			matrix.push();
			matrix.translate(1.625f + 0.03125f, 1.125f,0f);
			matrix.rotate(new Quaternion(-progress*720,0,0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller2");
			matrix.pop();
		}
		else {
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Hull");
			matrix.push();
			matrix.translate(1.375f, 0.6875f, 0);
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller1");
			matrix.pop();

			matrix.push();
			matrix.translate(1.9375f, 0.6875f,0f);
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller2");
			matrix.pop();

			matrix.push();

			matrix.translate(1.625f + 0.03125f, 1.125f,0f);
			matrix.rotate(new Quaternion(15f, 0,0,true));

			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller2");
			matrix.pop();

		}
		matrix.pop();
	}
}
