package zmaster587.advancedRocketry.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.event.RocketEventHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Literally the entire purpose of this class is to tell the player he/she is in an airless environment
 */
public class PacketOxygenState extends BasePacket {

	@Override
	public void write(ByteBuf out) {
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readClient(ByteBuf in) {
		RocketEventHandler.lastSuffocationTime = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
	}

	@Override
	public void read(ByteBuf in) {
		
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		
	}

	@Override
	public void executeServer(EntityPlayerMP player) {
		
	}

}
