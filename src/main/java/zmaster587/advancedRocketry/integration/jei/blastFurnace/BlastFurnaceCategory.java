package zmaster587.advancedRocketry.integration.jei.blastFurnace;

import mezz.jei.api.IGuiHelper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

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
