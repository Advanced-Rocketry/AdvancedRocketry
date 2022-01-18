package zmaster587.advancedRocketry.integration.jei.precisionassembler;

import mezz.jei.api.helpers.IJeiHelpers;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

public class PrecisionAssemblerRecipeMaker {

	public static List<PrecisionAssemblerWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<PrecisionAssemblerWrapper> list = new LinkedList<>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new PrecisionAssemblerWrapper(rec));
		}
		return list;
	}
	
}
