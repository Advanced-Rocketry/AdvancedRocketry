package zmaster587.advancedRocketry.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.network.BasePacket;

import java.util.logging.Logger;

public class PacketStationUpdate extends BasePacket {
	ISpaceObject spaceObject;
	ResourceLocation stationNumber;
	Type type;
	
	ResourceLocation destOrbitingBody;
	int fuel;
	double rx,ry,rz,drx,dry,drz;
	float orbitalDistance;
	CompoundNBT nbt;

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
		this.spaceObject = dimProperties;
		this.stationNumber = dimProperties.getId();
		this.type = type;
	}

	@Override
	public void write(PacketBuffer out) {
		out.writeResourceLocation(stationNumber);
		out.writeInt(type.ordinal());

		switch(type) {
		case DEST_ORBIT_UPDATE:
			out.writeResourceLocation(spaceObject.getDestOrbitingBody());
			break;
		case ORBIT_UPDATE:
			out.writeResourceLocation(spaceObject.getOrbitingPlanetId());
			break;
		case FUEL_UPDATE:
			if(spaceObject instanceof SpaceStationObject)
				out.writeInt(((SpaceStationObject)spaceObject).getFuelAmount());
			break;
		case ROTANGLE_UPDATE:
			out.writeDouble(spaceObject.getRotation(Direction.EAST));
			out.writeDouble(spaceObject.getRotation(Direction.UP));
			out.writeDouble(spaceObject.getRotation(Direction.NORTH));
			out.writeDouble(spaceObject.getDeltaRotation(Direction.EAST));
			out.writeDouble(spaceObject.getDeltaRotation(Direction.UP));
			out.writeDouble(spaceObject.getDeltaRotation(Direction.NORTH));
			break;
		case ALTITUDE_UPDATE:
			out.writeFloat(spaceObject.getOrbitalDistance());
			break;
		case DIM_PROPERTY_UPDATE:
			CompoundNBT nbt = new CompoundNBT();
			try {
				spaceObject.getProperties().writeToNBT(nbt);
				PacketBuffer packetBuffer = new PacketBuffer(out);
				packetBuffer.writeCompoundTag(nbt);
			} catch(NullPointerException e) {
				out.writeBoolean(true);
				Logger.getLogger("advancedRocketry").warning("Dimension " + stationNumber + " has thrown an exception trying to write NBT, deleting!");
				DimensionManager.getInstance().deleteDimension(stationNumber);
			}
		default:
		}
	}

	@Override
	public void readClient(PacketBuffer in) {
		stationNumber = in.readResourceLocation();
		type = Type.values()[in.readInt()];


		switch(type) {
		case DEST_ORBIT_UPDATE:
		case ORBIT_UPDATE:
			destOrbitingBody = in.readResourceLocation();
		break;
		case FUEL_UPDATE:
		    fuel = in.readInt();
			break;
		case ROTANGLE_UPDATE:
			rx = in.readDouble();
			ry = in.readDouble();
			rz = in.readDouble();
			drx = in.readDouble();
			dry = in.readDouble();
			drz = in.readDouble();
			break;
		case SIGNAL_WHITE_BURST:
			break;
		case ALTITUDE_UPDATE:
			orbitalDistance = in.readFloat();
			break;
		case DIM_PROPERTY_UPDATE:
			PacketBuffer packetBuffer = new PacketBuffer(in);
			nbt = packetBuffer.readCompoundTag();
			break;
		}	
	}

	@Override
	public void read(PacketBuffer in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(PlayerEntity thePlayer) {
		spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStation(stationNumber);
		
		switch(type) {
		case DEST_ORBIT_UPDATE:
			spaceObject.setDestOrbitingBody(destOrbitingBody );
			break;
		case ORBIT_UPDATE:
			spaceObject.setOrbitingBody(destOrbitingBody);
			break;
		case FUEL_UPDATE:
			if(spaceObject instanceof SpaceStationObject)
				((SpaceStationObject)spaceObject).setFuelAmount(fuel);
			break;
		case ROTANGLE_UPDATE:
			spaceObject.setRotation(rx, Direction.EAST);
			spaceObject.setRotation(ry, Direction.UP);
			spaceObject.setRotation(rz, Direction.NORTH);
			spaceObject.setDeltaRotation(drx, Direction.EAST);
			spaceObject.setDeltaRotation(dry, Direction.UP);
			spaceObject.setDeltaRotation(drz, Direction.NORTH);
			break;
		case SIGNAL_WHITE_BURST:
			PlanetEventHandler.runBurst(Minecraft.getInstance().world.getGameTime() + 20, 20);
			break;
		case ALTITUDE_UPDATE:
			spaceObject.setOrbitalDistance(orbitalDistance);
			break;
		case DIM_PROPERTY_UPDATE:
			if(nbt != null)
				spaceObject.getProperties().readFromNBT(nbt);
			break;
		}	
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {}
}
