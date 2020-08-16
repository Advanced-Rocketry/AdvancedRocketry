package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.ZUtils;

public class TileWarpCore extends TileMultiBlock {
	public TileWarpCore() {
		super(AdvancedRocketryTileEntityType.TILE_WARP_CORE);
	}

	private SpaceStationObject station;

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

	private SpaceStationObject getSpaceObject() {
		if(station == null && ZUtils.getDimensionIdentifier(world) == ARConfiguration.getCurrentConfig().spaceDimId) {
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
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {
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
				if(stack != null && ItemTags.getCollection().getOwningTags(stack.getItem()).stream().anyMatch(value -> { return value.getPath().equalsIgnoreCase("gemdilithium"); }) ) {
					int stackSize = stack.getCount();
					if(!world.isRemote)
						amt = getSpaceObject().addFuel(ARConfiguration.getCurrentConfig().fuelPointsPerDilithium*stack.getCount());
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
