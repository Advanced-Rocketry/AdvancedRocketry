package zmaster587.advancedRocketry.integration.jei.blastFurnace;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

public class BlastFurnaceRecipeHandler implements IRecipeHandler<BlastFurnaceWrapper> {

	@Override
	public Class<BlastFurnaceWrapper> getRecipeClass() {
		return BlastFurnaceWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid(BlastFurnaceWrapper recipe) {
		return ARPlugin.arcFurnaceUUID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(BlastFurnaceWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(BlastFurnaceWrapper recipe) {
		return true;
	}

}
