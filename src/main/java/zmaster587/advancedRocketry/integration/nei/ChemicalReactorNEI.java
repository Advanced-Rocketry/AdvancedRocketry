package zmaster587.advancedRocketry.integration.nei;

import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.tile.multiblock.TileChemicalReactor;

public class ChemicalReactorNEI  extends TemplateNEI {
	@Override
	public String getRecipeName() {
		return "Chemical Reactor";
	}

	@Override
	protected Class getMachine() {
		return TileChemicalReactor.class;
	}

	@Override
	protected ProgressBarImage getProgressBar() {
		return TextureResources.crystallizerProgressBar;
	}
}
