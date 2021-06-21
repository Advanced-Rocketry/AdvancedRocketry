package zmaster587.advancedRocketry.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
<<<<<<< HEAD
import net.minecraft.util.ResourceLocation;
=======
>>>>>>> origin/feature/nuclearthermalrockets
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.block.BlockSmallPlatePress;
import zmaster587.advancedRocketry.integration.jei.arcFurnace.ArcFurnaceCategory;
import zmaster587.advancedRocketry.integration.jei.arcFurnace.ArcFurnaceRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.centrifuge.CentrifugeCategory;
import zmaster587.advancedRocketry.integration.jei.centrifuge.CentrifugeRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.chemicalReactor.ChemicalReactorCategory;
import zmaster587.advancedRocketry.integration.jei.chemicalReactor.ChemicalReactorRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.crystallizer.CrystallizerCategory;
import zmaster587.advancedRocketry.integration.jei.crystallizer.CrystallizerRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.electrolyser.ElectrolyzerCategory;
import zmaster587.advancedRocketry.integration.jei.electrolyser.ElectrolyzerRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.lathe.LatheCategory;
import zmaster587.advancedRocketry.integration.jei.lathe.LatheRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.platePresser.PlatePressCategory;
import zmaster587.advancedRocketry.integration.jei.platePresser.PlatePressRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.precisionAssembler.PrecisionAssemblerCategory;
import zmaster587.advancedRocketry.integration.jei.precisionAssembler.PrecisionAssemblerRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.precisionLaserEtcher.PrecisionLaserEtcherCategory;
import zmaster587.advancedRocketry.integration.jei.precisionLaserEtcher.PrecisionLaserEtcherRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineCategory;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillCategory;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillRecipeMaker;
import zmaster587.advancedRocketry.tile.multiblock.machine.*;
<<<<<<< HEAD

@JeiPlugin
=======
import zmaster587.libVulpes.inventory.GuiModular;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

@JEIPlugin
>>>>>>> origin/feature/nuclearthermalrockets
public class ARPlugin implements IModPlugin {
	public static IJeiHelpers jeiHelpers;
	public static final ResourceLocation rollingMachineUUID = new ResourceLocation(Constants.modId, "zmaster587.ar.rollingmachine");
	public static final ResourceLocation latheUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.lathe");
	public static final ResourceLocation precisionAssemblerUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.precisionassembler");
	public static final ResourceLocation sawMillUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.sawmill");
	public static final ResourceLocation chemicalReactorUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.chemicalreactor");
	public static final ResourceLocation crystallizerUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.crystallizer");
	public static final ResourceLocation electrolyzerUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.electrolyzer");
	public static final ResourceLocation arcFurnaceUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.arcfurnace");
	public static final ResourceLocation platePresser =  new ResourceLocation(Constants.modId, "zmaster587.ar.platepresser");
	public static final ResourceLocation centrifugeUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.centrifuge");
	public static final ResourceLocation precisionLaserEngraverUUID =  new ResourceLocation(Constants.modId, "zmaster587.ar.precisionlasterengraver");
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		
<<<<<<< HEAD
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
		registry.addRecipeCategories(new RollingMachineCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockRollingMachine)),
		new LatheCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockLathe)),
		new PrecisionAssemblerCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockPrecisionAssembler)),
		new SawMillCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockSawBlade)),
		new ChemicalReactorCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockChemicalReactor)),
		new CrystallizerCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockCrystallizer)),
		new ElectrolyzerCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockElectrolyser)),
		new ArcFurnaceCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockArcFurnace)),
		new PlatePressCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockPlatePress)),
		new CentrifugeCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockCentrifuge)),
		new PrecisionLaserEtcherCategory(guiHelper, new ItemStack(AdvancedRocketryBlocks.blockPrecisionLaserEtcher)));
	}
	
	
	/**
	 * Register modded recipes.
	 */
	public void registerRecipes(IRecipeRegistration registry) {
=======
		registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler<GuiModular>() {
			@Override
			@Nonnull
			public Class<GuiModular> getGuiContainerClass() {
				return GuiModular.class;
			}

			@Override
			public List<Rectangle> getGuiExtraAreas(GuiModular guiContainer) {
				return guiContainer.getExtraAreasCovered();
			}

			@Override
			public Object getIngredientUnderMouse(GuiModular guiContainer,
					int mouseX, int mouseY) {
				return null;
			}
		});

		IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
		//Hide problematic blocks
		blacklist.addIngredientToBlacklist(new ItemStack(AdvancedRocketryBlocks.blockForceField));
		blacklist.addIngredientToBlacklist(new ItemStack(AdvancedRocketryBlocks.blockLightSource));
		blacklist.addIngredientToBlacklist(new ItemStack(AdvancedRocketryBlocks.blockAirLock));
		//Hide problematic items
		blacklist.addIngredientToBlacklist(new ItemStack(AdvancedRocketryItems.itemSpaceStation));



		registry.addRecipeHandlers(new RollingMachineRecipeHandler(),
		new LatheRecipeHandler(),
		new PrecisionAssemblerRecipeHandler(),
		new SawMillRecipeHandler(),
		new ChemicalReactorRecipeHandler(),
		new CrystallizerRecipeHandler(),
		new ElectrolyzerRecipeHandler(),
		new ArcFurnaceRecipeHandler(),
		new PlatePressRecipeHandler(),
		new CentrifugeRecipeHandler(),
		new PrecisionLaserEtcherRecipeHandler());

>>>>>>> origin/feature/nuclearthermalrockets
		registry.addRecipes(RollingMachineRecipeMaker.getMachineRecipes(jeiHelpers, TileRollingMachine.class), rollingMachineUUID);
		registry.addRecipes(LatheRecipeMaker.getMachineRecipes(jeiHelpers, TileLathe.class), latheUUID);
		registry.addRecipes(PrecisionAssemblerRecipeMaker.getMachineRecipes(jeiHelpers, TilePrecisionAssembler.class), precisionAssemblerUUID);
		registry.addRecipes(SawMillRecipeMaker.getMachineRecipes(jeiHelpers, TileCuttingMachine.class), sawMillUUID);
		registry.addRecipes(CrystallizerRecipeMaker.getMachineRecipes(jeiHelpers, TileCrystallizer.class), crystallizerUUID);
		registry.addRecipes(ArcFurnaceRecipeMaker.getMachineRecipes(jeiHelpers, TileElectricArcFurnace.class), arcFurnaceUUID);
		registry.addRecipes(PlatePressRecipeMaker.getMachineRecipes(jeiHelpers, BlockSmallPlatePress.class), platePresser);
		registry.addRecipes(ElectrolyzerRecipeMaker.getMachineRecipes(jeiHelpers, TileElectrolyser.class), electrolyzerUUID);
		registry.addRecipes(ChemicalReactorRecipeMaker.getMachineRecipes(jeiHelpers, TileChemicalReactor.class), chemicalReactorUUID);
		registry.addRecipes(CentrifugeRecipeMaker.getMachineRecipes(jeiHelpers, TileCentrifuge.class), centrifugeUUID);
		registry.addRecipes(PrecisionLaserEtcherRecipeMaker.getMachineRecipes(jeiHelpers, TilePrecisionLaserEtcher.class), precisionLaserEngraverUUID);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Constants.modId, "jeiplugin");
	}
}
