package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.libVulpes.network.BasePacket;

/**
 * Literally the entire purpose of this class is to tell the player he/she is in an airless environment
 */
public class PacketOxygenState extends BasePacket {

	@Override
	public void write(PacketBuffer out) {
		
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void readClient(PacketBuffer in) {
		AtmosphereHandler.lastSuffocationTime = Minecraft.getInstance().world.getGameTime();
	}

	@Override
	public void read(PacketBuffer in) {
		
	}

	@Override
	public void executeClient(PlayerEntity thePlayer) {
		
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {
		
	}

}
