package zmaster587.advancedRocketry.rocket;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
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
			stack = inv.split(amount);
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
<<<<<<< HEAD
=======
	
	@Override
	@Nonnull
	public String getName() {
		return getModularInventoryName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
>>>>>>> origin/feature/nuclearthermalrockets

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		
	}

	@Override
	public boolean isUsableByPlayer(@Nullable PlayerEntity player) {
		return true;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, @Nonnull ItemStack p_94041_2_) {
		return true;
	}
	
	public void writeToNBT(CompoundNBT nbt) {
		if(!inv.isEmpty()) {
			CompoundNBT itemNbt = new CompoundNBT();
			inv.write(itemNbt);
			nbt.put(destinationSlot, itemNbt);
		}
	}
	
	public void readFromNBT(CompoundNBT nbt) {
		//if(nbt.contains(destinationSlot))
			//ItemStack.loadItemStackFromNBT(nbt.getCompound(destinationSlot));
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();
		
		modules.add(new ModuleSlotArray(8, 17, this, 0, 1));
		
		return modules;
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
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
	public void clear() {
		
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return GuiHandler.guiId.MODULAR;
	}
}
