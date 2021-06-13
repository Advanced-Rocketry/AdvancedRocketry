package zmaster587.advancedRocketry.integration.jei;

import mezz.jei.api.*;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.block.BlockSmallPlatePress;
import zmaster587.advancedRocketry.integration.jei.arcFurnace.ArcFurnaceCategory;
import zmaster587.advancedRocketry.integration.jei.arcFurnace.ArcFurnaceRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.arcFurnace.ArcFurnaceRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.centrifuge.CentrifugeCategory;
import zmaster587.advancedRocketry.integration.jei.centrifuge.CentrifugeRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.centrifuge.CentrifugeRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.chemicalReactor.ChemicalReactorCategory;
import zmaster587.advancedRocketry.integration.jei.chemicalReactor.ChemicalReactorRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.chemicalReactor.ChemicalReactorRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.crystallizer.CrystallizerCategory;
import zmaster587.advancedRocketry.integration.jei.crystallizer.CrystallizerRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.crystallizer.CrystallizerRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.electrolyser.ElectrolyzerCategory;
import zmaster587.advancedRocketry.integration.jei.electrolyser.ElectrolyzerRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.electrolyser.ElectrolyzerRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.lathe.LatheCategory;
import zmaster587.advancedRocketry.integration.jei.lathe.LatheRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.lathe.LatheRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.platePresser.PlatePressCategory;
import zmaster587.advancedRocketry.integration.jei.platePresser.PlatePressRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.platePresser.PlatePressRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.precisionAssembler.PrecisionAssemblerCategory;
import zmaster587.advancedRocketry.integration.jei.precisionAssembler.PrecisionAssemblerRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.precisionAssembler.PrecisionAssemblerRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.precisionLaserEtcher.PrecisionLaserEtcherCategory;
import zmaster587.advancedRocketry.integration.jei.precisionLaserEtcher.PrecisionLaserEtcherRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.precisionLaserEtcher.PrecisionLaserEtcherRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineCategory;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillCategory;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillRecipeMaker;
import zmaster587.advancedRocketry.tile.multiblock.machine.*;
import zmaster587.libVulpes.inventory.GuiModular;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

@JEIPlugin
public class ARPlugin implements IModPlugin {
	public static IJeiHelpers jeiHelpers;
	public static final String rollingMachineUUID = "zmaster587.AR.rollingMachine";
	public static final String latheUUID = "zmaster587.AR.lathe";
	public static final String precisionAssemblerUUID = "zmaster587.AR.precisionAssembler";
	public static final String sawMillUUID = "zmaster587.AR.sawMill";
	public static final String chemicalReactorUUID = "zmaster587.AR.chemicalReactor";
	public static final String crystallizerUUID = "zmaster587.AR.crystallizer";
	public static final String electrolyzerUUID = "zmaster587.AR.electrolyzer";
	public static final String arcFurnaceUUID = "zmaster587.AR.arcFurnace";
	public static final String platePresser = "zmaster587.AR.platePresser";
	public static final String centrifugeUUID = "zmaster587.AR.centrifuge";
	public static final String precisionLaserEngraverUUID = "zmaster587.AR.precisionlaseretcher";

	//AR machines can reload recipes. We still need this for JEI to be up-to-date
	@SuppressWarnings("deprecation")
	public static void reload() {
		jeiHelpers.reload();
	}

    @Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new RollingMachineCategory(guiHelper),
				new LatheCategory(guiHelper),
				new PrecisionAssemblerCategory(guiHelper),
				new SawMillCategory(guiHelper),
				new ChemicalReactorCategory(guiHelper),
				new CrystallizerCategory(guiHelper),
				new ElectrolyzerCategory(guiHelper),
				new ArcFurnaceCategory(guiHelper),
				new PlatePressCategory(guiHelper),
				new CentrifugeCategory(guiHelper),
				new PrecisionLaserEtcherCategory(guiHelper));
	}

	@Override
	public void register(IModRegistry registry) {
		
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
		blacklist.addIngredientToBlacklist(new ItemStack(AdvancedRocketryBlocks.blockAstroBed));
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
		
		
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockRollingMachine), rollingMachineUUID);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockLathe), latheUUID);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockPrecisionAssembler), precisionAssemblerUUID);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockCuttingMachine), sawMillUUID);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockCrystallizer), crystallizerUUID);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockElectrolyser), electrolyzerUUID);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockChemicalReactor), chemicalReactorUUID);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockArcFurnace), arcFurnaceUUID);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockPlatePress), platePresser);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockCentrifuge), centrifugeUUID);
		registry.addRecipeCatalyst(new ItemStack(AdvancedRocketryBlocks.blockPrecisionLaserEngraver), precisionLaserEngraverUUID);
	}
}
