package zmaster587.advancedRocketry.integration.jei.chemicalReactor;

import mezz.jei.api.helpers.IJeiHelpers;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

public class ChemicalReactorRecipeMaker {

	public static List<ChemicalReactorlWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<ChemicalReactorlWrapper> list = new LinkedList<ChemicalReactorlWrapper>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new ChemicalReactorlWrapper(rec));
		}
		
		return list;
	}
	
}
