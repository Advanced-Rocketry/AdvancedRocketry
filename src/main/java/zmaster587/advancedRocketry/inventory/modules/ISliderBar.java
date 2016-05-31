package zmaster587.advancedRocketry.inventory.modules;

public interface ISliderBar extends IProgressBar {
	
	/**
	 * Called on the client when a Slider is changed
	 * @param id id of the progress bar to update
	 * @param progress progress data received from the server
	 */
	public void setProgressByUser(int id, int progress);
}
