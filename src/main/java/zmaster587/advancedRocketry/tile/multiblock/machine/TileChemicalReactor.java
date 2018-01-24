package zmaster587.advancedRocketry.tile.multiblock.machine;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

public class TileChemicalReactor extends TileMultiblockMachine {
	public static final Object[][][] structure = { 
		{{null, 'c',null},
		{'L', 'I','L'}},
		{{'P',  LibVulpesBlocks.motors, 'P'},
			{'l', new BlockMeta(LibVulpesBlocks.blockStructureBlock), 'O'}},

	};

	@Override
	public boolean shouldHideBlock(World world, int x, int y, int z, Block tile) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		
		return !TileMultiBlock.getMapping('P').contains(new BlockMeta(tile, BlockMeta.WILDCARD)) && tileEntity != null && !(tileEntity instanceof TileChemicalReactor);
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
					NBTTagCompound tag = (NBTTagCompound)list.getCompoundTagAt(i);
					//if(tag.getInteger("id") == AdvancedRocketryAPI.enchantmentSpaceProtection.effectId ) {

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
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

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
		List<LinkedList<ItemStack>> ingredients = recipe.getIngredients();

		for(int ingredientNum = 0;ingredientNum < ingredients.size(); ingredientNum++) {

			List<ItemStack> ingredient = ingredients.get(ingredientNum);

			ingredientCheck:
			for(IInventory hatch : itemInPorts) {
				for(int i = 0; i < hatch.getSizeInventory(); i++) {
					ItemStack stackInSlot = hatch.getStackInSlot(i);
					for (ItemStack stack : ingredient) {
						if(stackInSlot != null && stackInSlot.stackSize >= stack.stackSize && stackInSlot.isItemEqual(stack)) {
							ItemStack stack2 = hatch.decrStackSize(i, stack.stackSize);
							
							if(stack2.getItem() instanceof ItemArmor)
							{
								stack2.addEnchantment(AdvancedRocketryAPI.enchantmentSpaceProtection, 1);
								List<ItemStack> list = new LinkedList<ItemStack>();
								list.add(stack2);
								setOutputs(list);
							}
							
							hatch.markDirty();
							worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new Object[] {new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge,1, 0), new ItemStack(Items.coal, 1, 1)}, 40, 20, new ItemStack(AdvancedRocketryItems.itemCarbonScrubberCartridge, 1, AdvancedRocketryItems.itemCarbonScrubberCartridge.getMaxDamage()));
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new ItemStack(Items.dye,5,0xF), 100, 1, Items.bone, new FluidStack(AdvancedRocketryFluids.fluidNitrogen, 10));
		RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, new FluidStack(AdvancedRocketryFluids.fluidRocketFuel, 20), 100, 10, new FluidStack(AdvancedRocketryFluids.fluidOxygen, 10), new FluidStack(AdvancedRocketryFluids.fluidHydrogen, 10));

		if(Configuration.enableOxygen) {	
			for(Object key : Item.itemRegistry.getKeys()) {
				Item item = (Item) Item.itemRegistry.getObject(key);
				
				if(item instanceof ItemArmor && !(item instanceof ItemSpaceArmor)) {
					ItemStack enchanted = new ItemStack(item);
					enchanted.addEnchantment(AdvancedRocketryAPI.enchantmentSpaceProtection, 1);
					RecipesMachine.getInstance().addRecipe(TileChemicalReactor.class, enchanted, 100, 10, item, "gemDiamond");
	
				}
			}
		}
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public ResourceLocation getSound() {
		return TextureResources.sndRollingMachine;
	}

	@Override
	public int getSoundDuration() {
		return 30;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
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
