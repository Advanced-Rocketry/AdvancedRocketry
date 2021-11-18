package zmaster587.advancedRocketry.util;

import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.*;

public class RecipeHandler {

	private List<Class<?>> machineList = new ArrayList<>();
	
	public void registerMachine(Class<?> clazz) {
		if(!machineList.contains(clazz))
		{
			machineList.add(clazz);
			RecipesMachine.getInstance().recipeList.put(clazz, new LinkedList<>());
		}
		
	}

	public void clearAllMachineRecipes() {
		for(Class<?>  clazz : machineList) {
			RecipesMachine.getInstance().getRecipes(clazz).clear();
		}
	}
	
	public void registerAllMachineRecipes() {
		
		for(Class<?>  clazz : machineList)
			try {
				if(TileMultiblockMachine.class.isAssignableFrom(clazz))
				((TileMultiblockMachine)clazz.newInstance()).registerRecipes();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
	}
}
