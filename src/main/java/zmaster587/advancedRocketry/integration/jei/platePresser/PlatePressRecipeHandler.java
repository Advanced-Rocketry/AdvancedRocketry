package zmaster587.advancedRocketry.integration.jei.platePresser;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class PlatePressRecipeHandler implements IRecipeHandler<PlatePressWrapper> {

	@Override
	public Class<PlatePressWrapper> getRecipeClass() {
		return PlatePressWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return ARPlugin.platePresser;
	}

	@Override
	public String getRecipeCategoryUid(PlatePressWrapper recipe) {
		return ARPlugin.platePresser;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(PlatePressWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(PlatePressWrapper recipe) {
		return true;
	}

}
