package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.NumberedOreDictStack;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class TileChemicalReactor extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{null, 'c',null},
			{'L', 'I','L'}},

			{{'P', LibVulpesBlocks.motors, 'P'}, 
				{'l', 'O', 'l'}},

	};

	private static List<IRecipe> recipesSpecial = new LinkedList<>();

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) { return true; }

	//Called by inventory blocks that are part of the structure
	//This includes recipe management etc
	@Override
	public void onInventoryUpdated() {
		IRecipe recipe;

		//If we are already processing something don't bother
		//If airbreathing enchantment
		if(getOutputs() == null && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe) && !recipe.getOutput().isEmpty()
				&& EnchantmentHelper.getEnchantmentLevel(AdvancedRocketryAPI.enchantmentSpaceProtection, recipe.getOutput().get(0)) == 1) {
			if(!enabled) {
				setMachineRunning(false);
				return;
			}

			consumeItemsSpecial(recipe);
			setOutputFluids(new LinkedList<>());
			powerPerTick = (int)Math.ceil((getPowerMultiplierForRecipe(recipe)*recipe.getPower()));
			completionTime = Math.max((int)(getTimeMultiplierForRecipe(recipe)*recipe.getTime()), 1);



			markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);

			setMachineRunning(true); //turn on machine
		}
		else {
			super.onInventoryUpdated();
		}
	}

	//Consumes the items for the suit recipe and sets the output
	public void consumeItemsSpecial(IRecipe recipe) {
		List<List<ItemStack>> ingredients = recipe.getIngredients();

		for (List<ItemStack> ingredient : ingredients) {

			ingredientCheck:
			for (IInventory hatch : itemInPorts) {
				for (int i = 0; i < hatch.getSizeInventory(); i++) {
					ItemStack stackInSlot = hatch.getStackInSlot(i);
					for(ItemStack stack : ingredient) {
						if(!stackInSlot.isEmpty() && stackInSlot.getCount() >= stack.getCount() && (stackInSlot.getItem() == stack.getItem() && (stackInSlot.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE))) {
							ItemStack stack2 = hatch.decrStackSize(i, stack.getCount());

							if(stack2.getItem() instanceof ItemArmor) {
								if(EnchantmentHelper.getEnchantmentLevel(AdvancedRocketryAPI.enchantmentSpaceProtection, stack2) == 0) {
									stack2.addEnchantment(AdvancedRocketryAPI.enchantmentSpaceProtection, 1);
								}

								List<ItemStack> list = new LinkedList<>();
								list.add(stack2);
								setOutputs(list);
							}

							hatch.markDirty();
							world.notifyBlockUpdate(pos, world.getBlockState(((TileEntity) hatch).getPos()), world.getBlockState(((TileEntity) hatch).getPos()), 6);
							break ingredientCheck;
						}
					}
				}
			}
		}
	}

	@Override
	public void registerRecipes() {
		//Chemical Reactor
		if(ARConfiguration.getCurrentConfig().enableOxygen) {
			RecipesMachine recipesMachine = RecipesMachine.getInstance();
			List<IRecipe> recipes = recipesMachine.getRecipes(TileChemicalReactor.class);
			List<IRecipe> originalRecipes = new LinkedList<>(recipes);

			for(ResourceLocation key : Item.REGISTRY.getKeys()) {
				Item item = Item.REGISTRY.getObject(key);
	
				if(item instanceof ItemArmor && !(item instanceof ItemSpaceArmor)) {
					ItemStack enchanted = new ItemStack(item);
					enchanted.addEnchantment(AdvancedRocketryAPI.enchantmentSpaceProtection, 1);

					//TODO: fix lore not appearing
					/*NBTTagCompound tag = enchanted.getTagCompound();
					if(tag == null) {
						enchanted.setTagCompound(tag = new NBTTagCompound());
					}

					if(!tag.hasKey("display")) {
						tag.setTag("display", new NBTTagCompound());
					}

					if(tag.getTagId("display") == 10) {
						NBTTagCompound displayTag = tag.getCompoundTag("display");

						if(!displayTag.hasKey("Lore")) {
							displayTag.setTag("Lore", new NBTTagList());
						}

						if (displayTag.getTagId("Lore") == 9) {
							NBTTagList loreTag = displayTag.getTagList("Lore", 8);

							loreTag.appendTag(new NBTTagString("§eThis recipe adds the Airtight Seal enchantment"));
						}
					}*/
	
					if(((ItemArmor)item).armorType == EntityEquipmentSlot.CHEST)
						recipesMachine.addRecipe(TileChemicalReactor.class, enchanted, 100, 10, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(AdvancedRocketryBlocks.blockPipeSealer, 1), new NumberedOreDictStack("sheetTitaniumAluminide", 4), new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 3));
					else
						recipesMachine.addRecipe(TileChemicalReactor.class, enchanted, 100, 10, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(AdvancedRocketryBlocks.blockPipeSealer, 1), new NumberedOreDictStack("sheetTitaniumAluminide", 4));
				}
			}

			for(IRecipe recipe : recipes) {
				if(!originalRecipes.contains(recipe)) {
					recipesSpecial.add(recipe);
				}
			}
		}
	}

	public static void clearRecipesSpecial() {
		RecipesMachine.getInstance().getRecipes(TileChemicalReactor.class).removeAll(recipesSpecial);
		recipesSpecial.clear();
	}

	public static void reloadRecipesSpecial() {
		clearRecipesSpecial();
		new TileChemicalReactor().registerRecipes();
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public SoundEvent getSound() {
		return AudioRegistry.rollingMachine;
	}

	@Override
	public int getSoundDuration() {
		return 30;
	}

	@Override
	@Nonnull
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2,-2,-2), pos.add(2,2,2));
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return AdvancedRocketryBlocks.blockChemicalReactor.getLocalizedName();
	}
}
