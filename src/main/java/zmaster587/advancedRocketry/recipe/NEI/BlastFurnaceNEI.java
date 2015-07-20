package zmaster587.advancedRocketry.recipe.NEI;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.recipe.RecipesBlastFurnace;
import zmaster587.advancedRocketry.recipe.RecipesBlastFurnace.RecipeBlastFurnace;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class BlastFurnaceNEI extends TemplateRecipeHandler {
	
	public class CachedBlastFurnaceRecipe extends CachedRecipe {
		private ArrayList<PositionedStack> ingredients;
		private PositionedStack output;
		private int time;
		
		CachedBlastFurnaceRecipe(RecipeBlastFurnace recipe) {
			ingredients = new ArrayList<PositionedStack>();
			ingredients.add(new PositionedStack(recipe.getIngredients(),5,16));
			output = new PositionedStack(recipe.getOutput(),133,16);
			time = recipe.getTimeRequired();
		}
		
        public void computeVisuals() {
            for (PositionedStack p : ingredients)
                p.generatePermutations();
        }
        
        public int getTime() { return time; }

		@Override
		public PositionedStack getResult() {
			return output;
		}
		
        @Override
        public List<PositionedStack> getIngredients() {
            return ingredients;
        }
        
        @Override
        public PositionedStack getOtherStack() 
        {
        	return afuels.get((cycleticks / 48) % afuels.size()).stack;
            
        }
	}
	
    public static class FuelPairBlast
    {
        public PositionedStack stack;
        public int burnTime;
        
        public FuelPairBlast(ItemStack ingred, int burnTime) {
            this.stack = new PositionedStack(ingred, 51, 16, false);
            this.burnTime = burnTime;
        }
    }
	
	private static ArrayList<FuelPairBlast> afuels;
	
	public BlastFurnaceNEI() {
		super();
		afuels = new ArrayList<FuelPairBlast>();
		
		ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
        
        for(ItemStack i : RecipesBlastFurnace.getInstance().getFuels()) {
        	afuels.add(new FuelPairBlast(i, RecipesBlastFurnace.getInstance().getFuelValue(i)));
        }
	}
	
	@Override
	public String getRecipeName() {
		return "Blast Furnace";
	}
	
    @Override
    public void loadCraftingRecipes(ItemStack result) {
    	for(RecipeBlastFurnace i : RecipesBlastFurnace.getInstance().getRecipes()) {
    		if(NEIServerUtils.areStacksSameTypeCrafting(i.getOutput(), result)) {
    			CachedBlastFurnaceRecipe recipe = new CachedBlastFurnaceRecipe(i);
    			recipe.computeVisuals();
    			arecipes.add(recipe);
    		}
    	}
    }
    
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
    	if(outputId.equals("blast") && getClass() == BlastFurnaceNEI.class) {
    		for(RecipeBlastFurnace recipe : RecipesBlastFurnace.getInstance().getRecipes()) {
  
    			CachedBlastFurnaceRecipe rec = new CachedBlastFurnaceRecipe(recipe);
    			
    			rec.computeVisuals();
    			arecipes.add(rec);
    		}
    	}
    	else
    		super.loadCraftingRecipes(outputId, results);
    }
    
    @Override
    public void loadUsageRecipes(String outputId, Object... results) {
    	if(getClass() == BlastFurnaceNEI.class)
    		loadUsageRecipes((ItemStack) results[0]);
    	else
    		super.loadCraftingRecipes(outputId, results);
    }
    
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
    	for(RecipeBlastFurnace irecipe : RecipesBlastFurnace.getInstance().getRecipes()) {
    		CachedBlastFurnaceRecipe recipe = new CachedBlastFurnaceRecipe(irecipe);
    		
    		if(!recipe.contains(recipe.getIngredients(), ingredient))
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
		drawTexturedModalRect(0, 0, 3, 3, 150, 85);
	}
	
	@Override
    public void drawExtras(int recipe)
    {
		drawProgressBar(88, 17, 176, 0, 39, 49, 100, 0);
		drawProgressBar(50, 10, 176, 49, 36, 3, 1F, 0);
    }
	
	@Override
	public String getGuiTexture() {
		return "advancedrocketry:textures/gui/BlastFurnace.png";
	}
}
