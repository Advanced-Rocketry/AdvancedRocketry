package zmaster587.advancedRocketry.integration.nei;

import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.recipe.NEI.BlastFurnaceNEI;
import zmaster587.advancedRocketry.recipe.NEI.CrystallizerNEI;
import zmaster587.advancedRocketry.recipe.NEI.CuttingMachineNEI;
import zmaster587.advancedRocketry.recipe.NEI.PrecisionAssemberNEI;

public class NEIAdvancedRocketryConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		PrecisionAssemberNEI precAss = new PrecisionAssemberNEI();
		BlastFurnaceNEI blastFurnace = new BlastFurnaceNEI();
		CrystallizerNEI crystallizer = new CrystallizerNEI();
		CuttingMachineNEI cuttingMachine = new CuttingMachineNEI();
		API.registerRecipeHandler(precAss);
		API.registerUsageHandler(precAss);
		API.registerRecipeHandler(blastFurnace);
		API.registerUsageHandler(blastFurnace);
		API.registerRecipeHandler(crystallizer);
		API.registerUsageHandler(crystallizer);
		API.registerRecipeHandler(cuttingMachine);
		API.registerUsageHandler(cuttingMachine);
		API.hideItem(new ItemStack(AdvRocketryBlocks.blockQuartzCrucible));
	}

	@Override
	public String getName() {
		return "poop";
	}

	@Override
	public String getVersion() {
		return "0.0.1";
	}
}