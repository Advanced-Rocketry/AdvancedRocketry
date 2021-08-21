package zmaster587.advancedRocketry.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.world.util.WorldDummy;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;

import javax.annotation.Nonnull;

public class TileFluidTank extends TileFluidHatch {

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
		if(fluidChanged && world instanceof WorldDummy || world.getTotalWorldTime() - lastUpdateTime > MAX_UPDATE) {
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
			lastUpdateTime = world.getTotalWorldTime();
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
		
		if(resource == null)
			return 0;

		TileFluidTank handler2 = this.getFluidTankInDirection(EnumFacing.UP);
		
		//Move up, check if we can fill there, do top down
		if(handler2 != null && handler2.canFill(resource))
		{
			return handler2.fill(resource, doFill);
		}
		return fillInternal2(resource, doFill);
	}
	
	private int fillInternal2(FluidStack resource, boolean doFill) {
		
		TileFluidTank handler = this.getFluidTankInDirection(EnumFacing.DOWN);
		
		int amt = 0;

		if(handler != null) {
			amt = handler.fillInternal2(resource, doFill);
		}
		//Copy to avoid modifying the passed one
		FluidStack resource2 = resource.copy();
		resource2.amount -= amt;
		if(resource2.amount > 0)
			amt += super.fill(resource2, doFill);
		
		if(amt > 0 && doFill)
			fluidChanged = true;
		
		checkForUpdate();
		
		return amt;
	}
	
	@Override
	public String getModularInventoryName() {
		return AdvancedRocketryBlocks.blockPressureTank.getLocalizedName();
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		IFluidHandler handler = this.getFluidTankInDirection(EnumFacing.UP);

		FluidStack fStack = null;
		if(handler != null && handler.getTankProperties()[0].getContents() != null && 
				fluidTank.getFluid() != null && fluidTank.getFluid().getFluid() ==
				handler.getTankProperties()[0].getContents().getFluid()) {

			fStack = handler.drain(maxDrain, doDrain);
		}
		if(fStack != null)
			return fStack;

		FluidStack fStack2 = super.drain(maxDrain, doDrain);

		if(fStack2 != null && doDrain) {
			fluidChanged = true;
		}
		checkForUpdate();
		
		
		return fStack2;
	}

	@Override
	public FluidStack drain(FluidStack resource,
			boolean doDrain) {
		if(this.fluidTank.getFluid() == null || resource.getFluid() != this.fluidTank.getFluid().getFluid())
			return null;

		return this.drain(resource.amount, doDrain);
	}

	public TileFluidTank getFluidTankInDirection(EnumFacing direction) {
		TileEntity tile = world.getTileEntity(pos.offset(direction));

		if(tile instanceof TileFluidTank) {
			return ((TileFluidTank) tile);
		}
		return null;
	}
	
	private boolean canFill(FluidStack fStack)
	{
		FluidStack fStack2 = fluidTank.getFluid();
		
		return fStack2 == null || (fStack2.getFluid() == fStack.getFluid());
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
	protected boolean useBucket(int slot, @Nonnull ItemStack stack) {
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

	public void onAdjacentBlockUpdated(EnumFacing dir) {
		if(dir != EnumFacing.DOWN)
			return;
		
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
