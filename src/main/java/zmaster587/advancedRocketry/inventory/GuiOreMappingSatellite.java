package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import zmaster587.advancedRocketry.client.render.ClientDynamicTexture;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.render.RenderHelper;
import java.nio.IntBuffer;

public class GuiOreMappingSatellite extends ContainerScreen<ContainerOreMappingSatallite> {

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
	private int xSelected, zSelected, xCenter, zCenter, playerPosX, playerPosZ;
	private static final ResourceLocation backdrop = new ResourceLocation("advancedrocketry", "textures/gui/VideoSatallite.png");
	int[][] oreMap;
	World world;
	SatelliteOreMapping tile;

	public GuiOreMappingSatellite(ContainerOreMappingSatallite container, PlayerInventory inventoryPlayer, ITextComponent title) {
		super( container, inventoryPlayer, title);
		world = container.player.world;

		prevSlot = -1;
		this.tile = container.inv;
		//masterConsole = tile;
		playerPosX = xCenter = (int) container.player.getPosX();
		playerPosZ = zCenter = (int) container.player.getPosZ();

		//Max zoom is 128
		if(tile != null)
			maxZoom = (int) Math.pow(2, tile.getZoomRadius());

		if(maxZoom == 1)
			this.tile = null;
		scanSize = maxZoom;

		prevWorldTickTime = world.getGameTime();

		fancyScanOffset = 0;
	}

	//Create separate thread to do this because it takes a while!
	Runnable mapper = new Runnable() {
		@Override
		public void run() {
			oreMap = SatelliteOreMapping.scanChunk(world, xCenter, zCenter, scanSize/2, radius);
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
			oreMap = SatelliteOreMapping.scanChunk(world, xCenter, zCenter, scanSize/2, radius, myBlock);
			if(oreMap != null && !Thread.interrupted())
				merged = true;
			else merged = false;
		}
	};

	//Don't pause the game whilst player is looking at the satellite
	public boolean doesGuiPauseGame(){ return false; }

	private void runMapperWithSelection() {
		if(tile == null)
			return;

		currentMapping.interrupt();
		resetTexture();
		if(prevSlot == -1) {
			currentMapping = new Thread(mapper);
			currentMapping.setName("Ore Scan");
		}
		else {

			currentMapping = new Thread(new ItemMapper(this.playerInventory.getStackInSlot(prevSlot).getStack()));
			currentMapping.setName("Ore Scan");
		}
		currentMapping.start();
	}

	//onMouseclicked
	@Override
	public boolean mouseReleased(double x, double y, int button) {
		// TODO Auto-generated method stub
		boolean val = super.mouseReleased(x, y, button);

		int xOffset = 47 + (width - 240) / 2, yOffset = 20 + (height - 192) / 2;

		//Get selected slot and begin scan!
		if(button == 0 && tile.getSelectedSlot() != prevSlot) {
			prevSlot = tile.getSelectedSlot();
			runMapperWithSelection();
		}

		//Clicked off screen don't do anything
		if(x < xOffset || x > xOffset + SCREEN_SIZE || y < yOffset || y > yOffset + SCREEN_SIZE)
			return false;

		//If the grid is displayed get the value at this location
		if(oreMap != null) {
			double numPixels = (scanSize/(float)(SCREEN_SIZE*radius));
			mouseValue = oreMap[(int)((x - xOffset) * numPixels)][(int)((y - yOffset) * numPixels)]/0xFF;

			xSelected = (int)((x - xOffset) * numPixels) + xCenter - (radius*scanSize/2);
			zSelected = (int)((y - yOffset) * numPixels) + zCenter - (radius*scanSize/2);
		}
		return val;
	}



	@Override
	protected boolean itemStackMoved(int keyCode, int scanCode)  {
		if(keyCode == GLFW.GLFW_KEY_W) {
			zCenter -= radius;
			runMapperWithSelection();
		}
		else if(keyCode == GLFW.GLFW_KEY_S) {
			zCenter += radius;

			runMapperWithSelection();
		}
		else if(keyCode == GLFW.GLFW_KEY_A) {
			xCenter -= radius;

			runMapperWithSelection();
		}
		else if(keyCode == GLFW.GLFW_KEY_A) {
			xCenter += radius;

			runMapperWithSelection();
		}
		else if(keyCode == GLFW.GLFW_KEY_DOWN){
			scanSize = Math.min(scanSize*2, maxZoom);

			runMapperWithSelection();
		}
		else if(keyCode == GLFW.GLFW_KEY_UP) {
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
			return super.itemStackMoved(keyCode, scanCode);
		return true;
	}

	//Create our image here
	// InitGui
	@Override
	public void init() {
		super.init();
		texture = new ClientDynamicTexture(Math.max(scanSize/radius,1),Math.max(scanSize/radius,1));

		ItemStack stack = this.playerInventory.getStackInSlot(0).getStack();

		if(tile != null) {
			currentMapping = new Thread(mapper);
			currentMapping.setName("Ore Scan");
			currentMapping.start();
		}
	}

	/*@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

	}*/

	//Reset the texture and prevent memory leaks
	private void resetTexture() {
		GL11.glDeleteTextures(texture.getTextureId());
		texture = new ClientDynamicTexture(Math.max(scanSize/radius,1),Math.max(scanSize/radius,1));
	}

	@Override
	public void onClose() {
		super.onClose();
		//Delete texture and stop any mapping on close
		GL11.glDeleteTextures(texture.getTextureId());
		if(currentMapping != null)
			currentMapping.interrupt();
	}



	// Draw foreground
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int a, int b)  {

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		//Draw fancy things
		RenderSystem.disableTexture();
		RenderSystem.color4f(0f, 0.8f, 0f,1f);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(-21, 82 + fancyScanOffset, (double)this.getBlitOffset()).endVertex();
		buffer.pos(0, 84 + fancyScanOffset, (double)this.getBlitOffset()).endVertex();
		buffer.pos(0, 81 + fancyScanOffset, (double)this.getBlitOffset()).endVertex();
		buffer.pos(-21, 81 + fancyScanOffset, (double)this.getBlitOffset()).endVertex();
		Tessellator.getInstance().draw();


		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		buffer.pos(-21, 82 - fancyScanOffset + FANCYSCANMAXSIZE, (double)this.getBlitOffset()).endVertex();
		buffer.pos(0, 84 - fancyScanOffset + FANCYSCANMAXSIZE, (double)this.getBlitOffset()).endVertex();
		buffer.pos(0, 81 - fancyScanOffset + FANCYSCANMAXSIZE, (double)this.getBlitOffset()).endVertex();
		buffer.pos(-21, 81 - fancyScanOffset + FANCYSCANMAXSIZE, (double)this.getBlitOffset()).endVertex();
		Tessellator.getInstance().draw();


		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
		RenderSystem.color4f(0.5f, 0.5f, 0.0f,0.3f + ((float)Math.sin(Math.PI*(fancyScanOffset/(float)FANCYSCANMAXSIZE))/3f));

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		RenderHelper.renderNorthFace(matrix, buffer, this.getBlitOffset(), 173, 82, 194, 141,1f,1f,1f,1f);
		Tessellator.getInstance().draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);


		if(world.getGameTime() - prevWorldTickTime >= 1 ) {
			prevWorldTickTime = world.getGameTime();
			if(fancyScanOffset >= FANCYSCANMAXSIZE)
				fancyScanOffset = 0;
			else
				fancyScanOffset++;
		}


		//If a slot is selected draw an indicator
		int slot;
		if(tile != null && (slot = tile.getSelectedSlot()) != -1) {

			RenderSystem.disableTexture();
			RenderSystem.color4f(0f, 0.8f, 0f, 1f);

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(matrix, buffer, getBlitOffset(), 13 + (18*slot), 155, 13 + 16 + (18*slot), 155 + 16, 0, 0, 0, 0);
			Tessellator.getInstance().draw();
			RenderSystem.enableTexture();
		}


	}

	// Draw background
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float f1, int i2, int i3)  {
		int x = (width - 240) / 2, y = (height - 192) / 2;

		//If the scan is done then 
		if(merged) {
			IntBuffer buffer = texture.getByteBuffer();
			int scanWidth = Math.max(scanSize/radius,1);

			try {
				for(int yt = 0; yt < (texture.getHeight() * texture.getWidth()); yt++) {
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
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bindTexture(backdrop);
		this.blit(matrix, x, y, 0, 0, 240, 192);


		//NOTE: if the controls are rendered first the display never shows up
		//Draw the actual display
		RenderSystem.bindTexture( texture.getTextureId() );

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		RenderHelper.renderNorthFaceWithUV(matrix, buffer, this.getBlitOffset(), 47 + x, 20 + y, 47 + x + SCREEN_SIZE, 20 + y + SCREEN_SIZE, 0, 1, 0, 1);
		Tessellator.getInstance().draw();


		//Render player location
		float offsetX = playerPosX - xCenter + 0.5f;
		float offsetY = zCenter - playerPosZ + 0.5f;
		double numPixels = SCREEN_SIZE/scanSize;//(scanSize/(float)(SCREEN_SIZE*radius));


		float radius = 2;
		if(Math.abs(offsetX) < scanSize/2 && Math.abs(offsetY) < scanSize/2) {
			offsetX *= numPixels;
			offsetY *= numPixels;

			RenderSystem.disableTexture();
			RenderSystem.color4f(0.4f, 1f, 0.4f,1f);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUV(matrix, buffer, this.getBlitOffset(), offsetX + 47 + x + SCREEN_SIZE/2 - radius,  offsetY + 20 + y + SCREEN_SIZE/2 - radius, offsetX + 47 + x + SCREEN_SIZE/2 + radius, offsetY + 20 + y + SCREEN_SIZE/2 + radius, 0, 1, 0, 1);
			Tessellator.getInstance().draw();
			RenderSystem.color4f(1, 1, 1,1f);
			RenderSystem.enableTexture();
			this.drawCenteredString(matrix, this.font, "You", (int)(offsetX + 47 + x + SCREEN_SIZE/2 - radius), (int)(offsetY + 20 + y + SCREEN_SIZE/2 - radius) -10, 0xF0F0F0);
		}

		//Render sliders and controls
		Minecraft.getInstance().getTextureManager().bindTexture(backdrop);

		this.blit(matrix, 197 + x, 31 + y, 0, 192, 32, 14);
		//this.drawVerticalLine((int)(32*VulpineMath.log2(scanSize-1)/8F) + 199 + x, 34 + y, 45 + y, 0xFFC00F0F);
		this.drawString(matrix, this.font, "Zoom", 198 + x, 22 + y, 0xF0F0F0);
		this.drawString(matrix, this.font, "X: " + xSelected, 6 + x, 33 + y, 0xF0F0F0);
		this.drawString(matrix, this.font, "Z: " + zSelected, 6 + x, 49 + y, 0xF0F0F0);
		this.drawString(matrix, this.font,  LibVulpes.proxy.getLocalizedString("msg.itemorescanner.value"), 6 + x, 65 + y, 0xF0F0F0);
		this.drawString(matrix, this.font, String.valueOf(mouseValue), 6 + x, 79 + y, 0xF0F0F0);
	}

	public static void drawString(MatrixStack matrix, FontRenderer font, String str, int x, int z, int color)
	{
		font.func_243246_a(matrix, new StringTextComponent(str), x, z, 0);
	}
	

	public static void drawCenteredString(MatrixStack matrix, FontRenderer font, String str, int x, int z, int color)
	{
		font.func_243246_a(matrix, new StringTextComponent(str), x, z, 0);
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
    public void drawScreen(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
        this.renderBackground(matrix); // DrawDefaultWorldBackground
        super.render(matrix, mouseX, mouseY, partialTicks); //drawScreen
        this.renderHoveredTooltip(matrix, mouseX, mouseY); // renderHoveredToolTip
	}
}
