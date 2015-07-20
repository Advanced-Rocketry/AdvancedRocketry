package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.tile.TileMissionController;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.render.TextPart;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RendererMasterPanel extends TileEntitySpecialRenderer {
	ResourceLocation font = new ResourceLocation("advancedRocketry:textures/font.png");
	public RendererMasterPanel() {}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		Tessellator tessellator = Tessellator.instance;
		
		GL11.glPushMatrix();
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		
		int direction = tileentity.getBlockMetadata() & 3;
		
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glRotatef(direction * 90f + 180, 0, 1, 0);
		GL11.glTranslated(-0.5D, 1D, 0.5001D);
		
		String name = ((TileMissionController)tileentity).getSatelliteName();
		
		bindTexture(font);
		
		TextPart text = new TextPart("Connected:\n" + name, 0.09f, 0XFFFFFF);
		
		RenderHelper.renderText(text, 20, 10, font);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
}
