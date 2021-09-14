package zmaster587.advancedRocketry.client.render.planet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AstronomicalBodyHelper;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.Vector3F;

public class RenderSpaceSky extends RenderPlanetarySky {

	//Mostly vanilla code
	//TODO: make usable on other planets
	public RenderSpaceSky() {
		super();
	}

	Minecraft mc = Minecraft.getMinecraft();

	protected void drawStar(BufferBuilder buffer, StellarBody sun, DimensionProperties properties, int solarOrbitalDistance, float sunSize, Vec3d sunColor, float multiplier) {
		DimensionProperties parentProperties = properties.getParentProperties();
		if(parentProperties != null && sun != parentProperties.getStarData())
			super.drawStar(buffer, sun, properties, solarOrbitalDistance, sunSize, sunColor, multiplier);
	}
	
	@Override
	public void renderPlanet2(BufferBuilder buffer, DimensionProperties properties, float size, float alphaMultiplier, double shadowAngle, boolean hasRing, float[] shadowColorMultiplier, float alphaMultiplier2) {
		//ResourceLocation icon, int locationX, int locationY, double zLevel, float planetOrbitalDistance, float alphaMultiplier, double angle, boolean hasAtmosphere, float[] atmColor, float[] ringColor, boolean isGasgiant, boolean hasRings, boolean hasDecorators) {

		ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(mc.player.getPosition());

		if(spaceObject == null)
			return;
		float planetOrbitalDistance = spaceObject.getOrbitalDistance();

		if(properties.isStar()) {
			if (properties.getStar().isBlackHole()) {
				size = 10;
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.01f);
				float f10;

				mc.renderEngine.bindTexture(TextureResources.locationBlackHole);
				GL11.glPushMatrix();
				GL11.glRotatef(180, 0, 0, 1);
				GL11.glPushMatrix();
				GL11.glTranslatef(0, 100, 0);
				float phase = -(System.currentTimeMillis() % 3600) / 3600f;
				float scale = 1 + (float) Math.sin(phase * 3.14) * 0.1f;
				phase *= 360f;
				GL11.glRotatef(phase, 0, 1, 0);

				GL11.glScaled(scale, scale, scale);

				//Set sun color and distance
				GlStateManager.color((float) 1, (float) .5, (float) .4, 1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				f10 = size * 2f * AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);
				//multiplier = 2;
				buffer.pos(-f10, 0.0D, -f10).tex(0.0D, 0.0D).endVertex();
				buffer.pos(f10, 0.0D, -f10).tex(1.0D, 0.0D).endVertex();
				buffer.pos(f10, 0.0D, f10).tex(1.0D, 1.0D).endVertex();
				buffer.pos(-f10, 0.0D, f10).tex(0.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
				GL11.glPopMatrix();

				GL11.glDepthMask(false);
				for (int i = 0; i < 3; i++) {
					float speedMult = (i) * 1.01f + 1;
					//Render accretion disk
					mc.renderEngine.bindTexture(TextureResources.locationAccretionDisk);
					GL11.glPushMatrix();
					GL11.glTranslatef(0, 100 + i * 50, 0);
					GL11.glRotatef(60, 1, 0, 0);
					GL11.glRotatef((System.currentTimeMillis() % (int) (360 * 360 * speedMult)) / (360f * speedMult), 0, 1, 0);

					GlStateManager.color((float) 1, (float) .5, (float) .4, 1f);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					f10 = size * 40f * AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);
					buffer.pos(-f10, 0.0D, -f10).tex(0.0D, 0.0D).endVertex();
					buffer.pos(f10, 0.0D, -f10).tex(1.0D, 0.0D).endVertex();
					buffer.pos(f10, 0.0D, f10).tex(1.0D, 1.0D).endVertex();
					buffer.pos(-f10, 0.0D, f10).tex(0.0D, 1.0D).endVertex();
					Tessellator.getInstance().draw();
					GL11.glPopMatrix();

					GL11.glPushMatrix();

					GL11.glTranslatef(0, 99.9f + i * 50, 0);
					GL11.glRotatef(60, 1, 0, 0);
					GL11.glRotatef((System.currentTimeMillis() % (int) (360 * 200 * speedMult)) / (200f * speedMult), 0, 1, 0);

					GlStateManager.color((float) 0.8, (float) .7, (float) .4, 1f);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					f10 = size * 30f * AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);
					//multiplier = 2;
					buffer.pos(-f10, 0.0D, -f10).tex(0.0D, 0.0D).endVertex();
					buffer.pos(f10, 0.0D, -f10).tex(1.0D, 0.0D).endVertex();
					buffer.pos(f10, 0.0D, f10).tex(1.0D, 1.0D).endVertex();
					buffer.pos(-f10, 0.0D, f10).tex(0.0D, 1.0D).endVertex();
					Tessellator.getInstance().draw();
					GL11.glPopMatrix();

					GL11.glPushMatrix();

					GL11.glTranslatef(0, 99.8f + i * 50, 0);
					GL11.glRotatef(60, 1, 0, 0);
					GL11.glRotatef((System.currentTimeMillis() % (int) (36000 * speedMult)) / (100f * speedMult), 0, 1, 0);

					GlStateManager.color((float) 0.2, (float) .4, (float) 1, 1f);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					f10 = size * 15f * AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);
					//multiplier = 2;
					buffer.pos(-f10, 0.0D, -f10).tex(0.0D, 0.0D).endVertex();
					buffer.pos(f10, 0.0D, -f10).tex(1.0D, 0.0D).endVertex();
					buffer.pos(f10, 0.0D, f10).tex(1.0D, 1.0D).endVertex();
					buffer.pos(-f10, 0.0D, f10).tex(0.0D, 1.0D).endVertex();
					Tessellator.getInstance().draw();
					GL11.glPopMatrix();
				}
				GL11.glPopMatrix();
			} else {
				mc.renderEngine.bindTexture(TextureResources.locationSunPng);
				//Set sun color and distance
				GlStateManager.color(properties.getStar().getColor()[0], properties.getStar().getColor()[1], properties.getStar().getColor()[2], 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				float f10 = properties.getStar().getSize() * 15f * AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);
				//multiplier = 2;
				buffer.pos(-f10, 100.0D, -f10).tex(0.0D, 0.0D).endVertex();
				buffer.pos(f10, 100.0D, -f10).tex(1.0D, 0.0D).endVertex();
				buffer.pos(f10, 100.0D, f10).tex(1.0D, 1.0D).endVertex();
				buffer.pos(-f10, 100.0D, f10).tex(0.0D, 1.0D).endVertex();
				Tessellator.getInstance().draw();
			}
			return;
		}
		


		float[] atmColor = properties.skyColor;

		GL11.glPushMatrix();
		//GL11.glDisable(GL11.GL_BLEND);

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GlStateManager.disableFog();

		//GL11.glDisable(GL11.GL_LIGHTING);

		GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		mc.renderEngine.bindTexture(getTextureForPlanet(properties));

		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//int k = mc.theWorld.getMoonPhase();
		//int l = k % 4;
		//int i1 = k / 4 % 2;

		//Set planet Orbiting distance; size
		float f10 = 100f*AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);

		float Xoffset = (float)((System.currentTimeMillis()/1000000d % 1));

		float f14 = 1f + Xoffset;
		float f15 = 0f + Xoffset;
		float f16 = f15;
		float f17 = f14;

		//TODO: draw sky planets

		GlStateManager.color(1f, 1f, 1f, alphaMultiplier);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(-f10, -10.0D, f10).tex(f16, f17).endVertex();
		buffer.pos(f10, -10.0D, f10).tex(f14, f17).endVertex();
		buffer.pos(f10, -10.0D, -f10).tex(f14, f15).endVertex();
		buffer.pos(-f10, -10.0D, -f10).tex(f16, f15).endVertex();

		Tessellator.getInstance().draw();
		GL11.glPopAttrib();

		if(properties.hasDecorators()) {

			//Draw atmosphere if applicable
			if(properties.isGasGiant()) {
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				mc.renderEngine.bindTexture(DimensionProperties.getAtmosphereLEOResource());
				GlStateManager.color(atmColor[0], atmColor[1], atmColor[2], 0.5f);

				double dist = -5D - 4*(planetOrbitalDistance)/200D;
				double scalingMult = 1D - 0.9*(planetOrbitalDistance)/200D;

				int maxAmt = 6;
				float lng = (float) (Minecraft.getSystemTime()/100000d % 1);
				for(int i = 0; i < maxAmt; i++) {
					//IDK it looks pretty
					Xoffset = lng*(i-(maxAmt/4f));
					float Yoffset = -lng*i;

					f14 = i + Yoffset;
					f15 = 0f + Yoffset;
					f16 = 0f + Xoffset;
					f17 = i + Xoffset;

					RenderHelper.renderTopFaceWithUV(buffer, -10D + i*scalingMult, -f10, -f10, 0, 0, f14, f15, f16, f17);
					RenderHelper.renderTopFaceWithUV(buffer, -10D + i*scalingMult, 0, 0, f10, f10, f14, f15, f16, f17);
					RenderHelper.renderTopFaceWithUV(buffer, -10D + i*scalingMult, -f10, 0, 0, f10, f14, f15, f16, f17);
					RenderHelper.renderTopFaceWithUV(buffer, -10D + i*scalingMult, 0, -f10, f10, 0, f14, f15, f16, f17);
				}

				Tessellator.getInstance().draw();


				GlStateManager.disableTexture2D();
				//GL11.glDisable(GL11.GL_BLEND);
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				GlStateManager.color(atmColor[0], atmColor[1], atmColor[2], 0.08f);


				for(int i = 0; i < 5 ; i++) {
					RenderHelper.renderTopFaceWithUV(buffer, dist + i*scalingMult, -f10, -f10, 0, 0, f14, f15, f16, f17);
					RenderHelper.renderTopFaceWithUV(buffer, dist + i*scalingMult, 0, 0, f10, f10, f14, f15, f16, f17);
					RenderHelper.renderTopFaceWithUV(buffer, dist + i*scalingMult, -f10, 0, 0, f10, f14, f15, f16, f17);
					RenderHelper.renderTopFaceWithUV(buffer, dist + i*scalingMult, 0, -f10, f10, 0, f14, f15, f16, f17);
				}
				Tessellator.getInstance().draw();
				GlStateManager.enableTexture2D();
			}
			else if(properties.hasAtmosphere()) {
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				mc.renderEngine.bindTexture(DimensionProperties.getAtmosphereLEOResource());
				GlStateManager.color(1,1,1, 0.5f);

				Xoffset = (float)((System.currentTimeMillis()/100000d % 1));

				f14 = 1f + Xoffset;
				f15 = 0f + Xoffset;
				f16 = f15;
				f17 = f14;

				RenderHelper.renderTopFaceWithUV(buffer, -10D, -f10, -f10, 0, 0, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(buffer, -10D, 0, 0, f10, f10, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(buffer, -10D, -f10, 0, 0, f10, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(buffer, -10D, 0, -f10, f10, 0, f14, f15, f16, f17);

				Tessellator.getInstance().draw();


				GlStateManager.disableTexture2D();
				//GL11.glDisable(GL11.GL_BLEND);
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

				GlStateManager.color(atmColor[0], atmColor[1], atmColor[2], 0.08f);
				//f10 *= 100;
				double dist = -5D - 4*(planetOrbitalDistance)/200D;
				double scalingMult = 1D - 0.9*(planetOrbitalDistance)/200D;
				for(int i = 0; i < 5 ; i++) {
					RenderHelper.renderTopFaceWithUV(buffer, dist + i*scalingMult, -f10, -f10, 0, 0, f14, f15, f16, f17);
					RenderHelper.renderTopFaceWithUV(buffer, dist + i*scalingMult, 0, 0, f10, f10, f14, f15, f16, f17);
					RenderHelper.renderTopFaceWithUV(buffer, dist + i*scalingMult, -f10, 0, 0, f10, f14, f15, f16, f17);
					RenderHelper.renderTopFaceWithUV(buffer, dist + i*scalingMult, 0, -f10, f10, 0, f14, f15, f16, f17);
				}
				Tessellator.getInstance().draw();
				GlStateManager.enableTexture2D();
			}
		}


		GlStateManager.color(1f,1f,1f,1f);
		GlStateManager.enableFog();
		//GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@Override
	protected EnumFacing getRotationAxis(DimensionProperties properties,
			BlockPos pos) {
		try {
			return SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos).getForwardDirection().rotateY();
		} catch(Exception e) {
			return EnumFacing.EAST;
		}
	}

	@Override
	protected void rotateAroundAxis() {
		Vector3F<Float> axis = getRotateAxis();
		//GL11.glRotatef(90f, axis.x, axis.y, axis.z);
		ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(mc.player.getPosition());
		if(spaceObject != null) {
			GL11.glRotated(spaceObject.getRotation(EnumFacing.UP)*360, 0, 1, 0);
			GL11.glRotated(spaceObject.getRotation(EnumFacing.EAST)*360, 1, 0, 0);
		}
		//GL11.glRotated(360, spaceObject.getRotation(EnumFacing.EAST), spaceObject.getRotation(EnumFacing.UP), spaceObject.getRotation(EnumFacing.NORTH));

	}

	@Override
	protected ResourceLocation getTextureForPlanet(DimensionProperties properties) {
		return properties.getPlanetIconLEO();
	}
}
