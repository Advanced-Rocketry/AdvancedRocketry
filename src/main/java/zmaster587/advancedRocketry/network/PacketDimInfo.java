package zmaster587.advancedRocketry.network;

import java.io.IOException;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.dimension.DimensionManager;
import zmaster587.advancedRocketry.api.dimension.DimensionProperties;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class PacketDimInfo extends BasePacket {

	DimensionProperties dimProperties;
	int dimNumber;

	public PacketDimInfo() {}

	public PacketDimInfo(int dimNumber,DimensionProperties dimProperties) {
		this.dimProperties = dimProperties;
		this.dimNumber = dimNumber;
	}

	@Override
	public void write(ByteBuf out) {
		NBTTagCompound nbt = new NBTTagCompound();
		out.writeInt(dimNumber);
		boolean flag = dimProperties == null;
		
		if(!flag) {
			
			//Try to send the nbt data of the dimension to the client, if it fails(probably due to non existent Biome ids) then remove the dimension
			try {
				dimProperties.writeToNBT(nbt);
				PacketBuffer packetBuffer = new PacketBuffer(out);
				out.writeBoolean(false);
				//TODO: error handling
				try {
					packetBuffer.writeNBTTagCompoundToBuffer(nbt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch(NullPointerException e) {
				out.writeBoolean(true);
				AdvancedRocketry.logger.warning("Dimension " + dimNumber + " has thrown an exception trying to write NBT, deleting!");
				DimensionManager.getInstance().deleteDimension(dimNumber);
			}

		}
		else
			out.writeBoolean(flag);

	}

	@Override
	public void readClient(ByteBuf in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);
		NBTTagCompound nbt;
		dimNumber = in.readInt();

		if(in.readBoolean()) {
			if(DimensionManager.getInstance().isDimensionCreated(dimNumber)) {
				DimensionManager.getInstance().deleteDimension(dimNumber);
			}
		}
		else {
			//TODO: error handling
			try {
				nbt = packetBuffer.readNBTTagCompoundFromBuffer();

			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			if(dimNumber == 0) {
				DimensionManager.overworldProperties.readFromNBT(nbt);
			}
			else if( DimensionManager.getInstance().isDimensionCreated(dimNumber) ) {
				DimensionManager.getInstance().getDimensionProperties(dimNumber).readFromNBT(nbt);
			} else {
				DimensionManager.getInstance().registerDimNoUpdate(DimensionProperties.createFromNBT(dimNumber, nbt));
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
