package zmaster587.advancedRocketry.integration.jei.arcFurnace;

import mezz.jei.api.IGuiHelper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

import javax.annotation.Nonnull;

public class ArcFurnaceCategory extends MachineCategoryTemplate<ArcFurnaceWrapper> {

	public ArcFurnaceCategory(IGuiHelper helper) {
		super(helper, TextureResources.arcFurnaceProgressBar);
	}
	
	@Override
	@Nonnull
	public String getUid() {
		return ARPlugin.arcFurnaceUUID;
	}

	@Override
	@Nonnull
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.electricArcFurnace.name");
	}

    @Override
	@Nonnull
    public String getModName()
    {
        return "Advanced Rocketry";
    }

}
