package zmaster587.advancedRocketry.integration.nei;

/*import static codechicken.lib.gui.GuiDraw.blit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.GuiModular;

public class PrecisionAssemberNEI extends TemplateNEI {

	private static final int ticksPerBar = 150;
	
	
	@Override
	public String getRecipeName() {
		return "Precision Assembler";
	}
	
	
	@Override
	public int recipiesPerPage() {
		return 1;
	}
	
	@Override
    public void drawExtras(int recipe)
    {
		Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.progressBars);
		
		blit(58, 1, 132, 0, 53, 66);
		
    	byte mode = (byte) ((cycleticks % ticksPerBar)/(ticksPerBar/3));
    	blit(62, 1, 90, 45, 12, 13);
    	
    	//(cycleticks % 100) /100f;
    	if(mode == 0)
    		drawProgressBar(93, 23, 54, 42, 13, 15, (cycleticks % (ticksPerBar/3)) /(float)(ticksPerBar/3), 1);
    	else if(mode == 1) {
    		blit(61, 22, 78, 42, 12, 13);
    		
    		blit(93, 23, 54, 42, 13, 15);
    		
    		drawProgressBar(94, 42, 67, 42, 11, 15, (cycleticks % (ticksPerBar/3)) /(float)(ticksPerBar/3), 1);
    	}
    	else if(mode == 2) {
    		blit(59, 51, 54, 57, 14, 9);
    		blit(61, 22, 78, 42, 12, 13);
    		
    		blit(93, 23, 54, 42, 13, 15);
    		blit(94, 42, 67, 42, 11, 15);
    		
    		drawProgressBar(89, 63, 90, 42, 22, 3, (cycleticks % (ticksPerBar/3)) /(float)(ticksPerBar/3), 0);
    	}
    }

	@Override
	protected Class getMachine() {
		return TilePrecisionAssembler.class;
	}

	@Override
	protected ProgressBarImage getProgressBar() {
		return TextureResources.progressScience;
	}
}
*/