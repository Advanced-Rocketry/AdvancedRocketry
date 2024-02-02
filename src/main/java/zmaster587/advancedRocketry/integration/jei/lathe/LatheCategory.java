package zmaster587.advancedRocketry.integration.jei.lathe;

import mezz.jei.api.IGuiHelper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

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

    @Override
    public String getModName() {
        return "Advanced Rocketry";
    }

}
