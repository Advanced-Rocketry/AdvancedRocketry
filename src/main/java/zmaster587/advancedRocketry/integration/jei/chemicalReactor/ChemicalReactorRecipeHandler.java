package zmaster587.advancedRocketry.integration.jei.chemicalReactor;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class ChemicalReactorRecipeHandler implements IRecipeHandler<ChemicalReactorlWrapper> {

	@Override
	public Class<ChemicalReactorlWrapper> getRecipeClass() {
		return ChemicalReactorlWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return ARPlugin.chemicalReactorUUID;
	}

	@Override
	public String getRecipeCategoryUid(ChemicalReactorlWrapper recipe) {
		return ARPlugin.chemicalReactorUUID;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(ChemicalReactorlWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(ChemicalReactorlWrapper recipe) {
		return true;
	}

}
