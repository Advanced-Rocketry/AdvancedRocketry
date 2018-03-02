package zmaster587.advancedRocketry.integration.jei.lathe;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

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
