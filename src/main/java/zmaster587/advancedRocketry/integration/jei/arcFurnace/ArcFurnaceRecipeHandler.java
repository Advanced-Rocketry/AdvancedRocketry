package zmaster587.advancedRocketry.integration.jei.arcFurnace;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArcFurnaceRecipeHandler implements IRecipeHandler<ArcFurnaceWrapper> {

    @Override
    @Nonnull
    public Class<ArcFurnaceWrapper> getRecipeClass() {
        return ArcFurnaceWrapper.class;
    }

    @Override
    @Nonnull
    public String getRecipeCategoryUid(@Nullable ArcFurnaceWrapper recipe) {
        return ARPlugin.arcFurnaceUUID;
    }

    @Override
    @Nonnull
    public IRecipeWrapper getRecipeWrapper(@Nonnull ArcFurnaceWrapper recipe) {
        return recipe;
    }

    @Override
    public boolean isRecipeValid(@Nullable ArcFurnaceWrapper recipe) {
        return true;
    }

}
