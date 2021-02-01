package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/blackholegenerator.png");

	public RenderBlackHoleEnergy(){
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/blackholegenerator.obj"));
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

		GL11.glPushMatrix();

		//Initial setup

		//Rotate and move the model into position
		EnumFacing front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glTranslated(x + .5, y + .5, z + .5);

		GL11.glRotatef((front.getFrontOffsetZ() == 1 ? 180 : 0) - front.getFrontOffsetX()*90f, 0, 1, 0);
		
		bindTexture(texture);
		
		model.renderAll();
		
		if(multiBlockTile.isProducingPower())
		{
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.color(1f, 1f, 0.5f, 0.5f);
			BufferBuilder buffer = Tessellator.getInstance().getBuffer();
			
			GL11.glPushMatrix();
			GL11.glTranslatef(0, (float)Math.sin(System.currentTimeMillis() / 128.0)*.3f, 0);
			
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
			RenderHelper.renderCube(buffer, -0.45, 0.95, 0.55, 0.45, 1.05, 1.45);
			Tessellator.getInstance().draw();
			
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			GL11.glTranslatef(0, -(float)Math.sin(System.currentTimeMillis() / 128.0)*.3f, 0);
			
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
			RenderHelper.renderCube(buffer, -0.45, 0.95, 0.55, 0.45, 1.05, 1.45);
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();
			
			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.resetColor();
		}
		
		
		GL11.glPopMatrix();
	}

}
