package zmaster587.advancedRocketry.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceItemStack;
import zmaster587.libVulpes.recipe.RecipesMachine.Recipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MachineRecipe implements IRecipeWrapper {
	
	private List<List<ItemStack>> ingredients;
	private ArrayList<ItemStack> result;
	private ArrayList<ChanceItemStack> resultChance;
	private List<FluidStack> fluidIngredients;
	private List<FluidStack> fluidOutputs;
	private int energy, time;


	protected MachineRecipe(IRecipe rec) {
		//TODO: multiple outputs
		if(rec instanceof Recipe)
		{
			resultChance = new ArrayList<>(((Recipe) rec).getChanceOutputs());
			result = new ArrayList<>();
			
			int i = -1;
			float totalChance = 0;
			for( ChanceItemStack stack : resultChance )
				totalChance += stack.chance;
			
			for( ChanceItemStack stack : resultChance )
			{
				i++;
				if(stack.chance == 0)
				{
					result.add(stack.stack.copy());
					continue;
				}
				
				ItemStack stack2 = stack.stack.copy();
				stack2.setStackDisplayName(String.format("%s   Chance: %.1f%%",  stack2.getDisplayName(), 100*stack.chance/totalChance));
				result.add(stack2);
			}
		}
		else
		{
			result  = new ArrayList<>(rec.getOutput());
		}
		ingredients = rec.getIngredients();
		energy = rec.getPower();
		time = rec.getTime();
		fluidIngredients = rec.getFluidIngredients();
		fluidOutputs = rec.getFluidOutputs();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, this.ingredients);
		ingredients.setOutputs(ItemStack.class, result);
		ingredients.setInputs(FluidStack.class, fluidIngredients);
		ingredients.setOutputs(FluidStack.class, fluidOutputs);
	}
	
	public List<ItemStack> getResults() {
		return result;
	}
	
	public List<List<ItemStack>> getInputs() {
		return ingredients;
	}
	
	public int getEnergy() {return energy;}
	public int getTime() {return time;}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth,
			int recipeHeight, int mouseX, int mouseY) {
		
			String powerString = String.format("Power: %d RF/t", energy);
			FontRenderer fontRendererObj = minecraft.fontRenderer;
			int stringWidth = fontRendererObj.getStringWidth(powerString);
			fontRendererObj.drawString(powerString, 0, 55, Color.black.getRGB());
			
			String timeString = String.format("Time: %d s", time/20);
			stringWidth = fontRendererObj.getStringWidth(powerString);
			fontRendererObj.drawString(timeString, recipeWidth - 55, 55, Color.black.getRGB());
			
	}
}
