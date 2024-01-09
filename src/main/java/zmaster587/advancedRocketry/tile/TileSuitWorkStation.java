package zmaster587.advancedRocketry.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.util.EmbeddedInventory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedList;
import java.util.List;

public class TileSuitWorkStation extends TileEntity implements IModularInventory, IInventory {

	private EmbeddedInventory inventory;
	private ModuleTexturedLimitedSlotArray[] slotArray;

	public TileSuitWorkStation() {
		inventory = new EmbeddedInventory(getSizeInventory());
		slotArray = new ModuleTexturedLimitedSlotArray[6];
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();

		modules.add(new ModuleSlotArray(15, 15, this, 0, 1));
		
		for(int i = 0; i < 6; i++) {
			slotArray[i] = new ModuleTexturedLimitedSlotArray(15 + i*18, 35, this, i + 1, i + 2, null);
			modules.add(slotArray[i]);
		}
		
		modules.add(new ModuleSlotArmor(150, 8, player));

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return AdvancedRocketryBlocks.blockSuitWorkStation.getLocalizedName();
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot) {
		if(slot == 0) {
			return inventory.getStackInSlot(slot);
		}
		else if(!inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(0).getItem() instanceof IModularArmor && ((IModularArmor)inventory.getStackInSlot(0).getItem()).getNumSlots(inventory.getStackInSlot(0)) > slot-1) {
			return ((IModularArmor)inventory.getStackInSlot(0).getItem()).getComponentInSlot(inventory.getStackInSlot(0), slot-1);
		}
		return ItemStack.EMPTY;
	}

	@Override
	@NotNull
	public ItemStack decrStackSize(int slot, int amt) {
		if(slot == 0) {
			return inventory.decrStackSize(slot, amt);
		}
		else if(!inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(0).getItem() instanceof IModularArmor) {

			return ((IModularArmor)inventory.getStackInSlot(0).getItem()).removeComponent(world, inventory.getStackInSlot(0), slot - 1);
		}
		return ItemStack.EMPTY;

		//return inventory.decrStackSize(i, j);
	}

	@Override
	public void setInventorySlotContents(int slot, @NotNull ItemStack contents) {
		if(slot == 0) {
			if(!contents.isEmpty() && contents.getItem() instanceof IModularArmor) {
				
				for(ModuleTexturedLimitedSlotArray slot2 : slotArray) {
					slot2.setEnabled(false);
					slot2.setResource(null);
				}
				List<ItemStack> list = ((IModularArmor)contents.getItem()).getComponents(contents);
				for(int i = 0; i < getSizeInventory() -1; i++) {
					if(i >= list.size())
						inventory.setInventorySlotContents(i, ItemStack.EMPTY);
					else
						inventory.setInventorySlotContents(i, list.get(i));
				}
				
				for(int i = 0; i < ((IModularArmor)contents.getItem()).getNumSlots(contents); i++) {
					slotArray[i].setEnabled(true);
					slotArray[i].setResource(((IModularArmor)contents.getItem()).getResourceForSlot(i));
				}
			}
			else {
				for(ModuleTexturedLimitedSlotArray slot2 : slotArray) {
					if(slot2 != null) {
						slot2.setEnabled(false);
						slot2.setResource(null);
					}
				}
			}
			inventory.setInventorySlotContents(slot, contents);
			
		}
		else if(!inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(0).getItem() instanceof IModularArmor &&
			slot - 1 < ((IModularArmor)inventory.getStackInSlot(0).getItem()).getNumSlots(inventory.getStackInSlot(0))) {
			//TODO
			if(!contents.isEmpty() && contents.getItem() instanceof IArmorComponent)
				((IModularArmor)inventory.getStackInSlot(0).getItem()).addArmorComponent(world, inventory.getStackInSlot(0), contents, slot - 1);
			else if(!contents.isEmpty()) {
				//If somehow an item gets forced into the slot 
				
			} else
				((IModularArmor)inventory.getStackInSlot(0).getItem()).removeComponent(world, inventory.getStackInSlot(0), slot - 1);
		}
	}

	@Override
	@NotNull
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		inventory.writeToNBT(nbt);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		inventory.readFromNBT(nbt);
	}
	
	@Override
	@NotNull
	public String getName() {
		return getModularInventoryName();
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(@Nullable EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
	
	@Override
	public void openInventory(@Nullable EntityPlayer player) {
		inventory.openInventory(player);
	}

	@Override
	public void closeInventory(@Nullable EntityPlayer player) {
		inventory.closeInventory(player);	
	}

	@Override
	public boolean isItemValidForSlot(int slot, @NotNull ItemStack stack) {
		
		return (slot == 0 && stack.getItem() instanceof IModularArmor) || 
				(!inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(0).getItem() instanceof IModularArmor && slot != 0 && stack.getItem() instanceof IArmorComponent && 
				((IArmorComponent)stack.getItem()).isAllowedInSlot(stack, ((ItemArmor)inventory.getStackInSlot(0).getItem()).armorType) && slot - 1 < ((IModularArmor)inventory.getStackInSlot(0).getItem()).getNumSlots(inventory.getStackInSlot(0))
				&& ((IModularArmor)inventory.getStackInSlot(0).getItem()).isItemValidForSlot(stack, slot - 1) );
	}

	@Override
	@NotNull
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return ItemStack.EMPTY;
	}

	@Override
	public int getField(int id) {
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inventory.getFieldCount();
	}

	@Override
	public void clear() {
		inventory.clear();
		
	}

}
