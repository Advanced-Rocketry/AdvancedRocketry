package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.api.material.AllowedProducts;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;

public class TileWarpCore extends TileMultiBlock {
	private SpaceStationObject station;

	public static final Object[][][] structure = { 
		{{"blockWarpCoreRim", "blockWarpCoreRim", "blockWarpCoreRim"},
			{"blockWarpCoreRim", 'I', "blockWarpCoreRim"},
			{"blockWarpCoreRim", "blockWarpCoreRim", "blockWarpCoreRim"}},

			{{null, new BlockMeta(LibVulpesBlocks.blockStructureBlock), null},
				{new BlockMeta(LibVulpesBlocks.blockStructureBlock), "blockWarpCoreCore", new BlockMeta(LibVulpesBlocks.blockStructureBlock)},
				{null, new BlockMeta(LibVulpesBlocks.blockStructureBlock), null}},

				{{"blockWarpCoreRim", 'c', "blockWarpCoreRim"},
					{"blockWarpCoreRim", "blockWarpCoreCore", "blockWarpCoreRim"},
					{"blockWarpCoreRim", "blockWarpCoreRim", "blockWarpCoreRim"}},

	};

	private SpaceStationObject getSpaceObject() {
		if(station == null && world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(object instanceof SpaceStationObject)
				station = (SpaceStationObject) object;
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
		
		if(getSpaceObject() == null || (getSpaceObject().getMaxFuelAmount() - getSpaceObject().getFuelAmount() < ARConfiguration.getCurrentConfig().fuelPointsPerDilithium))
			return;
		for(IInventory inv : itemInPorts) {
			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				int amt = 0;
				if(stack != null && OreDictionary.itemMatches(MaterialRegistry.getItemStackFromMaterialAndType("Dilithium", AllowedProducts.getProductByName("GEM")), stack, false)) {
					if(!world.isRemote)
						amt = getSpaceObject().addFuel(ARConfiguration.getCurrentConfig().fuelPointsPerDilithium);
					inv.decrStackSize(i, amt/ARConfiguration.getCurrentConfig().fuelPointsPerDilithium);
					inv.markDirty();
					
					//If full
					if(getSpaceObject().getMaxFuelAmount() - getSpaceObject().getFuelAmount() < ARConfiguration.getCurrentConfig().fuelPointsPerDilithium)
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
