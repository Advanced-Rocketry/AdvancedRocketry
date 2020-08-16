package zmaster587.advancedRocketry.client.render.planet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vector3d;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AstronomicalBodyHelper;
import zmaster587.libVulpes.util.Vector3F;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RenderAsteroidSky extends IRenderHandler {


	private int starGLCallList;
	private int glSkyList;
	private int glSkyList2;
	private int glSkyList3;
	ResourceLocation currentlyBoundTex = null;
	float celestialAngle;
	Vector3F<Float> axis;
	
	public static final  ResourceLocation asteroid1 = new ResourceLocation("advancedRocketry:textures/planets/asteroid_a.png");
	public static final  ResourceLocation asteroid2 = new ResourceLocation("advancedRocketry:textures/planets/asteroid_b.png");
	public static final  ResourceLocation asteroid3 = new ResourceLocation("advancedRocketry:textures/planets/asteroid_c.png");
	
	//Mostly vanilla code
	//TODO: make usable on other planets
	public RenderAsteroidSky() {
		axis = new Vector3F<Float>(1f, 0f, 0f);

		this.starGLCallList = GLAllocation.generateDisplayLists(4);
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
		
		this.glSkyList3 = this.starGLCallList + 3;
		matrix.push();
		GL11.glNewList(this.glSkyList3, GL11.GL_COMPILE);
		renderAsteroids();
		GL11.glEndList();
		matrix.pop();
	}

	Minecraft mc = Minecraft.getInstance();

	
	private void renderAsteroids()
	{
		Random random = new Random(10843L);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		for (int i = 0; i < 200; ++i)
		{
			double d0 = (double)(random.nextFloat()*2F - 1F);
			double d1 = (double)(random.nextFloat()*1F - .5F);
			double d2 = (double)(random.nextFloat()*2F - 1F);
			double size = (double)(0.15F + random.nextFloat());
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d4 < 1.0D && d4 > 0.01D)
			{
				d4 = 0.5D / Math.sqrt(d4);
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
				
				float r,g,b;
				r = random.nextFloat()*0.05f + .95f;
				g = random.nextFloat()*0.1f + .9f;
				b = random.nextFloat()*0.1f + .9f;

				for (int j = 0; j < 4; ++j)
				{
					double d17 = 0.0D;
					double d18 = (double)((j & 2) - 1) * size;
					double d19 = (double)((j + 1 & 2) - 1) * size;
					double d20 = d18 * d16 - d19 * d15;
					double d21 = d19 * d16 + d18 * d15;
					double d22 = d20 * d12 + d17 * d13;
					double d23 = d17 * d12 - d20 * d13;
					double d24 = d23 * d9 - d21 * d10;
					double d25 = d21 * d9 + d23 * d10;
					buffer.pos(d5 + d24, d6 + d22, d7 + d25).tex(d18/(size*2) + .5,  d19/(size*2) +.5 ).color(r, g, b, 1f).endVertex();
				}
			}
		}			

		Tessellator.getInstance().draw();
		//buffer.finishDrawing();
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
		//buffer.finishDrawing();
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		//TODO: properly handle this
		float atmosphere;
		int solarOrbitalDistance, planetOrbitalDistance = 0;
		double myPhi = 0, myTheta = 0, myPrevOrbitalTheta = 0, myRotationalPhi = 0;
		boolean hasAtmosphere = false, isMoon;
		float parentAtmColor[] = new float[]{1f,1f,1f};
		float parentRingColor[] = new float[] {1f,1f,1f};
		float ringColor[] = new float[] {1f,1f,1f};
		float sunSize = 1.0f;
		float starSeperation = 0f;
		boolean isWarp = false;
		boolean isGasGiant = false;
		boolean hasRings = false;
		boolean parentHasRings = false;
		Direction travelDirection = null;
		ResourceLocation parentPlanetIcon = null;
		List<DimensionProperties> children;
		List<StellarBody> subStars = new LinkedList<StellarBody>();
		celestialAngle = mc.world.getCelestialAngle(partialTicks);

		Vector3d sunColor;
		
		
		if(mc.world.provider instanceof IPlanetaryProvider) {
			IPlanetaryProvider planetaryProvider = (IPlanetaryProvider)mc.world.provider;

			DimensionProperties properties = (DimensionProperties)planetaryProvider.getDimensionProperties(mc.player.getPosition());

			atmosphere = planetaryProvider.getAtmosphereDensityFromHeight(mc.getRenderViewEntity().posY, mc.player.getPosition());
			Direction dir = getRotationAxis(properties, mc.player.getPosition());
			axis.x = (float) dir.getXOffset();
			axis.y = (float) dir.getYOffset();
			axis.z = (float) dir.getZOffset();

			myPhi = properties.orbitalPhi;
			myTheta = properties.orbitTheta;
			myRotationalPhi = properties.rotationalPhi;
			myPrevOrbitalTheta = properties.prevOrbitalTheta;
			hasRings = properties.hasRings();
			ringColor = properties.ringColor;

			children = new LinkedList<DimensionProperties>();
			for (Integer i : properties.getChildPlanets()) {
				children.add(DimensionManager.getInstance().getDimensionProperties(i));
			}

			solarOrbitalDistance = properties.getSolarOrbitalDistance();


			if(isMoon = properties.isMoon()) {
				DimensionProperties parentProperties = properties.getParentProperties();
				isGasGiant = parentProperties.isGasGiant();
				hasAtmosphere = parentProperties.hasAtmosphere();
				planetOrbitalDistance = properties.getParentOrbitalDistance();
				parentAtmColor = parentProperties.skyColor;
				parentPlanetIcon = getTextureForPlanet(parentProperties);
				parentHasRings = parentProperties.hasRings;
				parentRingColor = parentProperties.ringColor;
			}

			sunColor = planetaryProvider.getSunColor(mc.player.getPosition());
			sunSize = properties.getStar().getSize();
			subStars = properties.getStar().getSubStars();
			starSeperation = properties.getStar().getStarSeparation();
			if(world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
				isWarp = properties.getParentPlanet() == SpaceObjectManager.WARPDIMID;
				if(isWarp) {
					SpaceStationObject station = (SpaceStationObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(mc.player.getPosition());
					travelDirection = station.getForwardDirection();
				}
			}
		}
		else if(DimensionManager.getInstance().isDimensionCreated(mc.world.provider.getDimension())) {

			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(mc.world.provider.getDimension());

			atmosphere = properties.getAtmosphereDensityAtHeight(mc.getRenderViewEntity().posY);//planetaryProvider.getAtmosphereDensityFromHeight(mc.getRenderViewEntity().posY, mc.player.getPosition());
			Direction dir = getRotationAxis(properties, mc.player.getPosition());
			axis.x = (float) dir.getXOffset();
			axis.y = (float) dir.getYOffset();
			axis.z = (float) dir.getZOffset();

			myPhi = properties.orbitalPhi;
			myTheta = properties.orbitTheta;
			myRotationalPhi = properties.rotationalPhi;
			myPrevOrbitalTheta = properties.prevOrbitalTheta;
			hasRings = properties.hasRings();
			ringColor = properties.ringColor;

			children = new LinkedList<DimensionProperties>();
			for (Integer i : properties.getChildPlanets()) {
				children.add(DimensionManager.getInstance().getDimensionProperties(i));
			}

			solarOrbitalDistance = properties.getSolarOrbitalDistance();


			if(isMoon = properties.isMoon()) {
				DimensionProperties parentProperties = properties.getParentProperties();
				isGasGiant = parentProperties.isGasGiant();
				hasAtmosphere = parentProperties.hasAtmosphere();
				planetOrbitalDistance = properties.getParentOrbitalDistance();
				parentAtmColor = parentProperties.skyColor;
				parentPlanetIcon = getTextureForPlanet(parentProperties);
				parentHasRings = parentProperties.hasRings;
				parentRingColor = parentProperties.ringColor;
			}

			float sunColorFloat[] = properties.getSunColor();
			sunColor = new Vector3d(sunColorFloat[0], sunColorFloat[1], sunColorFloat[2]);//planetaryProvider.getSunColor(mc.player.getPosition());
			sunSize = properties.getStar().getSize();
			subStars = properties.getStar().getSubStars();
			starSeperation = properties.getStar().getStarSeparation();
			if(world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
				isWarp = properties.getParentPlanet() == SpaceObjectManager.WARPDIMID;
				if(isWarp) {
					SpaceStationObject station = (SpaceStationObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(mc.player.getPosition());
					travelDirection = station.getForwardDirection();
				}
			}
		}
		else {
			children = new LinkedList<DimensionProperties>();
			isMoon = false;
			hasAtmosphere = DimensionManager.overworldProperties.hasAtmosphere();
			atmosphere = DimensionManager.overworldProperties.getAtmosphereDensityAtHeight(mc.getRenderViewEntity().posY);
			solarOrbitalDistance = DimensionManager.overworldProperties.orbitalDist;
			sunColor = new Vector3d(1, 1, 1);
		}

		GlStateManager.disableTexture();
		Vector3d vec3 = Minecraft.getInstance().world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
		float f1 = (float)vec3.x;
		float f2 = (float)vec3.y;
		float f3 = (float)vec3.z;
		float f6;

		if (this.mc.gameSettings.anaglyph)
		{
			float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
			float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
			f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
			f1 = f4;
			f2 = f5;
			f3 = f6;
		}

		//Simulate atmospheric thickness
		f1 *= atmosphere;
		f2 *= atmosphere;
		f3 *= atmosphere;

		GL11.glColor3f(f1, f2, f3);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		GL11.glDepthMask(false);
		GlStateManager.enableFog();
		GL11.glColor3f(f1, f2, f3);
		GL11.glCallList(this.glSkyList);
		
		GlStateManager.disableFog();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		float[] afloat = mc.world.provider.calcSunriseSunsetColors(celestialAngle, partialTicks);
		float f7;
		float f8;
		float f9;
		float f10;

		if (afloat != null)
		{
			GlStateManager.disableTexture();
			GL11.glShadeModel(GL11.GL_SMOOTH);
			matrix.push();
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(MathHelper.sin(mc.world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotated(90.0F - myRotationalPhi, 0.0F, 0.0F, 1.0F);

			//Sim atmospheric thickness
			f6 = afloat[0];
			f7 = afloat[1];
			f8 = afloat[2];
			float f11;

			if (this.mc.gameSettings.anaglyph)
			{
				f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
				f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
				f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
				f6 = f9;
				f7 = f10;
				f8 = f11;
			}

			buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3] * atmosphere).endVertex();
			byte b0 = 16;

			for (int j = 0; j <= b0; ++j)
			{
				f11 = (float)j * (float)Math.PI * 2.0F / (float)b0;
				float f12 = MathHelper.sin(f11);
				float f13 = MathHelper.cos(f11);
				buffer.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
			}

			Tessellator.getInstance().draw();
			matrix.pop();
			GL11.glShadeModel(GL11.GL_FLAT);
		}
		
		GlStateManager.enableTexture();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		matrix.push();


		if(atmosphere > 0)
			f6 = 1.0F - (mc.world.getRainStrength(partialTicks)*(atmosphere/100f));
		else
			f6 = 1f;

		f7 = 0.0F;
		f8 = 0.0F;
		f9 = 0.0F;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, f6);
		GL11.glTranslatef(f7, f8, f9);
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);

		float multiplier = (2-atmosphere)/2f;//atmosphere > 1 ? (2-atmosphere) : 1f;
		if(mc.world.isRainingAt(mc.player.getPosition().add(0, 199, 0)))
			multiplier *= 1-mc.world.getRainStrength(partialTicks);

		GL11.glRotatef((float)myRotationalPhi, 0f, 1f, 0f);

		//Draw Rings
		if(hasRings) {
			matrix.push();
			GL11.glRotatef(90f, 0f, 1f, 0f);

			f10 = 100;
			double ringDist = 0;
			mc.renderEngine.bindTexture(DimensionProperties.planetRings);

			GL11.glRotated(70, 1, 0, 0);
			matrix.translate(0, -10, 0);

			GlStateManager.color4f(ringColor[0], ringColor[1], ringColor[2],multiplier);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
			buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			matrix.push();

			GL11.glRotatef(90f, 0f, 1f, 0f);
			GL11.glRotated(70, 1, 0, 0);
			GL11.glRotatef(isWarp ? 0 : celestialAngle * 360.0F, 0, 1, 0);
			matrix.translate(0, -10, 0);



			mc.renderEngine.bindTexture(DimensionProperties.planetRingShadow);
			GlStateManager.color4f(0f, 0f, 0f,multiplier);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
			buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		}

		if(!isWarp)
			rotateAroundAxis();


		GlStateManager.disableTexture();
		float f18 = mc.world.getStarBrightness(partialTicks) * f6 * (atmosphere) + (1-atmosphere);

		if(mc.world.isRainingAt(mc.player.getPosition()))
			f18 *= 1-mc.world.getRainStrength(partialTicks);

		if (f18 > 0.0F)
		{
			GL11.glColor4f(f18, f18, f18, f18);
			matrix.push();
			if(isWarp) {
				for(int i = -3; i < 5; i++) {
					matrix.push();
					double magnitude = i*-100 + (((System.currentTimeMillis()) + 50) % 2000)/20f;
					matrix.translate(-travelDirection.getZOffset()*magnitude, 0, travelDirection.getXOffset()*magnitude);
					GL11.glCallList(this.starGLCallList);
					matrix.pop();
				}
				//matrix.translate(((System.currentTimeMillis()/10) + 50) % 100, 0, 0);
			}
			else {
				GL11.glCallList(this.starGLCallList);
				//Extra stars for low ATM
				if(atmosphere < 0.5) {
					GL11.glColor4f(f18, f18, f18, f18/2f);
					matrix.push();
					GL11.glRotatef(-90, 0, 1, 0);
					GL11.glCallList(this.starGLCallList);
					matrix.pop();
				}
				if(atmosphere < 0.25) {
					GL11.glColor4f(f18, f18, f18, f18/4f);
					matrix.push();
					GL11.glRotatef(90, 0, 1, 0);
					GL11.glCallList(this.starGLCallList);
					matrix.pop();
				}
				GL11.glColor4f(f18, f18, f18, f18);
			}
			matrix.pop();
		}
		GlStateManager.enableTexture();

		mc.renderEngine.bindTexture(TextureResources.locationSunPng);

		//--------------------------- Draw the suns --------------------
		if(!isWarp) {
			//Set sun color and distance
			drawStar(buffer, solarOrbitalDistance, sunSize, sunColor, multiplier);

			if(subStars != null && !subStars.isEmpty()) {
				matrix.push();
				float phaseInc = 360/subStars.size();
				
				for(StellarBody subStar : subStars) {
					GL11.glRotatef(phaseInc, 0, 1, 0);
					matrix.push();
					
					GL11.glRotatef(subStar.getStarSeparation()*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance), 1, 0, 0);
					float color[] = subStar.getColor();
					drawStar(buffer, solarOrbitalDistance, subStar.getSize(), new Vector3d(color[0], color[1], color[2]), multiplier);
					matrix.pop();
				}
				matrix.pop();
			}

		}
		f10 = 20.0F;


		if(isMoon) {
			matrix.push();

			GL11.glRotatef((float)myPhi, 0f, 0f, 1f);
			GL11.glRotatef((float)((partialTicks*myTheta + ((1-partialTicks)*myPrevOrbitalTheta)) * 180F/Math.PI), 1f, 0f, 0f);

			float phiAngle = (float)((myPhi) * Math.PI/180f);

			//Close enough approximation, I missed something but seems to off by no more than 30*
			//Nobody will look
			double x = MathHelper.sin(phiAngle)*MathHelper.cos((float)myTheta);
			double y = -MathHelper.sin((float)myTheta);
			double rotation = -Math.PI/2f + Math.atan2(x, y) - (myTheta - Math.PI )*MathHelper.sin(phiAngle);

			//Draw Rings
			if(parentHasRings) {
				matrix.push();
				GL11.glRotatef(90f, 0f, 1f, 0f);

				f10 = 100;
				double ringDist = 0;
				mc.renderEngine.bindTexture(DimensionProperties.planetRings);

				GL11.glRotated(70, 1, 0, 0);
				matrix.translate(0, -10, 50);

				GlStateManager.color4f(parentRingColor[0], parentRingColor[1], parentRingColor[2],multiplier);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
				buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				matrix.push();

				GL11.glRotatef(90f, 0f, 1f, 0f);
				GL11.glRotated(70, 1, 0, 0);
				matrix.translate(0, -10, 50);

				mc.renderEngine.bindTexture(DimensionProperties.planetRingShadow);
				GlStateManager.color4f(0f, 0f, 0f,1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
				buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			}

			renderPlanet2(buffer, parentPlanetIcon, 0,0,-100, AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance), multiplier, rotation, hasAtmosphere, parentAtmColor, parentRingColor, isGasGiant, false);
			matrix.pop();
		}

		for(DimensionProperties moons : children) {
			matrix.push();

			moons.orbitalPhi = 10;
			double rot = ((partialTicks*moons.orbitTheta + ((1-partialTicks)*moons.prevOrbitalTheta)) * 180F/Math.PI);

			GL11.glRotatef((float)moons.orbitalPhi, 0f, 0f, 1f);
			GL11.glRotated(rot, 1f, 0f, 0f);

			//Close enough approximation, I missed something but seems to off by no more than 30*
			//Nobody will look
			float phiAngle = (float)((moons.orbitalPhi) * Math.PI/180f);
			double x = -MathHelper.sin(phiAngle)*MathHelper.cos((float)moons.orbitTheta);
			double y = MathHelper.sin((float)moons.orbitTheta);
			double rotation = -Math.PI/2f + Math.atan2(x, y) - (moons.orbitTheta - Math.PI)*MathHelper.sin(phiAngle);


			//double x = -MathHelper.sin(phiAngle)*MathHelper.cos((float)moons.orbitTheta);
			//double y = MathHelper.sin((float)moons.orbitTheta);


			//double x = -MathHelper.cos((float)moons.orbitTheta)*MathHelper.sin(phiAngle);
			//double y = -MathHelper.sin((float)moons.orbitTheta);
			//double z = MathHelper.cos(phiAngle)*MathHelper.cos((float)moons.orbitTheta) - 1;



			//double hyp = Math.sqrt(x*x +  y*y);
			//System.out.println(x*x + z*z + y*y);
			//double rotation = Math.atan2(z,hyp );// - MathHelper.sin((float)moons.orbitTheta);//-Math.PI/2f + Math.atan2(x, y) - (moons.orbitTheta - Math.PI)*MathHelper.sin(phiAngle)*hyp;


			renderPlanet(buffer, moons.getPlanetIcon(), (1/(float)moons.getParentOrbitalDistance())*moons.gravitationalMultiplier, multiplier, rotation, moons.hasAtmosphere(), moons.skyColor, moons.ringColor, isGasGiant, moons.hasRings());
			matrix.pop();
		}

		GlStateManager.enableFog();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();

		matrix.pop();

		GlStateManager.enableTexture();
		
		mc.renderEngine.bindTexture(asteroid1);
		GlStateManager.color4f(1, 1, 1);
		GL11.glCallList(this.glSkyList3);
		
		matrix.push();
		GL11.glRotatef(90, 0.2f, 0.8f, 0);
		mc.renderEngine.bindTexture(asteroid2);
		GL11.glCallList(this.glSkyList3);
		GL11.glRotatef(90, 0.2f, 0.8f, 0);
		mc.renderEngine.bindTexture(asteroid3);
		GL11.glCallList(this.glSkyList3);
		matrix.pop();
		
		GL11.glDepthMask(true);


		RocketEventHandler.onPostWorldRender(partialTicks);
		//Fix player/items going transparent
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
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

	protected void renderPlanet(BufferBuilder buffer, ResourceLocation icon, float planetOrbitalDistance, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing) {
		renderPlanet2(buffer, icon, 0, 0, -100, 10f*AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance), alphaMultiplier, shadowAngle, hasAtmosphere, skyColor, ringColor, gasGiant, hasRing);
	}

	protected void renderPlanet2(BufferBuilder buffer, ResourceLocation icon, int locationX, int locationY, double zLevel, float size, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing) {
		renderPlanetPubHelper(buffer, icon, locationX, locationY, zLevel, size, alphaMultiplier, shadowAngle, hasAtmosphere, skyColor, ringColor, gasGiant, hasRing);
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

	public static void renderPlanetPubHelper(BufferBuilder buffer, ResourceLocation icon, int locationX, int locationY, double zLevel, float size, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing) {
		GL11.glEnable(GL11.GL_BLEND);

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


		//ATM Glow
		matrix.push();
		GL11.glRotated(90-shadowAngle* 180/Math.PI, 0, 1, 0);

		//Rings
		if(hasRing) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.atmGlow);

		GlStateManager.color4f(1f, 1f, 1f, alphaMultiplier);
		buffer.pos(-f10, zLevel+0.01f, f10).tex((double)f16, (double)f17).endVertex();
		buffer.pos(f10, zLevel+0.01f, f10).tex((double)f14, (double)f17).endVertex();
		buffer.pos(f10, zLevel+0.01f, -f10).tex((double)f14, (double)f15).endVertex();
		buffer.pos(-f10, zLevel+0.01f, -f10).tex((double)f16, (double)f15).endVertex();
		Tessellator.getInstance().draw();
		matrix.pop();

		//End ATM glow

		Minecraft.getInstance().getTextureManager().bindTexture(icon);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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


		//Draw atmosphere if applicable
		if(hasAtmosphere) {

			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

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
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1f, 1f, 1f, alphaMultiplier);
		buffer.pos(-f10, zLevel-0.01f, f10).tex((double)f16, (double)f17).endVertex();
		buffer.pos(f10, zLevel-0.01f, f10).tex((double)f14, (double)f17).endVertex();
		buffer.pos(f10, zLevel-0.01f, -f10).tex((double)f14, (double)f15).endVertex();
		buffer.pos(-f10, zLevel-0.01f, -f10).tex((double)f16, (double)f15).endVertex();
		Tessellator.getInstance().draw();

		matrix.pop();


		GlStateManager.color4f(1f, 1f, 1f, 1f);
	}

	private void drawStar(BufferBuilder buffer, int solarOrbitalDistance, float sunSize, Vector3d sunColor, float multiplier) {
		//Set sun color and distance
		GlStateManager.color4f((float)sunColor.x, (float)sunColor.y , (float)sunColor.z ,Math.min((multiplier)*2f,1f));
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
		float f10 = sunSize*30f*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance);
		//multiplier = 2;
		buffer.pos((double)(-f10), 100.0D, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
		buffer.pos((double)f10, 100.0D, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
		buffer.pos((double)f10, 100.0D, (double)f10).tex(1.0D, 1.0D).endVertex();
		buffer.pos((double)(-f10), 100.0D, (double)f10).tex(0.0D, 1.0D).endVertex();
		Tessellator.getInstance().draw();
	}
}
