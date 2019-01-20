package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeElectricArcFurnace extends RecipeMachineFactory {

	@Override
	public Class getMachine() {
		return TileElectricArcFurnace.class;
	}
}
