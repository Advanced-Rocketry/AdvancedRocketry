package zmaster587.advancedRocketry.client.render.planet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

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

public class RenderAsteroidSky implements ISkyRenderer { //extends IRenderHandler {


	private VertexBuffer starGLCallList;
	private VertexBuffer glSkyList;
	private VertexBuffer glSkyList2;
	private VertexBuffer glSkyList3;
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
		
		
		glSkyList3 = new VertexBuffer(DefaultVertexFormats.POSITION_TEX);
		this.renderAsteroids(buffer);
		glSkyList3.upload(buffer);
	}

	Minecraft mc = Minecraft.getInstance();

	
	private void renderAsteroids(BufferBuilder buffer)
	{
		Random random = new Random(10843L);

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
					buffer.pos(d5 + d24, d6 + d22, d7 + d25).tex( (float)(d18/(size*2) + .5f),  (float)(d19/(size*2) +.5f) ).color(r, g, b, 1f).endVertex();
				}
			}
		}			
		
		buffer.finishDrawing();
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
		buffer.finishDrawing();
	}

	
	public void render(MatrixStack matrix, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		DimensionManager dimensionMgr = DimensionManager.getInstance();
		ClientWorld world = mc.world;
		
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
		boolean parentPlanetHasDecorator = true;
		boolean parentHasRings = false;
		DimensionProperties parentProperties = null;
		DimensionProperties properties;
		Direction travelDirection = null;
		ResourceLocation parentPlanetIcon = null;
		List<DimensionProperties> children;
		StellarBody primaryStar = DimensionManager.getSol();
		celestialAngle = mc.world.getCelestialAngleRadians(partialTicks);
		BlockPos playerPos = new BlockPos(mc.player.getPositionVec());

		Vector3d sunColor;

		if(dimensionMgr.isDimensionCreated(mc.world)) {

			properties = DimensionManager.getInstance().getDimensionProperties(mc.world);
			

			atmosphere = properties.getAtmosphereDensityAtHeight(mc.getRenderViewEntity().getPosY());//planetaryProvider.getAtmosphereDensityFromHeight(mc.getRenderViewEntity().posY, mc.player.getPosition());
			Direction dir = getRotationAxis(properties, playerPos);
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
			for (ResourceLocation i : properties.getChildPlanets()) {
				children.add(DimensionManager.getInstance().getDimensionProperties(i));
			}

			solarOrbitalDistance = properties.getSolarOrbitalDistance();


			if(isMoon = properties.isMoon()) {
				parentProperties = properties.getParentProperties();
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
			primaryStar = properties.getStar();
			if (primaryStar != null)
			{
				sunSize = properties.getStar().getSize();
				starSeperation = properties.getStar().getStarSeparation();
			}
			else
				primaryStar = DimensionManager.getSol();
			if(ARConfiguration.GetSpaceDimId().equals(properties.getId())) {
				isWarp = SpaceObjectManager.WARPDIMID.equals(properties.getParentPlanet());
				if(isWarp) {
					SpaceStationObject station = (SpaceStationObject) SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(playerPos);
					travelDirection = station.getForwardDirection();
				}
			}
		}
		else {
			children = new LinkedList<DimensionProperties>();
			isMoon = false;
			hasAtmosphere = DimensionManager.overworldProperties.hasAtmosphere();
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
		f1 *= atmosphere;
		f2 *= atmosphere;
		f3 *= atmosphere;

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
		float[] afloat = world.func_239132_a_().func_230492_a_(world.func_242415_f(partialTicks), partialTicks);
		float f7;
		float f8;
		float f9;
		float f10;

		if (afloat != null)
		{
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
			buffer.pos(0.0D, 100.0f, 0.0f).color(f6, f7, f8, afloat[3] * atmosphere).endVertex();
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
			RenderSystem.shadeModel(GL11.GL_FLAT);
		}

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

			f10 = 100;
			double ringDist = 0;
			mc.getTextureManager().bindTexture(DimensionProperties.planetRings);

			matrix.rotate(new Quaternion(70, 0, 0, true));
			matrix.translate(0, -10, 0);

			RenderSystem.color4f(ringColor[0], ringColor[1], ringColor[2],multiplier);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
			buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0f, 1.0f).endVertex();
			buffer.pos((double)f10, ringDist, (double)f10).tex(1.0f, 1.0f).endVertex();
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
			buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
			buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0f, 1.0f).endVertex();
			buffer.pos((double)f10, ringDist, (double)f10).tex(1.0f, 1.0f).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();

			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		}

		if(!isWarp)
			rotateAroundAxis(matrix);


		RenderSystem.disableTexture();
		float f18 = mc.world.getStarBrightness(partialTicks) * f6 * (atmosphere) + (1-atmosphere);

		if(mc.world.isRainingAt(playerPos))
			f18 *= 1-mc.world.getRainStrength(partialTicks);

		if (f18 > 0.0F)
		{
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
			}
			else {
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
					starGLCallList.draw(matrix.getLast().getMatrix(), 7);
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
					starGLCallList.draw(matrix.getLast().getMatrix(), 7);
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
		f10 = 20.0F;

		//Render the parent planet
		if(isMoon) {
			matrix.push();

			matrix.rotate(new Quaternion(0, 0, (float)myPhi, true));
			matrix.rotate(new Quaternion((float)((partialTicks*myTheta + ((1-partialTicks)*myPrevOrbitalTheta)) * 180F/Math.PI), 0f, 0f, true));

			float phiAngle = (float)((myPhi) * Math.PI/180f);

			//Close enough approximation, I missed something but seems to off by no more than 30*
			//Nobody will look
			double x = MathHelper.sin(phiAngle)*MathHelper.cos((float)myTheta);
			double y = -MathHelper.sin((float)myTheta);
			double rotation = -Math.PI/2f + Math.atan2(x, y) - (myTheta - Math.PI )*MathHelper.sin(phiAngle);

			//Draw Rings
			if(parentHasRings) {
				matrix.push();
				matrix.rotate(new Quaternion( 0f, 90f, 0f, true));

				f10 = 100;
				double ringDist = 0;
				mc.getTextureManager().bindTexture(DimensionProperties.planetRings);

				matrix.rotate(new Quaternion(70, 0, 0, true));
				matrix.translate(0, -10, 50);

				RenderSystem.color4f(parentRingColor[0], parentRingColor[1], parentRingColor[2],multiplier);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0f, 1.0f).endVertex();
				buffer.pos((double)f10, ringDist, (double)f10).tex(1.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				matrix.push();

				matrix.rotate(new Quaternion( 0f, 90f, 0f, true));
				matrix.rotate(new Quaternion(70, 0, 0, true));
				matrix.translate(0, -10, 50);

				mc.getTextureManager().bindTexture(DimensionProperties.planetRingShadow);
				RenderSystem.color4f(0f, 0f, 0f,1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				buffer.pos((double)f10, ringDist, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
				buffer.pos((double)(-f10), ringDist, (double)f10).tex(0.0f, 1.0f).endVertex();
				buffer.pos((double)f10, ringDist, (double)f10).tex(1.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();

				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			}

			assert(parentProperties != null);
			renderPlanet2(buffer, matrix, parentPlanetIcon, 0,0,-100, AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance), multiplier, rotation, hasAtmosphere, parentAtmColor, parentRingColor, isGasGiant, false);
			matrix.pop();
		}

		for(DimensionProperties moons : children) {
			matrix.push();

			moons.orbitalPhi = 10;
			double rot = ((partialTicks*moons.orbitTheta + ((1-partialTicks)*moons.prevOrbitalTheta)) * 180F/Math.PI);

			matrix.rotate(new Quaternion(0, 0, (float)moons.orbitalPhi,true));
			matrix.rotate(new Quaternion(0f, (float) rot, 0f, true));

			//Close enough approximation, I missed something but seems to off by no more than 30*
			//Nobody will look
			float phiAngle = (float)((moons.orbitalPhi) * Math.PI/180f);
			double x = -MathHelper.sin(phiAngle)*MathHelper.cos((float)moons.orbitTheta);
			double y = MathHelper.sin((float)moons.orbitTheta);
			double rotation = -Math.PI/2f + Math.atan2(x, y) - (moons.orbitTheta - Math.PI)*MathHelper.sin(phiAngle);

			renderPlanet(buffer, matrix, moons.getPlanetIcon(), (1/(float)moons.getParentOrbitalDistance())*moons.gravitationalMultiplier, multiplier, rotation, moons.hasAtmosphere(), moons.skyColor, moons.ringColor, isGasGiant, moons.hasRings());
			matrix.pop();
		}

		RenderSystem.enableFog();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();

		matrix.pop();

		RenderSystem.enableTexture();
		
		mc.getTextureManager().bindTexture(asteroid1);
		RenderSystem.color3f(1, 1, 1);
		glSkyList3.bindBuffer();
		DefaultVertexFormats.POSITION_TEX.setupBufferState(0L);
		glSkyList3.draw(matrix.getLast().getMatrix(), 7);
		VertexBuffer.unbindBuffer();
		DefaultVertexFormats.POSITION_TEX.clearBufferState();
		
		matrix.push();
		GL11.glRotatef(90, 0.2f, 0.8f, 0);
		mc.getTextureManager().bindTexture(asteroid2);
		glSkyList3.bindBuffer();
		DefaultVertexFormats.POSITION_TEX.setupBufferState(0L);
		glSkyList3.draw(matrix.getLast().getMatrix(), 7);
		VertexBuffer.unbindBuffer();
		DefaultVertexFormats.POSITION_TEX.clearBufferState();
		GL11.glRotatef(90, 0.2f, 0.8f, 0);
		mc.getTextureManager().bindTexture(asteroid3);
		glSkyList3.bindBuffer();
		DefaultVertexFormats.POSITION_TEX.setupBufferState(0L);
		glSkyList3.draw(matrix.getLast().getMatrix(), 7);
		VertexBuffer.unbindBuffer();
		DefaultVertexFormats.POSITION_TEX.clearBufferState();
		matrix.pop();
		
		GL11.glDepthMask(true);


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

	protected void renderPlanet(BufferBuilder buffer, MatrixStack matrix, ResourceLocation icon, float planetOrbitalDistance, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing) {
		renderPlanet2(buffer, matrix, icon, 0, 0, -100, 10f*AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance), alphaMultiplier, shadowAngle, hasAtmosphere, skyColor, ringColor, gasGiant, hasRing);
	}

	protected void renderPlanet2(BufferBuilder buffer, MatrixStack matrix, ResourceLocation icon, int locationX, int locationY, double zLevel, float size, float alphaMultiplier, double shadowAngle, boolean hasAtmosphere, float[] skyColor, float[] ringColor, boolean gasGiant, boolean hasRing) {
		renderPlanetPubHelper(buffer, matrix, icon, locationX, locationY, zLevel, size, alphaMultiplier, shadowAngle, hasAtmosphere, skyColor, ringColor, gasGiant, hasRing, false);
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
			matrix.rotate(new Quaternion(0, (float) (90-shadowAngle* 180/Math.PI), 0, true));

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


			matrix.rotate(new Quaternion(0, (float) (90-shadowAngle* 180/Math.PI), 0, true));

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

	protected void drawStar(BufferBuilder buffer, MatrixStack matrix, int solarOrbitalDistance, float sunSize, Vector3d sunColor, float multiplier) {
		//Set sun color and distance
		RenderSystem.color4f((float)sunColor.x, (float)sunColor.y , (float)sunColor.z ,Math.min((multiplier)*2f,1f));
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
		float f10 = sunSize*30f*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance);
		//multiplier = 2;
		buffer.pos((double)(-f10), 100.0D, (double)(-f10)).tex(0.0f, 0.0f).endVertex();
		buffer.pos((double)f10, 100.0D, (double)(-f10)).tex(1.0f, 0.0f).endVertex();
		buffer.pos((double)f10, 100.0D, (double)f10).tex(1.0f, 1.0f).endVertex();
		buffer.pos((double)(-f10), 100.0D, (double)f10).tex(0.0f, 1.0f).endVertex();
		Tessellator.getInstance().draw();
	}
	
	protected void drawStarAndSubStars(BufferBuilder buffer, MatrixStack matrix, StellarBody sun, DimensionProperties properties, int solarOrbitalDistance, float sunSize, Vector3d sunColor, float multiplier)
	{
		drawStar(buffer, matrix, solarOrbitalDistance, sunSize, sunColor, multiplier);

		List<StellarBody> subStars = sun.getSubStars();
		
		if(subStars != null && !subStars.isEmpty()) {
			matrix.push();
			float phaseInc = 360/subStars.size();

			for(StellarBody subStar : subStars) {
				matrix.rotate(new Quaternion(0, phaseInc, 0, true));
				matrix.push();

				matrix.rotate(new Quaternion(subStar.getStarSeparation()*AstronomicalBodyHelper.getBodySizeMultiplier(solarOrbitalDistance), 0, 0, true));
				float color[] = subStar.getColor();
				drawStar(buffer, matrix, solarOrbitalDistance, subStar.getSize(), new Vector3d(color[0], color[1], color[2]), multiplier);
				matrix.pop();
			}
			matrix.pop();
		}
	}
}
