package zmaster587.advancedRocketry.integration.jei.electrolyser;

import mezz.jei.api.IJeiHelpers;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;

import java.util.LinkedList;
import java.util.List;

public class ElectrolyzerRecipeMaker {

	public static List<ElectrolyzerWrapper> getMachineRecipes(IJeiHelpers helpers, Class clazz) {
		
		List<ElectrolyzerWrapper> list = new LinkedList<ElectrolyzerWrapper>();
		for(IRecipe rec : RecipesMachine.getInstance().getRecipes(clazz)) {
			list.add(new ElectrolyzerWrapper(rec));
		}
		
		List<ItemStack> output = new LinkedList<ItemStack>();
		output.add(new ItemStack(AdvancedRocketryItems.itemBucketHydrogen));
		output.add(new ItemStack(AdvancedRocketryItems.itemBucketOxygen));
		
		List<ItemStack> input = new LinkedList<ItemStack>();
		List<List<ItemStack>> finalInput  = new LinkedList<List<ItemStack>>();
		input.add(new ItemStack(Items.WATER_BUCKET));
		finalInput.add(input);
		
		IRecipe waterElectro = new RecipesMachine.Recipe(output, finalInput, 0, 0, null);
		
		list.add(new ElectrolyzerWrapper(waterElectro));
		
		return list;
	}
	
}
