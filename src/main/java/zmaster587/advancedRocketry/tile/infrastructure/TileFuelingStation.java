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
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.BlockTileRedstoneEmitter;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.tile.TileRocketAssembler;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketEntity;
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

	public TileFuelingStation() {
		super( AdvancedRocketryTileEntityType.TILE_FUELING_STATION, 1000, 2, 5000);
		masterBlock = new HashedBlockPosition(0, -1, 0);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, "", this);
		state = RedstoneState.ON;
		inventory.setCanInsertSlot(0, true);
		inventory.setCanInsertSlot(1, false);
		inventory.setCanExtractSlot(0, false);
		inventory.setCanExtractSlot(1, true);
	}

	@Override
	public int getMaxLinkDistance() {
		return 10;
	}

	@Override
	public void performFunction() {
		if(!world.isRemote) {
			//Lock rocket to a specific fluid so that it has only one oxidizer/bipropellant/monopropellant/etc
			FluidStack currentFluidStack = tank.getFluid();
			Fluid currentFluid = currentFluidStack.getFluid();
			if (!currentFluidStack.isEmpty()) {
				IFluidTank rocketTank = linkedRocket.stats.getFluidTank(currentFluidStack);

				//Consume and then set fuel rates based on said fluid
				int consumedFluid = tank.drain(rocketTank.fill(new FluidStack(currentFluid, 50), FluidAction.EXECUTE), FluidAction.EXECUTE).getAmount();
                if (consumedFluid > 0) {
					if (linkedRocket.getRocketFuelType() == FuelType.LIQUID_BIPROPELLANT && FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, currentFluid)) {
						linkedRocket.stats.getFluidTank(FuelType.LIQUID_OXIDIZER).setFuelRate(FuelRegistry.instance.getMultiplier(FuelType.LIQUID_OXIDIZER, currentFluid));
					} else {
						linkedRocket.stats.getFluidTank(linkedRocket.getRocketFuelType()).setFuelRate(FuelRegistry.instance.getMultiplier(linkedRocket.getRocketFuelType(), currentFluid));
					}

					//If rocket exists and is right type, update its NBT
					if (linkedRocket instanceof  EntityRocket)((EntityRocket)linkedRocket).updateAllClientsNBT();
					//If the rocket is full then emit redstone
					updateState();
				}
			}
		}
		useBucket(0, inventory.getStackInSlot(0));
	}

	@Override
	public int getPowerPerOperation() {
		return 120;
	}

	public void updateState() {
		if(state == RedstoneState.OFF) return;
		//If we can actually emit, do so
		BlockTileRedstoneEmitter block = ((BlockTileRedstoneEmitter)world.getBlockState(pos).getBlock());
		block.setRedstoneState(world, world.getBlockState(pos), pos, linkedRocket != null && linkedRocket.stats.getFuelFillPercentage(linkedRocket.getRocketFuelType()) == 1 && state != RedstoneState.INVERTED);
	}

	@Override
	@Nonnull
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
		return linkedRocket.stats.getFluidTank(new FluidStack(fluid, 1)).getCapacity() > 0;
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
		return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
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
		return  new int[]{0, 1};
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
	public void unlinkMission() { }

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		nbt.putByte("redstonestate", (byte) state.ordinal());
		if(hasMaster()) {
			nbt.putIntArray("masterPos", new int[] {masterBlock.x, masterBlock.y, masterBlock.z});
		}
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		this.state = RedstoneState.values()[nbt.getByte("redstonestate")];
		redstoneControl.setRedstoneState(this.state);

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
	public void setComplete(BlockPos pos) { }

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
	public void readDataFromNetwork(PacketBuffer in, byte packetId, CompoundNBT nbt) {
		nbt.putByte("state", in.readByte());
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id, CompoundNBT nbt) {
		state = RedstoneState.values()[nbt.getByte("state")];
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
