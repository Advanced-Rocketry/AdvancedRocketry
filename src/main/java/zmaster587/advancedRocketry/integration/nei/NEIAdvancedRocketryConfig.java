package zmaster587.advancedRocketry.integration.nei;

import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;

public class NEIAdvancedRocketryConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		TemplateNEI precAss = new PrecisionAssemberNEI();
		TemplateNEI blastFurnace = new BlastFurnaceNEI();
		TemplateNEI crystallizer = new CrystallizerNEI();
		TemplateNEI cuttingMachine = new CuttingMachineNEI();
		TemplateNEI lathe = new LatheNEI();
		TemplateNEI rollingMachine = new RollingMachineNEI();
		TemplateNEI smallPlatePress = new PlatePresserNEI();
		TemplateNEI electrolyser = new ElectrolyserNEI();
		TemplateNEI chemReactor = new ChemicalReactorNEI();
		
		API.registerRecipeHandler(lathe);
		API.registerUsageHandler(lathe);
		API.registerRecipeHandler(precAss);
		API.registerUsageHandler(precAss);
		API.registerRecipeHandler(blastFurnace);
		API.registerUsageHandler(blastFurnace);
		API.registerRecipeHandler(crystallizer);
		API.registerUsageHandler(crystallizer);
		API.registerRecipeHandler(cuttingMachine);
		API.registerUsageHandler(cuttingMachine);
		API.registerRecipeHandler(rollingMachine);
		API.registerUsageHandler(rollingMachine);
		API.registerRecipeHandler(smallPlatePress);
		API.registerUsageHandler(smallPlatePress);
		API.registerRecipeHandler(electrolyser);
		API.registerUsageHandler(electrolyser);
		API.registerRecipeHandler(chemReactor);
		API.registerUsageHandler(chemReactor);
		
		API.hideItem(new ItemStack(AdvancedRocketryBlocks.blockQuartzCrucible));
		API.hideItem(new ItemStack(AdvancedRocketryItems.itemSatellite));
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