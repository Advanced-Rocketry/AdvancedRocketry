package zmaster587.advancedRocketry.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.advancedRocketry.world.util.WorldDummy;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.util.IAdjBlockUpdate;

public class TileFluidTank extends TileFluidHatch implements IAdjBlockUpdate {

	private long lastUpdateTime;
	private static final int MAX_UPDATE = 20;
	private boolean fluidChanged;

	public TileFluidTank() {
		super();
		fluidChanged = false;
	}

	public TileFluidTank(int i) {
		super(i);
		fluidChanged = false;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}
	
	private void checkForUpdate() {
		if(fluidChanged && worldObj instanceof WorldDummy || worldObj.getTotalWorldTime() - lastUpdateTime > MAX_UPDATE) {
			this.markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			lastUpdateTime = worldObj.getTotalWorldTime();
			fluidChanged = false;
		}
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		IFluidHandler handler = this.getFluidTankInDirection(ForgeDirection.DOWN);
		int amt = 0;

		if(handler != null) {
			amt = handler.fill(from, resource, doFill);
		}
		//Copy to avoid modifiying the passed one
		FluidStack resource2 = resource.copy();
		resource2.amount -= amt;
		if(resource2.amount > 0)
			amt += super.fill(from, resource2, doFill);
		
		if(amt > 0 && doFill)
			fluidChanged = true;	
		
		checkForUpdate();
		
		return amt;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		IFluidHandler handler = this.getFluidTankInDirection(ForgeDirection.UP);

		FluidStack stack = null;
		if(handler != null && handler.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid != null && 
				fluidTank.getFluid() != null && fluidTank.getFluid().getFluidID() ==
				handler.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid.getFluidID()) {

			stack = handler.drain(from, maxDrain, doDrain);
		}
		
		if(stack != null)
			return stack;

		FluidStack stack2 = super.drain(from, maxDrain - (stack != null ? stack.amount : 0), doDrain);

		if(stack != null && stack2 != null)
			stack2.amount += stack.amount;

		
		if(stack2 != null && doDrain) {
			fluidChanged = true;
		}
		checkForUpdate();
		
		
		return stack2;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if(this.fluidTank.getFluid() == null || resource.getFluidID() != this.fluidTank.getFluid().getFluidID())
			return null;

		return this.drain(from, resource.amount, doDrain);
	}

	public TileFluidTank getFluidTankInDirection(ForgeDirection direction) {
		TileEntity tile = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);

		if(tile instanceof TileFluidTank) {
			return ((TileFluidTank) tile);
		}
		return null;
	}

	@Override
	protected void writeToNBTHelper(NBTTagCompound nbtTagCompound) {
		super.writeToNBTHelper(nbtTagCompound);
		fluidTank.writeToNBT(nbtTagCompound);
	}

	@Override
	protected void readFromNBTHelper(NBTTagCompound nbtTagCompound) {
		super.readFromNBTHelper(nbtTagCompound);
		fluidTank.readFromNBT(nbtTagCompound);
	}

	@Override
	protected boolean useBucket(int slot, ItemStack stack) {
		boolean bucketUsed = super.useBucket(slot, stack);

		if(bucketUsed) {
			IFluidHandler handler = getFluidTankInDirection(ForgeDirection.DOWN);
			if(handler != null) {
				FluidStack othertank = handler.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid;
				if(othertank == null || (othertank.amount < handler.getTankInfo(ForgeDirection.UNKNOWN)[0].capacity))
					fluidTank.drain(handler.fill(ForgeDirection.UNKNOWN, fluidTank.getFluid(), true),true);
			}
		}

		return bucketUsed;
	}

	@Override
	public void onAdjacentBlockUpdated() {
		TileFluidTank tank = getFluidTankInDirection(ForgeDirection.UP);

		if(tank != null && tank.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid != null) {
			if(fluidTank.getFluid() == null) {
				fluidTank.fill(tank.fluidTank.drain(fluidTank.getCapacity(), true), true);
			}
			else if(tank.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid.getFluidID() == fluidTank.getFluid().getFluidID()) {
				fluidTank.fill(tank.drain(ForgeDirection.UNKNOWN, fluidTank.getCapacity() - fluidTank.getFluidAmount(), false), true);
				tank.fluidTank.drain(fluidTank.getCapacity() - fluidTank.getFluidAmount(), true);
			}

			this.markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
}
