package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeChemicalReactor extends RecipeMachineFactory {

	
	public static final RecipeChemicalReactor INSTANCE = new RecipeChemicalReactor();
	
	@Override
	public Class getMachine() {
		return TileChemicalReactor.class;
	}
}
