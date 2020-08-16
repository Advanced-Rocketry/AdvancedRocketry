package zmaster587.advancedRocketry.integration.jei.blastFurnace;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class BlastFurnaceCategory extends MachineCategoryTemplate<MachineRecipe> {

	public BlastFurnaceCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, TextureResources.arcFurnaceProgressBar, icon);
	}
	
	@Override
	public ResourceLocation getUid() {
		return ARPlugin.arcFurnaceUUID;
	}

	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}
	
	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.electricArcFurnace.name");
	}
}
