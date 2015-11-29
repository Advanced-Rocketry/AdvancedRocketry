package zmaster587.advancedRocketry.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.interfaces.IRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipesMachine {
	public class Recipe implements IRecipe {

		private ArrayList<ItemStack> input;
		private ArrayList<FluidStack> fluidInput;
		private ArrayList<ItemStack> output;
		private ArrayList<FluidStack> fluidOutput;
		private int completionTime, power;

		public Recipe() {}

		public Recipe(List<ItemStack> output, List<ItemStack> input, int completionTime, int powerReq) {
			this.output = new ArrayList<ItemStack>();
			this.output.addAll(output);

			this.input = new ArrayList<ItemStack>();
			this.input.addAll(input);

			this.completionTime = completionTime;
			this.power = powerReq;

			this.fluidInput = new ArrayList<FluidStack>();
			this.fluidOutput = new ArrayList<FluidStack>();
		}

		public Recipe(List<ItemStack> output, List<ItemStack> input, List<FluidStack> fluidOutput, List<FluidStack> fluidInput, int completionTime, int powerReq) {
			this(output, input, completionTime, powerReq);

			this.fluidInput.addAll(fluidInput);
			this.fluidOutput.addAll(fluidOutput);
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
		public ArrayList<FluidStack> getFluidIngredients() {
			return fluidInput;
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

		@Override
		public List<FluidStack> getFluidOutputs() {
			ArrayList<FluidStack> stack = new ArrayList<FluidStack>();

			for(FluidStack i : fluidOutput) {
				stack.add(i.copy());
			}

			return stack;
		}

		public IRecipe getRecipeAsAllItemsOnly() {
			Recipe recipe = new Recipe(output, input, completionTime, power);
			
			for(FluidStack stack : getFluidIngredients()) {
				recipe.input.add(new ItemStack(stack.getFluid().getBlock()));
			}
			
			for(FluidStack stack : getFluidOutputs()) {
				recipe.output.add(new ItemStack(stack.getFluid().getBlock()));
			}
			
			return recipe;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Recipe) {
				Recipe otherRecipe = (Recipe)obj;
				if(input.size() != otherRecipe.input.size() || fluidInput.size() != otherRecipe.fluidInput.size())
					return false;
				
				
				for(int i = 0; i < input.size(); i++) {
					if(!ItemStack.areItemStacksEqual(input.get(i), otherRecipe.input.get(i)))
							return false;
				}
				
				for(int i = 0; i < fluidInput.size(); i++) {
					if(!FluidStack.areFluidStackTagsEqual(fluidInput.get(i), otherRecipe.fluidInput.get(i)))
							return false;
				}
				
			}
			return super.equals(obj);
		}
	}

	public HashMap<Class<Object>, List<IRecipe>> recipeList;

	private static RecipesMachine instance = new RecipesMachine();

	public RecipesMachine() {
		recipeList = new HashMap<Class<Object>, List<IRecipe>>();
	}

	public static RecipesMachine getInstance() { return instance; }

	public void addRecipe(Class clazz ,List<Object> out, int timeRequired, int power, Object ... inputs) {
		List<IRecipe> recipes = getRecipes(clazz);
		if(recipes == null) {
			recipes = new LinkedList<IRecipe>();
			recipeList.put(clazz,recipes);
		}


		ArrayList<ItemStack> stack = new ArrayList<ItemStack>();
		ArrayList<FluidStack> inputFluidStacks = new ArrayList<FluidStack>();

		try {

			for(int i = 0; i < inputs.length; i++) {
				if(inputs[i] != null) {
					if(inputs[i] instanceof String) {
						Object[] obj2 = inputs.clone();

						for (ItemStack itemStack : OreDictionary.getOres((String)inputs[i])) {
							obj2[i] = itemStack;
							addRecipe(clazz, out, timeRequired, power, obj2);
						}
						return;	
					}
					else if(inputs[i] instanceof FluidStack)
						inputFluidStacks.add((FluidStack) inputs[i]);
					else {

						if(inputs[i] instanceof Item) 
							inputs[i] = new ItemStack((Item)inputs[i]);
						else if(inputs[i] instanceof Block)
							inputs[i] = new ItemStack((Block)inputs[i]);

						stack.add((ItemStack)inputs[i]);
					}
				}
			}
			ArrayList<ItemStack> outputItem = new ArrayList<ItemStack>();
			ArrayList<FluidStack> outputFluidStacks = new ArrayList<FluidStack>();

			for(Object outputObject : out) {
				if(outputObject instanceof ItemStack)
					outputItem.add((ItemStack)outputObject);
				else
					outputFluidStacks.add((FluidStack)outputObject);
			}

			Recipe recipe;
			if(inputFluidStacks.isEmpty() && outputFluidStacks.isEmpty())
				recipe = new Recipe(outputItem, stack, timeRequired, power);
			else
				recipe = new Recipe(outputItem, stack, outputFluidStacks, inputFluidStacks, timeRequired, power);
			
		if(!recipes.contains(recipe))
			recipes.add(recipe);

		} catch(ClassCastException e) {
			//Custom handling to make sure it logs and can be suppressed by user
			String message = e.getLocalizedMessage();

			for(StackTraceElement element : e.getStackTrace()) {
				message += "\n\t" + element.toString();
			}

			AdvancedRocketry.logger.warning("Cannot add recipe!");
			AdvancedRocketry.logger.warning(message);

		}
	}

	public void addRecipe(Class clazz , Object out, int timeRequired, int power, Object ... inputs) {
		List<Object> newList;
		
		if(out instanceof List) {
			newList = (List)out;
		}
		else {
			newList = new LinkedList<Object>();
			newList.add(out);
		}
		
		addRecipe(clazz, newList, timeRequired, power, inputs);
	}

	//Given the class return the list
	public List<IRecipe> getRecipes(Class clazz) {
		return recipeList.get(clazz);
	}
}