package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
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
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class RendererCuttingMachine extends TileEntityRenderer<TileCuttingMachine> {

	private WavefrontObject model;
	public final static ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/cuttingmachine.png");

	public RendererCuttingMachine(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry","models/cuttingmachine.obj"));
		} catch (ModelFormatException e) {

			e.printStackTrace();
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public void render(TileCuttingMachine tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		if (tile.getWorld() != null) {
			combinedLightIn = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos().add(0, 1, 0));
		} else {
			combinedLightIn = 15728880;
		}

		matrix.push();

		//Initial setup

		//Rotate and move the model into position
		matrix.translate(.5f, 0,  0.5f);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		matrix.translate(-.5f, 0, -1.5f);

		IVertexBuilder entityTransparentBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));

		if(tile.isRunning()) {

			float progress = tile.getProgress(0)/(float)tile.getTotalProgress(0);
			float tray;
			tray = 2.2f*progress;

			List<ItemStack> inputList = tile.getInputs();
			if(inputList != null && !inputList.isEmpty() && progress < 0.65) {
				ItemStack inputStack = ItemStack.EMPTY;
				for (ItemStack stack: inputList) {
					if (!stack.isEmpty() && inputStack.isEmpty())
						inputStack = stack;
				}

				matrix.push();
				matrix.rotate(new Quaternion(90, 0, 0, true));
				matrix.translate(1f, tray + .45, -1.05);
				//RenderHelper.renderItem(tile, inputStack, Minecraft.getInstance().getItemRenderer());
				matrix.pop();
			}


			List<ItemStack> outputList = tile.getOutputs();
			if(outputList != null && !outputList.isEmpty() && progress >= 0.65) {
				ItemStack stack = outputList.get(0);

				matrix.push();
				matrix.rotate(new Quaternion(90, 0, 0, true));
				matrix.translate(1f, tray + .45, -1.05);
				//RenderHelper.renderItem(tile, stack, Minecraft.getInstance().getRenderItem());
				matrix.pop();
			}

			model.tessellatePart(matrix, combinedLightIn, combinedOverlayIn,  entityTransparentBuilder, "Hull");

			matrix.push();

			matrix.translate(1f, 1f, 1.5f);

			matrix.rotate(new Quaternion(-6*tile.getProgress(0) % 360, 0, 0, true));
			matrix.translate(-1f, -1f, -1.5f);
			model.tessellatePart(matrix, combinedLightIn, combinedOverlayIn,  entityTransparentBuilder, "Saw");
			matrix.pop();

		}
		else {
			model.tessellateAll(matrix, combinedLightIn, combinedOverlayIn, entityTransparentBuilder);
		}
		matrix.pop();
	}

}
