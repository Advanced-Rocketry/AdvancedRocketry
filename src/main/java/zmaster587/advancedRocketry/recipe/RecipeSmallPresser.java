package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeSmallPresser extends RecipeMachineFactory {

	public static RecipeSmallPresser INSTANCE = new RecipeSmallPresser();
	
	@Override
	public Class getMachine() {
		return BlockPress.class;
	}
}
