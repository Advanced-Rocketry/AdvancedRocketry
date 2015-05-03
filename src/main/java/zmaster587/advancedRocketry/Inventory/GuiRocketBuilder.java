package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.gui.GuiImageButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiRocketBuilder extends GuiContainer {

	
	private ResourceLocation backdrop = new ResourceLocation("advancedrocketry","textures/gui/rocketBuilder.png");
	private ResourceLocation buttonScan[] = {new ResourceLocation("advancedrocketry", "textures/gui/GuiScan.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiScan_hover.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiScan_pressed.png"), null};
	private ResourceLocation buttonBuild[] = {new ResourceLocation("advancedrocketry", "textures/gui/GuiButtonRed.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiButtonRed_hover.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiButtonRed_pressed.png"), null};
	
	private TileRocketBuilder tile;
	GuiImageButton scan;
	GuiImageButton build;
	
	public GuiRocketBuilder(InventoryPlayer playerInv, TileRocketBuilder tile) {
		super(new ContainerRocketBuilder(playerInv, tile));
		
		this.tile = tile;
	}

	@Override
	public void initGui() {
		super.initGui();
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		scan = new GuiImageButton(0, x + 6, y + 92, 64, 20, buttonScan);
		build =  new GuiImageButton(0, x + 6, y + 116, 64, 20, buttonBuild);
		buttonList.add(scan);
		buttonList.add(build);
	}
	
	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		
		if(button == scan) {
			PacketHandler.sendToServer(new PacketMachine(tile, (byte)0));
		}
		else if(button == build) {
			PacketHandler.sendToServer(new PacketMachine(tile, (byte)1));
		}
	}
	
	
	@Override
	protected void drawGuiContainerForegroundLayer(int a,
			int b) {
		super.drawGuiContainerForegroundLayer(a, b);
		
		//Draw Button Text
				this.drawString(fontRendererObj, "Scan", 24, 99, 0xFF22FF22);
				this.drawString(fontRendererObj, "Build", 24, 123, 0xFFFF2222);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		
		this.mc.renderEngine.bindTexture(backdrop);
		
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, 176, 170);
		
		boolean enoughThrust = tile.getAcceleration() > 0;
		boolean isScanning = tile.isScanning();
		
		float fuelAmt = enoughThrust ? MathHelper.clamp_float(0.5f + 0.5f*((tile.getFuel()-tile.getNeededFuel())/tile.getNeededFuel()), 0f, 1f) : 0;
		float accAmt = MathHelper.clamp_float(0.5f + tile.getAcceleration()/200f, 0f, 1f);
		String thrustStr = isScanning ? "Thrust: ???" :  String.format("Thrust: %dN",tile.getThrust());
		String weightStr = isScanning ? "Weight: ???"  : String.format("Weight: %dkg",tile.getWeight());
		String fuelStr = isScanning ? "Fuel: ???" :  String.format("Fuel: %.1fmb/s",tile.getRocketStats().getFuelRate()/(20f));
		String accStr = isScanning ? "Acc: ???" : String.format("Acc: %.1fm/s", tile.getAcceleration());
		
		
		//Draw bar showing player how much fuel they have vs need
		this.drawTexturedModalRect(x+89, y+49, 176, 0, (int)((isScanning ? 0 : fuelAmt)*80), 15);
		
		//Draw bar to indicate acceleration amtNeeded
		this.drawTexturedModalRect(x+89, y+68, 176, 0, (int)((isScanning ? 0 : accAmt)*80), 15);
		
		//Draw ProgressBar
		if(isScanning) {
			this.drawTexturedModalRect(x+114, y+93 + (38-(int)(tile.getNormilizedScanTime()*38)), 176, 53-(int)(tile.getNormilizedScanTime()*38), 2,(int)(tile.getNormilizedScanTime()*38));
		}
		
		this.drawString(fontRendererObj, thrustStr, x + 8, y+15, 0xFF22FF22);
		
		this.drawString(fontRendererObj, weightStr, x + 8, y+34, 0xFF22FF22);
		
		this.drawString(fontRendererObj, fuelStr, x + 8, y+52, 0xFF22FF22);
		
		//Render string showing players fuel numbers
		if(enoughThrust && !isScanning)
			this.drawString(fontRendererObj, String.format("%.1f/%.1f l",tile.getFuel()/1000f,tile.getNeededFuel()/1000f), x + 92, y+52, (tile.getFuel() > tile.getNeededFuel()) ? 0xFF22FF22 : 0xFFFF2222);
		else
			this.drawString(fontRendererObj, "N/A", x + 92, y+52, 0xFF22FF22);
		
		this.drawString(fontRendererObj, accStr, x + 8, y+71, 0xFF22FF22);
		
		

		
	}

}
