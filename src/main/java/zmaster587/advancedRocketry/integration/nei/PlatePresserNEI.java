/*package zmaster587.advancedRocketry.integration.nei;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.client.util.ProgressBarImage;

public class PlatePresserNEI  extends TemplateNEI {
	@Override
	public String getRecipeName() {
		return "Small Plate Presser";
	}
	
    
	@Override
	public int recipiesPerPage() {
		return 2;
	}


	@Override
	protected Class getMachine() {
		return BlockPress.class;
	}

	@Override
	public void drawForeground(int recipe) {
		GL11.glColor3f(1f, 1f, 1f);
		drawExtras(recipe);
	}

	@Override
	protected ProgressBarImage getProgressBar() {
		return TextureResources.smallPlatePresser;
	}
}
*/