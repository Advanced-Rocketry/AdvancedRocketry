package zmaster587.advancedRocketry.integration.jei.crystallizer;

import mezz.jei.api.IGuiHelper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class CrystallizerCategory extends MachineCategoryTemplate<CrystallizerWrapper> {

    public CrystallizerCategory(IGuiHelper helper) {
        super(helper, TextureResources.crystallizerProgressBar);
    }

    @Override
    public String getUid() {
        return ARPlugin.crystallizerUUID;
    }

    @Override
    public String getTitle() {
        return LibVulpes.proxy.getLocalizedString("tile.Crystallizer.name");
    }

    @Override
    public String getModName() {
        return "Advanced Rocketry";
    }

}
