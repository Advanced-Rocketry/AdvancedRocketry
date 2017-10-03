package zmaster587.advancedRocketry.integration.jei.platePresser;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import mezz.jei.api.IGuiHelper;

public class PlatePressCategory extends MachineCategoryTemplate<PlatePressWrapper> {

	public PlatePressCategory(IGuiHelper helper) {
		super(helper, TextureResources.smallPlatePresser);
	}
	
	@Override
	public String getUid() {
		return ARPlugin.platePresser;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.blockHandPress.name");
	}

    @Override
    public String getModName()
    {
        return "Advanced Rocketry";
    }

}
