package zmaster587.advancedRocketry.integration.jei.centrifuge;

import mezz.jei.api.helpers.IJeiHelpers;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

public class CentrifugeRecipeMaker {

	public static List<CentrifugeWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<CentrifugeWrapper> list = new LinkedList<>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new CentrifugeWrapper(rec));
		}
		return list;
	}
	
}
