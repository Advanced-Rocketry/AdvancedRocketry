package zmaster587.advancedRocketry.tile.infrastructure;

import io.netty.buffer.ByteBuf;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.block.multiblock.BlockARHatch;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.TileRocketAssemblingMachine;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

import javax.annotation.Nonnull;
import java.util.List;

public class TileRocketLoader extends TileInventoryHatch implements IInfrastructure, ITickable,  IButtonInventory, INetworkMachine, IGuiCallback  {

	EntityRocket rocket;
	ModuleRedstoneOutputButton redstoneControl;
	RedstoneState state;
	ModuleRedstoneOutputButton inputRedstoneControl;
	RedstoneState inputstate;
	ModuleBlockSideSelector sideSelectorModule;

	private final static int ALLOW_REDSTONEOUT = 2;

	public TileRocketLoader() {
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this, LibVulpes.proxy.getLocalizedString("msg.rocketLoader.loadingState"));
		state = RedstoneState.ON;
		inputRedstoneControl = new ModuleRedstoneOutputButton(174, 32, 1, "", this, LibVulpes.proxy.getLocalizedString("msg.rocketLoader.allowLoading"));
		inputstate = RedstoneState.OFF;
		inputRedstoneControl.setRedstoneState(inputstate);
		sideSelectorModule = new ModuleBlockSideSelector(90, 15, this, LibVulpes.proxy.getLocalizedString("msg.rocketLoader.none"), LibVulpes.proxy.getLocalizedString("msg.rocketLoader.allowredstoneoutput"), LibVulpes.proxy.getLocalizedString("msg.rocketLoader.allowredstoneinput"));
	}

	public TileRocketLoader(int size) {
		super(size);
		inventory.setCanInsertSlot(0, true);
		inventory.setCanInsertSlot(1, true);
		inventory.setCanInsertSlot(2, true);
		inventory.setCanInsertSlot(3, true);
		inventory.setCanExtractSlot(0, false);
		inventory.setCanExtractSlot(1, false);
		inventory.setCanExtractSlot(2, false);
		inventory.setCanExtractSlot(3, false);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, 0, "", this, LibVulpes.proxy.getLocalizedString("msg.rocketLoader.loadingState"));
		state = RedstoneState.ON;
		inputRedstoneControl = new ModuleRedstoneOutputButton(174, 32, 1, "", this, LibVulpes.proxy.getLocalizedString("msg.rocketLoader.allowLoading"));
		inputstate = RedstoneState.OFF;
		inputRedstoneControl.setRedstoneState(inputstate);
		sideSelectorModule = new ModuleBlockSideSelector(90, 15, this, LibVulpes.proxy.getLocalizedString("msg.rocketLoader.none"), LibVulpes.proxy.getLocalizedString("msg.rocketLoader.allowredstoneoutput"), LibVulpes.proxy.getLocalizedString("msg.rocketLoader.allowredstoneinput"));

	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(getMasterBlock() instanceof TileRocketAssemblingMachine)
			((TileRocketAssemblingMachine)getMasterBlock()).removeConnectedInfrastructure(this);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.loader.3.name";
	}

	@Override
	public boolean allowRedstoneOutputOnSide(EnumFacing facing) {
		return sideSelectorModule.getStateForSide(facing.getOpposite()) == 1;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> list = super.getModules(ID, player);
		list.add(redstoneControl);
		list.add(inputRedstoneControl);
		list.add(sideSelectorModule);
		return list;
	}

	protected boolean getStrongPowerForSides(World world, BlockPos pos) {
		for(int i = 0; i < 6; i++) {
			if(sideSelectorModule.getStateForSide(i) == ALLOW_REDSTONEOUT && world.getRedstonePower(pos.offset(EnumFacing.VALUES[i]), EnumFacing.VALUES[i]) > 0)
				return true;
		}
		return false;
	}

	@Override
	public void update() {
		//Move a stack of items
		if(!world.isRemote && rocket != null ) {

			boolean isAllowedToOperate = (inputstate == RedstoneState.OFF || isStateActive(inputstate, getStrongPowerForSides(world, getPos())));

			List<TileEntity> tiles = rocket.storage.getInventoryTiles();
			boolean foundStack = false;
			boolean rocketContainsItems = false;
			out:
				//Function returns if something can be moved
				for(TileEntity tile : tiles) {
					if(tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
						IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

						for(int i = 0; i < inv.getSlots(); i++) {
							if(inv.getStackInSlot(i).isEmpty())
								rocketContainsItems = true;

							//Loop though this inventory's slots and find a suitible one
							for(int j = 0; j < getSizeInventory(); j++) {
								if((inv.getStackInSlot(i).isEmpty()) && !inventory.getStackInSlot(j).isEmpty()) {
									if(isAllowedToOperate) {
										inv.insertItem(i, inventory.getStackInSlot(j), false);
										inventory.setInventorySlotContents(j,ItemStack.EMPTY);
									}
									rocketContainsItems = true;
									break out;
								}
								else if(!getStackInSlot(j).isEmpty() && inv.getStackInSlot(i).getItem() == getStackInSlot(j).getItem() &&
										ItemStack.areItemStackTagsEqual(inv.getStackInSlot(i), getStackInSlot(j)) && inv.getStackInSlot(i).getMaxStackSize() != inv.getStackInSlot(i).getCount() ) {
									if(isAllowedToOperate) {
										ItemStack stack2 = inventory.decrStackSize(j, inv.getStackInSlot(i).getMaxStackSize() - inv.getStackInSlot(i).getCount());
										inv.getStackInSlot(i).setCount(inv.getStackInSlot(i).getCount() + stack2.getCount());
									}
									rocketContainsItems = true;

									if(inventory.getStackInSlot(j).isEmpty())
										break out;

									foundStack = true;
								}
							}
							if(foundStack)
								break out;
						}
					}
					else {
						if(tile instanceof IInventory && !(tile instanceof TileGuidanceComputer)) {
							IInventory inv = ((IInventory)tile);

							for(int i = 0; i < inv.getSizeInventory(); i++) {
								if(inv.getStackInSlot(i).isEmpty())
									rocketContainsItems = true;

								//Loop though this inventory's slots and find a suitible one
								for(int j = 0; j < getSizeInventory(); j++) {
									if((inv.getStackInSlot(i).isEmpty()) && !inventory.getStackInSlot(j).isEmpty()) {
										if(isAllowedToOperate) {
											inv.setInventorySlotContents(i, inventory.getStackInSlot(j));
											inventory.setInventorySlotContents(j,ItemStack.EMPTY);
										}
										rocketContainsItems = true;
										break out;
									}
									else if(!getStackInSlot(j).isEmpty() && inv.isItemValidForSlot(i, getStackInSlot(j)) && inv.getStackInSlot(i).getItem() == getStackInSlot(j).getItem() &&
											ItemStack.areItemStackTagsEqual(inv.getStackInSlot(i), getStackInSlot(j)) && inv.getStackInSlot(i).getMaxStackSize() != inv.getStackInSlot(i).getCount() ) {
										if(isAllowedToOperate) {
											ItemStack stack2 = inventory.decrStackSize(j, inv.getStackInSlot(i).getMaxStackSize() - inv.getStackInSlot(i).getCount());
											inv.getStackInSlot(i).setCount(inv.getStackInSlot(i).getCount() + stack2.getCount());
										}
										rocketContainsItems = true;

										if(inventory.getStackInSlot(j).isEmpty())
											break out;

										foundStack = true;
									}
								}
								if(foundStack)
									break out;
							}
						}
					}
				}

			//Update redstone state
			setRedstoneState(!rocketContainsItems);

		}
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

	protected void setRedstoneState(boolean condition) {
		condition = isStateActive(state, condition);
		((BlockARHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(world,world.getBlockState(pos), pos, condition);
	}

	protected boolean isStateActive(RedstoneState state, boolean condition) {
		if(state == RedstoneState.INVERTED)
			return !condition;
		else if(state == RedstoneState.OFF)
			return false;
		return condition;
	}

	@Override
	public boolean onLinkStart(@Nonnull ItemStack item, TileEntity entity,
							   EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, this.pos);

		if(this.rocket != null) {
			this.rocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}

		if(player.world.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("%s %s",new TextComponentTranslation("msg.rocketLoader.link"), ": " + getPos().getX() + " " + getPos().getY() + " " + getPos().getZ()));
		return true;
	}

	@Override
	public boolean onLinkComplete(@Nonnull ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		if(player.world.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("msg.linker.error.firstMachine"));
		return false;
	}

	@Override
	public void unlinkRocket() {
		rocket = null;
		((BlockARHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(world, world.getBlockState(pos), pos, false);
		//On unlink prevent the tile from ticking anymore

		//if(!worldObj.isRemote)
		//worldObj.loadedTileEntityList.remove(this);
	}

	@Override
	public boolean disconnectOnLiftOff() {
		return true;
	}

	@Override
	public boolean linkRocket(EntityRocketBase rocket) {
		//On linked allow the tile to tick
		//if(!worldObj.isRemote)
		//worldObj.loadedTileEntityList.add(this);
		this.rocket = (EntityRocket) rocket;
		return true;
	}

	@Override
	public boolean canUpdate() {
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
	public int getMaxLinkDistance() {
		return 32;
	}

	public boolean canRenderConnection() {
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);

		inputstate = RedstoneState.values()[nbt.getByte("inputRedstoneState")];
		inputRedstoneControl.setRedstoneState(inputstate);

		sideSelectorModule.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("redstoneState", (byte) state.ordinal());
		nbt.setByte("inputRedstoneState", (byte) inputstate.ordinal());
		sideSelectorModule.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId == 0)
			state = redstoneControl.getState();
		if(buttonId == 1)
			inputstate = inputRedstoneControl.getState();
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		out.writeByte(state.ordinal());
		out.writeByte(inputstate.ordinal());
		for(int i = 0; i < 6; i++)
			out.writeByte(sideSelectorModule.getStateForSide(i));
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		nbt.setByte("state", in.readByte());
		nbt.setByte("inputstate", in.readByte());

		byte[] bytes = new byte[6];
		for(int i = 0; i < 6; i++)
			bytes[i] = in.readByte();
		nbt.setByteArray("bytes", bytes);
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		state = RedstoneState.values()[nbt.getByte("state")];
		inputstate = RedstoneState.values()[nbt.getByte("inputstate")];

		byte[] bytes = nbt.getByteArray("bytes");
		for(int i = 0; i < 6; i++)
			sideSelectorModule.setStateForSide(i, bytes[i]);

		if(rocket == null)
			setRedstoneState(state == RedstoneState.INVERTED);

		markDirty();
		world.markChunkDirty(getPos(), this);
	}


	@Override
	public void onModuleUpdated(ModuleBase module) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
