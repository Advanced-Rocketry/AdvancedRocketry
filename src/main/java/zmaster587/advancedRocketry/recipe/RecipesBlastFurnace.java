package zmaster587.advancedRocketry.recipe;

import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

import zmaster587.advancedRocketry.api.recipe.ITimedPoweredMachine;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class RecipesBlastFurnace {

	public class RecipeBlastFurnace {
		private ItemStack output;
		private ArrayList<ItemStack> input;
		int completionTime;

		public RecipeBlastFurnace(ItemStack output, int time, ArrayList<ItemStack> obj, int amt) {
			this.output = output;
			input = obj;

			completionTime = time;
		}

		public ArrayList<ItemStack> getIngredients() {
			return input;
		}

		public boolean matchesStack(ItemStack item) {
			if(item == null)
				return false;

			for(ItemStack stack : input)
				if(stack.isItemEqual(item))
					return true;

			return false;
		}

		public ItemStack getOutput(ItemStack input) {return output;}

		public ItemStack getOutput() {return output.copy();}
		public int getTimeRequired() {return completionTime; }
	}

	public class BlastFurnaceFuels {
		ArrayList<SimpleEntry<Object, Integer>> fuels;

		BlastFurnaceFuels(Object fuel, int time) {
			fuels = new ArrayList<SimpleEntry<Object, Integer>>();
			fuels.add(new SimpleEntry(fuel, time));
		}

		public int getFuelValue(ItemStack item) {
			for(SimpleEntry<Object, Integer> fuel : fuels)
				if(fuel.getKey() instanceof ItemStack) {
					if(item.isItemEqual((ItemStack)fuel.getKey()))
						return fuel.getValue();
				}
				else if(OreDictionary.getOres(OreDictionary.getOreID(item)).contains((String)fuel.getKey()))
					return fuel.getValue();
			return -1;
		}

		public boolean isObjectFuel(ItemStack item) { 
			return getFuelValue(item) != -1;
		}

		public ArrayList<ItemStack> getFuelItems() {
			ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
			for(SimpleEntry<Object, Integer> fuel : fuels) {
				if(fuel.getKey() instanceof ItemStack)
					ret.add((ItemStack)fuel.getKey());
				else
					ret.addAll(OreDictionary.getOres((String)fuel.getKey()));
			}
			return ret;
		}
	}

	private ArrayList<RecipeBlastFurnace> recipies;
	private ArrayList<BlastFurnaceFuels> fuels;
	private static RecipesBlastFurnace instance = new RecipesBlastFurnace();

	public static RecipesBlastFurnace getInstance() { return instance; }

	private RecipesBlastFurnace() {
		recipies = new ArrayList<RecipeBlastFurnace>();
		fuels = new ArrayList<BlastFurnaceFuels>();
	}

	public ArrayList<RecipeBlastFurnace> getRecipes() {
		return recipies;
	}

	public void addRecipe(ItemStack output, int time, Object obj, int amt) {
		//Check to make sure the items exist...
		ArrayList<ItemStack> ingrdients = new ArrayList<ItemStack>();
		if(obj instanceof String) {
			if(OreDictionary.getOres((String)obj).isEmpty()){
				return;
			}
			ingrdients = OreDictionary.getOres((String)obj);
		}
		else if(obj instanceof Item)
			obj = new ItemStack((Item)obj);
		else if(obj instanceof Block)
			obj = new ItemStack((Block)obj);
		//TODO: throw error

		ingrdients.add((ItemStack)obj);
		recipies.add(new RecipeBlastFurnace(output, time, ingrdients, amt));
	}

	public void addFuel(Object obj, int value) {

		if(obj instanceof String && OreDictionary.getOres((String)obj).isEmpty()){
			return;
		}
		if(obj instanceof Item)
			obj = new ItemStack((Item)obj);
		else if(obj instanceof Block)
			obj = new ItemStack((Block)obj);
		fuels.add(new BlastFurnaceFuels(obj, value));
	}

	public boolean isValidInputStack(ItemStack item) {
		for(RecipeBlastFurnace recipe : recipies) {
			if(recipe.matchesStack(item))
				return true;
		}

		return false;
	}

	public ItemStack getRecipeOutput(IInventory inv, int slot, World world) {

		for(RecipeBlastFurnace recipe : recipies) {

			if(recipe.matchesStack(inv.getStackInSlot(slot)))
				return recipe.getOutput();
		}
		return null;
	}

	public int getRecipeTime(ItemStack ingredient) {
		for(RecipeBlastFurnace i : recipies) {
			if(i.matchesStack(ingredient))
				return i.getTimeRequired();
		}
		return 0;
	}

	/**
	 * Assumed the inventory already has the required resources
	 * @param inv
	 * @param world
	 */
	public void useResources(ITimedPoweredMachine inv,int slot, World world) {
		ItemStack stack = inv.getStackInSlot(slot);
		if(stack != null) {
			for(RecipeBlastFurnace item : recipies) {
				if(item.matchesStack(stack)) {
					//int amt = stack.stackSize / item.getMinNumObject();
					//stack.stackSize %= item.getMinNumObject();
					//if(stack.stackSize == 0)
					inv.decrStackSize(slot, 1);
					inv.setRecipeTime(item.getTimeRequired());
					//else
					//inv.setInventorySlotContents(slot, stack);
					//return amt;
				}
			}
		}
		//return 0;
	}

	public boolean isValidFuel(ItemStack item) {
		if(item == null)
			return false;
		for(BlastFurnaceFuels fuel : fuels) {
			if(fuel.isObjectFuel(item))
				return true;
		}
		return false;
	}

	public int getFuelValue(ItemStack item) {
		for(BlastFurnaceFuels fuel : fuels) {
			int value = fuel.getFuelValue(item);
			if(value != -1)
				return value;
		}
		return -1;
	}

	public ArrayList<ItemStack> getFuels() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for(BlastFurnaceFuels i : fuels) {
			list.addAll(i.getFuelItems());
		}

		return list;
	}
}
