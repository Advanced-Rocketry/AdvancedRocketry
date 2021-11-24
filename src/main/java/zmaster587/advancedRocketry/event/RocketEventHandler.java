package zmaster587.advancedRocketry.event;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.RocketEvent;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.client.render.ClientDynamicTexture;
import zmaster587.advancedRocketry.client.render.planet.ISkyRenderer;
import zmaster587.advancedRocketry.client.render.planet.RenderPlanetarySky;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.ItemAirUtils;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.client.ResourceIcon;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.ZUtils;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

public class RocketEventHandler extends Screen {

	public RocketEventHandler( ) {
		super(new StringTextComponent(""));
	}

	private ResourceLocation background = TextureResources.rocketHud;
	private static ClientDynamicTexture earth;
	private static ClientDynamicTexture outerBounds;
	private static final int getImgSize = 512;
	private static final int outerImgSize = getImgSize/8;
	private static boolean mapReady = false;
	private static boolean mapNeedsBinding = false;
	private static IntBuffer table,outerBoundsTable;
	private static ISkyRenderer prevRenderHanlder = null;
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
			mapReady = true; //Temp
			//prepareOrbitalMap(event);

			//Sky blend color gets stuck and doesnt update unless a new X/Z coord is passed
			//So fix that...
			//ForgeHooksClient. getSkyBlendColour(event.world, new BlockPos(event.getEntity().getPositionVec()));

			if(ARConfiguration.getCurrentConfig().planetSkyOverride.get() && !DimensionManager.getInstance().isDimensionCreated(event.world)) {
				DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(event.world));
				prevRenderHanlder = props.getSkyRenderer();
				props.setSkyRenderer(new RenderPlanetarySky());
			}
		}
	}

	@SubscribeEvent
	public void playerTeleportEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
		//Fix O2, space elevator popup displaying after teleporting
		lastDisplayTime = -1000;
	}
	
	@SubscribeEvent
	public void onRocketLaunch(RocketEvent.RocketLaunchEvent event) {
		if(ARConfiguration.getCurrentConfig().planetSkyOverride.get() && event.world.isRemote && !event.getEntity().getPassengers().isEmpty() && event.getEntity().getPassengers().contains(Minecraft.getInstance().player)) {
			//prepareOrbitalMap(event);
			mapReady = true; //temp
			DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(event.world));
			prevRenderHanlder = props.getSkyRenderer();
			props.setSkyRenderer(new RenderPlanetarySky());
		}
	}

	@OnlyIn(value=Dist.CLIENT)
	public static void setOverlay(long endTime, String msg) {
		displayString = msg;
		lastDisplayTime = endTime;
	}

	@OnlyIn(value=Dist.CLIENT)
	public static void destroyOrbitalTextures(World world) {
		if(!DimensionManager.getInstance().isDimensionCreated(ZUtils.getDimensionIdentifier(world))) {
			DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(world));
			props.setSkyRenderer(prevRenderHanlder);
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

	@OnlyIn(value=Dist.CLIENT)
	private void prepareOrbitalMap(RocketEvent event) {
		mapReady = false;

		//Attempt to generate everything on seperate thread
		if (earth == null) {
			earth = new ClientDynamicTexture(getImgSize,getImgSize);
			outerBounds = new ClientDynamicTexture(outerImgSize, outerImgSize);
		}

		if(DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(event.world))) {
			destroyOrbitalTextures(event.world);
			mapReady = false;
			return;
		}

		//Multi thread texture creation b/c it can be expensive
		final World worldObj = event.world;
		final Entity entity = event.getEntity();

		if(thread == null || !thread.isAlive()) {

			thread = new Thread(() -> {

				int numChunksLoaded = 0;

				//table = earth.getByteBuffer();
				//outerBoundsTable = outerBounds.getByteBuffer();

				//Get the average of each edge RGB
				long[] total = new long[]{0, 0, 0};

				int numtries = 0;


				do {
					numtries++;
					for(int i = 0; i < getImgSize*getImgSize; i++) {
						//TODO: Optimize
						int xOffset = (i % getImgSize);
						int yOffset = (i / getImgSize);

						int xPosition = (int)entity.getPosX() - (getImgSize/2) + xOffset;
						int zPosition = (int)entity.getPosZ() - (getImgSize/2) + yOffset;
						BlockPos thisPos = new BlockPos(xPosition, 0, zPosition);
						Chunk chunk = worldObj.getChunkAt(thisPos);

						if(!chunk.isEmpty()) {
							//Get Xcoord and ZCoords in the chunk
							numChunksLoaded++;
							int heightValue = chunk.getTopBlockY(Type.WORLD_SURFACE, xPosition + (chunk.getPos().x >= 0 ? - (Math.abs( chunk.getPos().x )<< 4) : (Math.abs( chunk.getPos().x )<< 4)), zPosition + (chunk.getPos().z >= 0 ? - (Math.abs(chunk.getPos().z )<< 4) : (Math.abs(chunk.getPos().z )<< 4)));
							MaterialColor color = MaterialColor.AIR;
							int yPosition;

							BlockState block = null;

							//Get the first non-air block
							for(yPosition = heightValue; yPosition > 0; yPosition-- ) {
								block = worldObj.getBlockState(new BlockPos(xPosition, yPosition, zPosition));
								if((color = block.getMaterialColor(worldObj, thisPos)) != MaterialColor.AIR) {
									break;
								}
							}
							if(block == null)
								continue;

							int intColor;

							if(block.getBlock() == Blocks.GRASS_BLOCK || block.getBlock() == Blocks.TALL_GRASS) {
								int color2 = worldObj.getBiome(thisPos).getGrassColor(thisPos.getX(), thisPos.getZ());
								int r = (color2 & 0xFF);
								int g = ( (color2 >>> 8) & 0xFF);
								int b = ( (color2 >>> 16) & 0xFF);
								intColor = b | (g << 8) | (r << 16);
							}
							else if(block.getBlock() == Blocks.OAK_LEAVES ) {
								int color2 = worldObj.getBiome(thisPos).getFoliageColor();
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
				} while(numChunksLoaded == 0 && numtries < 5000);

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


					int color = MathHelper.clamp((int) ( (total[0] & 0xFF) + randR ),0, 0xFF ) |
							MathHelper.clamp((int)(total[0] & 0xFF00) + randG, 0x0100, 0xFF00)  |
							MathHelper.clamp( (int)(( total[0] & 0xFF0000) + randB), 0x010000, 0xFF0000);

					outerBoundsTable.put(i, color | 0xff000000);
				}

				outerBoundsTable.flip();
				table.flip(); //Yes really
				mapNeedsBinding = true;
				mapReady = true;
			}, "Planet Texture Creator");
			thread.start();
		}
	}


	//@SubscribeEvent
	public static void onPostWorldRender(MatrixStack matrix, float partialTicks) {

		if(!mapReady)
			return;
		
		if(DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(Minecraft.getInstance().getRenderViewEntity().world))) {
			destroyOrbitalTextures(Minecraft.getInstance().getRenderViewEntity().world);
			mapReady = false;
			return;
		}
		
		if(Minecraft.getInstance().player.getPosY() < 260)
			return;

		/*if(mapNeedsBinding) {
			mapNeedsBinding = false;
			earth.setByteBuffer(table);
			outerBounds.setByteBuffer(outerBoundsTable);
		}*/

		matrix.push();
		matrix.translate(0, -5, 0);
		RenderSystem.enableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableFog();
		RenderSystem.alphaFunc(GL11.GL_GREATER, .01f);
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		float brightness = 16;

		if(Minecraft.getInstance().getRenderViewEntity() != null && Minecraft.getInstance().getRenderViewEntity().world != null)
			brightness = Minecraft.getInstance().getRenderViewEntity().world.getBrightness(new BlockPos( Minecraft.getInstance().getRenderViewEntity().getPositionVec()));

		double deltaY = (Minecraft.getInstance().getRenderViewEntity().getPosY() - Minecraft.getInstance().getRenderViewEntity().lastTickPosY)*partialTicks;
		double size = (getImgSize/(5 * Minecraft.getInstance().getRenderViewEntity().getPosY() * (1000f / ARConfiguration.getCurrentConfig().orbit.get())));
		
		
		DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(Minecraft.getInstance().getRenderViewEntity().world));

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();

		//Less detailed land

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		//RenderSystem.bindTexture(outerBounds.getTextureId());
		Minecraft.getInstance().textureManager.bindTexture(props.getPlanetIconLEO());
		double size2 = size*400;
		float brightness2 =brightness;
		RenderHelper.renderTopFaceWithUV(matrix, buffer, -10.1, size2, size2, -size2, -size2, 0, 1, 0, 1, brightness2, brightness2, brightness2, MathHelper.clamp(((float)Minecraft.getInstance().getRenderViewEntity().getPosY() -400f)/50f, 0f, 1f));
		Tessellator.getInstance().draw();


		/*buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		RenderSystem.bindTexture(earth.getTextureId());

		float opacityFromHeight = MathHelper.clamp(((float)Minecraft.getInstance().getRenderViewEntity().getPosY() -200f)/100f, 0f, 1f);

		//Detailed Land
		RenderSystem.color4f(brightness2, brightness2, brightness2, MathHelper.clamp(((float)Minecraft.getInstance().getRenderViewEntity().getPosY() -200f)/50f, 0f, 1f));
		RenderHelper.renderTopFaceWithUV(matrix, buffer, -10 , size, size, -size,  -size, 0f, 1f, 0f, 1f);

		Tessellator.getInstance().draw();*/

		//AtmosphereGlow
		int skyColorI = Minecraft.getInstance().getRenderViewEntity().world.getBiome(new BlockPos(Minecraft.getInstance().getRenderViewEntity().getPositionVec())).getSkyColor();

		Vector3d skyColor = new Vector3d((skyColorI >> 8) & 0xff, (skyColorI >> 4) & 0xff, skyColorI & 0xff);
		
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.disableTexture();
		//RenderSystem.bindTexture(0);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		RenderSystem.color4f((float)skyColor.x, (float)skyColor.y, (float)skyColor.z, 0.15f);

		size = (getImgSize*100/(180-Minecraft.getInstance().getRenderViewEntity().getPosY() - deltaY));


		for(int i = 0; i < 5 * MathHelper.clamp(( ( DimensionManager.getInstance().getDimensionProperties(ZUtils.getDimensionIdentifier(Minecraft.getInstance().getRenderViewEntity().world)).getAtmosphereDensity() *.01f * (float)Minecraft.getInstance().getRenderViewEntity().getPosY() -280f) )/150f, 0f, 2f); i++) {
			RenderHelper.renderTopFace(matrix, buffer, -9 + i*.6, size, size, -size , -size, (float)skyColor.x/255f, (float)skyColor.y/255f, (float)skyColor.z/255f, 0.1f);
		}

		//
		RenderSystem.enableTexture();

		Tessellator.getInstance().draw();
		RenderSystem.disableBlend();
		RenderSystem.enableFog();
		matrix.pop();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@SubscribeEvent
	public void onScreenRender(RenderGameOverlayEvent.Post event) {
		Entity ride;
		if(event.getType() == ElementType.HOTBAR) {
			if((ride = Minecraft.getInstance().player.getRidingEntity()) instanceof EntityRocket) {
				EntityRocket rocket = (EntityRocket)ride;

				RenderSystem.enableBlend();
				//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

				Minecraft.getInstance().getTextureManager().bindTexture(background);

				//Draw BG
				this.blit(event.getMatrixStack(),0, 0, 0, 0, 17, 252);

				//Draw altitude indicator
				float percentOrbit = MathHelper.clamp((float) ((rocket.getPosY() - rocket.world.getSeaLevel())/(float)(ARConfiguration.getCurrentConfig().orbit.get()-rocket.world.getSeaLevel())), 0f, 1f);
				this.blit(event.getMatrixStack(), 3, 8 + (int)(79*(1 - percentOrbit)), 17, 0, 6, 6); //6 to 83

				//Draw Velocity indicator
				this.blit(event.getMatrixStack(), 3, 94 + (int)(69*(0.5 - (MathHelper.clamp((float) (rocket.getMotion().y), -1f, 1f)/2f))), 17, 0, 6, 6); //94 to 161

				//Draw fuel indicator
				int size = (int)(68 * rocket.getNormallizedProgress(0));
				this.blit(event.getMatrixStack(), 3, 242 - size, 17, 75 - size, 3, size); //94 to 161

				RenderSystem.disableBlend();
				String str = rocket.getTextOverlay();
				if(!str.isEmpty()) {

					String[] strs = str.split("\n");
					int vertPos = 0;
					for(String strPart : strs) {

						FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;

						float scale = str.length() < 50 ? 1f : 0.5f;

						int screenX = (int) ((Minecraft.getInstance().getMainWindow().getScaledWidth()/(scale*6) - fontRenderer.getStringWidth(strPart)/2));
						int screenY = (int) ((Minecraft.getInstance().getMainWindow().getScaledHeight()/18)/scale) + 18*vertPos;


						GL11.glPushMatrix();
						GL11.glScalef(scale*3, scale*3, scale*3);

						fontRenderer.drawTextWithShadow(event.getMatrixStack(), new StringTextComponent(strPart), screenX, screenY, 0xFFFFFF);

						GL11.glPopMatrix();

						vertPos++;
					}
				}
			}

			//Draw the O2 Bar if needed
			boolean isCreativeOrSpec = Minecraft.getInstance().player.isCreative() || Minecraft.getInstance().player.isSpectator();
			if(!isCreativeOrSpec) {
				ItemStack chestPiece = Minecraft.getInstance().player.getItemStackFromSlot(EquipmentSlotType.CHEST);
				IFillableArmor fillable = null;
				if(!chestPiece.isEmpty() && chestPiece.getItem() instanceof IFillableArmor)
					fillable = (IFillableArmor)chestPiece.getItem();
				else if(ItemAirUtils.INSTANCE.isStackValidAirContainer(chestPiece))
					fillable = new ItemAirUtils.ItemAirWrapper(chestPiece);

				if(fillable != null) {
					float size = fillable.getAirRemaining(chestPiece)/(float)fillable.getMaxAir(chestPiece);

					RenderSystem.enableBlend();
					Minecraft.getInstance().getTextureManager().bindTexture(background);
					RenderSystem.color4f(1f, 1f, 1f, 1f);
					int width = 83;
					
					int screenX = Minecraft.getInstance().getMainWindow().getScaledWidth()/2 + oxygenBar.getRenderX();//+ 8;
					int screenY = Minecraft.getInstance().getMainWindow().getScaledHeight() + oxygenBar.getRenderY();//- 57;

					//Draw BG
					this.blit(event.getMatrixStack(), screenX, screenY, 23, 0, width, 17);
					this.blit(event.getMatrixStack(),screenX , screenY, 23, 17, (int)(width*size), 17);
				}
			}

			//Draw module icons
			if(!isCreativeOrSpec && Minecraft.getInstance().player.getItemStackFromSlot(EquipmentSlotType.HEAD) != null && Minecraft.getInstance().player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() instanceof IModularArmor) {
				for(EquipmentSlotType slot : EquipmentSlotType.values()) {
					renderModuleSlots(Minecraft.getInstance().player.getItemStackFromSlot(slot), 4-slot.getIndex(), event);
				}
			}

			//In event of world change make sure the warning isn't displayed
			if(Minecraft.getInstance().world.getGameTime() - AtmosphereHandler.lastSuffocationTime < 0)
				AtmosphereHandler.lastSuffocationTime = 0;
			//Tell the player he's suffocating if needed
			if(Minecraft.getInstance().world.getGameTime() - AtmosphereHandler.lastSuffocationTime < numTicksToDisplay) {
				FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
				String str = "";
				if(AtmosphereHandler.currentAtm != null) {
					str = AtmosphereHandler.currentAtm.getDisplayMessage();
				}

				int screenX = Minecraft.getInstance().getMainWindow().getScaledWidth()/6 - fontRenderer.getStringWidth(str)/2;
				int screenY = Minecraft.getInstance().getMainWindow().getScaledHeight()/18;

				GL11.glPushMatrix();
				GL11.glScalef(3, 3, 3);

				fontRenderer.drawTextWithShadow(event.getMatrixStack(), new StringTextComponent(str), screenX, screenY, 0xFF5656);
				RenderSystem.color4f(1f, 1f, 1f, 1f);
				Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.progressBars);
				this.blit( event.getMatrixStack(), screenX + fontRenderer.getStringWidth(str)/2 -8, screenY - 16, 0, 156, 16, 16);

				GL11.glPopMatrix();
			}

			//Draw arbitrary string
			if(Minecraft.getInstance().world.getGameTime() <= lastDisplayTime) {
				FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
				GL11.glPushMatrix();
				GL11.glScalef(2,2,2);
				int loc = 0;
				for(String str : displayString.split("\n")) {

					int screenX = Minecraft.getInstance().getMainWindow().getScaledWidth()/4 - fontRenderer.getStringWidth(str)/2;
					int screenY = Minecraft.getInstance().getMainWindow().getScaledHeight()/12 + loc*(Minecraft.getInstance().getMainWindow().getScaledHeight())/12;



					fontRenderer.drawTextWithShadow(event.getMatrixStack(), new StringTextComponent(str), screenX, screenY, 0xFF5656);
					loc++;
				}

				RenderSystem.color4f(1f, 1f, 1f, 1f);
				GL11.glPopMatrix();
			}
		}
	}
	private void renderModuleSlots(ItemStack armorStack, int slot, RenderGameOverlayEvent event) {
		int index = 1;
		float color = 0.85f + 0.15F*MathHelper.sin( 2f*(float)Math.PI*((Minecraft.getInstance().world.getGameTime()) % 60)/60f );
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		float alpha = 0.6f;

		MatrixStack matrix = event.getMatrixStack();

		if( !armorStack.isEmpty() ) {

			boolean modularArmorFlag = armorStack.getItem() instanceof IModularArmor;

			if(modularArmorFlag || ItemAirUtils.INSTANCE.isStackValidAirContainer(armorStack)) {

				int size = 24;
				int screenY = suitPanel.getRenderY() + (slot-1)*(size + 8);
				int screenX = suitPanel.getRenderX();

				//Draw BG
				RenderSystem.color4f(1f, 1f, 1f, 1f);
				Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.frameHUDBG);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				RenderHelper.renderNorthFaceWithUV(matrix, buffer, this.getBlitOffset()-1, screenX - 4, screenY - 4, screenX + size, screenY + size + 4,0f,0.5f,0f,1f);
				Tessellator.getInstance().draw();

				Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.frameHUDBG);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				RenderHelper.renderNorthFaceWithUV(matrix, buffer, this.getBlitOffset()-1, screenX + size, screenY - 3, screenX + 2 + size, screenY + size + 3,0.5f,0.5f,0f,0f);
				Tessellator.getInstance().draw();

				//Draw Icon
				RenderSystem.color4f(color,color,color, color);
				Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.armorSlots[slot-1]);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				RenderHelper.renderNorthFaceWithUV(matrix, buffer, this.getBlitOffset()-1, screenX, screenY, screenX + size, screenY + size,0f,1f,1f,0f);
				Tessellator.getInstance().draw();

				if(modularArmorFlag) {
					List<ItemStack> stacks = ((IModularArmor)armorStack.getItem()).getComponents(armorStack);
					for(ItemStack stack : stacks) {
						RenderSystem.color4f(1f, 1f, 1f, 1f);
						((IArmorComponent)stack.getItem()).renderScreen(event.getMatrixStack(), stack, stacks, event, this);

						ResourceIcon icon = ((IArmorComponent)stack.getItem()).getComponentIcon(stack);
						ResourceLocation texture = null; 
						if(icon != null) 
							texture= icon.getResourceLocation();

						//if(texture != null) {

						screenX = suitPanel.getRenderX() + 4 + index*(size+2);

						//Draw BG

						Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.frameHUDBG);
						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
						RenderHelper.renderNorthFaceWithUV(matrix, buffer, this.getBlitOffset() -1, screenX - 4, screenY - 4, screenX + size - 2, screenY + size + 4,0.5f,0.5f,0f,1f);
						Tessellator.getInstance().draw();




						if(texture != null) {
							//Draw Icon
							Minecraft.getInstance().getTextureManager().bindTexture(texture);
							RenderSystem.color4f(color,color,color, alpha);
							buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
							RenderHelper.renderNorthFaceWithUV(matrix, buffer, this.getBlitOffset()-1, screenX, screenY, screenX + size, screenY + size, icon.getMinU(),icon.getMaxU(), icon.getMaxV(),icon.getMinV());
							Tessellator.getInstance().draw();
						}
						else {
							GL11.glPushMatrix();
							RenderSystem.translatef(screenX , screenY, 0);
							RenderSystem.scalef(1.5f, 1.5f, 1.5f);
							Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack,  0,0);
							GL11.glPopMatrix();
						}

						index++;
						//}
					}
				}

				screenX = (index)*(size+2) + suitPanel.getRenderX() - 12;
				//Draw BG
				RenderSystem.color4f(1,1,1, 1f);
				Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.frameHUDBG);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				RenderHelper.renderNorthFaceWithUV(matrix, buffer, (double) this.getBlitOffset()-1, (double)screenX + 12, (double)screenY - 4, (double)screenX + size, (double)screenY + size + 4,0.75f,1f,0f,1f);
				Tessellator.getInstance().draw();
			}
		}

		RenderSystem.disableAlphaTest();
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
			if(x < scaleX /3) {
				modeX = -1;
				this.setRawX(x); 
			}
			else if(x > scaleX *2/3) {
				this.setRawX((int) (scaleX - x));
				modeX = 1;
			}
			else {
				this.setRawX((int)(scaleX /2 - x));
				modeX = 0;
			}
		}

		public void setRenderY(int y, double scaleY) {
			if(y < scaleY /3) {
				modeY = -1;
				this.setRawY(y); 
			}
			else if(y > scaleY *2/3) {
				this.setRawY((int) (scaleY - y));
				modeY = 1;
			}
			else {
				this.setRawY((int)(scaleY /2 - y));
				modeY = 0;
			}
		}

		public int getRenderX() {
			int i = Minecraft.getInstance().getMainWindow().getScaledWidth();

			if( modeX == 1) {
				return i - getRawX();
			}
			else if (modeX == 0) {
				return i/2 - getRawX();
			}
			return this.getRawX();
		}

		public int getRenderY() {
			int i = Minecraft.getInstance().getMainWindow().getScaledHeight();

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
	}
}
