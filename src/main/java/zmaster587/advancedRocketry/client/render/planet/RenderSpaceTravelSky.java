package zmaster587.advancedRocketry.client.render.planet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.client.render.entity.RenderPlanetUIEntity;
import zmaster587.advancedRocketry.client.render.multiblocks.RendererWarpCore;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.EntityUIPlanet;
import zmaster587.advancedRocketry.entity.EntityUIStar;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.SpacePosition;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.Vector3F;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RenderSpaceTravelSky extends RenderPlanetarySky {


	private VertexBuffer starGLCallList;
	private VertexBuffer glSkyList;
	private VertexBuffer glSkyList2;
	ResourceLocation currentlyBoundTex = null;
	float celestialAngle;
	Vector3F<Float> axis;

	private static WavefrontObject sphere;
	public static ResourceLocation planetUIBG = new ResourceLocation("advancedrocketry:textures/gui/planetUIOverlay.png");
	public static ResourceLocation planetUIFG = new ResourceLocation("advancedrocketry:textures/gui/planetUIOverlayFG.png");

	static {
		try {
			sphere = new WavefrontObject(new ResourceLocation("advancedrocketry:models/atmosphere.obj"));
		} catch(ModelFormatException e) {
			throw new RuntimeException(e);
		}
	}

	private final static double SIZE_SCALE = 0.01;


	//Mostly vanilla code
	//TODO: make usable on other planets
	public RenderSpaceTravelSky() {
		axis = new Vector3F<Float>(1f, 0f, 0f);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		starGLCallList = new VertexBuffer(DefaultVertexFormats.POSITION);
		this.renderStars(buffer);
		starGLCallList.upload(buffer);


		buffer = Tessellator.getInstance().getBuffer();
		this.glSkyList = new VertexBuffer(DefaultVertexFormats.POSITION);
		byte b2 = 64;
		int i = 256 / b2 + 2;
		float f = 16.0F;
		int j;
		int k;

		for (j = -b2 * i; j <= b2 * i; j += b2)
		{
			for (k = -b2 * i; k <= b2 * i; k += b2)
			{
				buffer.pos((double)(j + 0), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + b2)).endVertex();
				buffer.pos((double)(j + 0), (double)f, (double)(k + b2)).endVertex();

			}
		}
		buffer.finishDrawing();
		glSkyList.upload(buffer);

		buffer = Tessellator.getInstance().getBuffer();
		this.glSkyList2 = new VertexBuffer(DefaultVertexFormats.POSITION);
		f = -16.0F;

		for (j = -b2 * i; j <= b2 * i; j += b2)
		{
			for (k = -b2 * i; k <= b2 * i; k += b2)
			{
				buffer.pos((double)(j + 0), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + b2)).endVertex();
				buffer.pos((double)(j + 0), (double)f, (double)(k + b2)).endVertex();
			}
		}

		buffer.finishDrawing();
		glSkyList2.upload(buffer);
	}

	Minecraft mc = Minecraft.getInstance();


	public void renderPlanet(IDimensionProperties properties, MatrixStack matrix, SpacePosition position, SpacePosition playerPosition, float sizeOverride) {

		matrix.push();
		matrix.translate((float)position.x, (float)position.y, (float)position.z);
		//Max because moon was too small to be visible


		matrix.scale(sizeOverride, sizeOverride, sizeOverride);
		RenderSystem.disableLighting();
		RenderSystem.depthMask(true);
		Minecraft.getInstance().getTextureManager().bindTexture(properties.getPlanetIconLEO());
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.alphaFunc(GL11.GL_GREATER, 0);

		RenderSystem.color4f(1f, 1, 1f, 1f);
		matrix.push();
		sphere.renderAll(matrix);
		matrix.pop();
		RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA);

		//Render shadow
		matrix.push();
		matrix.scale(1.1f, 1.1f, 1.1f);
		matrix.rotate(new Quaternion( 0, 0, 90, true));
		matrix.rotate(new Quaternion((float)-(properties.getOrbitTheta() * 180/Math.PI), 0, 0, true));
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.shadow3);
		RenderSystem.color4f(.1f, .1f, .1f,0.75f);
		sphere.renderAll(matrix);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		if(properties.hasRings()) {
			//Rotate for rings
			matrix.rotate(new Quaternion( 90, 0, 0, true));
			matrix.rotate(new Quaternion( 0, 0, -90, true));

			//Draw ring
			float[] ringColor = properties.getRingColor();
			RenderSystem.color4f(ringColor[0], ringColor[1], ringColor[2],0.5f);
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRings);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			//RenderHelper.renderTopFaceWithUV(matrix, buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			RenderHelper.renderBottomFaceWithUV(matrix, buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1, 1,1,1,1);
			Tessellator.getInstance().draw();

			//Draw ring shadow
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRingShadow);
			RenderSystem.color4f(1,1,1,0.5f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			RenderHelper.renderTopFaceWithUV(matrix, buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1, 1,1,1,1);
			RenderHelper.renderBottomFaceWithUV(matrix, buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1, 1,1,1,1);
			Tessellator.getInstance().draw();
		}

		matrix.pop();

		//Render ATM
		if(properties.hasAtmosphere() ) {
			matrix.push();
			matrix.rotate(new Quaternion( 0, 0, 90, true));
			matrix.rotate(new Quaternion((float)-(properties.getOrbitTheta() * 180/Math.PI), 0, 0, true));
			matrix.scale(1.1f, 1.1f, 1.1f);
			RenderSystem.disableTexture();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float[] skyColor = properties.getSkyColor();
			RenderSystem.color4f(skyColor[0], skyColor[1], skyColor[2], .04f);

			for(int i = 0; i < 10; i++) {
				matrix.scale(1.01f, 1.01f, 1.01f);
				sphere.renderAll(matrix);
			}

			RenderSystem.enableTexture();
			matrix.pop();
		}

		RenderSystem.depthMask(true);
		matrix.pop();

		//Clean up and make player not transparent
		RenderSystem.color3f(1, 1, 1);
		RenderSystem.disableBlend();
		RenderSystem.enableLighting();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private double getDistance(SpacePosition a, SpacePosition b)
	{
		double deltaX = a.x -b.x;
		double deltaY = a.y - b.y;
		double deltaZ = a.z - b.z;
		return Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
	}


	public void renderStar(StellarBody entity, MatrixStack matrix, SpacePosition position, SpacePosition playerSpacePosition) {

		StellarBody body = entity;
		if(body == null)
			return;

		if(entity.isBlackHole())
		{
			float sunSize = entity.getDisplayRadius()/50f;
			matrix.push();
			matrix.translate(position.x,position.y,position.z);

			Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.locationBlackHole);

			BufferBuilder buffer = Tessellator.getInstance().getBuffer();

			RenderSystem.depthMask(true);
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.alphaFunc(GL11.GL_GREATER, 0.01f);
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
			RenderSystem.color4f((float)1, (float).5 , (float).4 ,1f);

			matrix.push();
			//matrix.translate(0, 100, 0);

			float f10 = sunSize*5f;

			float phase = -(System.currentTimeMillis() % 3600)/3600f;
			float scale = 1+(float)Math.sin(phase*3.14)*0.1f;
			scale *= 0.1;
			phase*=360f;
			matrix.rotate(new Quaternion(0, phase, 0, true));
			GL11.glScaled(scale,scale,scale);

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			RenderHelper.renderNorthFaceWithUV(matrix, buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1, 1,1,1,1);
			RenderHelper.renderEastFaceWithUV(matrix, buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1, 1,1,1,1);
			RenderHelper.renderTopFaceWithUV(matrix, buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1, 1,1,1,1);
			Tessellator.getInstance().draw();
			matrix.pop();

			//Render accretion disk
			mc.getTextureManager().bindTexture(TextureResources.locationAccretionDisk);
			RenderSystem.depthMask(false);
			for(int i = 0; i < 3; i++)
			{
				float speedMult = (i)*1.01f + 1;
				matrix.push();
				//matrix.translate(0, 100, 0);
				matrix.rotate(new Quaternion(-80, 80, 0, true));
				matrix.rotate(new Quaternion(0, (System.currentTimeMillis() % (int)(speedMult*36000))/(100f*speedMult),0, true));

				RenderSystem.color4f((float)1, (float).5 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*40f;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0f, 1.0f).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				matrix.translate(0, -0.1f, 0);
				matrix.rotate(new Quaternion (-80, 80, 0, true));
				matrix.rotate(new Quaternion(0, (System.currentTimeMillis() % (int)(speedMult*360*50))/(50f*speedMult), 0, true));

				RenderSystem.color4f((float)0.8, (float).7 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*30f;
				//multiplier = 2;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0f, 1.0f).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				matrix.translate(0, -0.2f, 0);
				matrix.rotate(new Quaternion (-80, 80, 0, true));
				matrix.rotate(new Quaternion(0,(System.currentTimeMillis() % (int)(speedMult*360*25))/(25f*speedMult), 1, 0));

				RenderSystem.color4f((float)0.2, (float).4 , (float)1 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*15f;
				//multiplier = 2;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0f, 1.0f).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();
			}

			matrix.pop();

			RenderSystem.enableDepthTest();

			//Clean up and make player not transparent
			RenderSystem.enableLighting();
			RenderSystem.disableBlend();
			RenderSystem.color4f(1, 1, 1, 1);
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		else
		{

			matrix.push();
			matrix.translate(position.x,position.y,position.z);
			matrix.scale(10, 10, 10);

			//RenderHelper.setupPlayerFacingMatrix(getDistance(playerSpacePosition, position)*getDistance(playerSpacePosition, position), playerSpacePosition.x, playerSpacePosition.y, playerSpacePosition.z);
			Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.locationSunNew);

			BufferBuilder buffer = Tessellator.getInstance().getBuffer();

			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.alphaFunc(GL11.GL_GREATER, 0.01f);
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
			//RenderSystem.color4f((float)1, (float).5 , (float).4 ,1f);

			RenderSystem.color3f(body.getColor()[0], body.getColor()[1], body.getColor()[2]);

			RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			RenderHelper.renderNorthFaceWithUV(matrix, buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1,1,1,1,1);
			RenderHelper.renderEastFaceWithUV(matrix, buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1,1,1,1,1);
			RenderHelper.renderTopFaceWithUV(matrix, buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1,1,1,1,1);
			RenderHelper.renderNorthFaceWithUV(matrix, buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1,1,1,1,1);
			RenderHelper.renderEastFaceWithUV(matrix, buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1,1,1,1,1);
			RenderHelper.renderTopFaceWithUV(matrix, buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1,1,1,1,1);
			Tessellator.getInstance().draw();

			if(true) {
				matrix.push();
				RenderSystem.disableTexture();
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				float[] skyColor = entity.getColor();
				RenderSystem.color4f(skyColor[0]*.02f, skyColor[1]*.02f, skyColor[2]*.02f, 1f);

				matrix.scale(7.1f, 7.1f, 7.1f);
				for(int i = 0; i < 10; i++) {
					matrix.scale(1.05f, 1.05f, 1.05f);
					sphere.renderAll(matrix);
				}

				RenderSystem.enableTexture();
				matrix.pop();
			}

			matrix.pop();

			//Clean up and make player not transparent
			RenderSystem.enableLighting();
			RenderSystem.disableBlend();
			RenderSystem.color4f(1, 1, 1, 1);
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	private void renderStars(BufferBuilder buffer)
	{
		Random random = new Random(10842L);

		for (int i = 0; i < 2000; ++i)
		{
			double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d4 < 1.0D && d4 > 0.01D)
			{
				d4 = 1.0D / Math.sqrt(d4);
				d0 *= d4;
				d1 *= d4;
				d2 *= d4;
				double d5 = d0 * 100.0D;
				double d6 = d1 * 100.0D;
				double d7 = d2 * 100.0D;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d15 = Math.sin(d14);
				double d16 = Math.cos(d14);

				for (int j = 0; j < 4; ++j)
				{
					double d17 = 0.0D;
					double d18 = (double)((j & 2) - 1) * d3;
					double d19 = (double)((j + 1 & 2) - 1) * d3;
					double d20 = d18 * d16 - d19 * d15;
					double d21 = d19 * d16 + d18 * d15;
					double d22 = d20 * d12 + d17 * d13;
					double d23 = d17 * d12 - d20 * d13;
					double d24 = d23 * d9 - d21 * d10;
					double d25 = d21 * d9 + d23 * d10;
					buffer.pos(d5 + d24, d6 + d22, d7 + d25).endVertex();
				}
			}
		}			

		Tessellator.getInstance().draw();
	}

	private void getAllPlanets(List<IDimensionProperties> allPlanets, IDimensionProperties planet)
	{
		for(ResourceLocation i : planet.getChildPlanets())
		{
			IDimensionProperties childProps = DimensionManager.getInstance().getDimensionProperties(i);
			allPlanets.add(DimensionManager.getInstance().getDimensionProperties(i));
			getAllPlanets(allPlanets, childProps);
		}
	}

	private void buildSolarSystem(SpacePosition playerPosition, MatrixStack matrix)
	{
		List<IDimensionProperties> planets = playerPosition.star.getPlanets();
		// Orbiting a planet
		if(playerPosition.world != null)
		{
			float sizeScale = playerPosition.world.getRenderSizePlanetView();
			//SpacePosition spacePos = property.getSpacePosition();
			SpacePosition newSpacePos = new SpacePosition();

			renderPlanet(playerPosition.world, matrix, newSpacePos, playerPosition, sizeScale);

			//render subplanets
			List<IDimensionProperties> subPlanets = new LinkedList<IDimensionProperties>();
			getAllPlanets(subPlanets, playerPosition.world);

			for(IDimensionProperties subproperty : subPlanets)
			{
				SpacePosition subPlanetPos = subproperty.getSpacePosition();
				sizeScale = subproperty.getRenderSizePlanetView();

				subPlanetPos.x = subPlanetPos.x + newSpacePos.x;
				subPlanetPos.y = subPlanetPos.y + newSpacePos.y;
				subPlanetPos.z = subPlanetPos.z + newSpacePos.z;


				renderPlanet(subproperty, matrix, subPlanetPos, playerPosition, sizeScale);
			}

			subPlanets.add(playerPosition.world);

			// Indicators
			mc.getTextureManager().bindTexture(TextureResources.locationReticle);
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			RenderSystem.color4f(1,0.5f, 0.5f, 1);
			BufferBuilder buffer = Tessellator.getInstance().getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			for(IDimensionProperties subPlanet : subPlanets)
			{
				// indicator hint

				List<ISpaceObject> stations = SpaceObjectManager.getSpaceManager().getSpaceStationsOrbitingPlanet(subPlanet.getId());
				if(stations == null)
					continue;

				for(ISpaceObject property : stations)
				{
					SpacePosition spacePos = ((SpaceStationObject)property).getSpacePosition();
					renderCrossHair(buffer, spacePos, 50);
				}
			}
			Tessellator.getInstance().draw();

			// tags indicator
			matrix.push();
			matrix.translate(0, 3, 0);
			for(IDimensionProperties subPlanet : subPlanets)
			{
				SpacePosition spacePos = subPlanet.getId() == playerPosition.world.getId() ? new SpacePosition() : subPlanet.getSpacePosition();
				float distance = (float)getDistance(playerPosition, spacePos);
				//RenderHelper.renderTag(matrix, distance, String.format("%s : %dkm", subPlanet.getName(), (int)(distance - subPlanet.getRenderSizePlanetView())), spacePos.x, spacePos.y, spacePos.z, 200, distance/10f);


				List<ISpaceObject> stations = SpaceObjectManager.getSpaceManager().getSpaceStationsOrbitingPlanet(subPlanet.getId());
				if(stations == null)
					continue;
				// indicator hint
				for(ISpaceObject property : stations)
				{
					spacePos = ((SpaceStationObject)property).getSpacePosition();
					distance = (float)getDistance(playerPosition, spacePos);
					//RenderHelper.renderTag(distance, String.format("Station - %d : %dkm", property.getId(), (int)distance), spacePos.x, spacePos.y, spacePos.z, 200, distance/10f);
				}
			}
			matrix.pop();
			RenderSystem.enableBlend();


		}
		// Orbiting a star
		else
		{
			for(IDimensionProperties property : planets)
			{
				SpacePosition spacePos = property.getSpacePosition();
				float sizeScale = property.getRenderSizeSolarView();
				double distance = getDistance(playerPosition, spacePos);

				sizeScale = property.isMoon() ? sizeScale*0.2f : sizeScale;
				//sizeScale*=100/(distance*distance);

				renderPlanet(property, matrix, spacePos, playerPosition, sizeScale);
				List<IDimensionProperties> subPlanets = new LinkedList<IDimensionProperties>();
				getAllPlanets(subPlanets, property);

				for(IDimensionProperties subproperty : subPlanets)
				{
					SpacePosition subPlanetPos = subproperty.getSpacePosition();
					sizeScale = subproperty.getRenderSizeSolarView();

					//sizeScale*=10/(distance*distance);

					subPlanetPos.x = subPlanetPos.x/10f + spacePos.x;
					subPlanetPos.y = subPlanetPos.y/10f + spacePos.y;
					subPlanetPos.z = subPlanetPos.z/10f + spacePos.z;


					renderPlanet(subproperty, matrix, subPlanetPos, playerPosition, sizeScale);
				}
			}


			matrix.push();
			float adjustmentScale = 4;
			matrix.scale(adjustmentScale,adjustmentScale,adjustmentScale);
			List<StellarBody> subStars = playerPosition.star.getSubStars();
			StellarBody star = playerPosition.star;
			SpacePosition mainStarPos = star.getSpacePosition();
			renderStar(star, matrix, mainStarPos, playerPosition);


			if(subStars != null && !subStars.isEmpty()) {
				float phaseInc = 360/subStars.size();
				double phase = 0;
				for(StellarBody subStar : subStars) {
					float solarOrbitalDistance = 40*subStar.getStarSeparation();

					SpacePosition subStarSpacePosition = new SpacePosition();

					//Radius to put the player
					double radius = solarOrbitalDistance;
					double theta = phase;
					phase += phaseInc;

					subStarSpacePosition = mainStarPos.getFromSpherical(radius, theta);

					renderStar(subStar, matrix, subStarSpacePosition, playerPosition);
				}
			}
			matrix.pop();


			// Indicators
			mc.getTextureManager().bindTexture(TextureResources.locationReticle);
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			RenderSystem.color3f(0, 1, 0);
			BufferBuilder buffer = Tessellator.getInstance().getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			// indicator hint
			for(IDimensionProperties property : planets)
			{
				SpacePosition spacePos = property.getSpacePosition();
				renderCrossHair(buffer, spacePos, 200);

			}
			Tessellator.getInstance().draw();

			// tags indicator
			matrix.push();
			matrix.translate(0, 3, 0);
			//TODO: tags
			/*for(IDimensionProperties property : planets)
			{
				SpacePosition spacePos = property.getSpacePosition();
				float distance = (float)getDistance(playerPosition, spacePos);

				int displayDist = 100*(int)(distance - property.getRenderSizeSolarView());

				if(displayDist > 1000000)
					RenderHelper.renderTag(distance, String.format("%s : FAR", property.getName()), spacePos.x, spacePos.y, spacePos.z, 200, distance/10f);
				else
					RenderHelper.renderTag(distance, String.format("%s : %dkm", property.getName(), displayDist), spacePos.x, spacePos.y, spacePos.z, 200, distance/10f);
			}*/
			matrix.pop();
			RenderSystem.enableBlend();
		}
	}

	private void renderCrossHair(BufferBuilder buffer, SpacePosition spacePos, float size)
	{
		//TOOD: crosshair
		/*ActiveRenderInfo renderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
		float f = 0;
		float f1 = 1;
		float f2 = 0;
		float f3 = 1;
		float f4 = size; //scale

		float f5 = (float) spacePos.x;
		float f6 = (float) spacePos.y;
		float f7 = (float) spacePos.z;

		float rotationX = renderinfo.getRotation().getRotationX();
		float rotationZ = ActiveRenderInfo.getRotationXZ();
		float rotationYZ = ActiveRenderInfo.getRotationZ();
		float rotationXY = ActiveRenderInfo.getRotationYZ();
		float rotationXZ = ActiveRenderInfo.getRotationXY();


		Vector3d[] avec3d = new Vector3d[] {new Vector3d((double)(-rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(-rotationYZ * f4 - rotationXZ * f4)), new Vector3d((double)(-rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(-rotationYZ * f4 + rotationXZ * f4)), new Vector3d((double)(rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(rotationYZ * f4 + rotationXZ * f4)), new Vector3d((double)(rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(rotationYZ * f4 - rotationXZ * f4))};
		buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex(f1, f3).endVertex();
		buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex(f1, f2).endVertex();
		buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex(f, f2).endVertex();
		buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex(f, f3).endVertex();*/
	}

	private MatrixStack setupSpaceCam(float partialTicks)
	{
		RenderSystem.matrixMode(5889);
		
		RenderSystem.loadIdentity();
		MatrixStack matrix = new MatrixStack();

		double fov = this.mc.gameSettings.fov; // Should really be  this.getFOVModifier(this.mc.gameRenderer.getActiveRenderInfo(), partialTicks, true), but that's private
		matrix.getLast().getMatrix().mul(Matrix4f.perspective(fov, (float)this.mc.getMainWindow().getFramebufferWidth() / (float)this.mc.getMainWindow().getFramebufferHeight(), 0.05F, 1000000));
		RenderSystem.matrixMode(5888);
		return matrix;
	}


	// This actually gets taken care of inside render entity, keeping around in case it's needed later
	private MatrixStack undoSpaceCam(float partialTicks) {
		float farplane = this.mc.gameSettings.renderDistanceChunks * 16; // this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
		
		
		RenderSystem.matrixMode(5889);
		RenderSystem.loadIdentity();
		MatrixStack matrix = new MatrixStack();

		double fov = this.mc.gameSettings.fov; // Should really be  this.getFOVModifier(this.mc.gameRenderer.getActiveRenderInfo(), partialTicks, true), but that's private
		matrix.getLast().getMatrix().mul(Matrix4f.perspective(fov, (float)this.mc.getMainWindow().getFramebufferWidth() / (float)this.mc.getMainWindow().getFramebufferHeight(), 0.05F, farplane * MathHelper.SQRT_2));
		RenderSystem.matrixMode(5888);
		return matrix;
	}

	@Override
	public void render(MatrixStack matrix, float partialTicks) {
		//Get player position first
		Minecraft mc = Minecraft.getInstance();
		setupSpaceCam(partialTicks);
		SpacePosition spacePosition = null;
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if(player == null)
			return;
		Entity ridingEntity = player.getRidingEntity();
		if(ridingEntity instanceof EntityRocket)
		{
			spacePosition = ((EntityRocket)ridingEntity).getSpacePosition();
		}
		else
			return;

		celestialAngle = mc.world.getCelestialAngleRadians(partialTicks);


		RenderSystem.enableTexture();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		matrix.push();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0f);
		matrix.rotate(new Quaternion(0.0F, 90.0F, 0.0F, true));
		matrix.rotate(new Quaternion(-90.0F, 0.0F, 0.0F, true));

		if(spacePosition.world != null)
			matrix.rotate(new Quaternion(0,0, -(float)(spacePosition.world.getOrbitTheta() * 180/Math.PI), true));

		GL11.glScaled(100, 100, 100);

		RenderSystem.disableTexture();
		RenderSystem.disableFog();
		float f18 = 1;
		if (f18 > 0.0F)
		{
			RenderSystem.color4f(f18, f18, f18, f18);
			matrix.push();

			starGLCallList.bindBuffer();
			DefaultVertexFormats.POSITION.setupBufferState(0L);
			starGLCallList.draw(matrix.getLast().getMatrix(), 7);
			VertexBuffer.unbindBuffer();
			DefaultVertexFormats.POSITION.clearBufferState();
			//Extra stars for low ATM
			RenderSystem.color4f(f18, f18, f18, f18/2f);
			matrix.push();
			matrix.rotate(new Quaternion(0, -90, 0, true));
			starGLCallList.bindBuffer();
			DefaultVertexFormats.POSITION.setupBufferState(0L);
			starGLCallList.draw(matrix.getLast().getMatrix(), 7);
			VertexBuffer.unbindBuffer();
			DefaultVertexFormats.POSITION.clearBufferState();
			matrix.pop();

			//Extra extra, read all about it!
			RenderSystem.color4f(f18, f18, f18, f18/4f);
			matrix.push();
			matrix.rotate(new Quaternion(0, 90, 0, true));
			starGLCallList.bindBuffer();
			DefaultVertexFormats.POSITION.setupBufferState(0L);
			starGLCallList.draw(matrix.getLast().getMatrix(), 7);
			VertexBuffer.unbindBuffer();
			DefaultVertexFormats.POSITION.clearBufferState();
			matrix.pop();

			RenderSystem.color4f(f18, f18, f18, f18);
			matrix.pop();
		}

		// If we're orbitting a world, draw the skybox normally
		if(spacePosition.world != null) {

			RenderSystem.enableBlend();
			RenderSystem.enableTexture();
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

			BufferBuilder bufferBuilder =  Tessellator.getInstance().getBuffer();
			DimensionProperties properties = spacePosition.world;
			StellarBody sun = spacePosition.star;
			sun.getSize();
			float[] suncolorfloat = sun.getColor();
			Vector3d color = new Vector3d(suncolorfloat[0], suncolorfloat[1], suncolorfloat[2]);
			drawStarAndSubStars(bufferBuilder, matrix, spacePosition.star, spacePosition.world, properties.getSolarOrbitalDistance(), sun.getSize(), color, 1f);

			RenderSystem.disableBlend();
		}

		matrix.pop();
		matrix.push();

		matrix.translate(-spacePosition.x, -spacePosition.y, -spacePosition.z);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0f);

		RenderSystem.disableCull();
		RenderSystem.enableTexture();
		RenderSystem.disableFog();
		buildSolarSystem(spacePosition, matrix);

		RenderSystem.enableFog();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();

		matrix.pop();

		RenderSystem.enableTexture();
		RenderSystem.depthMask(true);

		RocketEventHandler.onPostWorldRender(partialTicks);
		//Fix player/items going transparent
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		RenderSystem.clear(256, false);
	}

	protected ResourceLocation getTextureForPlanet(DimensionProperties properties) {
		return properties.getPlanetIcon();
	}

	protected ResourceLocation getTextureForPlanetLEO(DimensionProperties properties) {
		return properties.getPlanetIcon();
	}

	protected Direction getRotationAxis(DimensionProperties properties, BlockPos pos) {
		return Direction.EAST;
	}

	protected void renderPlanet(BufferBuilder buffer, MatrixStack matrix, DimensionProperties properties, float planetOrbitalDistance, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, boolean hasRing) {
		renderPlanet2(buffer, matrix, properties, 10f*(200-planetOrbitalDistance)/100f, alphaMultiplier, shadowAngle, hasRing);
	}

	protected void renderPlanet2(BufferBuilder buffer, MatrixStack matrix, DimensionProperties properties, float size, float alphaMultiplier, double shadowAngle, boolean hasRing) {
		ResourceLocation icon = getTextureForPlanet(properties);
		boolean hasAtmosphere = properties.hasAtmosphere();
		boolean gasGiant = properties.isGasGiant();
		boolean hasDecorators = properties.hasDecorators();
		float skyColor[] = properties.skyColor;
		float ringColor[] = properties.skyColor;

		renderPlanetPubHelper(buffer, matrix, icon, 0, 0, -20, size*0.2f, alphaMultiplier, shadowAngle, hasAtmosphere, skyColor, ringColor, gasGiant, hasRing, hasDecorators);
	}

	protected void rotateAroundAxis(MatrixStack matrix) {
		Vector3F<Float> axis = getRotateAxis();
		float x = getSkyRotationAmount() * 360.0F*axis.x ,y = getSkyRotationAmount() * 360.0F*axis.y,z = getSkyRotationAmount() * 360.0F*axis.z;
		matrix.rotate(new Quaternion(x,y,z, true));
	}

	protected float getSkyRotationAmount() {
		return celestialAngle;
	}

	protected Vector3F<Float> getRotateAxis() {
		return axis;
	}

	public static void renderPlanetPubHelper(BufferBuilder buffer, MatrixStack matrix, ResourceLocation icon, int locationX, int locationY, double zLevel, float size, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing, boolean hasDecorators) {
		RenderSystem.enableBlend();

		//int k = mc.theWorld.getMoonPhase();
		//int l = k % 4;
		//int i1 = k / 4 % 2;

		//Set planet Orbiting distance; size
		float f10 = size;

		float f14 = 1f;//(float)(l + 0) / 4.0F;
		float f15 = 0f;//(float)(i1 + 0) / 2.0F;
		float f16 = f15;//(float)(l + 1) / 4.0F;
		float f17 = f14;//(float)(i1 + 1) / 2.0F;


		matrix.push();
		matrix.translate(locationX, zLevel, locationY);

		if (hasDecorators) {
			//ATM Glow
			matrix.push();
			GL11.glRotated(90-shadowAngle* 180/Math.PI, 0, 1, 0);

			//Rings
			if(hasRing) {
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				RenderSystem.color4f(ringColor[0], ringColor[1], ringColor[2], alphaMultiplier*0.2f);
				float ringSize = f10 *1.4f;
				Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRings);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

				buffer.pos(-ringSize, zLevel-0.01f, ringSize).tex(f16, f17).endVertex();
				buffer.pos(ringSize, zLevel-0.01f, ringSize).tex(f14, f17).endVertex();
				buffer.pos(ringSize, zLevel-0.01f, -ringSize).tex(f14, f15).endVertex();
				buffer.pos(-ringSize, zLevel-0.01f, -ringSize).tex(f16, f15).endVertex();
				Tessellator.getInstance().draw();

				RenderSystem.color4f(0f, 0f, 0f, alphaMultiplier);
				Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRingShadow);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buffer.pos(-ringSize, zLevel-0.01f, ringSize).tex(f16, f17).endVertex();
				buffer.pos(ringSize, zLevel-0.01f, ringSize).tex(f14, f17).endVertex();
				buffer.pos(ringSize, zLevel-0.01f, -ringSize).tex(f14, f15).endVertex();
				buffer.pos(-ringSize, zLevel-0.01f, -ringSize).tex(f16, f15).endVertex();
				Tessellator.getInstance().draw();
			}

			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.atmGlow);

			RenderSystem.color4f(1f, 1f, 1f, alphaMultiplier);
			buffer.pos(-f10, zLevel+0.01f, f10).tex(f16, f17).endVertex();
			buffer.pos(f10, zLevel+0.01f, f10).tex(f14, f17).endVertex();
			buffer.pos(f10, zLevel+0.01f, -f10).tex(f14, f15).endVertex();
			buffer.pos(-f10, zLevel+0.01f, -f10).tex(f16, f15).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();
		}

		//End ATM glow

		Minecraft.getInstance().getTextureManager().bindTexture(icon);
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//TODO: draw sky planets

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		RenderSystem.color4f(1f, 1f, 1f, alphaMultiplier);
		buffer.pos(-f10, zLevel, f10).tex(f16, f17).endVertex();
		buffer.pos(f10, zLevel, f10).tex(f14, f17).endVertex();
		buffer.pos(f10, zLevel, -f10).tex(f14, f15).endVertex();
		buffer.pos(-f10, zLevel, -f10).tex(f16, f15).endVertex();
		Tessellator.getInstance().draw();
		//buffer.finishDrawing();

		//GL11.glEnable(GL11.GL_BLEND);

		if (hasDecorators) {
			//Draw atmosphere if applicable
			if(hasAtmosphere) {
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.getAtmosphereResource());
				RenderSystem.color4f(skyColor[0], skyColor[1], skyColor[2], alphaMultiplier);
				buffer.pos(-f10, zLevel, f10).tex(f16, f17).endVertex();
				buffer.pos(f10, zLevel, f10).tex(f14, f17).endVertex();
				buffer.pos(f10, zLevel, -f10).tex(f14, f15).endVertex();
				buffer.pos(-f10, zLevel, -f10).tex(f16, f15).endVertex();
				Tessellator.getInstance().draw();
				//buffer.finishDrawing();

			}


			GL11.glRotated(90-shadowAngle* 180/Math.PI, 0, 1, 0);

			//Draw Shadow
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.getShadowResource());
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderSystem.color4f(1f, 1f, 1f, alphaMultiplier);
			buffer.pos(-f10, zLevel-0.01f, f10).tex(f16, f17).endVertex();
			buffer.pos(f10, zLevel-0.01f, f10).tex(f14, f17).endVertex();
			buffer.pos(f10, zLevel-0.01f, -f10).tex(f14, f15).endVertex();
			buffer.pos(-f10, zLevel-0.01f, -f10).tex(f16, f15).endVertex();
			Tessellator.getInstance().draw();
		}

		matrix.pop();


		RenderSystem.color4f(1f, 1f, 1f, 1f);
	}

	protected void drawStar(BufferBuilder buffer, MatrixStack matrix, StellarBody sun, DimensionProperties properties, int solarOrbitalDistance, float sunSize, Vector3d sunColor, float multiplier) {
		if(sun != null && sun.isBlackHole()) {
			RenderSystem.depthMask(true);
			RenderSystem.enableAlphaTest();
			RenderSystem.alphaFunc(GL11.GL_GREATER, 0.01f);
			mc.getTextureManager().bindTexture(TextureResources.locationBlackHole);
			matrix.push();
			matrix.translate(0, 100, 0);
			float phase = -(System.currentTimeMillis() % 3600)/3600f;
			float scale = 1+(float)Math.sin(phase*3.14)*0.1f;
			phase*=360f;
			matrix.rotate(new Quaternion(0, phase, 0, true));

			GL11.glScaled(scale,scale,scale);

			//Set sun color and distance
			RenderSystem.color4f((float)1, (float).5 , (float).4 ,1f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			float f10 = sunSize*5f*(202-solarOrbitalDistance)/100f;
			//multiplier = 2;
			buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
			buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
			buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0f, 1.0f).endVertex();
			buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0f, 1.0f).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();


			//Render accretion disk
			mc.getTextureManager().bindTexture(TextureResources.locationAccretionDisk);
			RenderSystem.depthMask(false);
			for(int i = 0; i < 3; i++)
			{
				float speedMult = (i)*1.01f + 1;
				matrix.push();
				matrix.translate(0, 100, 0);
				matrix.rotate(new Quaternion (-80, 80, 0, true));
				matrix.rotate(new Quaternion (0, (System.currentTimeMillis() % (int)(speedMult*36000))/(100f*speedMult), 0, true));

				RenderSystem.color4f((float)1, (float).5 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*40f*(202-solarOrbitalDistance)/100f;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0f, 1.0f).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				matrix.translate(0, 99.99f, 0);
				matrix.rotate(new Quaternion (-80, 80, 0, true));
				matrix.rotate(new Quaternion (0, (System.currentTimeMillis() % (int)(speedMult*360*50))/(50f*speedMult), 0, true));


				RenderSystem.color4f((float)0.8, (float).7 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*30f*(202-solarOrbitalDistance)/100f;
				//multiplier = 2;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0f, 1.0f).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				matrix.translate(0, 99.98f, 0);
				matrix.rotate(new Quaternion (-80, 80, 0, true));
				matrix.rotate(new Quaternion (0, (System.currentTimeMillis() % (int)(speedMult*360*25))/(25f*speedMult), 0, true));

				RenderSystem.color4f((float)0.2, (float).4 , (float)1 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*15f*(202-solarOrbitalDistance)/100f;
				//multiplier = 2;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0f, 1.0f).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();
			}

			RenderSystem.depthMask(true);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			RenderSystem.depthMask(false);

		}
		else {
			mc.getTextureManager().bindTexture(TextureResources.locationSunPng);
			//Set sun color and distance
			RenderSystem.color4f((float)sunColor.x, (float)sunColor.y , (float)sunColor.z ,Math.min((multiplier)*2f,1f));
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			float f10 = sunSize*30f*(202-solarOrbitalDistance)/100f;
			//multiplier = 2;
			buffer.pos((double)(-f10), 100.0D, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
			buffer.pos((double)f10, 100.0D, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
			buffer.pos((double)f10, 100.0D, (double)f10).tex(1.0f, 1.0f).endVertex();
			buffer.pos((double)(-f10), 100.0D, (double)f10).tex(0.0f, 1.0f).endVertex();
			Tessellator.getInstance().draw();
		}
	}
}
