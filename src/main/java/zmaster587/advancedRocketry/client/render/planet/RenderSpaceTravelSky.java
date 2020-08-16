package zmaster587.advancedRocketry.client.render.planet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vector3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

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


	private int starGLCallList;
	private int glSkyList;
	private int glSkyList2;
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

		this.starGLCallList = GLAllocation.generateDisplayLists(3);
		matrix.push();
		GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		matrix.pop();
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		this.glSkyList = this.starGLCallList + 1;
		GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
		byte b2 = 64;
		int i = 256 / b2 + 2;
		float f = 16.0F;
		int j;
		int k;

		for (j = -b2 * i; j <= b2 * i; j += b2)
		{
			for (k = -b2 * i; k <= b2 * i; k += b2)
			{
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
				buffer.pos((double)(j + 0), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + 0)).endVertex();
				buffer.pos((double)(j + b2), (double)f, (double)(k + b2)).endVertex();
				buffer.pos((double)(j + 0), (double)f, (double)(k + b2)).endVertex();
				Tessellator.getInstance().draw();
			}
		}

		GL11.glEndList();
		this.glSkyList2 = this.starGLCallList + 2;
		GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
		f = -16.0F;
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

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

		Tessellator.getInstance().draw();
		GL11.glEndList();
	}

	Minecraft mc = Minecraft.getInstance();


	public void renderPlanet(IDimensionProperties properties, SpacePosition position, SpacePosition playerPosition, float sizeOverride) {

		GlStateManager.pushMatrix();
		GlStateManager.translate((float)position.x, (float)position.y, (float)position.z);
		//Max because moon was too small to be visible


		GlStateManager.scale(sizeOverride, sizeOverride, sizeOverride);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(true);
		Minecraft.getInstance().getTextureManager().bindTexture(properties.getPlanetIconLEO());
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);

		GlStateManager.color4f(1f, 1, 1f, 1f);
		GlStateManager.pushMatrix();
		sphere.renderAll();
		GlStateManager.popMatrix();
		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_SRC_ALPHA);

		//Render shadow
		GlStateManager.pushMatrix();
		GlStateManager.scale(1.1f, 1.1f, 1.1f);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate( (float)-(properties.getOrbitTheta() * 180/Math.PI), 1, 0, 0);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.shadow3);
		GlStateManager.color4f(.1f, .1f, .1f,0.75f);
		sphere.renderAll();

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		if(properties.hasRings()) {
			//Rotate for rings
			GlStateManager.rotate(90, 1, 0, 0);
			GlStateManager.rotate(-90, 0, 0, 1);

			//Draw ring
			float[] ringColor = properties.getRingColor();
			GlStateManager.color4f(ringColor[0], ringColor[1], ringColor[2],0.5f);
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRings);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			//RenderHelper.renderTopFaceWithUV(buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			RenderHelper.renderBottomFaceWithUV(buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			Tessellator.getInstance().draw();

			//Draw ring shadow
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRingShadow);
			GlStateManager.color4f(1,1,1,0.5f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderTopFaceWithUV(buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			RenderHelper.renderBottomFaceWithUV(buffer, 0, -1, -1, 1, 1, 0, 1, 0, 1);
			Tessellator.getInstance().draw();
		}

		matrix.pop();

		//Render ATM
		if(properties.hasAtmosphere() ) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90, 0, 0, 1);
			GlStateManager.rotate( (float)-(properties.getOrbitTheta() * 180/Math.PI), 1, 0, 0);
			GlStateManager.scale(1.1f, 1.1f, 1.1f);
			GlStateManager.disableTexture();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float[] skyColor = properties.getSkyColor();
			GlStateManager.color4f(skyColor[0], skyColor[1], skyColor[2], .04f);

			for(int i = 0; i < 10; i++) {
				GlStateManager.scale(1.01f, 1.01f, 1.01f);
				sphere.renderAll();
			}

			GlStateManager.enableTexture();
			GlStateManager.popMatrix();
		}

		GlStateManager.depthMask(true);
		GlStateManager.popMatrix();

		//Clean up and make player not transparent
		GlStateManager.color4f(1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private double getDistance(SpacePosition a, SpacePosition b)
	{
		double deltaX = a.x -b.x;
		double deltaY = a.y - b.y;
		double deltaZ = a.z - b.z;
		return Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
	}


	public void renderStar(StellarBody entity, SpacePosition position, SpacePosition playerSpacePosition) {

		StellarBody body = entity;
		if(body == null)
			return;

		if(entity.isBlackHole())
		{
			float sunSize = entity.getDisplayRadius()/50f;
			GlStateManager.pushMatrix();
			GlStateManager.translate(position.x,position.y,position.z);

			Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.locationBlackHole);

			BufferBuilder buffer = Tessellator.getInstance().getBuffer();

			GlStateManager.depthMask(true);
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
			GlStateManager.color4f((float)1, (float).5 , (float).4 ,1f);

			GlStateManager.pushMatrix();
			//GL11.glTranslatef(0, 100, 0);

			float f10 = sunSize*5f;

			float phase = -(System.currentTimeMillis() % 3600)/3600f;
			float scale = 1+(float)Math.sin(phase*3.14)*0.1f;
			scale *= 0.1;
			phase*=360f;
			GL11.glRotatef(phase, 0, 1, 0);
			GL11.glScaled(scale,scale,scale);

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
			RenderHelper.renderEastFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
			RenderHelper.renderTopFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
			Tessellator.getInstance().draw();
			GlStateManager.popMatrix();

			//Render accretion disk
			mc.renderEngine.bindTexture(TextureResources.locationAccretionDisk);
			GlStateManager.depthMask(false);
			for(int i = 0; i < 3; i++)
			{
				float speedMult = (i)*1.01f + 1;
				matrix.push();
				//GL11.glTranslatef(0, 100, 0);
				GL11.glRotatef(80, -1, 1, 0);
				GL11.glRotatef((System.currentTimeMillis() % (int)(speedMult*36000))/(100f*speedMult), 0, 1, 0);

				GlStateManager.color4f((float)1, (float).5 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*40f;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0D, 1.0D).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				GL11.glTranslatef(0, -0.1f, 0);
				GL11.glRotatef(80, -1, 1, 0);
				GL11.glRotatef((System.currentTimeMillis() % (int)(speedMult*360*50))/(50f*speedMult), 0, 1, 0);

				GlStateManager.color4f((float)0.8, (float).7 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*30f;
				//multiplier = 2;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0D, 1.0D).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				GL11.glTranslatef(0, -0.2f, 0);
				GL11.glRotatef(80, -1, 1, 0);
				GL11.glRotatef((System.currentTimeMillis() % (int)(speedMult*360*25))/(25f*speedMult), 0, 1, 0);

				GlStateManager.color4f((float)0.2, (float).4 , (float)1 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*15f;
				//multiplier = 2;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0D, 1.0D).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();
			}

			GlStateManager.popMatrix();

			GlStateManager.enableDepth();

			//Clean up and make player not transparent
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color4f(1, 1, 1, 1);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		else
		{

			GlStateManager.pushMatrix();
			GlStateManager.translate(position.x,position.y,position.z);
			GlStateManager.scale(10, 10, 10);

			//RenderHelper.setupPlayerFacingMatrix(getDistance(playerSpacePosition, position)*getDistance(playerSpacePosition, position), playerSpacePosition.x, playerSpacePosition.y, playerSpacePosition.z);
			Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.locationSunNew);

			BufferBuilder buffer = Tessellator.getInstance().getBuffer();

			GlStateManager.depthMask(false);
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
			//GlStateManager.color4f((float)1, (float).5 , (float).4 ,1f);

			GlStateManager.color4f(body.getColor()[0], body.getColor()[1], body.getColor()[2]);

			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
			RenderHelper.renderEastFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
			RenderHelper.renderTopFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
			RenderHelper.renderNorthFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
			RenderHelper.renderEastFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
			RenderHelper.renderTopFaceWithUV(buffer, 0, -5, -5, 5, 5, 0, 1, 0, 1);
			Tessellator.getInstance().draw();

			if(true) {
				GlStateManager.pushMatrix();
				GlStateManager.disableTexture();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				float[] skyColor = entity.getColor();
				GlStateManager.color4f(skyColor[0]*.02f, skyColor[1]*.02f, skyColor[2]*.02f, 1f);

				GlStateManager.scale(7.1f, 7.1f, 7.1f);
				for(int i = 0; i < 10; i++) {
					GlStateManager.scale(1.05f, 1.05f, 1.05f);
					sphere.renderAll();
				}

				GlStateManager.enableTexture();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();

			//Clean up and make player not transparent
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color4f(1, 1, 1, 1);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	private void renderStars()
	{
		Random random = new Random(10842L);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

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
		for(int i : planet.getChildPlanets())
		{
			IDimensionProperties childProps = DimensionManager.getInstance().getDimensionProperties(i);
			allPlanets.add(DimensionManager.getInstance().getDimensionProperties(i));
			getAllPlanets(allPlanets, childProps);
		}
	}

	private void buildSolarSystem(SpacePosition playerPosition)
	{
		List<IDimensionProperties> planets = playerPosition.star.getPlanets();
		// Orbiting a planet
		if(playerPosition.world != null)
		{
			float sizeScale = playerPosition.world.getRenderSizePlanetView();
			//SpacePosition spacePos = property.getSpacePosition();
			SpacePosition newSpacePos = new SpacePosition();

			renderPlanet(playerPosition.world, newSpacePos, playerPosition, sizeScale);

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


				renderPlanet(subproperty, subPlanetPos, playerPosition, sizeScale);
			}

			subPlanets.add(playerPosition.world);

			// Indicators
			mc.renderEngine.bindTexture(TextureResources.locationReticle);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.color4f(1,0.5f, 0.5f, 1);
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
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 3, 0);
			for(IDimensionProperties subPlanet : subPlanets)
			{
				SpacePosition spacePos = subPlanet.getId() == playerPosition.world.getId() ? new SpacePosition() : subPlanet.getSpacePosition();
				float distance = (float)getDistance(playerPosition, spacePos);
				RenderHelper.renderTag(distance, String.format("%s : %dkm", subPlanet.getName(), (int)(distance - subPlanet.getRenderSizePlanetView())), spacePos.x, spacePos.y, spacePos.z, 200, distance/10f);
				
				
				List<ISpaceObject> stations = SpaceObjectManager.getSpaceManager().getSpaceStationsOrbitingPlanet(subPlanet.getId());
				if(stations == null)
					continue;
				// indicator hint
				for(ISpaceObject property : stations)
				{
					spacePos = ((SpaceStationObject)property).getSpacePosition();
					distance = (float)getDistance(playerPosition, spacePos);
					RenderHelper.renderTag(distance, String.format("Station - %d : %dkm", property.getId(), (int)distance), spacePos.x, spacePos.y, spacePos.z, 200, distance/10f);
				}
			}
			GlStateManager.popMatrix();
			GlStateManager.enableBlend();


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

				renderPlanet(property, spacePos, playerPosition, sizeScale);
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


					renderPlanet(subproperty, subPlanetPos, playerPosition, sizeScale);
				}
			}


			GlStateManager.pushMatrix();
			float adjustmentScale = 4;
			GlStateManager.scale(adjustmentScale,adjustmentScale,adjustmentScale);
			List<StellarBody> subStars = playerPosition.star.getSubStars();
			StellarBody star = playerPosition.star;
			SpacePosition mainStarPos = star.getSpacePosition();
			renderStar(star, mainStarPos, playerPosition);


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

					renderStar(subStar, subStarSpacePosition, playerPosition);
				}
			}
			GlStateManager.popMatrix();


			// Indicators
			mc.renderEngine.bindTexture(TextureResources.locationReticle);
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GlStateManager.color4f(0, 1, 0);
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
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 3, 0);
			for(IDimensionProperties property : planets)
			{
				SpacePosition spacePos = property.getSpacePosition();
				float distance = (float)getDistance(playerPosition, spacePos);
				
				int displayDist = 100*(int)(distance - property.getRenderSizeSolarView());
				
				if(displayDist > 1000000)
					RenderHelper.renderTag(distance, String.format("%s : FAR", property.getName()), spacePos.x, spacePos.y, spacePos.z, 200, distance/10f);
				else
					RenderHelper.renderTag(distance, String.format("%s : %dkm", property.getName(), displayDist), spacePos.x, spacePos.y, spacePos.z, 200, distance/10f);
			}
			GlStateManager.popMatrix();
			GlStateManager.enableBlend();
		}
	}

	private void renderCrossHair(BufferBuilder buffer, SpacePosition spacePos, float size)
	{
		float f = 0;
		float f1 = 1;
		float f2 = 0;
		float f3 = 1;
		float f4 = size; //scale

		float f5 = (float) spacePos.x;
		float f6 = (float) spacePos.y;
		float f7 = (float) spacePos.z;

		float rotationX = ActiveRenderInfo.getRotationX();
		float rotationZ = ActiveRenderInfo.getRotationXZ();
		float rotationYZ = ActiveRenderInfo.getRotationZ();
		float rotationXY = ActiveRenderInfo.getRotationYZ();
		float rotationXZ = ActiveRenderInfo.getRotationXY();


		Vector3d[] avec3d = new Vector3d[] {new Vector3d((double)(-rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(-rotationYZ * f4 - rotationXZ * f4)), new Vector3d((double)(-rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(-rotationYZ * f4 + rotationXZ * f4)), new Vector3d((double)(rotationX * f4 + rotationXY * f4), (double)(rotationZ * f4), (double)(rotationYZ * f4 + rotationXZ * f4)), new Vector3d((double)(rotationX * f4 - rotationXY * f4), (double)(-rotationZ * f4), (double)(rotationYZ * f4 - rotationXZ * f4))};
		buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f1, (double)f3).endVertex();
		buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f1, (double)f2).endVertex();
		buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f, (double)f2).endVertex();
		buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f, (double)f3).endVertex();
	}

	private void setupSpaceCam(float partialTicks)
	{
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();

		float fov = this.mc.gameSettings.fovSetting; // Should really be  this.getFOVModifier(partialTicks, true), but that's private
		Project.gluPerspective(fov, (float)this.mc.displayWidth / (float)this.mc.displayHeight, 10F, 1000000);
		GlStateManager.matrixMode(5888);
	}


	// This actually gets taken care of inside render entity, keeping around in case it's needed later
	private void undoSpaceCam(float partialTicks) {
		float fov = this.mc.gameSettings.fovSetting; // Should really be  this.getFOVModifier(partialTicks, true), but that's private
		float farplane = this.mc.gameSettings.renderDistanceChunks * 16; // this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
		Project.gluPerspective(fov, (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, farplane * MathHelper.SQRT_2);
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		//Get player position first
		setupSpaceCam(partialTicks);
		SpacePosition spacePosition = null;
		EntityPlayerSP player = Minecraft.getInstance().player;
		if(player == null)
			return;
		Entity ridingEntity = player.getRidingEntity();
		if(ridingEntity instanceof EntityRocket)
		{
			spacePosition = ((EntityRocket)ridingEntity).getSpacePosition();
		}
		else
			return;

		celestialAngle = mc.world.getCelestialAngle(partialTicks);


		GlStateManager.enableTexture();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		matrix.push();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0f);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);

		if(spacePosition.world != null)
			GlStateManager.rotate( -(float)(spacePosition.world.getOrbitTheta() * 180/Math.PI), 0, 0, 1);

		GL11.glScaled(100, 100, 100);
		
		GlStateManager.disableTexture();
		GlStateManager.disableFog();
		float f18 = 1;
		if (f18 > 0.0F)
		{
			GlStateManager.color4f(f18, f18, f18, f18);
			matrix.push();

			GL11.glCallList(this.starGLCallList);
			//Extra stars for low ATM
			GlStateManager.color4f(f18, f18, f18, f18/2f);
			matrix.push();
			GL11.glRotatef(-90, 0, 1, 0);
			GL11.glCallList(this.starGLCallList);
			matrix.pop();

			//Extra extra, read all about it!
			GlStateManager.color4f(f18, f18, f18, f18/4f);
			matrix.push();
			GL11.glRotatef(90, 0, 1, 0);
			GL11.glCallList(this.starGLCallList);
			matrix.pop();

			GlStateManager.color4f(f18, f18, f18, f18);
			matrix.pop();
		}

		// If we're orbitting a world, draw the skybox normally
		if(spacePosition.world != null) {

			GlStateManager.enableBlend();
			GlStateManager.enableTexture();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

			BufferBuilder bufferBuilder =  Tessellator.getInstance().getBuffer();
			DimensionProperties properties = spacePosition.world;
			StellarBody sun = spacePosition.star;
			sun.getSize();
			float[] suncolorfloat = sun.getColor();
			Vector3d color = new Vector3d(suncolorfloat[0], suncolorfloat[1], suncolorfloat[2]);
			drawStarAndSubStars(bufferBuilder, spacePosition.star, spacePosition.world, properties.getSolarOrbitalDistance(), sun.getSize(), color, 1f);

			GlStateManager.disableBlend();
		}

		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();

		matrix.translate(-spacePosition.x, -spacePosition.y, -spacePosition.z);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0f);

		GlStateManager.disableCull();
		GlStateManager.enableTexture();
		GlStateManager.disableFog();
		buildSolarSystem(spacePosition);

		GlStateManager.enableFog();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();

		matrix.pop();

		GlStateManager.enableTexture();
		GlStateManager.depthMask(true);

		RocketEventHandler.onPostWorldRender(partialTicks);
		//Fix player/items going transparent
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.clear(256);
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

	protected void renderPlanet(BufferBuilder buffer, DimensionProperties properties, float planetOrbitalDistance, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, boolean hasRing) {
		renderPlanet2(buffer, properties, 10f*(200-planetOrbitalDistance)/100f, alphaMultiplier, shadowAngle, hasRing);
	}

	protected void renderPlanet2(BufferBuilder buffer, DimensionProperties properties, float size, float alphaMultiplier, double shadowAngle, boolean hasRing) {
		ResourceLocation icon = getTextureForPlanet(properties);
		boolean hasAtmosphere = properties.hasAtmosphere();
		boolean gasGiant = properties.isGasGiant();
		boolean hasDecorators = properties.hasDecorators();
		float skyColor[] = properties.skyColor;
		float ringColor[] = properties.skyColor;

		renderPlanetPubHelper(buffer, icon, 0, 0, -20, size*0.2f, alphaMultiplier, shadowAngle, hasAtmosphere, skyColor, ringColor, gasGiant, hasRing, hasDecorators);
	}

	protected void rotateAroundAxis() {
		Vector3F<Float> axis = getRotateAxis();
		GL11.glRotatef(getSkyRotationAmount() * 360.0F, axis.x, axis.y, axis.z);
	}

	protected float getSkyRotationAmount() {
		return celestialAngle;
	}

	protected Vector3F<Float> getRotateAxis() {
		return axis;
	}

	public static void renderPlanetPubHelper(BufferBuilder buffer, ResourceLocation icon, int locationX, int locationY, double zLevel, float size, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing, boolean hasDecorators) {
		GlStateManager.enableBlend();

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
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.color4f(ringColor[0], ringColor[1], ringColor[2], alphaMultiplier*0.2f);
				float ringSize = f10 *1.4f;
				Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRings);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

				buffer.pos(-ringSize, zLevel-0.01f, ringSize).tex((double)f16, (double)f17).endVertex();
				buffer.pos(ringSize, zLevel-0.01f, ringSize).tex((double)f14, (double)f17).endVertex();
				buffer.pos(ringSize, zLevel-0.01f, -ringSize).tex((double)f14, (double)f15).endVertex();
				buffer.pos(-ringSize, zLevel-0.01f, -ringSize).tex((double)f16, (double)f15).endVertex();
				Tessellator.getInstance().draw();

				GlStateManager.color4f(0f, 0f, 0f, alphaMultiplier);
				Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRingShadow);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buffer.pos(-ringSize, zLevel-0.01f, ringSize).tex((double)f16, (double)f17).endVertex();
				buffer.pos(ringSize, zLevel-0.01f, ringSize).tex((double)f14, (double)f17).endVertex();
				buffer.pos(ringSize, zLevel-0.01f, -ringSize).tex((double)f14, (double)f15).endVertex();
				buffer.pos(-ringSize, zLevel-0.01f, -ringSize).tex((double)f16, (double)f15).endVertex();
				Tessellator.getInstance().draw();
			}

			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.atmGlow);

			GlStateManager.color4f(1f, 1f, 1f, alphaMultiplier);
			buffer.pos(-f10, zLevel+0.01f, f10).tex((double)f16, (double)f17).endVertex();
			buffer.pos(f10, zLevel+0.01f, f10).tex((double)f14, (double)f17).endVertex();
			buffer.pos(f10, zLevel+0.01f, -f10).tex((double)f14, (double)f15).endVertex();
			buffer.pos(-f10, zLevel+0.01f, -f10).tex((double)f16, (double)f15).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();
		}

		//End ATM glow

		Minecraft.getInstance().getTextureManager().bindTexture(icon);
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//TODO: draw sky planets

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		GlStateManager.color4f(1f, 1f, 1f, alphaMultiplier);
		buffer.pos(-f10, zLevel, f10).tex((double)f16, (double)f17).endVertex();
		buffer.pos(f10, zLevel, f10).tex((double)f14, (double)f17).endVertex();
		buffer.pos(f10, zLevel, -f10).tex((double)f14, (double)f15).endVertex();
		buffer.pos(-f10, zLevel, -f10).tex((double)f16, (double)f15).endVertex();
		Tessellator.getInstance().draw();
		//buffer.finishDrawing();

		//GL11.glEnable(GL11.GL_BLEND);

		if (hasDecorators) {
			//Draw atmosphere if applicable
			if(hasAtmosphere) {
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.getAtmosphereResource());
				GlStateManager.color4f(skyColor[0], skyColor[1], skyColor[2], alphaMultiplier);
				buffer.pos(-f10, zLevel, f10).tex((double)f16, (double)f17).endVertex();
				buffer.pos(f10, zLevel, f10).tex((double)f14, (double)f17).endVertex();
				buffer.pos(f10, zLevel, -f10).tex((double)f14, (double)f15).endVertex();
				buffer.pos(-f10, zLevel, -f10).tex((double)f16, (double)f15).endVertex();
				Tessellator.getInstance().draw();
				//buffer.finishDrawing();

			}


			GL11.glRotated(90-shadowAngle* 180/Math.PI, 0, 1, 0);

			//Draw Shadow
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.getShadowResource());
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.color4f(1f, 1f, 1f, alphaMultiplier);
			buffer.pos(-f10, zLevel-0.01f, f10).tex((double)f16, (double)f17).endVertex();
			buffer.pos(f10, zLevel-0.01f, f10).tex((double)f14, (double)f17).endVertex();
			buffer.pos(f10, zLevel-0.01f, -f10).tex((double)f14, (double)f15).endVertex();
			buffer.pos(-f10, zLevel-0.01f, -f10).tex((double)f16, (double)f15).endVertex();
			Tessellator.getInstance().draw();
		}

		matrix.pop();


		GlStateManager.color4f(1f, 1f, 1f, 1f);
	}

	protected void drawStar(BufferBuilder buffer, StellarBody sun, DimensionProperties properties, int solarOrbitalDistance, float sunSize, Vector3d sunColor, float multiplier) {
		if(sun != null && sun.isBlackHole()) {
			GlStateManager.depthMask(true);
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
			mc.renderEngine.bindTexture(TextureResources.locationBlackHole);

			matrix.push();
			GL11.glTranslatef(0, 100, 0);
			float phase = -(System.currentTimeMillis() % 3600)/3600f;
			float scale = 1+(float)Math.sin(phase*3.14)*0.1f;
			phase*=360f;
			GL11.glRotatef(phase, 0, 1, 0);

			GL11.glScaled(scale,scale,scale);

			//Set sun color and distance
			GlStateManager.color4f((float)1, (float).5 , (float).4 ,1f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			float f10 = sunSize*5f*(202-solarOrbitalDistance)/100f;
			//multiplier = 2;
			buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
			buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
			buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0D, 1.0D).endVertex();
			buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0D, 1.0D).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();


			//Render accretion disk
			mc.renderEngine.bindTexture(TextureResources.locationAccretionDisk);
			GlStateManager.depthMask(false);
			for(int i = 0; i < 3; i++)
			{
				float speedMult = (i)*1.01f + 1;
				matrix.push();
				GL11.glTranslatef(0, 100, 0);
				GL11.glRotatef(80, -1, 1, 0);
				GL11.glRotatef((System.currentTimeMillis() % (int)(speedMult*36000))/(100f*speedMult), 0, 1, 0);

				GlStateManager.color4f((float)1, (float).5 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*40f*(202-solarOrbitalDistance)/100f;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0D, 1.0D).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				GL11.glTranslatef(0, 99.99f, 0);
				GL11.glRotatef(80, -1, 1, 0);
				GL11.glRotatef((System.currentTimeMillis() % (int)(speedMult*360*50))/(50f*speedMult), 0, 1, 0);

				GlStateManager.color4f((float)0.8, (float).7 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*30f*(202-solarOrbitalDistance)/100f;
				//multiplier = 2;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0D, 1.0D).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				GL11.glTranslatef(0, 99.98f, 0);
				GL11.glRotatef(80, -1, 1, 0);
				GL11.glRotatef((System.currentTimeMillis() % (int)(speedMult*360*25))/(25f*speedMult), 0, 1, 0);

				GlStateManager.color4f((float)0.2, (float).4 , (float)1 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = sunSize*15f*(202-solarOrbitalDistance)/100f;
				//multiplier = 2;
				buffer.pos((double)(-f10), 0.0D, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)f10, 0.0D, (double)f10).tex(1.0D, 1.0D).endVertex();
				buffer.pos((double)(-f10), 0.0D, (double)f10).tex(0.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();
			}

			GlStateManager.depthMask(true);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			GlStateManager.depthMask(false);

		}
		else {
			mc.renderEngine.bindTexture(TextureResources.locationSunPng);
			//Set sun color and distance
			GlStateManager.color4f((float)sunColor.x, (float)sunColor.y , (float)sunColor.z ,Math.min((multiplier)*2f,1f));
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			float f10 = sunSize*30f*(202-solarOrbitalDistance)/100f;
			//multiplier = 2;
			buffer.pos((double)(-f10), 100.0D, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
			buffer.pos((double)f10, 100.0D, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
			buffer.pos((double)f10, 100.0D, (double)f10).tex(1.0D, 1.0D).endVertex();
			buffer.pos((double)(-f10), 100.0D, (double)f10).tex(0.0D, 1.0D).endVertex();
			Tessellator.getInstance().draw();
		}
	}
}
