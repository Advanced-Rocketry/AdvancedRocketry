package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;
import java.util.logging.Logger;

public class PacketSpaceStationInfo extends BasePacket {
	SpaceStationObject spaceObject;
	ResourceLocation stationNumber;
	boolean isBeingDeleted;
	int direction;
	String clazzId;
	int fuelAmt;
	CompoundNBT nbt;
	boolean hasWarpCores;
	
	public PacketSpaceStationInfo() {}

	public PacketSpaceStationInfo(ResourceLocation stationNumber, ISpaceObject spaceObject) {
		this.spaceObject = (SpaceStationObject)spaceObject;
		this.stationNumber = stationNumber;
	}

	@Override
	public void write(PacketBuffer out) {
		CompoundNBT nbt = new CompoundNBT();
		out.writeResourceLocation(stationNumber);
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
	public void readClient(PacketBuffer in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);
		
		stationNumber = in.readResourceLocation();

		//Is dimension being deleted
		isBeingDeleted = in.readBoolean();
		if(!isBeingDeleted) {
			//TODO: error handling

			clazzId = packetBuffer.readString(127);
			nbt = packetBuffer.readCompoundTag();
			fuelAmt = packetBuffer.readInt();
			
			hasWarpCores = in.readBoolean();
			
			direction = in.readInt();
		}
	}

	@Override
	public void read(PacketBuffer in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(PlayerEntity thePlayer) {
		if(isBeingDeleted) {
			if(DimensionManager.getInstance().isDimensionCreated(stationNumber)) {
				DimensionManager.getInstance().deleteDimension(stationNumber);
			}
		}
		else {
			ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStation(stationNumber);
			this.spaceObject = (SpaceStationObject)spaceObject;
			
			//Station needs to be created
<<<<<<< HEAD
			if( iObject == null ) {
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getNewSpaceObjectFromIdentifier(clazzId);
				object.readFromNbt(nbt);
				object.setProperties(DimensionProperties.createFromNBT(stationNumber, nbt));
				((SpaceStationObject)object).setForwardDirection(Direction.values()[direction]);
=======
			if( spaceObject == null ) {
				ISpaceObject newSpaceObject = SpaceObjectManager.getSpaceManager().getNewSpaceObjectFromIdentifier(clazzId);
				newSpaceObject.readFromNbt(nbt);
				newSpaceObject.setProperties(DimensionProperties.createFromNBT(stationNumber, nbt));
				((SpaceStationObject)newSpaceObject).setForwardDirection(EnumFacing.values()[direction]);
>>>>>>> origin/feature/nuclearthermalrockets
				
				SpaceObjectManager.getSpaceManager().registerSpaceObjectClient(newSpaceObject, newSpaceObject.getOrbitingPlanetId(), stationNumber);
				((SpaceStationObject)newSpaceObject).setFuelAmount(fuelAmt);
				((SpaceStationObject)newSpaceObject).hasWarpCores = hasWarpCores;
			}
			else {
<<<<<<< HEAD
				iObject.readFromNbt(nbt);
				//iObject.setProperties(DimensionProperties.createFromNBT(stationNumber, nbt));
				((SpaceStationObject)iObject).setForwardDirection(Direction.values()[direction]);
				((SpaceStationObject)iObject).setFuelAmount(fuelAmt);
				((SpaceStationObject)iObject).hasWarpCores = hasWarpCores;
=======
				spaceObject.readFromNbt(nbt);
				//spaceObject.setProperties(DimensionProperties.createFromNBT(stationNumber, nbt));
				((SpaceStationObject)spaceObject).setForwardDirection(EnumFacing.values()[direction]);
				((SpaceStationObject)spaceObject).setFuelAmount(fuelAmt);
				((SpaceStationObject)spaceObject).hasWarpCores = hasWarpCores;
>>>>>>> origin/feature/nuclearthermalrockets
			}
		}
			
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {}

}
