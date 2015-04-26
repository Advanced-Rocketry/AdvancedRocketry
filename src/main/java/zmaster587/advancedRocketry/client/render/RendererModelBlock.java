package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.client.render.model.ITextureModel;
import zmaster587.advancedRocketry.tile.TileModelRender;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class RendererModelBlock  extends TileEntitySpecialRenderer {

	protected static ITextureModel model;
	


	public RendererModelBlock() {
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x,
			double y, double z, float f) {

		
		TileModelRender rendertile = (TileModelRender)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y+ 0.5, z+ 0.5);
		bindTexture(rendertile.getTexture());
		rendertile.getModel().renderAll();
		GL11.glPopMatrix();
	}

}
