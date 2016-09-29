package zmaster587.advancedRocketry.integration.jei.sawmill;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class SawMillRecipeHandler implements IRecipeHandler<SawMillWrapper> {

	@Override
	public Class<SawMillWrapper> getRecipeClass() {
		return SawMillWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return ARPlugin.sawMillUUID;
	}

	@Override
	public String getRecipeCategoryUid(SawMillWrapper recipe) {
		return ARPlugin.sawMillUUID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(SawMillWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(SawMillWrapper recipe) {
		return true;
	}

}
