package zmaster587.advancedRocketry.integration.jei.chemicalReactor;

import zmaster587.advancedRocketry.integration.jei.ARPlugin;
import zmaster587.advancedRocketry.integration.jei.MachineCategoryTemplate;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import mezz.jei.api.IGuiHelper;

public class ChemicalReactorCategory extends MachineCategoryTemplate<ChemicalReactorlWrapper> {

	public ChemicalReactorCategory(IGuiHelper helper) {
		super(helper, TextureResources.crystallizerProgressBar);
	}
	
	@Override
	public String getUid() {
		return ARPlugin.chemicalReactorUUID;
	}

	@Override
	public String getTitle() {
		return LibVulpes.proxy.getLocalizedString("tile.chemreactor.name");
	}

    @Override
    public String getModName()
    {
        return "Advanced Rocketry";
    }

}
