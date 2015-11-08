package zmaster587.advancedRocketry.tile.multiblock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleImage;
import zmaster587.advancedRocketry.Inventory.modules.ModuleLiquidIndicator;
import zmaster587.advancedRocketry.Inventory.modules.ModuleSlotArray;
import zmaster587.advancedRocketry.api.FuelRegistry;
import zmaster587.advancedRocketry.api.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.util.EmbeddedInventory;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.IconResource;

public class TileFluidHatch extends TilePointer implements IFluidHandler, IModularInventory, IInventory {

	private FluidTank fluidTank;
	private EmbeddedInventory inventory;
	private boolean outputOnly;

	public TileFluidHatch() {
		fluidTank = new FluidTank(16000);
		inventory = new EmbeddedInventory(2);
	}

	public TileFluidHatch(boolean outputOnly) {
		this();

		this.outputOnly = outputOnly;
	}

	public boolean isOutputOnly() {
		return outputOnly;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(doFill && this.hasMaster() && this.getMasterBlock() instanceof TileMultiblockMachine)
				((TileMultiblockMachine)this.getMasterBlock()).onInventoryUpdated();
		if(outputOnly && from != ForgeDirection.UNKNOWN)
			return 0;
		return fluidTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {

		if(resource.isFluidEqual(fluidTank.getFluid()))
		return fluidTank.drain(resource.amount, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return fluidTank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return outputOnly;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { fluidTank.getInfo()};
	}

	@Override
	public List<ModuleBase> getModules() {
		List<ModuleBase> list = new ArrayList<ModuleBase>();

		list.add(new ModuleSlotArray(45, 18, this, 0, 1));
		list.add(new ModuleSlotArray(45, 54, this, 1, 2));
		list.add(new ModuleImage(44, 35, new IconResource(194, 0, 18, 18, CommonResources.genericBackground)));
		list.add(new ModuleLiquidIndicator(27, 18, this));

		return list;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.liquidHatch.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
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
		useBucket(0, getStackInSlot(0));
	}

	@Override
	public String getInventoryName() {
		return getModularInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
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
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setBoolean("outputOnly", outputOnly);
		inventory.writeToNBT(nbt);
		fluidTank.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		outputOnly = nbt.getBoolean("outputOnly");
		inventory.readFromNBT(nbt);
		fluidTank.readFromNBT(nbt);
	}

	//Yes i was lazy
	//TODO: make better
	private boolean useBucket( int slot, ItemStack stack) {

		if(FluidContainerRegistry.isFilledContainer(stack)) {
				if(!outputOnly && slot == 0 && fluidTank.getFluidAmount() + FluidContainerRegistry.getContainerCapacity(stack) <= fluidTank.getCapacity()) {
					ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(stack);

					if(emptyContainer != null && getStackInSlot(1) == null || (emptyContainer.isItemEqual(getStackInSlot(1)) && getStackInSlot(1).stackSize < getStackInSlot(1).getMaxStackSize())) {
						fluidTank.fill(FluidContainerRegistry.getFluidForFilledItem(stack), true);

						if(getStackInSlot(1) == null)
							inventory.setInventorySlotContents(1, emptyContainer);
						else
							getStackInSlot(1).stackSize++;
						decrStackSize(0, 1);
						return true;
					}
				}
		}
		else if(FluidContainerRegistry.isContainer(stack)) {
			if(slot == 0 && fluidTank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
				ItemStack fullContainer = FluidContainerRegistry.fillFluidContainer(fluidTank.drain(FluidContainerRegistry.BUCKET_VOLUME, false), stack);

				
				if(fullContainer != null && (getStackInSlot(1) == null || (fullContainer.isItemEqual(getStackInSlot(1)) && getStackInSlot(1).stackSize < getStackInSlot(1).getMaxStackSize())) ) {
					fluidTank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);

					if(getStackInSlot(1) == null)
						inventory.setInventorySlotContents(1, fullContainer);
					else
						getStackInSlot(1).stackSize++;
					decrStackSize(0, 1);
					return true;
				}
			}
		}

		return false;
	}
}
