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
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipesMachine {
	public class Recipe implements IRecipe {

		private LinkedList<LinkedList<ItemStack>> input;
		private LinkedList<FluidStack> fluidInput;
		private LinkedList<ItemStack> output;
		private LinkedList<FluidStack> fluidOutput;
		private int completionTime, power;

		public Recipe() {}

		public Recipe(List<ItemStack> output, LinkedList<LinkedList<ItemStack>> input, int completionTime, int powerReq) {
			this.output = new LinkedList<ItemStack>();
			this.output.addAll(output);

			this.input = new LinkedList<LinkedList<ItemStack>>();
		
			this.input.addAll(input);

			this.completionTime = completionTime;
			this.power = powerReq;

			this.fluidInput = new LinkedList<FluidStack>();
			this.fluidOutput = new LinkedList<FluidStack>();
		}

		public Recipe(List<ItemStack> output, LinkedList<LinkedList<ItemStack>> input, List<FluidStack> fluidOutput, List<FluidStack> fluidInput, int completionTime, int powerReq) {
			this(output, input, completionTime, powerReq);

			this.fluidInput.addAll(fluidInput);
			this.fluidOutput.addAll(fluidOutput);
		}

		public int getCompletionTime() {return completionTime; }


		public int getPowerReq() {
			return power;
		}

		@Override
		public LinkedList<LinkedList<ItemStack>> getIngredients() {
			return input;
		}

		@Override
		public List<FluidStack> getFluidIngredients() {
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
				FluidRegistry.getFluidID(stack.getFluid());

				Block block = stack.getFluid().getBlock();

				if(block == null) {
					for(FluidContainerRegistry.FluidContainerData container : FluidContainerRegistry.getRegisteredFluidContainerData()) {
						if(container.fluid.containsFluid(stack)) {
							LinkedList<ItemStack> list = new LinkedList<ItemStack>();
							list.add(container.filledContainer.copy());
							recipe.input.add(list);
							break;
						}
					}
				}
				else {
					LinkedList<ItemStack> list = new LinkedList<ItemStack>();
					list.add(new ItemStack(block));
					recipe.input.add(list);
				}
			}

			for(FluidStack stack : getFluidOutputs()) {
				FluidRegistry.getFluidID(stack.getFluid());

				Block block = stack.getFluid().getBlock();

				if(block == null) {
					for(FluidContainerRegistry.FluidContainerData container : FluidContainerRegistry.getRegisteredFluidContainerData()) {
						if(container.fluid.containsFluid(stack)) {
							recipe.output.add(container.filledContainer.copy());
							break;
						}
					}
				}
				else
					recipe.output.add(new ItemStack(block));
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
					if(input.get(i).size() != otherRecipe.input.get(i).size())
						return false;
					for(int j = 0; j < input.get(i).size(); j++) {
						if(!ItemStack.areItemStacksEqual(input.get(i).get(j), otherRecipe.input.get(i).get(j)))
							return false;
					}
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

	public void addRecipe(Class clazz , Object[] out, int timeRequired, int power, Object ... inputs) {
		List<IRecipe> recipes = getRecipes(clazz);
		if(recipes == null) {
			recipes = new LinkedList<IRecipe>();
			recipeList.put(clazz,recipes);
		}


		LinkedList<LinkedList<ItemStack>> stack = new LinkedList<LinkedList<ItemStack>>();

		ArrayList<FluidStack> inputFluidStacks = new ArrayList<FluidStack>();

		try {

			for(int i = 0; i < inputs.length; i++) {
				LinkedList<ItemStack> innerList = new LinkedList<ItemStack>();
				if(inputs[i] != null) {
					if(inputs[i] instanceof String) {
						for (ItemStack itemStack : OreDictionary.getOres((String)inputs[i])) {
							innerList.add(itemStack.copy());
						}
					}
					else if(inputs[i] instanceof FluidStack)
						inputFluidStacks.add((FluidStack) inputs[i]);
					else {

						if(inputs[i] instanceof Item) 
							inputs[i] = new ItemStack((Item)inputs[i]);
						else if(inputs[i] instanceof Block)
							inputs[i] = new ItemStack((Block)inputs[i]);

						innerList.add((ItemStack)inputs[i]);
					}
				}
				if(!innerList.isEmpty())
				stack.add(innerList);
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

	/**
	 * @param clazz Class object of the machine to register the recipe
	 * @param out outout object of the machine, accepts itemStack and fluidStacks
	 * @param timeRequired base running time for the recipe in ticks
	 * @param power power units per tick
	 * @param inputs input objects for the recipe, accepts forge ore dict entries as strings, itemStacks, Items, Blocks, and fluidStacks
	 */
	public void addRecipe(Class clazz , Object out, int timeRequired, int power, Object ... inputs) {
		addRecipe(clazz, new Object[] {out}, timeRequired, power, inputs);
	}

	//Given the class return the list
	public List<IRecipe> getRecipes(Class clazz) {
		return recipeList.get(clazz);
	}
}