package zmaster587.advancedRocketry.recipe;

import net.minecraft.item.crafting.IRecipeSerializer;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeRollingMachine extends RecipeMachineFactory {

	public static RecipeRollingMachine INSTANCE = new RecipeRollingMachine();

	@Override
	public Class getMachine() {
		return TileRollingMachine.class;
	}
}
