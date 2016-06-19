package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.cables.TilePipe;
import zmaster587.libVulpes.render.RenderHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class RendererPipe extends TileEntitySpecialRenderer {


	private ResourceLocation texture;
	

	public RendererPipe(ResourceLocation texture) {
		this.texture = texture;
	}
	
	public void drawCube(double radius, Tessellator tessellator) {
		//TOP
		tessellator.addVertex(radius, -radius, radius);
		tessellator.addVertex(radius, radius, radius);
		tessellator.addVertex(-radius, radius, radius);
		tessellator.addVertex(-radius, -radius, radius);

		//BOTTOM
		tessellator.addVertex(radius, radius, -radius);
		tessellator.addVertex(radius, -radius, -radius);
		tessellator.addVertex(-radius, -radius, -radius);
		tessellator.addVertex(-radius, radius, -radius);

		//EAST
		tessellator.addVertex(radius, -radius, -radius);
		tessellator.addVertex(radius, radius, -radius);
		tessellator.addVertex(radius, radius, radius);
		tessellator.addVertex(radius, -radius, radius);

		//SOUTH
		tessellator.addVertex(radius, -radius, -radius);
		tessellator.addVertex(radius, -radius, radius);
		tessellator.addVertex(-radius, -radius, radius);
		tessellator.addVertex(-radius, -radius, -radius);

		//WEST
		tessellator.addVertex(-radius, -radius, radius);
		tessellator.addVertex(-radius, radius, radius);
		tessellator.addVertex(-radius, radius, -radius);
		tessellator.addVertex(-radius, -radius, -radius);

		//NORTH
		tessellator.addVertex(radius, radius, radius);
		tessellator.addVertex(radius, radius, -radius);
		tessellator.addVertex(-radius, radius, -radius);
		tessellator.addVertex(-radius, radius, radius);
	}
	
	public void drawCubeUV(double radius, Tessellator tessellator) {
		//TOP
		tessellator.addVertexWithUV(radius, -radius, radius,1,0);
		tessellator.addVertexWithUV(radius, radius, radius,1,1);
		tessellator.addVertexWithUV(-radius, radius, radius,0,1);
		tessellator.addVertexWithUV(-radius, -radius, radius,0,0);

		//BOTTOM
		tessellator.addVertexWithUV(radius, radius, -radius,1,1);
		tessellator.addVertexWithUV(radius, -radius, -radius,1,0);
		tessellator.addVertexWithUV(-radius, -radius, -radius,0,0);
		tessellator.addVertexWithUV(-radius, radius, -radius,0,1);

		//EAST
		tessellator.addVertexWithUV(radius, -radius, -radius,0,0);
		tessellator.addVertexWithUV(radius, radius, -radius,1,0);
		tessellator.addVertexWithUV(radius, radius, radius,1,1);
		tessellator.addVertexWithUV(radius, -radius, radius,0,1);

		//SOUTH
		tessellator.addVertexWithUV(radius, -radius, -radius,1,0);
		tessellator.addVertexWithUV(radius, -radius, radius,1,1);
		tessellator.addVertexWithUV(-radius, -radius, radius,0,1);
		tessellator.addVertexWithUV(-radius, -radius, -radius,0,0);

		//WEST
		tessellator.addVertexWithUV(-radius, -radius, radius,0,1);
		tessellator.addVertexWithUV(-radius, radius, radius,1,1);
		tessellator.addVertexWithUV(-radius, radius, -radius,1,0);
		tessellator.addVertexWithUV(-radius, -radius, -radius,0,0);

		//NORTH
		tessellator.addVertexWithUV(radius, radius, radius,1,1);
		tessellator.addVertexWithUV(radius, radius, -radius,1,0);
		tessellator.addVertexWithUV(-radius, radius, -radius,0,0);
		tessellator.addVertexWithUV(-radius, radius, radius,0,1);
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y,
			double z, float f) {
		Tessellator tessellator = Tessellator.instance;

		GL11.glPushMatrix();

		GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
		
		
		//GL11.glEnable(GL11.GL_BLEND);
		//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);


		/*tessellator.startDrawingQuads();

		drawCube(0.35D, tessellator);

		tessellator.draw();*/
		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		
		bindTexture(texture);
		
		tessellator.startDrawingQuads();
		RenderHelper.renderCubeWithUV(Tessellator.instance, -0.3f,  -0.3f,  -0.3f,  0.3f, 0.3f, 0.3f, 0, 1, 0, 1);
		tessellator.draw();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glDisable(GL11.GL_LIGHTING);
		
		for(int i=0; i < 6; i++) {
			if(((TilePipe)tile).canConnect(i)) {
				GL11.glPushMatrix();

				ForgeDirection dir = ForgeDirection.getOrientation(i);

				GL11.glTranslated(0.5*dir.offsetX, 0.5*dir.offsetY, 0.5*dir.offsetZ);

				tessellator.startDrawingQuads();

				//tessellator.setColorRGBA_F(0.1F, 0.1F, 0.95F, 1.0f);
				
				//bindTexture(texture);
				
				//for(int g=0; g < 8; g++) {
				tessellator.setColorOpaque_F(.4f, 0.4f, 0.4f);
				RenderHelper.renderCubeWithUV(Tessellator.instance, -0.25f,  -0.25f,  -0.25f,  0.25f, 0.25f, 0.25f, 0, 0, 0, 0);
					//drawCube(0.25D, tessellator);
				//}

				tessellator.draw();

				GL11.glPopMatrix();
			}
		}

		//GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}
}
