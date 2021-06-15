package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;

public class PacketAtmSync extends BasePacket {

	String type;
	int pressure;
	
	public PacketAtmSync(String type, int pressure) {
		this.type = type;
		this.pressure = pressure;
	}
	
	public PacketAtmSync() {
		
	}
	
	@Override
	public void write(ByteBuf out) {
		NBTTagCompound nbt = new NBTTagCompound();
		
		nbt.setString("type", type);
		nbt.setShort("pressure", (short)pressure);
		PacketBuffer packetBuffer = new PacketBuffer(out);
		
		packetBuffer.writeCompoundTag(nbt);
	}

	@Override
	public void readClient(ByteBuf in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);
		
		try {
			NBTTagCompound nbt = packetBuffer.readCompoundTag();
			type = nbt.getString("type");
			pressure = nbt.getShort("pressure");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void read(ByteBuf in) {
		//Do nothing on server, we don't want hackers now do we ;)
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		AtmosphereHandler.currentAtm = AtmosphereRegister.getInstance().getAtmosphere(type);
		AtmosphereHandler.currentPressure = pressure;
	}

	@Override
	public void executeServer(EntityPlayerMP player) {
		
	}

}
