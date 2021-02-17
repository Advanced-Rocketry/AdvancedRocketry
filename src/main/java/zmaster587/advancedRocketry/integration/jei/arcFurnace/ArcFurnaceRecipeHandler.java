package zmaster587.advancedRocketry.integration.jei.arcFurnace;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

public class ArcFurnaceRecipeHandler implements IRecipeHandler<ArcFurnaceWrapper> {

	@Override
	public Class<ArcFurnaceWrapper> getRecipeClass() {
		return ArcFurnaceWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(ArcFurnaceWrapper recipe) {
		return ARPlugin.arcFurnaceUUID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ArcFurnaceWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(ArcFurnaceWrapper recipe) {
		return true;
	}

}
