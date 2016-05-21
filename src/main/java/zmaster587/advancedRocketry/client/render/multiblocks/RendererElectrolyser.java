package zmaster587.advancedRocketry.client.render.multiblocks;


import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.multiblock.TileMultiblockMachine;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererElectrolyser extends TileEntitySpecialRenderer{

	IModelCustom model = AdvancedModelLoader.loadModel(new ResourceLocation("advancedrocketry:models/electrolyser.obj"));

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/electrolyser.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileMultiblockMachine multiBlockTile = (TileMultiblockMachine)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);

		//Rotate and move the model into position
		GL11.glTranslated(x+.5f, y, z + 0.5f);
		ForgeDirection front = RotatableBlock.getFront(tile.getBlockMetadata());
		GL11.glRotatef((front.offsetZ == 1 ? 180 : 0) + front.offsetX*90f, 0, 1, 0);

		bindTexture(texture);
		model.renderAll();

		//Lightning effect

		if(multiBlockTile.isRunning()) {
			Tessellator tess = Tessellator.instance;

			double width = 0.01;

			//Isn't precision fun?
			double ySkew = 0.1*MathHelper.sin((tile.getWorldObj().getWorldTime() & 0xffff)*2f);
			double xSkew = 0.1*MathHelper.sin((200 + tile.getWorldObj().getWorldTime() & 0xffff)*3f);
			double yPos = 1.4;

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA);

			tess.startDrawingQuads();
			tess.setColorRGBA_F(0.64f, 0.64f, 1f, 0.4f);

			double xMin = -0.3f;
			double xMax = -.15f;
			double zMin = 1f;
			double zMax = 1;
			RenderHelper.renderCrossXZ(tess, width, xMin, yPos, zMin, xMax, yPos + ySkew, zMax  + xSkew);

			//tess.addVertex(xMin, yMax, zMin);
			//tess.addVertex(xMax, yMax + ySkew, zMin);
			//tess.addVertex(xMax, yMin + ySkew, zMin);
			//tess.addVertex(xMin, yMin, zMin);

			xMax += 0.15;
			xMin += 0.15;

			RenderHelper.renderCrossXZ(tess, width, xMin, yPos + ySkew, zMin + xSkew, xMax, yPos - ySkew, zMax - xSkew);

			xMax += 0.15;
			xMin += 0.15;

			RenderHelper.renderCrossXZ(tess, width, xMin, yPos - ySkew, zMin - xSkew, xMax, yPos + ySkew, zMax + xSkew);

			xMax += 0.15;
			xMin += 0.15;

			RenderHelper.renderCrossXZ(tess, width, xMin, yPos + ySkew, zMin + xSkew, xMax, yPos, zMax);

			tess.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
			
		}
		GL11.glPopMatrix();
	}

}
