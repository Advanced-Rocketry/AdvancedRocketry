package zmaster587.advancedRocketry.integration.jei.precisionassembler;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.client.util.ProgressBarImage;

import javax.annotation.Nonnull;

public class PrecisionAssemblerCategory extends MachineCategoryTemplate<MachineRecipe> {

	public PrecisionAssemblerCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, new ProgressBarImage(168, 41, 11, 15, 67, 42, 11, 15, Direction.DOWN, TextureResources.progressBars), icon);
	}

	@Nonnull
	@Override
	public ResourceLocation getUid() {
		return ARPlugin.precisionAssemblerUUID;
	}

	@Nonnull
	@Override
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}

	@Nonnull
	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("block.advancedrocketry.precisionassembler");
	}

}
