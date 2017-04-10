package zmaster587.advancedRocketry.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileCuttingMachine;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectricArcFurnace;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileElectrolyser;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionAssembler;
import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class RecipeHandler {
	
	private List<Class<? extends TileMultiblockMachine>> machineList = new ArrayList<Class<? extends TileMultiblockMachine>>();
	
	public void registerMachine(Class<? extends TileMultiblockMachine> clazz) {
		if(!machineList.contains(clazz))
			machineList.add(clazz);
	}

	public void clearAllMachineRecipes() {
		for(Class<? extends TileMultiblockMachine>  clazz : machineList) {
			RecipesMachine.getInstance().getRecipes(clazz).clear();
		}
	}
	
	public void registerXMLRecipes() {
		//Load XML recipes
		LibVulpes.instance.loadXMLRecipe(TileCuttingMachine.class);
		LibVulpes.instance.loadXMLRecipe(TilePrecisionAssembler.class);
		LibVulpes.instance.loadXMLRecipe(TileChemicalReactor.class);
		LibVulpes.instance.loadXMLRecipe(TileCrystallizer.class);
		LibVulpes.instance.loadXMLRecipe(TileElectrolyser.class);
		LibVulpes.instance.loadXMLRecipe(TileElectricArcFurnace.class);
		LibVulpes.instance.loadXMLRecipe(TileLathe.class);
		LibVulpes.instance.loadXMLRecipe(TileRollingMachine.class);
		LibVulpes.instance.loadXMLRecipe(BlockPress.class);
	}
	
	public void registerAllMachineRecipes() {
		
		for(Class<? extends TileMultiblockMachine>  clazz : machineList)
			try {
				clazz.newInstance().registerRecipes();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	}
	
	public void createAutoGennedRecipes(HashMap<AllowedProducts, HashSet<String>> modProducts) {
		//AutoGenned Recipes
		for(zmaster587.libVulpes.api.material.Material ore : MaterialRegistry.getAllMaterials()) {
			if(AllowedProducts.getProductByName("ORE").isOfType(ore.getAllowedProducts()) && AllowedProducts.getProductByName("INGOT").isOfType(ore.getAllowedProducts()))
				GameRegistry.addSmelting(ore.getProduct(AllowedProducts.getProductByName("ORE")), ore.getProduct(AllowedProducts.getProductByName("INGOT")), 0);

			if(AllowedProducts.getProductByName("NUGGET").isOfType(ore.getAllowedProducts())) {
				ItemStack nugget = ore.getProduct(AllowedProducts.getProductByName("NUGGET"));
				nugget.stackSize = 9;
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapelessOreRecipe(nugget, AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + str));
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("INGOT")), "ooo", "ooo", "ooo", 'o', AllowedProducts.getProductByName("NUGGET").name().toLowerCase(Locale.ENGLISH) + str));
				}
			}

			if(AllowedProducts.getProductByName("CRYSTAL").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, ore.getProduct(AllowedProducts.getProductByName("CRYSTAL")), 300, 20, AllowedProducts.getProductByName("DUST").name().toLowerCase(Locale.ENGLISH) + str);
			}

			if(AllowedProducts.getProductByName("BOULE").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(TileCrystallizer.class, ore.getProduct(AllowedProducts.getProductByName("BOULE")), 300, 20, AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + str, AllowedProducts.getProductByName("NUGGET").name().toLowerCase(Locale.ENGLISH) + str);
			}

			if(AllowedProducts.getProductByName("STICK").isOfType(ore.getAllowedProducts()) && AllowedProducts.getProductByName("INGOT").isOfType(ore.getAllowedProducts())) {
				for(String name : ore.getOreDictNames())
					if(OreDictionary.doesOreNameExist(AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + name)) {

						GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("STICK"),4), "x  ", " x ", "  x", 'x', AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + name));

						RecipesMachine.getInstance().addRecipe(TileLathe.class, ore.getProduct(AllowedProducts.getProductByName("STICK"),2), 300, 20, AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + name); //ore.getProduct(AllowedProducts.getProductByName("INGOT")));
					}
			}

			if(AllowedProducts.getProductByName("PLATE").isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames()) {
					if(OreDictionary.doesOreNameExist(AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + oreDictNames)) {
						RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, ore.getProduct(AllowedProducts.getProductByName("PLATE")), 300, 20, AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + oreDictNames);
						if(AllowedProducts.getProductByName("BLOCK").isOfType(ore.getAllowedProducts()) || ore.isVanilla())
							RecipesMachine.getInstance().addRecipe(BlockPress.class, ore.getProduct(AllowedProducts.getProductByName("PLATE"),4), 0, 0, AllowedProducts.getProductByName("BLOCK").name().toLowerCase(Locale.ENGLISH) + oreDictNames);
					}
				}
			}

			if(AllowedProducts.getProductByName("SHEET").isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames()) {
					RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, ore.getProduct(AllowedProducts.getProductByName("SHEET")), 300, 200, AllowedProducts.getProductByName("PLATE").name().toLowerCase(Locale.ENGLISH) + oreDictNames);
				}
			}

			if(AllowedProducts.getProductByName("COIL").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("COIL")), "ooo", "o o", "ooo",'o', AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + str));
			}

			if(AllowedProducts.getProductByName("FAN").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("FAN")), "p p", " r ", "p p", 'p', AllowedProducts.getProductByName("PLATE").name().toLowerCase(Locale.ENGLISH) + str, 'r', AllowedProducts.getProductByName("STICK").name().toLowerCase(Locale.ENGLISH) + str));
				}
			}
			if(AllowedProducts.getProductByName("GEAR").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("GEAR")), "sps", " r ", "sps", 'p', AllowedProducts.getProductByName("PLATE").name().toLowerCase(Locale.ENGLISH) + str, 's', AllowedProducts.getProductByName("STICK").name().toLowerCase(Locale.ENGLISH) + str, 'r', AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + str));
				}
			}
			if(AllowedProducts.getProductByName("BLOCK").isOfType(ore.getAllowedProducts())) {
				ItemStack ingot = ore.getProduct(AllowedProducts.getProductByName("INGOT"));
				ingot.stackSize = 9;
				for(String str : ore.getOreDictNames()) {
					GameRegistry.addRecipe(new ShapelessOreRecipe(ingot, AllowedProducts.getProductByName("BLOCK").name().toLowerCase(Locale.ENGLISH) + str));
					GameRegistry.addRecipe(new ShapedOreRecipe(ore.getProduct(AllowedProducts.getProductByName("BLOCK")), "ooo", "ooo", "ooo", 'o', AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + str));
				}
			}

			if(AllowedProducts.getProductByName("DUST").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames()) {
					if(AllowedProducts.getProductByName("ORE").isOfType(ore.getAllowedProducts()) || ore.isVanilla()) {
						ItemStack stack = ore.getProduct(AllowedProducts.getProductByName("DUST"));
						stack.stackSize = 2;
						RecipesMachine.getInstance().addRecipe(BlockPress.class, stack, 0, 0, AllowedProducts.getProductByName("ORE").name().toLowerCase(Locale.ENGLISH) + str);
					}
					if(AllowedProducts.getProductByName("INGOT").isOfType(ore.getAllowedProducts()) || ore.isVanilla())
						GameRegistry.addSmelting(ore.getProduct(AllowedProducts.getProductByName("DUST")), ore.getProduct(AllowedProducts.getProductByName("INGOT")), 0);
				}
			}
		}

		//Handle vanilla integration
		if(zmaster587.advancedRocketry.api.Configuration.allowSawmillVanillaWood) {
			for(int i = 0; i < 4; i++) {
				RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(Blocks.PLANKS, 6, i), 80, 10, new ItemStack(Blocks.LOG,1, i));
			}
			RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(Blocks.PLANKS, 6, 4), 80, 10, new ItemStack(Blocks.LOG2,1, 0));
			RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(Blocks.PLANKS, 6, 5), 80, 10, new ItemStack(Blocks.LOG2,1, 1));
		}

		//Handle items from other mods
		if(zmaster587.advancedRocketry.api.Configuration.allowMakingItemsForOtherMods) {
			for(Entry<AllowedProducts, HashSet<String>> entry : modProducts.entrySet()) {
				if(entry.getKey() == AllowedProducts.getProductByName("PLATE")) {
					for(String str : entry.getValue()) {
						zmaster587.libVulpes.api.material.Material material = zmaster587.libVulpes.api.material.Material.valueOfSafe(str.toUpperCase());

						if(OreDictionary.doesOreNameExist("ingot" + str) && OreDictionary.getOres("ingot" + str).size() > 0 && (material == null || !AllowedProducts.getProductByName("PLATE").isOfType(material.getAllowedProducts())) ) {

							RecipesMachine.getInstance().addRecipe(TileRollingMachine.class, OreDictionary.getOres("plate" + str).get(0), 300, 20, "ingot" + str);
						}
					}
				}
				else if(entry.getKey() == AllowedProducts.getProductByName("STICK")) {
					for(String str : entry.getValue()) {
						zmaster587.libVulpes.api.material.Material material = zmaster587.libVulpes.api.material.Material.valueOfSafe(str.toUpperCase());

						if(OreDictionary.doesOreNameExist("ingot" + str) && OreDictionary.getOres("ingot" + str).size() > 0 && (material == null || !AllowedProducts.getProductByName("STICK").isOfType(material.getAllowedProducts())) ) {

							//GT registers rods as sticks
							ItemStack stackToAdd = null;
							if(OreDictionary.doesOreNameExist("rod" + str) && OreDictionary.getOres("rod" + str).size() > 0) {
								stackToAdd = OreDictionary.getOres("rod" + str).get(0).copy();
								stackToAdd.stackSize = 2;
							}
							else if(OreDictionary.doesOreNameExist("stick" + str)  && OreDictionary.getOres("stick" + str).size() > 0) {
								stackToAdd = OreDictionary.getOres("stick" + str).get(0).copy();
								stackToAdd.stackSize = 2;
								}
							else 
								continue;

							RecipesMachine.getInstance().addRecipe(TileLathe.class, stackToAdd, 300, 20, "ingot" + str);
						}
					}
				}
			}
		}
	}
}
