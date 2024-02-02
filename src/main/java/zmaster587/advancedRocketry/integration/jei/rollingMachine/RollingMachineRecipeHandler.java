package zmaster587.advancedRocketry.integration.jei.rollingMachine;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

public class RollingMachineRecipeHandler implements IRecipeHandler<RollingMachineWrapper> {

    @Override
    public Class<RollingMachineWrapper> getRecipeClass() {
        return RollingMachineWrapper.class;
    }


    @Override
    public String getRecipeCategoryUid(RollingMachineWrapper recipe) {
        return ARPlugin.rollingMachineUUID;
    }

    @Override
    public IRecipeWrapper getRecipeWrapper(RollingMachineWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(RollingMachineWrapper recipe) {
        return true;
    }

}
