package zmaster587.advancedRocketry.integration.jei.chemicalReactor;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class ChemicalReactorCategory extends MachineCategoryTemplate<ChemicalReactorlWrapper> {

	public ChemicalReactorCategory(IGuiHelper helper) {
		super(helper, TextureResources.crystallizerProgressBar);
	}

	@Override
	public String getUid() {
		return ARPlugin.chemicalReactorUUID;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.chemreactor.name");
	}

	@Override
	public String getModName()
	{
		return "Advanced Rocketry";
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout,
			ChemicalReactorlWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

		for(int i = 0; i < 10; i++ ) {
			guiItemStacks.init(i, true,   18*(i%3),  18*(i/3));

			//Set capacity to 1mb to make sure it fills the screen
			guiFluidStacks.init(i, true,   18*(i%3) + 1,  18*(i/3) + 1, 16, 16, 1, false, null);
		}

		for(int i = 0; i < 10; i++ ) {
			guiItemStacks.init(i+9, false, 108 + 18*(i%3),  18*(i/3));
			guiFluidStacks.init(i+9, false, 108 + 18*(i%3) + 1,  18*(i/3) + 1, 16, 16, 1, false, null);
		}

		int i = 0;

		boolean isArmorRecipe = false;
		int value = 0;

		for(List<ItemStack> stacks : ingredients.getInputs(ItemStack.class)) {

			if(stacks.get(0).getItem() instanceof ItemArmor)
				isArmorRecipe = true;

			guiItemStacks.set(i++, stacks);
		}

		for(List<FluidStack> stacks : ingredients.getInputs(FluidStack.class)) {
			guiFluidStacks.set(i++, stacks);
		}

		i = 9;

		if(isArmorRecipe)
		{
			List<ItemStack> outputStacks = new LinkedList<>();
			for(ItemStack stacks : ingredients.getInputs(ItemStack.class).get(0)) {
				outputStacks.add(new ItemStack( ingredients.getOutputs(ItemStack.class).get(0).get(0).getItem() ,1, stacks.getItemDamage() ));
			}
			guiItemStacks.set(i++, outputStacks);
		}
		else
		{
			for(List<ItemStack> stacks : ingredients.getOutputs(ItemStack.class)) {
				guiItemStacks.set(i++, stacks);
			}
		}

		for(List<FluidStack> stacks : ingredients.getOutputs(FluidStack.class)) {
			guiFluidStacks.set(i++, stacks);
		}
	}

}
