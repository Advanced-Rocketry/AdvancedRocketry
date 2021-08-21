package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileCentrifuge;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeCentrifuge extends RecipeMachineFactory {

	
	public static final RecipeCentrifuge INSTANCE = new RecipeCentrifuge();
	
	@Override
	public Class getMachine() {
		return TileCentrifuge.class;
	}
}
