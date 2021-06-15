package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.event.PlanetEventHandler;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;
import java.util.logging.Logger;

public class PacketStationUpdate extends BasePacket {
	ISpaceObject spaceObject;
	int stationNumber;
	Type type;
	
	int destOrbitingBody;
	int fuel;
	double rx,ry,rz,drx,dry,drz;
	float orbitalDistance;
	NBTTagCompound nbt;

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
			if(spaceObject instanceof SpaceStationObject)
				out.writeInt(((SpaceStationObject)spaceObject).getFuelAmount());
			break;
		case ROTANGLE_UPDATE:
			out.writeDouble(spaceObject.getRotation(EnumFacing.EAST));
			out.writeDouble(spaceObject.getRotation(EnumFacing.UP));
			out.writeDouble(spaceObject.getRotation(EnumFacing.NORTH));
			out.writeDouble(spaceObject.getDeltaRotation(EnumFacing.EAST));
			out.writeDouble(spaceObject.getDeltaRotation(EnumFacing.UP));
			out.writeDouble(spaceObject.getDeltaRotation(EnumFacing.NORTH));
			break;
		case ALTITUDE_UPDATE:
			out.writeFloat(spaceObject.getOrbitalDistance());
			break;
		case DIM_PROPERTY_UPDATE:
			NBTTagCompound nbt = new NBTTagCompound();
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
	public void readClient(ByteBuf in) {
		stationNumber = in.readInt();
		type = Type.values()[in.readInt()];


		switch(type) {
			case DEST_ORBIT_UPDATE:
			case ORBIT_UPDATE:
					destOrbitingBody = in.readInt();
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
				try {
					nbt = packetBuffer.readCompoundTag();

				} catch (IOException e) {
					e.printStackTrace();
					nbt = null;
					return;
				}
				break;
		}	
	}

	@Override
	public void read(ByteBuf in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
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
			spaceObject.setRotation(rx, EnumFacing.EAST);
			spaceObject.setRotation(ry, EnumFacing.UP);
			spaceObject.setRotation(rz, EnumFacing.NORTH);
			spaceObject.setDeltaRotation(drx, EnumFacing.EAST);
			spaceObject.setDeltaRotation(dry, EnumFacing.UP);
			spaceObject.setDeltaRotation(drz, EnumFacing.NORTH);
			break;
		case SIGNAL_WHITE_BURST:
			PlanetEventHandler.runBurst(Minecraft.getMinecraft().world.getTotalWorldTime() + 20, 20);
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
	public void executeServer(EntityPlayerMP player) {}
}
