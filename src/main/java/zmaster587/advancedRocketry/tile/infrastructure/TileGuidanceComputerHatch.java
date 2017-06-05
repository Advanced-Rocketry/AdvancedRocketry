package zmaster587.advancedRocketry.tile.infrastructure;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IToggleButton;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleLimitedSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;

public class TileGuidanceComputerHatch extends TilePointer implements IInfrastructure, IInventory, IModularInventory, IToggleButton, INetworkMachine, ITickable {

	private static final int buttonAutoEject = 0, buttonSatellite = 1, buttonPlanet = 2, buttonStation = 3, redstoneState = 4;
	private ModuleToggleSwitch module_autoEject, module_satellite, module_planet, module_station;
	private boolean buttonState[];
	private boolean chipEjected;
	EntityRocket rocket;
	ModuleRedstoneOutputButton redstoneControl;
	RedstoneState state;

	public TileGuidanceComputerHatch() {
		buttonState = new boolean[4];
		chipEjected = false;

		redstoneControl = new ModuleRedstoneOutputButton(174, 4, redstoneState, "", this, "Loading State: ");
		state = RedstoneState.ON;
		module_autoEject = new ModuleToggleSwitch(90, 15, buttonAutoEject, "", this, TextureResources.buttonAutoEject, LibVulpes.proxy.getLocalizedString("Auto Eject Upon Landing"), 24, 24, false);
		module_satellite = new ModuleToggleSwitch(64, 41, buttonSatellite, "", this, TextureResources.buttonAutoEject, LibVulpes.proxy.getLocalizedString("Allow Ejection of Satellite Chips"), 24, 24, false); 
		module_planet = new ModuleToggleSwitch(90, 41, buttonPlanet, "", this, TextureResources.buttonAutoEject, LibVulpes.proxy.getLocalizedString("Allow Ejection of Planet Chips"), 24, 24, false);
		module_station = new ModuleToggleSwitch(116, 41, buttonStation, "", this, TextureResources.buttonAutoEject, LibVulpes.proxy.getLocalizedString("Allow Ejection of Station Chips"), 24, 24, false);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		handleUpdateTag(pkt.getNbtCompound());
		setModuleStates();
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(getMasterBlock() instanceof TileRocketBuilder)
			((TileRocketBuilder)getMasterBlock()).removeConnectedInfrastructure(this);
	}

	@Override
	public String getName() {
		return getModularInventoryName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			return guidanceComputer.getStackInSlot(index);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			return guidanceComputer.decrStackSize(index, count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			return guidanceComputer.removeStackFromSlot(index);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			guidanceComputer.setInventorySlotContents(index, stack);
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			return guidanceComputer.isItemValidForSlot(index, stack);
		}

		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			guidanceComputer.clear();
		}
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, this.getPos());

		if(this.rocket != null) {
			this.rocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}

		if(player.world.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new TextComponentString("You program the linker with the fluid loader at: " + getPos().getX() + " " + getPos().getY() + " " + getPos().getZ())));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		if(player.world.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new TextComponentString("This must be the first machine to link!")));
		return false;
	}

	@Override
	public void unlinkRocket() {
		rocket = null;
		((BlockHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(world, world.getBlockState(pos), pos, false);
		chipEjected = false;
	}

	@Override
	public boolean disconnectOnLiftOff() {
		return true;
	}

	@Override
	public boolean linkRocket(EntityRocketBase rocket) {
		this.rocket = (EntityRocket) rocket;
		IInventory guidanceComputer;

		if(!chipEjected && buttonState[buttonAutoEject] && (guidanceComputer = this.rocket.storage.getGuidanceComputer()) != null ) {
			ItemStack stack = guidanceComputer.getStackInSlot(0);
			if(!stack.isEmpty()) {
				if(		(stack.getItem() instanceof ItemSatelliteIdentificationChip && buttonState[buttonSatellite]) ||
						(stack.getItem() instanceof ItemPlanetIdentificationChip && buttonState[buttonPlanet]) ||
						(stack.getItem() instanceof ItemStationChip && buttonState[buttonStation])) {
					ejectChipFrom(guidanceComputer);
					chipEjected = true;
				}
			}
			else
				chipEjected = true;
		}

		return true;
	}

	private void ejectChipFrom(IInventory guidanceComputer) {
		for(EnumFacing dir : EnumFacing.VALUES) {
			TileEntity tile = world.getTileEntity(getPos().offset(dir));
			if(tile instanceof IInventory) {
				if(ZUtils.doesInvHaveRoom(guidanceComputer.getStackInSlot(0), (IInventory)tile)) {
					ZUtils.mergeInventory(guidanceComputer.getStackInSlot(0), (IInventory)tile);
					guidanceComputer.removeStackFromSlot(0);
				}
			}
		}
	}

	@Override
	public int getMaxLinkDistance() {
		return 64;
	}

	@Override
	public boolean canRenderConnection() {
		return true;
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModuleLimitedSlotArray(15, 15, this, 0, 1));
		modules.add(redstoneControl);


		modules.add(module_autoEject);



		if(world.isRemote)
			module_satellite.setBGColor(0xFF2a4bad);

		modules.add(module_satellite);

		if(world.isRemote)
			module_planet.setBGColor(0xFF8fdc60);

		modules.add(module_planet);

		if(world.isRemote)
			module_station.setBGColor(0xFFdddddd);

		modules.add(module_station);
		setModuleStates();

		return modules;
	}

	private void setModuleStates() {
		module_station.setToggleState(buttonState[buttonStation]);
		module_satellite.setToggleState(buttonState[buttonSatellite]);
		module_autoEject.setToggleState(buttonState[buttonAutoEject]);
		module_planet.setToggleState(buttonState[buttonPlanet]);
	}

	public void update() {
		if(!world.isRemote && rocket != null) {
			boolean rocketContainsItems = rocket.storage.getGuidanceComputer() != null && !rocket.storage.getGuidanceComputer().getStackInSlot(0).isEmpty() && (chipEjected || !buttonState[buttonAutoEject]);
			//Update redstone state
			setRedstoneState(!rocketContainsItems);

		}
	}

	protected void setRedstoneState(boolean condition) {
		condition = isStateActive(state, condition);
		((BlockHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(world,world.getBlockState(pos), pos, condition);

	}

	protected boolean isStateActive(RedstoneState state, boolean condition) {
		if(state == RedstoneState.INVERTED)
			return !condition;
		else if(state == RedstoneState.OFF)
			return false;
		return condition;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.loader.5.name";
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
	public void onInventoryButtonPressed(int buttonId) {
		if(redstoneState == buttonId) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)redstoneState));
		}
		else {
			buttonState[buttonId] = !buttonState[buttonId];
			PacketHandler.sendToServer(new PacketMachine(this, (byte)buttonId));
		}

	}

	@Override
	public void stateUpdated(ModuleBase module) {

	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == redstoneState)
			out.writeByte(state.ordinal());
		else {
			short status = 0;
			for(int i = 0; i < buttonState.length; i++) {
				status += buttonState[i] ? 1<<i : 0; 
			}
			out.writeShort(status);
		}

	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == redstoneState)
			nbt.setByte("state", in.readByte());
		else {

			nbt.setShort("status", in.readShort());

		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == redstoneState) {
			state = RedstoneState.values()[nbt.getByte("state")];

			markDirty();
			world.setBlockState(getPos(), world.getBlockState(getPos()));
		}
		else {
			short status = nbt.getShort("status");
			for(int i = 0; i < buttonState.length; i++) {
				buttonState[i] = (status & 1<<i) != 0;
			}
			setModuleStates();
			markDirty();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		nbt.setBoolean("chipEjected", chipEjected);
		nbt.setByte("redstoneState", (byte) state.ordinal());

		short status = 0;
		for(int i = 0; i < buttonState.length; i++) {
			status += buttonState[i] ? 1<<i : 0; 
		}

		nbt.setShort("statuses", status);

		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		chipEjected = nbt.getBoolean("chipEjected");
		state = RedstoneState.values()[nbt.getByte("redstoneState")];

		short status = nbt.getShort("statuses");
		for(int i = 0; i < buttonState.length; i++) {
			buttonState[i] = (status & 1<<i) != 0; 
		}

		super.readFromNBT(nbt);
	}

	@Override
	public boolean isEmpty() {
		return getStackInSlot(0).isEmpty();
	}

}
