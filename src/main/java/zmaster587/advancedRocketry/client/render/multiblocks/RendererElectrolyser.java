package zmaster587.advancedRocketry.client.render.multiblocks;


import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class RendererElectrolyser extends TileEntitySpecialRenderer {

	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/electrolyser.png");

	public RendererElectrolyser() {
		try {
			model = new  WavefrontObject(new ResourceLocation("advancedrocketry:models/electrolyser.obj"));
		} catch (ModelFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int destroyState, float a) {
		TileMultiblockMachine multiBlockTile = (TileMultiblockMachine)tile;

		if(!multiBlockTile.canRender())
			return;

		matrix.push();

		//Rotate and move the model into position
		matrix.translate(x+.5f, y, z + 0.5f);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getZOffset() == 1 ? 180 : 0) - front.getXOffset()*90f, 0, 1, 0);

		bindTexture(texture);
		model.renderAll();

		//Lightning effect

		if(multiBlockTile.isRunning()) {
			BufferBuilder buffer = Tessellator.getInstance().getBuffer();

			double width = 0.01;

			//Isn't precision fun?
			double ySkew = 0.1*MathHelper.sin((tile.getWorld().getGameTime() & 0xffff)*2f);
			double xSkew = 0.1*MathHelper.sin((200 + tile.getWorld().getGameTime() & 0xffff)*3f);
			double yPos = 1.4;

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA);

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
			GlStateManager.color4f(.64f, 0.64f, 1f, 0.4f);
			double xMin = -0.3f;
			double xMax = -.15f;
			double zMin = 1f;
			double zMax = 1;
			RenderHelper.renderCrossXZ(buffer, width, xMin, yPos, zMin, xMax, yPos + ySkew, zMax  + xSkew);

			//tess.addVertex(xMin, yMax, zMin);
			//tess.addVertex(xMax, yMax + ySkew, zMin);
			//tess.addVertex(xMax, yMin + ySkew, zMin);
			//tess.addVertex(xMin, yMin, zMin);

			xMax += 0.15;
			xMin += 0.15;

			RenderHelper.renderCrossXZ(buffer, width, xMin, yPos + ySkew, zMin + xSkew, xMax, yPos - ySkew, zMax - xSkew);

			xMax += 0.15;
			xMin += 0.15;

			RenderHelper.renderCrossXZ(buffer, width, xMin, yPos - ySkew, zMin - xSkew, xMax, yPos + ySkew, zMax + xSkew);

			xMax += 0.15;
			xMin += 0.15;

			RenderHelper.renderCrossXZ(buffer, width, xMin, yPos + ySkew, zMin + xSkew, xMax, yPos, zMax);

			Tessellator.getInstance().draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
		}
		matrix.pop();
	}

}
