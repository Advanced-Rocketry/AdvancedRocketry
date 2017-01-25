package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public class RenderLaser extends Render {

	private static final ResourceLocation flare = new ResourceLocation("advancedrocketry", "textures/entity/Flare.png");

	float color[];
	float flareColor[];
	//float flareColo
	double size;
	
	public RenderLaser(double size, float[] flarecolor, float[] color) {
		this.size = size;
		this.flareColor = flarecolor;
		this.color = color;
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z,
			float f, float f1) {

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Minecraft.getMinecraft().renderEngine.bindTexture(flare);
		//bindTexture(flare);

		tessellator.startDrawing(7);

		tessellator.setColorRGBA_F(flareColor[0],flareColor[1],flareColor[2],flareColor[3]);

		for(int i = 0; i < 4; i++) {
			tessellator.addVertexWithUV(-(i*6) - x, -y + 200, (i*6) - z, 0,1);
			tessellator.addVertexWithUV(-(i*6) - x, -y + 200, -(i*6) - z,0,0);
			tessellator.addVertexWithUV((i*6) - x, -y + 200, -(i*6) - z,1,0);
			tessellator.addVertexWithUV((i*6) - x, -y + 200, (i*6) - z,1,1);
		}

		tessellator.draw();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		tessellator.startDrawing(7);
		tessellator.setColorRGBA_F(color[0], color[1], color[2], color[3]);//0.9F, 0.2F, 0.3F, 0.5F);

		for(float radius = 0.25F; radius < size; radius += .25F) {

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

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glDepthMask(true);
		GL11.glPopMatrix();

	}

	@Override
	public void doRenderShadowAndFire(Entity p_76979_1_, double p_76979_2_,
			double p_76979_4_, double p_76979_6_, float p_76979_8_,
			float p_76979_9_) {
		//Do not render shadow or fire
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

}
