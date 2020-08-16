package zmaster587.advancedRocketry.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.tile.multiblock.TileSpaceLaser;

public class RenderLaserTile extends TileEntitySpecialRenderer {

	@Override
	public void render(TileEntity tileentity, double x, double y,
			double z, float f, int damage, float a) {

		if(!((TileSpaceLaser)tileentity).isRunning())
			return;
		
		matrix.push();
		matrix.translate(x, y, z);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		//GL11.glB
		//GL11.gl
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		
		buffer.color(0.9F, 0.2F, 0.3F, 1F);

		for(float radius = 0.1F; radius < .5; radius += .1F) {
			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				
				buffer.pos(- x , -y - 100,  - z).endVertex();
				buffer.pos(- x, -y - 100, - z).endVertex();
				buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F).endVertex();
				buffer.pos(+ (radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F).endVertex();
			}

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				buffer.pos(- x, -y - 100,- z).endVertex();
				buffer.pos(- x, -y - 100, - z).endVertex();
				buffer.pos(+ (radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F).endVertex();
				buffer.pos(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F).endVertex();
			}
		}

		buffer.endVertex();

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture();
		GlStateManager.enableFog();
		GlStateManager.enableDepth();
		matrix.pop();
	}

}
