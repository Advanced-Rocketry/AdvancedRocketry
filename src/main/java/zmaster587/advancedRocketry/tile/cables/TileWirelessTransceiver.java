package zmaster587.advancedRocketry.tile.cables;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.api.DataStorage.DataType;
import zmaster587.advancedRocketry.api.satellite.IDataHandler;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.world.util.MultiData;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IToggleButton;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleToggleSwitch;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class TileWirelessTransceiver extends TileEntity implements INetworkMachine, IModularInventory, ILinkableTile, IDataHandler, ITickableTileEntity, IToggleButton {


	boolean extractMode;
	boolean enabled;
	int networkID;
	MultiData data;
	ModuleToggleSwitch toggle;
	protected ModuleToggleSwitch toggleSwitch;

	public TileWirelessTransceiver() {
		super(AdvancedRocketryTileEntityType.TILE_WIRELESS_TRANSCEIVER);
		networkID = -1;
		data = new MultiData();
		data.setMaxData(100);
		toggle = new ModuleToggleSwitch(50, 50, LibVulpes.proxy.getLocalizedString("msg.wirelesstransciever.extract"), this, TextureResources.buttonGeneric, 64, 18, false);
		toggleSwitch = new ModuleToggleSwitch(160, 5, "", this,  zmaster587.libVulpes.inventory.TextureResources.buttonToggleImage, 11, 26, true);
	}


	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkStart(ItemStack item, TileEntity entity, PlayerEntity player, World world) {
		ItemLinker.setMasterCoords(item, getPos());

		if(world.isRemote)
			player.sendMessage(new TranslationTextComponent("msg.linker.program"), Util.DUMMY_UUID);

		return true;
	}

	@Override
	public void onChunkUnloaded() {
		super.onChunkUnloaded();
		if(NetworkRegistry.dataNetwork.doesNetworkExist(networkID))
			NetworkRegistry.dataNetwork.getNetwork(networkID).removeFromAll(this);
	}

	@Override
	@ParametersAreNonnullByDefault
	public boolean onLinkComplete(ItemStack item, TileEntity entity, PlayerEntity player, World world) {
		BlockPos pos = ItemLinker.getMasterCoords(item);

		TileEntity tile = world.getTileEntity(pos);

		if(tile instanceof TileWirelessTransceiver) {
			if(world.isRemote) {
				player.sendMessage(new TranslationTextComponent("msg.linker.success"), Util.DUMMY_UUID);
				return true;
			}

			int otherNetworkId = ((TileWirelessTransceiver)tile).networkID;

			if(networkID == -1 && otherNetworkId == -1) {
				networkID = NetworkRegistry.dataNetwork.getNewNetworkID();
				((TileWirelessTransceiver)tile).networkID = networkID;

			} else if(networkID == -1) {
				networkID = otherNetworkId;
			} else if(otherNetworkId == -1) {
				((TileWirelessTransceiver)tile).networkID = networkID;
			} else {
				networkID = NetworkRegistry.dataNetwork.mergeNetworks(otherNetworkId, networkID);
				((TileWirelessTransceiver)tile).networkID = networkID;
			}
			addToNetwork();
			((TileWirelessTransceiver)tile).addToNetwork();

			ItemLinker.resetPosition(item);

			return true;
		}

		return false;
	}

	private void addToNetwork() {
		if(networkID == -1 || world.isRemote)
			return;
		else if(!NetworkRegistry.dataNetwork.doesNetworkExist(networkID))
			NetworkRegistry.dataNetwork.getNewNetworkID(networkID);

		if(extractMode) {
			NetworkRegistry.dataNetwork.getNetwork(networkID).addSource(this, Direction.UP);
		} else {
			NetworkRegistry.dataNetwork.getNetwork(networkID).addSink(this, Direction.UP);
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);

		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}

	@Override 
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(getBlockState(), pkt.getNbtCompound());
	}

	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {LinkedList<ModuleBase> list = new LinkedList<>();
		list.add(toggle);
		list.add(toggleSwitch);

		return list;
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.wirelesstransceiver";
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == 0)
			out.writeBoolean(toggle.getState());
		else if(id == 1)
			out.writeBoolean(toggleSwitch.getState());
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId, CompoundNBT nbt) {
		nbt.putBoolean("state", in.readBoolean());

	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id, CompoundNBT nbt) {
		if(side.isDedicatedServer()) {
			if(id == 0) {
				extractMode = nbt.getBoolean("state");
				if(NetworkRegistry.dataNetwork.doesNetworkExist(networkID)) {
					NetworkRegistry.dataNetwork.getNetwork(networkID).removeFromAll(this);

					if(extractMode) 
						NetworkRegistry.dataNetwork.getNetwork(networkID).addSource(this, Direction.UP);
					else
						NetworkRegistry.dataNetwork.getNetwork(networkID).addSink(this, Direction.UP);
				}
			} else if(id == 1) {
				enabled = nbt.getBoolean("state");
			}
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		extractMode = nbt.getBoolean("mode");
		enabled = nbt.getBoolean("enabled");
		networkID = nbt.getInt("networkID");
		data.readFromNBT(nbt);
		//addToNetwork();

		toggle.setToggleState(extractMode);
		toggleSwitch.setToggleState(enabled);
	}

	@Nonnull
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt.putBoolean("mode", extractMode);
		nbt.putBoolean("enabled", enabled);
		nbt.putInt("networkID", networkID);
		data.writeToNBT(nbt);
		return super.write(nbt);
	}

	@Override
	public int extractData(int maxAmount, DataType type, Direction dir, boolean commit) {
		return enabled ? data.extractData(maxAmount, type, dir, commit) : 0;
	}

	@Override
	public int addData(int maxAmount, DataType type, Direction dir, boolean commit) {
		return enabled ? data.addData(maxAmount, type, dir, commit) : 0;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if(!world.isRemote) {
			if(!NetworkRegistry.dataNetwork.doesNetworkExist(networkID))
				NetworkRegistry.dataNetwork.getNewNetworkID(networkID);
			
			NetworkRegistry.dataNetwork.getNetwork(networkID).removeFromAll(this);

			if(extractMode) 
				NetworkRegistry.dataNetwork.getNetwork(networkID).addSource(this, Direction.UP);
			else
				NetworkRegistry.dataNetwork.getNetwork(networkID).addSink(this, Direction.UP);

		}
	}

	@Override
	public void tick() {
		if(!world.isRemote) {
			BlockState state = world.getBlockState(getPos());
			if (state.getBlock() instanceof RotatableBlock) {
				Direction facing = RotatableBlock.getFront(state).getOpposite();

				TileEntity tile = world.getTileEntity(getPos().add(facing.getXOffset(),facing.getYOffset(),facing.getZOffset()));

				if( tile instanceof IDataHandler && !(tile instanceof TileWirelessTransceiver)) {
					for(DataType data : DataType.values()) {

						if(data == DataStorage.DataType.UNDEFINED)
							continue;

						if(!extractMode) {
							int amountCurrent = this.data.getDataAmount(data);
							if (amountCurrent > 0) {
								int amt = ((IDataHandler)tile).addData(amountCurrent, data, facing.getOpposite(), true);
								this.data.extractData(amt, data, facing.getOpposite(), true);
							}
						} else {
							int amt = ((IDataHandler)tile).extractData(this.data.getMaxData() - this.data.getDataAmount(data), data, facing.getOpposite(), true);
							this.data.addData(amt, data, facing.getOpposite(), true);
						}
					}
				}
			}
		}
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(buttonId == toggleSwitch)
			enabled = toggleSwitch.getState();
		else if(buttonId == toggle)
			extractMode = toggle.getState();
		PacketHandler.sendToServer(new PacketMachine(this, (byte)(buttonId == toggle ? 0  : 1)));
	}

	@Override
	public void stateUpdated(ModuleBase module) {
		if(module == toggleSwitch)
			enabled = toggleSwitch.getState();
		else if(module == toggle)
			extractMode = toggle.getState();

		if(!world.isRemote) {
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
		}
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
