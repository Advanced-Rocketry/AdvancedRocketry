package zmaster587.advancedRocketry.client.render.planet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AstronomicalBodyHelper;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.Vector3F;

public class RenderSpaceSky extends RenderPlanetarySky {

	public RenderSpaceSky() {
		super();
	}

	Minecraft mc = Minecraft.getInstance();

	protected void drawStar(BufferBuilder buffer, MatrixStack matrix, StellarBody sun, DimensionProperties properties, int solarOrbitalDistance, float sunSize, Vector3d sunColor, float multiplier) {
		DimensionProperties parentProperties = properties.getParentProperties();
		if(parentProperties != null && sun != parentProperties.getStarData())
			super.drawStar(buffer, matrix, sun, properties, solarOrbitalDistance, sunSize, sunColor, multiplier);
	}
	
	@Override
	public void renderPlanet2(BufferBuilder buffer, MatrixStack matrix, DimensionProperties properties, float size, float alphaMultiplier, double shadowAngle, boolean hasRing, float[] shadowColorMultiplier, float alphaMultiplier2) {
		BlockPos playerPos = new BlockPos(mc.player.getPositionVec());
		ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(playerPos);

		if(object == null)
			return;
		float planetOrbitalDistance = object.getOrbitalDistance();
		
		if(properties.isStar()) {
			size = 10;
			RenderSystem.depthMask(true);
			RenderSystem.enableAlphaTest();
			RenderSystem.alphaFunc(GL11.GL_GREATER, 0.01f);
			float f10;
			
			mc.getTextureManager().bindTexture(TextureResources.locationBlackHole);
			matrix.push();
			matrix.rotate(new Quaternion(0, 0, 180, true));
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
			f10 = size*2f*AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);
			//multiplier = 2;
			RenderHelper.vertexPos(matrix, buffer, -f10, 0.0D, -f10).tex(0.0f, 0.0f).endVertex();
			RenderHelper.vertexPos(matrix, buffer, f10, 0.0D, -f10).tex(1.0f, 0.0f).endVertex();
			RenderHelper.vertexPos(matrix, buffer, f10, 0.0D, f10).tex(1.0f, 1.0f).endVertex();
			RenderHelper.vertexPos(matrix, buffer, -f10, 0.0D, f10).tex(0.0f, 1.0f).endVertex();
			Tessellator.getInstance().draw();
			matrix.pop();
			
			GL11.glDepthMask(false);
			for(int i = 0; i < 3; i++) {
				float speedMult = (i)*1.01f + 1;
				//Render accretion disk
				mc.getTextureManager().bindTexture(TextureResources.locationAccretionDisk);
				matrix.push();
				matrix.translate(0, 100+i*50, 0);
				matrix.rotate(new Quaternion(60, 0, 0, true));
				matrix.rotate(new Quaternion(0, (System.currentTimeMillis() % (int)(360*360*speedMult))/(360f*speedMult), 0, true));
				
				RenderSystem.color4f((float)1, (float).5 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = size*40f*AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);
				RenderHelper.vertexPos(matrix, buffer, -f10, 0.0D, -f10).tex(0.0f, 0.0f).endVertex();
				RenderHelper.vertexPos(matrix, buffer, f10, 0.0D, -f10).tex(1.0f, 0.0f).endVertex();
				RenderHelper.vertexPos(matrix, buffer, f10, 0.0D, f10).tex(1.0f, 1.0f).endVertex();
				RenderHelper.vertexPos(matrix, buffer, -f10, 0.0D, f10).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();
				
				matrix.push();
				
				matrix.translate(0, 99.9f+i*50, 0);
				matrix.rotate(new Quaternion(60, 0, 0, true));
				matrix.rotate(new Quaternion(0, (System.currentTimeMillis() % (int)(360*200*speedMult))/(200f*speedMult), 0, true));
				
				RenderSystem.color4f((float)0.8, (float).7 , (float).4 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = size*30f*AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);
				//multiplier = 2;
				RenderHelper.vertexPos(matrix, buffer, -f10, 0.0D, -f10).tex(0.0f, 0.0f).endVertex();
				RenderHelper.vertexPos(matrix, buffer, f10, 0.0D, -f10).tex(1.0f, 0.0f).endVertex();
				RenderHelper.vertexPos(matrix, buffer, f10, 0.0D, f10).tex(1.0f, 1.0f).endVertex();
				RenderHelper.vertexPos(matrix, buffer, -f10, 0.0D, f10).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();
				
				matrix.push();
				
				matrix.translate(0, 99.8f+i*50, 0);
				matrix.rotate(new Quaternion(60, 0, 0, true));
				matrix.rotate(new Quaternion( 0, (System.currentTimeMillis() % (int)(36000*speedMult))/(100f*speedMult), 0, true));
				
				RenderSystem.color4f((float)0.2, (float).4 , (float)1 ,1f);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);	
				f10 = size*15f*AstronomicalBodyHelper.getBodySizeMultiplier(planetOrbitalDistance);
				//multiplier = 2;
				RenderHelper.vertexPos(matrix, buffer, -f10, 0.0D, -f10).tex(0.0f, 0.0f).endVertex();
				RenderHelper.vertexPos(matrix, buffer, f10, 0.0D, -f10).tex(1.0f, 0.0f).endVertex();
				RenderHelper.vertexPos(matrix, buffer, f10, 0.0D, f10).tex(1.0f, 1.0f).endVertex();
				RenderHelper.vertexPos(matrix, buffer, -f10, 0.0D, f10).tex(0.0f, 1.0f).endVertex();
				Tessellator.getInstance().draw();
				matrix.pop();
			}
			matrix.pop();
			return;
		}
		


		float[] atmColor = properties.skyColor;

		matrix.push();
		//GL11.glDisable(GL11.GL_BLEND);


		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		RenderSystem.disableFog();

		//GL11.glDisable(GL11.GL_LIGHTING);

		RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		mc.getTextureManager().bindTexture(getTextureForPlanet(properties));

		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
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

		RenderSystem.color4f(1f, 1f, 1f, alphaMultiplier);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.vertexPos(matrix, buffer, -f10, -10.0D, f10).tex(f16, f17).endVertex();
		RenderHelper.vertexPos(matrix, buffer, f10, -10.0D, f10).tex(f14, f17).endVertex();
		RenderHelper.vertexPos(matrix, buffer, f10, -10.0D, -f10).tex(f14, f15).endVertex();
		RenderHelper.vertexPos(matrix, buffer, -f10, -10.0D, -f10).tex(f16, f15).endVertex();

		Tessellator.getInstance().draw();
		GL11.glPopAttrib();

		if(properties.hasDecorators() ) {

			//Draw atmosphere if applicable
			if(properties.isGasGiant()) {
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
				mc.getTextureManager().bindTexture(DimensionProperties.getAtmosphereLEOResource());
				RenderSystem.color4f(atmColor[0], atmColor[1], atmColor[2], 0.5f);

				double dist = -5D - 4*(planetOrbitalDistance)/200D;
				double scalingMult = 1D - 0.9*(planetOrbitalDistance)/200D;

				float r = 0.5f;
				float g = 0.5f;
				float b = 1f;
				float a = 0.08f;
				
				int maxAmt = 6;
				float lng = (float) (System.currentTimeMillis()/100000d % 1);
				for(int i = 0; i < maxAmt; i++) {
					r = 0.05f*(maxAmt-i/6f);
					g = .4f*(i/6f);
					b = 1f;
					a = 0.4f;
					//IDK it looks pretty
					Xoffset = lng*(i-(maxAmt/4f));
					float Yoffset = -lng*i;

					f14 = i + Yoffset;
					f15 = 0f + Yoffset;
					f16 = 0f + Xoffset;
					f17 = i + Xoffset;

					RenderHelper.renderTopFaceWithUV(matrix, buffer, -10D + i*scalingMult, -f10, -f10, 0, 0, f14, f15, f16, f17, r,g,b,a);
					RenderHelper.renderTopFaceWithUV(matrix, buffer, -10D + i*scalingMult, 0, 0, f10, f10, f14, f15, f16, f17, r,g,b,a);
					RenderHelper.renderTopFaceWithUV(matrix, buffer, -10D + i*scalingMult, -f10, 0, 0, f10, f14, f15, f16, f17, r,g,b,a);
					RenderHelper.renderTopFaceWithUV(matrix, buffer, -10D + i*scalingMult, 0, -f10, f10, 0, f14, f15, f16, f17, r,g,b,a);
				}

				Tessellator.getInstance().draw();


				RenderSystem.disableTexture();
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
				r = 0.5f;
				g = 0.5f;
				b = 1f;
				a = 0.08f;

				for(int i = 0; i < 5 ; i++) {
					RenderHelper.renderTopFaceWithUV(matrix, buffer, dist + i*scalingMult, -f10, -f10, 0, 0, f14, f15, f16, f17, r,g,b,a);
					RenderHelper.renderTopFaceWithUV(matrix, buffer, dist + i*scalingMult, 0, 0, f10, f10, f14, f15, f16, f17, r,g,b,a);
					RenderHelper.renderTopFaceWithUV(matrix, buffer, dist + i*scalingMult, -f10, 0, 0, f10, f14, f15, f16, f17, r,g,b,a);
					RenderHelper.renderTopFaceWithUV(matrix, buffer, dist + i*scalingMult, 0, -f10, f10, 0, f14, f15, f16, f17, r,g,b,a);
				}
				Tessellator.getInstance().draw();
				RenderSystem.enableTexture();
			}
			else if(properties.hasAtmosphere() ) {
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
				//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
				mc.getTextureManager().bindTexture(DimensionProperties.getAtmosphereLEOResource());

				float a = 0.5f;
				Xoffset = (float)((System.currentTimeMillis()/100000d % 1));

				f14 = 1f + Xoffset;
				f15 = 0f + Xoffset;
				f16 = f15;
				f17 = f14;

				RenderHelper.renderTopFaceWithUV(matrix, buffer, -10D, -f10, -f10, 0, 0, f14, f15, f16, f17, 1,1,1,a);
				RenderHelper.renderTopFaceWithUV(matrix, buffer, -10D, 0, 0, f10, f10, f14, f15, f16, f17, 1,1,1,a);
				RenderHelper.renderTopFaceWithUV(matrix, buffer, -10D, -f10, 0, 0, f10, f14, f15, f16, f17, 1,1,1,a);
				RenderHelper.renderTopFaceWithUV(matrix, buffer, -10D, 0, -f10, f10, 0, f14, f15, f16, f17, 1,1,1,a);

				Tessellator.getInstance().draw();


				RenderSystem.disableTexture();
				//GL11.glDisable(GL11.GL_BLEND);
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

				a = 0.08f;
				
				//f10 *= 100;
				double dist = -5D - 4*(planetOrbitalDistance)/200D;
				double scalingMult = 1D - 0.9*(planetOrbitalDistance)/200D;
				for(int i = 0; i < 5 ; i++) {
					RenderHelper.renderTopFaceWithUV(matrix, buffer, dist + i*scalingMult, -f10, -f10, 0, 0, f14, f15, f16, f17, atmColor[0], atmColor[1], atmColor[2], a);
					RenderHelper.renderTopFaceWithUV(matrix, buffer, dist + i*scalingMult, 0, 0, f10, f10, f14, f15, f16, f17, atmColor[0], atmColor[1], atmColor[2], a);
					RenderHelper.renderTopFaceWithUV(matrix, buffer, dist + i*scalingMult, -f10, 0, 0, f10, f14, f15, f16, f17, atmColor[0], atmColor[1], atmColor[2], a);
					RenderHelper.renderTopFaceWithUV(matrix, buffer, dist + i*scalingMult, 0, -f10, f10, 0, f14, f15, f16, f17, atmColor[0], atmColor[1], atmColor[2], a);
				}
				Tessellator.getInstance().draw();
				RenderSystem.enableTexture();
			}
		}


		RenderSystem.color4f(1f,1f,1f,1f);
		RenderSystem.enableFog();
		//GL11.glEnable(GL11.GL_LIGHTING);
		matrix.pop();
	}

	@Override
	protected Direction getRotationAxis(DimensionProperties properties,
			BlockPos pos) {
		try {
			return SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos).getForwardDirection().rotateY();
		} catch(Exception e) {
			return Direction.EAST;
		}
	}

	@Override
	protected void rotateAroundAxis(MatrixStack matrix) {
		Vector3F<Float> axis = getRotateAxis();
		//matrix.rotate(90f, axis.x, axis.y, axis.z);
		ISpaceObject obj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(new BlockPos(mc.player.getPositionVec()));
		if(obj != null) {
			matrix.rotate(new Quaternion(0f, (float) (obj.getRotation(Direction.UP)*360f), 0f, true));
			matrix.rotate(new Quaternion((float) (obj.getRotation(Direction.EAST)*360), 0, 0, true));
		}
		//matrix.rotate(360, obj.getRotation(Direction.EAST), obj.getRotation(Direction.UP), obj.getRotation(Direction.NORTH));

	}

	@Override
	protected ResourceLocation getTextureForPlanet(DimensionProperties properties) {
		return properties.getPlanetIconLEO();
	}
}
