package zmaster587.advancedRocketry.Inventory.multiblock;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;

public class GuiCrystallizer extends GuiMultiblock {

	public static ResourceLocation progressBar = new ResourceLocation("advancedrocketry:textures/gui/progressBars/progressBars.png");
	
	TileMultiBlockMachine tile;
	
	public GuiCrystallizer(InventoryPlayer player, TileMultiBlockMachine multiblock) {
		super(new ContainerMultiblock(player, multiblock), player, multiblock);
		tile = multiblock;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
		
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.mc.renderEngine.bindTexture(progressBar);
		
		
		drawTexturedModalRect(x + 120, y + 4, 0, 0, 31, 66);
		
		if(tile.isRunning()) {
			drawProgressBarVertical(x + 124, y + 21, 31, 0, 23, 49, tile.getProgress()/(float)tile.getTotalProgress());
		}
	}

}
