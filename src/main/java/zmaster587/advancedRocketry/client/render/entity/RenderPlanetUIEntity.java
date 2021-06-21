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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityUIPlanet;
import zmaster587.libVulpes.render.RenderHelper;

public class RenderPlanetUIEntity extends EntityRenderer<EntityUIPlanet> implements IRenderFactory<EntityUIPlanet> {

	private static WavefrontObject sphere;
	public static ResourceLocation planetUIBG = new ResourceLocation("advancedrocketry","textures/gui/planetuioverlay.png");
	public static ResourceLocation planetUIFG = new ResourceLocation("advancedrocketry","textures/gui/planetuioverlayfg.png");

	static {
		try {
			sphere = new WavefrontObject(new ResourceLocation("advancedrocketry:models/atmosphere.obj"));
		} catch(ModelFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public RenderPlanetUIEntity(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public EntityRenderer<? super EntityUIPlanet> createRenderFor(
			EntityRendererManager manager) {
		return new RenderPlanetUIEntity(manager);
	}

	@Override
	public ResourceLocation getEntityTexture(EntityUIPlanet entity) {
		return DimensionProperties.PlanetIcons.EARTHLIKE.getResource();
	}
	
	@Override
	public void render(EntityUIPlanet entity, float entityYaw, float partialTicks, MatrixStack matrix,
			IRenderTypeBuffer bufferIn, int packedLightIn) {

		DimensionProperties properties = entity.getProperties();
		if(properties == null)
			return;

		float sizeScale = Math.max(properties.gravitationalMultiplier*properties.gravitationalMultiplier*entity.getScale(), .5f);
        int j = packedLightIn;
        int k = OverlayTexture.NO_OVERLAY;
		
		matrix.push();
		matrix.translate(0, sizeScale*0.03f, 0);
		//Max because moon was too small to be visible

		matrix.scale(.1f*sizeScale, .1f*sizeScale, .1f*sizeScale);
		IVertexBuilder translucentBuffer = bufferIn.getBuffer(RenderHelper.getTranslucentEntityModelRenderType(properties.getPlanetIconLEO()));
		
		
		
		matrix.push();
		matrix.rotate(new Quaternion(0, entity.world.getGameTime() & 0xFF, 0, true));
		sphere.tessellateAll(matrix, j, k, translucentBuffer);
		
		
		//Render shadow
		/*matrix.push();
		GL11.glScalef(1.1f, 1.1f, 1.1f);
		GL11.glRotatef(90, 0, 0, 1);
		GL11.glRotated( -(properties.orbitTheta * 180/Math.PI), 1, 0, 0);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.shadow3);
		GlStateManager.color4f(.1f, .1f, .1f,0.75f);
		sphere.renderAll(matrix);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		if(properties.hasRings) {
			//Rotate for rings
			GL11.glRotatef(90, 1, 0, 0);
			GL11.glRotatef(-90, 0, 0, 1);
			
			//Draw ring
			GlStateManager.color4f(properties.ringColor[0], properties.ringColor[1], properties.ringColor[2],0.5f);
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRings);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderTopFaceWithUV(matrix, buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			RenderHelper.renderBottomFaceWithUV(matrix, buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			Tessellator.getInstance().draw();

			//Draw ring shadow
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRingShadow);
			GlStateManager.color4f(1,1,1,0.5f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderTopFaceWithUV(matrix, buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			RenderHelper.renderBottomFaceWithUV(matrix, buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			Tessellator.getInstance().draw();
		}

		matrix.pop();*/

		//Render ATM
		IVertexBuilder atmBuffer = bufferIn.getBuffer(RenderHelper.getLightningTranslucencyNoTexEntityModelRenderType());
		if(properties.hasAtmosphere()) {
			matrix.push();
			matrix.scale(1.1f,1.1f,1.1f);
			for(int i = 0; i < 5; i++) {
				matrix.scale(1.01f,1.01f,1.01f);
				sphere.renderOnly(matrix, 0, 0, atmBuffer, properties.skyColor[0], properties.skyColor[1], properties.skyColor[2], 0.5f, "Sphere");	
			}
			matrix.pop();
		}
		matrix.pop();

		//Render hololines
		matrix.push();
		
		IVertexBuilder buf = bufferIn.getBuffer(RenderHelper.getTranslucentManualRenderType());

<<<<<<< HEAD
		float myTime = ((entity.world.getGameTime() & 0xF)/16f);
=======
		BufferBuilder buf = Tessellator.getInstance().getBuffer();
		GlStateManager.disableTexture2D();

		float myTime;
>>>>>>> origin/feature/nuclearthermalrockets

		for(int i = 0; i < 4; i++ ) {
			myTime = ((i*4 + entity.world.getGameTime() & 0xF)/16f);
			RenderHelper.renderTopFace(matrix, buf, myTime - 0.5, -.5f, -.5f, .5f, .5f, 0, 1f, 1f, .2f*(1-myTime));
			RenderHelper.renderBottomFace(matrix, buf, myTime - 0.5, -.5f, -.5f, .5f, .5f, 0, 1f, 1f, .2f*(1-myTime));
		}
		matrix.pop();

		//RenderSelection
		if(entity.isSelected()) {
			double speedRotate = 0.025d;
			//GlStateManager.color4f(0.4f, 0.4f, 1f, 0.6f);
			float r = 0.4f, g = 0.4f, b = 1.0f, a = 0.6f;
			matrix.translate(0, -1.25, 0);
			matrix.push();
			matrix.rotate(new Quaternion(0f, (float) (speedRotate*System.currentTimeMillis() % 360), 0f, true));
			RendererWarpCore.model.renderOnly(matrix, j, k, translucentBuffer, r,g,b,a, "Rotate1");
			matrix.pop();

			matrix.push();
			matrix.rotate(new Quaternion(0f, (float) (180 + speedRotate*System.currentTimeMillis() % 360), 0f, true));
			RendererWarpCore.model.renderOnly(matrix, j, k, translucentBuffer, r,g,b,a, "Rotate1");
			matrix.pop();
		}

		matrix.pop();

		
		
		RayTraceResult hitObj = Minecraft.getInstance().objectMouseOver;
		if(hitObj != null && hitObj.getType() == Type.ENTITY && ((EntityRayTraceResult)hitObj).getEntity() == entity) {

			matrix.push();
			matrix.translate(0, sizeScale*0.1f, 0);
			sizeScale = .005f*sizeScale;
			matrix.scale(sizeScale,sizeScale,sizeScale);

			//Render atmosphere UI/planet info

			matrix.rotate( Minecraft.getInstance().getRenderManager().getCameraOrientation());
			matrix.rotate(new Quaternion(0, 0, 180, true));
			
			IVertexBuilder HUDBufferFG = bufferIn.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(planetUIFG));
			//Draw Mass indicator
			renderMassIndicator(matrix, HUDBufferFG, Math.min(properties.gravitationalMultiplier/2f, 1f));

			//Draw background
			IVertexBuilder HUDBufferBG = bufferIn.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(planetUIBG));
			RenderHelper.renderNorthFaceWithUV(matrix, HUDBufferBG, 1, -40, -25, 40, 55, 1, 0, 1, 0);


			//Render ATM
			HUDBufferFG = bufferIn.getBuffer(RenderHelper.getTranslucentTexturedManualRenderType(planetUIFG));
			
			renderATMIndicator(matrix, HUDBufferFG, Math.min(properties.getAtmosphereDensity()/200f, 1f));
			//Render Temp
			renderTemperatureIndicator(matrix, HUDBufferFG, Math.min(properties.getAverageTemp()/400f,1f));

			//Render planet name
			RenderHelper.renderTag(matrix, bufferIn, Minecraft.getInstance().player.getDistanceSq(hitObj.getHitVec().z, hitObj.getHitVec().y, hitObj.getHitVec().x), properties.getName(), packedLightIn, 5);
			RenderHelper.renderTag(matrix, bufferIn, Minecraft.getInstance().player.getDistanceSq(hitObj.getHitVec().z, hitObj.getHitVec().y, hitObj.getHitVec().x), "NumMoons: " + properties.getChildPlanets().size(), packedLightIn, 5);

			matrix.pop();
		}
	}

	protected void renderMassIndicator(MatrixStack matrix, IVertexBuilder buffer, float percent) {

		float maxUV = (1-percent)*0.5f;

		RenderHelper.renderNorthFaceWithUV(matrix, buffer, 0, -20, -5 + 41*(1-percent), 20, 36, .5f, 0f, .5f, maxUV,1,1,1,1);
	}

	protected void renderATMIndicator(MatrixStack matrix, IVertexBuilder buffer, float percent) {

		float maxUV = (1-percent)*0.406f + .578f;
		//Offset by 15 for Y
		RenderHelper.renderNorthFaceWithUV(matrix, buffer, 0, 6, 20 + (1-percent)*33, 39, 53, .5624f, .984f, .984f, maxUV, 1,1,1,1);
	}

	protected void renderTemperatureIndicator(MatrixStack matrix, IVertexBuilder buffer, float percent) {

		float maxUV = (1-percent)*0.406f + .578f;
		//Offset by 15 for Y
		RenderHelper.renderNorthFaceWithUV(matrix, buffer, 0, -38, 21.4f + (1-percent)*33, -4, 53, .016f, .4376f, .984f, maxUV, 1,1,1,1);
	}
}
