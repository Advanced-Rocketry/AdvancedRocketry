package zmaster587.advancedRocketry.tile;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.network.PacketAirParticle;
import zmaster587.advancedRocketry.network.PacketFluidParticle;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.cap.FluidCapability;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleLiquidIndicator;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.tile.TileEntityRFConsumer;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

public class TilePump extends TileEntityRFConsumer implements IFluidHandler, IModularInventory {

	private FluidTank tank;
	private List<BlockPos> cache;
	private final int RANGE = 64;

	public TilePump() {
		super(AdvancedRocketryTileEntityType.TILE_PUMP, 1000);
		tank = new FluidTank(16000);
		cache = new LinkedList<>();
	}
	
	public int getPowerPerOperation() {
		return 100;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> new FluidCapability(this)).cast();
		}
		return super.getCapability(capability);
	}

	@Override
	public void tick() {
		super.tick();
		
		//Attempt fluid Eject
		if(!world.isRemote && !tank.getFluid().isEmpty()) {
			for(Direction direction : Direction.values()) {
				BlockPos newBlock = getPos().offset(direction);
				TileEntity tile  = world.getTileEntity(newBlock);
				if(tile != null && tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).isPresent())
				{
<<<<<<< HEAD
					IFluidHandler cap = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).orElse(null);
					FluidStack stack =  tank.getFluid().copy();
					stack.setAmount((int)Math.min(tank.getFluid().getAmount(), 1000));
=======
					IFluidHandler cap = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
					FluidStack stack = tank.getFluid().copy();
					stack.amount = Math.min(tank.getFluid().amount, 1000);
>>>>>>> origin/feature/nuclearthermalrockets
					//Perform the drain
					cap.fill(tank.drain(cap.fill(stack, FluidAction.SIMULATE), FluidAction.EXECUTE), FluidAction.EXECUTE);
					
					//Abort if we run out of fluid
					if(tank.getFluid().isEmpty())
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

		if(!world.isRemote) {
			//Do we have room?
			if(tank.getCapacity() - 1000 < tank.getFluidAmount())
				return;
			
			BlockPos nextPos = getNextBlockLocation();
<<<<<<< HEAD
			if(nextPos != null)
			{
				if(canFitFluid(nextPos))
				{
					BlockState state = world.getBlockState(nextPos);
					Block worldBlock = state.getBlock();
					Material mat = world.getBlockState(nextPos).getMaterial();
					if(worldBlock instanceof FlowingFluidBlock)
					{
						Fluid stack = ((FlowingFluidBlock)worldBlock).pickupFluid(world, nextPos, state);

						if(stack != null)
							tank.fill(new FluidStack(stack, 1000), FluidAction.EXECUTE);
						int colour = ((FlowingFluidBlock)worldBlock).getFluid().getAttributes().getColor();
=======
			if(nextPos != null) {
				if(canFitFluid(nextPos)) {
					Block worldBlock = world.getBlockState(nextPos).getBlock();
					Material mat = world.getBlockState(nextPos).getMaterial();
					if(worldBlock instanceof IFluidBlock) {
						FluidStack fStack = ((IFluidBlock)worldBlock).drain(world, nextPos, true);

						if(fStack != null)
							tank.fill(fStack, true);
						int colour = ((IFluidBlock)worldBlock).getFluid().getColor();
>>>>>>> origin/feature/nuclearthermalrockets
						if(mat == Material.LAVA)
							colour = 0xFFbd3718;
						
						PacketHandler.sendToNearby(new PacketFluidParticle(nextPos, this.pos, 200, colour), ZUtils.getDimensionIdentifier(world), this.pos, 128);
					}
				}
			}
		}
	}

	private boolean canFitFluid(BlockPos pos) {
		Block worldBlock = world.getBlockState(pos).getBlock();
<<<<<<< HEAD
		if(worldBlock instanceof FlowingFluidBlock)
		{
			// Can we put it into the tank?
			if(tank.getFluid().isEmpty() || tank.getFluid().getFluid() == ((FlowingFluidBlock)worldBlock).getFluid())
			{
				return true;
			}
=======
		if(worldBlock instanceof IFluidBlock) {
			// Can we put it into the tank?
			return tank.getFluid() == null || tank.getFluid().getFluid() == ((IFluidBlock) worldBlock).getFluid();
>>>>>>> origin/feature/nuclearthermalrockets
		}
		return false;
	}

	private BlockPos getNextBlockLocation() {

		if(!cache.isEmpty())
			return cache.remove(0);

		BlockPos currentPos = new BlockPos(getPos().down());

		while(world.isAirBlock(currentPos))
			currentPos = currentPos.down();

		// We found a fluid
		Block worldBlock = world.getBlockState(currentPos).getBlock();

		if(canFitFluid(currentPos))
			findFluidAtOrAbove(currentPos, ((FlowingFluidBlock)worldBlock).getFluid());
		if(!cache.isEmpty())
			return cache.remove(0);
		return null;
	}

	private List<BlockPos> findFluidAtOrAbove(BlockPos pos, Fluid fluid) {
		Queue<BlockPos> queue = new LinkedList<>();
		Set<BlockPos> visited = new HashSet<>();
		queue.add(pos);

		while(!queue.isEmpty()) {
			BlockPos nextElement = queue.poll();
<<<<<<< HEAD
			if(visited.contains(nextElement) || !nextElement.withinDistance(new Vector3i(pos.getX(), nextElement.getY(), pos.getZ()), RANGE) )
				continue;

			BlockState state = world.getBlockState(nextElement);
			Block worldBlock = state.getBlock();
			if(worldBlock instanceof FlowingFluidBlock)
			{
				if(fluid.isEquivalentTo(Fluids.EMPTY) || ((FlowingFluidBlock)worldBlock).getFluid().isEquivalentTo(fluid))
				{
=======
			if(visited.contains(nextElement) || nextElement.getDistance(pos.getX(), nextElement.getY(), pos.getZ()) > RANGE)
				continue;

			Block worldBlock = world.getBlockState(nextElement).getBlock();
			if(worldBlock instanceof IFluidBlock) {
				if(fluid == null || ((IFluidBlock)worldBlock).getFluid() == fluid) {
>>>>>>> origin/feature/nuclearthermalrockets
					//only add drainable fluids, allow chaining along flowing fluid tho
					if(((FlowingFluidBlock)worldBlock).getFluidState(state).isSource())
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
		return tank.getFluidAmount() <= tank.getCapacity() && AdvancedRocketry.proxy.getWorldTimeUniversal() % getFrequencyFromPower() == 0;
	}

	@Override
	public int fill(FluidStack resource, FluidAction doFill) {
		// Don't fill
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		return tank.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
<<<<<<< HEAD
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(new ModuleLiquidIndicator(27, 18, this));
		return modules;
=======
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		return new LinkedList<>();
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.blockpump";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return false;
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

	@Override
	public int getTanks() {
		return this.tank.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.tank.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.tank.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return this.tank.isFluidValid(tank, stack);
	}

}
