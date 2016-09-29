package zmaster587.advancedRocketry.integration.jei.precisionAssembler;

import java.util.LinkedList;
import java.util.List;

import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import mezz.jei.api.IJeiHelpers;

public class PrecisionAssemblerRecipeMaker {

	public static List<PrecisionAssemblerWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<PrecisionAssemblerWrapper> list = new LinkedList<PrecisionAssemblerWrapper>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new PrecisionAssemblerWrapper(rec));
		}
		return list;
	}
	
}
