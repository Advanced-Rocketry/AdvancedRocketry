package zmaster587.advancedRocketry.integration.jei.platePresser;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.ItemDraw;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class PlatePressCategory extends MachineCategoryTemplate<MachineRecipe> {

	public PlatePressCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, TextureResources.smallPlatePresser, icon);
	}
	
	@Override
	public ResourceLocation getUid() {
		return ARPlugin.platePresser;
	}
	
	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("block.advancedrocketry.platepress");
	}
}
