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
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class RendererLathe extends TileEntityRenderer<TileLathe> {
	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/lathe.png");

	public RendererLathe(TileEntityRendererDispatcher tile) {
		super(tile);
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/lathe.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void render(TileLathe tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn){

		if(!tile.canRender())
			return;

		matrix.push();

		//Rotate and move the model into position
		matrix.translate(.5f, 0, 0.5f);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.rotate(new Quaternion(0, (front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, true));
		matrix.translate(-.5f, -1f, -2.5f);
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(texture));

		ItemStack outputStack;
		if(tile.isRunning()) {

			float progress = tile.getProgress(0)/(float)tile.getTotalProgress(0);

			model.tessellatePart(entitySolidBuilder, "body");

			matrix.push();

			if(progress < 0.95f)
				matrix.translate(0f, 0f, progress/.95f);
			else
				matrix.translate(0f, 0f, (1 - progress)/.05f);

			model.renderOnly(entitySolidBuilder, "Tray");
			matrix.pop();

			matrix.push();
			matrix.translate(.5f, 1.5625f, 0f);
			matrix.rotate(new Quaternion(0,0, progress*1500, true));
			model.renderOnly(entitySolidBuilder, "Cylinder");

			int color;
			//Check for rare bug when outputs is null, usually occurs if player opens machine within 1st tick
			if(tile.getOutputs() != null && (outputStack = tile.getOutputs().get(0)) != null)
				color = MaterialRegistry.getColorFromItemMaterial(outputStack);
			else
				color = 0;
			
			//GL11.glColor3d((0xff & color >> 16)/256f, (0xff & color >> 8)/256f , (color & 0xff)/256f);

			model.renderOnly(entitySolidBuilder, "rod");
			matrix.pop();
			
			//GL11.glColor4f(1f, 1f, 1f, 1f);
		}
		else {
			model.tessellatePart(entitySolidBuilder, "body");

			model.tessellatePart(entitySolidBuilder, "Tray");
		}
		matrix.pop();
	}
}
