package zmaster587.advancedRocketry.integration.jei.precisionLaserEtcher;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class PrecisionLaserEtcherCategory extends MachineCategoryTemplate<MachineRecipe> {

	public PrecisionLaserEtcherCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, TextureResources.latheProgressBar, icon);
	}
	
	@Override
	public ResourceLocation getUid() {
		return ARPlugin.precisionLaserEngraverUUID;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.precisionlaseretcher.name");
	}

	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}

}
