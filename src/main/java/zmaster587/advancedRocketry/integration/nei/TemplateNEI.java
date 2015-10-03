package zmaster587.advancedRocketry.integration.nei;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.Inventory.GuiModular;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.libVulpes.interfaces.IRecipe;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect;

public abstract class TemplateNEI extends TemplateRecipeHandler {
		
		
		public class CachedMachineRecipe extends CachedRecipe {
			private ArrayList<PositionedStack> ingredients;
			private PositionedStack result;
			private int energy, time;


			CachedMachineRecipe(IRecipe rec) {
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
		
		protected abstract Class getMachine();
		protected abstract ProgressBarImage getProgressBar();
		
	    @Override
	    public void loadTransferRects()
	    {
	        transferRects.add(new RecipeTransferRect(new Rectangle(58, 6, 41, 54), getRecipeName()));
	    }
	    
	    /*@Override
	    public List<Class<? extends GuiContainer>> getRecipeTransferRectGuis() {
	    	List list= new LinkedList();
	    	list.add(GuiModular.class);
	    	return list;
	    }*/
		
		public void loadCraftingRecipes(ItemStack result) {
			super.loadCraftingRecipes(result);
			for(IRecipe i : RecipesMachine.getInstance().getRecipes(getMachine())) {

				boolean match = false;
				for(ItemStack stack : i.getOutput() ) {
					match = NEIServerUtils.areStacksSameTypeCrafting(stack, result);
					if(match)
						break;
				}

				if(match) {
					CachedMachineRecipe recipe = new CachedMachineRecipe(i);
					recipe.computeVisuals();
					arecipes.add(recipe);
				}
			}
		}
	
		
		public void loadAllRecipes() {
			for(IRecipe i : RecipesMachine.getInstance().getRecipes(getMachine())) {
				arecipes.add(new CachedMachineRecipe(i));
			}
		}
		
		@Override
		public void loadCraftingRecipes(String outputId, Object... results) {
				super.loadCraftingRecipes(outputId, results);
				if(outputId.equals(getRecipeName())) {
					loadAllRecipes();
				}
		}

		@Override
		public void loadUsageRecipes(ItemStack ingredient) {
			for(IRecipe irecipe : RecipesMachine.getInstance().getRecipes(getMachine())) {
				CachedMachineRecipe recipe = new CachedMachineRecipe(irecipe);
				
				for(PositionedStack posStack : recipe.getIngredients()) {
					if(posStack.item.isItemEqual(ingredient)) {
						recipe.setIngredientPermutation(recipe.ingredients, ingredient);
						arecipes.add(recipe);
						break;
					}
				}
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
			GuiDraw.drawString(((CachedMachineRecipe)arecipes.get(recipe)).getEnergy() + " RF/t", 4, 60, 0x3d3d3d, false);
			GuiDraw.drawString(((CachedMachineRecipe)arecipes.get(recipe)).getTime()/20 + " Seconds", 4, 70, 0x3d3d3d, false);
		
		}
		@Override
		public void drawExtras(int recipe)
		{
			ProgressBarImage progressBar = getProgressBar();
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureResources.progressBars);
			drawTexturedModalRect(65, 3, progressBar.getBackOffsetX(), progressBar.getBackOffsetY(), progressBar.getBackWidth(), progressBar.getBackHeight());
			
			
			drawProgressBar(65 + progressBar.getInsetX(), 3 +  + progressBar.getInsetY(), progressBar.getForeOffsetX(), progressBar.getForeOffsetY(), progressBar.getForeWidth(),  progressBar.getForeHeight(), 50, progressBar.getDirection().ordinal());
		}

		@Override
		public String getGuiTexture() {
			return "advancedrocketry:textures/gui/GenericNeiBackground.png";
		}
}
