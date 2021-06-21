package zmaster587.advancedRocketry.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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
	public void write(PacketBuffer out) {
		CompoundNBT nbt = new CompoundNBT();
		
		nbt.putString("type", type);
		nbt.putShort("pressure", (short)pressure);
		PacketBuffer packetBuffer = new PacketBuffer(out);
		
		packetBuffer.writeCompoundTag(nbt);
	}

	@Override
<<<<<<< HEAD
	public void readClient(PacketBuffer in) {
		CompoundNBT nbt = new CompoundNBT();
		PacketBuffer packetBuffer = new PacketBuffer(in);
		
		nbt = packetBuffer.readCompoundTag();
		type = nbt.getString("type");
		pressure = nbt.getShort("pressure");
=======
	public void readClient(ByteBuf in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);
		
		try {
			NBTTagCompound nbt = packetBuffer.readCompoundTag();
			type = nbt.getString("type");
			pressure = nbt.getShort("pressure");
		} catch (IOException e) {
			e.printStackTrace();
		}
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
	public void read(PacketBuffer in) {
		//Do nothing on server, we don't want hackers now do we ;)
	}

	@Override
	public void executeClient(PlayerEntity thePlayer) {
		AtmosphereHandler.currentAtm = AtmosphereRegister.getInstance().getAtmosphere(type);
		AtmosphereHandler.currentPressure = pressure;
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {
		
	}

}
