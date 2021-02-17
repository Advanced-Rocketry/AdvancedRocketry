package zmaster587.advancedRocketry.integration.jei.precisionLaserEtcher;

import mezz.jei.api.IGuiHelper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class PrecisionLaserEtcherCategory extends MachineCategoryTemplate<PrecisionLaserEtcherWrapper> {

	public PrecisionLaserEtcherCategory(IGuiHelper helper) {
		super(helper, TextureResources.latheProgressBar);
	}
	
	@Override
	public String getUid() {
		return ARPlugin.precisionLaserEngraverUUID;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.precisionlaserengraver.name");
	}

    @Override
    public String getModName()
    {
        return "Advanced Rocketry";
    }

}
