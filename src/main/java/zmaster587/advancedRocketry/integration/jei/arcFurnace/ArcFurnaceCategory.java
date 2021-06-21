package zmaster587.advancedRocketry.integration.jei.arcFurnace;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.ItemDraw;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.integration.jei.MachineRecipe;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

<<<<<<< HEAD
public class ArcFurnaceCategory extends MachineCategoryTemplate<MachineRecipe> {
=======
import javax.annotation.Nonnull;

public class ArcFurnaceCategory extends MachineCategoryTemplate<ArcFurnaceWrapper> {
>>>>>>> origin/feature/nuclearthermalrockets

	public ArcFurnaceCategory(IGuiHelper helper, ItemStack icon) {
		super(helper, TextureResources.arcFurnaceProgressBar, icon);
	}
	
	@Override
<<<<<<< HEAD
	public ResourceLocation getUid() {
=======
	@Nonnull
	public String getUid() {
>>>>>>> origin/feature/nuclearthermalrockets
		return ARPlugin.arcFurnaceUUID;
	}

	@Override
<<<<<<< HEAD
	public Class<? extends MachineRecipe> getRecipeClass() {
		return MachineRecipe.class;
	}
	
	@Override
=======
	@Nonnull
>>>>>>> origin/feature/nuclearthermalrockets
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("block.advancedrocketry.arcfurnace");
	}
<<<<<<< HEAD
=======

    @Override
	@Nonnull
    public String getModName()
    {
        return "Advanced Rocketry";
    }

>>>>>>> origin/feature/nuclearthermalrockets
}
