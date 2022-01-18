package zmaster587.advancedRocketry.tile.infrastructure;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemPlanetChip;
import zmaster587.advancedRocketry.item.ItemSatelliteChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.TileRocketAssembler;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class TileGuidanceComputerAccessHatch extends TilePointer implements IInfrastructure, IInventory, IModularInventory, IToggleButton, INetworkMachine, ITickableTileEntity {

	private static final int buttonAutoEject = 0, buttonSatellite = 1, buttonPlanet = 2, buttonStation = 3, redstoneState = 4;
	private ModuleToggleSwitch module_autoEject, module_satellite, module_planet, module_station;
	private boolean[] buttonState;
	private boolean chipEjected;
	EntityRocket rocket;
	ModuleRedstoneOutputButton redstoneControl;
	RedstoneState state;

	public TileGuidanceComputerAccessHatch() {
		super(AdvancedRocketryTileEntityType.TILE_GUIDANCE_COMPUTER__ACCESS_HATCH);
		buttonState = new boolean[4];
		chipEjected = false;

		redstoneControl = new ModuleRedstoneOutputButton(174, 4, "", this, LibVulpes.proxy.getLocalizedString("msg.guidancecomputerhatch.loadingstate"));
		state = RedstoneState.ON;
		redstoneControl.setAdditionalData(redstoneState);
		
		module_autoEject = new ModuleToggleSwitch(90, 15, "", this, TextureResources.buttonAutoEject, LibVulpes.proxy.getLocalizedString("msg.guidancecomputerhatch.ejectonlanding"), 24, 24, false);
		module_autoEject.setAdditionalData(buttonAutoEject);
		
		module_satellite = new ModuleToggleSwitch(64, 41, "", this, TextureResources.buttonAutoEject, LibVulpes.proxy.getLocalizedString("msg.guidancecomputerhatch.ejectonsatlanding"), 24, 24, false); 
		module_satellite.setAdditionalData(buttonSatellite);
		
		module_planet = new ModuleToggleSwitch(90, 41, "", this, TextureResources.buttonAutoEject, LibVulpes.proxy.getLocalizedString("msg.guidancecomputerhatch.ejectonplanetlanding"), 24, 24, false);
		module_planet.setAdditionalData(buttonPlanet);
		
		module_station = new ModuleToggleSwitch(116, 41, "", this, TextureResources.buttonAutoEject, LibVulpes.proxy.getLocalizedString("msg.guidancecomputerhatch.ejectonstationlanding"), 24, 24, false);
		module_station.setAdditionalData(buttonStation);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
		setModuleStates();
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public void remove() {
		super.remove();
		if(getMasterBlock() instanceof TileRocketAssembler)
			((TileRocketAssembler)getMasterBlock()).removeConnectedInfrastructure(this);
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int index) {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			return guidanceComputer.getStackInSlot(index);
		}
		return ItemStack.EMPTY;
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int index, int count) {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			return guidanceComputer.decrStackSize(index, count);
		}
		return ItemStack.EMPTY;
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			return guidanceComputer.removeStackFromSlot(index);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
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
	@ParametersAreNonnullByDefault
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void openInventory(PlayerEntity player) {

	}

	@Override
	@ParametersAreNonnullByDefault
	public void closeInventory(PlayerEntity player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
		TileGuidanceComputer guidanceComputer;
		if(rocket != null && (guidanceComputer = rocket.storage.getGuidanceComputer()) != null) {
			return guidanceComputer.isItemValidForSlot(index, stack);
		}

		return false;
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
			PlayerEntity player, World world) {

		ItemLinker.setMasterCoords(item, this.getPos());

		if(this.rocket != null) {
			this.rocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}

		if(player.world.isRemote)
			Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("%s %s",new TranslationTextComponent("msg.guidancecomputerhatch.link"), ": " + getPos().getX() + " " + getPos().getY() + " " + getPos().getZ()));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			PlayerEntity player, World world) {
		if(player.world.isRemote)
			Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(new TranslationTextComponent("msg.linker.error.firstmachine"));
		return false;
	}

	@Override
	public void unlinkRocket() {
		rocket = null;
		((BlockARHatch)world.getBlockState(pos).getBlock()).setRedstoneState(world, world.getBlockState(pos), pos, false);
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
				if(		(stack.getItem() instanceof ItemSatelliteChip && buttonState[buttonSatellite]) ||
						(stack.getItem() instanceof ItemPlanetChip && buttonState[buttonPlanet]) ||
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
		for(Direction dir : Direction.values()) {
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
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();

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

	@Override
	public void tick() {
		if(!world.isRemote && rocket != null) {
			boolean rocketContainsItems = rocket.storage.getGuidanceComputer() != null && !rocket.storage.getGuidanceComputer().getStackInSlot(0).isEmpty() && (chipEjected || !buttonState[buttonAutoEject]);
			//Update redstone state
			setRedstoneState(!rocketContainsItems);

		}
	}

	protected void setRedstoneState(boolean condition) {
		condition = isStateActive(state, condition);
		((BlockARHatch)world.getBlockState(pos).getBlock()).setRedstoneState(world,world.getBlockState(pos), pos, condition);
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
		return "block.advancedrocketry.guidancecomputeraccesshatch";
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
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(redstoneControl == buttonId) {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)redstoneState));
		}
		else {
			buttonState[(int)buttonId.getAdditionalData()] = !buttonState[(int)buttonId.getAdditionalData()];
			PacketHandler.sendToServer(new PacketMachine(this, (byte)buttonId.getAdditionalData()));
		}

	}

	@Override
	public void stateUpdated(ModuleBase module) {

	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
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
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		if(packetId == redstoneState)
			nbt.putByte("state", in.readByte());
		else {

			nbt.putShort("status", in.readShort());

		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
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
	public CompoundNBT write(CompoundNBT nbt) {

		nbt.putBoolean("chipEjected", chipEjected);
		nbt.putByte("redstoneState", (byte) state.ordinal());

		short status = 0;
		for(int i = 0; i < buttonState.length; i++) {
			status += buttonState[i] ? 1<<i : 0; 
		}

		nbt.putShort("statuses", status);

		return super.write(nbt);
	}

	@Override
	public void read(BlockState blkstate, CompoundNBT nbt) {
		chipEjected = nbt.getBoolean("chipEjected");
		state = RedstoneState.values()[nbt.getByte("redstoneState")];

		short status = nbt.getShort("statuses");
		for(int i = 0; i < buttonState.length; i++) {
			buttonState[i] = (status & 1<<i) != 0; 
		}

		super.read(blkstate, nbt);
	}

	@Override
	public boolean isEmpty() {
		return getStackInSlot(0).isEmpty();
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
