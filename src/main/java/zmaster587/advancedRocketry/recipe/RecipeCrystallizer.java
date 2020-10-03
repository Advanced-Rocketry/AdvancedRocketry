package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeCrystallizer extends RecipeMachineFactory {

	public static final RecipeCrystallizer INSTANCE = new RecipeCrystallizer();
	
	@Override
	public Class getMachine() {
		return TileCrystallizer.class;
	}
}
