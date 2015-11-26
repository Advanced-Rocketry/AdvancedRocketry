package zmaster587.advancedRocketry.integration.nei;

import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.tile.multiblock.TileElectricArcFurnace;

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
