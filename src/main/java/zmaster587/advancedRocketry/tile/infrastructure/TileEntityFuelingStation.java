package zmaster587.advancedRocketry.tile.infrastructure;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.*;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.block.BlockTileRedstoneEmitter;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.IconResource;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import java.util.ArrayList;
import java.util.List;

public class TileEntityFuelingStation extends TileInventoriedRFConsumerTank implements IModularInventory, IMultiblock, IInfrastructure, ILinkableTile, INetworkMachine, IButtonInventory {
	EntityRocketBase linkedRocket;
	HashedBlockPosition masterBlock;
	ModuleRedstoneOutputButton redstoneControl;
	RedstoneState state;

	public TileEntityFuelingStation() {
		super(1000,3, 5000);
		masterBlock = new HashedBlockPosition(0, -1, 0);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this);
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
			//Lock rocket to a specific fluid so that it has only one oxidizer/bipropellant/monopropellant
			if (tank.getFluid() != null && linkedRocket.stats.getFuelFluid() == "null") {
				if (FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, tank.getFluid().getFluid()) || FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, tank.getFluid().getFluid())) {
					linkedRocket.stats.setFuelFluid(tank.getFluid().getFluid().getName());
				}
			} else if (tank.getFluid() != null && linkedRocket.stats.getOxidizerFluid() == "null") {
				if (FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, tank.getFluid().getFluid())) {
					linkedRocket.stats.setOxidizerFluid(tank.getFluid().getFluid().getName());
				}
			}

			if (tank.getFluid() != null && isFluidFillable(tank.getFluid().getFluid())) {
				if (FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, tank.getFluid().getFluid())) {
					int fuelRate = (int)FuelRegistry.instance.getMultiplier(FuelType.LIQUID_MONOPROPELLANT, tank.getFluid().getFluid()) * linkedRocket.stats.getBaseFuelRate(FuelType.LIQUID_MONOPROPELLANT);

					tank.drain(linkedRocket.addFuelAmountMonopropellant(ARConfiguration.getCurrentConfig().fuelPointsPer10Mb), true);
					linkedRocket.setFuelRateMonopropellant(fuelRate);
				} else if (FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, tank.getFluid().getFluid())) {
					int fuelRate = (int)FuelRegistry.instance.getMultiplier(FuelType.LIQUID_BIPROPELLANT, tank.getFluid().getFluid()) * linkedRocket.stats.getBaseFuelRate(FuelType.LIQUID_BIPROPELLANT);

					tank.drain(linkedRocket.addFuelAmountBipropellant(ARConfiguration.getCurrentConfig().fuelPointsPer10Mb), true);
					linkedRocket.setFuelRateBipropellant(fuelRate);
				} else if (FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, tank.getFluid().getFluid())) {
					int fuelRate = (int)FuelRegistry.instance.getMultiplier(FuelType.LIQUID_OXIDIZER, tank.getFluid().getFluid()) * linkedRocket.stats.getBaseFuelRate(FuelType.LIQUID_OXIDIZER);

					tank.drain(linkedRocket.addFuelAmountOxidizer(ARConfiguration.getCurrentConfig().fuelPointsPer10Mb), true);
					linkedRocket.setFuelRateOxidizer(fuelRate);
				}
			}
			//If the rocket is full then emit redstone
			setRedstoneState(linkedRocket.getFuelAmountMonopropellant() == linkedRocket.getFuelCapacityMonopropellant());
		}
		useBucket(0, inventory.getStackInSlot(0));
	}

	public boolean isFluidFillable (Fluid fluid) {
		return fluid == FluidRegistry.getFluid(linkedRocket.stats.getFuelFluid()) || (FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluid) && fluid == FluidRegistry.getFluid(linkedRocket.stats.getOxidizerFluid()) && FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, FluidRegistry.getFluid(linkedRocket.stats.getFuelFluid())));
	}

	@Override
	public int getPowerPerOperation() {
		return 30;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}


	@Override
	public boolean canPerformFunction() {
		// TODO Solid fuel?
		return linkedRocket != null && ( /*(inv != null) ||*/ (tank.getFluid() != null && tank.getFluidAmount() > 9 &&
				(linkedRocket.getRocketStats().getFuelAmount(FuelType.LIQUID_MONOPROPELLANT) < linkedRocket.getRocketStats().getFuelCapacity(FuelType.LIQUID_MONOPROPELLANT) ||
				linkedRocket.getRocketStats().getFuelAmount(FuelType.LIQUID_BIPROPELLANT) < linkedRocket.getRocketStats().getFuelCapacity(FuelType.LIQUID_BIPROPELLANT) ||
				linkedRocket.getRocketStats().getFuelAmount(FuelType.LIQUID_OXIDIZER) < linkedRocket.getRocketStats().getFuelCapacity(FuelType.LIQUID_OXIDIZER))));
	}

	@Override
	public boolean canFill(Fluid fluid) {
		return FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT,fluid) || FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT,fluid) || FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER,fluid);
	}


	@Override
	public String getModularInventoryName() {
		return "Fueling Station";
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		super.setInventorySlotContents(slot, stack);
		while(useBucket(0, getStackInSlot(0)));

	}

	//Yes i was lazy
	//TODO: make better
	//Returns true if bucket was actually used
	//TODO centralize
	private boolean useBucket( int slot, ItemStack stack) {
		if(slot == 0 && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
			FluidStack fluidStack = fluidItem.getTankProperties()[0].getContents();

			if(fluidStack != null && (FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, fluidStack.getFluid()) || FuelRegistry.instance.isFuel(FuelType.LIQUID_BIPROPELLANT, fluidStack.getFluid()) || FuelRegistry.instance.isFuel(FuelType.LIQUID_OXIDIZER, fluidStack.getFluid())) && tank.getFluidAmount() + fluidItem.getTankProperties()[0].getCapacity() <= tank.getCapacity()) {

				ItemStack emptyContainer = stack.copy();
				emptyContainer.setCount(1);
				emptyContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP).drain(8000, true);

				//disposable tank
				if(emptyContainer.isEmpty()) {
					tank.fill(fluidStack, true);
					decrStackSize(0, 1);
				}
				else
				{
					emptyContainer = emptyContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP).getContainer();

					if(!emptyContainer.isEmpty() && inventory.getStackInSlot(1).isEmpty() || (emptyContainer.isItemEqual(inventory.getStackInSlot(1)) && inventory.getStackInSlot(1).getCount() < inventory.getStackInSlot(1).getMaxStackSize())) {
						tank.fill(fluidStack, true);

						if(inventory.getStackInSlot(1).isEmpty())
							super.setInventorySlotContents(1, emptyContainer);
						else {
							inventory.getStackInSlot(1).setCount(inventory.getStackInSlot(1).getCount() + 1);
						}
						decrStackSize(0, 1);
					}
					else 
						return false;
				}
			}
			else
				return false;
		}
		else
			return false;

		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP)) {
			FluidStack fstack = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP).getTankProperties()[0].getContents();
			return fstack != null && FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT, fstack.getFluid());
		}
		return FuelRegistry.instance.isFuel(FuelType.LIQUID_MONOPROPELLANT,stack);
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
		setRedstoneState(linkedRocket.getFuelAmountMonopropellant() == linkedRocket.getFuelCapacityMonopropellant());
		return true;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, pos);

		if(this.linkedRocket != null) {
			this.linkedRocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}

		if(player.world.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.fuelingStation.link") + ": " + this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ())));
		return true;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(getMasterBlock() instanceof TileRocketBuilder)
			((TileRocketBuilder)getMasterBlock()).removeConnectedInfrastructure(this);

		//Mostly for client rendering stuff
		if(linkedRocket != null)
			linkedRocket.unlinkInfrastructure(this);
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		if(player.world.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("msg.linker.error.firstMachine"));
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if(side == EnumFacing.DOWN)
			return  new int[]{1};
		return  new int[]{0}; 
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> list = new ArrayList<ModuleBase>();

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
	public String getName() {
		return null;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public boolean linkMission(IMission misson) {
		return false;
	}

	@Override
	public void unlinkMission() {
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("redstoneState", (byte) state.ordinal());
		if(hasMaster()) {
			nbt.setIntArray("masterPos", new int[] {masterBlock.x, masterBlock.y, masterBlock.z});
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);

		if(nbt.hasKey("masterPos")) {
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
	public void onInventoryButtonPressed(int buttonId) {
		state = redstoneControl.getState();
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		out.writeByte(state.ordinal());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		nbt.setByte("state", in.readByte());
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		state = RedstoneState.values()[nbt.getByte("state")];

		if(linkedRocket != null)
			setRedstoneState(linkedRocket.getFuelAmountMonopropellant() == linkedRocket.getFuelCapacityMonopropellant());
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
}
