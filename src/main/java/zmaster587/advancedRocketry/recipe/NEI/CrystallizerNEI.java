package zmaster587.advancedRocketry.recipe.NEI;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.Inventory.multiblock.GuiCrystallizer;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileCrystallizer;
import zmaster587.libVulpes.interfaces.IRecipe;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class CrystallizerNEI extends TemplateRecipeHandler {

	public class CachedCrystallizerRecipe extends CachedRecipe {
		private ArrayList<PositionedStack> ingredients;
		private PositionedStack result;
		private int energy, time;


		CachedCrystallizerRecipe(IRecipe rec) {
			for(int i = 0; i < rec.getOutput().size(); i++ ) {
				result = new PositionedStack(rec.getOutput(), 112+ 18*(i%3), 4 + 18*(i/3));
			}
			ingredients = new ArrayList<PositionedStack>();
			for(int i = 0; i < rec.getIngredients().size(); i++ ) {
				ingredients.add(new PositionedStack(rec.getIngredients().get(i), 4 + 18*(i%3), 4 + 18*(i/3)));
			}
			energy = rec.getPower();
			time = rec.getTime();
		}

		public void computeVisuals() {
			for (PositionedStack p : ingredients)
				p.generatePermutations();
		}

		@Override
		public PositionedStack getResult() {
			return result;
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return ingredients;
		}
		public int getEnergy() {return energy;}
		public int getTime() {return time;}

	}

	@Override
	public String getRecipeName() {
		return "Crystallizer";
	}

	/*@Override
	public void loadCraftingRecipes(ItemStack result) {
		for(IRecipe i : RecipesCrystallizer.instance.getRecipes()) {

			boolean match = false;
			for(ItemStack stack : i.getOutput() ) {
				match = NEIServerUtils.areStacksSameTypeCrafting(stack, result);
				if(match)
					break;
			}

			if(match) {
				CachedCrystallizerRecipe recipe = new CachedCrystallizerRecipe(i);
				recipe.computeVisuals();
				arecipes.add(recipe);
			}
		}
	}*/

	public void loadCraftingRecipes(ItemStack result) {
		for(IRecipe i : RecipesMachine.getInstance().getRecipes(TileCrystallizer.class)) {

			boolean match = false;
			for(ItemStack stack : i.getOutput() ) {
				match = NEIServerUtils.areStacksSameTypeCrafting(stack, result);
				if(match)
					break;
			}

			if(match) {
				CachedCrystallizerRecipe recipe = new CachedCrystallizerRecipe(i);
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
		if(getClass() == CrystallizerNEI.class)
			loadUsageRecipes((ItemStack) results[0]);
		else
			super.loadCraftingRecipes(outputId, results);
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for(IRecipe irecipe : RecipesMachine.getInstance().getRecipes(TileCrystallizer.class)) {
			CachedCrystallizerRecipe recipe = new CachedCrystallizerRecipe(irecipe);

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
		drawTexturedModalRect(3,3, 7, 16, 163, 55);
	}

	@Override
	public void drawForeground(int recipe)
	{
		super.drawForeground(recipe);
		
		GuiDraw.drawString(((CachedCrystallizerRecipe)arecipes.get(recipe)).getEnergy() + " RF", 4, 60, 0xFFFFFF);
		GuiDraw.drawString(((CachedCrystallizerRecipe)arecipes.get(recipe)).getTime()/20 + " Seconds", 95, 60, 0xFFFFFF);
	}

	@Override
	public void drawExtras(int recipe)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiCrystallizer.progressBar);
		drawTexturedModalRect(70, 3, 0, 13, 31, 66);
		drawProgressBar(74, 7, 31, 0, 23, 49, 50, 3);
	}

	@Override
	public String getGuiTexture() {
		return "advancedrocketry:textures/gui/GenericNeiBackground.png";
	}
}