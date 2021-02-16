package zmaster587.advancedRocketry.integration.jei;

import mezz.jei.api.*;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.integration.jei.blastFurnace.BlastFurnaceCategory;
import zmaster587.advancedRocketry.integration.jei.blastFurnace.BlastFurnaceRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.blastFurnace.BlastFurnaceRecipeMaker;
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
import zmaster587.advancedRocketry.integration.jei.precisionLaserEngraver.lathe.PrecisionLaserEngraverCategory;
import zmaster587.advancedRocketry.integration.jei.precisionLaserEngraver.lathe.PrecisionLaserEngraverRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.precisionLaserEngraver.lathe.PrecisionLaserEngraverRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineCategory;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillCategory;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillRecipeMaker;
import zmaster587.advancedRocketry.tile.multiblock.machine.*;
import zmaster587.libVulpes.inventory.GuiModular;

import java.awt.*;
import java.util.List;

@JEIPlugin
public class ARPlugin extends BlankModPlugin {
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
	public static final String precisionLaserEngraverUUID = "zmaster587.AR.precisionlaserengraver";
	
	public static void reload() {
		jeiHelpers.reload();
	}
	
	@Override
	public void register(IModRegistry registry) {
		jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		
		registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler<GuiModular>() {
			@Override
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
		
		registry.addRecipeCategories(new RollingMachineCategory(guiHelper),
		new LatheCategory(guiHelper),
		new PrecisionAssemblerCategory(guiHelper),
		new SawMillCategory(guiHelper),
		new ChemicalReactorCategory(guiHelper),
		new CrystallizerCategory(guiHelper),
		new ElectrolyzerCategory(guiHelper),
		new BlastFurnaceCategory(guiHelper),
		new PlatePressCategory(guiHelper),
		new CentrifugeCategory(guiHelper),
		new PrecisionLaserEngraverCategory(guiHelper));
		
		registry.addRecipeHandlers(new RollingMachineRecipeHandler(),
		new LatheRecipeHandler(),
		new PrecisionAssemblerRecipeHandler(),
		new SawMillRecipeHandler(),
		new ChemicalReactorRecipeHandler(),
		new CrystallizerRecipeHandler(),
		new ElectrolyzerRecipeHandler(),
		new BlastFurnaceRecipeHandler(),
		new PlatePressRecipeHandler(),
		new CentrifugeRecipeHandler(),
		new PrecisionLaserEngraverRecipeHandler());
		
		registry.addRecipes(RollingMachineRecipeMaker.getMachineRecipes(jeiHelpers, TileRollingMachine.class));
		registry.addRecipes(LatheRecipeMaker.getMachineRecipes(jeiHelpers, TileLathe.class));
		registry.addRecipes(PrecisionAssemblerRecipeMaker.getMachineRecipes(jeiHelpers, TilePrecisionAssembler.class));
		registry.addRecipes(SawMillRecipeMaker.getMachineRecipes(jeiHelpers, TileCuttingMachine.class));
		registry.addRecipes(CrystallizerRecipeMaker.getMachineRecipes(jeiHelpers, TileCrystallizer.class));
		registry.addRecipes(BlastFurnaceRecipeMaker.getMachineRecipes(jeiHelpers, TileElectricArcFurnace.class));
		registry.addRecipes(PlatePressRecipeMaker.getMachineRecipes(jeiHelpers, BlockPress.class));
		registry.addRecipes(ElectrolyzerRecipeMaker.getMachineRecipes(jeiHelpers, TileElectrolyser.class));
		registry.addRecipes(ChemicalReactorRecipeMaker.getMachineRecipes(jeiHelpers, TileChemicalReactor.class));
		registry.addRecipes(CentrifugeRecipeMaker.getMachineRecipes(jeiHelpers, TileCentrifuge.class));
		registry.addRecipes(PrecisionLaserEngraverRecipeMaker.getMachineRecipes(jeiHelpers, TilePrecisionLaserEngraver.class));
		
		
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockRollingMachine), rollingMachineUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockLathe), latheUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockPrecisionAssembler), precisionAssemblerUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockCuttingMachine), sawMillUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockCrystallizer), crystallizerUUID);		
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockElectrolyser), electrolyzerUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockChemicalReactor), chemicalReactorUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockArcFurnace), arcFurnaceUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockPlatePress), platePresser);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockCentrifuge), centrifugeUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockPrecisionLaserEngraver), precisionLaserEngraverUUID);
	}
}
