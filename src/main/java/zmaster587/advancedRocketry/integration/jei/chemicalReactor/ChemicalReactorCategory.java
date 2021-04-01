package zmaster587.advancedRocketry.integration.jei.chemicalReactor;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.ItemDraw;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class ChemicalReactorCategory extends MachineCategoryTemplate<MachineRecipe> {

	public ChemicalReactorCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, TextureResources.crystallizerProgressBar, icon);
	}

	@Override
	public ResourceLocation getUid() {
		return ARPlugin.chemicalReactorUUID;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("block.advancedrocketry.chemicalreactor");
	}
	
	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MachineRecipe recipeWrapper, IIngredients ingredients) {
		
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

		for(List<ItemStack> stacks : ingredients.getInputs(VanillaTypes.ITEM)) {

			if(stacks.get(0).getItem() instanceof ArmorItem)
				isArmorRecipe = true;

			guiItemStacks.set(i++, stacks);
		}

		for(List<FluidStack> stacks : ingredients.getInputs(VanillaTypes.FLUID)) {
			guiFluidStacks.set(i++, stacks);
		}

		i = 9;

		if(isArmorRecipe)
		{
			List<ItemStack> outputStacks = new LinkedList<ItemStack>();
			for(ItemStack stacks : ingredients.getInputs(VanillaTypes.ITEM).get(0)) {
				outputStacks.add(new ItemStack( ingredients.getOutputs(VanillaTypes.ITEM).get(0).get(0).getItem() ,1));
			}
			guiItemStacks.set(i++, outputStacks);
		}
		else
		{
			for(List<ItemStack> stacks : ingredients.getOutputs(VanillaTypes.ITEM)) {
				guiItemStacks.set(i++, stacks);
			}
		}

		for(List<FluidStack> stacks : ingredients.getOutputs(VanillaTypes.FLUID)) {
			guiFluidStacks.set(i++, stacks);
		}
	}

}
