package zmaster587.advancedRocketry.event;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
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
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Mouse;
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
import zmaster587.advancedRocketry.util.ItemAirUtils;
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
	private static IRenderHandler prevRenderHanlder = null;
	Thread thread = null; 
	public static GuiBox suitPanel = new GuiBox(8,8,24,24);
	public static GuiBox oxygenBar = new GuiBox(8,-57, 80, 48);
	public static GuiBox hydrogenBar = new GuiBox(8,-74, 80, 48);
	public static GuiBox atmBar = new GuiBox(8, 27, 200, 48);
	private static GuiBox currentlySelectedBox = null;

	private static final int numTicksToDisplay = 100;

	private static String displayString = "";
	private static long lastDisplayTime = -1000;


	@SubscribeEvent
	public void onRocketDeorbit(RocketEvent.RocketDeOrbitingEvent event) {
		if(event.world.isRemote) {
			prepareOrbitalMap(event);

			//Sky blend color gets stuck and doesnt update unless a new X/Z coord is passed
			//So fix that...
			ForgeHooksClient.getSkyBlendColour(event.world, event.getEntity().getPosition());

			if(Configuration.planetSkyOverride && !(event.world.provider instanceof IPlanetaryProvider)) {
				prevRenderHanlder = event.world.provider.getSkyRenderer();
				event.world.provider.setSkyRenderer(new RenderPlanetarySky());
			}
		}
	}

	@SubscribeEvent
	public void onRocketLaunch(RocketEvent.RocketLaunchEvent event) {
		if(Configuration.planetSkyOverride && event.world.isRemote && !event.getEntity().getPassengers().isEmpty() && event.getEntity().getPassengers().contains(Minecraft.getMinecraft().player)) {
			prepareOrbitalMap(event);
			prevRenderHanlder = event.world.provider.getSkyRenderer();
			event.world.provider.setSkyRenderer(new RenderPlanetarySky());
		}
	}

	@SideOnly(Side.CLIENT)
	public static void setOverlay(long endTime, String msg) {
		displayString = msg;
		lastDisplayTime = endTime;
	}

	@SideOnly(Side.CLIENT)
	public static void destroyOrbitalTextures(World world) {
		if(!Configuration.skyOverride && !(world.provider instanceof IPlanetaryProvider)) {
			world.provider.setSkyRenderer(prevRenderHanlder);
			prevRenderHanlder = null;
		}

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
								int heightValue = chunk.getHeightValue( xPosition + (chunk.x >= 0 ? - (Math.abs( chunk.x )<< 4) : (Math.abs( chunk.x )<< 4)), zPosition + (chunk.z >= 0 ? - (Math.abs(chunk.z )<< 4) : (Math.abs(chunk.z )<< 4)));
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
								if(block == null)
									continue;

								int intColor;

								if(block.getBlock() == Blocks.GRASS || block.getBlock() == Blocks.TALLGRASS) {
									int color2 = worldObj.getBiome(thisPos).getGrassColorAtPos(thisPos.add(0, yPosition, 0));
									int r = (color2 & 0xFF);
									int g = ( (color2 >>> 8) & 0xFF);
									int b = ( (color2 >>> 16) & 0xFF);
									intColor = b | (g << 8) | (r << 16);
								}
								else if(block.getBlock() == Blocks.LEAVES || block.getBlock() == Blocks.LEAVES2) {
									int color2 = worldObj.getBiome(thisPos).getFoliageColorAtPos(thisPos.add(0, yPosition, 0));
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


						int color = (int)( MathHelper.clamp((int) ( (total[0] & 0xFF) + randR ),0, 0xFF ) |
								MathHelper.clamp((int)(total[0] & 0xFF00) + randG, 0x0100, 0xFF00)  | 
								MathHelper.clamp( (int)(( total[0] & 0xFF0000) + randB), 0x010000, 0xFF0000) );

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

		float brightness = 16;
		
		if(Minecraft.getMinecraft().getRenderViewEntity() != null && Minecraft.getMinecraft().getRenderViewEntity().world != null)
			brightness = Minecraft.getMinecraft().getRenderViewEntity().world.getSunBrightness(partialTicks);

		double deltaY = (Minecraft.getMinecraft().getRenderViewEntity().posY - Minecraft.getMinecraft().getRenderViewEntity().lastTickPosY)*partialTicks;

		double size = (getImgSize*5/(72-Minecraft.getMinecraft().getRenderViewEntity().posY - deltaY));


		VertexBuffer buffer = Tessellator.getInstance().getBuffer();

		//Less detailed land

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, outerBounds.getTextureId());
		double size2 = size*16;
		float brightness2 =brightness*.43f;
		GlStateManager.color(brightness2, brightness2, brightness2, MathHelper.clamp(((float)Minecraft.getMinecraft().getRenderViewEntity().posY -200f)/50f, 0f, 1f));
		RenderHelper.renderTopFaceWithUV(buffer, -10.1, size2, size2, -size2, -size2, 0, 1, 0, 1);
		Tessellator.getInstance().draw();


		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, earth.getTextureId());

		float opacityFromHeight = MathHelper.clamp(((float)Minecraft.getMinecraft().getRenderViewEntity().posY -200f)/100f, 0f, 1f);

		//Detailed Land
		GlStateManager.color(brightness2, brightness2, brightness2, MathHelper.clamp(((float)Minecraft.getMinecraft().getRenderViewEntity().posY -200f)/50f, 0f, 1f));
		RenderHelper.renderTopFaceWithUV(buffer, -10 , size, size, -size,  -size, 0f, 1f, 0f, 1f);

		Tessellator.getInstance().draw();

		//AtmosphereGlow
		Vec3d skyColor = Minecraft.getMinecraft().getRenderViewEntity().world.provider.getSkyColor(Minecraft.getMinecraft().getRenderViewEntity(), partialTicks);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,0);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
		GlStateManager.color((float)skyColor.xCoord, (float)skyColor.yCoord, (float)skyColor.zCoord, 0.05f);

		size = (getImgSize*100/(180-Minecraft.getMinecraft().getRenderViewEntity().posY - deltaY));


		for(int i = 0; i < 5 * MathHelper.clamp(( ( DimensionManager.getInstance().getDimensionProperties(Minecraft.getMinecraft().getRenderViewEntity().world.provider.getDimension()).getAtmosphereDensity() *.01f * (float)Minecraft.getMinecraft().getRenderViewEntity().posY -280f) )/150f, 0f, 2f); i++) {
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
			if((ride = Minecraft.getMinecraft().player.getRidingEntity()) instanceof EntityRocket) {
				EntityRocket rocket = (EntityRocket)ride;

				GL11.glEnable(GL11.GL_BLEND);
				//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

				Minecraft.getMinecraft().renderEngine.bindTexture(background);

				//Draw BG
				this.drawTexturedModalRect(0, 0, 0, 0, 17, 252);

				//Draw altitude indicator
				float percentOrbit = MathHelper.clamp((float) ((rocket.posY - rocket.world.provider.getAverageGroundLevel())/(float)(Configuration.orbit-rocket.world.provider.getAverageGroundLevel())), 0f, 1f);
				this.drawTexturedModalRect(3, 8 + (int)(79*(1 - percentOrbit)), 17, 0, 6, 6); //6 to 83

				//Draw Velocity indicator
				this.drawTexturedModalRect(3, 94 + (int)(69*(0.5 - (MathHelper.clamp((float) (rocket.motionY), -1f, 1f)/2f))), 17, 0, 6, 6); //94 to 161

				//Draw fuel indicator
				int size = (int)(68*(rocket.getFuelAmount() /(float)rocket.getFuelCapacity()));
				this.drawTexturedModalRect(3, 242 - size, 17, 75 - size, 3, size); //94 to 161

				GL11.glDisable(GL11.GL_BLEND);
				String str = rocket.getTextOverlay();
				if(!str.isEmpty()) {

					String[] strs = str.split("\n");
					int vertPos = 0;
					for(String strPart : strs) {

						FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

						float scale = str.length() < 50 ? 1f : 0.5f;

						int screenX = (int) ((event.getResolution().getScaledWidth()/(scale*6) - fontRenderer.getStringWidth(strPart)/2));
						int screenY = (int) ((event.getResolution().getScaledHeight()/18)/scale) + 18*vertPos;


						GL11.glPushMatrix();
						GL11.glScalef(scale*3, scale*3, scale*3);

						fontRenderer.drawStringWithShadow(strPart, screenX, screenY, 0xFFFFFF);

						GL11.glPopMatrix();

						vertPos++;
					}
				}
			}

			//Draw the O2 Bar if needed
			if(!Minecraft.getMinecraft().player.capabilities.isCreativeMode) {
				ItemStack chestPiece = Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
				IFillableArmor fillable = null;
				if(!chestPiece.isEmpty() && chestPiece.getItem() instanceof IFillableArmor)
					fillable = (IFillableArmor)chestPiece.getItem();
				else if(ItemAirUtils.INSTANCE.isStackValidAirContainer(chestPiece))
					fillable = new ItemAirUtils.ItemAirWrapper(chestPiece);

				if(fillable != null) {
					float size = fillable.getAirRemaining(chestPiece)/(float)fillable.getMaxAir(chestPiece);

					GL11.glEnable(GL11.GL_BLEND);
					Minecraft.getMinecraft().renderEngine.bindTexture(background);
					GL11.glColor3f(1f, 1f, 1f);
					int width = 83;
					int screenX = oxygenBar.getRenderX();//+ 8;
					int screenY = oxygenBar.getRenderY();//- 57;

					//Draw BG
					this.drawTexturedModalRect(screenX, screenY, 23, 0, width, 17);
					this.drawTexturedModalRect(screenX , screenY, 23, 17, (int)(width*size), 17);
				}
			}

			//Draw module icons
			if(!Minecraft.getMinecraft().player.capabilities.isCreativeMode && Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof IModularArmor) {
				for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
					renderModuleSlots(Minecraft.getMinecraft().player.getItemStackFromSlot(slot), 4-slot.getIndex(), event);
				}
			}

			//In event of world change make sure the warning isn't displayed
			if(Minecraft.getMinecraft().world.getTotalWorldTime() - AtmosphereHandler.lastSuffocationTime < 0)
				AtmosphereHandler.lastSuffocationTime = 0;
			//Tell the player he's suffocating if needed
			if(Minecraft.getMinecraft().world.getTotalWorldTime() - AtmosphereHandler.lastSuffocationTime < numTicksToDisplay) {
				FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
				String str = "";
				if(AtmosphereHandler.currentAtm != null) {
					str = AtmosphereHandler.currentAtm.getDisplayMessage();
				}

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

			//Draw arbitrary string
			if(Minecraft.getMinecraft().world.getTotalWorldTime() <= lastDisplayTime) {
				FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
				GL11.glPushMatrix();
				GL11.glScalef(2,2,2);
				int loc = 0;
				for(String str : displayString.split("\n")) {

					int screenX = event.getResolution().getScaledWidth()/4 - fontRenderer.getStringWidth(str)/2;
					int screenY = event.getResolution().getScaledHeight()/12 + loc*(event.getResolution().getScaledHeight())/12;



					fontRenderer.drawStringWithShadow(str, screenX, screenY, 0xFF5656);
					loc++;
				}

				GL11.glColor3f(1f, 1f, 1f);
				GL11.glPopMatrix();
			}
		}
	}

	@SubscribeEvent
	public void mouseInputEvent(MouseInputEvent event) {
		if(!Configuration.lockUI && !Mouse.isGrabbed()) {

			if(Mouse.isButtonDown(2)) {
				ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
				int i = scaledresolution.getScaledWidth();
				int j = scaledresolution.getScaledHeight();
				int mouseX =  Mouse.getX() * i / Minecraft.getMinecraft().displayWidth;
				int mouseY = j - Mouse.getY() * j / Minecraft.getMinecraft().displayHeight - 1;

				if(currentlySelectedBox == null && mouseX >= suitPanel.getX(i) && mouseX < suitPanel.getX(i) + suitPanel.sizeX &&
						mouseY >= suitPanel.getY(j) && mouseY < suitPanel.getY(j) + suitPanel.sizeY) {
					currentlySelectedBox = suitPanel;
				}

				if(currentlySelectedBox == null && mouseX >= oxygenBar.getX(i) && mouseX < oxygenBar.getX(i) + oxygenBar.sizeX &&
						mouseY >= oxygenBar.getY(j) && mouseY < oxygenBar.getY(j) + oxygenBar.sizeY) {
					currentlySelectedBox = oxygenBar;
				}

				if(currentlySelectedBox == null && mouseX >= hydrogenBar.getX(i) && mouseX < hydrogenBar.getX(i) + hydrogenBar.sizeX &&
						mouseY >= hydrogenBar.getY(j) && mouseY < hydrogenBar.getY(j) + hydrogenBar.sizeY) {
					currentlySelectedBox = hydrogenBar;
				}
				
				if(currentlySelectedBox == null && mouseX >= atmBar.getX(i) && mouseX < atmBar.getX(i) + atmBar.sizeX &&
						mouseY >= atmBar.getY(j) && mouseY < atmBar.getY(j) + atmBar.sizeY) {
					currentlySelectedBox = atmBar;
				}
				
				if(currentlySelectedBox != null) {

					currentlySelectedBox.setRenderX(mouseX, i);
					currentlySelectedBox.setRenderY(mouseY, j);
				}
			}
			else
				currentlySelectedBox = null;
		}
	}

	private void renderModuleSlots(ItemStack armorStack, int slot, RenderGameOverlayEvent event) {
		int index = 1;
		float color = 0.85f + 0.15F*MathHelper.sin( 2f*(float)Math.PI*((Minecraft.getMinecraft().world.getTotalWorldTime()) % 60)/60f );
		VertexBuffer buffer = Tessellator.getInstance().getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		float alpha = 0.6f;


		if( armorStack != null ) {

			boolean modularArmorFlag = armorStack.getItem() instanceof IModularArmor;

			if(modularArmorFlag || ItemAirUtils.INSTANCE.isStackValidAirContainer(armorStack)) {

				int size = 24;
				int screenY = suitPanel.getRenderY() + (slot-1)*(size + 8);
				int screenX = suitPanel.getRenderX();

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

				if(modularArmorFlag) {
					List<ItemStack> stacks = ((IModularArmor)armorStack.getItem()).getComponents(armorStack);
					for(ItemStack stack : stacks) {
						GL11.glColor4f(1f, 1f, 1f, 1f);
						((IArmorComponent)stack.getItem()).renderScreen(stack, stacks, event, this);

						ResourceIcon icon = ((IArmorComponent)stack.getItem()).getComponentIcon(stack);
						ResourceLocation texture = null; 
						if(icon != null) 
							texture= icon.getResourceLocation();

						//if(texture != null) {

						screenX = suitPanel.getRenderX() + 4 + index*(size+2);

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
				}

				screenX = (index)*(size+2) + suitPanel.getRenderX() - 12;
				//Draw BG
				GlStateManager.color(1,1,1, 1f);
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureResources.frameHUDBG);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel-1, screenX + 12, screenY - 4, screenX + size, screenY + size + 4,0.75d,1d,0d,1d);
				Tessellator.getInstance().draw();
			}
		}

		GlStateManager.disableAlpha();
	}

	public static class GuiBox {
		private int x;
		private int y;
		int modeX = -1;
		int modeY = -1;
		int sizeX, sizeY;
		boolean isVisible = true;

		public GuiBox(int x, int y, int sizeX, int sizeY) {
			this.setRawX(x);
			this.setRawY(y);
			this.sizeX = sizeX;
			this.sizeY = sizeY;
		}

		public int getX(int scaledW) { 

			if(modeX == 1)
				return scaledW - getRawX();
			else if(modeX == 0) {
				return scaledW/2 - getRawX();
			}
			return getRawX();
		}

		public int getY(int scaledH) { 

			if(modeY == 1)
				return scaledH - getRawY();
			else if(modeY == 0) {
				return scaledH/2 - getRawY();
			}
			return getRawY();
		}

		public void setRenderX(int x, double scaleX) {
			double i = scaleX;
			if(x < i/3) {
				modeX = -1;
				this.setRawX(x); 
			}
			else if(x > i*2/3) {
				this.setRawX((int) (i - x));
				modeX = 1;
			}
			else {
				this.setRawX((int)(i/2 - x));
				modeX = 0;
			}
		}

		public void setRenderY(int y, double scaleY) {
			double i = scaleY;
			if(y < i/3) {
				modeY = -1;
				this.setRawY(y); 
			}
			else if(y > i*2/3) {
				this.setRawY((int) (i - y));
				modeY = 1;
			}
			else {
				this.setRawY((int)(i/2 - y));
				modeY = 0;
			}
		}

		public int getRenderX() {
			ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
			int i = scaledresolution.getScaledWidth();

			if( modeX == 1) {
				return i - getRawX();
			}
			else if (modeX == 0) {
				return i/2 - getRawX();
			}
			return this.getRawX();
		}

		public int getRenderY() {
			ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
			int i = scaledresolution.getScaledHeight();

			if( modeY == 1) {
				return i - getRawY();
			}
			else if (modeY == 0) {
				return i/2 - getRawY();
			}
			return this.getRawY();
		}

		public int getRawX() {
			return x;
		}

		public void setRawX(int x) {
			this.x = x;
		}

		public int getRawY() {
			return y;
		}

		public void setRawY(int y) {
			this.y = y;
		}

		public void setSizeModeX(int int1) {
			modeX = int1;
		}

		public void setSizeModeY(int int1) {
			modeY = int1;
		}

		public int getSizeModeX() {
			return modeX;
		}

		public int getSizeModeY() {
			return modeY;
		}
	}
}
