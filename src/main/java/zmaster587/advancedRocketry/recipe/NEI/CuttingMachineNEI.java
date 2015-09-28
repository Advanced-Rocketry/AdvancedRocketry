package zmaster587.advancedRocketry.recipe.NEI;

import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.tile.multiblock.TileCuttingMachine;

public class CuttingMachineNEI extends TemplateNEI {
	
	@Override
	public String getRecipeName() {
		return "Cutting Machine";
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	@Override
	protected Class getMachine() {
		return TileCuttingMachine.class;
	}

	@Override
	protected ProgressBarImage getProgressBar() {
		return TextureResources.cuttingMachineProgressBar;
	}
}
