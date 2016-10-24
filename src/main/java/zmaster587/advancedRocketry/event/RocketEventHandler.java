package zmaster587.advancedRocketry.event;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.IPlanetaryProvider;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.client.render.ClientDynamicTexture;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.ZUtils;

public class RocketEventHandler extends Gui {

	private ResourceLocation background = TextureResources.rocketHud;
	private static ClientDynamicTexture earth;
	private static ClientDynamicTexture outerBounds;
	private static final int getImgSize = 512;
	private static final int outerImgSize = getImgSize/8;
	private static boolean mapReady = false;
	private static boolean mapNeedsBinding = false;
	private static IntBuffer table,outerBoundsTable;
	Thread thread = null; 

	private static final int numTicksToDisplay = 100;


	@SubscribeEvent
	public void onRocketDeorbit(RocketEvent.RocketDeOrbitingEvent event) {
		if(event.world.isRemote) {
			prepareOrbitalMap(event);

			//Sky blend color gets stuck and doesnt update unless a new X/Z coord is passed
			//So fix that...
			ForgeHooksClient.getSkyBlendColour(event.world, event.getEntity().getPosition());

			if(!(event.world.provider instanceof IPlanetaryProvider)) {
				event.world.provider.setSkyRenderer(new RenderPlanetarySky());
			}
		}
	}

	@SubscribeEvent
	public void onRocketLaunch(RocketEvent.RocketLaunchEvent event) {
		if(event.world.isRemote && !event.getEntity().getPassengers().isEmpty() && event.getEntity().getPassengers().contains(Minecraft.getMinecraft().thePlayer)) {
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
	private void prepareOrbitalMap(RocketEvent event) {
		mapReady = false;

		//Attempt to generate everything on seperate thread
		if (earth == null) {
			earth = new ClientDynamicTexture(getImgSize,getImgSize);
			outerBounds = new ClientDynamicTexture(outerImgSize, outerImgSize);
		}

		if(event.world.provider.getDimension() == Configuration.spaceDimId) {
			destroyOrbitalTextures(event.world);
			return;
		}

		//Multi thread texture creation b/c it can be expensive
		final World worldObj = event.world;
		final Entity entity = event.getEntity();

		if(thread == null || !thread.isAlive()) {

			thread = new Thread(new Runnable() {
				@Override
				public void run() {

					int numChunksLoaded = 0;
					
						table = earth.getByteBuffer();
						outerBoundsTable = outerBounds.getByteBuffer();

						//Get the average of each edge RGB
						long topEdge[], bottomEdge[], leftEdge[], rightEdge[], total[];
						total = topEdge = bottomEdge = leftEdge = rightEdge = new long[] {0,0,0};


						do {
						for(int i = 0; i < getImgSize*getImgSize; i++) {
							//TODO: Optimize
							int xOffset = (i % getImgSize);
							int yOffset = (i / getImgSize);

							int xPosition = (int)entity.posX - (getImgSize/2) + xOffset;
							int zPosition = (int)entity.posZ - (getImgSize/2) + yOffset;
							BlockPos thisPos = new BlockPos(xPosition, 0, zPosition);
							Chunk chunk = worldObj.getChunkFromBlockCoords(thisPos);

							if(chunk.isLoaded() && !chunk.isEmpty()) {
								//Get Xcoord and ZCoords in the chunk
								numChunksLoaded++;
								int heightValue = chunk.getHeightValue( xPosition + (chunk.xPosition >= 0 ? - (Math.abs( chunk.xPosition )<< 4) : (Math.abs( chunk.xPosition )<< 4)), zPosition + (chunk.zPosition >= 0 ? - (Math.abs(chunk.zPosition )<< 4) : (Math.abs(chunk.zPosition )<< 4)));
								MapColor color = MapColor.AIR;
								int yPosition;

								IBlockState block = null;

								//Get the first non-air block
								for(yPosition = heightValue; yPosition > 0; yPosition-- ) {
									block = worldObj.getBlockState(new BlockPos(xPosition, yPosition, zPosition));
									if((color = block.getMapColor()) != MapColor.AIR) {
										break;
									}
								}

								int intColor;

								if(block.getBlock() == Blocks.GRASS || block.getBlock() == Blocks.TALLGRASS) {
									int color2 = worldObj.getBiomeGenForCoords(thisPos).getGrassColorAtPos(thisPos.add(0, yPosition, 0));
									int r = (color2 & 0xFF);
									int g = ( (color2 >>> 8) & 0xFF);
									int b = ( (color2 >>> 16) & 0xFF);
									intColor = b | (g << 8) | (r << 16);
								}
								else if(block.getBlock() == Blocks.LEAVES || block.getBlock() == Blocks.LEAVES2) {
									int color2 = worldObj.getBiomeGenForCoords(thisPos).getFoliageColorAtPos(thisPos.add(0, yPosition, 0));
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
					} while(numChunksLoaded == 0);

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
			}, "Planet Texture Creator");
			thread.start();
		}
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
		GL11.glDisable(GL11.GL_FOG);
		GL11.glAlphaFunc(GL11.GL_GREATER, .01f);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		float brightness = Minecraft.getMinecraft().getRenderViewEntity().worldObj.getSunBrightness(partialTicks);

		double deltaY = (Minecraft.getMinecraft().getRenderViewEntity().posY - Minecraft.getMinecraft().getRenderViewEntity().lastTickPosY)*partialTicks;

		double size = (getImgSize*5/(72-Minecraft.getMinecraft().getRenderViewEntity().posY - deltaY));


		VertexBuffer buffer = Tessellator.getInstance().getBuffer();

		//Less detailed land

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, outerBounds.getTextureId());
		double size2 = size*16;
		float brightness2 =brightness*.43f;
		GlStateManager.color(brightness2, brightness2, brightness2, MathHelper.clamp_float(((float)Minecraft.getMinecraft().getRenderViewEntity().posY -200f)/50f, 0f, 1f));
		RenderHelper.renderTopFaceWithUV(buffer, -10.1, size2, size2, -size2, -size2, 0, 1, 0, 1);
		Tessellator.getInstance().draw();


		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, earth.getTextureId());

		float opacityFromHeight = MathHelper.clamp_float(((float)Minecraft.getMinecraft().getRenderViewEntity().posY -200f)/100f, 0f, 1f);

		//Detailed Land
		GlStateManager.color(brightness2, brightness2, brightness2, MathHelper.clamp_float(((float)Minecraft.getMinecraft().getRenderViewEntity().posY -200f)/50f, 0f, 1f));
		RenderHelper.renderTopFaceWithUV(buffer, -10 , size, size, -size,  -size, 0f, 1f, 0f, 1f);

		Tessellator.getInstance().draw();

		//AtmosphereGlow
		Vec3d skyColor = Minecraft.getMinecraft().getRenderViewEntity().worldObj.provider.getSkyColor(Minecraft.getMinecraft().getRenderViewEntity(), partialTicks);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,0);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
		GlStateManager.color((float)skyColor.xCoord, (float)skyColor.yCoord, (float)skyColor.zCoord, 0.05f);

		size = (getImgSize*100/(180-Minecraft.getMinecraft().getRenderViewEntity().posY - deltaY));


		for(int i = 0; i < 5 * MathHelper.clamp_float(( ( DimensionManager.getInstance().getDimensionProperties(Minecraft.getMinecraft().getRenderViewEntity().worldObj.provider.getDimension()).getAtmosphereDensity() *.01f * (float)Minecraft.getMinecraft().getRenderViewEntity().posY -280f) )/150f, 0f, 2f); i++) {
			RenderHelper.renderTopFace(buffer, -9 + i*.6, size, size, -size , -size);
		}

		//
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		Tessellator.getInstance().draw();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 0);
	}

	@SubscribeEvent
	public void onScreenRender(RenderGameOverlayEvent.Post event) {
		Entity ride;
		if(event.getType() == ElementType.HOTBAR) {
			if((ride = Minecraft.getMinecraft().thePlayer.getRidingEntity()) instanceof EntityRocket) {
				EntityRocket rocket = (EntityRocket)ride;

				GL11.glEnable(GL11.GL_BLEND);
				//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

				Minecraft.getMinecraft().renderEngine.bindTexture(background);

				//Draw BG
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
				String str = rocket.getTextOverlay();
				if(!str.isEmpty()) {
					FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
					
					float scale = str.length() < 50 ? 1f : 0.5f;
					
					int screenX = (int) ((event.getResolution().getScaledWidth()/(scale*6) - fontRenderer.getStringWidth(str)/2));
					int screenY = (int) ((event.getResolution().getScaledHeight()/18)/scale);
					

					GL11.glPushMatrix();
					GL11.glScalef(scale*3, scale*3, scale*3);

					fontRenderer.drawStringWithShadow(str, screenX, screenY, 0xFFFFFF);

					GL11.glPopMatrix();
				}
			}

			//Draw the O2 Bar if needed
			ItemStack chestPiece = Minecraft.getMinecraft().thePlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if(!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode && chestPiece != null && chestPiece.getItem() instanceof IFillableArmor) {
				float size = ((IFillableArmor)chestPiece.getItem()).getAirRemaining(chestPiece)/(float)((IFillableArmor)chestPiece.getItem()).getMaxAir();

				GL11.glEnable(GL11.GL_BLEND);
				Minecraft.getMinecraft().renderEngine.bindTexture(background);
				GL11.glColor3f(1f, 1f, 1f);
				int width = 83;
				int screenX = event.getResolution().getScaledWidth()/2 + 8;
				int screenY = event.getResolution().getScaledHeight() - 57;

				//Draw BG
				this.drawTexturedModalRect(screenX, screenY, 23, 0, width, 17);
				this.drawTexturedModalRect(screenX , screenY, 23, 17, (int)(width*size), 17);
			}


			//Draw module icons
			if(!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode && Minecraft.getMinecraft().thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && Minecraft.getMinecraft().thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof IModularArmor) {
				for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
					renderModuleSlots(Minecraft.getMinecraft().thePlayer.getItemStackFromSlot(slot), 4-slot.getIndex(), event);
				}
			}

			//In event of world change make sure the warning isn't displayed
			if(Minecraft.getMinecraft().theWorld.getTotalWorldTime() - AtmosphereHandler.lastSuffocationTime < 0)
				AtmosphereHandler.lastSuffocationTime = 0;
			//Tell the player he's suffocating if needed
			if(Minecraft.getMinecraft().theWorld.getTotalWorldTime() - AtmosphereHandler.lastSuffocationTime < numTicksToDisplay) {
				FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
				String str = "Warning: No Oxygen detected!";
				int screenX = event.getResolution().getScaledWidth()/6 - fontRenderer.getStringWidth(str)/2;
				int screenY = event.getResolution().getScaledHeight()/18;

				GL11.glPushMatrix();
				GL11.glScalef(3, 3, 3);

				fontRenderer.drawStringWithShadow(str, screenX, screenY, 0xFF5656);
				GL11.glColor3f(1f, 1f, 1f);
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureResources.progressBars);
				this.drawTexturedModalRect(screenX + fontRenderer.getStringWidth(str)/2 -8, screenY - 16, 0, 156, 16, 16);

				GL11.glPopMatrix();
			}
		}
	}

	private void renderModuleSlots(ItemStack armorStack, int slot, RenderGameOverlayEvent event) {
		int index = 1;
		float color = 0.85f + 0.15F*MathHelper.sin( 2f*(float)Math.PI*((Minecraft.getMinecraft().theWorld.getTotalWorldTime()) % 60)/60f );
		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		float alpha = 0.6f;

		//TODO other armor slots
		if(armorStack != null && armorStack.getItem() instanceof IModularArmor) {

			int size = 24;
			int screenY = 8 + (slot-1)*(size + 8);
			int screenX = 8;

			//Draw BG
			GL11.glColor4f(1f,1f,1f, 1f);
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.frameHUDBG);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel-1, screenX - 4, screenY - 4, screenX + size, screenY + size + 4,0d,0.5d,0d,1d);
			Tessellator.getInstance().draw();

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.frameHUDBG);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel-1, screenX + size, screenY - 3, screenX + 2 + size, screenY + size + 3,0.5d,0.5d,0d,0d);
			Tessellator.getInstance().draw();

			//Draw Icon
			GlStateManager.color(color,color,color, color);
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.armorSlots[slot-1]);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel-1, screenX, screenY, screenX + size, screenY + size,0d,1d,1d,0d);
			Tessellator.getInstance().draw();

			List<ItemStack> stacks = ((IModularArmor)armorStack.getItem()).getComponents(armorStack);

			for(ItemStack stack : stacks) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
				((IArmorComponent)stack.getItem()).renderScreen(stack, stacks, event, this);

				ResourceIcon icon = ((IArmorComponent)stack.getItem()).getComponentIcon(stack);
				ResourceLocation texture = null; 
				if(icon != null) 
					texture= icon.getResourceLocation();

				//if(texture != null) {

				screenX = 12 + index*(size+2);

				//Draw BG
				
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.frameHUDBG);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel -1, screenX - 4, screenY - 4, screenX + size - 2, screenY + size + 4,0.5d,0.5d,0d,1d);
				Tessellator.getInstance().draw();




				if(texture != null) {
					//Draw Icon
					Minecraft.getMinecraft().renderEngine.bindTexture(texture);
					GlStateManager.color(color,color,color, alpha);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel-1, screenX, screenY, screenX + size, screenY + size, icon.getMinU(),icon.getMaxU(), icon.getMaxV(),icon.getMinV());
					Tessellator.getInstance().draw();
				}
				else {
					GL11.glPushMatrix();
					GlStateManager.translate(screenX , screenY, 0);
					GlStateManager.scale(1.5f, 1.5f, 1.5f);
					Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack,  0,0);
					GL11.glPopMatrix();
				}

				index++;
				//}
			}

			screenX = (index)*(size+2) - 4;
			//Draw BG
			GlStateManager.color(1,1,1, 1f);
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.frameHUDBG);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel-1, screenX + 12, screenY - 4, screenX + size, screenY + size + 4,0.75d,1d,0d,1d);
			Tessellator.getInstance().draw();
		}

		GlStateManager.disableAlpha();
	}
}
