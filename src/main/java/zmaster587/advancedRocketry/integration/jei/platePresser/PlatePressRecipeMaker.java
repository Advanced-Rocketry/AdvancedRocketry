package zmaster587.advancedRocketry.integration.jei.platePresser;

import java.util.LinkedList;
import java.util.List;

import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import mezz.jei.api.IJeiHelpers;

public class PlatePressRecipeMaker {

	public static List<PlatePressWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<PlatePressWrapper> list = new LinkedList<PlatePressWrapper>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new PlatePressWrapper(rec));
		}
		return list;
	}
	
}
