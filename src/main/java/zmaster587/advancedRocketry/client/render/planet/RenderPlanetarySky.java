package zmaster587.advancedRocketry.client.render.planet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AstronomicalBodyHelper;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RenderPlanetarySky implements ISkyRenderer { // implements IRenderHandler {

	private final VertexFormat skyVertexFormat = DefaultVertexFormats.POSITION;
	private VertexBuffer starGLCallList;
	private VertexBuffer glSkyList;
	private VertexBuffer glSkyList2;
	private VertexBuffer glSkyList3;
	float celestialAngle;
	Vector3F<Float> axis;
	
	public static final  ResourceLocation asteroid1 = new ResourceLocation("advancedrocketry:textures/planets/asteroid_a.png");
	public static final  ResourceLocation asteroid2 = new ResourceLocation("advancedrocketry:textures/planets/asteroid_b.png");
	public static final  ResourceLocation asteroid3 = new ResourceLocation("advancedrocketry:textures/planets/asteroid_c.png");

	public RenderPlanetarySky() {
		axis = new Vector3F<>(1f, 0f, 0f);

		this.generateStars();
		this.generateSky();
		this.generateSky2();
		this.generateAsteroids();
	}

	private void generateSky2() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		if (this.glSkyList2 != null) {
			this.glSkyList2.close();
		}

		this.glSkyList2 = new VertexBuffer(this.skyVertexFormat);
		this.renderSky(bufferbuilder, -16.0F, true);
		bufferbuilder.finishDrawing();
		this.glSkyList2.upload(bufferbuilder);
	}

	private void generateSky() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		if (this.glSkyList != null) {
			this.glSkyList.close();
		}

		this.glSkyList = new VertexBuffer(this.skyVertexFormat);
		this.renderSky(bufferbuilder, 16.0F, false);
		bufferbuilder.finishDrawing();
		this.glSkyList.upload(bufferbuilder);
	}

	private void renderSky(BufferBuilder bufferBuilderIn, float posY, boolean reverseX) {
		bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

		for(int k = -384; k <= 384; k += 64) {
			for(int l = -384; l <= 384; l += 64) {
				float f = (float)k;
				float f1 = (float)(k + 64);
				if (reverseX) {
					f1 = (float)k;
					f = (float)(k + 64);
				}

				bufferBuilderIn.pos(f, posY, l).endVertex();
				bufferBuilderIn.pos(f1, posY, l).endVertex();
				bufferBuilderIn.pos(f1, posY, l + 64).endVertex();
				bufferBuilderIn.pos(f, posY, l + 64).endVertex();
			}
		}

	}

	private void generateStars() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		if (this.starGLCallList != null) {
			this.starGLCallList.close();
		}

		this.starGLCallList = new VertexBuffer(this.skyVertexFormat);
		this.renderStars(bufferbuilder);
		bufferbuilder.finishDrawing();
		this.starGLCallList.upload(bufferbuilder);
	}

	private void renderStars(BufferBuilder bufferBuilderIn) {
		Random random = new Random(10842L);
		bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

		for(int i = 0; i < 1500; ++i) {
			double d0 = random.nextFloat() * 2.0F - 1.0F;
			double d1 = random.nextFloat() * 2.0F - 1.0F;
			double d2 = random.nextFloat() * 2.0F - 1.0F;
			double d3 = 0.15F + random.nextFloat() * 0.1F;
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;
			if (d4 < 1.0D && d4 > 0.01D) {
				d4 = 1.0D / Math.sqrt(d4);
				d0 = d0 * d4;
				d1 = d1 * d4;
				d2 = d2 * d4;
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

				for(int j = 0; j < 4; ++j) {
					double d18 = (double)((j & 2) - 1) * d3;
					double d19 = (double)((j + 1 & 2) - 1) * d3;

					double d21 = d18 * d16 - d19 * d15;
					double d22 = d19 * d16 + d18 * d15;
					double d23 = d21 * d12 + 0.0D * d13;
					double d24 = 0.0D * d12 - d21 * d13;
					double d25 = d24 * d9 - d22 * d10;
					double d26 = d22 * d9 + d24 * d10;
					bufferBuilderIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
				}
			}
		}
	}


	private void generateAsteroids() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		if (this.glSkyList3 != null) {
			this.glSkyList3.close();
		}

		this.glSkyList3 = new VertexBuffer(DefaultVertexFormats.POSITION_TEX);
		this.renderAsteroids(bufferbuilder);
		this.glSkyList3.upload(bufferbuilder);
	}
	
	private void renderAsteroids(BufferBuilder buffer)
	{
		Random random = new Random(10843L);
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		
		for (int i = 0; i < 200; ++i)
		{
			double d0 = random.nextFloat()*2F - 1F;
			double d1 = random.nextFloat()*1F - .5F;
			double d2 = random.nextFloat()*2F - 1F;
			double size = 0.15F + random.nextFloat();
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

				for (int j = 0; j < 4; ++j) {
					double d17 = 0.0D;
					double d18 = (double)((j & 2) - 1) * size;
					double d19 = (double)((j + 1 & 2) - 1) * size;
					double d20 = d18 * d16 - d19 * d15;
					double d21 = d19 * d16 + d18 * d15;
					double d22 = d20 * d12 + d17 * d13;
					double d23 = d17 * d12 - d20 * d13;
					double d24 = d23 * d9 - d21 * d10;
					double d25 = d21 * d9 + d23 * d10;
					buffer.pos(d5 + d24, d6 + d22, d7 + d25).tex( (float)(d18/(size*2) + .5f),  (float)(d19/(size*2) +.5f) ).color(r, g, b, 1f).endVertex();
				}
			}
		}
		buffer.finishDrawing();
	}
	
	Minecraft mc = Minecraft.getInstance();


	public void render(MatrixStack matrix, float partialTicks) {

		Minecraft mc = Minecraft.getInstance();
		DimensionManager dimensionMgr = DimensionManager.getInstance();
		ClientWorld world = mc.world;

		//TODO: properly handle this
		float atmosphere;
		int solarOrbitalDistance, planetOrbitalDistance = 0;
		double myPhi = 0, myTheta = 0, myRotationalPhi = 0;
		boolean isMoon;
		float[] shadowColorMultiplier;
		float[] parentRingColor = new float[] {1f,1f,1f};
		float[] ringColor = new float[] {1f,1f,1f};
		float sunSize = 1.0f;
		float starSeparation = 0f;
		boolean isWarp = false;
		boolean hasRings = false;
		boolean parentHasRings = false;
		DimensionProperties parentProperties = null;
		DimensionProperties properties;
		Direction travelDirection = null;
		List<DimensionProperties> children;

		StellarBody primaryStar;
		celestialAngle = mc.world.getCelestialAngleRadians(partialTicks);
		BlockPos playerPos = new BlockPos(mc.player.getPositionVec());

		Vector3d sunColor;

		if(dimensionMgr.isDimensionCreated(mc.world)) {

			properties = DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(mc.world), new BlockPos(mc.player.getPositionVec()));


			atmosphere = properties.getAtmosphereDensityAtHeight(mc.getRenderViewEntity().getPosY());//planetaryProvider.getAtmosphereDensityFromHeight(mc.getRenderViewEntity().posY, mc.player.getPosition());
			Direction dir = getRotationAxis(properties, playerPos);
			axis.x = (float) dir.getXOffset();
			axis.y = (float) dir.getYOffset();
			axis.z = (float) dir.getZOffset();

			myPhi = properties.orbitalPhi;
			myTheta = properties.orbitTheta;
			myRotationalPhi = properties.rotationalPhi;
			hasRings = properties.hasRings();
			ringColor = properties.ringColor;

			children = new LinkedList<>();
			for (ResourceLocation i : properties.getChildPlanets()) {
				children.add(DimensionManager.getInstance().getDimensionProperties(i));
			}

			solarOrbitalDistance = properties.getSolarOrbitalDistance();


			if(isMoon = properties.isMoon()) {
				parentProperties = properties.getParentProperties();
				planetOrbitalDistance = properties.getParentOrbitalDistance();
				parentHasRings = parentProperties.hasRings;
				parentRingColor = parentProperties.ringColor;
			}

			float[] sunColorFloat = properties.getSunColor();
			sunColor = new Vector3d(sunColorFloat[0], sunColorFloat[1], sunColorFloat[2]);//planetaryProvider.getSunColor(mc.player.getPosition());
			primaryStar = properties.getStar();
			if (primaryStar != null) {
				sunSize = properties.getStar().getSize();
			} else
				primaryStar = DimensionManager.getInstance().getStar(new ResourceLocation(Constants.STAR_NAMESPACE, "0"));
			if(properties.isStation()) {
				isWarp = SpaceObjectManager.WARPDIMID.equals(properties.getParentPlanet());
				if(isWarp) {
					SpaceStationObject station = (SpaceStationObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(playerPos);
					travelDirection = station.getForwardDirection();
				}
			}
		} else {
			children = new LinkedList<>();
			isMoon = false;
			atmosphere = DimensionManager.overworldProperties.getAtmosphereDensityAtHeight(mc.getRenderViewEntity().getPosY());
			solarOrbitalDistance = DimensionManager.overworldProperties.orbitalDist;
			sunColor = new Vector3d(1, 1, 1);
			primaryStar = DimensionManager.overworldProperties.getStar();
			properties = DimensionManager.overworldProperties;
		}

		RenderSystem.disableTexture();
		Vector3d vec3 = world.getSkyColor(mc.gameRenderer.getActiveRenderInfo().getBlockPos(), partialTicks);
		float f1 = (float)vec3.x;
		float f2 = (float)vec3.y;
		float f3 = (float)vec3.z;
		float f6;

		//Simulate atmospheric thickness
		int atmosphereInt = properties.getAtmosphereDensity();
		f1 = atmosphereInt < 1 ? 0 : (float) Math.pow(f1, Math.sqrt(Math.max(atmosphere, 0.81)));
		f2 = atmosphereInt < 1 ? 0 : (float) Math.pow(f2, Math.sqrt(Math.max(atmosphere, 0.81)));
		f3 = atmosphereInt < 1 ? 0 : (float) Math.pow(f3, Math.sqrt(Math.max(atmosphere, 0.81)));


		RenderSystem.color3f(f1, f2, f3);
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		RenderSystem.depthMask(false);
		RenderSystem.enableFog();
		RenderSystem.color3f(f1, f2, f3);
		glSkyList.bindBuffer();
		DefaultVertexFormats.POSITION.setupBufferState(0L);
		glSkyList.draw(matrix.getLast().getMatrix(), 7);
		VertexBuffer.unbindBuffer();
		DefaultVertexFormats.POSITION.clearBufferState();
		RenderSystem.disableFog();
		RenderSystem.disableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderHelper.disableStandardItemLighting();
		float[] afloat = world.getDimensionRenderInfo().func_230492_a_(world.func_242415_f(partialTicks), partialTicks);
		float f7;
		float f8;
		float f9;
		float size;

		if (afloat != null) {
			RenderSystem.disableTexture();
			RenderSystem.shadeModel(GL11.GL_SMOOTH);
			matrix.push();
			matrix.rotate(new Quaternion(90.0F, 0.0F, 0.0F, true));
			matrix.rotate(new Quaternion(0.0F, 0.0F, MathHelper.sin(mc.world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, true));
			matrix.rotate(new Quaternion( 0.0F, 0.0F, 90.0F - (float)myRotationalPhi,true));

			//Sim atmospheric thickness
			f6 = afloat[0];
			f7 = afloat[1];
			f8 = afloat[2];
			float f11;

			buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, 0.0D, 100.0f, 0.0f).color(f6, f7, f8, afloat[3] * atmosphere).endVertex();
			byte b0 = 16;

			for (int j = 0; j <= b0; ++j)
			{
				f11 = (float)j * (float)Math.PI * 2.0F / (float)b0;
				float f12 = MathHelper.sin(f11);
				float f13 = MathHelper.cos(f11);
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, f12 * 120.0F, f13 * 120.0F, -f13 * 40.0F * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
			}

			Tessellator.getInstance().draw();
			matrix.pop();
			RenderSystem.shadeModel(GL11.GL_FLAT);
		}
		shadowColorMultiplier = new float[]{f1, f2, f3};

		RenderSystem.enableTexture();
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		matrix.push();


		if(atmosphere > 0)
			f6 = 1.0F - (mc.world.getRainStrength(partialTicks)*(atmosphere/100f));
		else
			f6 = 1f;

		f7 = 0.0F;
		f8 = 0.0F;
		f9 = 0.0F;
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, f6);
		matrix.translate(f7, f8, f9);
		matrix.rotate(new Quaternion(0.0F,-90.0F, 0.0F, true));

		float multiplier = (2-atmosphere)/2f;//atmosphere > 1 ? (2-atmosphere) : 1f;
		if(mc.world.isRainingAt(new BlockPos(mc.player.getPositionVec().add(0, 199, 0))))
			multiplier *= 1-mc.world.getRainStrength(partialTicks);

		matrix.rotate(new Quaternion(0f, (float)myRotationalPhi, 0f, true));

		//Draw Rings
		if(hasRings) {
			matrix.push();
			matrix.rotate(new Quaternion( 0f, 90f, 0f, true));

			size = 100;
			double ringDist = 0;
			mc.getTextureManager().bindTexture(DimensionProperties.planetRings);

			matrix.rotate(new Quaternion(70, 0, 0, true));
			matrix.translate(0, -10, 0);

			RenderSystem.color4f(ringColor[0], ringColor[1], ringColor[2],multiplier);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, ringDist, -size).tex(1.0f, 0.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, ringDist, -size).tex(0.0f, 0.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, ringDist, size).tex(0.0f, 1.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, ringDist, size).tex(1.0f, 1.0f).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();

			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			matrix.push();

			matrix.rotate(new Quaternion(0f, 90f, 0f, true));
			matrix.rotate(new Quaternion(70, 0, 0, true));
			matrix.rotate(new Quaternion(0, isWarp ? 0 : celestialAngle * 360.0F, 0, true));
			matrix.translate(0, -10, 0);

			mc.getTextureManager().bindTexture(DimensionProperties.planetRingShadow);
			RenderSystem.color4f(0f, 0f, 0f,multiplier);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, ringDist, -size).tex(1.0f, 0.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, ringDist, -size).tex(0.0f, 0.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, ringDist, size).tex(0.0f, 1.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, ringDist, size).tex(1.0f, 1.0f).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();

			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		}

		if(!isWarp)
			rotateAroundAxis(matrix);


		RenderSystem.disableTexture();
		//This determines whether stars should come out regardless of thickness of atmosphere, as that is factored in later
		// - it checks if the colors of the sky are so close to black that you'd see stars, or if the atmosphere is zero and so no one gives a damn
		float f18 = mc.world.getStarBrightness(partialTicks) * f6 + ((atmosphere == 0 || (f1 < 0.09 && f2 < 0.09 && f3 < 0.09)) ? 1 : 0) - (atmosphere > 1 ? atmosphere - 1 : 0);

		if(mc.world.isRainingAt(playerPos.add(0, 199, 0)))
			f18 *= 1-mc.world.getRainStrength(partialTicks);

		if (f18 > 0.0F) {
			RenderSystem.color4f(f18, f18, f18, f18);

			matrix.push();
			if(isWarp) {
				for(int i = -3; i < 5; i++) {
					matrix.push();
					double magnitude = i*-100 + (((System.currentTimeMillis()) + 50) % 2000)/20f;
					matrix.translate(-travelDirection.getZOffset()*magnitude, 0, travelDirection.getXOffset()*magnitude);

					starGLCallList.bindBuffer();
					DefaultVertexFormats.POSITION.setupBufferState(0L);
					starGLCallList.draw(matrix.getLast().getMatrix(), 7);
					VertexBuffer.unbindBuffer();
					DefaultVertexFormats.POSITION.clearBufferState();
					matrix.pop();
				}
				//matrix.translate(((System.currentTimeMillis()/10) + 50) % 100, 0, 0);
			} else {
				starGLCallList.bindBuffer();
				DefaultVertexFormats.POSITION.setupBufferState(0L);
				starGLCallList.draw(matrix.getLast().getMatrix(), 7);
				VertexBuffer.unbindBuffer();
				DefaultVertexFormats.POSITION.clearBufferState();

				//Extra stars for low ATM
				if(atmosphere < 0.5) {
					RenderSystem.color4f(f18, f18, f18, f18/2f);
					matrix.push();
					matrix.rotate(new Quaternion(0, -90, 0, true));
					starGLCallList.bindBuffer();
					DefaultVertexFormats.POSITION.setupBufferState(0L);
					//starGLCallList.draw(matrix.getLast().getMatrix(), 7);
					VertexBuffer.unbindBuffer();
					DefaultVertexFormats.POSITION.clearBufferState();
					matrix.pop();
				}
				if(atmosphere < 0.25) {
					RenderSystem.color4f(f18, f18, f18, f18/4f);
					matrix.push();
					matrix.rotate(new Quaternion(0, 90, 0, true));
					starGLCallList.bindBuffer();
					DefaultVertexFormats.POSITION.setupBufferState(0L);
					//starGLCallList.draw(matrix.getLast().getMatrix(), 7);
					VertexBuffer.unbindBuffer();
					DefaultVertexFormats.POSITION.clearBufferState();
					matrix.pop();
				}
				RenderSystem.color4f(f18, f18, f18, f18);
			}
			matrix.pop();
		}
		RenderSystem.enableTexture();

		//--------------------------- Draw the suns --------------------
		if(!isWarp) {
			//Set sun color and distance
			drawStarAndSubStars(buffer, matrix, primaryStar, properties, solarOrbitalDistance, sunSize, sunColor, multiplier);

		}

		//Useful celestial angle for the next renders
		float celestialAngleDegrees = 360 * celestialAngle;

		//For these parts only render if the atmosphere is below a certain threshold (SHP atmosphere)
		if (DimensionProperties.AtmosphereTypes.SUPERHIGHPRESSURE.denserThan(DimensionProperties.AtmosphereTypes.getAtmosphereTypeFromValue((int)(100 * atmosphere)))) {
			//Render the parent planet
			if (isMoon) {

				//Do a whole lotta math to figure out where the parent planet is supposed to be
				float planetPositionTheta = AstronomicalBodyHelper.getParentPlanetThetaFromMoon(properties.rotationalPeriod, properties.orbitalDist, parentProperties.gravitationalMultiplier, myTheta, properties.baseOrbitTheta);

				matrix.rotate(new Quaternion(0, 0, (float)myPhi, true));
				matrix.rotate(new Quaternion(planetPositionTheta, 0f, 0f, true));
				rotateAroundAntiAxis();

				float phiAngle = (float) ((myPhi) * Math.PI / 180f);

				//Close enough approximation, I missed something but seems to off by no more than 30*
				//Nobody will look
				double x = MathHelper.sin(phiAngle) * MathHelper.cos((float) myTheta);
				double y = -MathHelper.sin((float) myTheta);
				double rotation = -Math.PI / 2f + Math.atan2(x, y) - (myTheta - Math.PI) * MathHelper.sin(phiAngle);

				//Draw Rings
				//Technically these should be BEFORE the planet position theta rotate call, with their own, but it keeps crashing due to something dumb and I don't want to bother
				//So we have the hacky internal stuff
				if(parentHasRings) {
					matrix.push();

					//Semihacky rotation stuff to keep rings synced to a different rotation than planet in the sky
					matrix.rotate(new Quaternion(-planetPositionTheta + ((float) (myTheta * 180f / Math.PI) % 360f), 0f, 0f, true));

					matrix.rotate(new Quaternion( 0f, 90f, 0f, true));

					size = 100;
					double ringDist = 0;
					mc.getTextureManager().bindTexture(DimensionProperties.planetRings);

					matrix.rotate(new Quaternion(70, 0, 0, true));
					matrix.translate(0, -10, 50);

					RenderSystem.color4f(parentRingColor[0], parentRingColor[1], parentRingColor[2],multiplier);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, ringDist, -size).tex(1.0f, 0.0f).endVertex();
					zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, ringDist, -size).tex(0.0f, 0.0f).endVertex();
					zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, ringDist, size).tex(0.0f, 1.0f).endVertex();
					zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, ringDist, size).tex(1.0f, 1.0f).endVertex();
					Tessellator.getInstance().draw();
					matrix.pop();

					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					matrix.push();

					//Semihacky rotation stuff to keep rings synced to a different rotation than planet in the sky
					matrix.rotate(new Quaternion(-planetPositionTheta + ((float) (myTheta * 180f / Math.PI) % 360f), 0f, 0f, true));

					matrix.rotate(new Quaternion( 0f, 90f, 0f, true));
					matrix.rotate(new Quaternion(70, 0, 0, true));
					matrix.translate(0, -10, 50);

					mc.getTextureManager().bindTexture(DimensionProperties.planetRingShadow);
					RenderSystem.color4f(0f, 0f, 0f,1);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, ringDist, -size).tex(1.0f, 0.0f).endVertex();
					zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, ringDist, -size).tex(0.0f, 0.0f).endVertex();
					zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, ringDist, size).tex(0.0f, 1.0f).endVertex();
					zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, ringDist, size).tex(1.0f, 1.0f).endVertex();
					Tessellator.getInstance().draw();
					matrix.pop();

					RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				}

				float flippedPlanetPositionTheta = 360 - planetPositionTheta;

				//This is for non-sunset stuff
				float alpha2 = atmosphere >= 2 ? 0 : 1;
				//Color sunset planets in the sunset zone by fog and atmosphere color
				//This math is one hell of a trip. Very very annoying to play with. Don't touch without knowledge of what it's doing
				if (afloat != null && ((180 < celestialAngleDegrees && celestialAngleDegrees < flippedPlanetPositionTheta) || (180 > celestialAngleDegrees && celestialAngleDegrees < flippedPlanetPositionTheta)))
					shadowColorMultiplier = new float[]{f1, f2, f3};
				else if (afloat != null && (planetPositionTheta < 105 || planetPositionTheta > 255)) {
					shadowColorMultiplier = afloat;
					shadowColorMultiplier = new float[]{shadowColorMultiplier[0] * (1 - multiplier) + f1 * multiplier, shadowColorMultiplier[1] * (1 - multiplier) + f2 * multiplier, shadowColorMultiplier[2] * (1 - multiplier) + f3 * multiplier};
				}

				renderPlanet(buffer, matrix, parentProperties, planetOrbitalDistance, multiplier, rotation, false, false, (float)Math.pow(parentProperties.getGravitationalMultiplier(), 0.4), shadowColorMultiplier, alpha2);
				matrix.pop();
			}

			//This needs to exist specifically for init purposes
			//The overworld literally breaks without it
			shadowColorMultiplier[0] = 1.000001f * shadowColorMultiplier[0];

			for (DimensionProperties moons : children) {

				float planetPositionTheta = (float)((partialTicks * moons.orbitTheta + ((1 - partialTicks) * moons.prevOrbitalTheta)) * 180F / Math.PI);
				float flippedPlanetPositionTheta = 360 - planetPositionTheta;

				matrix.rotate(new Quaternion(0f, 0f, (float)moons.orbitalPhi, true));
				matrix.rotate(new Quaternion(planetPositionTheta, 0f, 0f, true));

				//Close enough approximation, I missed something but seems to off by no more than 30*
				//Nobody will look
				float phiAngle = (float) ((moons.orbitalPhi) * Math.PI / 180f);
				double x = -MathHelper.sin(phiAngle) * MathHelper.cos((float) moons.orbitTheta);
				double y = MathHelper.sin((float) moons.orbitTheta);
				double rotation = (-Math.PI / 2f + Math.atan2(x, y) - (moons.orbitTheta - Math.PI) * MathHelper.sin(phiAngle)) + Math.PI;

				//This is for non-sunset stuff
				float alpha2 = atmosphere >= 2 ? 0 : 1;
				//Color sunset planets in the sunset zone by fog and atmosphere color
				//This math is one hell of a trip. Very very annoying to play with. Don't touch without knowledge of what it's doing
				if (afloat != null && ((180 < celestialAngleDegrees && celestialAngleDegrees < flippedPlanetPositionTheta) || (180 > celestialAngleDegrees && celestialAngleDegrees < flippedPlanetPositionTheta)))
					shadowColorMultiplier = new float[]{f1, f2, f3};
				else if (afloat != null && (planetPositionTheta < 105 || planetPositionTheta > 255)) {
					shadowColorMultiplier = afloat;
					shadowColorMultiplier = new float[]{shadowColorMultiplier[0] * (1 - multiplier) + f1 * multiplier, shadowColorMultiplier[1] * (1 - multiplier) + f2 * multiplier, shadowColorMultiplier[2] * (1 - multiplier) + f3 * multiplier};
				}

				renderPlanet(buffer, matrix, moons, moons.getParentOrbitalDistance(), multiplier, rotation, moons.hasAtmosphere(), moons.hasRings, (float)Math.pow(moons.gravitationalMultiplier, 0.4), shadowColorMultiplier, alpha2);
				matrix.pop();
			}
		}

		RenderSystem.enableTexture();


		RenderSystem.enableTexture();

		if(properties.isAsteroid()) {
			mc.getTextureManager().bindTexture(asteroid1);
			RenderSystem.color3f(1, 1, 1);
			glSkyList3.bindBuffer();
			DefaultVertexFormats.POSITION_TEX.setupBufferState(0L);
			glSkyList3.draw(matrix.getLast().getMatrix(), 7);
			VertexBuffer.unbindBuffer();
			DefaultVertexFormats.POSITION_TEX.clearBufferState();

			matrix.push();
			matrix.rotate(new Quaternion(18f, 72f,0f, true));
			mc.getTextureManager().bindTexture(asteroid2);
			glSkyList3.bindBuffer();
			DefaultVertexFormats.POSITION_TEX.setupBufferState(0L);
			glSkyList3.draw(matrix.getLast().getMatrix(), 7);
			VertexBuffer.unbindBuffer();
			DefaultVertexFormats.POSITION_TEX.clearBufferState();
			matrix.rotate(new Quaternion(18f, 72f,0f, true));
			mc.getTextureManager().bindTexture(asteroid3);
			glSkyList3.bindBuffer();
			DefaultVertexFormats.POSITION_TEX.setupBufferState(0L);
			glSkyList3.draw(matrix.getLast().getMatrix(), 7);
			VertexBuffer.unbindBuffer();
			DefaultVertexFormats.POSITION_TEX.clearBufferState();
			matrix.pop();
		}

		RenderSystem.depthMask(true);

		RocketEventHandler.onPostWorldRender(matrix, partialTicks);
		//Fix player/items going transparent
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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

	protected void renderPlanet(BufferBuilder buffer, MatrixStack matrix, DimensionProperties properties, float planetOrbitalDistance, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, boolean hasRing, float gravitationalMultiplier, float[] shadowColorMultiplier, float alphaMultiplier2) {
		renderPlanet2(buffer, matrix, properties, 20f*AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance) * gravitationalMultiplier, alphaMultiplier, shadowAngle, hasRing, shadowColorMultiplier, alphaMultiplier2);
	}

	protected void renderPlanet2(BufferBuilder buffer, MatrixStack matrix, DimensionProperties properties, float size, float alphaMultiplier, double shadowAngle, boolean hasRing, float[] shadowColorMultiplier, float alphaMultiplier2) {
		ResourceLocation icon = getTextureForPlanet(properties);
		boolean hasAtmosphere = properties.hasAtmosphere();
		boolean hasDecorators = properties.hasDecorators();
		boolean gasGiant = properties.isGasGiant();
		float[] skyColor = properties.skyColor;
		float[] ringColor = properties.skyColor;

		renderPlanetPubHelper(buffer, matrix, icon, 0, 0, -20, size*0.2f, alphaMultiplier, shadowAngle, hasAtmosphere, skyColor, ringColor, gasGiant, hasRing, hasDecorators, shadowColorMultiplier, alphaMultiplier2);
	}

	protected void rotateAroundAxis(MatrixStack matrix) {
		Vector3F<Float> axis = getRotateAxis();
		float x = getSkyRotationAmount() * axis.x ,y = getSkyRotationAmount() * axis.y,z = getSkyRotationAmount() * axis.z;
		matrix.rotate(new Quaternion(x,y,z, false));
	}

	protected void rotateAroundAntiAxis() {
		Vector3F<Float> axis = getRotateAxis();
		GL11.glRotatef(-getSkyRotationAmount() * 360.0F, axis.x, axis.y, axis.z);
	}

	protected float getSkyRotationAmount() {
		return celestialAngle;
	}

	protected Vector3F<Float> getRotateAxis() {
		return axis;
	}

	public static void renderPlanetPubHelper(BufferBuilder buffer, MatrixStack matrix, ResourceLocation icon, int locationX, int locationY, double zLevel, float size, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing, boolean hasDecorators,  float[] shadowColorMultiplier, float alphaMultiplier2) {
		RenderSystem.enableBlend();

		//Set planet Orbiting distance; size

		float f14 = 1f;//(float)(l + 0) / 4.0F;
		float f15 = 0f;//(float)(i1 + 0) / 2.0F;


		matrix.push();
		matrix.translate(locationX, zLevel, locationY);

		if (hasDecorators) {
			//ATM Glow
			matrix.push();
			matrix.rotate(new Quaternion(0, (float) (90-shadowAngle* 180/Math.PI), 0, true));

			//Rings
			if(hasRing) {
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				RenderSystem.color4f(ringColor[0], ringColor[1], ringColor[2], alphaMultiplier*0.2f);
				float ringSize = size *1.4f;
				Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRings);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -ringSize, zLevel-0.01f, ringSize).tex(f15, f14).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, ringSize, zLevel-0.01f, ringSize).tex(f14, f14).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, ringSize, zLevel-0.01f, -ringSize).tex(f14, f15).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -ringSize, zLevel-0.01f, -ringSize).tex(f15, f15).endVertex();
				Tessellator.getInstance().draw();

				RenderSystem.color4f(0f, 0f, 0f, alphaMultiplier);
				Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.planetRingShadow);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -ringSize, zLevel-0.01f, ringSize).tex(f15, f14).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, ringSize, zLevel-0.01f, ringSize).tex(f14, f15).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, ringSize, zLevel-0.01f, -ringSize).tex(f14, f15).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -ringSize, zLevel-0.01f, -ringSize).tex(f15, f15).endVertex();
				Tessellator.getInstance().draw();
			}

			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.atmGlow);

			RenderSystem.color4f(1f, 1f, 1f, alphaMultiplier);
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, zLevel+0.01f, size).tex(f15, f14).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, zLevel+0.01f, size).tex(f14, f14).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, zLevel+0.01f, -size).tex(f14, f15).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, zLevel+0.01f, -size).tex(f15, f15).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();
		}

		//End ATM glow

		Minecraft.getInstance().getTextureManager().bindTexture(icon);
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//TODO: draw sky planets

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		RenderSystem.color4f(1f, 1f, 1f, alphaMultiplier);
		zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, zLevel, size).tex(f15, f14).endVertex();
		zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, zLevel, size).tex(f14, f14).endVertex();
		zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, zLevel, -size).tex(f14, f15).endVertex();
		zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, zLevel, -size).tex(f15, f15).endVertex();
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
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, zLevel, size).tex(f15, f14).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, zLevel, size).tex(f14, f14).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, zLevel, -size).tex(f14, f15).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, zLevel, -size).tex(f15, f15).endVertex();
				Tessellator.getInstance().draw();
				//buffer.finishDrawing();

			}


			matrix.rotate(new Quaternion(0, (float) (90-shadowAngle* 180/Math.PI), 0, true));


			//Draw Shadow
			RenderSystem.clearColor(1f, 1f, 1f, 1f);
			RenderSystem.color4f(shadowColorMultiplier[0], shadowColorMultiplier[1], shadowColorMultiplier[2], alphaMultiplier2);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Minecraft.getInstance().getTextureManager().bindTexture(DimensionProperties.getShadowResource());
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderSystem.color4f(1f, 1f, 1f, alphaMultiplier);
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, zLevel-0.01f, size).tex(f15, f14).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, zLevel-0.01f, size).tex(f14, f14).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, zLevel-0.01f, -size).tex(f14, f15).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, zLevel-0.01f, -size).tex(f15, f15).endVertex();
			Tessellator.getInstance().draw();
		}

		matrix.pop();


		RenderSystem.color4f(1f, 1f, 1f, 1f);
	}

	protected void drawStarAndSubStars(BufferBuilder buffer, MatrixStack matrix, StellarBody sun, DimensionProperties properties, int solarOrbitalDistance, float sunSize, Vector3d sunColor, float multiplier) {
		drawStar(buffer, matrix, sun, properties, solarOrbitalDistance, sunSize, sunColor, multiplier);

		List<StellarBody> subStars = sun.getSubStars();

		if(subStars != null && !subStars.isEmpty()) {
			matrix.push();
			float phaseInc = 360f/subStars.size();

			for(StellarBody subStar : subStars) {
				matrix.rotate(new Quaternion(0, phaseInc, 0, true));
				matrix.push();

				matrix.rotate(new Quaternion(subStar.getStarSeparation()*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance), 0, 0, true));
				float[] color = subStar.getColor();
				drawStar(buffer, matrix, subStar , properties, solarOrbitalDistance, subStar.getSize(), new Vector3d(color[0], color[1], color[2]), multiplier);
				matrix.pop();
			}
			matrix.pop();
		}
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

			matrix.scale(scale,scale,scale);

			//Set sun color and distance
			RenderSystem.color4f((float)1, (float).5 , (float).4 ,1f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			float size = sunSize*2.5f*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance);
			//multiplier = 2;
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 0.0D, -size).tex(0.0f, 0.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 0.0D, -size).tex(1.0f, 0.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 0.0D, size).tex(1.0f, 1.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 0.0D, size).tex(0.0f, 1.0f).endVertex();
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
				matrix.rotate(new Quaternion(-80, 80, 0, true));
				matrix.rotate(new Quaternion(0, (System.currentTimeMillis() % (int)(speedMult*36000))/(100f*speedMult), 0, true));

				RenderSystem.color4f((float)1, (float).5 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				size = sunSize*20f*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance);
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 0.0D, -size).tex(0.0f, 0.0f).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 0.0D, -size).tex(1.0f, 0.0f).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 0.0D, size).tex(1.0f, 1.0f).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 0.0D, size).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				matrix.translate(0, 99.99f, 0);
				matrix.rotate(new Quaternion(-80, 80, 0, true));
				matrix.rotate(new Quaternion(0, (System.currentTimeMillis() % (int)(speedMult*360*50))/(50f*speedMult), 0,true));

				RenderSystem.color4f((float)0.8, (float).7 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				size = sunSize*15f*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance);
				//multiplier = 2;
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 0.0D, -size).tex(0.0f, 0.0f).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 0.0D, -size).tex(1.0f, 0.0f).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 0.0D, size).tex(1.0f, 1.0f).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 0.0D, size).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				matrix.push();

				matrix.translate(0, 99.98f, 0);
				matrix.rotate(new Quaternion(-80, 80, 0, true));
				matrix.rotate(new Quaternion(0, (System.currentTimeMillis() % (int)(speedMult*360*25))/(25f*speedMult), 0, true));

				RenderSystem.color4f((float)0.2, (float).4 , (float)1 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				size = sunSize*7.5f*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance);
				//multiplier = 2;
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 0.0D, -size).tex(0.0f, 0.0f).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 0.0D, -size).tex(1.0f, 0.0f).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 0.0D, size).tex(1.0f, 1.0f).endVertex();
				zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 0.0D, size).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();
			}

			RenderSystem.depthMask(true);
			RenderSystem.clearDepth(1);
			RenderSystem.depthMask(false);

		}
		else {
			mc.getTextureManager().bindTexture(TextureResources.locationSunPng);
			//Set sun color and distance
			RenderSystem.color4f((float)sunColor.x, (float)sunColor.y , (float)sunColor.z ,Math.min((multiplier)*2f,1f));
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			float size = sunSize*15f*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance);
			//multiplier = 2;
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 100.0D, -size).tex(0.0f, 0.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 100.0D, -size).tex(1.0f, 0.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, size, 100.0D, size).tex(1.0f, 1.0f).endVertex();
			zmaster587.libVulpes.render.RenderHelper.vertexPos(matrix, buffer, -size, 100.0D, size).tex(0.0f, 1.0f).endVertex();
			Tessellator.getInstance().draw();
		}
	}
}
