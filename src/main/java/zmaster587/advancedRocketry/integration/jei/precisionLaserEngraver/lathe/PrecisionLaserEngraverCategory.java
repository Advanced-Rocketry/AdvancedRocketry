package zmaster587.advancedRocketry.integration.jei.precisionLaserEngraver.lathe;

import mezz.jei.api.IGuiHelper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class PrecisionLaserEngraverCategory extends MachineCategoryTemplate<PrecisionLaserEngraverWrapper> {

	public PrecisionLaserEngraverCategory(IGuiHelper helper) {
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
