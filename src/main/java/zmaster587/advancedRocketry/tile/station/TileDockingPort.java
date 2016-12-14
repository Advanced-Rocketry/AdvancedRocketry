package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
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
		modules.add(new ModuleText(20, 50, "Target Id", 0x2a2a2a));
		if(worldObj.isRemote) {
			myId = new ModuleTextBox(this, 20, 30, 60, 12, 9);
			targetId = new ModuleTextBox(this, 20, 60, 60, 12, 9);
			targetId.setText(targetIdStr);
			myId.setText(myIdStr);

			modules.add(targetId);
			modules.add(myId);
		}


		modules.add(new ModuleText(20, 20, "My Id", 0x2a2a2a));

		return modules;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0,nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
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
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(!myIdStr.isEmpty())
			nbt.setString("myId", myIdStr);
		if(!targetIdStr.isEmpty())
			nbt.setString("targetId", targetIdStr);
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
		unregisterTileWithStation(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public String getModularInventoryName() {
		return "tile.stationMarker.name";
	}


	public void registerTileWithStation(World world, int x, int y, int z) {
		if(!world.isRemote && world.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);

			if(spaceObj instanceof SpaceObject) {
				((SpaceObject)spaceObj).addDockingPosition(x, y, z, myIdStr);
			}
		}
	}

	public void unregisterTileWithStation(World world, int x, int y, int z) {
		if(!world.isRemote && world.provider.dimensionId == Configuration.spaceDimId) {
			ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(x, z);
			if(spaceObj instanceof SpaceObject)
				((SpaceObject)spaceObj).removeDockingPosition(x, y, z);
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0) {
			PacketBuffer buff = new PacketBuffer(out);
			try {
				buff.writeInt(myIdStr.length());
				buff.writeStringToBuffer(myIdStr);
			} catch (IOException e) {
				//Silent
			}
		}
		else if(id == 1) {
			PacketBuffer buff = new PacketBuffer(out);
			try {
				buff.writeInt(targetIdStr.length());
				buff.writeStringToBuffer(targetIdStr);
			} catch (IOException e) {
				//Silent
			}
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		int len = in.readInt();
		PacketBuffer buff = new PacketBuffer(in);
		try {
			nbt.setString("id", buff.readStringFromBuffer(len));
		} catch (IOException e) {

		}

	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			myIdStr = nbt.getString("id");
			if(!worldObj.isRemote && worldObj.provider.dimensionId == Configuration.spaceDimId) {
				ISpaceObject spaceObj = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(xCoord, zCoord);

				if(spaceObj instanceof SpaceObject) {
					((SpaceObject)spaceObj).addDockingPosition(xCoord, yCoord, zCoord, myIdStr);
				}
			}
		}
		else if(id == 1)  {
			targetIdStr = nbt.getString("id");
		}
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
}
