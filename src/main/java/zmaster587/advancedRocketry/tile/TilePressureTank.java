package zmaster587.advancedRocketry.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.world.util.WorldDummy;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;

import javax.annotation.Nonnull;

public class TilePressureTank extends TileFluidHatch {

	private long lastUpdateTime;
	private static final int MAX_UPDATE = 5;
	private boolean fluidChanged;

	public TilePressureTank() {
		super(AdvancedRocketryTileEntityType.TILE_FLUID_TANK);
		fluidChanged = false;
	}

	public TilePressureTank(int i) {
		super(AdvancedRocketryTileEntityType.TILE_FLUID_TANK, i);
		fluidChanged = false;
	}

	private void checkForUpdate() {
		if(fluidChanged && world instanceof WorldDummy || world.getGameTime() - lastUpdateTime > MAX_UPDATE) {
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 2);
			lastUpdateTime = world.getGameTime();
			fluidChanged = false;
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getPos(), 0, getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		read(getBlockState(), pkt.getNbtCompound());
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction doFill) {
		
		if(resource == null || resource.isEmpty())
			return 0;

		TilePressureTank handler2 = this.getFluidTankInDirection(Direction.UP);
		
		//Move up, check if we can fill there, do top down
		if(handler2 != null && handler2.canFill(resource)) {
			return handler2.fill(resource, doFill);
		}
		return fillInternal2(resource, doFill);
	}
	
	private int fillInternal2(FluidStack resource, FluidAction doFill) {
		
		TilePressureTank handler = this.getFluidTankInDirection(Direction.DOWN);
		
		int amt = 0;

		if(handler != null) {
			amt = handler.fillInternal2(resource, doFill);
		}
		//Copy to avoid modifying the passed one
		FluidStack resource2 = resource.copy();
		resource2.setAmount(resource2.getAmount() - amt);
		if(resource2.getAmount() > 0)
			amt += super.fill(resource2, doFill);
		
		if(amt > 0 && doFill.execute())
			fluidChanged = true;
		
		checkForUpdate();
		
		return amt;
	}
	
	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.pressuretank";
	}

	@Override
	@Nonnull
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		IFluidHandler handler = this.getFluidTankInDirection(Direction.UP);

		FluidStack stack = FluidStack.EMPTY;
		if(handler != null && !handler.getFluidInTank(0).isEmpty() &&
				!fluidTank.getFluid().isEmpty() && fluidTank.getFluid().getFluid() ==
				handler.getFluidInTank(0).getFluid()) {

			stack = handler.drain(maxDrain, doDrain);
		}
		if(!stack.isEmpty())
			return stack;

		FluidStack stack2 = super.drain(maxDrain - (!stack.isEmpty() ? stack.getAmount() : 0), doDrain);

		if(!stack.isEmpty() && stack2.isEmpty())
			stack2.setAmount(stack2.getAmount() + stack.getAmount());

		
		if(stack2.isEmpty()) {
			fluidChanged = true;
		}
		checkForUpdate();
		
		
		return stack2;
	}

	@Override
	public FluidStack drain(FluidStack resource,
			FluidAction doDrain) {
		if(this.fluidTank.getFluid().isEmpty() || resource.getFluid() != this.fluidTank.getFluid().getFluid())
			return FluidStack.EMPTY;

		return this.drain(resource.getAmount(), doDrain);
	}

	public TilePressureTank getFluidTankInDirection(Direction direction) {
		TileEntity tile = world.getTileEntity(pos.offset(direction));

		if(tile instanceof TilePressureTank) {
			return ((TilePressureTank) tile);
		}
		return null;
	}
	
	private boolean canFill(FluidStack stack)
	{
		FluidStack stack2 = fluidTank.getFluid();

		return stack2.isEmpty() || (stack2.getFluid() == stack.getFluid());
	}

	@Override
	protected CompoundNBT writeToNBTHelper(CompoundNBT nbtTagCompound) {
		super.writeToNBTHelper(nbtTagCompound);
		fluidTank.writeToNBT(nbtTagCompound);
		return nbtTagCompound;
	}

	@Override
	protected void readFromNBTHelper(CompoundNBT nbtTagCompound) {
		super.readFromNBTHelper(nbtTagCompound);
		fluidTank.readFromNBT(nbtTagCompound);
	}

	@Override
	protected boolean useBucket(int slot, @Nonnull ItemStack stack) {
		boolean bucketUsed = super.useBucket(slot, stack);

		if(bucketUsed) {
			IFluidHandler handler = getFluidTankInDirection(Direction.DOWN);
			if(handler != null) {
				FluidStack othertank = handler.getFluidInTank(0);
				if(othertank == null || (othertank.getAmount() < handler.getTankCapacity(0)))
					fluidTank.drain(handler.fill(fluidTank.getFluid(), FluidAction.EXECUTE), FluidAction.EXECUTE);
			}
		}

		return bucketUsed;
	}

	public void onAdjacentBlockUpdated(Direction dir) {
		if(dir != Direction.DOWN)
			return;
		
		TilePressureTank tank = getFluidTankInDirection(Direction.UP);

		if(tank != null && !tank.getFluidInTank(0).isEmpty()) {
			if(fluidTank.getFluid().isEmpty()) {
				fluidTank.fill(tank.fluidTank.drain(fluidTank.getCapacity(),  FluidAction.EXECUTE),  FluidAction.EXECUTE);
			}
			else if(tank.getFluidInTank(0).getFluid() == fluidTank.getFluid().getFluid()) {
				fluidTank.fill(tank.drain(fluidTank.getCapacity() - fluidTank.getFluidAmount(),  FluidAction.EXECUTE),  FluidAction.EXECUTE);
				tank.fluidTank.drain(fluidTank.getCapacity() - fluidTank.getFluidAmount(),  FluidAction.EXECUTE);
			}

			this.markDirty();
		}
	}
}
