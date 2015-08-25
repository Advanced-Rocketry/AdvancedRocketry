package zmaster587.advancedRocketry.tile.multiblock;

import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.tile.TileEntityPointer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileInventoryHatch extends TileEntityPointer implements ISidedInventory, IModularInventory {

	protected ItemStack inv[];

	public TileInventoryHatch() {
	}

	public TileInventoryHatch(int invSize) {
		inv = new ItemStack[invSize];
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("size", inv.length);

		NBTTagList list = new NBTTagList();
		for(int i = 0; i < inv.length; i++)
		{
			ItemStack stack = inv[i];

			if(stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)(i));
				stack.writeToNBT(tag);
				list.appendTag(tag);
			}
		}
		
		nbt.setTag("outputItems", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		NBTTagList list = nbt.getTagList("outputItems", 10);
		inv = new ItemStack[nbt.getInteger("size")];

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) list.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = inv[slot];
		if(stack != null) {
			ItemStack stack2 = stack.splitStack(amt);
			if(stack.stackSize == 0)
				inv[slot] = null;
			
			return stack2;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;
		TileEntity master = getMasterBlock();
		if(master != null && master instanceof TileMultiBlockMachine)
			((TileMultiBlockMachine)getMasterBlock()).onInventoryUpdated();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
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
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {

		int array[] = new int[inv.length];

		for(int i = 0; i < inv.length; i++) {
			array[i] = i;
		}
		return array;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		return true;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		return true;
	}

	@Override
	public List<ModuleBase> getModules() {
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

}
