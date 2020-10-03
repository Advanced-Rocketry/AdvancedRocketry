package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeCuttingMachine extends RecipeMachineFactory {

	public static RecipeCuttingMachine INSTANCE = new RecipeCuttingMachine();

	@Override
	public Class getMachine() {
		return TileCuttingMachine.class;
	}
}
