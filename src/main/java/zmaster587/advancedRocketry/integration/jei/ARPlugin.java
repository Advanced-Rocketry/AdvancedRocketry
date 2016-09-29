package zmaster587.advancedRocketry.integration.jei;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.integration.jei.blastFurnace.BlastFurnaceCategory;
import zmaster587.advancedRocketry.integration.jei.blastFurnace.BlastFurnaceRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.blastFurnace.BlastFurnaceRecipeMaker;
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
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineCategory;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.rollingMachine.RollingMachineRecipeMaker;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillCategory;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillRecipeHandler;
import zmaster587.advancedRocketry.integration.jei.sawmill.SawMillRecipeMaker;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class ARPlugin extends BlankModPlugin {
	public static IJeiHelpers jeiHelpers;
	public static final String rollingMachineUUID = "rollingMachine";
	public static final String latheUUID = "lathe";
	public static final String precisionAssemblerUUID = "precisionAssembler";
	public static final String sawMillUUID = "sawMill";
	public static final String chemicalReactorUUID = "chemicalReactor";
	public static final String crystallizerUUID = "crystallizer";
	public static final String electrolyzerUUID = "electrolyzer";
	public static final String arcFurnaceUUID = "arcFurnace";
	public static final String platePresser = "platePresser";
	
	@Override
	public void register(IModRegistry registry) {
		jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		
		registry.addRecipeCategories(new RollingMachineCategory(guiHelper),
		new LatheCategory(guiHelper),
		new PrecisionAssemblerCategory(guiHelper),
		new SawMillCategory(guiHelper),
		new ChemicalReactorCategory(guiHelper),
		new CrystallizerCategory(guiHelper),
		new ElectrolyzerCategory(guiHelper),
		new BlastFurnaceCategory(guiHelper),
		new PlatePressCategory(guiHelper));
		
		registry.addRecipeHandlers(new RollingMachineRecipeHandler(),
		new LatheRecipeHandler(),
		new PrecisionAssemblerRecipeHandler(),
		new SawMillRecipeHandler(),
		new ChemicalReactorRecipeHandler(),
		new CrystallizerRecipeHandler(),
		new ElectrolyzerRecipeHandler(),
		new BlastFurnaceRecipeHandler(),
		new PlatePressRecipeHandler());
		
		registry.addRecipes(RollingMachineRecipeMaker.getMachineRecipes(jeiHelpers, TileRollingMachine.class));
		registry.addRecipes(LatheRecipeMaker.getMachineRecipes(jeiHelpers, TileLathe.class));
		registry.addRecipes(PrecisionAssemblerRecipeMaker.getMachineRecipes(jeiHelpers, TilePrecisionAssembler.class));
		registry.addRecipes(SawMillRecipeMaker.getMachineRecipes(jeiHelpers, TileCuttingMachine.class));
		registry.addRecipes(CrystallizerRecipeMaker.getMachineRecipes(jeiHelpers, TileCrystallizer.class));
		registry.addRecipes(BlastFurnaceRecipeMaker.getMachineRecipes(jeiHelpers, TileElectricArcFurnace.class));
		registry.addRecipes(PlatePressRecipeMaker.getMachineRecipes(jeiHelpers, BlockPress.class));
		registry.addRecipes(ElectrolyzerRecipeMaker.getMachineRecipes(jeiHelpers, TileElectrolyser.class));
		registry.addRecipes(ChemicalReactorRecipeMaker.getMachineRecipes(jeiHelpers, TileChemicalReactor.class));
		
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockRollingMachine), rollingMachineUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockLathe), latheUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockPrecisionAssembler), precisionAssemblerUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockCuttingMachine), sawMillUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockCrystallizer), crystallizerUUID);		
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockElectrolyser), electrolyzerUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockChemicalReactor), chemicalReactorUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockArcFurnace), arcFurnaceUUID);
		registry.addRecipeCategoryCraftingItem(new ItemStack(AdvancedRocketryBlocks.blockPlatePress), platePresser);
	}
}
