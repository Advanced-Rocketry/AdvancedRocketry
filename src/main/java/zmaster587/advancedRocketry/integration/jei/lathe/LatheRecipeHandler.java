package zmaster587.advancedRocketry.integration.jei.lathe;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class LatheRecipeHandler implements IRecipeHandler<LatheWrapper> {

	@Override
	public Class<LatheWrapper> getRecipeClass() {
		return LatheWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(LatheWrapper recipe) {
		return ARPlugin.latheUUID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(LatheWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(LatheWrapper recipe) {
		return true;
	}

}
