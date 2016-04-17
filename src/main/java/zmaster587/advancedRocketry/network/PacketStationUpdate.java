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
		FUEL_UPDATE
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
		
		if(type == Type.DEST_ORBIT_UPDATE)
			out.writeInt(spaceObject.getDestOrbitingBody());
		else if(type == Type.ORBIT_UPDATE)
			out.writeInt(spaceObject.getOrbitingPlanetId());
		else if(type == Type.FUEL_UPDATE)
			out.writeInt(spaceObject.getFuelAmount());
	}

	@Override
	public void readClient(ByteBuf in) {
		stationNumber = in.readInt();
		spaceObject = (SpaceObject)SpaceObjectManager.getSpaceManager().getSpaceStation(stationNumber);
		type = Type.values()[in.readInt()];
		if(type == Type.DEST_ORBIT_UPDATE)
			spaceObject.setDestOrbitingBody(in.readInt());
		else if(type == Type.ORBIT_UPDATE) {
			spaceObject.setOrbitingBody(in.readInt());
		}
		else if(type == Type.SIGNAL_WHITE_BURST) {
			PlanetEventHandler.runBurst(Minecraft.getMinecraft().theWorld.getTotalWorldTime() + 20, 20);
		}
		else if(type == Type.FUEL_UPDATE)
			spaceObject.setFuelAmount(in.readInt());
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
