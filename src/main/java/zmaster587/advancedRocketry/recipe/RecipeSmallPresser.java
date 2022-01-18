package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.block.multiblock.BlockSmallPlatePress;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeSmallPresser extends RecipeMachineFactory {

	public static RecipeSmallPresser INSTANCE = new RecipeSmallPresser();
	
	@Override
	public Class getMachine() {
		return BlockSmallPlatePress.class;
	}
}
