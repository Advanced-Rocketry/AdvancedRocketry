package zmaster587.advancedRocketry.tile.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;

public class TileDataProgrammer extends TileEntity implements IInventory {
	
	public TileDataProgrammer() {
		super(AdvancedRocketryTileEntityType.TILE_DATA_PROGRAMMER);
	}

	ItemStack dataCell;

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return dataCell;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return dataCell;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		
	}



	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return pos.distanceSq(new BlockPos(player.getPositionVec())) < 4096;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = dataCell;
		dataCell = null;
		return stack;
	}

	@Override
	public void clear() {
		
	}
	
}
