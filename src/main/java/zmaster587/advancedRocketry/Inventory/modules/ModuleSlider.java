package zmaster587.advancedRocketry.Inventory.modules;

import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;

public class ModuleSlider extends ModuleProgress {

	public ModuleSlider(int offsetX, int offsetY, int id,
			ProgressBarImage progressBar, IProgressBar progress) {
		super(offsetX, offsetY, id, progressBar, progress);
	}

	@Override
	public void onMouseClickedAndDragged(int x, int y, int button,
			long timeSineLastClick) {
		onMouseClicked(x, y, button);
	}
	
	@Override
	public void onMouseClicked(int x, int y, int button) {
		super.onMouseClicked(x, y, button);

		if(button == 0) {
			int localX = x - offsetX - progressBar.getInsetX();
			int localY = y - offsetY - progressBar.getInsetY();

			//If user is over the slider
			if(localX > 0 && localX < progressBar.getBackWidth() - progressBar.getInsetX() && localY > 0 && localY < progressBar.getBackHeight() - progressBar.getInsetY()) {

				float percent;

				if(progressBar.getDirection().offsetX != 0) { // horizontal
					percent = localX / (float)(progressBar.getBackWidth() - progressBar.getInsetX());
				}
				else
					percent = 1 - (localY / (float)(progressBar.getBackHeight() - progressBar.getInsetY()));

				progress.setProgress(id, (int) (percent*progress.getTotalProgress(id)));
			}
		}
	}

}
