package zmaster587.advancedRocketry.recipe.NEI;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;
import zmaster587.libVulpes.interfaces.IRecipe;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class CuttingMachineNEI extends TemplateRecipeHandler {
	public class CachedCuttingMachineRecipe extends CachedRecipe {
		private ArrayList<PositionedStack> ingredients;
		private int energy, time;
		private PositionedStack result;

		CachedCuttingMachineRecipe(IRecipe recipe) {
			ingredients = new ArrayList<PositionedStack>();
			for(ItemStack i : recipe.getIngredients())
				ingredients.add(new PositionedStack(i, 42, 35));
			result = new PositionedStack(recipe.getOutput(), 142, 35);

			time = recipe.getTime();
			energy = recipe.getPower();
		}

		@Override
		public PositionedStack getResult() {
			return result;
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return ingredients;
		}

		public void computeVisuals() {
			for (PositionedStack p : ingredients)
				p.generatePermutations();
		}

		public int getEnergy() { return energy; }

		public int getTime() { return time; }
	}

	@Override
	public String getRecipeName() {
		return "Cutting Machine";
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for(IRecipe i : RecipesMachine.getInstance().getRecipes(TileCuttingMachine.class)) {

			boolean match = false;
			for(ItemStack stack : i.getOutput() ) {
				match = NEIServerUtils.areStacksSameTypeCrafting(stack, result);
				if(match)
					break;
			}

			if(match) {
				CachedCuttingMachineRecipe recipe = new CachedCuttingMachineRecipe(i);
				recipe.computeVisuals();
				arecipes.add(recipe);
			}
		}
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {

		super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(String outputId, Object... results) {

		super.loadUsageRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for(IRecipe irecipe : RecipesMachine.getInstance().getRecipes(TileCuttingMachine.class)) {
			CachedCuttingMachineRecipe recipe = new CachedCuttingMachineRecipe(irecipe);

			if(!recipe.contains(recipe.ingredients, ingredient))
				continue;

			recipe.setIngredientPermutation(recipe.ingredients, ingredient);
			arecipes.add(recipe);
		}
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	public void drawBackground(int recipe)
	{
		GL11.glColor4f(1, 1, 1, 1);
		changeTexture(getGuiTexture());
		drawTexturedModalRect(0, 0, 3, 3, 160, 85);
	}

	@Override
	public void drawForeground(int recipe)
	{
		super.drawForeground(recipe);
		drawTexturedModalRect(8, 40, 176, 0, 16, 42);
		GuiDraw.drawString(((CachedCuttingMachineRecipe)arecipes.get(recipe)).getEnergy() + " RF", 7, 85, 0xFFFFFF);
		GuiDraw.drawString(((CachedCuttingMachineRecipe)arecipes.get(recipe)).getTime()/20 + " Seconds", 7, 95, 0xFFFFFF);
	}

	@Override
	public void drawExtras(int recipe)
	{
		drawProgressBar(78, 25, 192, 0, 42, 42, 20, 0);
	}

	@Override
	public String getGuiTexture() {
		return "advancedrocketry:textures/gui/CuttingMachine.png";
	}
}
