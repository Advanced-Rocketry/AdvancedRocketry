package zmaster587.advancedRocketry.integration.jei.sawmill;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import mezz.jei.api.IGuiHelper;

public class SawMillCategory extends MachineCategoryTemplate<SawMillWrapper> {

	public SawMillCategory(IGuiHelper helper) {
		super(helper, TextureResources.cuttingMachineProgressBar);
	}
	
	@Override
	public String getUid() {
		return ARPlugin.sawMillUUID;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.cuttingMachine.name");
	}

}
