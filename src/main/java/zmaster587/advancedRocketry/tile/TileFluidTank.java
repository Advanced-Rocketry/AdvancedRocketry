package zmaster587.advancedRocketry.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import zmaster587.advancedRocketry.world.util.WorldDummy;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.util.IAdjBlockUpdate;

public class TileFluidTank extends TileFluidHatch implements IAdjBlockUpdate {

	private long lastUpdateTime;
	private static final int MAX_UPDATE = 5;
	private boolean fluidChanged;

	public TileFluidTank() {
		super();
		fluidChanged = false;
	}

	public TileFluidTank(int i) {
		super(i);
		fluidChanged = false;
	}

	private void checkForUpdate() {
		if(fluidChanged && worldObj instanceof WorldDummy || worldObj.getTotalWorldTime() - lastUpdateTime > MAX_UPDATE) {
			this.markDirty();
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 2);
			lastUpdateTime = worldObj.getTotalWorldTime();
			fluidChanged = false;
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
	
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		IFluidHandler handler = this.getFluidTankInDirection(EnumFacing.DOWN);
		int amt = 0;

		if(handler != null) {
			amt = handler.fill(resource, doFill);
		}
		//Copy to avoid modifiying the passed one
		FluidStack resource2 = resource.copy();
		resource2.amount -= amt;
		if(resource2.amount > 0)
			amt += super.fill(resource2, doFill);
		
		if(amt > 0 && doFill)
			fluidChanged = true;	
		
		//checkForUpdate();
		
		return amt;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		IFluidHandler handler = this.getFluidTankInDirection(EnumFacing.UP);

		FluidStack stack = null;
		if(handler != null && handler.getTankProperties()[0].getContents() != null && 
				fluidTank.getFluid() != null && fluidTank.getFluid().getFluid() ==
				handler.getTankProperties()[0].getContents().getFluid()) {

			stack = handler.drain(maxDrain, doDrain);
		}

		FluidStack stack2 = super.drain(maxDrain - (stack != null ? stack.amount : 0), doDrain);

		if(stack != null && stack2 != null)
			stack2.amount += stack.amount;

		
		if(stack2 != null && doDrain) {
			fluidChanged = true;
		}
		checkForUpdate();
		
		
		return stack2;
	}

	@Override
	public FluidStack drain(FluidStack resource,
			boolean doDrain) {
		if(this.fluidTank.getFluid() == null || resource.getFluid() != this.fluidTank.getFluid().getFluid())
			return null;

		return this.drain(resource.amount, doDrain);
	}

	public TileFluidTank getFluidTankInDirection(EnumFacing direction) {
		TileEntity tile = worldObj.getTileEntity(pos.offset(direction));

		if(tile instanceof TileFluidTank) {
			return ((TileFluidTank) tile);
		}
		return null;
	}

	@Override
	protected NBTTagCompound writeToNBTHelper(NBTTagCompound nbtTagCompound) {
		super.writeToNBTHelper(nbtTagCompound);
		fluidTank.writeToNBT(nbtTagCompound);
		return nbtTagCompound;
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
			IFluidHandler handler = getFluidTankInDirection(EnumFacing.DOWN);
			if(handler != null) {
				FluidStack othertank = handler.getTankProperties()[0].getContents();
				if(othertank == null || (othertank.amount < handler.getTankProperties()[0].getCapacity()))
					fluidTank.drain(handler.fill(fluidTank.getFluid(), true),true);
			}
		}

		return bucketUsed;
	}

	@Override
	public void onAdjacentBlockUpdated() {
		TileFluidTank tank = getFluidTankInDirection(EnumFacing.UP);

		if(tank != null && tank.getTankProperties()[0].getContents() != null) {
			if(fluidTank.getFluid() == null) {
				fluidTank.fill(tank.fluidTank.drain(fluidTank.getCapacity(), true), true);
			}
			else if(tank.getTankProperties()[0].getContents().getFluid() == fluidTank.getFluid().getFluid()) {
				fluidTank.fill(tank.drain(fluidTank.getCapacity() - fluidTank.getFluidAmount(), true), true);
				tank.fluidTank.drain(fluidTank.getCapacity() - fluidTank.getFluidAmount(), true);
			}

			this.markDirty();
		}
	}
}
