package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.tile.multiblock.energy.TileMicrowaveReciever;
import zmaster587.libVulpes.render.RenderHelper;

import javax.annotation.ParametersAreNonnullByDefault;

public class RendererMicrowaveReciever extends TileEntityRenderer<TileMicrowaveReciever> {

	public RendererMicrowaveReciever(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	ResourceLocation texture = new ResourceLocation("advancedrocketry","textures/blocks/machines/solar.png");
	ResourceLocation panelSide = new ResourceLocation("libvulpes","textures/blocks/machinegeneric.png");
	
	@Override
	@ParametersAreNonnullByDefault
	public void render(TileMicrowaveReciever tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!tile.canRender())
			return;

		if (tile.getWorld() != null) {
			combinedLightIn = WorldRenderer.getCombinedLight(tile.getWorld(), tile.getPos().add(0, 1, 0));
		} else {
			combinedLightIn = 15728880;
		}

		matrix.push();
		//Initial setup
		IVertexBuilder entitySolidManual;
		IVertexBuilder entitySolidSideManual;
		IVertexBuilder laserBeam;
		
		//Initial setup
        
        
		//Draw heat FX
		entitySolidManual = buffer.getBuffer(RenderHelper.getSolidTexturedManualRenderType(texture));
		if(tile.getPowerMadeLastTick() > 0) {
			double distance = tile.getPos().distanceSq(new BlockPos( Minecraft.getInstance().player.getPositionVec()));
			if(distance < 16*16 ) {
				double u = 256/distance;
				double resolution = (int)u;

				double[][] yLoc = new double[(int)resolution][(int)resolution];

				for(int i = 0; i < (int)resolution; i++) {
					for(int g = 0; g < (int)resolution; g++) {
						double amplitideMax = 0.002/resolution;

						amplitideMax *= (resolution/2) - Math.abs(g - resolution/2);
						amplitideMax *= (resolution/2) - Math.abs(i - resolution/2);

						yLoc[i][g] = amplitideMax*MathHelper.sin(((i*16 + g + tile.getWorld().getGameTime()) & 0xffff)*0.5f);
					}

				}

				matrix.push();
				matrix.translate(-2, 0, -2);
				
				for(int i = 0; i < (int)resolution; i++) {
					for(int g = 0; g < (int)resolution; g++) {
						RenderHelper.renderTopFaceWithUV(matrix, entitySolidManual, 1.01 + yLoc[i][g], 5*i/resolution, 5*g/resolution, 5*(i+1)/resolution, 5*(g+1)/resolution,(float) (5*i/resolution), (float)(5*(i+1)/resolution), (float)(5*g/resolution), (float)(5*(g+1)/resolution),1f,1f,1f,1f);
					}
				}
				matrix.pop();
			}
		}

		//Draw main panel
		RenderHelper.renderTopFaceWithUV(matrix, entitySolidManual, 1.01, -2, -2, 3, 3, 0, 5, 0, 5,1,1,1,1);
		//And sides
		
		entitySolidSideManual = buffer.getBuffer(RenderHelper.getSolidTexturedManualRenderType(panelSide));
		RenderHelper.renderNorthFaceWithUV(matrix, entitySolidSideManual, -1.99, -2, 0, 3, 1, 0, 5, 0 ,1,1,1,1,1);
		RenderHelper.renderSouthFaceWithUV(matrix, entitySolidSideManual, 2.99, -2, 0, 3, 1, 0, 5, 0 ,1,1,1,1,1);
		RenderHelper.renderEastFaceWithUV(matrix, entitySolidSideManual, 2.99, 0, -2, 1, 3, 0, 5, 0 ,1,1,1,1,1);
		RenderHelper.renderWestFaceWithUV(matrix, entitySolidSideManual, -1.99, 0, -2, 1, 3, 0, 5, 0 ,1,1,1,1,1);

		RenderHelper.renderBottomFaceWithUV(matrix, entitySolidSideManual, 0.001, -2, -2, 3, 3,1,1,1,1, 1, 1, 1, 1);
		
		RenderHelper.renderCubeWithUV(matrix, entitySolidSideManual, -2, 0.99, -2, -1.9, 1.1, 3, 1,1,1,1, 1, 1, 1, 1);
		RenderHelper.renderCubeWithUV(matrix, entitySolidSideManual, -2, 0.99, -2, 3, 1.1, -1.9,1,1,1,1, 1, 1, 1, 1);

		RenderHelper.renderCubeWithUV(matrix, entitySolidSideManual, -1.9, 0.99, 2.9, 3, 1.1, 3, 1,1,1,1, 1, 1, 1, 1);
		RenderHelper.renderCubeWithUV(matrix, entitySolidSideManual, 2.9, 0.99, -1.9, 3, 1.1, 3, 1,1,1,1, 1, 1, 1, 1);

		float r = 1,g = 1,b = 1,a = 0.5f;
		if(tile.getPowerMadeLastTick() > 0 ) {
			matrix.push();
			//GlStateManager.color4f(0.2F, 0.2F, 0.2F, 0.3F);
			float x = 0, y = 0, z = 0;
			laserBeam = buffer.getBuffer(RenderHelper.getLaserBeamType());
			
			for(float radius = 0.25F; radius < 2; radius += .25F) {

				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					laserBeam.pos(- x , -y + 200,  - z).color(r,g,b,a).endVertex();
					laserBeam.pos(- x, -y + 200, - z).color(r,g,b,a).endVertex();
					laserBeam.pos(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F).color(r,g,b,a).endVertex();
					laserBeam.pos((radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F).color(r,g,b,a).endVertex();
				}

				for(double i = 0; i < 2*Math.PI; i += Math.PI) {
					laserBeam.pos(- x, -y + 200,- z).color(r,g,b,a).endVertex();
					laserBeam.pos(- x, -y + 200, - z).color(r,g,b,a).endVertex();
					laserBeam.pos((radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F).color(r,g,b,a).endVertex();
					laserBeam.pos(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F).color(r,g,b,a).endVertex();
				}
			}

			matrix.pop();
		}

		matrix.pop();
	}
}
