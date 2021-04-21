package zmaster587.advancedRocketry.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceItemStack;
import zmaster587.libVulpes.recipe.RecipesMachine.Recipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public class MachineRecipe extends Recipe implements IRecipeCategoryExtension {
	private List<List<ItemStack>> ingredients;
	private ArrayList<ItemStack> result;
	private ArrayList<ChanceItemStack> resultChance;
	private List<FluidStack> fluidIngredients;
	private List<FluidStack> fluidOutputs;
	private int energy, time;
	private ResourceLocation name;


	protected MachineRecipe(IRecipe rec) {
		//TODO: multiple outputs
		if(rec instanceof Recipe)
		{
			resultChance = new ArrayList<ChanceItemStack>(((Recipe)rec).getChanceOutputs());
			result = new ArrayList<>();
			
			int i = -1;
			float totalChance = 0;
			this.name = rec.getId();
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
				stack2.setDisplayName(new StringTextComponent(String.format("%s   Chance: %.1f%%",  stack2.getDisplayName(), 100*stack.chance/totalChance)));
				result.add(stack2);
			}
		}
		else
		{
			result  = new ArrayList<>(rec.getOutput());
		}
		ingredients = rec.getPossibleIngredients();
		energy = rec.getPower();
		time = rec.getTime();
		fluidIngredients = rec.getFluidIngredients();
		fluidOutputs = rec.getFluidOutputs();
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
	public void drawInfo(int recipeWidth, int recipeHeight, MatrixStack matrixStack, double mouseX, double mouseY) {
		
		String powerString = String.format("Power: %d RF/t", energy);
		FontRenderer fontRendererObj = Minecraft.getInstance().fontRenderer;
		int stringWidth = fontRendererObj.getStringWidth(powerString);
		fontRendererObj.func_243246_a(matrixStack, new StringTextComponent(powerString), 0, 55, Color.black.getRGB());
		
		String timeString = String.format("Time: %d s", time/20);
		stringWidth = fontRendererObj.getStringWidth(powerString);
		fontRendererObj.func_243246_a(matrixStack, new StringTextComponent(timeString), recipeWidth - 55, 55, Color.black.getRGB());
	}
	
	@Override
	public void setIngredients(IIngredients ingredients) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public ResourceLocation getId() {
		return name;
	}
}
