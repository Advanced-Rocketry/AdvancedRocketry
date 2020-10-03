package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipePrecisionAssembler extends RecipeMachineFactory {

	public static RecipePrecisionAssembler INSTANCE = new RecipePrecisionAssembler();

	@Override
	public Class getMachine() {
		return TilePrecisionAssembler.class;
	}
}
