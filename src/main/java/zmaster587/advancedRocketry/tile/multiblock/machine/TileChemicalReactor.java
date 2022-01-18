package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.recipe.RecipeChemicalReactor;
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

	public TileChemicalReactor() {
		super(AdvancedRocketryTileEntityType.TILE_CHEMICAL_REACTOR);
	}
	
	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) { return true; }

	//Called by inventory blocks that are part of the structure
	//This includes recipe management etc
	@Override
	public void onInventoryUpdated() {
		//If we are already processing something don't bother
		IRecipe recipe;
		boolean flag = false;
		if(getOutputs() == null && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe))
		{/*
			if(!recipe.getOutput().isEmpty()) {
			ListNBT list = recipe.getOutput().get(0).getEnchantmentTagList();

			if(list != null) {
				for( int i = 0 ; i < list.size(); i++ ) {
					CompoundNBT tag = (CompoundNBT)list.get(i);
					//if(tag.getInt("id") == Enchantment.getEnchantmentID(AdvancedRocketryAPI.enchantmentSpaceProtection) ) {

						flag = true;
						break;
					}
				}
				flag = true;

			}*/
		}

		//If airbreathing enchantment
		if(flag && getOutputs().isEmpty()) {
			if(enabled && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe)) {
				consumeItemsSpecial(recipe);
				setOutputFluids(new LinkedList<>());
				powerPerTick = (int)Math.ceil((getPowerMultiplierForRecipe(recipe)*recipe.getPower()));
				completionTime = Math.max((int)(getTimeMultiplierForRecipe(recipe)*recipe.getTime()), 1);

				

				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
			}
		}
		else {
			super.onInventoryUpdated();
		}
	}

	//Consumes the items for the suit recipe and sets the output
	public void consumeItemsSpecial(IRecipe recipe) {
		List<List<ItemStack>> ingredients = recipe.getPossibleIngredients();

		for (List<ItemStack> ingredient : ingredients) {

			ingredientCheck:
			for (IInventory hatch : itemInPorts) {
				for (int i = 0; i < hatch.getSizeInventory(); i++) {
					ItemStack stackInSlot = hatch.getStackInSlot(i);
					for (ItemStack stack : ingredient) {
						if(stackInSlot != null && stackInSlot.getCount() >= stack.getCount() && (stackInSlot.getItem() == stack.getItem() && (stackInSlot.getDamage() == stack.getDamage()) )) {
							ItemStack stack2 = hatch.decrStackSize(i, stack.getCount());
							
							if(stack2.getItem() instanceof ArmorItem)
							{
								stack2.addEnchantment(AdvancedRocketryAPI.enchantmentSpaceProtection, 1);
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

	public static void registerRecipes() {
		//Chemical Reactor
		if(ARConfiguration.getCurrentConfig().enableOxygen.get()) {
			for(ResourceLocation key : ForgeRegistries.ITEMS.getKeys()) {
				Item item = ForgeRegistries.ITEMS.getValue(key);
	
				if(item instanceof ArmorItem && !(item instanceof ItemSpaceArmor)) {
					ItemStack enchanted = new ItemStack(item);
					enchanted.addEnchantment(AdvancedRocketryAPI.enchantmentSpaceProtection, 1);
	
					if(((ArmorItem)item).getEquipmentSlot() == EquipmentSlotType.CHEST)
						RecipesMachine.getInstance().addRecipe(key, RecipeChemicalReactor.INSTANCE, TileChemicalReactor.class, enchanted, 100, 10, new ItemStack(AdvancedRocketryBlocks.blockSeal, 1), new NumberedOreDictStack(new ResourceLocation("forge","sheets/titaniumaluminide"), 8), new ItemStack(AdvancedRocketryItems.itemTitaniumPressureTank, 1));
					else
						RecipesMachine.getInstance().addRecipe(key, RecipeChemicalReactor.INSTANCE, TileChemicalReactor.class, enchanted, 100, 10, new ItemStack(AdvancedRocketryBlocks.blockSeal, 1), new NumberedOreDictStack(new ResourceLocation("forge","sheets/titaniumaluminide"), 4));
	
				}
			}
		}
	}

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
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.chemicalreactor";
	}
}
