package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;
import java.util.logging.Logger;

public class PacketSpaceStationInfo extends BasePacket {
	SpaceObject spaceObject;
	int stationNumber;
	boolean isBeingDeleted;
	int direction;
	String clazzId;
	int fuelAmt;
	NBTTagCompound nbt;
	boolean hasWarpCores;
	
	public PacketSpaceStationInfo() {}

	public PacketSpaceStationInfo(int stationNumber, ISpaceObject spaceObject) {
		this.spaceObject = (SpaceObject)spaceObject;
		this.stationNumber = stationNumber;
	}

	@Override
	public void write(ByteBuf out) {
		NBTTagCompound nbt = new NBTTagCompound();
		out.writeInt(stationNumber);
		boolean flag = false; //TODO //dimProperties == null;
		
		if(!flag) {
			
			//Try to send the nbt data of the dimension to the client, if it fails(probably due to non existent Biome ids) then remove the dimension
			try {
				spaceObject.writeToNbt(nbt);
				//spaceObject.getProperties().writeToNBT(nbt);
				PacketBuffer packetBuffer = new PacketBuffer(out);
				out.writeBoolean(false);
				packetBuffer.writeString(SpaceObjectManager.getSpaceManager().getItentifierFromClass(spaceObject.getClass()));
				packetBuffer.writeCompoundTag(nbt);
				
				packetBuffer.writeInt(spaceObject.getFuelAmount());
				packetBuffer.writeBoolean(spaceObject.hasWarpCores);
				
				out.writeInt(spaceObject.getForwardDirection().ordinal());
				
			} catch(NullPointerException e) {
				out.writeBoolean(true);
				Logger.getLogger("advancedRocketry").warning("Dimension " + stationNumber + " has thrown an exception trying to write NBT, deleting!");
				DimensionManager.getInstance().deleteDimension(stationNumber);
			}

		}
		else
			out.writeBoolean(flag);

	}

	@Override
	public void readClient(ByteBuf in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);
		
		stationNumber = in.readInt();

		//Is dimension being deleted
		isBeingDeleted = in.readBoolean();
		if(!isBeingDeleted) {
			//TODO: error handling

			try {
				clazzId = packetBuffer.readString(127);
				nbt = packetBuffer.readCompoundTag();
				fuelAmt = packetBuffer.readInt();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			hasWarpCores = in.readBoolean();
			
			direction = in.readInt();
		}
	}

	@Override
	public void read(ByteBuf in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		if(isBeingDeleted) {
			if(DimensionManager.getInstance().isDimensionCreated(stationNumber)) {
				DimensionManager.getInstance().deleteDimension(stationNumber);
			}
		}
		else {
			ISpaceObject iObject = SpaceObjectManager.getSpaceManager().getSpaceStation(stationNumber);
			spaceObject = (SpaceObject)iObject;
			
			//Station needs to be created
			if( iObject == null ) {
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getNewSpaceObjectFromIdentifier(clazzId);
				object.readFromNbt(nbt);
				object.setProperties(DimensionProperties.createFromNBT(stationNumber, nbt));
				((SpaceObject)object).setForwardDirection(EnumFacing.values()[direction]);
				
				SpaceObjectManager.getSpaceManager().registerSpaceObjectClient(object, object.getOrbitingPlanetId(), stationNumber);
				((SpaceObject)object).setFuelAmount(fuelAmt);
				((SpaceObject)object).hasWarpCores = hasWarpCores;
			}
			else {
				iObject.readFromNbt(nbt);
				//iObject.setProperties(DimensionProperties.createFromNBT(stationNumber, nbt));
				((SpaceObject)iObject).setForwardDirection(EnumFacing.values()[direction]);
				((SpaceObject)iObject).setFuelAmount(fuelAmt);
				((SpaceObject)iObject).hasWarpCores = hasWarpCores;
			}
		}
			
	}

	@Override
	public void executeServer(EntityPlayerMP player) {}

}
