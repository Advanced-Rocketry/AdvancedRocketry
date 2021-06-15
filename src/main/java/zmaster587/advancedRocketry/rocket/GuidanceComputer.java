package zmaster587.advancedRocketry.rocket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class GuidanceComputer implements IInventory, IModularInventory {

	private ItemStack inv;
	private static final String destinationSlot = "destinationSlot";
	
	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return inv;
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int slot, int amount) {
		
		if(inv.isEmpty())
			return ItemStack.EMPTY;
		else {
			ItemStack stack;
			stack = inv.splitStack(amount);
			if(inv.getCount() == 0)
				inv = ItemStack.EMPTY;
			return stack;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		inv = stack;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.guidanceComputer.name";
	}
	
	@Override
	@Nonnull
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
	public boolean isUsableByPlayer(@Nullable EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, @Nonnull ItemStack p_94041_2_) {
		return true;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		if(!inv.isEmpty()) {
			NBTTagCompound itemNbt = new NBTTagCompound();
			inv.writeToNBT(itemNbt);
			nbt.setTag(destinationSlot, itemNbt);
		}
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		//if(nbt.hasKey(destinationSlot))
			//ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(destinationSlot));
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();
		
		modules.add(new ModuleSlotArray(8, 17, this, 0, 1));
		
		return modules;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	@Nullable
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = inv;
		inv = ItemStack.EMPTY;
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

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}
}
