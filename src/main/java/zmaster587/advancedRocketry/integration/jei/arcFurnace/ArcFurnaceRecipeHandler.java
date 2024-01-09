package zmaster587.advancedRocketry.integration.jei.arcFurnace;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArcFurnaceRecipeHandler implements IRecipeHandler<ArcFurnaceWrapper> {

	@Override
	@NotNull
	public Class<ArcFurnaceWrapper> getRecipeClass() {
		return ArcFurnaceWrapper.class;
	}

	@Override
	@NotNull
	public String getRecipeCategoryUid(@Nullable ArcFurnaceWrapper recipe) {
		return ARPlugin.arcFurnaceUUID;
	}

	@Override
	@NotNull
	public IRecipeWrapper getRecipeWrapper(@NotNull ArcFurnaceWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(@Nullable ArcFurnaceWrapper recipe) {
		return true;
	}

}
