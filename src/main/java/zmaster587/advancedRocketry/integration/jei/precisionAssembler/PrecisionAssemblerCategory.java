package zmaster587.advancedRocketry.integration.jei.precisionAssembler;

import mezz.jei.api.IGuiHelper;
import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.client.util.ProgressBarImage;

public class PrecisionAssemblerCategory extends MachineCategoryTemplate<PrecisionAssemblerWrapper> {

    public PrecisionAssemblerCategory(IGuiHelper helper) {
        super(helper, new ProgressBarImage(168, 41, 11, 15, 67, 42, 11, 15, EnumFacing.DOWN, TextureResources.progressBars));
    }

    @Override
    public String getUid() {
        return ARPlugin.precisionAssemblerUUID;
    }

    @Override
    public String getTitle() {
        return LibVulpes.proxy.getLocalizedString("tile.precisionAssemblingMachine.name");
    }

    @Override
    public String getModName() {
        return "Advanced Rocketry";
    }

}
