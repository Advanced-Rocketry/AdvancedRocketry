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
import zmaster587.libVulpes.recipe.RecipesMachine.LibVulpesRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class MachineRecipe extends LibVulpesRecipe implements IRecipeCategoryExtension {
	private final List<List<ItemStack>> ingredients;
	private final ArrayList<ItemStack> result;
	private ArrayList<ChanceItemStack> resultChance;
	private final List<FluidStack> fluidIngredients;
	private final List<FluidStack> fluidOutputs;
	private final int energy;
	private final int time;
	private ResourceLocation name;


	protected MachineRecipe(IRecipe rec) {
		if(rec instanceof LibVulpesRecipe) {
			resultChance = new ArrayList<>(((LibVulpesRecipe) rec).getChanceOutputs());
			result = new ArrayList<>();

			float totalChance = 0;
			for( ChanceItemStack stack : resultChance)
				totalChance += stack.chance;
			
			for( ChanceItemStack stack : resultChance) {
				if(stack.chance == 0) {
					result.add(stack.stack.copy());
					continue;
				}
				
				ItemStack stack2 = stack.stack.copy();
				stack2.setDisplayName(new StringTextComponent(String.format("%s   Chance: %.1f%%",  stack2.getDisplayName(), 100*stack.chance/totalChance)));
				result.add(stack2);
			}
		} else {
			result  = new ArrayList<>(rec.getOutput());
		}
		ingredients = rec.getPossibleIngredients();
		energy = rec.getPower();
		time = rec.getTime();
		fluidIngredients = rec.getFluidIngredients();
		fluidOutputs = rec.getFluidOutputs();
		name = rec.getId();
	}
	
	public List<ItemStack> getResults() {
		return result;
	}

	public List<FluidStack> getFluidResults() {
		return fluidOutputs;
	}
	
	public List<List<ItemStack>> getInputs() {
		return ingredients;
	}

	public List<FluidStack> getFluidInputs() {
		return fluidIngredients;
	}
	
	public int getEnergy() {return energy;}
	public int getTime() {return time;}
	
	@Override
	@ParametersAreNonnullByDefault
	public void drawInfo(int recipeWidth, int recipeHeight, MatrixStack matrixStack, double mouseX, double mouseY) {
		
		String powerString = String.format("Power: %d RF/t", energy);
		FontRenderer fontRendererObj = Minecraft.getInstance().fontRenderer;
		fontRendererObj.drawTextWithShadow(matrixStack, new StringTextComponent(powerString), 0, 55, Color.black.getRGB());
		
		String timeString = String.format("Time: %d s", time/20);
		fontRendererObj.drawTextWithShadow(matrixStack, new StringTextComponent(timeString), recipeWidth - 55, 55, Color.black.getRGB());
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void setIngredients(IIngredients ingredients) {
		// TODO Auto-generated method stub
	}

	@Override
	public ResourceLocation getId() {return name;}

}
