package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class RenderLaserTile extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {

		if(!((TileSpaceLaser)tileentity).isRunning())
			return;
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		//GL11.glB
		//GL11.gl
		tessellator.startDrawing(7);
		
		tessellator.setColorRGBA_F(0.9F, 0.2F, 0.3F, 1F);

		for(float radius = 0.1F; radius < .5; radius += .1F) {

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				tessellator.addVertex(- x , -y - 100,  - z);
				tessellator.addVertex(- x, -y - 100, - z);
				tessellator.addVertex(- (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F);
				tessellator.addVertex(+ (radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F);
			}

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				tessellator.addVertex(- x, -y - 100,- z);
				tessellator.addVertex(- x, -y - 100, - z);
				tessellator.addVertex(+ (radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F);
				tessellator.addVertex(- (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F);
			}
		}

		tessellator.draw();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();
	}

}
