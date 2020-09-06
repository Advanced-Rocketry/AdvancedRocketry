package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class RendererCrystallizer extends TileEntityRenderer {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/models/crystallizer.png");


	public RendererCrystallizer(TileEntityRendererDispatcher tile) {
		super(tile);

		try {
			model =  new WavefrontObject(new ResourceLocation("advancedrocketry","models/crystallizer.obj"));
		} catch (ModelFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void render(TileEntity tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		TileMultiblockMachine multiBlockTile = (TileMultiblockMachine)tile;

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		//Rotate and move the model into position
		matrix.translate(0.5f, 0, 0.5f);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		matrix.translate(-.5f, 0, -1.5f);
		IVertexBuilder entityTransparentBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));
		
		if(multiBlockTile.isRunning()) {

			float progress = multiBlockTile.getProgress(0)/(float)multiBlockTile.getTotalProgress(0);
			
			model.tessellatePart(entityTransparentBuilder, "Hull");

			List<ItemStack> outputList = multiBlockTile.getOutputs();
			/*if(outputList != null && !outputList.isEmpty()) {
				ItemStack stack = outputList.get(0);
				ItemEntity entity = new ItemEntity(tile.getWorld(), 0,0,0);

				entity.setItem(stack);
				entity.hoverStart = 0;

				int rotation = (int)(tile.getWorld().getGameTime() % 360);
				matrix.push();
				matrix.translate(0, 1, 0);

				matrix.push();
				matrix.translate(1, 0.2, 0.7);
				matrix.rotate(new Quaternion(0,rotation,0, true));
				matrix.scale(progress, progress, progress);
				zmaster587.libVulpes.render.RenderHelper.renderItem(multiBlockTile, stack, Minecraft.getInstance().getRenderManager());
				matrix.pop();

				matrix.push();
				matrix.translate(1, 0.2, 1.5);
				GL11.glRotatef(rotation, 0, 1, 0);
				GL11.glScalef(progress, progress, progress);
				zmaster587.libVulpes.render.RenderHelper.renderItem(multiBlockTile, stack, Minecraft.getInstance().getRenderItem());
				matrix.pop();

				matrix.push();
				matrix.translate(1, 0.2, 2.3);
				GL11.glRotatef(rotation, 0, 1, 0);
				GL11.glScalef(progress, progress, progress);
				zmaster587.libVulpes.render.RenderHelper.renderItem(multiBlockTile, stack, Minecraft.getInstance().getRenderItem());
				matrix.pop();

				matrix.pop();



				matrix.push();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );

				int color = Minecraft.getInstance().getItemColors().getColorFromItemstack(stack, 0);

				float divisor = 1/255f;

				GL11.glColor4f((color & 0xFF)*divisor*.5f, ((color & 0xFF00) >>> 8)*divisor*.5f,  ((color & 0xFF0000) >>> 16)*divisor*.5f, 0xE4*divisor);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				matrix.translate(0, 1.1f, 0);

				//Fill before emptying
				if(progress < 0.05)
					GL11.glScaled(1, 20*progress, 1);
				else
					GL11.glScaled(1, (1.1-(progress*1.111)), 1);

				matrix.translate(0, -1.1f, 0);
				model.renderPart("Liquid");
			}*/
			
			matrix.pop();

		}
		else {
			model.tessellatePart(entityTransparentBuilder, "Hull");
		}
		matrix.pop();
	}
}
