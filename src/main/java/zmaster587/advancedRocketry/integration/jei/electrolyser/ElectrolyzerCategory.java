package zmaster587.advancedRocketry.integration.jei.electrolyser;

import mezz.jei.api.IGuiHelper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class ElectrolyzerCategory extends MachineCategoryTemplate<ElectrolyzerWrapper> {

    public ElectrolyzerCategory(IGuiHelper helper) {
        super(helper, TextureResources.crystallizerProgressBar);
    }

    @Override
    public String getUid() {
        return ARPlugin.electrolyzerUUID;
    }

    @Override
    public String getTitle() {
        return LibVulpes.proxy.getLocalizedString("tile.electrolyser.name");
    }

    @Override
    public String getModName() {
        return "Advanced Rocketry";
    }

}
