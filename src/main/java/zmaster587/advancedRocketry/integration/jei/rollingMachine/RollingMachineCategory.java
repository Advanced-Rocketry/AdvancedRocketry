package zmaster587.advancedRocketry.integration.jei.rollingMachine;

import mezz.jei.api.IGuiHelper;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;

public class RollingMachineCategory extends MachineCategoryTemplate<RollingMachineWrapper> {

    public RollingMachineCategory(IGuiHelper helper) {
        super(helper, TextureResources.rollingMachineProgressBar);
    }

    @Override
    public String getUid() {
        return ARPlugin.rollingMachineUUID;
    }

    @Override
    public String getTitle() {
        return LibVulpes.proxy.getLocalizedString("tile.rollingMachine.name");
    }

    @Override
    public String getModName() {
        return "Advanced Rocketry";
    }

}
