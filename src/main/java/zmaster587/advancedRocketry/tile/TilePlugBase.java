package zmaster587.advancedRocketry.tile;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModulePower;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.UniversalBattery;

public abstract class TilePlugBase extends TilePointer implements IModularInventory, IUniversalEnergy, IMultiblock, IInventory {

	protected UniversalBattery storage;
	protected int teir;
	
	public TilePlugBase() {
		
	}
	
	public TilePlugBase(int teir) {
		this.teir = teir;
		storage = new UniversalBattery(getMaxEnergy(teir));
	}
	
	protected int getMaxEnergy(int teir) {
		return (int)Math.pow(10,teir)*10000;
	}
	
	protected int getMaxDrainRate(int teir) {
		return 250*(int)Math.pow(2, teir);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("teir", teir);
		storage.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		teir = nbt.getInteger("teir");
		
		storage = new UniversalBattery(getMaxEnergy(teir));
		storage.readFromNBT(nbt);
	}

	@Override
	public void setEnergyStored(int amt) {
		storage.setEnergyStored(amt);
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {
		
	}

	@Override
	public void closeInventory() {
		
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

	
	@Override
	public List<ModuleBase> getModules(int ID) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModulePower(18, 20,this));
		return modules;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public int extractEnergy(int amt, boolean simulate) {
		return storage.extractEnergy(amt, simulate);
	}

	@Override
	public int getEnergyStored() {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return storage.getMaxEnergyStored();
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		return  storage.acceptEnergy(amt, simulate);
	}

}
