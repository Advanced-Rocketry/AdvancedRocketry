package zmaster587.advancedRocketry.network;

import java.io.IOException;
import java.util.logging.Logger;

import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.ForgeDirection;

public class PacketSpaceStationInfo extends BasePacket {
	ISpaceObject spaceObject;
	int stationNumber;

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
				spaceObject.getProperties().writeToNBT(nbt);
				PacketBuffer packetBuffer = new PacketBuffer(out);
				out.writeBoolean(false);
				//TODO: error handling
				try {
					packetBuffer.writeStringToBuffer(SpaceObjectManager.getSpaceManager().getItentifierFromClass(spaceObject.getClass()));
					packetBuffer.writeNBTTagCompoundToBuffer(nbt);
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		NBTTagCompound nbt;
		stationNumber = in.readInt();

		//Is dimension being deleted
		if(in.readBoolean()) {
			if(DimensionManager.getInstance().isDimensionCreated(stationNumber)) {
				DimensionManager.getInstance().deleteDimension(stationNumber);
			}
		}
		else {
			//TODO: error handling
			int direction;
			String clazzId;
			try {
				clazzId = packetBuffer.readStringFromBuffer(127);
				nbt = packetBuffer.readNBTTagCompoundFromBuffer();
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			
			ISpaceObject iObject = SpaceObjectManager.getSpaceManager().getSpaceStation(stationNumber);
			//TODO: interface
			spaceObject = (SpaceObject)iObject;
			
			//Station needs to be created
			if( iObject == null ) {
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getNewSpaceObjectFromIdentifier(clazzId);
				object.setProperties(DimensionProperties.createFromNBT(stationNumber, nbt));
				
				SpaceObjectManager.getSpaceManager().registerSpaceObjectClient(object, object.getOrbitingPlanetId(), stationNumber);
			}
			else {
				iObject.setProperties(DimensionProperties.createFromNBT(stationNumber, nbt));
			}
		}
	}

	@Override
	public void read(ByteBuf in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {}

	@Override
	public void executeServer(EntityPlayerMP player) {}

}
