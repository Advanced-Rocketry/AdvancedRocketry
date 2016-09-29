package zmaster587.advancedRocketry.integration.jei.crystallizer;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CrystallizerRecipeHandler implements IRecipeHandler<CrystallizerWrapper> {

	@Override
	public Class<CrystallizerWrapper> getRecipeClass() {
		return CrystallizerWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return ARPlugin.crystallizerUUID;
	}

	@Override
	public String getRecipeCategoryUid(CrystallizerWrapper recipe) {
		return ARPlugin.crystallizerUUID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CrystallizerWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(CrystallizerWrapper recipe) {
		return true;
	}

}
