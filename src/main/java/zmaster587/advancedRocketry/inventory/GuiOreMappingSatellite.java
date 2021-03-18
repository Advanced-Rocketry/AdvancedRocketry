package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.client.render.ClientDynamicTexture;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.util.VulpineMath;

import java.io.IOException;
import java.nio.IntBuffer;

public class GuiOreMappingSatellite extends GuiContainer {

	ClientDynamicTexture texture;
	Thread currentMapping;
	TileEntity masterConsole;
	boolean merged = false;
	private static final int SCREEN_SIZE = 146;
	private int maxZoom = 128;
	private static final int MAXRADIUS = 16;
	private static final int FANCYSCANMAXSIZE = 57;
	private int fancyScanOffset;
	private long prevWorldTickTime;
	private int prevSlot;
	private int mouseValue;
	private int scanSize = 2;
	private int radius = 1;
	private int zoomScale;
	private int xSelected, zSelected, xCenter, zCenter, playerPosX, playerPosZ;
	private static final ResourceLocation backdrop = new ResourceLocation("advancedrocketry", "textures/gui/VideoSatallite.png");
	int[][] oreMap;
	World world;
	SatelliteOreMapping satellite;

	public GuiOreMappingSatellite(SatelliteOreMapping satellite,EntityPlayer inventoryPlayer) {
		super( new ContainerOreMappingSatallite(satellite,inventoryPlayer.inventory));
		world = inventoryPlayer.world;

		prevSlot = -1;
		this.satellite = satellite;
		//masterConsole = satellite;
		playerPosX = xCenter = (int) inventoryPlayer.posX;
		playerPosZ = zCenter = (int) inventoryPlayer.posZ;

		//Max zoom is 128
		if(satellite != null) {
			maxZoom = (int) Math.pow(2, satellite.getZoomRadius());
			zoomScale = satellite.getZoomRadius();
		}

		if(maxZoom == 1)
			this.satellite = null;
		scanSize = maxZoom;

		prevWorldTickTime = world.getTotalWorldTime();

		fancyScanOffset = 0;
	}

	//Create separate thread to do this because it takes a while!
	Runnable mapper = new Runnable() {
		@Override
		public void run() {
			oreMap = satellite.scanChunk(world, xCenter, zCenter, scanSize/2, radius, zoomScale);
			if(oreMap != null && !Thread.interrupted())
				merged = true;
			else merged = false;
		}
	};

	//Create separate thread to do this because it takes a while!
	class ItemMapper implements Runnable {
		private ItemStack myBlock;

		ItemMapper(ItemStack block) {
			//Copy so we dont have any possible CME or oddness due to that
			myBlock = block.copy();
		}

		@Override
		public void run() {
			oreMap = satellite.scanChunk(world, xCenter, zCenter, scanSize/2, radius, myBlock, zoomScale);
			if(oreMap != null && !Thread.interrupted())
				merged = true;
			else merged = false;
		}
	};

	//Don't pause the game whilst player is looking at the satellite
	public boolean doesGuiPauseGame(){ return false; }

	private void runMapperWithSelection() {
		if(satellite == null)
			return;

		currentMapping.interrupt();
		resetTexture();
		if(prevSlot == -1) {
			currentMapping = new Thread(mapper);
			currentMapping.setName("Ore Scan");
		}
		else {

			currentMapping = new Thread(new ItemMapper(inventorySlots.getSlot(prevSlot).getStack()));
			currentMapping.setName("Ore Scan");
		}
		currentMapping.start();
	}

	@Override
	protected void mouseClicked(int x, int y,
			int button) throws IOException {
		// TODO Auto-generated method stub
		super.mouseClicked(x, y, button);

		int xOffset = 47 + (width - 240) / 2, yOffset = 20 + (height - 192) / 2;

		//Get selected slot and begin scan!
		if(button == 0 && satellite.getSelectedSlot() != prevSlot) {
			prevSlot = satellite.getSelectedSlot();
			runMapperWithSelection();
		}

		//Clicked off screen don't do anything
		if(x < xOffset || x > xOffset + SCREEN_SIZE || y < yOffset || y > yOffset + SCREEN_SIZE)
			return;

		//If the grid is displayed get the value at this location
		if(oreMap != null) {
			double numPixels = (scanSize/(float)(SCREEN_SIZE*radius));
			mouseValue = oreMap[(int)((x - xOffset) * numPixels)][(int)((y - yOffset) * numPixels)]/0xFF;

			xSelected = (int)((x - xOffset) * numPixels) + xCenter - (radius*scanSize/2);
			zSelected = (int)((y - yOffset) * numPixels) + zCenter - (radius*scanSize/2);
		}

	}



	@Override
	protected void keyTyped(char c, int i) throws IOException {
		if(i == Keyboard.KEY_W) {
			zCenter -= radius;
			runMapperWithSelection();
		}
		else if(i == Keyboard.KEY_S) {
			zCenter += radius;

			runMapperWithSelection();
		}
		else if(i == Keyboard.KEY_A) {
			xCenter -= radius;

			runMapperWithSelection();
		}
		else if(i == Keyboard.KEY_D) {
			xCenter += radius;

			runMapperWithSelection();
		}
		else if(i == Keyboard.KEY_DOWN){
			scanSize = Math.min(scanSize*2, maxZoom);

			runMapperWithSelection();
		}
		else if(i == Keyboard.KEY_UP) {
			if((scanSize/2)/radius > 0) {
				scanSize = Math.max(scanSize/2, 2);

				runMapperWithSelection();
			}
		}
		//TODO: fix radius
		/*else if(i == Keyboard.KEY_LEFT) {
			radius = Math.max(radius / 2, 1);

			currentMapping.interrupt();
			resetTexture();
			currentMapping = new Thread(mapper);
			currentMapping.start();
		} else if(i == Keyboard.KEY_RIGHT) {
			if(scanSize/(radius*2) > 0) {
				radius = Math.min(radius*2, MAXRADIUS);
				currentMapping.interrupt();
				resetTexture();
				currentMapping = new Thread(mapper);
				currentMapping.start();
			}
		}*/
		else 
			super.keyTyped(c, i);
	}

	//Create our image here
	@Override
	public void initGui() {
		super.initGui();
		texture = new ClientDynamicTexture(Math.max(scanSize/radius,1),Math.max(scanSize/radius,1));

		ItemStack stack = inventorySlots.getSlot(0).getStack();

		if(satellite != null) {
			currentMapping = new Thread(mapper);
			currentMapping.setName("Ore Scan");
			currentMapping.start();
		}
	}

	//Reset the texture and prevent memory leaks
	private void resetTexture() {
		GL11.glDeleteTextures(texture.getTextureId());
		texture = new ClientDynamicTexture(Math.max(scanSize/radius,1),Math.max(scanSize/radius,1));
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		//Delete texture and stop any mapping on close
		GL11.glDeleteTextures(texture.getTextureId());
		if(currentMapping != null)
			currentMapping.interrupt();
	}



	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_,	int p_146979_2_) {

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		//Draw fancy things
		GlStateManager.disableTexture2D();
		GlStateManager.color(0f, 0.8f, 0f);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(-21, 82 + fancyScanOffset, (double)this.zLevel).endVertex();
		buffer.pos(0, 84 + fancyScanOffset, (double)this.zLevel).endVertex();
		buffer.pos(0, 81 + fancyScanOffset, (double)this.zLevel).endVertex();
		buffer.pos(-21, 81 + fancyScanOffset, (double)this.zLevel).endVertex();
		Tessellator.getInstance().draw();


		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(-21, 82 - fancyScanOffset + FANCYSCANMAXSIZE, (double)this.zLevel).endVertex();
		buffer.pos(0, 84 - fancyScanOffset + FANCYSCANMAXSIZE, (double)this.zLevel).endVertex();
		buffer.pos(0, 81 - fancyScanOffset + FANCYSCANMAXSIZE, (double)this.zLevel).endVertex();
		buffer.pos(-21, 81 - fancyScanOffset + FANCYSCANMAXSIZE, (double)this.zLevel).endVertex();
		Tessellator.getInstance().draw();


		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
		GlStateManager.color(0.5f, 0.5f, 0.0f,0.3f + ((float)Math.sin(Math.PI*(fancyScanOffset/(float)FANCYSCANMAXSIZE))/3f));

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
		RenderHelper.renderNorthFace(buffer, this.zLevel, 173, 82, 194, 141);
		Tessellator.getInstance().draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);


		if(world.getTotalWorldTime() - prevWorldTickTime >= 1 ) {
			prevWorldTickTime = world.getTotalWorldTime();
			if(fancyScanOffset >= FANCYSCANMAXSIZE)
				fancyScanOffset = 0;
			else
				fancyScanOffset++;
		}


		//If a slot is selected draw an indicator
		int slot;
		if(satellite != null && (slot = satellite.getSelectedSlot()) != -1) {

			GlStateManager.disableTexture2D();
			GlStateManager.color(0f, 0.8f, 0f, 1f);

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel, 13 + (18*slot), 155, 13 + 16 + (18*slot), 155 + 16, 0, 0, 0, 0);
			Tessellator.getInstance().draw();
			GlStateManager.enableTexture2D();
		}


	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		int x = (width - 240) / 2, y = (height - 192) / 2;

		//If the scan is done then 
		if(merged) {
			IntBuffer buffer = texture.getByteBuffer();
			int scanWidth = Math.max(scanSize/radius,1);

			try {
				for(int yt = 0; yt < (texture.getImage().getHeight() * texture.getImage().getWidth()); yt++) {
					buffer.put(yt, oreMap[yt % scanWidth][scanWidth - 1 - yt / scanWidth] | 0xFF000000);
				}
				buffer.flip();
				texture.setByteBuffer(buffer);
			} catch (IndexOutOfBoundsException e)
			{
				
			}
			merged = false;
		}

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		//Render the background then render
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(backdrop);
		this.drawTexturedModalRect(x, y, 0, 0, 240, 192);


		//NOTE: if the controls are rendered first the display never shows up
		//Draw the actual display
		GlStateManager.bindTexture( texture.getTextureId() );

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel, 47 + x, 20 + y, 47 + x + SCREEN_SIZE, 20 + y + SCREEN_SIZE, 0, 1, 0, 1);
		Tessellator.getInstance().draw();


		//Render player location
		float offsetX = playerPosX - xCenter + 0.5f;
		float offsetY = zCenter - playerPosZ + 0.5f;
		double numPixels = SCREEN_SIZE/scanSize;//(scanSize/(float)(SCREEN_SIZE*radius));


		float radius = 2;
		if(Math.abs(offsetX) < scanSize/2 && Math.abs(offsetY) < scanSize/2) {
			offsetX *= numPixels;
			offsetY *= numPixels;

			GlStateManager.disableTexture2D();
			GlStateManager.color(0.4f, 1f, 0.4f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(buffer, this.zLevel, offsetX + 47 + x + SCREEN_SIZE/2 - radius,  offsetY + 20 + y + SCREEN_SIZE/2 - radius, offsetX + 47 + x + SCREEN_SIZE/2 + radius, offsetY + 20 + y + SCREEN_SIZE/2 + radius, 0, 1, 0, 1);
			Tessellator.getInstance().draw();
			GlStateManager.color(1, 1, 1);
			GlStateManager.enableTexture2D();
			this.drawCenteredString(this.fontRenderer, "You", (int)(offsetX + 47 + x + SCREEN_SIZE/2 - radius), (int)(offsetY + 20 + y + SCREEN_SIZE/2 - radius) -10, 0xF0F0F0);
		}

		//Render sliders and controls
		this.mc.renderEngine.bindTexture(backdrop);

		this.drawTexturedModalRect(197 + x, 31 + y, 0, 192, 32, 14);
		this.drawVerticalLine((int)(32*VulpineMath.log2(scanSize-1)/8F) + 199 + x, 34 + y, 45 + y, 0xFFC00F0F);
		//this.drawTexturedModalRect(197 + x, 63 + y, 0, 192, 32, 14);
		//this.drawVerticalLine((int)(28*MathVulpes.log2(radius)/4F) + 199 + x, 67 + y, 77 + y, 0xFF000000);
		this.drawString(this.fontRenderer, "Zoom", 198 + x, 22 + y, 0xF0F0F0);
		//this.drawString(this.fontRendererObj, "Clarity", 198 + x, 52 + y, 0xb0b0b0);
		this.drawString(this.fontRenderer, "X: " + xSelected, 6 + x, 33 + y, 0xF0F0F0);
		this.drawString(this.fontRenderer, "Z: " + zSelected, 6 + x, 49 + y, 0xF0F0F0);
		this.drawString(this.fontRenderer,  LibVulpes.proxy.getLocalizedString("msg.itemorescanner.value"), 6 + x, 65 + y, 0xF0F0F0);
		this.drawString(this.fontRenderer, String.valueOf(mouseValue), 6 + x, 79 + y, 0xF0F0F0);
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
