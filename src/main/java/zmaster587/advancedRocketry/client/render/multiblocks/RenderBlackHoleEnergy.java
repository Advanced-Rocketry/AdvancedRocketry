package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileBlackHoleGenerator;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerProducer;

public class RenderBlackHoleEnergy extends TileEntitySpecialRenderer {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/black_hole_generator.jpg");

	public RenderBlackHoleEnergy(){
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/black_hole_generator.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileEntity tile, double x, double y, double z,
			float partialTicks, int destroyStage, float a) {
		TileBlackHoleGenerator multiBlockTile = (TileBlackHoleGenerator)tile;

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		//Initial setup

		//Rotate and move the model into position
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		matrix.translate(x + .5, y + .5, z + .5);

		GL11.glRotatef((front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f, 0, 1, 0);
		
		bindTexture(texture);
		
		model.renderAll();
		
		if(multiBlockTile.isProducingPower())
		{
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.color4f(1f, 1f, 0.5f, 0.5f);
			BufferBuilder buffer = Tessellator.getInstance().getBuffer();
			
			matrix.push();
			GL11.glTranslatef(0, (float)Math.sin(System.currentTimeMillis() / 128.0)*.3f, 0);
			
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
			RenderHelper.renderCube(buffer, -0.45, 0.95, 0.55, 0.45, 1.05, 1.45);
			Tessellator.getInstance().draw();
			
			matrix.pop();
			
			matrix.push();
			GL11.glTranslatef(0, -(float)Math.sin(System.currentTimeMillis() / 128.0)*.3f, 0);
			
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
			RenderHelper.renderCube(buffer, -0.45, 0.95, 0.55, 0.45, 1.05, 1.45);
			Tessellator.getInstance().draw();
			matrix.pop();
			
			GlStateManager.disableBlend();
			GlStateManager.enableTexture();
			GlStateManager.enableLighting();
			GlStateManager.resetColor();
		}
		
		
		matrix.pop();
	}

}
