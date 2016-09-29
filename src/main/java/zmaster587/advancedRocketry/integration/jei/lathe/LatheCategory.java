package zmaster587.advancedRocketry.integration.jei.lathe;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import mezz.jei.api.IGuiHelper;

public class LatheCategory extends MachineCategoryTemplate<LatheWrapper> {

	public LatheCategory(IGuiHelper helper) {
		super(helper, TextureResources.latheProgressBar);
	}
	
	@Override
	public String getUid() {
		return ARPlugin.latheUUID;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.lathe.name");
	}

}
