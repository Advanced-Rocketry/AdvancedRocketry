package zmaster587.advancedRocketry.tile.multiblock;

import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleSlotArray;
import zmaster587.advancedRocketry.util.EmbeddedInventory;
import zmaster587.libVulpes.tile.TilePointer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileInventoryHatch extends TilePointer implements ISidedInventory, IModularInventory {

	protected EmbeddedInventory inventory;

	public TileInventoryHatch() {
		inventory = new EmbeddedInventory(0);
	}

	public TileInventoryHatch(int invSize) {
		inventory = new EmbeddedInventory(invSize);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		inventory.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		inventory.readFromNBT(nbt);
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return inventory.decrStackSize(slot, amt);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
		if(this.hasMaster() && this.getMasterBlock() instanceof TileMultiBlock)
			((TileMultiBlock)this.getMasterBlock()).onInventoryUpdated();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inventory.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistance(xCoord, yCoord, zCoord) < 64;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		return inventory.getAccessibleSlotsFromSide(side);
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		return inventory.canInsertItem(p_102007_1_, p_102007_2_, p_102007_3_);
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		return inventory.canExtractItem(p_102008_1_, p_102008_2_, p_102008_3_);
	}

	@Override
	public List<ModuleBase> getModules(int ID) {
		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModuleSlotArray(8, 18, this, 0, this.getSizeInventory()));

		return modules;
	}

	@Override
	public String getInventoryName() {
		return getModularInventoryName();
	}

	@Override
	public String getModularInventoryName() {
		return null;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(!worldObj.isRemote) {
			TileEntity tile = getFinalPointedTile();
			if(tile instanceof TileMultiBlock) {
				((TileMultiBlock) tile).invalidateComponent(this);
			}
		}
	}

}
