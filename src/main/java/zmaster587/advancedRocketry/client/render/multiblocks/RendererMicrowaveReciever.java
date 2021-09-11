package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererMicrowaveReciever extends TileEntitySpecialRenderer {

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/blocks/solar.png");
	ResourceLocation panelSide = new ResourceLocation("advancedrocketry:textures/blocks/panelSide.png");

	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {
		TileMicrowaveReciever multiBlockTile = (TileMicrowaveReciever)tile;

		if(!multiBlockTile.canRender())
			return;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		//Initial setup
		bindTexture(texture);
		
		//Initial setup
        int i2 = this.getWorld().getCombinedLight(tile.getPos().add(0, 1, 0), 0);
        int j = i2 % 65536;
        int k = i2 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        
		//Draw heat FX
		if(ARConfiguration.getCurrentConfig().advancedVFX && multiBlockTile.getPowerMadeLastTick() > 0) {
			double distance = Math.sqrt(Minecraft.getMinecraft().player.getDistanceSq(tile.getPos()));
			if(distance < 16 ) {
				double u = 256/distance;
				double resolution = (int)u;

				double[][] yLoc = new double[(int)resolution][(int)resolution];

				for(int i = 0; i < (int)resolution; i++) {
					for(int g = 0; g < (int)resolution; g++) {
						double amplitideMax = 0.002/resolution;

						amplitideMax *= (resolution/2) - Math.abs(g - resolution/2);
						amplitideMax *= (resolution/2) - Math.abs(i - resolution/2);

						yLoc[i][g] = amplitideMax*MathHelper.sin(((i*16 + g + tile.getWorld().getTotalWorldTime()) & 0xffff)*0.5f);
					}

				}

				GL11.glPushMatrix();
				GL11.glTranslated(-2, 0, -2);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				
				for(int i = 0; i < (int)resolution; i++) {
					for(int g = 0; g < (int)resolution; g++) {
						RenderHelper.renderTopFaceWithUV(buffer, 1.01 + yLoc[i][g], 5*i/resolution, 5*g/resolution, 5*(i+1)/resolution, 5*(g+1)/resolution, 5*i/resolution, 5*(i+1)/resolution, 5*g/resolution, 5*(g+1)/resolution);
					}
				}
				Tessellator.getInstance().draw();
				GL11.glPopMatrix();
			}
		}

		//Draw main panel
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.renderTopFaceWithUV(buffer, 1.01, -2, -2, 3, 3, 0, 5, 0, 5);
		Tessellator.getInstance().draw();
		//And sides
		
		bindTexture(panelSide);
		
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.renderNorthFaceWithUV(buffer, -1.99, -2, 0, 3, 1, 0, 5, 0 ,1);
		RenderHelper.renderSouthFaceWithUV(buffer, 2.99, -2, 0, 3, 1, 0, 5, 0 ,1);
		RenderHelper.renderEastFaceWithUV(buffer, 2.99, 0, -2, 1, 3, 0, 5, 0 ,1);
		RenderHelper.renderWestFaceWithUV(buffer, -1.99, 0, -2, 1, 3, 0, 5, 0 ,1);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		RenderHelper.renderBottomFace(buffer, 0.001, -2, -2, 3, 3);
		
		RenderHelper.renderCubeWithUV(buffer, -2, 0.99, -2, -1.9, 1.1, 3, 0, 0, 0,0);
		RenderHelper.renderCubeWithUV(buffer, -2, 0.99, -2, 3, 1.1, -1.9, 0, 0, 0,0);
		
		RenderHelper.renderCubeWithUV(buffer, -1.9, 0.99, 2.9, 3, 1.1, 3, 0, 0, 0,0);
		RenderHelper.renderCubeWithUV(buffer, 2.9, 0.99, -1.9, 3, 1.1, 3, 0, 0, 0,0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		Tessellator.getInstance().draw();

		if(multiBlockTile.getPowerMadeLastTick() > 0 ) {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_FOG);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDepthMask(false);

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_NONE);
			GL11.glPushMatrix();
			GlStateManager.color(0.2F, 0.2F, 0.2F, 0.3F);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
	
			//GL11.glTranslated(0.5, 0, 0.5);
			//GL11.glRotated(tile.getWorldObj().getTotalWorldTime()/10.0 % 360, 0, 1, 0);
			//GL11.glTranslated(-0.3, 0, -0.3);
			
			for(float radius = 0.25F; radius < 2; radius += .25F) {

				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					buffer.pos(- x , -y + 200,  - z).endVertex();
					buffer.pos(- x, -y + 200, - z).endVertex();
					buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F).endVertex();
					buffer.pos((radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F).endVertex();
				}

				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					buffer.pos(- x, -y + 200,- z).endVertex();
					buffer.pos(- x, -y + 200, - z).endVertex();
					buffer.pos((radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F).endVertex();
					buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F).endVertex();
				}
			}
			Tessellator.getInstance().draw();

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
