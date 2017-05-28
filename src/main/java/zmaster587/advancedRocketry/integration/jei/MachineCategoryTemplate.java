package zmaster587.advancedRocketry.integration.jei;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.client.util.ProgressBarImage;

public abstract class MachineCategoryTemplate<T extends MachineRecipe> extends BlankRecipeCategory<T> {

	IDrawable background;
	ProgressBarImage bar;
	
	public MachineCategoryTemplate(IGuiHelper helper, ProgressBarImage bar) {
		//drawTexturedModalRect(3,3, 7, 16, 163, 55);
		background = helper.createDrawable(new ResourceLocation("advancedrocketry:textures/gui/GenericNeiBackground.png"), 7, 16, 163, 55 ); //helper.createDrawable(bar.getResourceLocation(), bar.getBackOffsetX(),
				//bar.getBackOffsetY(), bar.getBackWidth(), bar.getBackHeight());
		this.bar = bar;
	}
	
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawAnimations(Minecraft minecraft) {
		
		ProgressBarImage progressBar = bar;
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureResources.progressBars);
		
		minecraft.currentScreen.drawTexturedModalRect(65, 3, progressBar.getBackOffsetX(), progressBar.getBackOffsetY(), progressBar.getBackWidth(), progressBar.getBackHeight());

		progressBar.renderProgressBar(65, 3, (Minecraft.getSystemTime() % 3000)/3000f, minecraft.currentScreen);
		//drawProgressBar(65 + progressBar.getInsetX(), 3 +  + progressBar.getInsetY(), progressBar.getForeOffsetX(), progressBar.getForeOffsetY(), progressBar.getForeWidth(),  progressBar.getForeHeight(), 50, progressBar.getDirection().rotateAround(EnumFacing.Axis.Z));
	
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout,
			T recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
		
		for(int i = 0; i < 10; i++ ) {
			guiItemStacks.init(i, true,   18*(i%3),  18*(i/3));
			guiFluidStacks.init(i, true,   18*(i%3) + 1,  18*(i/3) + 1);
		}
		
		for(int i = 0; i < 10; i++ ) {
			guiItemStacks.init(i+9, false, 108 + 18*(i%3),  18*(i/3));
			guiFluidStacks.init(i+9, false, 108 + 18*(i%3) + 1,  18*(i/3) + 1);
		}
		
		int i = 0;
		
		for(List<ItemStack> stacks : ingredients.getInputs(ItemStack.class)) {
			guiItemStacks.set(i++, stacks);
		}
		
		for(List<FluidStack> stacks : ingredients.getInputs(FluidStack.class)) {
			guiFluidStacks.set(i++, stacks);
		}
		
		i = 9;
		
		for(ItemStack stacks : ingredients.getOutputs(ItemStack.class)) {
			guiItemStacks.set(i++, stacks);
		}
		
		for(List<FluidStack> stacks : ingredients.getOutputs(FluidStack.class)) {
			guiFluidStacks.set(i++, stacks);
		}
	}

}
