package zmaster587.advancedRocketry.client.render.planet;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.language.bm.Rule.RPattern;
import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;

public class RenderPlanetarySky extends IRenderHandler {


	private int starGLCallList;
	private int glSkyList;
	private int glSkyList2;
	ResourceLocation currentlyBoundTex = null;
	float celestialAngle;
	Vector3F<Float> axis;

	public static WavefrontObject obj;

	static {
		if(obj == null)
			try {
				obj = new WavefrontObject(new ResourceLocation("advancedrocketry:models/atmosphere.obj"));
			} catch(ModelFormatException e) {

			}
	}

	//Mostly vanilla code
	//TODO: make usable on other planets
	public RenderPlanetarySky() {
		axis = new Vector3F<Float>(1f, 0f, 0f);

		this.starGLCallList = GLAllocation.generateDisplayLists(3);
		GL11.glPushMatrix();
		GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
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

	Minecraft mc = Minecraft.getMinecraft();

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
		EnumFacing travelDirection = null;
		ResourceLocation parentPlanetIcon = null;
		List<DimensionProperties> children;
		List<StellarBody> subStars = new LinkedList<StellarBody>();
		celestialAngle = mc.world.getCelestialAngle(partialTicks);

		Vec3d sunColor;
		
		
		if(mc.world.provider instanceof IPlanetaryProvider) {
			IPlanetaryProvider planetaryProvider = (IPlanetaryProvider)mc.world.provider;

			DimensionProperties properties = (DimensionProperties)planetaryProvider.getDimensionProperties(mc.player.getPosition());

			atmosphere = planetaryProvider.getAtmosphereDensityFromHeight(mc.getRenderViewEntity().posY, mc.player.getPosition());
			EnumFacing dir = getRotationAxis(properties, mc.player.getPosition());
			axis.x = (float) dir.getFrontOffsetX();
			axis.y = (float) dir.getFrontOffsetY();
			axis.z = (float) dir.getFrontOffsetZ();

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
			starSeperation = properties.getStar().getStarSeperation();
			if(world.provider.getDimension() == Configuration.spaceDimId) {
				isWarp = properties.getParentPlanet() == SpaceObjectManager.WARPDIMID;
				if(isWarp) {
					SpaceObject station = (SpaceObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(mc.player.getPosition());
					travelDirection = station.getForwardDirection();
				}
			}
		}
		else if(DimensionManager.getInstance().isDimensionCreated(mc.world.provider.getDimension())) {

			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(mc.world.provider.getDimension());

			atmosphere = properties.getAtmosphereDensityAtHeight(mc.getRenderViewEntity().posY);//planetaryProvider.getAtmosphereDensityFromHeight(mc.getRenderViewEntity().posY, mc.player.getPosition());
			EnumFacing dir = getRotationAxis(properties, mc.player.getPosition());
			axis.x = (float) dir.getFrontOffsetX();
			axis.y = (float) dir.getFrontOffsetY();
			axis.z = (float) dir.getFrontOffsetZ();

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
			sunColor = new Vec3d(sunColorFloat[0], sunColorFloat[1], sunColorFloat[2]);//planetaryProvider.getSunColor(mc.player.getPosition());
			sunSize = properties.getStar().getSize();
			subStars = properties.getStar().getSubStars();
			starSeperation = properties.getStar().getStarSeperation();
			if(world.provider.getDimension() == Configuration.spaceDimId) {
				isWarp = properties.getParentPlanet() == SpaceObjectManager.WARPDIMID;
				if(isWarp) {
					SpaceObject station = (SpaceObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(mc.player.getPosition());
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
			sunColor = new Vec3d(1, 1, 1);
		}

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Vec3d vec3 = Minecraft.getMinecraft().world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
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
		GL11.glEnable(GL11.GL_FOG);
		GL11.glColor3f(f1, f2, f3);
		GL11.glCallList(this.glSkyList);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		float[] afloat = mc.world.provider.calcSunriseSunsetColors(celestialAngle, partialTicks);
		float f7;
		float f8;
		float f9;
		float f10;

		if (afloat != null)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glPushMatrix();
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
			GL11.glPopMatrix();
			GL11.glShadeModel(GL11.GL_FLAT);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//OpenGlHelper.glBlendFunc(770, 1, 1, 0);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);

		GL11.glPushMatrix();


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
			GL11.glPushMatrix();
			GL11.glRotatef(90f, 0f, 1f, 0f);

			f10 = 100;
			double ringDist = 0;
			mc.renderEngine.bindTexture(DimensionProperties.planetRings);

			GL11.glRotated(70, 1, 0, 0);
			GL11.glTranslated(0, -10, 0);

			GlStateManager.color(ringColor[0], ringColor[1], ringColor[2],multiplier);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
			buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();

			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glPushMatrix();

			GL11.glRotatef(90f, 0f, 1f, 0f);
			GL11.glRotated(70, 1, 0, 0);
			GL11.glRotatef(isWarp ? 0 : celestialAngle * 360.0F, 0, 1, 0);
			GL11.glTranslated(0, -10, 0);



			mc.renderEngine.bindTexture(DimensionProperties.planetRingShadow);
			GlStateManager.color(0f, 0f, 0f,multiplier);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
			buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();

			OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);
		}

		if(!isWarp)
			rotateAroundAxis();


		GL11.glDisable(GL11.GL_TEXTURE_2D);
		float f18 = mc.world.getStarBrightness(partialTicks) * f6 * (atmosphere) + (1-atmosphere);

		if(mc.world.isRainingAt(mc.player.getPosition()))
			f18 *= 1-mc.world.getRainStrength(partialTicks);

		if (f18 > 0.0F)
		{
			GL11.glColor4f(f18, f18, f18, f18);
			GL11.glPushMatrix();
			if(isWarp) {
				for(int i = -3; i < 5; i++) {
					GL11.glPushMatrix();
					double magnitude = i*-100 + (((System.currentTimeMillis()) + 50) % 2000)/20f;
					GL11.glTranslated(-travelDirection.getFrontOffsetZ()*magnitude, 0, travelDirection.getFrontOffsetX()*magnitude);
					GL11.glCallList(this.starGLCallList);
					GL11.glPopMatrix();
				}
				//GL11.glTranslated(((System.currentTimeMillis()/10) + 50) % 100, 0, 0);
			}
			else {
				GL11.glCallList(this.starGLCallList);
				//Extra stars for low ATM
				if(atmosphere < 0.5) {
					GL11.glColor4f(f18, f18, f18, f18/2f);
					GL11.glPushMatrix();
					GL11.glRotatef(-90, 0, 1, 0);
					GL11.glCallList(this.starGLCallList);
					GL11.glPopMatrix();
				}
				if(atmosphere < 0.25) {
					GL11.glColor4f(f18, f18, f18, f18/4f);
					GL11.glPushMatrix();
					GL11.glRotatef(90, 0, 1, 0);
					GL11.glCallList(this.starGLCallList);
					GL11.glPopMatrix();
				}
				GL11.glColor4f(f18, f18, f18, f18);
			}
			GL11.glPopMatrix();
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		mc.renderEngine.bindTexture(TextureResources.locationSunPng);

		//--------------------------- Draw the suns --------------------
		if(!isWarp) {
			//Set sun color and distance
			drawStar(buffer, solarOrbitalDistance, sunSize, sunColor, multiplier);

			if(subStars != null && !subStars.isEmpty()) {
				GL11.glPushMatrix();
				float phaseInc = 360/subStars.size();
				
				for(StellarBody subStar : subStars) {
					GL11.glRotatef(phaseInc, 0, 1, 0);
					GL11.glPushMatrix();
					
					GL11.glRotatef(subStar.getStarSeperation()*(202-solarOrbitalDistance)/100f, 1, 0, 0);
					float color[] = subStar.getColor();
					drawStar(buffer, solarOrbitalDistance, subStar.getSize(), new Vec3d(color[0], color[1], color[2]), multiplier);
					GL11.glPopMatrix();
				}
				GL11.glPopMatrix();
			}

		}
		f10 = 20.0F;


		if(isMoon) {
			GL11.glPushMatrix();

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
				GL11.glPushMatrix();
				GL11.glRotatef(90f, 0f, 1f, 0f);

				f10 = 100;
				double ringDist = 0;
				mc.renderEngine.bindTexture(DimensionProperties.planetRings);

				GL11.glRotated(70, 1, 0, 0);
				GL11.glTranslated(0, -10, 50);

				GlStateManager.color(parentRingColor[0], parentRingColor[1], parentRingColor[2],multiplier);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
				buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				GL11.glPopMatrix();

				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glPushMatrix();

				GL11.glRotatef(90f, 0f, 1f, 0f);
				GL11.glRotated(70, 1, 0, 0);
				GL11.glTranslated(0, -10, 50);

				mc.renderEngine.bindTexture(DimensionProperties.planetRingShadow);
				GlStateManager.color(0f, 0f, 0f,1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0D, 0.0D).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0D, 0.0D).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0D, 1.0D).endVertex();
				buffer.pos((double)f10, ringDist, (double)f10).tex(1.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				GL11.glPopMatrix();

				OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 1, 0);
			}


			renderPlanet2(buffer, parentPlanetIcon, 0,0,-100, (200-planetOrbitalDistance), multiplier, rotation, hasAtmosphere, parentAtmColor, parentRingColor, isGasGiant, false);
			GL11.glPopMatrix();
		}

		for(DimensionProperties moons : children) {
			GL11.glPushMatrix();

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
			GL11.glPopMatrix();
		}

		GL11.glEnable(GL11.GL_FOG);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0.0F, 0.0F, 0.0F);
		double d0 = this.mc.player.getPositionEyes(partialTicks).y - mc.world.getHorizon();

		if (d0 < 0.0D)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 12.0F, 0.0F);
			GL11.glCallList(this.glSkyList2);
			GL11.glPopMatrix();
			f8 = 1.0F;
			f9 = -((float)(d0 + 65.0D));
			f10 = -f8;
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

			buffer.color(0,0,0,1f);
			buffer.pos((double)(-f8), (double)f9, (double)f8).endVertex();
			buffer.pos((double)f8, (double)f9, (double)f8).endVertex();
			buffer.pos((double)f8, (double)f10, (double)f8).endVertex();
			buffer.pos((double)(-f8), (double)f10, (double)f8).endVertex();
			buffer.pos((double)(-f8), (double)f10, (double)(-f8)).endVertex();
			buffer.pos((double)f8, (double)f10, (double)(-f8)).endVertex();
			buffer.pos((double)f8, (double)f9, (double)(-f8)).endVertex();
			buffer.pos((double)(-f8), (double)f9, (double)(-f8)).endVertex();
			buffer.pos((double)f8, (double)f10, (double)(-f8)).endVertex();
			buffer.pos((double)f8, (double)f10, (double)f8).endVertex();
			buffer.pos((double)f8, (double)f9, (double)f8).endVertex();
			buffer.pos((double)f8, (double)f9, (double)(-f8)).endVertex();
			buffer.pos((double)(-f8), (double)f9, (double)(-f8)).endVertex();
			buffer.pos((double)(-f8), (double)f9, (double)f8).endVertex();
			buffer.pos((double)(-f8), (double)f10, (double)f8).endVertex();
			buffer.pos((double)(-f8), (double)f10, (double)(-f8)).endVertex();
			buffer.pos((double)(-f8), (double)f10, (double)(-f8)).endVertex();
			buffer.pos((double)(-f8), (double)f10, (double)f8).endVertex();
			buffer.pos((double)f8, (double)f10, (double)f8).endVertex();
			buffer.pos((double)f8, (double)f10, (double)(-f8)).endVertex();

			Tessellator.getInstance().draw();
		}

		if (mc.world.provider.isSkyColored())
		{
			GL11.glColor3f(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
		}
		else
		{
			GL11.glColor3f(f1, f2, f3);
		}

		//Blackness @ bottom of world
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, -((float)(d0 - 16.0D)), 0.0F);
		GL11.glCallList(this.glSkyList2);
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
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

	protected EnumFacing getRotationAxis(DimensionProperties properties, BlockPos pos) {
		return EnumFacing.EAST;
	}

	protected void renderPlanet(BufferBuilder buffer, ResourceLocation icon, float planetOrbitalDistance, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing) {
		renderPlanet2(buffer, icon, 0, 0, -100, 10f*(200-planetOrbitalDistance)/100f, alphaMultiplier, shadowAngle, hasAtmosphere, skyColor, ringColor, gasGiant, hasRing);
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


		GL11.glPushMatrix();
		GL11.glTranslated(locationX, zLevel, locationY);


		//ATM Glow
		GL11.glPushMatrix();
		GL11.glRotated(90-shadowAngle* 180/Math.PI, 0, 1, 0);

		//Rings
		if(hasRing) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(ringColor[0], ringColor[1], ringColor[2], alphaMultiplier*0.2f);
			float ringSize = f10 *1.4f;
			Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.planetRings);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			buffer.pos(-ringSize, zLevel-0.01f, ringSize).tex((double)f16, (double)f17).endVertex();
			buffer.pos(ringSize, zLevel-0.01f, ringSize).tex((double)f14, (double)f17).endVertex();
			buffer.pos(ringSize, zLevel-0.01f, -ringSize).tex((double)f14, (double)f15).endVertex();
			buffer.pos(-ringSize, zLevel-0.01f, -ringSize).tex((double)f16, (double)f15).endVertex();
			Tessellator.getInstance().draw();

			GlStateManager.color(0f, 0f, 0f, alphaMultiplier);
			Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.planetRingShadow);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			buffer.pos(-ringSize, zLevel-0.01f, ringSize).tex((double)f16, (double)f17).endVertex();
			buffer.pos(ringSize, zLevel-0.01f, ringSize).tex((double)f14, (double)f17).endVertex();
			buffer.pos(ringSize, zLevel-0.01f, -ringSize).tex((double)f14, (double)f15).endVertex();
			buffer.pos(-ringSize, zLevel-0.01f, -ringSize).tex((double)f16, (double)f15).endVertex();
			Tessellator.getInstance().draw();
		}

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.atmGlow);

		GlStateManager.color(1f, 1f, 1f, alphaMultiplier);
		buffer.pos(-f10, zLevel+0.01f, f10).tex((double)f16, (double)f17).endVertex();
		buffer.pos(f10, zLevel+0.01f, f10).tex((double)f14, (double)f17).endVertex();
		buffer.pos(f10, zLevel+0.01f, -f10).tex((double)f14, (double)f15).endVertex();
		buffer.pos(-f10, zLevel+0.01f, -f10).tex((double)f16, (double)f15).endVertex();
		Tessellator.getInstance().draw();
		GL11.glPopMatrix();

		//End ATM glow

		Minecraft.getMinecraft().renderEngine.bindTexture(icon);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//TODO: draw sky planets

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		GlStateManager.color(1f, 1f, 1f, alphaMultiplier);
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
			Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.getAtmosphereResource());
			GlStateManager.color(skyColor[0], skyColor[1], skyColor[2], alphaMultiplier);
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
		Minecraft.getMinecraft().renderEngine.bindTexture(DimensionProperties.getShadowResource());
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1f, 1f, 1f, alphaMultiplier);
		buffer.pos(-f10, zLevel-0.01f, f10).tex((double)f16, (double)f17).endVertex();
		buffer.pos(f10, zLevel-0.01f, f10).tex((double)f14, (double)f17).endVertex();
		buffer.pos(f10, zLevel-0.01f, -f10).tex((double)f14, (double)f15).endVertex();
		buffer.pos(-f10, zLevel-0.01f, -f10).tex((double)f16, (double)f15).endVertex();
		Tessellator.getInstance().draw();

		GL11.glPopMatrix();


		GlStateManager.color(1f, 1f, 1f, 1f);
	}

	private void drawStar(BufferBuilder buffer, int solarOrbitalDistance, float sunSize, Vec3d sunColor, float multiplier) {
		//Set sun color and distance
		GlStateManager.color((float)sunColor.x, (float)sunColor.y , (float)sunColor.z ,Math.min((multiplier)*2f,1f));
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
