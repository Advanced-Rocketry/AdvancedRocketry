package zmaster587.advancedRocketry.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.tile.cables.TilePipe;
import zmaster587.libVulpes.render.RenderHelper;

public class RendererPipe { //extends TileEntityRenderer {

/*
	private ResourceLocation texture;
	

	public RendererPipe(ResourceLocation texture) {
		this.texture = texture;
	}


	@Override
	public void render(TileEntity tile, double x, double y,
			double z, float f, int damage, float a) {
		
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		matrix.push();

		matrix.translate(x + 0.5F, y + 0.5F, z + 0.5F);
		
		bindTexture(texture);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glDisable(GL11.GL_LIGHTING);
		GlStateManager.color4f(0.4f, 0.4f, 0.4f);
		for(int i=0; i < 6; i++) {
			if(((TilePipe)tile).canConnect(i)) {
				matrix.push();

				Direction dir = Direction.values()[i];

				matrix.translate(0.5*dir.getXOffset(), 0.5*dir.getYOffset(), 0.5*dir.getZOffset());
				
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);

				//buffer.color(.4f, 0.4f, 0.4f,1f);
				RenderHelper.renderCube(buffer, -0.25f,  -0.25f,  -0.25f,  0.25f, 0.25f, 0.25f);
					//drawCube(0.25D, tessellator);
				//}
				Tessellator.getInstance().draw();

				matrix.pop();
			}
		}
		GlStateManager.color4f(1f,1f,1f);

		//GL11.glDisable(GL11.GL_BLEND);
		//GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		matrix.pop();
	}*/
}
