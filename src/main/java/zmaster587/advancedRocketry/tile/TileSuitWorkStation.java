package zmaster587.advancedRocketry.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.libVulpes.api.IArmorComponent;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.util.EmbeddedInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class TileSuitWorkStation extends TileEntity implements IModularInventory, IInventory {

	private EmbeddedInventory inventory;
	private ModuleTexturedLimitedSlotArray[] slotArray;

	public TileSuitWorkStation() {
		super(AdvancedRocketryTileEntityType.TILE_WORK_STATION);
		inventory = new EmbeddedInventory(getSizeInventory());
		slotArray = new ModuleTexturedLimitedSlotArray[6];
	}

	@Override
<<<<<<< HEAD
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
=======
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();
>>>>>>> origin/feature/nuclearthermalrockets

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
		return "block.advancedrocketry.suitworkstation";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	@Nonnull
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
	@Nonnull
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
	public void setInventorySlotContents(int slot, @Nonnull ItemStack contents) {
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
<<<<<<< HEAD
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
=======
	@Nonnull
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
>>>>>>> origin/feature/nuclearthermalrockets
		
		inventory.write(nbt);
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		
		inventory.readFromNBT(nbt);
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
		return true;
	}
>>>>>>> origin/feature/nuclearthermalrockets

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
<<<<<<< HEAD
	public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
=======
	public boolean isUsableByPlayer(@Nullable EntityPlayer p_70300_1_) {
>>>>>>> origin/feature/nuclearthermalrockets
		return true;
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
	
	@Override
<<<<<<< HEAD
	public void openInventory(PlayerEntity player) {
=======
	public void openInventory(@Nullable EntityPlayer player) {
>>>>>>> origin/feature/nuclearthermalrockets
		inventory.openInventory(player);
	}

	@Override
<<<<<<< HEAD
	public void closeInventory(PlayerEntity player) {
=======
	public void closeInventory(@Nullable EntityPlayer player) {
>>>>>>> origin/feature/nuclearthermalrockets
		inventory.closeInventory(player);	
	}

	@Override
<<<<<<< HEAD
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
=======
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		
>>>>>>> origin/feature/nuclearthermalrockets
		return (slot == 0 && stack.getItem() instanceof IModularArmor) || 
				(!inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(0).getItem() instanceof IModularArmor && slot != 0 && stack.getItem() instanceof IArmorComponent && 
				((IArmorComponent)stack.getItem()).isAllowedInSlot(stack, ((ArmorItem)inventory.getStackInSlot(0).getItem()).getEquipmentSlot()) && slot - 1 < ((IModularArmor)inventory.getStackInSlot(0).getItem()).getNumSlots(inventory.getStackInSlot(0))
				&& ((IModularArmor)inventory.getStackInSlot(0).getItem()).isItemValidForSlot(stack, slot - 1) );
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return ItemStack.EMPTY;
	}

	@Override
	public void clear() {
		inventory.clear();
		
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}

}
