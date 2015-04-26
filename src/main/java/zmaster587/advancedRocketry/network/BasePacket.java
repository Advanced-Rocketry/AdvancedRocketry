package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;

import java.net.ProtocolException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BasePacket {
	public static final String CHANNEL = "advancedRocketry";
	private static final BiMap<Integer,Class<? extends BasePacket>> idMap;

	static {
		ImmutableBiMap.Builder<Integer, Class<? extends BasePacket>> builder = ImmutableBiMap.builder();
		//builder.put(Integer.valueOf(0), PacketMachine.class);
		idMap = builder.build();
	}

	public static BasePacket constructPacket(int packetId) throws ProtocolException, InstantiationException, IllegalAccessException {
		Class<? extends BasePacket> clazz = idMap.get(Integer.valueOf(packetId));
		if(clazz == null){
			throw new ProtocolException("Protocol Exception!  Unknown Packet Id!");
		} else {
			return clazz.newInstance();
		}
	}
	

    public static class ProtocolException extends Exception {

            public ProtocolException() {
            }

            public ProtocolException(String message, Throwable cause) {
                    super(message, cause);
            }

            public ProtocolException(String message) {
                    super(message);
            }

            public ProtocolException(Throwable cause) {
                    super(cause);
            }
    }

	public final int getPacketId() {
		if(idMap.inverse().containsKey(getClass())) {
			return idMap.inverse().get(getClass()).intValue();
		} else {
			throw new RuntimeException("Packet " + getClass().getSimpleName() + " is a missing mapping!");
		}
	}
	
	/*public final Packet makePacket() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeByte(getPacketId());
		write(out);
		
		//byte[] output = getPacketId().
		
		C17PacketCustomPayload packet = new C17PacketCustomPayload(MechanicalUtilities.MOD_ID, (ByteBuf)out);
		
		return packet;
	}*/

	public abstract void write(ByteBuf out);

	public abstract void readClient(ByteBuf in);
	
	public abstract void read(ByteBuf in);

	@SideOnly(Side.CLIENT)
	public abstract void executeClient(EntityPlayer thePlayer);
	
	public abstract void executeServer(EntityPlayerMP player);
}
