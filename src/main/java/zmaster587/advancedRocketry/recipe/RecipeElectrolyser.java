package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeElectrolyser extends RecipeMachineFactory {

	@Override
	public Class getMachine() {
		return TileElectrolyser.class;
	}
}
