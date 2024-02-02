package zmaster587.advancedRocketry.integration.jei.centrifuge;

import mezz.jei.api.IGuiHelper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class CentrifugeCategory extends MachineCategoryTemplate<CentrifugeWrapper> {

    public CentrifugeCategory(IGuiHelper helper) {
        super(helper, TextureResources.crystallizerProgressBar);
    }

    @Override
    public String getUid() {
        return ARPlugin.centrifugeUUID;
    }

    @Override
    public String getTitle() {
        return LibVulpes.proxy.getLocalizedString("tile.centrifuge.name");
    }

    @Override
    public String getModName() {
        return "Advanced Rocketry";
    }

}
