package zmaster587.advancedRocketry.integration.jei;

import zmaster587.libVulpes.client.util.ProgressBarImage;
import net.minecraft.client.Minecraft;
import mezz.jei.api.gui.IDrawableAnimated;

public class DrawableProgressBarWrapper implements IDrawableAnimated {

	
	ProgressBarImage bar;
	
	public DrawableProgressBarWrapper( ProgressBarImage bar) {
		this.bar = bar;
	}
	
	@Override
	public int getWidth() {
		return bar.getBackWidth();
	}

	@Override
	public int getHeight() {
		return bar.getBackHeight();
	}

	@Override
	public void draw(Minecraft minecraft) {
		bar.renderProgressBar(0,0,0, 100);
	}

	@Override
	public void draw(Minecraft minecraft, int xOffset, int yOffset) {
		bar.renderProgressBar(xOffset,0,yOffset, 100);
	}

}
