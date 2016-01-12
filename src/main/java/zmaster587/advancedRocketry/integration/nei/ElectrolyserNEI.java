package zmaster587.advancedRocketry.integration.nei;

import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.tile.multiblock.TileElectrolyser;

public class ElectrolyserNEI extends TemplateNEI {
	@Override
	public String getRecipeName() {
		return "Electrolyser";
	}

	@Override
	protected Class getMachine() {
		return TileElectrolyser.class;
	}

	@Override
	protected ProgressBarImage getProgressBar() {
		return TextureResources.crystallizerProgressBar;
	}
}
