package zmaster587.advancedRocketry.integration.jei.rollingmachine;

import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

import mezz.jei.api.helpers.IJeiHelpers;

public class RollingMachineRecipeMaker {

	public static List<RollingMachineWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<RollingMachineWrapper> list = new LinkedList<>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new RollingMachineWrapper(rec));
		}
		return list;
	}
	
}
