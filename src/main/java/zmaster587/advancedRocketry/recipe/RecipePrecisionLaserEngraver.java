package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionLaserEngraver;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipePrecisionLaserEngraver extends RecipeMachineFactory {

	@Override
	public Class getMachine() {
		return TilePrecisionLaserEngraver.class;
	}
}
