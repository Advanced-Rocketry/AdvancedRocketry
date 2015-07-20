package zmaster587.advancedRocketry.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zmaster587.libVulpes.interfaces.IRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipesMachine {
	public class Recipe implements IRecipe {

		private ArrayList<ItemStack> input;
		private ArrayList<ItemStack> output;
		private int completionTime, power;

		public Recipe() {}

		public Recipe(List<ItemStack> output, List<ItemStack> input, int completionTime, int powerReq) {
			this.output = new ArrayList<ItemStack>();
			this.output.addAll(output);

			this.input = new ArrayList<ItemStack>();
			this.input.addAll(input);

			this.completionTime = completionTime;
			this.power = powerReq;
		}

		public int getCompletionTime() {return completionTime; }


		public int getPowerReq() {
			return power;
		}

		@Override
		public ArrayList<ItemStack> getIngredients() {
			return input;
		}

		@Override
		public int getTime() {
			return completionTime;
		}

		@Override
		public int getPower() {
			return power;
		}

		@Override
		public List<ItemStack> getOutput() {
			ArrayList<ItemStack> stack = new ArrayList<ItemStack>();

			for(ItemStack i : output) {
				stack.add(i.copy());
			}

			return stack;
		}
	}

	public HashMap<Class<Object>, ArrayList<IRecipe>> recipeList;

	private static RecipesMachine instance = new RecipesMachine();

	public RecipesMachine() {
		recipeList = new HashMap<Class<Object>, ArrayList<IRecipe>>();
	}

	public static RecipesMachine getInstance() { return instance; }

	public void addRecipe(Class clazz ,ItemStack out, int timeRequired, int power, Object ... obj) {

		ArrayList<IRecipe> recipes = getRecipes(clazz);
		if(recipes == null) {
			recipes = new ArrayList<IRecipe>();
			recipeList.put(clazz,recipes);
		}
			
		
		ArrayList<ItemStack> stack = new ArrayList<ItemStack>();

		for(int i = 0; i < obj.length; i++) {
			if(obj[i] != null) {
				if(obj[i] instanceof String) {

					Object[] obj2 = obj.clone();

					for (ItemStack itemStack : OreDictionary.getOres((String)obj[i])) {
						obj2[i] = itemStack;
						addRecipe(clazz, out, timeRequired, power, obj2);
					}
					return;	
				}
				else {

					if(obj[i] instanceof Item) 
						obj[i] = new ItemStack((Item)obj[i]);
					else if(obj[i] instanceof Block)
						obj[i] = new ItemStack((Block)obj[i]);

					stack.add((ItemStack)obj[i]);
				}
			}
		}
		ArrayList<ItemStack> outputItem = new ArrayList<ItemStack>();
		outputItem.add(out);

		Recipe recipe = new Recipe(outputItem, stack, timeRequired, power);
		recipes.add(recipe);
	}

	//Given the class return the list
	public ArrayList<IRecipe> getRecipes(Class clazz) {
		return recipeList.get(clazz);
	}
}