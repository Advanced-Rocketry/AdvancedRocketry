package zmaster587.advancedRocketry.integration.jei;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import zmaster587.libVulpes.interfaces.IRecipe;

public class MachineRecipe extends BlankRecipeWrapper {
	
	private List<List<ItemStack>> ingredients;
	private ArrayList<ItemStack> result;
	private int energy, time;


	protected MachineRecipe(IRecipe rec) {
		//TODO: multiple outputs
		result  = new ArrayList<>(rec.getOutput());
		ingredients = rec.getIngredients();
		energy = rec.getPower();
		time = rec.getTime();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, this.ingredients);
		ingredients.setOutputs(ItemStack.class, result);
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
		
			/*String powerString = String.format("Power: %d", energy);
			FontRenderer fontRendererObj = minecraft.fontRendererObj;
			int stringWidth = fontRendererObj.getStringWidth(powerString);
			fontRendererObj.drawString(powerString, recipeWidth - stringWidth, 20, Color.black.getRGB());
			
			String timeString = String.format("Time: %d s", time/20);
			stringWidth = fontRendererObj.getStringWidth(powerString);
			fontRendererObj.drawString(timeString, recipeWidth - stringWidth, 30, Color.black.getRGB());*/
	}
}
