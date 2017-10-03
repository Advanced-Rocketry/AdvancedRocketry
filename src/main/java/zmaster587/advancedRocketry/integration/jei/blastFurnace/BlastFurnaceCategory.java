package zmaster587.advancedRocketry.integration.jei.blastFurnace;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import mezz.jei.api.IGuiHelper;

public class BlastFurnaceCategory extends MachineCategoryTemplate<BlastFurnaceWrapper> {

	public BlastFurnaceCategory(IGuiHelper helper) {
		super(helper, TextureResources.arcFurnaceProgressBar);
	}
	
	@Override
	public String getUid() {
		return ARPlugin.arcFurnaceUUID;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.electricArcFurnace.name");
	}

    @Override
    public String getModName()
    {
        return "Advanced Rocketry";
    }

}
