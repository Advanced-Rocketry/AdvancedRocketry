package zmaster587.advancedRocketry.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderLaser extends EntityRenderer<EntityLaserNode> implements IRenderFactory<EntityLaserNode> {

	private static final ResourceLocation flare = new ResourceLocation("advancedrocketry", "textures/entity/flare.png");

	float[] color;
	float[] flareColor;
	//float flareColo
	private double size;
	
	public RenderLaser(double size, float[] flarecolor, float[] color) {
		super(Minecraft.getInstance().getRenderManager());
		this.size = size;
		this.flareColor = flarecolor;
		this.color = color;
	}
	
	@Override
	public void render(EntityLaserNode entity, float entityYaw, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer buffer, int packedLightIn) {

		matrix.push();
		doRender(buffer, matrix);
		matrix.pop();
	}
	
	public void doRender(IRenderTypeBuffer buffer, MatrixStack matrix) {
		IVertexBuilder translucentRenderer = buffer.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(flare));
		float x = 0, y = 0, z = 0;

		for(int i = 0; i < 4; i++) {
			RenderHelper.renderBottomFaceWithUV(matrix, translucentRenderer, -y + 200, -(i*6) - x, -(i*6) - z, (i*6) - x, (i*6) - z, 0, 1, 0, 1, flareColor[0],flareColor[1],flareColor[2],flareColor[3]);
		}


		IVertexBuilder laserBuilder = buffer.getBuffer(RenderHelper.getLaserBeamType());

		
		
		for(float radius = 0.25F; radius < size; radius += .25F) {

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				RenderHelper.vertexPos(matrix, laserBuilder, - x , -y + 200,  - z).color(color[0], color[1], color[2], color[3]).endVertex();
				RenderHelper.vertexPos(matrix, laserBuilder, - x, -y + 200, - z).color(color[0], color[1], color[2], color[3]).endVertex();
				RenderHelper.vertexPos(matrix, laserBuilder, - (radius* Math.cos(i)) + 0.5F, 0,- (radius* Math.sin(i)) + 0.5F).color(color[0], color[1], color[2], color[3]).endVertex();
				RenderHelper.vertexPos(matrix, laserBuilder, + (radius* Math.sin(i)) + 0.5F, 0, (radius* Math.cos(i)) + 0.5F).color(color[0], color[1], color[2], color[3]).endVertex();
			}

			for(double i = 0; i < 2*Math.PI; i += Math.PI) {
				RenderHelper.vertexPos(matrix, laserBuilder, - x, -y + 200,- z).color(color[0], color[1], color[2], color[3]).endVertex();
				RenderHelper.vertexPos(matrix, laserBuilder, - x, -y + 200, - z).color(color[0], color[1], color[2], color[3]).endVertex();
				RenderHelper.vertexPos(matrix, laserBuilder, + (radius* Math.sin(i)) + 0.5F, 0, -(radius* Math.cos(i)) + 0.5F).color(color[0], color[1], color[2], color[3]).endVertex();
				RenderHelper.vertexPos(matrix, laserBuilder, - (radius* Math.cos(i)) + 0.5F, 0,(radius* Math.sin(i)) + 0.5F).color(color[0], color[1], color[2], color[3]).endVertex();
			}
		}
	}

    @Override
	public EntityRenderer<? super EntityLaserNode> createRenderFor(EntityRendererManager manager) {
		return this;
	}

	@Override
	public ResourceLocation getEntityTexture(EntityLaserNode entity) {
		return null;
	}
}
