package zmaster587.advancedRocketry.tile.station;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class TileDockingPort extends TileEntity implements IModularInventory, IGuiCallback, INetworkMachine {

	ModuleTextBox myId, targetId;
	String targetIdStr, myIdStr;

	public TileDockingPort() {
		super(AdvancedRocketryTileEntityType.TILE_DOCKING_PORT);
		targetIdStr = "";
		myIdStr = "";
	}

	public String getTargetId() {
		return targetIdStr;
	}

	public String getMyId() {
		return myIdStr;
	}

	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();
		modules.add(new ModuleText(20, 50, LibVulpes.proxy.getLocalizedString("msg.dockingport.target"), 0x2a2a2a));
		if(world.isRemote) {
			myId = new ModuleTextBox(this, 20, 30, 60, 12, 9);
			targetId = new ModuleTextBox(this, 20, 60, 60, 12, 9);
			targetId.setText(targetIdStr);
			myId.setText(myIdStr);

			modules.add(targetId);
			modules.add(myId);
		}


		modules.add(new ModuleText(20, 20, LibVulpes.proxy.getLocalizedString("msg.dockingport.me"), 0x2a2a2a));

		return modules;
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, write(new CompoundNBT()));
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		read(getBlockState(), pkt.getNbtCompound());
		
		if(targetId != null) {
			targetId.setText(targetIdStr);
			myId.setText(myIdStr);
		}
	}
	
	@Nonnull
	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}
	
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		
		if(targetId != null) {
			targetId.setText(targetIdStr);
			myId.setText(myIdStr);
		}
	}

	@Override
	public void onModuleUpdated(ModuleBase module) {
		if(module == myId) {
			myIdStr =  myId.getText();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
		} else if(module == targetId) {
			targetIdStr =  targetId.getText();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)1));
		}
	}

	@Nonnull
	@Override
	@ParametersAreNonnullByDefault
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		if(!myIdStr.isEmpty())
			nbt.putString("myId", myIdStr);
		if(!targetIdStr.isEmpty())
			nbt.putString("targetId", targetIdStr);

		return nbt;
	}

	@Override
	@ParametersAreNonnullByDefault
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		myIdStr = nbt.getString("myId");
		targetIdStr = nbt.getString("targetId");
	}

	@Override
	public boolean canInteractWithContainer(PlayerEntity entity) {
		return true;
	}

	@Override
	public void remove() {
		super.remove();
		unregisterTileWithStation(world, pos);
	}

	
	@Override
	public void onLoad() {
		super.onLoad();
		registerTileWithStation(world, pos);
	}

	@Override
	public String getModularInventoryName() {
		return "block.advancedrocketry.stationdockingport";
	}


	public void registerTileWithStation(World world, BlockPos pos) {
		if(!world.isRemote && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

			if(spaceObj instanceof SpaceStationObject) {
				((SpaceStationObject)spaceObj).addDockingPosition(pos, myIdStr);
			}
		}
	}

	public void unregisterTileWithStation(World world, BlockPos pos) {
		if(!world.isRemote && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObj instanceof SpaceStationObject)
				((SpaceStationObject)spaceObj).removeDockingPosition(pos);
		}
	}

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		if(id == 0) {
			PacketBuffer buff = new PacketBuffer(out);
			buff.writeInt(myIdStr.length());
			buff.writeString(myIdStr);
		}
		else if(id == 1) {
			PacketBuffer buff = new PacketBuffer(out);
			buff.writeInt(targetIdStr.length());
			buff.writeString(targetIdStr);
		}
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		int len = in.readInt();
		PacketBuffer buff = new PacketBuffer(in);
		nbt.putString("id", buff.readString(len));
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		if(id == 0) {
			myIdStr = nbt.getString("id");
			if(!world.isRemote && DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
				ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(spaceObj instanceof SpaceStationObject) {
					((SpaceStationObject)spaceObj).addDockingPosition(pos, myIdStr);
				}
			}
		}
		else if(id == 1)  {
			targetIdStr = nbt.getString("id");
		}
		markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
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
		return guiId.MODULAR;
	}
}
