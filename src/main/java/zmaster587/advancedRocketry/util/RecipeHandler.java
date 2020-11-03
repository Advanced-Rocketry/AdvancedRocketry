package zmaster587.advancedRocketry.util;

import net.minecraft.block.Blocks;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.loot.functions.Smelt;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.block.BlockPress;
import zmaster587.advancedRocketry.recipe.RecipeCrystallizer;
import zmaster587.advancedRocketry.recipe.RecipeLathe;
import zmaster587.advancedRocketry.recipe.RecipeRollingMachine;
import zmaster587.advancedRocketry.recipe.RecipeSmallPresser;
import zmaster587.advancedRocketry.tile.multiblock.machine.*;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.*;
import java.util.Map.Entry;

import com.google.gson.JsonObject;

public class RecipeHandler {
	
	private List<Class<?>> machineList = new ArrayList<Class<?>>();
	
	public void registerMachine(Class<?> clazz) {
		if(!machineList.contains(clazz))
		{
			machineList.add(clazz);
			RecipesMachine.getInstance().recipeList.put(clazz, new LinkedList<IRecipe>());
		}
		
	}

	public void clearAllMachineRecipes() {
		for(Class<?>  clazz : machineList) {
			RecipesMachine.getInstance().getRecipes(clazz).clear();
		}
	}
	
	public void registerXMLRecipes() {
		//Load XML recipes
		/*LibVulpes.instance.loadXMLRecipe(TileCuttingMachine.class);
		LibVulpes.instance.loadXMLRecipe(TilePrecisionAssembler.class);
		LibVulpes.instance.loadXMLRecipe(TileChemicalReactor.class);
		LibVulpes.instance.loadXMLRecipe(TileCrystallizer.class);
		LibVulpes.instance.loadXMLRecipe(TileElectrolyser.class);
		LibVulpes.instance.loadXMLRecipe(TileElectricArcFurnace.class);
		LibVulpes.instance.loadXMLRecipe(TileLathe.class);
		LibVulpes.instance.loadXMLRecipe(TileRollingMachine.class);
		LibVulpes.instance.loadXMLRecipe(BlockPress.class);*/
	}
	
	public void registerAllMachineRecipes() {
		
		for(Class<?>  clazz : machineList)
			try {
				if(clazz.isAssignableFrom(TileMultiblockMachine.class))
				((TileMultiblockMachine)clazz.newInstance()).registerRecipes();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
	}
	
	public void createAutoGennedRecipes(HashMap<AllowedProducts, HashSet<String>> modProducts) {
		
		// no more auto genned recipies for now
		final String group = "idk";
		List<net.minecraft.item.crafting.IRecipe<?>> recipeList = new LinkedList<>();
		
		for(zmaster587.libVulpes.api.material.Material ore : MaterialRegistry.getAllMaterials()) {
			if(AllowedProducts.getProductByName("ORE").isOfType(ore.getAllowedProducts()) && AllowedProducts.getProductByName("INGOT").isOfType(ore.getAllowedProducts()))
            	recipeList.add(new FurnaceRecipe(new ResourceLocation(Constants.modId, "smelting" + ore.getOreDictNames()[0]), "smelting", 
            			Ingredient.fromItems(ore.getProduct(AllowedProducts.getProductByName("ORE")).getItem()), 
            			ore.getProduct(AllowedProducts.getProductByName("INGOT")), 0, 600));

			if(AllowedProducts.getProductByName("NUGGET").isOfType(ore.getAllowedProducts())) {
				ItemStack nugget = ore.getProduct(AllowedProducts.getProductByName("NUGGET"));
				nugget.setCount(9);
				for(String str : ore.getOreDictNames()) {
					
					NonNullList<Ingredient> nuggetList = NonNullList.create();
					nuggetList.add(Ingredient.fromTag(ItemTags.getCollection().get(new ResourceLocation("forge", AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + "/" + str))));
					
					recipeList.add(new ShapelessRecipe(new ResourceLocation(Constants.modId, "unpacknugget"+str), "nuggets", nugget, nuggetList));
				    
					ShapedRecipeBuilder.shapedRecipe(ore.getProduct(AllowedProducts.getProductByName("INGOT")).getItem())
					.patternLine("ooo").patternLine("ooo").patternLine("ooo")
					.key('o', ItemTags.getCollection().get(new ResourceLocation("forge", AllowedProducts.getProductByName("NUGGET").name().toLowerCase(Locale.ENGLISH) + "/" + str)))
					.build(finishedRecipe -> { JsonObject json = new JsonObject(); finishedRecipe.serialize(json); recipeList.add(ShapedRecipe.Serializer.CRAFTING_SHAPED.read(new ResourceLocation(Constants.modId, "ingot" + str), json)); });
				
				}
			}

			if(AllowedProducts.getProductByName("GEM").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(new ResourceLocation(Constants.modId, "crystalize_"+str.toLowerCase()), RecipeCrystallizer.INSTANCE, TileCrystallizer.class, ore.getProduct(AllowedProducts.getProductByName("GEM")), 300, 20, AllowedProducts.getProductByName("DUST").name().toLowerCase(Locale.ENGLISH) + str);
			}

			if(AllowedProducts.getProductByName("BOULE").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames())
					RecipesMachine.getInstance().addRecipe(new ResourceLocation(Constants.modId, "boule_"+str.toLowerCase()),  RecipeCrystallizer.INSTANCE, TileCrystallizer.class, ore.getProduct(AllowedProducts.getProductByName("BOULE")), 300, 20, AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + str, AllowedProducts.getProductByName("NUGGET").name().toLowerCase(Locale.ENGLISH) + str);
			}

			if(AllowedProducts.getProductByName("STICK").isOfType(ore.getAllowedProducts()) && AllowedProducts.getProductByName("INGOT").isOfType(ore.getAllowedProducts())) {
				for(String name : ore.getOreDictNames())
					if(ItemTags.getCollection().getRegisteredTags().contains(new ResourceLocation("forge", AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + "/" + name))) {

						ResourceLocation stickInput = new ResourceLocation("forge", AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + "/" + name);

						NonNullList<Ingredient> stickInputList = NonNullList.create();
						for(int i = 0 ; i < 3; i++)
							stickInputList.add(Ingredient.fromTag(ItemTags.getCollection().get(stickInput)));
						
						new ShapedRecipe(new ResourceLocation(Constants.modId, "stick"+name), "sticks", 3, 3, stickInputList, ore.getProduct(AllowedProducts.getProductByName("STICK"),4));

						RecipesMachine.getInstance().addRecipe(new ResourceLocation(Constants.modId, "stick_"+name.toLowerCase()),  RecipeLathe.INSTANCE, TileLathe.class, ore.getProduct(AllowedProducts.getProductByName("STICK"),2), 300, 20, AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + name); //ore.getProduct(AllowedProducts.getProductByName("INGOT")));
					}
			}

			if(AllowedProducts.getProductByName("PLATE").isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames()) {
					ResourceLocation resource = new ResourceLocation("forge", AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + oreDictNames);
					if(ItemTags.getCollection().getRegisteredTags().contains(resource)) {
						
						RecipesMachine.getInstance().addRecipe(new ResourceLocation(Constants.modId, "plate_"+oreDictNames.toLowerCase()),  RecipeRollingMachine.INSTANCE,TileRollingMachine.class, ore.getProduct(AllowedProducts.getProductByName("PLATE")), 300, 20, AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + oreDictNames, new FluidStack(Fluids.WATER, 100));
						if(AllowedProducts.getProductByName("BLOCK").isOfType(ore.getAllowedProducts()) || ore.isVanilla())
							RecipesMachine.getInstance().addRecipe(new ResourceLocation(Constants.modId, "plate_"+oreDictNames.toLowerCase()),  RecipeSmallPresser.INSTANCE, BlockPress.class, ore.getProduct(AllowedProducts.getProductByName("PLATE"),4), 0, 0, AllowedProducts.getProductByName("BLOCK").name().toLowerCase(Locale.ENGLISH) + oreDictNames);
					}
				}
			}

			if(AllowedProducts.getProductByName("SHEET").isOfType(ore.getAllowedProducts())) {
				for(String oreDictNames : ore.getOreDictNames()) {
					RecipesMachine.getInstance().addRecipe(new ResourceLocation(Constants.modId, "sheet_" + oreDictNames.toLowerCase()),  RecipeRollingMachine.INSTANCE, TileRollingMachine.class, ore.getProduct(AllowedProducts.getProductByName("SHEET")), 300, 200, AllowedProducts.getProductByName("PLATE").name().toLowerCase(Locale.ENGLISH) + oreDictNames, new FluidStack(Fluids.WATER, 100));
				}
			}

			if(AllowedProducts.getProductByName("COIL").isOfType(ore.getAllowedProducts())) {

				
				for(String str : ore.getOreDictNames())
					ShapedRecipeBuilder.shapedRecipe(ore.getProduct(AllowedProducts.getProductByName("COIL")).getItem()).patternLine("ooo").patternLine("o o").patternLine("ooo")
					.key('o', ItemTags.getCollection().get(new ResourceLocation("forge", AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + "/" + str)))
					.build((finishedRecipe -> { JsonObject json = new JsonObject(); finishedRecipe.serialize(json); recipeList.add(ShapedRecipe.Serializer.CRAFTING_SHAPED.read(new ResourceLocation(Constants.modId, "coil" + str), json)); }) );
			}

			if(AllowedProducts.getProductByName("FAN").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames()) {
					
					ShapedRecipeBuilder.shapedRecipe(ore.getProduct(AllowedProducts.getProductByName("FAN")).getItem())
					.patternLine("p p").patternLine(" r ").patternLine("p p")
					.key('p', ItemTags.getCollection().get(new ResourceLocation("forge", AllowedProducts.getProductByName("PLATE").name().toLowerCase(Locale.ENGLISH) + "/" + str)))
					.key('r', ItemTags.getCollection().get(new ResourceLocation("forge", AllowedProducts.getProductByName("STICK").name().toLowerCase(Locale.ENGLISH) + "/" + str)))
					.build(finishedRecipe -> { JsonObject json = new JsonObject(); finishedRecipe.serialize(json); recipeList.add(ShapedRecipe.Serializer.CRAFTING_SHAPED.read(new ResourceLocation(Constants.modId, "coil" + str), json)); });
				}
			}
			if(AllowedProducts.getProductByName("GEAR").isOfType(ore.getAllowedProducts())) {
				for(String str : ore.getOreDictNames()) {
					
					ShapedRecipeBuilder.shapedRecipe(ore.getProduct(AllowedProducts.getProductByName("GEAR")).getItem())
					.patternLine("sps").patternLine(" r ").patternLine("sps")
					.key('p', ItemTags.getCollection().get(new ResourceLocation("forge", AllowedProducts.getProductByName("PLATE").name().toLowerCase(Locale.ENGLISH) + "/" + str)))
					.key('s', ItemTags.getCollection().get(new ResourceLocation("forge", AllowedProducts.getProductByName("STICK").name().toLowerCase(Locale.ENGLISH) + "/" + str)))
					.key('s', ItemTags.getCollection().get(new ResourceLocation("forge", AllowedProducts.getProductByName("INGOT").name().toLowerCase(Locale.ENGLISH) + "/" + str)))
					.build(finishedRecipe -> { JsonObject json = new JsonObject(); finishedRecipe.serialize(json); recipeList.add(ShapedRecipe.Serializer.CRAFTING_SHAPED.read(new ResourceLocation(Constants.modId, "gear" + str), json)); });
					
				}
			}
			if(AllowedProducts.getProductByName("BLOCK").isOfType(ore.getAllowedProducts())) {
				ItemStack ingot = ore.getProduct(AllowedProducts.getProductByName("INGOT"));
				ingot.setCount(9);
				for(String str : ore.getOreDictNames())
                {
					
					ResourceLocation oreName = new ResourceLocation("forge", AllowedProducts.getProductByName("BLOCK").name().toLowerCase(Locale.ENGLISH) + "/" + str);
					NonNullList<Ingredient> blockToIngot = NonNullList.create();
					blockToIngot.add(Ingredient.fromTag(ItemTags.getCollection().getTagByID(oreName)));
					
					ShapedRecipeBuilder.shapedRecipe(ore.getProduct(AllowedProducts.getProductByName("BLOCK")).getItem())
					.patternLine("ooo").patternLine("ooo").patternLine("ooo")
					.key('o', ItemTags.getCollection().get(new ResourceLocation("forge", AllowedProducts.getProductByName("BLOCK").name().toLowerCase(Locale.ENGLISH) + "/" + str)))
					.build(finishedRecipe -> { JsonObject json = new JsonObject(); finishedRecipe.serialize(json); recipeList.add(ShapedRecipe.Serializer.CRAFTING_SHAPED.read(new ResourceLocation(Constants.modId, "block" + str), json)); });
				
					
					recipeList.add(new ShapelessRecipe(new ResourceLocation(Constants.modId, "unpackblock"+str), "blocks", ingot, blockToIngot));
                }
            }

            if (AllowedProducts.getProductByName("DUST").isOfType(ore.getAllowedProducts()))
            {
                for (String str : ore.getOreDictNames())
                {
                    if (AllowedProducts.getProductByName("ORE").isOfType(ore.getAllowedProducts()) || ore.isVanilla())
                    {
                        ItemStack stack = ore.getProduct(AllowedProducts.getProductByName("DUST"));
                        stack.setCount(2);
                        RecipesMachine.getInstance().addRecipe(new ResourceLocation(Constants.modId, "oretodust_" + str), RecipeSmallPresser.INSTANCE , BlockPress.class, stack, 0, 0,
                                AllowedProducts.getProductByName("ORE").name().toLowerCase(Locale.ENGLISH) + str);
                    }
                    if (AllowedProducts.getProductByName("INGOT").isOfType(ore.getAllowedProducts()) || ore.isVanilla())
                    	
                    	recipeList.add(new FurnaceRecipe(new ResourceLocation(Constants.modId, "smelting" + str), "smelting", 
                    			Ingredient.fromItems(ore.getProduct(AllowedProducts.getProductByName("DUST")).getItem()), 
                    			ore.getProduct(AllowedProducts.getProductByName("INGOT")), 0, 600));
                }
            }
        }

        // Handle vanilla integration
        /*if (zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().allowSawmillVanillaWood)
        {
            for (int i = 0; i < 4; i++)
            {
                RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(Blocks.PLANKS, 6, i), 80,
                        10, new ItemStack(Blocks.LOG, 1, i));
            }
            RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(Blocks.PLANKS, 6, 4), 80, 10,
                    new ItemStack(Blocks.LOG2, 1, 0));
            RecipesMachine.getInstance().addRecipe(TileCuttingMachine.class, new ItemStack(Blocks.PLANKS, 6, 5), 80, 10,
                    new ItemStack(Blocks.LOG2, 1, 1));
        }*/

        // Handle items from other mods
        /*if (zmaster587.advancedRocketry.api.ARConfiguration.getCurrentConfig().allowMakingItemsForOtherMods)
        {
            for (Entry<AllowedProducts, HashSet<String>> entry : modProducts.entrySet())
            {
                if (entry.getKey() == AllowedProducts.getProductByName("PLATE"))
                {
                    for (String str : entry.getValue())
                    {
                        zmaster587.libVulpes.api.material.Material material = zmaster587.libVulpes.api.material.Material
                                .valueOfSafe(str.toUpperCase());

                        if (OreDictionary.doesOreNameExist("ingot" + str)
                                && OreDictionary.getOres("ingot" + str).size() > 0 &&
                                (OreDictionary.doesOreNameExist("plate" + str)
                                        && OreDictionary.getOres("plate" + str).size() > 0)
                                && (material == null || !AllowedProducts.getProductByName("PLATE")
                                        .isOfType(material.getAllowedProducts())))
                        {

                            RecipesMachine.getInstance().addRecipe(TileRollingMachine.class,
                                    OreDictionary.getOres("plate" + str).get(0), 300, 20, "ingot" + str, new FluidStack(FluidRegistry.WATER, 100));
                        }
                    }
                }
                else if (entry.getKey() == AllowedProducts.getProductByName("STICK"))
                {
                    for (String str : entry.getValue())
                    {
                        zmaster587.libVulpes.api.material.Material material = zmaster587.libVulpes.api.material.Material
                                .valueOfSafe(str.toUpperCase());

                        if (OreDictionary.doesOreNameExist("ingot" + str)
                                && OreDictionary.getOres("ingot" + str).size() > 0
                                && (material == null || !AllowedProducts.getProductByName("STICK")
                                        .isOfType(material.getAllowedProducts())))
                        {

                            // GT registers rods as sticks
                            ItemStack stackToAdd = null;
                            if (OreDictionary.doesOreNameExist("rod" + str)
                                    && OreDictionary.getOres("rod" + str).size() > 0)
                            {
                                stackToAdd = OreDictionary.getOres("rod" + str).get(0).copy();
                                stackToAdd.setCount(2);
                            }
                            else if (OreDictionary.doesOreNameExist("stick" + str)
                                    && OreDictionary.getOres("stick" + str).size() > 0)
                            {
                                stackToAdd = OreDictionary.getOres("stick" + str).get(0).copy();
                                stackToAdd.setCount(2);
                            }
                            else continue;

                            RecipesMachine.getInstance().addRecipe(TileLathe.class, stackToAdd, 300, 20, "ingot" + str);
                        }
                    }
                }
            }
        }*/
    }
}
