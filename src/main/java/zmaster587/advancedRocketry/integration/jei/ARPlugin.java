package zmaster587.advancedRocketry.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.block.multiblock.BlockSmallPlatePress;
import zmaster587.advancedRocketry.integration.jei.arcFurnace.ArcFurnaceCategory;
import zmaster587.advancedRocketry.integration.jei.arcFurnace.ArcFurnaceRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.centrifuge.CentrifugeCategory;
import zmaster587.advancedRocketry.integration.jei.centrifuge.CentrifugeRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.chemicalReactor.ChemicalReactorCategory;
import zmaster587.advancedRocketry.integration.jei.chemicalReactor.ChemicalReactorRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.crystallizer.CrystallizerCategory;
import zmaster587.advancedRocketry.integration.jei.crystallizer.CrystallizerRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.electrolyzer.ElectrolyzerCategory;
import zmaster587.advancedRocketry.integration.jei.electrolyzer.ElectrolyzerRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.lathe.LatheCategory;
import zmaster587.advancedRocketry.integration.jei.lathe.LatheRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.smallplatepress.PlatePressCategory;
import zmaster587.advancedRocketry.integration.jei.smallplatepress.PlatePressRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.precisionassembler.PrecisionAssemblerCategory;
import zmaster587.advancedRocketry.integration.jei.precisionassembler.PrecisionAssemblerRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.precisionlaseretcher.PrecisionLaserEtcherCategory;
import zmaster587.advancedRocketry.integration.jei.precisionlaseretcher.PrecisionLaserEtcherRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.rollingmachine.RollingMachineCategory;
import zmaster587.advancedRocketry.integration.jei.rollingmachine.RollingMachineRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.cuttingmachine.CuttingMachineCategory;
import zmaster587.advancedRocketry.integration.jei.cuttingmachine.CuttingMachineRecipeMaker;
import zmaster587.advancedRocketry.tile.multiblock.machine.*;

import javax.annotation.Nonnull;

@JeiPlugin
public class ARPlugin implements IModPlugin {
	public static IJeiHelpers jeiHelpers;
	public static final ResourceLocation rollingMachineUUID = new ResourceLocation(Constants.modId, "zmaster587.ar.rollingmachine");
	public static final ResourceLocation latheUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.lathe");
	public static final ResourceLocation precisionAssemblerUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.precisionassembler");
	public static final ResourceLocation cuttingMachineUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.cuttingmachine");
	public static final ResourceLocation chemicalReactorUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.chemicalreactor");
	public static final ResourceLocation crystallizerUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.crystallizer");
	public static final ResourceLocation electrolyzerUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.electrolyzer");
	public static final ResourceLocation arcFurnaceUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.arcfurnace");
	public static final ResourceLocation smallPlatePressUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.smallplatepresser");
	public static final ResourceLocation centrifugeUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.centrifuge");
	public static final ResourceLocation precisionLaserEtcherUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.precisionlaseretcher");
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {

		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new RollingMachineCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockRollingMachine)),
		new LatheCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockLathe)),
		new PrecisionAssemblerCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockPrecisionAssembler)),
		new CuttingMachineCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockCuttingMachine)),
		new ChemicalReactorCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockChemicalReactor)),
		new CrystallizerCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockCrystallizer)),
		new ElectrolyzerCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockElectrolyzer)),
		new ArcFurnaceCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockArcFurnace)),
		new PlatePressCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockSmallPlatePress)),
		new CentrifugeCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockCentrifuge)),
		new PrecisionLaserEtcherCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockPrecisionLaserEtcher)));
	}
	
	
	/**
	 * Register modded recipes.
	 */
	public void registerRecipes(IRecipeRegistration registry) {
		registry.addRecipes(RollingMachineRecipeMaker.getMachineRecipes(jeiHelpers, TileRollingMachine.class), rollingMachineUUID);
		registry.addRecipes(LatheRecipeMaker.getMachineRecipes(jeiHelpers, TileLathe.class), latheUUID);
		registry.addRecipes(PrecisionAssemblerRecipeMaker.getMachineRecipes(jeiHelpers, TilePrecisionAssembler.class), precisionAssemblerUUID);
		registry.addRecipes(CuttingMachineRecipeMaker.getMachineRecipes(jeiHelpers, TileCuttingMachine.class), cuttingMachineUUID);
		registry.addRecipes(CrystallizerRecipeMaker.getMachineRecipes(jeiHelpers, TileCrystallizer.class), crystallizerUUID);
		registry.addRecipes(ArcFurnaceRecipeMaker.getMachineRecipes(jeiHelpers, TileElectricArcFurnace.class), arcFurnaceUUID);
		registry.addRecipes(PlatePressRecipeMaker.getMachineRecipes(jeiHelpers, BlockSmallPlatePress.class), smallPlatePressUUID);
		registry.addRecipes(ElectrolyzerRecipeMaker.getMachineRecipes(jeiHelpers, TileElectrolyser.class), electrolyzerUUID);
		registry.addRecipes(ChemicalReactorRecipeMaker.getMachineRecipes(jeiHelpers, TileChemicalReactor.class), chemicalReactorUUID);
		registry.addRecipes(CentrifugeRecipeMaker.getMachineRecipes(jeiHelpers, TileCentrifuge.class), centrifugeUUID);
		registry.addRecipes(PrecisionLaserEtcherRecipeMaker.getMachineRecipes(jeiHelpers, TilePrecisionLaserEtcher.class), precisionLaserEtcherUUID);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockRollingMachine), rollingMachineUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockLathe), latheUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockPrecisionAssembler), precisionAssemblerUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockCuttingMachine), cuttingMachineUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockChemicalReactor), chemicalReactorUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockCrystallizer), crystallizerUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockElectrolyzer), electrolyzerUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockArcFurnace), arcFurnaceUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockSmallPlatePress), smallPlatePressUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockCentrifuge), centrifugeUUID);
		registration.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockPrecisionLaserEtcher), precisionLaserEtcherUUID);
	}

	@Override
	@Nonnull
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Constants.modId, "jeiplugin");
	}
}
