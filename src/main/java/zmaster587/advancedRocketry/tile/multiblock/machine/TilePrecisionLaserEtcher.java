package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.Iterator;
import java.util.List;

public class TilePrecisionLaserEtcher extends TileMultiblockMachine implements IModularInventory {

	public static final Object[][][] structure = { 
		{{"slab", "slab", "slab"},
		 {Blocks.AIR, "slab", Blocks.AIR},
		 {"slab", "slab", "slab"}},

		{{AdvancedRocketryBlocks.blockStructureTower, Blocks.AIR, LibVulpesBlocks.blockStructureBlock},
		 {Blocks.AIR, AdvancedRocketryBlocks.blockVacuumLaser, LibVulpesBlocks.blockStructureBlock},
		 {AdvancedRocketryBlocks.blockStructureTower, Blocks.AIR, LibVulpesBlocks.blockStructureBlock}},

		{{LibVulpesBlocks.blockStructureBlock, 'c', 'I'},
		 {'P', LibVulpesBlocks.motors, 'O'},
		 {'P', LibVulpesBlocks.blockStructureBlock, LibVulpesBlocks.blockStructureBlock}},
	};
	
	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		return true;
	}
	
	@Override
	protected float getTimeMultiplierForRecipe(IRecipe recipe) {
		return super.getTimeMultiplierForRecipe(recipe);
	}

	@Override
	public void consumeItems(IRecipe recipe) {
		List<List<ItemStack>> ingredients = recipe.getIngredients();

		label77:
		for(int ingredientNum = 0; ingredientNum < ingredients.size(); ++ingredientNum) {
			List<ItemStack> ingredient = (List)ingredients.get(ingredientNum);
			Iterator var5 = this.getItemInPorts().iterator();

			while(var5.hasNext()) {
				IInventory hatch = (IInventory)var5.next();

				for(int i = 0; i < hatch.getSizeInventory(); ++i) {
					ItemStack stackInSlot = hatch.getStackInSlot(i);
					Iterator var9 = ingredient.iterator();

					while(var9.hasNext()) {
						ItemStack stack = (ItemStack)var9.next();

						if ((stackInSlot != null && stackInSlot.getCount() >= stack.getCount() && (stackInSlot.isItemEqual(stack) || stack.getItemDamage() == 32767 && stackInSlot.getItem() == stack.getItem())) && !isLensItem(stack)) {
							hatch.decrStackSize(i, stack.getCount());
							hatch.markDirty();
							this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(((TileEntity)hatch).getPos()), this.world.getBlockState(((TileEntity)hatch).getPos()), 6);
							continue label77;
						}
					}
				}
			}
		}
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		return new AxisAlignedBB(pos.add(-3,-2,-3),pos.add(3,2,3));
	}

	private boolean isLensItem (ItemStack stack) {
		int[] oreIds = OreDictionary.getOreIDs(stack);
		for (int oreId : oreIds) {
			if (OreDictionary.getOreName(oreId).contains("lensPrecisionLaserEtcher")) {
				return true;
			}
		}
		return false;
	}
	@Override
	public SoundEvent getSound() {
		return AudioRegistry.lathe;
	}

	@Override
	public int getSoundDuration() {
		return 30;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 40, 0, TextureResources.latheProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return "tile.precisionlaseretcher.name";
	}
}
