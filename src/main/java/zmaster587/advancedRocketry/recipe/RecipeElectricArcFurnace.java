package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeElectricArcFurnace extends RecipeMachineFactory {

	public static final RecipeElectricArcFurnace INSTANCE = new RecipeElectricArcFurnace();

	@Override
	public Class getMachine() {
		return TileElectricArcFurnace.class;
	}
}
