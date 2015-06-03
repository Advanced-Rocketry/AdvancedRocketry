package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.render.RenderHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

public class RendererBlockRocketBuilder extends TileEntitySpecialRenderer {

	
	private ResourceLocation grid = new ResourceLocation("advancedrocketry:textures/models/grid.png");
	private ResourceLocation girder = new ResourceLocation("advancedrocketry:textures/models/girder.png");
	private ResourceLocation round_h = new ResourceLocation("advancedrocketry:textures/models/round_h.png");
	
	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {



		TileRocketBuilder renderTile = (TileRocketBuilder)tile;
		AxisAlignedBB bb;
		
		//If the rocketbuilder is scanning and a valid bounding box for the rocket exists
		if(renderTile.isScanning() && (bb = renderTile.getBBCache()) != null) {

			double xOffset = bb.minX - tile.xCoord;
			double yOffset = bb.maxY - tile.yCoord;
			double zOffset = bb.minZ - tile.zCoord;

			//Get size of the BB
			double xSize = bb.maxX - bb.minX+1;
			double zSize = bb.maxZ - bb.minZ+1;
			
			double yLocation = -(bb.maxY - bb.minY + 1.12)*renderTile.getNormilizedScanTime();
			Tessellator tess = Tessellator.instance;
			
			
			double xMin = xOffset;
			double yMin = yOffset + yLocation;
			double zMin = zOffset;
			double xMax = xOffset + 0.5;
			double yMax = yOffset + yLocation+ 0.25;
			double zMax = zOffset + zSize;
			double uMin = 0;
			double vMin = 0;
			double uMax = 1;
			double vMax = 1;

			GL11.glPushMatrix();
			GL11.glTranslated(x,y,z);
			
			//Initial setup
			int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
			int brightX = bright % 65536;
			int brightY = bright / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
			
			//Draw Supports
			GL11.glColor4f(0.78f, 0.5f, 0.34f, 1f);
			bindTexture(girder);
			GL11.glDepthMask(true);
			tess.startDrawingQuads();
			
			float size = 0.25f;
			
			vMax = yMin/size;
			
			RenderHelper.renderCubeWithUV(tess, xOffset, 0d, zOffset, xOffset + size, yOffset + yLocation, zOffset + size, uMin, uMax, 0d, vMax);
			RenderHelper.renderCubeWithUV(tess, xOffset + xSize - size, 0d, zOffset, xOffset  + xSize , yOffset + yLocation, zOffset + size, uMin, uMax, vMin, vMax);
			RenderHelper.renderCubeWithUV(tess, xOffset + xSize - size, 0d, zOffset + zSize - size, xOffset  + xSize, yOffset + yLocation, zOffset + zSize, uMin, uMax, vMin, vMax);
			RenderHelper.renderCubeWithUV(tess, xOffset, 0d, zOffset + zSize  - size, xOffset + size, yOffset + yLocation, zOffset + zSize, uMin, uMax, vMin, vMax);
			tess.draw();
			
			
			//Draw scanning grid
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_FOG);
			GL11.glEnable(GL11.GL_BLEND);
			if(renderTile.isBuilding())
				GL11.glColor4f(1, 0.5f, 0.5f, .05f);
			else
				GL11.glColor4f(0.5f, 1, 0.5f, .05f);
			GL11.glDepthMask(false);
			GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.0f);
			GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
			
			bindTexture(grid);

			float min = 0;
			float maxU = (float)(1*xSize);
			float maxV = (float)(1*zSize);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xf0, 0xf0);
			
			tess.startDrawing(7);
			for(int i = 0; i < 20; i++) {
				
				//BOTTOM
				double offset = i/80d;
				tess.addVertexWithUV(xOffset, yOffset + yLocation+offset, zOffset, min ,min);
				tess.addVertexWithUV(xOffset + xSize, yOffset + yLocation+offset, zOffset,  maxU, min);
				tess.addVertexWithUV(xOffset + xSize, yOffset + yLocation+offset, zOffset  + zSize,maxU , maxV);
				tess.addVertexWithUV(xOffset, yOffset + yLocation+offset, zOffset  + zSize,min,  maxV);

				//TOP
				
				tess.addVertexWithUV(xOffset + xSize, yOffset + yLocation+offset, zOffset,maxU, min);
				tess.addVertexWithUV(xOffset, yOffset + yLocation+offset, zOffset, min ,min);
				tess.addVertexWithUV(xOffset, yOffset + yLocation+offset, zOffset  + zSize,min,  maxV);
				tess.addVertexWithUV(xOffset + xSize, yOffset + yLocation+offset, zOffset  + zSize,maxU , maxV);
			}
			tess.draw();
			
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glDepthMask(true);
			

			
			
			GL11.glColor4f(1f, 1f, 1f, 1f);
			bindTexture(round_h);
			uMax = 1f;
			vMax = 1f;
			//Draw "beam emitters"
			//West block
			
			
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
			tess.startDrawingQuads();
			
			RenderHelper.renderBottomFaceWithUV(tess, yMin, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderWestFaceWithUV(tess, xMin, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderSouthFaceWithUV(tess, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderNorthFaceWithUV(tess, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderTopFaceWithUV(tess, yMax, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax);
			tess.draw();
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			//Set ignore light then draw the glowy bits
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xf0, 0xf0);
			tess.startDrawingQuads();
			if(renderTile.isBuilding())
				GL11.glColor4f(3f, 1f, 1f, 1f);
			else
				GL11.glColor4f(1f, 3f, 1f, 1f);
			
			RenderHelper.renderEastFace(tess, xMax, yMin, zMin, yMax, zMax);
			
			//Change mins/maxes then render east block
			xMin = xOffset + xSize - 0.5;
			xMax = xOffset + xSize;
			
			RenderHelper.renderWestFace(tess, xMin, yMin, zMin, yMax, zMax);
			tess.draw();
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
			GL11.glColor4f(1f, 1f, 1f, 1f);
			tess.startDrawingQuads();
			RenderHelper.renderBottomFaceWithUV(tess, yMin, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderEastFaceWithUV(tess, xMax, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderSouthFaceWithUV(tess, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderNorthFaceWithUV(tess, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderTopFaceWithUV(tess, yMax, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax);
			tess.draw();
			
			GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.1f);
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
	}

}
