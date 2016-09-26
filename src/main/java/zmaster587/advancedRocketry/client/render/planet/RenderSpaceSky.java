package zmaster587.advancedRocketry.client.render.planet;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderSpaceSky extends RenderPlanetarySky {

	//Mostly vanilla code
	//TODO: make usable on other planets
	public RenderSpaceSky() {
		super();
	}

	Minecraft mc = Minecraft.getMinecraft();

	@Override
	protected ForgeDirection getRotationAxis(DimensionProperties properties, int x, int z) {
		try {
			return SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x,z).getForwardDirection().getRotation(ForgeDirection.UP);
		} catch(NullPointerException e) {
			return ForgeDirection.EAST;
		}
	}
	
	@Override
	protected void renderPlanet(Tessellator tessellator1, ResourceLocation icon, float planetOrbitalDistance, float alphaMultiplier, boolean hasAtmosphere, boolean isGasgiant) {


		GL11.glPushMatrix();
		//GL11.glDisable(GL11.GL_BLEND);

		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_FOG);

		//GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		mc.renderEngine.bindTexture(icon);

		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//int k = mc.theWorld.getMoonPhase();
		//int l = k % 4;
		//int i1 = k / 4 % 2;

		//Set planet Orbiting distance; size
		float f10 = 100f*(200-planetOrbitalDistance)/100f;

		float Xoffset = (float)((System.currentTimeMillis()/1000000d % 1));

		float f14 = 1f + Xoffset;
		float f15 = 0f + Xoffset;
		float f16 = f15;
		float f17 = f14;

		//TODO: draw sky planets

		tessellator1.startDrawingQuads();

		tessellator1.setColorRGBA_F(1f, 1f, 1f, alphaMultiplier);

		tessellator1.addVertexWithUV((double)(-f10), -10.0D, (double)f10, (double)f16, (double)f17);
		tessellator1.addVertexWithUV((double)f10, -10.0D, (double)f10, (double)f14, (double)f17);
		tessellator1.addVertexWithUV((double)f10, -10.0D, (double)(-f10), (double)f14, (double)f15);
		tessellator1.addVertexWithUV((double)(-f10), -10.0D, (double)(-f10), (double)f16, (double)f15);



		tessellator1.draw();
		GL11.glPopAttrib();

		//Draw atmosphere if applicable
		if(isGasgiant) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

			tessellator1.startDrawingQuads();
			mc.renderEngine.bindTexture(DimensionProperties.getAtmosphereLEOResource());
			
			int maxAmt = 6;
			float lng = (float) (Minecraft.getSystemTime()/100000d % 1);
			for(int i = 0; i < maxAmt; i++) {
				tessellator1.setColorRGBA_F(0.05f*(maxAmt-i/6f), .4f*(i/6f), 1f, 0.4f);

				//IDK it looks pretty
				Xoffset = lng*(i-(maxAmt/4f));
				float Yoffset = -lng*i;

				f14 = i + Yoffset;
				f15 = 0f + Yoffset;
				f16 = 0f + Xoffset;
				f17 = i + Xoffset;

				
				
				RenderHelper.renderTopFaceWithUV(tessellator1, -10D +i, -f10, -f10, 0, 0, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(tessellator1, -10D+ i, 0, 0, f10, f10, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(tessellator1, -10D+ i, -f10, 0, 0, f10, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(tessellator1, -10D+ i, 0, -f10, f10, 0, f14, f15, f16, f17);
			}

			tessellator1.draw();


			GL11.glDisable(GL11.GL_TEXTURE_2D);
			//GL11.glDisable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			tessellator1.startDrawingQuads();
			tessellator1.setColorRGBA_F(0.5f,0.5f,1, 0.08f);

			f10 *= 100;
			for(int i = 0; i < 5 ; i++) {
				RenderHelper.renderTopFaceWithUV(tessellator1, -5D + i, -f10, -f10, 0, 0, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(tessellator1, -5D + i, 0, 0, f10, f10, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(tessellator1, -5D + i, -f10, 0, 0, f10, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(tessellator1, -5D + i, 0, -f10, f10, 0, f14, f15, f16, f17);
			}
			tessellator1.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		else if(hasAtmosphere) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			//GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

			tessellator1.startDrawingQuads();
			mc.renderEngine.bindTexture(DimensionProperties.getAtmosphereLEOResource());
			tessellator1.setColorRGBA_F(1f, 1f, 1f, .8f);

			Xoffset = (float)((System.currentTimeMillis()/100000d % 1));

			f14 = 1f + Xoffset;
			f15 = 0f + Xoffset;
			f16 = f15;
			f17 = f14;

			RenderHelper.renderTopFaceWithUV(tessellator1, -10D, -f10, -f10, 0, 0, f14, f15, f16, f17);
			RenderHelper.renderTopFaceWithUV(tessellator1, -10D, 0, 0, f10, f10, f14, f15, f16, f17);
			RenderHelper.renderTopFaceWithUV(tessellator1, -10D, -f10, 0, 0, f10, f14, f15, f16, f17);
			RenderHelper.renderTopFaceWithUV(tessellator1, -10D, 0, -f10, f10, 0, f14, f15, f16, f17);

			tessellator1.draw();


			GL11.glDisable(GL11.GL_TEXTURE_2D);
			//GL11.glDisable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			tessellator1.startDrawingQuads();
			tessellator1.setColorRGBA_F(1,1,1, 0.08f);

			f10 *= 100;
			for(int i = 0; i < 5 ; i++) {
				RenderHelper.renderTopFaceWithUV(tessellator1, -5D + i, -f10, -f10, 0, 0, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(tessellator1, -5D + i, 0, 0, f10, f10, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(tessellator1, -5D + i, -f10, 0, 0, f10, f14, f15, f16, f17);
				RenderHelper.renderTopFaceWithUV(tessellator1, -5D + i, 0, -f10, f10, 0, f14, f15, f16, f17);
			}
			tessellator1.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}



		GL11.glEnable(GL11.GL_FOG);
		//GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getTextureForPlanet(DimensionProperties properties) {
		return properties.getPlanetIconLEO();
	}
}
