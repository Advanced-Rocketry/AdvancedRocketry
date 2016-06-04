package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import zmaster587.advancedRocketry.stations.SpaceObject;

public class PacketStationUpdate extends BasePacket {
	SpaceObject spaceObject;
	int stationNumber;
	Type type;

	public enum Type {
		DEST_ORBIT_UPDATE,
		ORBIT_UPDATE,
		SIGNAL_WHITE_BURST,
		FUEL_UPDATE,
		ROTANGLE_UPDATE
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
			out.writeInt(spaceObject.getFuelAmount());
			break;
		case ROTANGLE_UPDATE:
			out.writeDouble(spaceObject.getRotation());
			out.writeDouble(spaceObject.getDeltaRotation());
		default:
		}
	}

	@Override
	public void readClient(ByteBuf in) {
		stationNumber = in.readInt();
		spaceObject = (SpaceObject)SpaceObjectManager.getSpaceManager().getSpaceStation(stationNumber);
		type = Type.values()[in.readInt()];


		switch(type) {
		case DEST_ORBIT_UPDATE:
			spaceObject.setDestOrbitingBody(in.readInt());
			break;
		case ORBIT_UPDATE:
			spaceObject.setOrbitingBody(in.readInt());
			break;
		case FUEL_UPDATE:
			spaceObject.setFuelAmount(in.readInt());
			break;
		case ROTANGLE_UPDATE:
			spaceObject.setRotation(in.readDouble());
			spaceObject.setDeltaRotation(in.readDouble());
			break;
		case SIGNAL_WHITE_BURST:
			PlanetEventHandler.runBurst(Minecraft.getMinecraft().theWorld.getTotalWorldTime() + 20, 20);
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
