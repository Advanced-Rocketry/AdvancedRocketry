package zmaster587.advancedRocketry.integration.jei.cuttingmachine;

import mezz.jei.api.helpers.IJeiHelpers;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

public class CuttingMachineRecipeMaker {

	public static List<CuttingMachineWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<CuttingMachineWrapper> list = new LinkedList<>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new CuttingMachineWrapper(rec));
		}
		return list;
	}
	
}
