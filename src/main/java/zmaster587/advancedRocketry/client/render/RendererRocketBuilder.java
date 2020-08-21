package zmaster587.advancedRocketry.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererRocketBuilder extends TileEntityRenderer<TileRocketBuilder> {

	
	public RendererRocketBuilder(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	private ResourceLocation grid = new ResourceLocation("advancedrocketry:textures/models/grid.png");
	private ResourceLocation girder = new ResourceLocation("advancedrocketry:textures/models/girder.png");
	private ResourceLocation round_h = new ResourceLocation("advancedrocketry:textures/models/round_h.png");
	
	
	@Override
	public void render(TileRocketBuilder tile, float partialTicks, MatrixStack matrix,
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
			float uMin = 0;
			float vMin = 0;
			float uMax = 1;
			float vMax = 1;

			matrix.push();
			
			
			//Draw Supports
			IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getSolidEntityModelRenderType(girder));
			
			float size = 0.25f;
			
			vMax = (float) (yMin/size);
			
			float r = 0.78f, g= 0.5f, b = 0.34f, a = 1f;
			
			RenderHelper.renderCubeWithUV(entitySolidBuilder, xOffset, 0d, zOffset, xOffset + size, yOffset + yLocation, zOffset + size, (float)uMin, (float)uMax, (float)0d, (float)vMax, r,g,b,a);
			RenderHelper.renderCubeWithUV(entitySolidBuilder, xOffset + xSize - size, 0d, zOffset, xOffset  + xSize , yOffset + yLocation, zOffset + size, uMin, uMax, vMin, vMax, r,g,b,a);
			RenderHelper.renderCubeWithUV(entitySolidBuilder, xOffset + xSize - size, 0d, zOffset + zSize - size, xOffset  + xSize, yOffset + yLocation, zOffset + zSize, uMin, uMax, vMin, vMax, r,g,b,a);
			RenderHelper.renderCubeWithUV(entitySolidBuilder, xOffset, 0d, zOffset + zSize  - size, xOffset + size, yOffset + yLocation, zOffset + zSize, uMin, uMax, vMin, vMax, r,g,b,a);
			Tessellator.getInstance().draw();
			
			
			IVertexBuilder gridBuilder = buffer.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(grid));
			
			//Draw scanning grid
			if(tile.isBuilding())
			{
				r = 1;
				g = 0.5f;
				b = 0.5f;
				a = 0.05f;
			}
			else
			{
				r = 0.5f;
				g = 1f;
				b = 0.5f;
				a = 0.05f;
			}

			float min = 0;
			float maxU = (float)(1*xSize);
			float maxV = (float)(1*zSize);
			for(int i = 0; i < 20; i++) {
				//BOTTOM
				double offset = i/80d;
				RenderHelper.renderBottomFaceWithUV(gridBuilder, yOffset + yLocation+offset, xOffset, zOffset, xOffset + xSize, zOffset  + zSize, min, maxU, min, maxV,r,g,b,a);
				RenderHelper.renderTopFaceWithUV(gridBuilder, yOffset + yLocation+offset, xOffset, zOffset, xOffset + xSize, zOffset  + zSize, min, maxU, min, maxV, r,g,b,a);
				
				//TOP
			}
			
			IVertexBuilder round = buffer.getBuffer(RenderHelper.getSolidTexturedManualRenderType(round_h));
			uMax = 1f;
			vMax = 1f;
			//Draw "beam emitters"
			//West block
			
			RenderHelper.renderBottomFaceWithUV(round, yMin, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderWestFaceWithUV(round, xMin, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderSouthFaceWithUV(round, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderNorthFaceWithUV(round, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderTopFaceWithUV(round, yMax, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			
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
			
			RenderHelper.renderEastFace(beam, xMax, yMin, zMin, yMax, zMax,r,g,b,a);
			
			//Change mins/maxes then render east block
			xMin = xOffset + xSize - 0.5;
			xMax = xOffset + xSize;
			
			RenderHelper.renderWestFace(beam, xMin, yMin, zMin, yMax, zMax,r,g,b,a);
			
			
			RenderHelper.renderBottomFaceWithUV(round, yMin, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderEastFaceWithUV(round, xMax, yMin, zMin, yMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderSouthFaceWithUV(round, zMax, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderNorthFaceWithUV(round, zMin, xMin, yMin, xMax, yMax, uMin, uMax, vMin, vMax,1,1,1,1);
			RenderHelper.renderTopFaceWithUV(round, yMax, xMin, zMin, xMax, zMax, uMin, uMax, vMin, vMax,1,1,1,1);
			matrix.pop();
		}
	}

}
