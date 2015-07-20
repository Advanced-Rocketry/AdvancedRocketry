package zmaster587.advancedRocketry.network;

import java.io.IOException;

import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.advancedRocketry.world.DimensionProperties;
import zmaster587.advancedRocketry.world.ProviderPlanet;
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
		
		dimProperties.writeToNBT(nbt);
		
		PacketBuffer packetBuffer = new PacketBuffer(out);
		//TODO: error handling
		try {
			packetBuffer.writeNBTTagCompoundToBuffer(nbt);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		packetBuffer.writeInt(dimNumber);
	}

	@Override
	public void readClient(ByteBuf in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);
		NBTTagCompound nbt;
		
		//TODO: error handling
		try {
			nbt = packetBuffer.readNBTTagCompoundFromBuffer();
			dimNumber = packetBuffer.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		
		if(DimensionManager.getInstance().isDimensionCreated(dimNumber)) {
			DimensionManager.getInstance().getDimensionProperties(dimNumber).readFromNBT(nbt);
		} else {
			
			DimensionManager.getInstance().registerDim(dimNumber, DimensionProperties.createFromNBT(nbt));
			net.minecraftforge.common.DimensionManager.registerProviderType(dimNumber,ProviderPlanet.class, false);
			net.minecraftforge.common.DimensionManager.registerDimension(dimNumber, dimNumber);
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
