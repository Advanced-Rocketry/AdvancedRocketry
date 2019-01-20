package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.LinkedList;
import java.util.List;

public class TileChemicalReactor extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{null, 'c',null},
			{'L', 'I','L'}},

			{{'P', LibVulpesBlocks.motors, 'P'}, 
				{'l', new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'O'}},

	};

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		TileEntity tileEntity = world.getTileEntity(pos);

		return !TileMultiBlock.getMapping('P').contains(new BlockMeta(tile.getBlock(), BlockMeta.WILDCARD)) && tileEntity != null && !(tileEntity instanceof TileChemicalReactor);

	}

	//Called by inventory blocks that are part of the structure
	//This includes recipe management etc
	@Override
	public void onInventoryUpdated() {
		//If we are already processing something don't bother
		IRecipe recipe;
		boolean flag = false;
		if(getOutputs() == null && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe))
		{
			if(!recipe.getOutput().isEmpty()) {
			NBTTagList list = recipe.getOutput().get(0).getEnchantmentTagList();
			
			if(list != null) {
				for( int i = 0 ; i < list.tagCount(); i++ ) {
					NBTTagCompound tag = (NBTTagCompound)list.get(i);
					//if(tag.getInteger("id") == Enchantment.getEnchantmentID(AdvancedRocketryAPI.enchantmentSpaceProtection) ) {

						flag = true;
						break;
					//}
				}
			}
				
			}
		}

		//If airbreathing enchantment
		if(flag && getOutputs() == null) {
			if(enabled && (recipe = getRecipe(getMachineRecipeList())) != null && canProcessRecipe(recipe)) {
				consumeItemsSpecial(recipe);
				setOutputFluids(new LinkedList<FluidStack>());
				powerPerTick = (int)Math.ceil((getPowerMultiplierForRecipe(recipe)*recipe.getPower()));
				completionTime = Math.max((int)(getTimeMultiplierForRecipe(recipe)*recipe.getTime()), 1);

				

				markDirty();
				world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);

				setMachineRunning(true); //turn on machine

			}
			else {
				setMachineRunning(false);
			}
		}
		else {
			super.onInventoryUpdated();
		}
	}

	//Consumes the items for the suit recipe and sets the output
	public void consumeItemsSpecial(IRecipe recipe) {
		List<List<ItemStack>> ingredients = recipe.getIngredients();

		for(int ingredientNum = 0;ingredientNum < ingredients.size(); ingredientNum++) {

			List<ItemStack> ingredient = ingredients.get(ingredientNum);

			ingredientCheck:
			for(IInventory hatch : itemInPorts) {
				for(int i = 0; i < hatch.getSizeInventory(); i++) {
					ItemStack stackInSlot = hatch.getStackInSlot(i);
					for (ItemStack stack : ingredient) {
						if(stackInSlot != null && stackInSlot.getCount() >= stack.getCount() && (stackInSlot.getItem() == stack.getItem() && (stackInSlot.getItemDamage() == stack.getItemDamage() || stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) )) {
							ItemStack stack2 = hatch.decrStackSize(i, stack.getCount());
							
							if(stack2.getItem() instanceof ItemArmor)
							{
								stack2.addEnchantment(AdvancedRocketryAPI.enchantmentSpaceProtection, 1);
								List<ItemStack> list = new LinkedList<ItemStack>();
								list.add(stack2);
								setOutputs(list);
							}
							
							hatch.markDirty();
							world.notifyBlockUpdate(pos, world.getBlockState(((TileEntity)hatch).getPos()),  world.getBlockState(((TileEntity)hatch).getPos()), 6);
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
		if(Configuration.enableOxygen) {
			for(ResourceLocation key : Item.REGISTRY.getKeys()) {
				Item item = Item.REGISTRY.getObject(key);
	
				if(item instanceof ItemArmor && !(item instanceof ItemSpaceArmor)) {
					ItemStack enchanted = new ItemStack(item);
					enchanted.addEnchantment(AdvancedRocketryAPI.enchantmentSpaceProtection, 1);
	
					if(((ItemArmor)item).armorType == EntityEquipmentSlot.CHEST)
						RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, enchanted, 100, 10, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE), "gemDiamond", new ItemStack(AdvancedRocketryItems.itemPressureTank, 1, 3));
					else
						RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, enchanted, 100, 10, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE), "gemDiamond");
	
				}
			}
		}
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
		return "tile.chemreactor.name";
	}
}
