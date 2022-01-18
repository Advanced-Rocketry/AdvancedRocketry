package zmaster587.advancedRocketry.integration.jei.cuttingmachine;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

import javax.annotation.Nonnull;

public class CuttingMachineCategory extends MachineCategoryTemplate<MachineRecipe> {

	public CuttingMachineCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, TextureResources.cuttingMachineProgressBar, icon);
	}
	
	@Nonnull
	@Override
	public ResourceLocation getUid() {
		return ARPlugin.cuttingMachineUUID;
	}
	
	@Nonnull
	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}

	
	@Nonnull
	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("block.advancedrocketry.cuttingmachine");
	}

}
