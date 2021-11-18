package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeLathe extends RecipeMachineFactory {

	public static RecipeLathe INSTANCE = new RecipeLathe();

    @Override
	public Class getMachine() {
		return TileLathe.class;
	}
}
