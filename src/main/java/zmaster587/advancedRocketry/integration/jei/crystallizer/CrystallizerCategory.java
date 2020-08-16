package zmaster587.advancedRocketry.integration.jei.crystallizer;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class CrystallizerCategory extends MachineCategoryTemplate<MachineRecipe> {

	public CrystallizerCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, TextureResources.crystallizerProgressBar, icon);
	}
	
	@Override
	public ResourceLocation getUid() {
		return ARPlugin.crystallizerUUID;
	}

	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.Crystallizer.name");
	}
}
