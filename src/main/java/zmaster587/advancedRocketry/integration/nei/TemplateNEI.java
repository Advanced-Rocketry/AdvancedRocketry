package zmaster587.advancedRocketry.integration.nei;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.lwjgl.opengl.GL11;

import zmaster587.advancedRocketry.integration.nei.PositionedFluidStack;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.recipe.RecipesMachine.Recipe;
import zmaster587.libVulpes.util.ZUtils;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;

public abstract class TemplateNEI extends TemplateRecipeHandler {


	public class CachedMachineRecipe extends CachedRecipe {
		private ArrayList<PositionedStack> ingredients;
		private ArrayList<PositionedStack> result;
		private ArrayList<PositionedFluidStack> fluids;
		private int energy, time;


		CachedMachineRecipe(IRecipe rec) {
			//TODO: multiple outputs
			result  = new ArrayList<PositionedStack>();
			fluids = new ArrayList<PositionedFluidStack>();
			int inputIndex = 0;
			int outputIndex = 0;
			for(int i = 0; i < rec.getOutput().size(); i++, outputIndex++ ) {
				result.add(new PositionedStack(rec.getOutput().get(i), 112+ 18*(i%3), 4 + 18*(i/3)));
			}
			
			ingredients = new ArrayList<PositionedStack>();
			for(int i = 0; i < rec.getIngredients().size(); i++, inputIndex++ ) {
				ingredients.add(new PositionedStack(rec.getIngredients().get(i), 4 + 18*(i%3), 4 + 18*(i/3)));
			}
			
			for(int i = 0; i < rec.getFluidIngredients().size(); i++, inputIndex++) {
				fluids.add(new PositionedFluidStack(rec.getFluidIngredients().get(i), 4 + 18*(inputIndex%3), 4 + 18*(inputIndex/3)));
			}
			
			for(int i = 0; i < rec.getFluidOutputs().size(); i++, outputIndex++) {
				fluids.add(new PositionedFluidStack(rec.getFluidOutputs().get(i), 112+ 18*(outputIndex%3), 4 + 18*(outputIndex/3)));
			}
			
			energy = rec.getPower();
			time = rec.getTime();
		}

		public void computeVisuals() {
			for (PositionedStack p : ingredients)
				p.generatePermutations();
		}

		
		
		public List<PositionedStack> getResults() {
			return result;
		}
		
		@Override
		public List<PositionedStack> getOtherStacks() {
			return getResults();
		}
		
		
		public PositionedStack getResult() {
			return null;//result.get(0);
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(TemplateNEI.this.cycleticks / 20, ingredients);
		}
		
		public List<PositionedFluidStack> getFluids() {
			return fluids;
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
			IRecipe newRecipe = i;
			
			boolean match = false;
			for(ItemStack stack : i.getOutput() ) {
				match = NEIServerUtils.areStacksSameTypeCrafting(stack, result) || ZUtils.areOresSameTypeOreDict(stack, result);
				if(match)
					break;
			}

			if(!match && (FluidContainerRegistry.isFilledContainer(result) || result.getItem() instanceof IFluidContainerItem || 
					(Block.getBlockFromItem(result.getItem()) != Blocks.air && FluidRegistry.lookupFluidForBlock(Block.getBlockFromItem(result.getItem())) != null  ))) {
				
				FluidStack fluidStack = null;
				
				if(result.getItem() instanceof IFluidContainerItem)
					fluidStack  = ((IFluidContainerItem)result.getItem()).getFluid(result);
				else if(FluidContainerRegistry.isFilledContainer(result) && FluidContainerRegistry.isFilledContainer(result))
					fluidStack = FluidContainerRegistry.getFluidForFilledItem(result);
					
				for(FluidStack stack : ((Recipe)i).getFluidOutputs() ) {
					if((fluidStack != null && fluidStack.getFluid() == stack.getFluid()) ||
							(Block.getBlockFromItem(result.getItem()) != Blocks.air && FluidRegistry.lookupFluidForBlock(Block.getBlockFromItem(result.getItem())) == stack.getFluid() )) {
						match = true;
						break;
					}
				}
			}

			if(match) {
				CachedMachineRecipe recipe = new CachedMachineRecipe(((Recipe)i));
				recipe.computeVisuals();
				arecipes.add(recipe);
				match = false;
			}
		}
	}


	public void loadAllRecipes() {
		for(IRecipe i : RecipesMachine.getInstance().getRecipes(getMachine())) {
			arecipes.add(new CachedMachineRecipe((Recipe)i));
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
			CachedMachineRecipe recipe = new CachedMachineRecipe((((Recipe)irecipe).getRecipeAsAllItemsOnly()));
			boolean match = false;
			
			for(PositionedStack posStack : recipe.getIngredients()) {
				if(posStack.item.isItemEqual(ingredient) || ZUtils.areOresSameTypeOreDict(posStack.item, ingredient)) {
					recipe.setIngredientPermutation(recipe.ingredients, ingredient);
					arecipes.add(recipe);
					match = true;
					break;
				}
			}
			
			if(!match && (FluidContainerRegistry.isFilledContainer(ingredient) || ingredient.getItem() instanceof IFluidContainerItem || 
					(Block.getBlockFromItem(ingredient.getItem()) != Blocks.air && FluidRegistry.lookupFluidForBlock(Block.getBlockFromItem(ingredient.getItem())) != null  ))) {
				
				FluidStack fluidStack = null;
				
				if(ingredient.getItem() instanceof IFluidContainerItem)
					fluidStack  = ((IFluidContainerItem)ingredient.getItem()).getFluid(ingredient);
				else if(FluidContainerRegistry.isFilledContainer(ingredient) && FluidContainerRegistry.isFilledContainer(ingredient))
					fluidStack = FluidContainerRegistry.getFluidForFilledItem(ingredient);
				
				for(FluidStack stack : ((Recipe)irecipe).getFluidIngredients() ) {
					if((fluidStack != null && fluidStack.getFluid() == stack.getFluid()) ||
							(Block.getBlockFromItem(ingredient.getItem()) != Blocks.air && FluidRegistry.lookupFluidForBlock(Block.getBlockFromItem(ingredient.getItem())) == stack.getFluid() )) {
						recipe.setIngredientPermutation(recipe.ingredients, ingredient);
						arecipes.add(recipe);
						break;
					}
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
		this.drawFluidTanks((CachedMachineRecipe)arecipes.get(recipe));
		GuiDraw.drawString(((CachedMachineRecipe)arecipes.get(recipe)).getEnergy() + " RF/t", 4, 60, 0x3d3d3d, false);
		GuiDraw.drawString(((CachedMachineRecipe)arecipes.get(recipe)).getTime()/20 + " Seconds", 4, 70, 0x3d3d3d, false);

	}
	@Override
	public void drawExtras(int recipe)
	{
		ProgressBarImage progressBar = getProgressBar();
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureResources.progressBars);
		drawTexturedModalRect(65, 3, progressBar.getBackOffsetX(), progressBar.getBackOffsetY(), progressBar.getBackWidth(), progressBar.getBackHeight());


		drawProgressBar(65 + progressBar.getInsetX(), 3 +  + progressBar.getInsetY(), progressBar.getForeOffsetX(), progressBar.getForeOffsetY(), progressBar.getForeWidth(),  progressBar.getForeHeight(), 50, progressBar.getDirection().getRotation(ForgeDirection.SOUTH).ordinal());
	}
	
    public void drawFluidTanks(CachedMachineRecipe r) {
        if (r.getFluids() != null) {
            for (PositionedFluidStack fluidTank : r.getFluids()) {
                fluidTank.draw();
            }
        }
    }
    
    public List<String> provideTooltip(GuiRecipe guiRecipe, List<String> currenttip, CachedMachineRecipe crecipe, Point relMouse) {
        if (crecipe.getFluids() != null) {
            for (PositionedFluidStack tank : crecipe.getFluids()) {
                if (relMouse.x > tank.posX && relMouse.x < tank.posX + PositionedFluidStack.size && 
                		relMouse.y > tank.posY && relMouse.y < tank.posY + PositionedFluidStack.size) {
                    tank.handleTooltip(currenttip);
                }
            }
        }
        return currenttip;
    }

    @Override
    public List<String> handleTooltip(GuiRecipe guiRecipe, List<String> currenttip, int recipe) {
        super.handleTooltip(guiRecipe, currenttip, recipe);
        CachedMachineRecipe crecipe = (CachedMachineRecipe) this.arecipes.get(recipe);
        if (GuiContainerManager.shouldShowTooltip(guiRecipe)) {
            Point mouse = GuiDraw.getMousePosition();
            Point offset = guiRecipe.getRecipePosition(recipe);
            Point relMouse = new Point(mouse.x - (guiRecipe.width - 176) / 2 - offset.x, mouse.y - (guiRecipe.height - 166) / 2 - offset.y);
            
            currenttip = this.provideTooltip(guiRecipe, currenttip, crecipe, relMouse);
        }
        return currenttip;
}
    
	@Override
	public String getGuiTexture() {
		return "advancedrocketry:textures/gui/GenericNeiBackground.png";
	}
}
