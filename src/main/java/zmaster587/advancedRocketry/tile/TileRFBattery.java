package zmaster587.advancedRocketry.tile;

import cofh.api.energy.EnergyStorage;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TileEntityPointer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileRFBattery extends TileEntityPointer implements IUniversalEnergy, IMultiblock, IInventory {

	EnergyStorage storage;
	int teir;
	
	public TileRFBattery() {
		
	}
	
	public TileRFBattery(int teir) {
		this.teir = teir;
		storage = new EnergyStorage(getMaxEnergy(teir), getMaxDrainRate(teir));
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
		
		storage = new EnergyStorage(getMaxEnergy(teir), getMaxDrainRate(teir));
		storage.readFromNBT(nbt);
	}
	
	@Override
	public int extractEnergy(ForgeDirection dir, int amt, boolean sim) {
		return storage.extractEnergy(amt, sim);
	}

	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(ForgeDirection arg0, int maxReceive, boolean simulate) {
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
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
	public String getInventoryName() {
		return "RF Storage Box";
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

}
