package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.event.RocketEventHandler;

public class PacketStationUpdate extends BasePacket {
	ISpaceObject spaceObject;
	int stationNumber;
	Type type;

	public enum Type {
		DEST_ORBIT_UPDATE,
		ORBIT_UPDATE,
		SIGNAL_WHITE_BURST
	}
	
	public PacketStationUpdate() {}

	public PacketStationUpdate(ISpaceObject dimProperties, Type type) {
		this.spaceObject = dimProperties;
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
	}

	@Override
	public void readClient(ByteBuf in) {
		stationNumber = in.readInt();
		spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStation(stationNumber);
		type = Type.values()[in.readInt()];
		if(type == Type.DEST_ORBIT_UPDATE)
			spaceObject.setDestOrbitingBody(in.readInt());
		else if(type == Type.ORBIT_UPDATE) {
			spaceObject.setOrbitingBody(in.readInt());
		}
		else if(type == Type.SIGNAL_WHITE_BURST) {
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
