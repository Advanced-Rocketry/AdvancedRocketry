package zmaster587.advancedRocketry.integration.jei.rollingMachine;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.ItemDraw;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.IRecipe;

public class RollingMachineCategory extends MachineCategoryTemplate<MachineRecipe> {

	public RollingMachineCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, TextureResources.rollingMachineProgressBar, icon);
	}
	
	@Override
	public ResourceLocation getUid() {
		return ARPlugin.rollingMachineUUID;
	}
	
	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("block.advancedrocketry.rollingmachine");
	}
}
