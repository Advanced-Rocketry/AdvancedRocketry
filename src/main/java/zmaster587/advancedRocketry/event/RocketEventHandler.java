package zmaster587.advancedRocketry.event;

import java.nio.IntBuffer;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLandedEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.client.render.ClientDynamicTexture;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.util.Configuration;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.ZUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RocketEventHandler extends Gui {

	private ResourceLocation background = new ResourceLocation("advancedrocketry:textures/gui/rocketHUD.png");
	private static ClientDynamicTexture earth;
	private static ClientDynamicTexture outerBounds;
	private static final int getImgSize = 512;
	private static final int outerImgSize = getImgSize/8;
	private static boolean mapReady = false;
	private static boolean mapNeedsBinding = false;
	private static IntBuffer table,outerBoundsTable;
	

	@SubscribeEvent
	public void onRocketDeorbit(RocketEvent.RocketDeOrbitingEvent event) {
		if(event.world.isRemote) {
			prepareOrbitalMap(event);

			if(!(event.world.provider instanceof IPlanetaryProvider)) {
				event.world.provider.setSkyRenderer(new RenderPlanetarySky());
			}
		}
	}

	@SubscribeEvent
	public void onRocketLand(RocketLandedEvent event) {

		if(event.world.isRemote) {
			if(!(event.world.provider instanceof IPlanetaryProvider))
				event.world.provider.setSkyRenderer(null);
			if(earth != null)
				GL11.glDeleteTextures(earth.getTextureId());
			if(outerBounds != null)
				GL11.glDeleteTextures(outerBounds.getTextureId());
			outerBounds = null;
			earth = null;
		}
	}

	@SubscribeEvent
	public void onRocketLaunch(RocketLaunchEvent event) {
		if(event.world.isRemote) {
			prepareOrbitalMap(event);
			event.world.provider.setSkyRenderer(new RenderPlanetarySky());
		}
	}

	private void prepareOrbitalMap(final RocketEvent event) {
		mapReady = false;

		//Attempt to generate everything on seperate thread
		if (earth == null) {
			earth = new ClientDynamicTexture(getImgSize,getImgSize);
			outerBounds = new ClientDynamicTexture(outerImgSize, outerImgSize);
		}
		
		//Multi thread texture creation b/c it can be expensive
		
		new Thread(new Runnable() {

			@Override
			public void run() {


				table = earth.getByteBuffer();
				outerBoundsTable = outerBounds.getByteBuffer();

				//Get the average of each edge RGB
				int topEdge[], bottomEdge[], leftEdge[], rightEdge[], total[];
				total = topEdge = bottomEdge = leftEdge = rightEdge = new int[] {0,0,0};

				for(int i = 0; i < getImgSize*getImgSize; i++) {
					//TODO: Optimize
					int xOffset = (i % getImgSize);
					int yOffset = (i / getImgSize);

					int xPosition = (int)event.entity.posX - (getImgSize/2) + xOffset;
					int zPosition = (int)event.entity.posZ - (getImgSize/2) + yOffset;
					Chunk chunk = event.world.getChunkFromBlockCoords(xPosition, zPosition);

					if(chunk.isChunkLoaded) {
						//Get Xcoord and ZCoords in the chunk

						int heightValue = chunk.getHeightValue( xPosition + (chunk.xPosition >= 0 ? - (Math.abs( chunk.xPosition )<< 4) : (Math.abs( chunk.xPosition )<< 4)), zPosition + (chunk.zPosition >= 0 ? - (Math.abs(chunk.zPosition )<< 4) : (Math.abs(chunk.zPosition )<< 4)));
						MapColor color = MapColor.airColor;
						int yPosition;

						//Get the first non-air block
						for(yPosition = heightValue; yPosition > 0; yPosition-- ) {
							if((color = event.world.getBlock(xPosition, yPosition, zPosition).getMapColor(event.world.getBlockMetadata(xPosition, yPosition, zPosition))) != MapColor.airColor) {
								break;
							}
						}

						int intColor = ( (color.colorValue & 0xFF) << 16) | ( ( color.colorValue >> 16 ) & 0xFF ) | ( color.colorValue & 0xFF00 );

						//If water get the first nonwater block
						/*if(color == MapColor.waterColor) {
					MapColor color2 = MapColor.airColor;

					for(; yPosition > 0; yPosition--) {
						if((color2 = event.world.getBlock(xPosition, yPosition, zPosition).getMapColor(event.world.getBlockMetadata(xPosition, yPosition, zPosition))) != MapColor.waterColor) {
							break;
						}
					}
					intColor = 0xff2121;

				}*/

						//Put into the table and make opaque
						table.put(i, intColor | 0xFF000000);

						//Try to get edge Averages
						if(xOffset == 0) {
							leftEdge[0] += intColor & 0xFF;
							leftEdge[1] += (intColor & 0xFF00) >> 8;
						leftEdge[2] += (intColor & 0xFF0000) >> 16;
						}
						else if(xOffset == getImgSize - 1) {
							rightEdge[0] += intColor & 0xFF;
							rightEdge[1] += (intColor & 0xFF00) >> 8;
						rightEdge[2] += (intColor & 0xFF0000) >> 16;
						}
						if(yOffset == 0) {
							topEdge[0] += intColor & 0xFF;
							topEdge[1] += (intColor & 0xFF00) >> 8;
							topEdge[2] += (intColor & 0xFF0000) >> 16;
						}
						else if(yOffset== getImgSize - 1 ) {
							bottomEdge[0] += intColor & 0xFF;
							bottomEdge[1] += (intColor & 0xFF00) >> 8;
							bottomEdge[2] += (intColor & 0xFF0000) >> 16;
						}

						//Background in case chunk doesnt load
						total[0] += intColor & 0xFF;
						total[1] += (intColor & 0xFF00) >> 8;
						total[2] += (intColor & 0xFF0000) >> 16;


					}
				}

				int multiplierGreen = 1;
				int multiplierBlue = 1;

				//Get the outer layer
				topEdge[0] = ZUtils.getAverageColor(topEdge[0],topEdge[1]*multiplierGreen, topEdge[2]*multiplierBlue, getImgSize*getImgSize);
				leftEdge[0] = ZUtils.getAverageColor(leftEdge[0], leftEdge[1]*multiplierGreen, leftEdge[2]* multiplierBlue, getImgSize*getImgSize);
				rightEdge[0] = ZUtils.getAverageColor(rightEdge[0], rightEdge[1]*multiplierGreen, rightEdge[2]* multiplierBlue, getImgSize*getImgSize);
				bottomEdge[0] = ZUtils.getAverageColor(bottomEdge[0], bottomEdge[1]*multiplierGreen, bottomEdge[2]* multiplierBlue, getImgSize*getImgSize);
				total[0] = ZUtils.getAverageColor(total[0], total[1]*multiplierGreen, total[2]* multiplierBlue, getImgSize*getImgSize);

				Random random = new Random(); 

				int randomMax = 0x1A;

				for(int i = 0; i < outerImgSize*outerImgSize; i++) {
					int color = ( (total[0] - randomMax + random.nextInt(randomMax) / 2 ) & 0xFF ) | ( (total[0] + (( randomMax - random.nextInt(randomMax)/2 ) << 8 ) ) & 0xFF00 ) | ( (total[0] + ( ( randomMax - random.nextInt(randomMax) /2 ) << 16 ) & 0xFF0000) );

					outerBoundsTable.put(i, color | 0xff000000);
				}

				outerBoundsTable.flip();
				table.flip(); //Yes really
				mapNeedsBinding = true;
				mapReady = true;
			}
		}, "Planet Texture Creator").start();


	}



	//@SubscribeEvent
	public static void onPostWorldRender(float partialTicks) {

		if(!mapReady )//&& (event.entityPlayer.ridingEntity == null || !(event.entityPlayer.ridingEntity instanceof EntityRocket)) )
			return;
		

		if(mapNeedsBinding) {
			mapNeedsBinding = false;
			earth.setByteBuffer(table);
			outerBounds.setByteBuffer(outerBoundsTable);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(0, -5, 0);
		GL11.glPushAttrib(GL11.GL_ALPHA_TEST_FUNC);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, .01f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		float brightness = Minecraft.getMinecraft().renderViewEntity.worldObj.getSunBrightness(partialTicks);

		double deltaY = (Minecraft.getMinecraft().renderViewEntity.posY - Minecraft.getMinecraft().renderViewEntity.lastTickPosY)*partialTicks;

		double size = (getImgSize*5/(72-Minecraft.getMinecraft().renderViewEntity.posY - deltaY));

		Tessellator tess = Tessellator.instance;

		//GL11.glTranslated(Minecraft.getMinecraft().renderViewEntity.posX, Minecraft.getMinecraft().renderViewEntity.posY, Minecraft.getMinecraft().renderViewEntity.posZ);


		//Less detailed land

		tess.startDrawingQuads();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, outerBounds.getTextureId());
		double size2 = size*16;
		float brightness2 =brightness*.43f;
		tess.setColorRGBA_F(brightness2, brightness2, brightness2, MathHelper.clamp_float(((float)Minecraft.getMinecraft().renderViewEntity.posY -200f)/50f, 0f, 1f));
		RenderHelper.renderTopFaceWithUV(tess, -10.1, size2, size2, -size2, -size2, 0, 1, 0, 1);
		tess.draw();


		tess.startDrawingQuads();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, earth.getTextureId());

		float opacityFromHeight = MathHelper.clamp_float(((float)Minecraft.getMinecraft().renderViewEntity.posY -200f)/100f, 0f, 1f);
		
		//Detailed Land
		tess.setColorRGBA_F(brightness, brightness, brightness, opacityFromHeight);
		RenderHelper.renderTopFaceWithUV(tess, -10 , size, size, -size,  -size, .2f, .8f, .2f, 0.8f);

		size *= 1.15f;
		
		tess.setColorRGBA_F(brightness, brightness, brightness, opacityFromHeight * 0.8f);
		RenderHelper.renderTopFaceWithUV(tess, -10.01, size, size, -size,  -size, .05f, .95f, .05f, 0.95f);

		size *= 1.18f;
		
		tess.setColorRGBA_F(brightness, brightness, brightness, opacityFromHeight * 0.4f);
		RenderHelper.renderTopFaceWithUV(tess, -10.02, size, size, -size,  -size, 0f, 1f, 0f, 1f);
		
		tess.draw();

		//AtmosphereGlow
		Vec3 skyColor = Minecraft.getMinecraft().renderViewEntity.worldObj.provider.getSkyColor(Minecraft.getMinecraft().renderViewEntity, partialTicks);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,0);

		tess.startDrawingQuads();
		tess.setColorRGBA_F((float)skyColor.xCoord, (float)skyColor.yCoord, (float)skyColor.zCoord, 0.05f);

		size = (getImgSize*100/(180-Minecraft.getMinecraft().renderViewEntity.posY - deltaY));


		for(int i = 0; i < 5 * MathHelper.clamp_float(( ( DimensionManager.getInstance().getDimensionProperties(Minecraft.getMinecraft().renderViewEntity.worldObj.provider.dimensionId).atmosphereDensity *.01f * (float)Minecraft.getMinecraft().renderViewEntity.posY -280f) )/150f, 0f, 2f); i++) {
			RenderHelper.renderTopFace(tess, -9 + i*.6, size, size, -size , -size);
		}

		//
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		tess.draw();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void onScreenRender(RenderGameOverlayEvent event) {
		Entity ride;

		if(event.type == ElementType.HOTBAR && (ride = Minecraft.getMinecraft().thePlayer.ridingEntity) instanceof EntityRocket) {
			EntityRocket rocket = (EntityRocket)ride;

			//If the space bar is pressed then send a packet to the server and launch the rocket
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !rocket.isInFlight()) {
				PacketHandler.sendToServer(new PacketEntity(rocket, (byte)EntityRocket.PacketType.LAUNCH.ordinal()));
				rocket.launch();
			}

			GL11.glEnable(GL11.GL_BLEND);

			Minecraft.getMinecraft().renderEngine.bindTexture(background);

			this.drawTexturedModalRect(0, 0, 0, 0, 17, 252);

			//Draw altitude indicator
			float percentOrbit = MathHelper.clamp_float((float) ((rocket.posY - rocket.worldObj.provider.getAverageGroundLevel())/(float)(Configuration.orbit-rocket.worldObj.provider.getAverageGroundLevel())), 0f, 1f);
			this.drawTexturedModalRect(3, 8 + (int)(79*(1 - percentOrbit)), 17, 0, 6, 6); //6 to 83

			this.drawTexturedModalRect(3, 94 + (int)(69*(0.5 - (MathHelper.clamp_float((float) (rocket.motionY), -1f, 1f)/2f))), 17, 0, 6, 6); //94 to 161

			//Draw fuel indicator
			int size = (int)(68*(rocket.getFuelAmount() /(float)rocket.getFuelCapacity()));
			this.drawTexturedModalRect(3, 242 - size, 17, 75 - size, 3, size); //94 to 161

			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
