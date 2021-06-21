package zmaster587.advancedRocketry.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import zmaster587.advancedRocketry.tile.TileRocketAssemblingMachine;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererRocketAssemblingMachine extends TileEntityRenderer<TileRocketAssemblingMachine> {

	
	public RendererRocketAssemblingMachine(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	private ResourceLocation grid = new ResourceLocation("advancedrocketry:textures/models/grid.png");
	private ResourceLocation girder = new ResourceLocation("advancedrocketry:textures/models/girder.png");
	private ResourceLocation round_h = new ResourceLocation("advancedrocketry:textures/models/round_h.png");
	
	
	@Override
	public void render(TileRocketAssemblingMachine tile, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		AxisAlignedBB bb;
		
		//If the rocketbuilder is scanning and a valid bounding box for the rocket exists
		if(tile.isScanning() && (bb = tile.getBBCache()) != null) {

			double xOffset = bb.minX - tile.getPos().getX();
			double yOffset = bb.maxY - tile.getPos().getY() + 1;
			double zOffset = bb.minZ - tile.getPos().getZ();

			//Get size of the BB
			double xSize = bb.maxX - bb.minX+1;
			double zSize = bb.maxZ - bb.minZ+1;
			
			double yLocation = -(bb.maxY - bb.minY + 1.12)*tile.getNormallizedProgress();
			
			double xMin = xOffset;
			double yMin = yOffset + yLocation;
			double zMin = zOffset;
			double xMax = xOffset + 0.5;
			double yMax = yOffset + yLocation+ 0.25;
			double zMax = zOffset + zSize;
<<<<<<< HEAD
			float uMin = 0;
			float vMin = 0;
			float uMax = 1;
			float vMax = 1;
=======
			double uMin = 0;
			double vMin = 0;
			double uMax = 1;
			double vMax;
>>>>>>> origin/feature/nuclearthermalrockets

			matrix.push();
			
			
<<<<<<< HEAD
			//Draw Supports
			IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidTexturedManualRenderType(girder));
			
			float size = 0.25f;
			
			vMax = (float) (yMin/size);
=======
			//Draw scanning grid
			GlStateManager.disableLighting();
			GlStateManager.disableFog();
			GlStateManager.enableBlend();
			if(renderTile.isBuilding())
				GlStateManager.color(1, 0.5f, 0.5f, .05f);
			else
				GlStateManager.color(0.5f, 1, 0.5f, .05f);
			GlStateManager.disableDepth();
			GlStateManager.alphaFunc(GL11.GL_GEQUAL, 0.01f);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			bindTexture(grid);

			float min = 0;
			float maxU = (float)(1*xSize);
			float maxV = (float)(1*zSize);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xf0, 0xf0);
			GlStateManager.enableDepth();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			for(int i = 0; i < 20; i++) {
				//BOTTOM
				double offset = i/80d;
				RenderHelper.renderBottomFaceWithUV(buffer, yOffset + yLocation+offset, xOffset, zOffset, xOffset + xSize, zOffset  + zSize, min, maxU, min, maxV);
				RenderHelper.renderTopFaceWithUV(buffer, yOffset + yLocation+offset, xOffset, zOffset, xOffset + xSize, zOffset  + zSize, min, maxU, min, maxV);
				
				//TOP
			}
			Tessellator.getInstance().draw();
			
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableFog();
>>>>>>> origin/feature/nuclearthermalrockets
			
			float r = 0.78f, g= 0.5f, b = 0.34f, a = 1f;
			
			RenderHelper.renderCubeWithUV(matrix, entitySolidBuilder, xOffset, 0d, zOffset, xOffset + size, yOffset + yLocation, zOffset + size, (float)uMin, (float)uMax, (float)0d, (float)vMax, r,g,b,a);
			RenderHelper.renderCubeWithUV(matrix, entitySolidBuilder, xOffset + xSize - size, 0d, zOffset, xOffset  + xSize , yOffset + yLocation, zOffset + size, uMin, uMax, vMin, vMax, r,g,b,a);
			RenderHelper.renderCubeWithUV(matrix, entitySolidBuilder, xOffset + xSize - size, 0d, zOffset + zSize - size, xOffset  + xSize, yOffset + yLocation, zOffset + zSize, uMin, uMax, vMin, vMax, r,g,b,a);
			RenderHelper.renderCubeWithUV(matrix, entitySolidBuilder, xOffset, 0d, zOffset + zSize  - size, xOffset + size, yOffset + yLocation, zOffset + zSize, uMin, uMax, vMin, vMax, r,g,b,a);

			
			IVertexBuilder round = buffer.getBuffer(RenderHelper.getSolidTexturedManualRenderType(round_h));
			uMax = 1f;
			vMax = 1f;
			//Draw "beam emitters"
			//West block
			
			RenderHelper.renderBottomFaceWithUV(matrix, round, yMin, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderWestFaceWithUV(matrix, round, xMin, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderSouthFaceWithUV(matrix, round, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderNorthFaceWithUV(matrix, round, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderTopFaceWithUV(matrix, round, yMax, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			
			//Set ignore light then draw the glowy bits
			IVertexBuilder beam = buffer.getBuffer(RenderHelper.getTranslucentManualRenderType());
			if(tile.isBuilding())
			{
				r = 1;
				g = 0.333f;
				b = 0.333f;
				a = 1f;
			}
			else
			{
				r = 0.333f;
				g = 1f;
				b = 0.333f;
				a = 1f;
			}
			
			RenderHelper.renderEastFace(matrix, beam, xMax, yMin, zMin, yMax, zMax,r,g,b,a);
			
			//Change mins/maxes then render east block
			xMin = xOffset + xSize - 0.5;
			xMax = xOffset + xSize;
			
			RenderHelper.renderWestFace(matrix, beam, xMin, yMin, zMin, yMax, zMax,r,g,b,a);
			
			round = buffer.getBuffer(RenderHelper.getSolidTexturedManualRenderType(round_h));
			RenderHelper.renderBottomFaceWithUV(matrix, round, yMin, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderEastFaceWithUV(matrix, round, xMax, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderSouthFaceWithUV(matrix, round, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderNorthFaceWithUV(matrix, round, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderTopFaceWithUV(matrix, round, yMax, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			
<<<<<<< HEAD
			//Draw scanning grid
			if(tile.isBuilding())
			{
				r = 1;
				g = 0.5f;
				b = 0.5f;
				a = 0.5f;
			}
			else
			{
				r = 0.5f;
				g = 1f;
				b = 0.5f;
				a = 0.5f;
			}
			float min = 0;
			float maxU = (float)(1*xSize);
			float maxV = (float)(1*zSize);
			IVertexBuilder gridBuilder = buffer.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(grid));
			for(int i = 0; i < 20; i++) {
				//BOTTOM
				double offset = i/80d;
				RenderHelper.renderBottomFaceWithUV(matrix, gridBuilder, yOffset + yLocation+offset, xOffset, zOffset, xOffset + xSize, zOffset  + zSize, min, maxU, min, maxV,r,g,b,a);
				
				// Spooky magic alpha
				RenderHelper.renderTopFaceWithUV(matrix, gridBuilder, yOffset + yLocation+offset, xOffset, zOffset, xOffset + xSize, zOffset  + zSize, min, maxU, min, maxV, r,g,b,a*0.1f);
				
				//TOP
			}
			
			matrix.pop();
=======
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderBottomFaceWithUV(buffer, yMin, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderEastFaceWithUV(buffer, xMax, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderSouthFaceWithUV(buffer, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderNorthFaceWithUV(buffer, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax);
			RenderHelper.renderTopFaceWithUV(buffer, yMax, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax);
			Tessellator.getInstance().draw();

            //Draw Supports
			GlStateManager.color(0.78f, 0.5f, 0.34f, 1f);
			bindTexture(girder);
			GlStateManager.enableDepth();
			GlStateManager.disableBlend();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			float size = 0.25f;

			vMax = yMin/size;

			RenderHelper.renderCubeWithUV(buffer, xOffset, 0d, zOffset, xOffset + size, yOffset + yLocation, zOffset + size, uMin, uMax, 0d, vMax);
			RenderHelper.renderCubeWithUV(buffer, xOffset + xSize - size, 0d, zOffset, xOffset  + xSize , yOffset + yLocation, zOffset + size, uMin, uMax, vMin, vMax);
			RenderHelper.renderCubeWithUV(buffer, xOffset + xSize - size, 0d, zOffset + zSize - size, xOffset  + xSize, yOffset + yLocation, zOffset + zSize, uMin, uMax, vMin, vMax);
			RenderHelper.renderCubeWithUV(buffer, xOffset, 0d, zOffset + zSize  - size, xOffset + size, yOffset + yLocation, zOffset + zSize, uMin, uMax, vMin, vMax);
			Tessellator.getInstance().draw();



			GlStateManager.alphaFunc(GL11.GL_GEQUAL, 0.1f);
			GlStateManager.enableDepth();
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GL11.glPopMatrix();
>>>>>>> origin/feature/nuclearthermalrockets
		}
	}

}
