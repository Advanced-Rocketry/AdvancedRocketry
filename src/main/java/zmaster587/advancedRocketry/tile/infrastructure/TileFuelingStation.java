package zmaster587.advancedRocketry.tile.infrastructure;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.BlockTileRedstoneEmitter;
import zmaster587.advancedRocketry.tile.TileRocketAssembler;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TileInventoriedFEConsumerTank;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public class TileFuelingStation extends TileInventoriedFEConsumerTank implements IModularInventory, IMultiblock, IInfrastructure, ILinkableTile, INetworkMachine, IButtonInventory {
	EntityRocketBase linkedRocket;
	HashedBlockPosition masterBlock;
	ModuleRedstoneOutputButton redstoneControl;
	RedstoneState state;
	final int fuelPointsPer10Mb = 10;

	public TileFuelingStation() {
		super( AdvancedRocketryTileEntityType.TILE_FUELING_STATION, 1000, 3, 5000);
		masterBlock = new HashedBlockPosition(0, -1, 0);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, "", this);
		state = RedstoneState.ON;
	}

	@Override
	public int getMaxLinkDistance() {
		return 10;
	}

	private void setRedstoneState(boolean condition) {
		if(state == RedstoneState.INVERTED)
			condition = !condition;
		else if(state == RedstoneState.OFF)
			condition = false;
		((BlockTileRedstoneEmitter)AdvancedRocketryBlocks.blockFuelingStation).setRedstoneState(world, world.getBlockState(pos), pos, condition);

	}

	@Override
	public void performFunction() {

		if(!world.isRemote) {
			//Lock rocket to a specific fluid so that it has only one oxidizer/bipropellant/monopropellant/etc
			FluidStack currentFluidStack = tank.getFluid();
			if (!currentFluidStack.isEmpty()) {
				Fluid currentFluid = currentFluidStack.getFluid();

				//Check to see if we should set the rocket fuel
				if (linkedRocket.stats.getFuelFluid() == null) {
					if ((FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, currentFluid) && linkedRocket.getFuelCapacity(FuelType.LIQUID_MONOPROPELLANT) > 0) || (FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, currentFluid) && linkedRocket.getFuelCapacity(FuelType.LIQUID_BIPROPELLANT) > 0))
						linkedRocket.stats.setFuelFluid(currentFluid);
				}
				if (linkedRocket.stats.getOxidizerFluid() == null) {
					if (FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, currentFluid))
						linkedRocket.stats.setOxidizerFluid(currentFluid);
				}
				if (linkedRocket.stats.getWorkingFluid() == null) {
					if (FuelRegistry.instance.isFuel(FuelType.NUCLEAR_WORKING_FLUID, currentFluid))
						linkedRocket.stats.setWorkingFluid(currentFluid);
				}

				//Actually fill the fuel if that is the case
				if (FluidUtils.areFluidsSameType(linkedRocket.stats.getFuelFluid(), currentFluid) || FluidUtils.areFluidsSameType(linkedRocket.stats.getOxidizerFluid(), currentFluid) || FluidUtils.areFluidsSameType(linkedRocket.stats.getWorkingFluid(), currentFluid)) {
					if (linkedRocket.getRocketFuelType() == FuelType.LIQUID_BIPROPELLANT && FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, currentFluid)) {
						int fuelRate = (int) (FuelRegistry.instance.getMultiplier(FuelType.LIQUID_OXIDIZER, currentFluid) * linkedRocket.stats.getBaseFuelRate(FuelType.LIQUID_OXIDIZER));
						tank.drain(linkedRocket.addFuelAmount(FuelType.LIQUID_OXIDIZER, 10), FluidAction.EXECUTE);
						linkedRocket.setFuelConsumptionRate(FuelType.LIQUID_OXIDIZER, fuelRate);
					} else {
						int fuelRate = (int) (FuelRegistry.instance.getMultiplier(linkedRocket.getRocketFuelType(), currentFluid) * linkedRocket.stats.getBaseFuelRate(linkedRocket.getRocketFuelType()));
						tank.drain(linkedRocket.addFuelAmount(linkedRocket.getRocketFuelType(), 10),  FluidAction.EXECUTE);
						linkedRocket.setFuelConsumptionRate(linkedRocket.getRocketFuelType(), fuelRate);
					}

				}

				//If the rocket is full then emit redstone
				setRedstoneState(!canRocketFitFluid(currentFluid));
			}
		}
		useBucket(0, inventory.getStackInSlot(0));
	}

	@Override
	public int getPowerPerOperation() {
		return 30;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}


	@Override
	public boolean canPerformFunction() {
		return linkedRocket != null && (!tank.getFluid().isEmpty() && tank.getFluidAmount() > 9 && canRocketFitFluid(tank.getFluid().getFluid()));
	}

	@Override
	public boolean canFill(Fluid fluid) {
		return FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT,fluid) || FuelRegistry.instance.isFuel(FuelType.NUCLEAR_WORKING_FLUID,fluid) || FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT,fluid) || FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER,fluid);
	}


	/**
	 * @param fluid the fluid to check whether the rocket has space for it
	 * @return boolean on whether the rocket can accept the fluid
	 */
	public boolean canRocketFitFluid(Fluid fluid) {
		return canFill(fluid) && ((linkedRocket.getRocketFuelType() == FuelType.LIQUID_BIPROPELLANT && FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluid)) ? linkedRocket.getFuelCapacity(FuelType.LIQUID_OXIDIZER) > linkedRocket.getFuelAmount(FuelType.LIQUID_OXIDIZER) : linkedRocket.getFuelCapacity(linkedRocket.getRocketFuelType()) > linkedRocket.getFuelAmount(linkedRocket.getRocketFuelType()));
	}


	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.fuelingstation";
	}


	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {

		super.setInventorySlotContents(slot, stack);
		while(useBucket(0, getStackInSlot(0)));

	}


	/**
	 * Handles internal bucket tank interaction
	 * @param slot integer slot to insert into
	 * @param stack the itemstack to work fluid handling on
	 * @return boolean on whether the fluid stack was successfully filled from or not, returns false if the stack cannot be extracted from or has no fluid left
	 */
	private boolean useBucket(int slot, @Nonnull ItemStack stack) {
		if(slot == 0 && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
			FluidStack fluidStack = fluidItem.getFluidInTank(0);
			if(!fluidStack.isEmpty() && (FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, fluidStack.getFluid()) || FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, fluidStack.getFluid()) || FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluidStack.getFluid())) && tank.getFluidAmount() + fluidItem.getTankCapacity(0) <= tank.getCapacity()) {

				return FluidUtils.attemptDrainContainerIInv(inventory, tank, stack, 0, 1);
			}
		}
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()) {
			FluidStack fstack = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null).getFluidInTank(0);
			return !fstack.isEmpty() && FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, fstack.getFluid());
		}
		return false;
	}

	@Override
	public void unlinkRocket() {
		this.linkedRocket = null;
		((BlockTileRedstoneEmitter)AdvancedRocketryBlocks.blockFuelingStation).setRedstoneState(world, world.getBlockState(pos), pos, false);

	}

	@Override
	public boolean disconnectOnLiftOff() {
		return true;
	}

	@Override
	public boolean linkRocket(EntityRocketBase rocket) {
		this.linkedRocket = rocket;
		if (!tank.getFluid().isEmpty())
			setRedstoneState(!canRocketFitFluid(tank.getFluid().getFluid()));
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkStart(ItemStack item, TileEntity entity, PlayerEntity player, World world) {

		ItemLinker.setMasterCoords(item, pos);

		if(this.linkedRocket != null) {
			this.linkedRocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}

		if(player.world.isRemote)
			Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage((new StringTextComponent(LibVulpes.proxy.getLocalizedString("msg.fuelingStation.link") + ": " + this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ())));
		return true;
	}

	@Override
	public void remove() {
		super.remove();
		if(getMasterBlock() instanceof TileRocketAssembler)
			((TileRocketAssembler)getMasterBlock()).removeConnectedInfrastructure(this);

		//Mostly for client rendering stuff
		if(linkedRocket != null)
			linkedRocket.unlinkInfrastructure(this);
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkComplete(ItemStack item, TileEntity entity, PlayerEntity player, World world) {
		if(player.world.isRemote)
			Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("msg.linker.error.firstmachine"));
		return false;
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public int[] getSlotsForFace(Direction side) {
		if(side == Direction.DOWN)
			return  new int[]{1};
		return  new int[]{0}; 
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> list = new ArrayList<>();

		list.add(new ModulePower(156, 12, this));
		list.add(new ModuleSlotArray(45, 18, this, 0, 1));
		list.add(new ModuleSlotArray(45, 54, this, 1, 2));
		list.add(redstoneControl);

		if(world.isRemote)
			list.add(new ModuleImage(44, 35, new IconResource(194, 0, 18, 18, CommonResources.genericBackground)));
		list.add(new ModuleLiquidIndicator(27, 18, this));

		return list;
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public boolean linkMission(IMission mission) {
		return false;
	}

	@Override
	public void unlinkMission() {
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putByte("redstoneState", (byte) state.ordinal());
		if(hasMaster()) {
			nbt.putIntArray("masterPos", new int[] {masterBlock.x, masterBlock.y, masterBlock.z});
		}
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		RedstoneState redstate = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(redstate);

		if(nbt.contains("masterPos")) {
			int[] pos = nbt.getIntArray("masterPos");
			setMasterBlock(new BlockPos(pos[0], pos[1], pos[2]));
		}
	}

	@Override
	public boolean hasMaster() {
		return masterBlock.y > -1;
	}

	@Override
	public TileEntity getMasterBlock() {
		return world.getTileEntity(new BlockPos(masterBlock.x, masterBlock.y, masterBlock.z));
	}

	@Override
	public void setComplete(BlockPos pos) {

	}

	@Override
	public void setIncomplete() {
		masterBlock.y = -1;
	}

	@Override
	public void setMasterBlock(BlockPos pos) {
		masterBlock = new HashedBlockPosition(pos);
	}

	public boolean canRenderConnection() {
		return true;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		state = redstoneControl.getState();
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		out.writeByte(state.ordinal());
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		nbt.putByte("state", in.readByte());
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		state = RedstoneState.values()[nbt.getByte("state")];

		if(linkedRocket != null && !tank.getFluid().isEmpty())
			setRedstoneState(!canRocketFitFluid(tank.getFluid().getFluid()));
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(getModularInventoryName());
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}


	@Override
	public GuiHandler.guiId getModularInvType() {
		return GuiHandler.guiId.MODULAR;
	}
}
