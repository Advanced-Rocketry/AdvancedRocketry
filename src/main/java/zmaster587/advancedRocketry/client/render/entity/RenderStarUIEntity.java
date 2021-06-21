package zmaster587.advancedRocketry.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
<<<<<<< HEAD
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
=======
import net.minecraft.client.renderer.GlStateManager;
>>>>>>> origin/feature/nuclearthermalrockets
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityUIStar;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderStarUIEntity extends EntityRenderer<EntityUIStar> implements IRenderFactory<EntityUIStar> {

	public RenderStarUIEntity(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public EntityRenderer<? super EntityUIStar> createRenderFor(
			EntityRendererManager manager) {
		return new RenderStarUIEntity(manager);
	}

	@Override
	public ResourceLocation getEntityTexture(EntityUIStar entity) {
		return DimensionProperties.PlanetIcons.EARTHLIKE.getResource();
	}

	@Override
	public void render(EntityUIStar entity, float entityYaw, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer bufferIn, int packedLightIn) {
		
		StellarBody body = entity.getStarProperties();
		if(body == null)
			return;
		float sizeScale = entity.getScale();
		matrix.scale(sizeScale,sizeScale,sizeScale);
		matrix.push();
		matrix.scale(.1f, .1f, .1f);
		matrix.translate(0, 2.5f, 0);
		
		matrix.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
		
		
		IVertexBuilder translucentBuffer = bufferIn.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(TextureResources.locationSunNew));
		RenderHelper.renderNorthFaceWithUV(matrix, translucentBuffer, 0, -5, -5, 5, 5, 0, 1, 0, 1, body.getColor()[0], body.getColor()[1], body.getColor()[2], 1f);
		matrix.pop();
		
		
		//Render hololines
<<<<<<< HEAD
		matrix.push();
		matrix.scale(.1f, .1f, .1f);
		
		IVertexBuilder buf = bufferIn.getBuffer(RenderHelper.getTranslucentManualRenderType());
		float myTime = ((entity.world.getGameTime() & 0xF)/16f);
=======
		GL11.glPushMatrix();
		GL11.glScaled(.1, .1, .1);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		GlStateManager.disableTexture2D();

		float myTime;
>>>>>>> origin/feature/nuclearthermalrockets
		
		for(int i = 0; i < 4; i++ ) {
			myTime = ((i*4 + entity.world.getGameTime() & 0xF)/16f);
			RenderHelper.renderTopFace(matrix, buf, myTime, -.5f, -.5f, .5f, .5f, 0, 1f, 1f, .2f*(1-myTime));
			RenderHelper.renderBottomFace(matrix, buf, myTime - 0.5, -.5f, -.5f, .5f, .5f, 0, 1f, 1f, .2f*(1-myTime));
		}
	
		
		//RenderSelection
		/*if(entity.isSelected()) {
			GlStateManager.disableTexture();
			double speedRotate = 0.025d;
			GlStateManager.color4f(0.4f, 0.4f, 1f, 0.6f);
			matrix.translate(0, -.75f, 0);
			matrix.push();
			GL11.glRotated(speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
			RendererWarpCore.model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, "Rotate1");
			matrix.pop();

			matrix.push();
			GL11.glRotated(180 + speedRotate*System.currentTimeMillis() % 360, 0f, 1f, 0f);
			RendererWarpCore.model.renderOnly(matrix, combinedLightIn, combinedOverlayIn, "Rotate1");
			matrix.pop();
			GlStateManager.enableTexture();
		}*/
		
		matrix.pop();
		
		/*RayTraceResult hitObj = Minecraft.getInstance().objectMouseOver;
		if(hitObj != null && hitObj.entityHit == entity) {
			
			matrix.push();
			GlStateManager.color4f(1, 1, 1);
			matrix.translate(x, y + sizeScale*0.03f, z);
			sizeScale = .1f*sizeScale;
			GL11.glScaled(sizeScale,sizeScale,sizeScale);
			
			//Render atmosphere UI/planet info
			
			RenderHelper.setupPlayerFacingMatrix(Minecraft.getInstance().player.getDistanceSq(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z), 0, 0, 0);
			buffer = Tessellator.getInstance().getBuffer();
			
			//Draw Mass indicator
			Minecraft.getInstance().getTextureManager().bindTexture(RenderPlanetUIEntity.planetUIFG);
			GlStateManager.color4f(1, 1, 1,0.8f);
			renderMassIndicator(buffer, body.getTemperature()/200f);
			
			//Draw background
			GlStateManager.color4f(1, 1, 1,1);
			Minecraft.getInstance().getTextureManager().bindTexture(RenderPlanetUIEntity.planetUIBG);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(matrix, buffer, 1, -40, -25, 40, 55, 1, 0, 1, 0);
			Tessellator.getInstance().draw();
			
			//Render planet name
			RenderHelper.cleanupPlayerFacingMatrix();
			RenderHelper.renderTag(Minecraft.getInstance().player.getDistanceSq(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z), body.getName(), 0, .9, 0, 5);
			RenderHelper.renderTag(Minecraft.getInstance().player.getDistanceSq(hitObj.hitVec.x, hitObj.hitVec.y, hitObj.hitVec.z), "Num Planets: " + body.getNumPlanets(), 0, .6, 0, 5);

			matrix.pop();
		}*/

		//Clean up and make player not transparent
	}
	
	protected void renderMassIndicator(MatrixStack matrix, BufferBuilder buffer, float percent) {
		
		float maxUV = (1-percent)*0.5f;
		
		RenderHelper.renderNorthFaceWithUV(matrix, buffer, 0, -20, -5 + 41*(1-percent), 20, 36, .5f, 0f, .5f, maxUV,1f,1f,1f,1f);
	}
	
	protected void renderATMIndicator(MatrixStack matrix, BufferBuilder buffer, float percent) {
		
		float maxUV = (1-percent)*0.406f + .578f;
		//Offset by 15 for Y
		RenderHelper.renderNorthFaceWithUV(matrix, buffer, 0, 6, 20 + (1-percent)*33, 39, 53, .5624f, .984f, .984f, maxUV,1f,1f,1f,1f);
	}
	
	protected void renderTemperatureIndicator(MatrixStack matrix, BufferBuilder buffer, float percent) {
		
		float maxUV = (1-percent)*0.406f + .578f;
		//Offset by 15 for Y
		RenderHelper.renderNorthFaceWithUV(matrix, buffer, 0, -38, 21.4f + (1-percent)*33, -4, 53, .016f, .4376f, .984f, maxUV,1f,1f,1f,1f);
	}
}
