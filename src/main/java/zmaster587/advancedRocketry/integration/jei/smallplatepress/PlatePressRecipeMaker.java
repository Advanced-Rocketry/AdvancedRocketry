package zmaster587.advancedRocketry.integration.jei.smallplatepress;

import mezz.jei.api.helpers.IJeiHelpers;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

public class PlatePressRecipeMaker {

	public static List<PlatePressWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<PlatePressWrapper> list = new LinkedList<>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new PlatePressWrapper(rec));
		}
		return list;
	}
	
}
