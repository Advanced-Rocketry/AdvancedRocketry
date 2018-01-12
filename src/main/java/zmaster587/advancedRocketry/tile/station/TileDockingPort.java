package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.IGuiCallback;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleTextBox;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;

public class TileDockingPort extends TileEntity implements IModularInventory, IGuiCallback, INetworkMachine {

	ModuleTextBox myId, targetId;
	String targetIdStr, myIdStr;

	public TileDockingPort() {
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
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
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
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.getNbtCompound());
		
		if(targetId != null) {
			targetId.setText(targetIdStr);
			myId.setText(myIdStr);
		}
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		
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

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(!myIdStr.isEmpty())
			nbt.setString("myId", myIdStr);
		if(!targetIdStr.isEmpty())
			nbt.setString("targetId", targetIdStr);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		myIdStr = nbt.getString("myId");
		targetIdStr = nbt.getString("targetId");
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		unregisterTileWithStation(world, pos);
	}

	
	@Override
	public void onLoad() {
		super.onLoad();
		registerTileWithStation(world, pos);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.stationMarker.name";
	}


	public void registerTileWithStation(World world, BlockPos pos) {
		if(!world.isRemote && world.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

			if(spaceObj instanceof SpaceObject) {
				((SpaceObject)spaceObj).addDockingPosition(pos, myIdStr);
			}
		}
	}

	public void unregisterTileWithStation(World world, BlockPos pos) {
		if(!world.isRemote && world.provider.getDimension() == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObj instanceof SpaceObject)
				((SpaceObject)spaceObj).removeDockingPosition(pos);
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
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
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		int len = in.readInt();
		PacketBuffer buff = new PacketBuffer(in);
		nbt.setString("id", buff.readString(len));
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			myIdStr = nbt.getString("id");
			if(!world.isRemote && world.provider.getDimension() == Configuration.spaceDimId) {
				ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(spaceObj instanceof SpaceObject) {
					((SpaceObject)spaceObj).addDockingPosition(pos, myIdStr);
				}
			}
		}
		else if(id == 1)  {
			targetIdStr = nbt.getString("id");
		}
		markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
	}
}
