package zmaster587.advancedRocketry.tile;

import java.util.LinkedList;
import java.util.List;

import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleLimitedSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArmor;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.util.EmbeddedInventory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileSuitWorkStation extends TileEntity implements IModularInventory, IInventory {

	EmbeddedInventory inventory;

	public TileSuitWorkStation() {
		inventory = new EmbeddedInventory(getSizeInventory());
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModuleSlotArray(15, 15, this, 0, 1));
		modules.add(new ModuleLimitedSlotArray(15, 35, this, 1, 5));
		modules.add(new ModuleSlotArmor(150, 8, player));

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.suitWorkStation.name";
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
	public ItemStack getStackInSlot(int slot) {
		if(slot == 0) {
			return inventory.getStackInSlot(slot);
		}
		else if(inventory.getStackInSlot(0) != null && inventory.getStackInSlot(0).getItem() instanceof IModularArmor) {
			return ((IModularArmor)inventory.getStackInSlot(0).getItem()).getComponentInSlot(inventory.getStackInSlot(0), slot-1);
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		if(slot == 0) {
			return inventory.decrStackSize(slot, amt);
		}
		else if(inventory.getStackInSlot(0) != null && inventory.getStackInSlot(0).getItem() instanceof IModularArmor) {

			return ((IModularArmor)inventory.getStackInSlot(0).getItem()).removeComponent(worldObj, inventory.getStackInSlot(0), slot - 1);
		}
		return null;

		//return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack contents) {
		if(slot == 0) {
			if(contents != null && contents.getItem() instanceof IModularArmor) {
				List<ItemStack> list = ((IModularArmor)contents.getItem()).getComponents(contents);
				for(int i = 0; i < getSizeInventory() -1; i++) {
					if(i >= list.size())
						inventory.setInventorySlotContents(i+1, null);
					else
						inventory.setInventorySlotContents(i+1, list.get(i));
				}
			}
			inventory.setInventorySlotContents(slot, contents);
		}
		else if(inventory.getStackInSlot(0) != null && inventory.getStackInSlot(0).getItem() instanceof IModularArmor) {
			//TODO
			if(contents != null && contents.getItem() instanceof IArmorComponent)
				((IModularArmor)inventory.getStackInSlot(0).getItem()).addArmorComponent(worldObj, inventory.getStackInSlot(0), contents, slot - 1);
			else if(contents != null) {
				//If somehow an item gets forced into the slot 
				
			} else
				((IModularArmor)inventory.getStackInSlot(0).getItem()).removeComponent(worldObj, inventory.getStackInSlot(0), slot - 1);
		}
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
	public String getInventoryName() {
		return getModularInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {
		inventory.openInventory();
	}

	@Override
	public void closeInventory() {
		inventory.closeInventory();	
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		
		return (slot == 0 && stack.getItem() instanceof IModularArmor) || (inventory.getStackInSlot(0) != null && inventory.getStackInSlot(0).getItem() instanceof IModularArmor && slot != 0 && stack.getItem() instanceof IArmorComponent && ((IArmorComponent)stack.getItem()).isAllowedInSlot(stack, ((ItemArmor)inventory.getStackInSlot(0).getItem()).armorType));
	}

}
