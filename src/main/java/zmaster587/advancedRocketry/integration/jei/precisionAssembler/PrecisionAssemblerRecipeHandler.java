package zmaster587.advancedRocketry.integration.jei.precisionAssembler;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class PrecisionAssemblerRecipeHandler implements IRecipeHandler<PrecisionAssemblerWrapper> {

	@Override
	public Class<PrecisionAssemblerWrapper> getRecipeClass() {
		return PrecisionAssemblerWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return ARPlugin.precisionAssemblerUUID;
	}

	@Override
	public String getRecipeCategoryUid(PrecisionAssemblerWrapper recipe) {
		return ARPlugin.precisionAssemblerUUID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(PrecisionAssemblerWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(PrecisionAssemblerWrapper recipe) {
		return true;
	}

}
