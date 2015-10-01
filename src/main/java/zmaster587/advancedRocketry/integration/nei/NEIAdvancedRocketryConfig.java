package zmaster587.advancedRocketry.integration.nei;

import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.recipe.NEI.BlastFurnaceNEI;
import zmaster587.advancedRocketry.recipe.NEI.CrystallizerNEI;
import zmaster587.advancedRocketry.recipe.NEI.CuttingMachineNEI;
import zmaster587.advancedRocketry.recipe.NEI.LatheNEI;
import zmaster587.advancedRocketry.recipe.NEI.PrecisionAssemberNEI;
import zmaster587.advancedRocketry.recipe.NEI.RollingMachineNEI;
import zmaster587.advancedRocketry.recipe.NEI.TemplateNEI;

public class NEIAdvancedRocketryConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		TemplateNEI precAss = new PrecisionAssemberNEI();
		TemplateNEI blastFurnace = new BlastFurnaceNEI();
		TemplateNEI crystallizer = new CrystallizerNEI();
		TemplateNEI cuttingMachine = new CuttingMachineNEI();
		TemplateNEI lathe = new LatheNEI();
		TemplateNEI rollingMachine = new RollingMachineNEI();
		
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
		
		API.hideItem(new ItemStack(AdvRocketryBlocks.blockQuartzCrucible));
		API.hideItem(new ItemStack(AdvRocketryBlocks.blockPlaceHolder));
		API.hideItem(new ItemStack(AdvRocketryBlocks.blockControllerDummy));
		API.hideItem(new ItemStack(AdvRocketryBlocks.blockPhantom));
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