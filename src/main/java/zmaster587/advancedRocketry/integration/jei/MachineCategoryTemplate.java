package zmaster587.advancedRocketry.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceFluidStack;
import zmaster587.libVulpes.recipe.RecipesMachine.ChanceItemStack;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

public abstract class MachineCategoryTemplate<T extends MachineRecipe> implements IRecipeCategory<T> {

	IDrawable background;
	ProgressBarImage bar;
	IDrawable icon;
	
	public MachineCategoryTemplate(IGuiHelper helper, ProgressBarImage bar, ItemStack icon) {
		//func_238474_b_(3,3, 7, 16, 163, 55);
		background = helper.createDrawable(new ResourceLocation("advancedrocketry:textures/gui/GenericNeiBackground.png"), 7, 16, 163, 55 ); //helper.createDrawable(bar.getResourceLocation(), bar.getBackOffsetX(),
				//bar.getBackOffsetY(), bar.getBackWidth(), bar.getBackHeight());
		this.bar = bar;
		this.icon = new ItemDraw(icon);
	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}
	
	
	@Override
	public IDrawable getIcon() {
		return icon;
	}
	
	@Override
	public void draw(T recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		IRecipeCategory.super.draw(recipe, matrixStack, mouseX, mouseY);
		
		ProgressBarImage progressBar = bar;
		Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.progressBars);
		
		Minecraft.getInstance().currentScreen.func_238474_b_(matrixStack, 65, 3, progressBar.getBackOffsetX(), progressBar.getBackOffsetY(), progressBar.getBackWidth(), progressBar.getBackHeight());

		progressBar.renderProgressBar(matrixStack, 65, 3, (System.currentTimeMillis() % 3000)/3000f, Minecraft.getInstance().currentScreen);
		//drawProgressBar(65 + progressBar.getInsetX(), 3 +  + progressBar.getInsetY(), progressBar.getForeOffsetX(), progressBar.getForeOffsetY(), progressBar.getForeWidth(),  progressBar.getForeHeight(), 50, progressBar.getDirection().rotateAround(Direction.Axis.Z));
	
	}
	
	
	@Override
	public void setIngredients(T recipe, IIngredients ingredients) {
		// TODO Auto-generated method stub
		ingredients.setInputLists(VanillaTypes.ITEM, recipe.getPossibleIngredients());
		ingredients.setInputs(VanillaTypes.FLUID, recipe.getFluidIngredients());
		
		List<ItemStack> outputStacks = new LinkedList<>();
		for(ChanceItemStack stack : recipe._getRawOutput())
		{
			outputStacks.add(stack.stack);
		}
		
		List<FluidStack> outputFluids = new LinkedList<>();
		for(ChanceFluidStack stack : recipe._getRawFluidOutput())
		{
			outputFluids.add(stack.stack);
		}
		
		ingredients.setOutputs(VanillaTypes.ITEM, outputStacks);
		ingredients.setOutputs(VanillaTypes.FLUID, outputFluids);
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout,
			T recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		int numOutputs = ingredients.getOutputs(VanillaTypes.ITEM).size() + ingredients.getOutputs(VanillaTypes.FLUID).size();
		
		for(int i = 0; i < 10; i++ ) {
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
