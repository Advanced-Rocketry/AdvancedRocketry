package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.tile.multiblock.TileEntityBlastFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiBlastFurnace extends GuiContainer {

	TileEntityBlastFurnace tile;
	public static final ResourceLocation backdrop = new ResourceLocation("advancedrocketry", "textures/gui/BlastFurnace.png");
	
	GuiBlastFurnace(InventoryPlayer inventoryPlayer, TileEntityBlastFurnace entity) {
		super(new ContainerBlastFurnace(inventoryPlayer, entity));
		tile = entity;
	}
	
	//TODO: heat
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		this.mc.renderEngine.bindTexture(backdrop);
		//get starting X and Y for the interface
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, 176, 171);
		
		if(tile.isRunning()) {
			this.drawTexturedModalRect(x + 101, y + 69,0, 171, 16, 16);
			this.drawTexturedModalRect(x + 91, y + 20, 176, 0, tile.getScaledProgress(39), 49);
		}
		if(tile.getHeat() != 0) {
			this.drawTexturedModalRect(x + 53, y + 13, 176, 49, tile.getScaledHeat(36), 3);
		}
		
		this.drawString(this.fontRendererObj, "Heat", x + 53, y+3, 0xFFFFFF);
	}
}
