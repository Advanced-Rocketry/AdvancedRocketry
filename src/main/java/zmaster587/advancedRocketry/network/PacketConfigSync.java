package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;

public class PacketConfigSync extends BasePacket {
	int spaceDimId;
	
	public PacketConfigSync() {
		
	}
	@Override
	public void write(ByteBuf out) {
		out.writeInt(Configuration.spaceDimId);
	}

	@Override
	public void readClient(ByteBuf in) {
		spaceDimId = in.readInt();
	}

	@Override
	public void read(ByteBuf in) {
		//nice try
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		Configuration.spaceDimId = spaceDimId;
	}

	@Override
	public void executeServer(EntityPlayerMP player) {}

}
