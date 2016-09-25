package zmaster587.advancedRocketry.network;

import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.libVulpes.network.BasePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		AtmosphereHandler.lastSuffocationTime = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
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
