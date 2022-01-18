package zmaster587.advancedRocketry.integration.jei.electrolyzer;

import mezz.jei.api.helpers.IJeiHelpers;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

public class ElectrolyzerRecipeMaker {

	public static List<ElectrolyzerWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<ElectrolyzerWrapper> list = new LinkedList<>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new ElectrolyzerWrapper(rec));
		}
		
		return list;
	}
	
}
