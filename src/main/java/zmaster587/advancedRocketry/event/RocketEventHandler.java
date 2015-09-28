package zmaster587.advancedRocketry.event;

import java.nio.IntBuffer;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLandedEvent;
import zmaster587.advancedRocketry.api.RocketEvent.RocketLaunchEvent;
import zmaster587.advancedRocketry.client.render.ClientDynamicTexture;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.Configuration;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.ZUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RocketEventHandler extends Gui {

	private ResourceLocation background = TextureResources.rocketHud;
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
	public void onRocketLaunch(RocketLaunchEvent event) {
		if(event.world.isRemote) {
			prepareOrbitalMap(event);
			event.world.provider.setSkyRenderer(new RenderPlanetarySky());
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void destroyOrbitalTextures(World world) {
			if(!(world.provider instanceof IPlanetaryProvider))
				world.provider.setSkyRenderer(null);
			if(earth != null)
				GL11.glDeleteTextures(earth.getTextureId());
			if(outerBounds != null)
				GL11.glDeleteTextures(outerBounds.getTextureId());
			outerBounds = null;
			earth = null;
			mapReady = false;
	}

	@SideOnly(Side.CLIENT)
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
				long topEdge[], bottomEdge[], leftEdge[], rightEdge[], total[];
				total = topEdge = bottomEdge = leftEdge = rightEdge = new long[] {0,0,0};

				int numChunksLoaded = 0;
				
				for(int i = 0; i < getImgSize*getImgSize; i++) {
					//TODO: Optimize
					int xOffset = (i % getImgSize);
					int yOffset = (i / getImgSize);

					int xPosition = (int)event.entity.posX - (getImgSize/2) + xOffset;
					int zPosition = (int)event.entity.posZ - (getImgSize/2) + yOffset;
					Chunk chunk = event.world.getChunkFromBlockCoords(xPosition, zPosition);

					if(chunk.isChunkLoaded && !chunk.isEmpty()) {
						//Get Xcoord and ZCoords in the chunk
						numChunksLoaded++;
						int heightValue = chunk.getHeightValue( xPosition + (chunk.xPosition >= 0 ? - (Math.abs( chunk.xPosition )<< 4) : (Math.abs( chunk.xPosition )<< 4)), zPosition + (chunk.zPosition >= 0 ? - (Math.abs(chunk.zPosition )<< 4) : (Math.abs(chunk.zPosition )<< 4)));
						MapColor color = MapColor.airColor;
						int yPosition;
						
						Block block = null;

						//Get the first non-air block
						for(yPosition = heightValue; yPosition > 0; yPosition-- ) {
							block = event.world.getBlock(xPosition, yPosition, zPosition);
							if((color = block.getMapColor(event.world.getBlockMetadata(xPosition, yPosition, zPosition))) != MapColor.airColor) {
								break;
							}
						}
						
						int intColor;
						
						if(block == Blocks.grass || block == Blocks.tallgrass) {
							int color2 = event.world.getBiomeGenForCoords(xPosition, zPosition).getBiomeGrassColor(xPosition, yPosition, zPosition);
							int r = (color2 & 0xFF);
							int g = ( (color2 >>> 8) & 0xFF);
							int b = ( (color2 >>> 16) & 0xFF);
							intColor = b | (g << 8) | (r << 16);
						}
						else if(block == Blocks.leaves || block == Blocks.leaves2) {
							int color2 = event.world.getBiomeGenForCoords(xPosition, zPosition).getBiomeFoliageColor(xPosition, yPosition, zPosition);
							int r = (color2 & 0xFF);
							int g = ( (color2 >>> 8) & 0xFF);
							int b = ( (color2 >>> 16) & 0xFF);
							intColor = b | (g << 8) | (r << 16);
						}
						else
							intColor = ( (color.colorValue & 0xFF) << 16) | ( ( color.colorValue >>> 16 ) & 0xFF ) | ( color.colorValue & 0xFF00 );

						//Put into the table and make opaque
						table.put(i, intColor | 0xFF000000);

						//Background in case chunk doesnt load
						total[0] += intColor & 0xFF;
						total[1] += (intColor & 0xFF00) >>> 8;
						total[2] += (intColor & 0xFF0000) >>> 16;
						
					}
				}

				int multiplierGreen = 1;
				int multiplierBlue = 1;

				//Get the outer layer
				total[0] =     ZUtils.getAverageColor(total[0], total[1]*multiplierGreen, total[2]* multiplierBlue, numChunksLoaded);

				Random random = new Random(); 

				int randomMax = 0x2A;

				for(int i = 0; i < outerImgSize*outerImgSize; i++) {
					
					int randR =   randomMax - random.nextInt(randomMax) / 2;
					int randG = ( randomMax - random.nextInt(randomMax)/2 ) << 8;
					int randB = ( randomMax - random.nextInt(randomMax) /2 ) << 16;

					
					int color = (int)( MathHelper.clamp_int((int) ( (total[0] & 0xFF) + randR ),0, 0xFF ) |
							MathHelper.clamp_int((int)(total[0] & 0xFF00) + randG, 0x0100, 0xFF00)  | 
							MathHelper.clamp_int( (int)(( total[0] & 0xFF0000) + randB), 0x010000, 0xFF0000) );

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

		if(!mapReady )
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
		RenderHelper.renderTopFaceWithUV(tess, -10 , size, size, -size,  -size, 0f, 1f, 0f, 1f);
		
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

			GL11.glEnable(GL11.GL_BLEND);

			Minecraft.getMinecraft().renderEngine.bindTexture(background);

			this.drawTexturedModalRect(0, 0, 0, 0, 17, 252);

			//Draw altitude indicator
			float percentOrbit = MathHelper.clamp_float((float) ((rocket.posY - rocket.worldObj.provider.getAverageGroundLevel())/(float)(Configuration.orbit-rocket.worldObj.provider.getAverageGroundLevel())), 0f, 1f);
			this.drawTexturedModalRect(3, 8 + (int)(79*(1 - percentOrbit)), 17, 0, 6, 6); //6 to 83

			//Draw Velocity indicator
			this.drawTexturedModalRect(3, 94 + (int)(69*(0.5 - (MathHelper.clamp_float((float) (rocket.motionY), -1f, 1f)/2f))), 17, 0, 6, 6); //94 to 161

			//Draw fuel indicator
			int size = (int)(68*(rocket.getFuelAmount() /(float)rocket.getFuelCapacity()));
			this.drawTexturedModalRect(3, 242 - size, 17, 75 - size, 3, size); //94 to 161

			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
