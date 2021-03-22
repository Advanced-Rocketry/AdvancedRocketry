/**
 * @author zmaster587
 * 
 */

package zmaster587.advancedRocketry.inventory;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import zmaster587.advancedRocketry.tile.multiblock.drill.TileOrbitalLaserDrill;
import zmaster587.libVulpes.gui.GuiImageButton;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;

import java.io.IOException;
import java.util.LinkedList;

public class GuiOrbitalLaserDrill extends GuiContainer {

	private ResourceLocation backdrop = new ResourceLocation("advancedrocketry", "textures/gui/LaserTile.png");
	private TileOrbitalLaserDrill laserTile;
	private int prevX, prevZ;

	GuiTextField xbox,ybox;
	GuiImageButton modeUp, modeDown;

	public GuiOrbitalLaserDrill(InventoryPlayer inventoryPlayer, TileOrbitalLaserDrill tile) {
		super(new ContainerOrbitalLaserDrill(inventoryPlayer, tile));
		laserTile = tile;
	}

	//TODO: enter button
	@Override
	public void initGui() {
		super.initGui();

		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		//Create and setup the textboxes
		xbox = new GuiTextField(0,this.fontRenderer, x + 113, y + 31, 50, 10);
		ybox = new GuiTextField(1,this.fontRenderer, x + 113, y + 41, 50, 10);
		xbox.setMaxStringLength(15);
		xbox.setEnableBackgroundDrawing(true);
		xbox.setFocused(true);
		xbox.setCanLoseFocus(true);
		xbox.setEnabled(true);
		ybox.setMaxStringLength(15);
		ybox.setEnableBackgroundDrawing(true);
		ybox.setFocused(false);
		ybox.setCanLoseFocus(true);
		ybox.setEnabled(true);

		modeDown = new GuiImageButton(0, x + 103, y + 20, 5, 8, zmaster587.libVulpes.inventory.TextureResources.buttonLeft);
		modeUp = new GuiImageButton(1, x + 157, y + 20, 5, 8,  zmaster587.libVulpes.inventory.TextureResources.buttonRight);
		this.buttonList.add(modeUp);
		this.buttonList.add(modeDown);
		this.buttonList.add(new GuiButton(2, x + 103, y + 62, 34,20, "Reset"));
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException
	{
		//Don't let players change the coords while the machine is running
		if(!laserTile.isRunning()) {
			if(Character.isDigit(par1) || par1 == '-' || par2 == Keyboard.KEY_BACK || par2 == Keyboard.KEY_DELETE || par2 == Keyboard.KEY_LEFT || par2 == Keyboard.KEY_RIGHT) {
				if(xbox.isFocused() && (par1 != '-' || (xbox.getCursorPosition() == 0 && !xbox.getText().startsWith("-")))) {
					xbox.textboxKeyTyped(par1, par2);

					if(!xbox.getText().isEmpty() && !xbox.getText().contentEquals("-"))
						laserTile.laserX = Integer.parseInt(xbox.getText());

					PacketHandler.sendToServer(new PacketMachine(laserTile,(byte) 0));
				}
				else if(ybox.isFocused() && (par1 != '-' || (ybox.getCursorPosition() == 0 && !ybox.getText().startsWith("-")))){
					ybox.textboxKeyTyped(par1, par2);
					if(!ybox.getText().isEmpty() && !ybox.getText().contentEquals("-"))
						laserTile.laserZ = Integer.parseInt(ybox.getText());

					PacketHandler.sendToServer(new PacketMachine(laserTile,(byte) 1));
				}
			}
		}

		if(par2 == Keyboard.KEY_TAB) {
			if(xbox.isFocused()) {
				xbox.setFocused(false);
				ybox.setFocused(true);
			}
			else if(ybox.isFocused()) {
				xbox.setFocused(true);
				ybox.setFocused(false);
			}
		}
		
		super.keyTyped(par1, par2);
	}

	//Overridden so the mouse works properly
	@Override
	protected void actionPerformed(GuiButton guiButton) {	
		//TODO: debug
		if(guiButton.id == 0){
			laserTile.decrementMode();
		}
		else if(guiButton.id == 1) {
			laserTile.incrementMode();
		}
		else if(guiButton.id == 2) {
			PacketHandler.sendToServer(new PacketMachine(laserTile, (byte)4));
			return;
		}
		else 
			return;

		if(!laserTile.isRunning())
			PacketHandler.sendToServer(new PacketMachine(laserTile, (byte)3));
	}

	@Override
	public void drawScreen(int a, int b, float c){
		super.drawScreen(a,b,c);
		//Don't bother updating unless somthing has changed
		if(laserTile.laserX != prevX) {
			xbox.setText(String.valueOf(laserTile.laserX));
			prevX = laserTile.laserX;
		}
		if(laserTile.laserZ != prevZ) {
			ybox.setText(String.valueOf(laserTile.laserZ));
			prevZ = laserTile.laserZ;
		}
		xbox.drawTextBox();
		ybox.drawTextBox();

		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		
		if(a > x + 11 && a < x + 27 && b < y + 85 && b > y + 43) {
			LinkedList<String> text = new LinkedList<String>();
			text.add(laserTile.getBatteries().getUniversalEnergyStored()  + " / " + laserTile.getBatteries().getMaxEnergyStored() + " RF");
			this.drawHoveringText(text, a, b, this.fontRenderer);
		}

		//Buttons don't need to be drawn because that is taken care of by super
	}

	/**
	 * Args: x, y, buttonClicked
	 * @throws IOException 
	 */
	@Override
	public void mouseClicked(int xPos, int yPos, int par3) throws IOException
	{
		//Make sure we can focus the textboxes
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		if(!xbox.isFocused() && yPos < y + 40 && yPos > y + 31 && xPos < x + 163 && xPos > x + 113) {
			xbox.setFocused(true);
			ybox.setFocused(false);
		}

		if(!ybox.isFocused() && yPos < y + 50 && yPos > y + 41 && xPos < x + 163 && xPos > x + 113){
			ybox.setFocused(true);
			xbox.setFocused(false);
		}

		super.mouseClicked(xPos, yPos, par3);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		this.mc.renderEngine.bindTexture(backdrop);
		//get starting X and Y for the interface
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, 176, 171);

		//Render the energy bar only if energy exists
		if(laserTile.hasEnergy()) {
			int offset = laserTile.getEnergyPercentScaled(42);
			this.drawTexturedModalRect(x + 11, y + 85 - offset, 176, 42 - offset, 16, offset);
		}

		//Display status
		if(laserTile.isFinished() || laserTile.getStackInSlot(0) == null)
			this.drawTexturedModalRect(x + 145, y + 64, 32, 171, 16, 16);
		else if(laserTile.isRunning() || laserTile.isJammed())
			this.drawTexturedModalRect(x + 145, y + 64, 16, 171, 16, 16);
		else
			this.drawTexturedModalRect(x + 145, y + 64, 0, 171, 16, 16);

		//Draw labels next to the input boxes
		this.drawString(this.fontRenderer, "X:", x + 103, y + 31, 0xffffff);
		this.drawString(this.fontRenderer, "Y:", x + 103, y + 41, 0xffffff);

		//Draw jamming indicator if jammed
		if(laserTile.isJammed())
			this.drawString(this.fontRenderer, "Jam!", x + 143, y + 68, 0xffffff);

		//Draw mode indicator
		this.drawCenteredString(this.fontRenderer, laserTile.getMode().toString(), x + 130, y + 20, 0xffffff);
	}
}
