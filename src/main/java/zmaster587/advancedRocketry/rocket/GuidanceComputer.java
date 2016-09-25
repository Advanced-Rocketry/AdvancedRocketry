package zmaster587.advancedRocketry.rocket;

import java.util.LinkedList;
import java.util.List;

import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

public class GuidanceComputer implements IInventory, IModularInventory {

	ItemStack inv;
	private static final String destinationSlot = "destinationSlot";
	
	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		
		if(inv == null)
			return null;
		else {
			ItemStack stack;
			stack = inv.splitStack(amount);
			if(inv.stackSize == 0)
				inv = null;
			return stack;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv = stack;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.guidanceComputer.name";
	}
	
	@Override
	public String getName() {
		return getModularInventoryName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		if(inv != null) {
			NBTTagCompound itemNbt = new NBTTagCompound();
			inv.writeToNBT(itemNbt);
			nbt.setTag(destinationSlot, itemNbt);
		}
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey(destinationSlot))
			ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(destinationSlot));
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		
		modules.add(new ModuleSlotArray(8, 17, this, 0, 1));
		
		return modules;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = inv;
		inv = null;
		return stack;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		
	}
}
