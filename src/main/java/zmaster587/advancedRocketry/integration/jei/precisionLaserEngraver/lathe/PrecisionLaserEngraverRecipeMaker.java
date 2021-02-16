package zmaster587.advancedRocketry.integration.jei.precisionLaserEngraver.lathe;

import mezz.jei.api.IJeiHelpers;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

public class PrecisionLaserEngraverRecipeMaker {

	public static List<PrecisionLaserEngraverWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {

		List<PrecisionLaserEngraverWrapper> list = new LinkedList<PrecisionLaserEngraverWrapper>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new PrecisionLaserEngraverWrapper(rec));
		}
		return list;
	}
	
}
