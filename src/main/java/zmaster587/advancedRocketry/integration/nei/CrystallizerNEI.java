package zmaster587.advancedRocketry.integration.nei;

import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.libVulpes.client.util.ProgressBarImage;

public class CrystallizerNEI extends TemplateNEI {

	@Override
	public String getRecipeName() {
		return "Crystallizer";
	}

	@Override
	protected Class getMachine() {
		return TileCrystallizer.class;
	}

	@Override
	protected ProgressBarImage getProgressBar() {
		return TextureResources.crystallizerProgressBar;
	}
	
}