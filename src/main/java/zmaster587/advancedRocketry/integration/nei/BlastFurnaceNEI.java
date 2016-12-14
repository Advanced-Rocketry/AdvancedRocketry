package zmaster587.advancedRocketry.integration.nei;

import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.libVulpes.client.util.ProgressBarImage;

public class BlastFurnaceNEI extends TemplateNEI {
	@Override
	public String getRecipeName() {
		return "Electric Arc Furnace";
	}
	
    
	@Override
	public int recipiesPerPage() {
		return 1;
	}


	@Override
	protected Class getMachine() {
		return TileElectricArcFurnace.class;
	}


	@Override
	protected ProgressBarImage getProgressBar() {
		return TextureResources.arcFurnaceProgressBar;
	}
}
