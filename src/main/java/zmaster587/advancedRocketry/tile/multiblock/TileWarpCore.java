package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.util.ZUtils;
<<<<<<< HEAD
=======

import javax.annotation.Nonnull;
>>>>>>> origin/feature/nuclearthermalrockets

public class TileWarpCore extends TileMultiBlock {
	public TileWarpCore() {
		super(AdvancedRocketryTileEntityType.TILE_WARP_CORE);
	}

	private SpaceStationObject station;

	public static final Object[][][] structure = { 
		{{new ResourceLocation("advancedrocketry", "warpcorerim"), new ResourceLocation("advancedrocketry", "warpcorerim"), new ResourceLocation("advancedrocketry", "warpcorerim")},
			{new ResourceLocation("advancedrocketry", "warpcorerim"), 'I', new ResourceLocation("advancedrocketry", "warpcorerim")},
			{new ResourceLocation("advancedrocketry", "warpcorerim"), new ResourceLocation("advancedrocketry", "warpcorerim"), new ResourceLocation("advancedrocketry", "warpcorerim")}},

			{{null, new BlockMeta(LibVulpesBlocks.blockStructureBlock), null},
				{new BlockMeta(LibVulpesBlocks.blockStructureBlock), new ResourceLocation("advancedrocketry", "warpcore"), new BlockMeta(LibVulpesBlocks.blockStructureBlock)},
				{null, new BlockMeta(LibVulpesBlocks.blockStructureBlock), null}},
			
				{{new ResourceLocation("advancedrocketry", "warpcorerim"), 'c', new ResourceLocation("advancedrocketry", "warpcorerim")},
					{new ResourceLocation("advancedrocketry", "warpcorerim"), new ResourceLocation("advancedrocketry", "warpcore"), new ResourceLocation("advancedrocketry", "warpcorerim")},
					{new ResourceLocation("advancedrocketry", "warpcorerim"), new ResourceLocation("advancedrocketry", "warpcorerim"), new ResourceLocation("advancedrocketry", "warpcorerim")}},

	};

	private SpaceStationObject getSpaceObject() {
<<<<<<< HEAD
		if(station == null && ARConfiguration.GetSpaceDimId().equals(ZUtils.getDimensionIdentifier(world))) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(object instanceof SpaceStationObject)
				station = (SpaceStationObject) object;
=======
		if(station == null && world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) {
			ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObject instanceof SpaceStationObject)
				station = (SpaceStationObject) spaceObject;
>>>>>>> origin/feature/nuclearthermalrockets
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
		
<<<<<<< HEAD
		if(getSpaceObject() == null || (getSpaceObject().getMaxFuelAmount() - getSpaceObject().getFuelAmount() < ARConfiguration.getCurrentConfig().fuelPointsPerDilithium.get()))
=======
		if(getSpaceObject() == null || (getSpaceObject().getMaxFuelAmount() - getSpaceObject().getFuelAmount()) < ARConfiguration.getCurrentConfig().fuelPointsPerDilithium)
>>>>>>> origin/feature/nuclearthermalrockets
			return;
		for(IInventory inv : itemInPorts) {
			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i).copy();
				stack.setCount(1);
				int amt = 0;
<<<<<<< HEAD
				if(!stack.isEmpty() && ItemTags.getCollection().getOwningTags(stack.getItem()).stream().anyMatch(value -> { return value.getPath().equalsIgnoreCase("gems/dilithium"); }) ) {
					int stackSize = stack.getCount();
=======
				if(!stack.isEmpty() && ZUtils.isItemInOreDict(stack, "gemDilithium")) {
>>>>>>> origin/feature/nuclearthermalrockets
					if(!world.isRemote)
						amt = getSpaceObject().addFuel(ARConfiguration.getCurrentConfig().fuelPointsPerDilithium.get());
					inv.decrStackSize(i, amt/ARConfiguration.getCurrentConfig().fuelPointsPerDilithium.get());
					inv.markDirty();
					
					//If full
					if(getSpaceObject().getMaxFuelAmount() - getSpaceObject().getFuelAmount() < ARConfiguration.getCurrentConfig().fuelPointsPerDilithium.get())
						return;
				}
			}
		}
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.warpcore";
	}
	
	@Override
	@Nonnull
	public AxisAlignedBB getRenderBoundingBox() {
		
		return new AxisAlignedBB(pos.add(-2,-2,-2),pos.add(2,2,2));
	}

}
