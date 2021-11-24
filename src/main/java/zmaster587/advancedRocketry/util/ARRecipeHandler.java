package zmaster587.advancedRocketry.util;

import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.*;

public class ARRecipeHandler {

	private List<Class<?>> machineList = new ArrayList<>();
	
	public void registerMachine(Class<?> clazz) {
		if(!machineList.contains(clazz)) {
			machineList.add(clazz);
			RecipesMachine.getInstance().recipeList.put(clazz, new LinkedList<>());
		}
		
	}
}
