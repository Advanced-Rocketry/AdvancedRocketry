package zmaster587.advancedRocketry.tile;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import zmaster587.advancedRocketry.network.PacketFluidParticle;
import zmaster587.libVulpes.cap.FluidCapability;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.tile.TileEntityRFConsumer;

import java.util.*;

public class TilePump extends TileEntityRFConsumer implements IFluidHandler, IModularInventory {

	private FluidTank tank;
	private List<BlockPos> cache;
	private final int RANGE = 64;

	public TilePump() {
		super(1000);
		tank = new FluidTank(16000);
		cache = new LinkedList<BlockPos>();
	}
	
	public int getPowerPerOperation() {
		return 100;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T) new FluidCapability(this);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void update() {
		super.update();
		
		//Attempt fluid Eject
		if(!world.isRemote && tank.getFluid() != null) {
			for(EnumFacing direction : EnumFacing.values()) {
				BlockPos newBlock = getPos().offset(direction);
				TileEntity tile  = world.getTileEntity(newBlock);
				if(tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()))
				{
					IFluidHandler cap = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
					FluidStack stack = tank.getFluid().copy();
					stack.amount = (int)Math.min(tank.getFluid().amount, 1000);
					//Perform the drain
					cap.fill(tank.drain(cap.fill(stack, false), true), true);
					
					//Abort if we run out of fluid
					if(tank.getFluid() == null)
						break;
				}
			}
		}
	}
	
	private int getFrequencyFromPower()
	{
		float ratio = energy.getUniversalEnergyStored()/(float)energy.getMaxEnergyStored();
		if(ratio > 0.5)
			return 1;
		return 10;
	}
	
	@Override
	public void performFunction() {

		if(!world.isRemote)
		{
			//Do we have room?
			if(tank.getCapacity() - 1000 < tank.getFluidAmount())
				return;
			
			BlockPos nextPos = getNextBlockLocation();
			if(nextPos != null)
			{
				if(canFitFluid(nextPos))
				{
					Block worldBlock = world.getBlockState(nextPos).getBlock();
					Material mat = world.getBlockState(nextPos).getMaterial();
					if(worldBlock instanceof IFluidBlock)
					{
						FluidStack stack = ((IFluidBlock)worldBlock).drain(world, nextPos, true);

						if(stack != null)
							tank.fill(stack, true);
						int colour = ((IFluidBlock)worldBlock).getFluid().getColor();
						if(mat == Material.LAVA)
							colour = 0xFFbd3718;
						
						PacketHandler.sendToNearby(new PacketFluidParticle(nextPos, this.pos, 200, colour), world.provider.getDimension(), this.pos, 128);
					}
				}
			}
		}
	}

	private boolean canFitFluid(BlockPos pos)
	{
		Block worldBlock = world.getBlockState(pos).getBlock();
		if(worldBlock instanceof IFluidBlock)
		{
			// Can we put it into the tank?
			if(tank.getFluid() == null || tank.getFluid().getFluid() == ((IFluidBlock)worldBlock).getFluid())
			{
				return true;
			}
		}
		return false;
	}

	private BlockPos getNextBlockLocation()
	{

		if(!cache.isEmpty())
			return cache.remove(0);

		BlockPos currentPos = new MutableBlockPos(getPos().down());

		while(world.isAirBlock(currentPos))
			currentPos = currentPos.down();

		// We found a fluid
		Block worldBlock = world.getBlockState(currentPos).getBlock();

		if(canFitFluid(currentPos))
			findFluidAtOrAbove(currentPos, ((IFluidBlock)worldBlock).getFluid());
		if(!cache.isEmpty())
			return cache.remove(0);
		return null;
	}

	private List<BlockPos> findFluidAtOrAbove(BlockPos pos, Fluid fluid)
	{
		Queue<BlockPos> queue = new LinkedList<BlockPos>();
		Set<BlockPos> visited = new HashSet<BlockPos>();
		queue.add(pos);

		while(!queue.isEmpty())
		{
			BlockPos nextElement = queue.poll();
			if(visited.contains(nextElement) || nextElement.getDistance(pos.getX(), nextElement.getY(), pos.getZ()) > RANGE )
				continue;

			Block worldBlock = world.getBlockState(nextElement).getBlock();
			if(worldBlock instanceof IFluidBlock)
			{
				if(fluid == null || ((IFluidBlock)worldBlock).getFluid() == fluid)
				{
					//only add drainable fluids, allow chaining along flowing fluid tho
					if(((IFluidBlock)worldBlock).canDrain(world, nextElement))
						cache.add(0, nextElement);
					visited.add(nextElement);
					queue.add(nextElement.west());
					queue.add(nextElement.east());
					queue.add(nextElement.north());
					queue.add(nextElement.south());
					queue.add(nextElement.up());
				}
			}
		}
		return cache;
	}

	@Override
	public boolean canPerformFunction() {
		return tank.getFluidAmount() <= tank.getCapacity() && world.getWorldTime() % getFrequencyFromPower() == 0;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return tank.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		// Don't fill
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.pump.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return false;
	}

}
