package zmaster587.advancedRocketry.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;

import mezz.jei.api.gui.drawable.IDrawableAnimated;
import net.minecraft.client.Minecraft;
import zmaster587.libVulpes.client.util.ProgressBarImage;

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
	public void draw(MatrixStack matrixStack, int xOffset, int yOffset) {
		bar.renderProgressBar(xOffset,0,yOffset, 100);
		
	}

}
