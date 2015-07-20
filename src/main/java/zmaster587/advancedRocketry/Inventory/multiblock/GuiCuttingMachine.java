package zmaster587.advancedRocketry.Inventory.multiblock;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;

public class GuiCuttingMachine extends GuiMultiblock {

	public static ResourceLocation progressBar = new ResourceLocation("advancedrocketry:textures/gui/progressBars/progressBars.png");
	
	TileMultiBlockMachine tile;
	
	public GuiCuttingMachine(InventoryPlayer player, TileMultiBlockMachine multiblock) {
		super(new ContainerMultiblock(player, multiblock), player, multiblock);
		tile = multiblock;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
		
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.mc.renderEngine.bindTexture(progressBar);
		
		
		drawTexturedModalRect(x + 100, y + 20, 54, 0, 42, 42);
		
		if(tile.isRunning()) {
			drawProgressBar(x + 103, y + 23, 96, 0, 36, 36, tile.getProgress()/(float)tile.getTotalProgress(), ForgeDirection.EAST);
		}
	}

}
