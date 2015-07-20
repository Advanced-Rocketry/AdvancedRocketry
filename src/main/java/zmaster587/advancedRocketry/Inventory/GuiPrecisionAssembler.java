package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.Inventory.multiblock.ContainerMultiblock;
import zmaster587.advancedRocketry.Inventory.multiblock.GuiMultiblock;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class GuiPrecisionAssembler extends GuiMultiblock {

	TilePrecisionAssembler tile;
	public static final ResourceLocation backdrop = new ResourceLocation("advancedrocketry:textures/gui/progressBars/progressBars.png");

	public GuiPrecisionAssembler(InventoryPlayer player, TilePrecisionAssembler multiblock) {
		super(new ContainerMultiblock(player, multiblock), player, multiblock);

		tile = multiblock;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		super.drawGuiContainerBackgroundLayer(f, i, j);
		
		this.mc.renderEngine.bindTexture(backdrop);
		//get starting X and Y for the interface
		int x = 40 + (width - xSize) / 2, y = 15 + (height - ySize) / 2;

		drawTexturedModalRect(x + 58,  y + 1, 132, 0, 53, 66);
		
		//Instead of calling drawTexturedModalRect thrice... just use a byte to store the offset
		if(tile.isRunning()) {
			//Add progress bars
			byte step = (byte)((3*tile.getProgress()) / tile.getTotalProgress());

			int progressPerStep = tile.getTotalProgress()/3;
			int progress = tile.getProgress() % progressPerStep;

			drawTexturedModalRect(x + 62, y + 1, 90, 45, 12, 13);
			
	    	if(step == 0)
	    		drawProgressBar(x + 93, y+ 23, 54, 42, 13, 15, progress/(float)progressPerStep, ForgeDirection.DOWN);
	    	else if(step == 1) {
	    		drawTexturedModalRect(x + 61, y+ 22, 78, 42, 12, 13);
	    		
	    		drawTexturedModalRect(x + 93, y+ 23, 54, 42, 13, 15);
	    		
	    		drawProgressBar(x + 94, y+ 42, 67, 42, 11, 15, progress/(float)progressPerStep, ForgeDirection.DOWN);
	    	}
	    	else if(step == 2) {
	    		drawTexturedModalRect(x + 59, y+ 51, 54, 57, 14, 15);
	    		drawTexturedModalRect(x + 61, y+ 22, 78, 42, 12, 13);
	    		
	    		drawTexturedModalRect(x + 93, y+ 23, 54, 42, 13, 15);
	    		drawTexturedModalRect(x + 94, y+ 42, 67, 42, 11, 15);
	    		
	    		drawProgressBar(x + 89, y+ 63, 90, 42, 22, 3, progress/(float)progressPerStep, ForgeDirection.EAST);
	    	}
			
			/*if(step == 0) {
				this.drawTexturedModalRect(x + 69, y + 15, 228, 3,11,11); //Step signal
				this.drawTexturedModalRect(x + 100, y + 37, 192, 0, 13, (int)(15*(progress/(float)progressPerStep))); // Progress signal
			}
			else if(step == 1) {
				this.drawTexturedModalRect(x + 69, y + 36, 216, 0,12,13); // Step signal
				this.drawTexturedModalRect(x + 100, y + 37, 192, 0, 13, 15); // Previous Step finished
				this.drawTexturedModalRect(x + 101, y + 56, 205, 0, 11, (int)(15*(progress/(float)progressPerStep))); //This step's progress
			}
			else if(step == 2) {
				this.drawTexturedModalRect(x + 66, y + 65, 192, 15, 14, 8);
				this.drawTexturedModalRect(x + 100, y + 37, 192, 0, 13, 15); // Previous Step finished
				this.drawTexturedModalRect(x + 101, y + 56, 205, 0, 11, 15); // Previous Step finished
				this.drawTexturedModalRect(x + 96, y + 77, 228, 0, (int)(22*(progress/(float)progressPerStep)), 3); //This step's progress
			}

			this.drawTexturedModalRect(x + 122, y + 31, 0, 171, 16, 16);*/
		}

	}
}

