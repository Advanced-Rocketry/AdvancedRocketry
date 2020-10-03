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

	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/rollingmachine.png");
	ResourceLocation coilSide = new ResourceLocation("libvulpes","textures/blocks/coilside.png");
	static int i = MaterialRegistry.getMaterialFromName("Copper").getColor();

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
		matrix.translate(-.5f, -1f, -0.5f);

		IVertexBuilder coilSolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(coilSide));
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		
		IVertexBuilder entityNoTex;

		//GL11.glColor3f(((i >>> 16) & 0xFF)/255f, ((i >>> 8) & 0xFF)/255f, (i & 0xFF)/255f);
		model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, coilSolidBuilder, "Coil");
		
		ItemStack outputStack;
		if(tile.isRunning()) {
			float progress = tile.getProgress(0)/(float)tile.getTotalProgress(0);

			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Hull");

			matrix.push();
			matrix.translate(2.12f, 1.0f, 2.56f);
			matrix.rotate(new Quaternion(-progress*720,0,0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller1");
			matrix.pop();

			matrix.push();
			matrix.translate(2.12f, 0.375f,2.18f);
			matrix.rotate(new Quaternion(-progress*720,0,0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller2");
			matrix.pop();

			matrix.push();
			matrix.translate(2.12f, 0.375f, 2.93f);
			matrix.rotate(new Quaternion(-progress*720,0,0, true));
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller2");
			matrix.pop();


			int color;
			if(tile.getOutputs() != null && (outputStack = tile.getOutputs().get(0)) != null)
				color = MaterialRegistry.getColorFromItemMaterial(outputStack);
			else
				color = 0;

			float r = (0xff & (color >> 16))/256f;
			float g = (0xff & (color >> 8))/256f;
			float b = ((color & 0xff)/256f);
			float a = 1f;
			
			entityNoTex = buffer.getBuffer(RenderHelper.getTranslucentNoTexEntityModelRenderType());
			
			//Render the ingot
			if(progress < 0.6f) {
				matrix.push();
				RenderSystem.disableTexture();
				matrix.translate(2.125f, 0.875f, 1.3125f + progress*2f);
				model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityNoTex,r,g,b,a, "Ingot");	
				RenderSystem.enableTexture();
				matrix.pop();
			}
			//Render the plate
			if(progress > 0.5f) {

				matrix.push();
				RenderSystem.disableTexture();
				matrix.translate(2.125f, 0.875f, 1.7125f + progress*2f);
				model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entityNoTex,r,g,b,a, "Plate");	
				RenderSystem.enableTexture();
				matrix.pop();
			}
			GL11.glColor3f(1f,1f,1f);
		}
		else {
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Hull");

			matrix.push();
			matrix.translate(2.12f, 1.0f, 2.56f);
			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller1");
			matrix.pop();

			matrix.push();
			matrix.translate(2.12f, 0.375f,2.18f);
			//model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller2");
			matrix.pop();

			matrix.push();

			matrix.translate(2.12f, 0.375f, 2.93f);
			matrix.rotate(new Quaternion(15f, 0,0,true));

			model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, entitySolidBuilder, "Roller2");
			matrix.pop();

		}
		matrix.pop();
	}
}
