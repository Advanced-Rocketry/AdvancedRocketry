package zmaster587.advancedRocketry.integration.jei;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import zmaster587.libVulpes.interfaces.IRecipe;

public class MachineRecipe extends BlankRecipeWrapper {
	
	private List<List<ItemStack>> ingredients;
	private ArrayList<ItemStack> result;
	private List<FluidStack> fluidIngredients;
	private List<FluidStack> fluidOutputs;
	private int energy, time;


	protected MachineRecipe(IRecipe rec) {
		//TODO: multiple outputs
		result  = new ArrayList<>(rec.getOutput());
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
			FontRenderer fontRendererObj = minecraft.fontRendererObj;
			int stringWidth = fontRendererObj.getStringWidth(powerString);
			fontRendererObj.drawString(powerString, 0, 55, Color.black.getRGB());
			
			String timeString = String.format("Time: %d s", time/20);
			stringWidth = fontRendererObj.getStringWidth(powerString);
			fontRendererObj.drawString(timeString, recipeWidth - 55, 55, Color.black.getRGB());
	}
}
