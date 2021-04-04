package zmaster587.advancedRocketry.client.render;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill.TileOrbitalLaserDrill;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderOrbitalLaserDrillTile extends TileEntityRenderer<TileOrbitalLaserDrill> {
	
	
	public RenderOrbitalLaserDrillTile(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}
	
	@Override
	public void render(TileOrbitalLaserDrill tileentity, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {

		if(!((TileOrbitalLaserDrill)tileentity).isRunning())
			return;
		
		matrix.push();
		IVertexBuilder entitySolidBuilder = buffer.getBuffer(RenderHelper.getLaserBeamType());
		
		float x = 0, y = 0, z = 0;

		for(float radius = 0.1F; radius < .5; radius += .1F) {
			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				
				entitySolidBuilder.pos(- x , -y - 100,  - z).color(0.9F, 0.2F, 0.3F, 1F).endVertex();
				entitySolidBuilder.pos(- x, -y - 100, - z).color(0.9F, 0.2F, 0.3F, 1F).endVertex();
				entitySolidBuilder.pos(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F).color(0.9F, 0.2F, 0.3F, 1F).endVertex();
				entitySolidBuilder.pos(+ (radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F).color(0.9F, 0.2F, 0.3F, 1F).endVertex();
			}

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				entitySolidBuilder.pos(- x, -y - 100,- z).color(0.9F, 0.2F, 0.3F, 1F).endVertex();
				entitySolidBuilder.pos(- x, -y - 100, - z).color(0.9F, 0.2F, 0.3F, 1F).endVertex();
				entitySolidBuilder.pos(+ (radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F).color(0.9F, 0.2F, 0.3F, 1F).endVertex();
				entitySolidBuilder.pos(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F).color(0.9F, 0.2F, 0.3F, 1F).endVertex();
			}
		}
		matrix.pop();
	}

}
