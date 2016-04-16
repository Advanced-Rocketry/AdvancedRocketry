package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.material.MaterialRegistry;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.libVulpes.block.BlockMeta;

public class TileWarpCore extends TileMultiBlock {
	private SpaceObject station;

	public static final Object[][][] structure = { 
		{{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)},
			{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), 'I', new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)},
			{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)}},

			{{null, new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), null},
				{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(Blocks.gold_block), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)},
				{null, new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), null}},

				{{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), 'c', new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)}, 
					{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(Blocks.gold_block), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)},
					{new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock), new BlockMeta(AdvancedRocketryBlocks.blockStructureBlock)}},

	};

	private SpaceObject getSpaceObject() {
		if(station == null && worldObj.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(xCoord, zCoord);
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
	public boolean shouldHideBlock(World world, int x, int y, int z, Block tile) {
		return x == xCoord && y == yCoord && z == zCoord;
	}
	
	@Override
	public void onInventoryUpdated() {
		//Needs completion
		if(itemInPorts.isEmpty() /*&& !worldObj.isRemote*/) {
			attemptCompleteStructure();
		}
		
		if(getSpaceObject() == null || getSpaceObject().getFuelAmount() == getSpaceObject().getMaxFuelAmount())
			return;
		for(IInventory inv : itemInPorts) {
			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				int amt = 0;
				if(stack != null && OreDictionary.itemMatches(MaterialRegistry.getItemStackFromMaterialAndType(MaterialRegistry.Materials.DILITHIUM, MaterialRegistry.AllowedProducts.CRYSTAL), stack, false)) {
					int stackSize = stack.stackSize;
					if(!worldObj.isRemote)
						amt = getSpaceObject().addFuel(10*stack.stackSize);
					else
						amt = Math.min(getSpaceObject().getFuelAmount() + 10*stack.stackSize, getSpaceObject().getMaxFuelAmount()) - getSpaceObject().getFuelAmount();//
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
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -2,yCoord -2, zCoord -2, xCoord + 2, yCoord + 2, zCoord + 2);
	}

}
