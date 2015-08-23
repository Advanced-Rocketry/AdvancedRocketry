package zmaster587.advancedRocketry.recipe.NEI;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import java.awt.*;

import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.recipe.RecipesMachine;
import zmaster587.advancedRocketry.tile.multiblock.TilePrecisionAssembler;
import zmaster587.libVulpes.interfaces.IRecipe;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class PrecisionAssemberNEI extends TemplateRecipeHandler {

	private static final int ticksPerBar = 150;
	
	public class CachedPrecisionAssemblerRecipe extends CachedRecipe {
		private ArrayList<PositionedStack> ingredients;
		private PositionedStack result;
		private int energy, time;
		
		@Override
		public PositionedStack getResult() {
			return result;
		}
		
        @Override
        public List<PositionedStack> getIngredients() {
            return ingredients;
        }
		
		public CachedPrecisionAssemblerRecipe(IRecipe recipe) {
			for(int i = 0; i < recipe.getOutput().size(); i++ ) {
				result = new PositionedStack(recipe.getOutput(), 113 + 18*(i%3), 14 + 18*(i/3));
			}
			ingredients = new ArrayList<PositionedStack>();
			for(int i = 0; i < recipe.getIngredients().size(); i++ ) {
				ingredients.add(new PositionedStack(recipe.getIngredients().get(i), 5 + 18*(i%3), 14 + 18*(i/3)));
			}
			energy = recipe.getPower();
			time = recipe.getTime();
		}
		
        public void computeVisuals() {
            for (PositionedStack p : ingredients)
                p.generatePermutations();
        }
        
        public int getEnergy() { return energy;}
        
        public int getTime() { return time; }
	}
	
	@Override
	public String getRecipeName() {
		return "Precision Assembler";
	}
	
    @Override
    public void loadCraftingRecipes(ItemStack result) {
		for(IRecipe i : RecipesMachine.getInstance().getRecipes(TilePrecisionAssembler.class)) {

			boolean match = false;
			for(ItemStack stack : i.getOutput() ) {
				match = NEIServerUtils.areStacksSameTypeCrafting(stack, result);
				if(match)
					break;
			}

			if(match) {
				CachedPrecisionAssemblerRecipe recipe = new CachedPrecisionAssemblerRecipe(i);
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
    	super.loadCraftingRecipes(outputId, results);
    }
    
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
    	for(IRecipe irecipe : RecipesMachine.getInstance().getRecipes(TilePrecisionAssembler.class)) {
    		CachedPrecisionAssemblerRecipe recipe = new CachedPrecisionAssemblerRecipe(irecipe);
    		
    		if(!recipe.contains(recipe.ingredients, ingredient))
    			continue;
  
    		recipe.setIngredientPermutation(recipe.ingredients, ingredient);
    		arecipes.add(recipe);
    	}
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new RecipeTransferRect(new Rectangle(82, 28, 31, 54), "assemble"));
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
		drawTexturedModalRect(0, 0, 3, 3, 167, 85);
	}
	
	@Override
    public void drawForeground(int recipe)
    {
        super.drawForeground(recipe);
        
        GuiDraw.drawString(((CachedPrecisionAssemblerRecipe)arecipes.get(recipe)).getEnergy() + " RF", 7, 85, 0xFFFFFF);
        GuiDraw.drawString(((CachedPrecisionAssemblerRecipe)arecipes.get(recipe)).getTime()/20 + " Seconds", 7, 95, 0xFFFFFF);
    }
	
	@Override
    public void drawExtras(int recipe)
    {
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureResources.progressBars);
		
		drawTexturedModalRect(58, 1, 132, 0, 53, 66);
		
    	byte mode = (byte) ((cycleticks % ticksPerBar)/(ticksPerBar/3));
    	drawTexturedModalRect(62, 1, 90, 45, 12, 13);
    	
    	//(cycleticks % 100) /100f;
    	if(mode == 0)
    		drawProgressBar(93, 23, 54, 42, 13, 15, (cycleticks % (ticksPerBar/3)) /(float)(ticksPerBar/3), 1);
    	else if(mode == 1) {
    		drawTexturedModalRect(61, 22, 78, 42, 12, 13);
    		
    		drawTexturedModalRect(93, 23, 54, 42, 13, 15);
    		
    		drawProgressBar(94, 42, 67, 42, 11, 15, (cycleticks % (ticksPerBar/3)) /(float)(ticksPerBar/3), 1);
    	}
    	else if(mode == 2) {
    		drawTexturedModalRect(59, 51, 54, 57, 14, 15);
    		drawTexturedModalRect(61, 22, 78, 42, 12, 13);
    		
    		drawTexturedModalRect(93, 23, 54, 42, 13, 15);
    		drawTexturedModalRect(94, 42, 67, 42, 11, 15);
    		
    		drawProgressBar(89, 63, 90, 42, 22, 3, (cycleticks % (ticksPerBar/3)) /(float)(ticksPerBar/3), 0);
    	}
    }
	
	@Override
	public String getGuiTexture() {
		return "advancedrocketry:textures/gui/GenericNeiBackground.png";
	}
}
