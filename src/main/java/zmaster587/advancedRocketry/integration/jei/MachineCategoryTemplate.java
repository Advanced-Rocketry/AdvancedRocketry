package zmaster587.advancedRocketry.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceFluidStack;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class MachineCategoryTemplate<T extends MachineRecipe> implements IRecipeCategory<T> {

	IDrawable background;
	ProgressBarImage bar;
	IDrawable icon;
	
	public MachineCategoryTemplate(IGuiHelper helper, ProgressBarImage bar, ItemStack icon) {
		//blit(3,3, 7, 16, 163, 55);
		background = helper.createDrawable(new ResourceLocation("advancedrocketry:textures/gui/genericneibackground.png"), 7, 16, 163, 55 ); //helper.createDrawable(bar.getResourceLocation(), bar.getBackOffsetX(),
				//bar.getBackOffsetY(), bar.getBackWidth(), bar.getBackHeight());
		this.bar = bar;
		this.icon = helper.createDrawableIngredient(icon);
	}
	
	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}
	
	
	@Nonnull
	@Override
	public IDrawable getIcon() {
		return icon;
	}
	
	@Override
	@ParametersAreNonnullByDefault
	public void draw(T recipe, MatrixStack matrixStack, double mouseX, double mouseY) {IRecipeCategory.super.draw(recipe, matrixStack, mouseX, mouseY);
		
		ProgressBarImage progressBar = bar;
		Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.progressBars);
		
		Minecraft.getInstance().currentScreen.blit(matrixStack, 65, 3, progressBar.getBackOffsetX(), progressBar.getBackOffsetY(), progressBar.getBackWidth(), progressBar.getBackHeight());

		progressBar.renderProgressBar(matrixStack, 65, 3, (System.currentTimeMillis() % 3000)/3000f, Minecraft.getInstance().currentScreen);
	}
	
	
	@Override
	public void setIngredients(T recipe, IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, recipe.getInputs());
		ingredients.setInputs(VanillaTypes.FLUID, recipe.getFluidInputs());

		List<ItemStack> outputStacks = new LinkedList<>(recipe.getResults());
		
		List<FluidStack> outputFluids = new LinkedList<>(recipe.getFluidResults());
		
		ingredients.setOutputs(VanillaTypes.ITEM, outputStacks);
		ingredients.setOutputs(VanillaTypes.FLUID, outputFluids);
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, @Nonnull T recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		int numOutputs = ingredients.getOutputs(VanillaTypes.ITEM).size() + ingredients.getOutputs(VanillaTypes.FLUID).size();
		
		for(int i = 0; i < 9; i++ ) {
			guiItemStacks.init(i, true,   18*(i%3),  18*(i/3));
			
			//Set capacity to 1mb to make sure it fills the screen
			guiFluidStacks.init(i, true,   18*(i%3) + 1,  18*(i/3) + 1, 16, 16, 1, false, null);
		}
		
		for(int i = 0; i < numOutputs; i++ ) {
			guiItemStacks.init(i+9, false, 108 + 18*(i%3),  18*(i/3));
			guiFluidStacks.init(i+9, false, 108 + 18*(i%3) + 1,  18*(i/3) + 1, 16, 16, 1, false, null);
		}
		
		int i = 0;
		
		for(List<ItemStack> stacks : ingredients.getInputs(VanillaTypes.ITEM)) {
			guiItemStacks.set(i++, stacks);
		}
		
		for(List<FluidStack> stacks : ingredients.getInputs(VanillaTypes.FLUID)) {
			guiFluidStacks.set(i++, stacks);
		}
		
		i = 9;
		
		for(List<ItemStack> stacks : ingredients.getOutputs(VanillaTypes.ITEM)) {
			guiItemStacks.set(i++, stacks);
		}
		
		for(List<FluidStack> stacks : ingredients.getOutputs(VanillaTypes.FLUID)) {
			guiFluidStacks.set(i++, stacks);
		}
	}

}
