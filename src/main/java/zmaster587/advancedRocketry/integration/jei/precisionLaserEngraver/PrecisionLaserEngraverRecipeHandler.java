package zmaster587.advancedRocketry.integration.jei.precisionLaserEngraver;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

public class PrecisionLaserEngraverRecipeHandler implements IRecipeHandler<PrecisionLaserEngraverWrapper> {

	@Override
	public Class<PrecisionLaserEngraverWrapper> getRecipeClass() {
		return PrecisionLaserEngraverWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(PrecisionLaserEngraverWrapper recipe) { return ARPlugin.precisionLaserEngraverUUID; }

	@Override
	public IRecipeWrapper getRecipeWrapper(PrecisionLaserEngraverWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(PrecisionLaserEngraverWrapper recipe) {
		return true;
	}

}
