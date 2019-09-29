package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

public class TileWarpCore extends TileMultiBlock {
	private SpaceObject station;

	public static final Object[][][] structure = { 
		{{"blockTitanium", "blockTitanium", "blockTitanium"},
			{"blockTitanium", 'I', "blockTitanium"},
			{"blockTitanium", "blockTitanium", "blockTitanium"}},

			{{null, new BlockMeta(LibVulpesBlocks.blockStructureBlock), null},
				{new BlockMeta(LibVulpesBlocks.blockStructureBlock), new BlockMeta(Blocks.GOLD_BLOCK), new BlockMeta(LibVulpesBlocks.blockStructureBlock)},
				{null, new BlockMeta(LibVulpesBlocks.blockStructureBlock), null}},

				{{"blockTitanium", 'c', "blockTitanium"}, 
					{"blockTitanium", new BlockMeta(Blocks.GOLD_BLOCK), "blockTitanium"},
					{"blockTitanium", "blockTitanium", "blockTitanium"}},

	};

	private SpaceObject getSpaceObject() {
		if(station == null && world.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(object instanceof SpaceObject)
				station = (SpaceObject) object;
		}
		return station;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		return pos.compareTo(this.pos) == 0;
	}
	
	
	@Override
	public void onInventoryUpdated() {
		//Needs completion
		if(itemInPorts.isEmpty() /*&& !worldObj.isRemote*/) {
			attemptCompleteStructure(world.getBlockState(pos));
		}
		
		if(getSpaceObject() == null || getSpaceObject().getFuelAmount() == getSpaceObject().getMaxFuelAmount())
			return;
		for(IInventory inv : itemInPorts) {
			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				int amt = 0;
				if(stack != null && OreDictionary.itemMatches(MaterialRegistry.getItemStackFromMaterialAndType("Dilithium", AllowedProducts.getProductByName("GEM")), stack, false)) {
					int stackSize = stack.getCount();
					if(!world.isRemote)
						amt = getSpaceObject().addFuel(Configuration.fuelPointsPerDilithium*stack.getCount());
					else
						amt = Math.min(getSpaceObject().getFuelAmount() + 10*stack.getCount(), getSpaceObject().getMaxFuelAmount()) - getSpaceObject().getFuelAmount();//
					inv.decrStackSize(i, amt/10);
					inv.markDirty();
					
					//If full
					if(stackSize/10 != amt)
						return;
				}
			}
		}
	}

	@Override
	public String getMachineName() {
		return "tile.warpCore.name";
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		
		return new AxisAlignedBB(pos.add(-2,-2,-2),pos.add(2,2,2));
	}

}
