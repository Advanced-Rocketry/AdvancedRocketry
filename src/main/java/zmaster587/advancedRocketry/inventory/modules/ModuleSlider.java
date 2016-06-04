package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.inventory.GuiModular;

public class ModuleSlider extends ModuleProgress {

	public ModuleSlider(int offsetX, int offsetY, int id,
			ProgressBarImage progressBar, ISliderBar progress) {
		super(offsetX, offsetY, id, progressBar, progress);
	}

	@Override
	public void onMouseClickedAndDragged(int x, int y, int button,
			long timeSineLastClick) {
		onMouseClicked(null,x, y, button);
	}
	
	@Override
	public void onMouseClicked(GuiModular gui, int x, int y, int button) {

		if(button == 0 && isEnabled()) {
			int localX = x - offsetX - progressBar.getInsetX();
			int localY = y - offsetY - progressBar.getInsetY();

			//If user is over the slider
			if(localX > 0 && localX < progressBar.getBackWidth() - progressBar.getInsetX() && localY > 0 && localY < progressBar.getBackHeight() - progressBar.getInsetY()) {

				float percent;
				if(progressBar.getDirection().offsetX != 0) { // horizontal
					percent = MathHelper.clamp_float((localX  + progressBar.getInsetX())/ ((float)(progressBar.getBackWidth() - 2*progressBar.getInsetX())),0f,1f);
				}
				else if(progressBar.getDirection().offsetY == 1)
					percent = 1 - (localY / (float)(progressBar.getBackHeight() - progressBar.getInsetY()));
				else
					percent = localY / (float)(progressBar.getBackHeight() + progressBar.getInsetY());
				
				
				((ISliderBar)progress).setProgressByUser(id, (int) (percent*progress.getTotalProgress(id)));
			}
		}
	}
}