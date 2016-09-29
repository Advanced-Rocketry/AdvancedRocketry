package zmaster587.advancedRocketry.integration.jei.chemicalReactor;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import mezz.jei.api.IJeiHelpers;

public class ChemicalReactorRecipeMaker {

	public static List<ChemicalReactorlWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<ChemicalReactorlWrapper> list = new LinkedList<ChemicalReactorlWrapper>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new ChemicalReactorlWrapper(rec));
		}
		
		List<ItemStack> input = new LinkedList<ItemStack>();
		input.add(new ItemStack(AdvancedRocketryItems.itemBucketHydrogen));
		
		
		
		List<List<ItemStack>> finalInput  = new LinkedList<List<ItemStack>>();
		finalInput.add(input);
		input = new LinkedList<ItemStack>();
		input.add(new ItemStack(AdvancedRocketryItems.itemBucketOxygen));
		finalInput.add(input);
		
		List<ItemStack> output = new LinkedList<ItemStack>();
		output.add(new ItemStack(AdvancedRocketryItems.itemBucketRocketFuel));
		
		
		IRecipe rocketFuel = new RecipesMachine.Recipe(output, finalInput, 0, 0);
		
		list.add(new ChemicalReactorlWrapper(rocketFuel));
		
		return list;
	}
	
}
