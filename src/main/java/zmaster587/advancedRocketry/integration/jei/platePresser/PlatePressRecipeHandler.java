package zmaster587.advancedRocketry.integration.jei.platePresser;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

public class PlatePressRecipeHandler implements IRecipeHandler<PlatePressWrapper> {

    @Override
    public Class<PlatePressWrapper> getRecipeClass() {
        return PlatePressWrapper.class;
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
