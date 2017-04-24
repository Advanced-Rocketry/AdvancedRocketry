package zmaster587.advancedRocketry.network;

import java.io.IOException;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.network.BasePacket;

public class PacketStationUpdate extends BasePacket {
	ISpaceObject spaceObject;
	int stationNumber;
	Type type;

	public enum Type {
		DEST_ORBIT_UPDATE,
		ORBIT_UPDATE,
		SIGNAL_WHITE_BURST,
		FUEL_UPDATE,
		ROTANGLE_UPDATE, 
		DIM_PROPERTY_UPDATE,
		ALTITUDE_UPDATE
	}

	public PacketStationUpdate() {}

	public PacketStationUpdate(ISpaceObject dimProperties, Type type) {
		this.spaceObject = (SpaceObject)dimProperties;
		this.stationNumber = dimProperties.getId();
		this.type = type;
	}

	@Override
	public void write(ByteBuf out) {
		out.writeInt(stationNumber);
		out.writeInt(type.ordinal());

		switch(type) {
		case DEST_ORBIT_UPDATE:
			out.writeInt(spaceObject.getDestOrbitingBody());
			break;
		case ORBIT_UPDATE:
			out.writeInt(spaceObject.getOrbitingPlanetId());
			break;
		case FUEL_UPDATE:
			if(spaceObject instanceof SpaceObject)
				out.writeInt(((SpaceObject)spaceObject).getFuelAmount());
			break;
		case ROTANGLE_UPDATE:
			out.writeDouble(spaceObject.getRotation(ForgeDirection.EAST));
			out.writeDouble(spaceObject.getRotation(ForgeDirection.UP));
			out.writeDouble(spaceObject.getRotation(ForgeDirection.NORTH));
			out.writeDouble(spaceObject.getDeltaRotation(ForgeDirection.EAST));
			out.writeDouble(spaceObject.getDeltaRotation(ForgeDirection.UP));
			out.writeDouble(spaceObject.getDeltaRotation(ForgeDirection.NORTH));
			break;
		case ALTITUDE_UPDATE:
			out.writeFloat(spaceObject.getOrbitalDistance());
			break;
		case DIM_PROPERTY_UPDATE:
			NBTTagCompound nbt = new NBTTagCompound();
			try {
				spaceObject.getProperties().writeToNBT(nbt);
				PacketBuffer packetBuffer = new PacketBuffer(out);
				//TODO: error handling
				try {
					packetBuffer.writeNBTTagCompoundToBuffer(nbt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch(NullPointerException e) {
				out.writeBoolean(true);
				Logger.getLogger("advancedRocketry").warning("Dimension " + stationNumber + " has thrown an exception trying to write NBT, deleting!");
				DimensionManager.getInstance().deleteDimension(stationNumber);
			}
		default:
		}
	}

	@Override
	public void readClient(ByteBuf in) {
		stationNumber = in.readInt();
		spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStation(stationNumber);
		type = Type.values()[in.readInt()];


		switch(type) {
		case DEST_ORBIT_UPDATE:
			spaceObject.setDestOrbitingBody(in.readInt());
			break;
		case ORBIT_UPDATE:
			spaceObject.setOrbitingBody(in.readInt());
			break;
		case FUEL_UPDATE:
			if(spaceObject instanceof SpaceObject)
				((SpaceObject)spaceObject).setFuelAmount(in.readInt());
			break;
		case ROTANGLE_UPDATE:
			spaceObject.setRotation(in.readDouble(), ForgeDirection.EAST);
			spaceObject.setRotation(in.readDouble(), ForgeDirection.UP);
			spaceObject.setRotation(in.readDouble(), ForgeDirection.NORTH);
			spaceObject.setDeltaRotation(in.readDouble(), ForgeDirection.EAST);
			spaceObject.setDeltaRotation(in.readDouble(), ForgeDirection.UP);
			spaceObject.setDeltaRotation(in.readDouble(), ForgeDirection.NORTH);
			break;
		case SIGNAL_WHITE_BURST:
			PlanetEventHandler.runBurst(Minecraft.getMinecraft().theWorld.getTotalWorldTime() + 20, 20);
			break;
		case ALTITUDE_UPDATE:
			spaceObject.setOrbitalDistance(in.readFloat());
			break;
		case DIM_PROPERTY_UPDATE:
			PacketBuffer packetBuffer = new PacketBuffer(in);
			NBTTagCompound nbt;
			try {
				nbt = packetBuffer.readNBTTagCompoundFromBuffer();

			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			spaceObject.getProperties().readFromNBT(nbt);
			break;
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
