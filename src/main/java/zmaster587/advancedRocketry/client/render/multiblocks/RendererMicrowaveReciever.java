package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererMicrowaveReciever extends TileEntitySpecialRenderer {

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/blocks/solar.png");
	ResourceLocation panelSide = new ResourceLocation("advancedrocketry:textures/blocks/panelSide.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {
		TileMicrowaveReciever multiBlockTile = (TileMicrowaveReciever)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		Tessellator tessellator = Tessellator.instance;

		//Initial setup
		int bright = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord,0);
		int brightX = bright % 65536;
		int brightY = bright / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightX, brightY);
		bindTexture(texture);


		//Draw heat FX
		if(Configuration.advancedVFX && multiBlockTile.getPowerMadeLastTick() > 0) {
			double distance = Minecraft.getMinecraft().thePlayer.getDistance(tile.xCoord, tile.yCoord, tile.zCoord);
			if(distance < 16 ) {
				double u = 256/distance;
				double resolution = (int)u;

				double yLoc[][] = new double[(int)resolution][(int)resolution];

				for(int i = 0; i < (int)resolution; i++) {
					for(int g = 0; g < (int)resolution; g++) {
						double amplitideMax = 0.002/resolution;

						amplitideMax *= (resolution/2) - Math.abs(g - resolution/2);
						amplitideMax *= (resolution/2) - Math.abs(i - resolution/2);

						yLoc[i][g] = amplitideMax*MathHelper.sin(((i*16 + g + tile.getWorldObj().getTotalWorldTime()) & 0xffff)*0.5f);
					}

				}

				GL11.glPushMatrix();
				GL11.glTranslated(-2, 0, -2);
				tessellator.startDrawingQuads();
				for(int i = 0; i < (int)resolution; i++) {
					for(int g = 0; g < (int)resolution; g++) {
						RenderHelper.renderTopFaceWithUV(tessellator, 1.01 + yLoc[i][g], 5*i/resolution, 5*g/resolution, 5*(i+1)/resolution, 5*(g+1)/resolution, 5*i/resolution, 5*(i+1)/resolution, 5*g/resolution, 5*(g+1)/resolution);
					}
				}
				tessellator.draw();
				GL11.glPopMatrix();
			}
		}

		//Draw main panel
		tessellator.startDrawingQuads();
		RenderHelper.renderTopFaceWithUV(tessellator, 1.01, -2, -2, 3, 3, 0, 5, 0, 5);
		tessellator.draw();
		//And sides
		
		bindTexture(panelSide);
		
		tessellator.startDrawingQuads();
		RenderHelper.renderNorthFaceWithUV(tessellator, -1.99, -2, 0, 3, 1, 0, 5, 0 ,1);
		RenderHelper.renderSouthFaceWithUV(tessellator, 2.99, -2, 0, 3, 1, 0, 5, 0 ,1);
		RenderHelper.renderEastFaceWithUV(tessellator, 2.99, 0, -2, 1, 3, 0, 5, 0 ,1);
		RenderHelper.renderWestFaceWithUV(tessellator, -1.99, 0, -2, 1, 3, 0, 5, 0 ,1);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		RenderHelper.renderBottomFace(tessellator, 0.001, -2, -2, 3, 3);
		
		RenderHelper.renderCubeWithUV(tessellator, -2, 0.99, -2, -1.9, 1.1, 3, 0, 0, 0,0);
		RenderHelper.renderCubeWithUV(tessellator, -2, 0.99, -2, 3, 1.1, -1.9, 0, 0, 0,0);
		
		RenderHelper.renderCubeWithUV(tessellator, -1.9, 0.99, 2.9, 3, 1.1, 3, 0, 0, 0,0);
		RenderHelper.renderCubeWithUV(tessellator, 2.9, 0.99, -1.9, 3, 1.1, 3, 0, 0, 0,0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		tessellator.draw();

		if(multiBlockTile.getPowerMadeLastTick() > 0 ) {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_FOG);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(false);

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glPushMatrix();
			tessellator.startDrawing(7);
			tessellator.setColorRGBA_F(0.2F, 0.2F, 0.2F, 0.3F);

			//GL11.glTranslated(0.5, 0, 0.5);
			//GL11.glRotated(tile.getWorldObj().getTotalWorldTime()/10.0 % 360, 0, 1, 0);
			//GL11.glTranslated(-0.3, 0, -0.3);
			
			for(float radius = 0.25F; radius < 2; radius += .25F) {

				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					tessellator.addVertex(- x , -y + 200,  - z);
					tessellator.addVertex(- x, -y + 200, - z);
					tessellator.addVertex(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F);
					tessellator.addVertex(+ (radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F);
				}

				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					tessellator.addVertex(- x, -y + 200,- z);
					tessellator.addVertex(- x, -y + 200, - z);
					tessellator.addVertex(+ (radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F);
					tessellator.addVertex(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F);
				}
			}
			tessellator.draw();

			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glDepthMask(true);
		}

		GL11.glPopMatrix();
	}
}
