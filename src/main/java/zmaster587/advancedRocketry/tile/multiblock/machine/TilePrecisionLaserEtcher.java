package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import javax.annotation.Nonnull;
import java.util.List;

public class TilePrecisionLaserEtcher extends TileMultiblockMachine implements IModularInventory {

	public static final Object[][][] structure = { 
			{{new ResourceLocation("minecraft","slabs"), new ResourceLocation("minecraft","slabs"), new ResourceLocation("minecraft","slabs")},
				{Blocks.AIR, new ResourceLocation("minecraft","slabs"), Blocks.AIR},
				{new ResourceLocation("minecraft","slabs"), new ResourceLocation("minecraft","slabs"), new ResourceLocation("minecraft","slabs")}},

			{{AdvancedRocketryBlocks.blockStructureTower, Blocks.AIR, LibVulpesBlocks.blockMachineStructure},
					{Blocks.AIR, AdvancedRocketryBlocks.blockLaser, LibVulpesBlocks.blockMachineStructure},
					{AdvancedRocketryBlocks.blockStructureTower, Blocks.AIR, LibVulpesBlocks.blockMachineStructure}},

			{{LibVulpesBlocks.blockMachineStructure, 'c', 'I'},
						{'P', LibVulpesBlocks.motors, 'O'},
						{'P', LibVulpesBlocks.blockMachineStructure, LibVulpesBlocks.blockMachineStructure}},
	};

	public TilePrecisionLaserEtcher()
	{
		super(AdvancedRocketryTileEntityType.TILE_PREC_LASER_ETCHER);
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {
		return true;
	}

	@Override
	protected float getTimeMultiplierForRecipe(IRecipe recipe) {
		return super.getTimeMultiplierForRecipe(recipe);
	}

	@Override
	public void consumeItems(IRecipe recipe) {
		List<List<ItemStack>> ingredients = recipe.getPossibleIngredients();

		label77:
		for (List<ItemStack> ingredient : ingredients) {
			for (IInventory hatch : this.getItemInPorts()) {
				for (int i = 0; i < hatch.getSizeInventory(); ++i) {
					ItemStack stackInSlot = hatch.getStackInSlot(i);

					for (ItemStack stack : ingredient) {
						if ((!stackInSlot.isEmpty() && stackInSlot.getCount() >= stack.getCount() && (stackInSlot.isItemEqual(stack) && stackInSlot.getItem() == stack.getItem())) && !isLensItem(stack)) {
							hatch.decrStackSize(i, stack.getCount());
							hatch.markDirty();
							this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(((TileEntity) hatch).getPos()), this.world.getBlockState(((TileEntity) hatch).getPos()), 6);
							continue label77;
						}
					}
				}
			}
		}
	}

	@Override
	@Nonnull
	public AxisAlignedBB getRenderBoundingBox() {

		return new AxisAlignedBB(pos.add(-3,-2,-3),pos.add(3,2,3));
	}

	private boolean isLensItem (ItemStack stack) {
		return ItemTags.getCollection().getOwningTags(stack.getItem()).contains(new ResourceLocation("advancedrocketry:lensPrecisionLaserEtcher"));
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
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(new ModuleProgress(100, 40, 0, TextureResources.latheProgressBar, this));
		return modules;
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.precisionlaseretcher";
	}
}
