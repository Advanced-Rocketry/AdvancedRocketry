package zmaster587.advancedRocketry.integration.jei.lathe;

import mezz.jei.api.IJeiHelpers;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

public class LatheRecipeMaker {

	public static List<LatheWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<LatheWrapper> list = new LinkedList<>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new LatheWrapper(rec));
		}
		return list;
	}
	
}
