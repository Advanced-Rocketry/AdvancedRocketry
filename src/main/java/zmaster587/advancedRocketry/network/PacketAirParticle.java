package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.entity.fx.OxygenCloudFX;
import zmaster587.advancedRocketry.entity.fx.OxygenTraceFX;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.util.HashedBlockPosition;

public class PacketAirParticle extends BasePacket {

	HashedBlockPosition toPos;

	public PacketAirParticle(HashedBlockPosition toPos) {
		this.toPos = toPos;
	}

	public PacketAirParticle() {
	}

	@Override
	public void write(PacketBuffer out) {
		out.writeInt(toPos.x);
		out.writeInt(toPos.y);
		out.writeInt(toPos.z);
	}

	@Override
	public void readClient(PacketBuffer in) {
		toPos = new HashedBlockPosition(in.readInt(), in.readInt(), in.readInt());
	}

	@Override
	public void read(PacketBuffer in) {

	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void executeClient(PlayerEntity thePlayer) {

		if(Minecraft.getInstance().gameSettings.particles == ParticleStatus.ALL) {
			OxygenCloudFX fx3 = new OxygenCloudFX(thePlayer.world,  toPos.x + 0.5, toPos.y + 0.5, toPos.z + 0.5, 0, 0, 0);
			Minecraft.getInstance().particles.addEffect(fx3);
		}

		int numIterations = Minecraft.getInstance().gameSettings.particles == ParticleStatus.ALL ? 5 : (Minecraft.getInstance().gameSettings.particles == ParticleStatus.DECREASED ? 3 : 1);
		
		for(int i = 0; i < numIterations;i++) {
			OxygenTraceFX fx2 = new OxygenTraceFX(thePlayer.world,  toPos.x + 0.5, toPos.y + 0.5, toPos.z + 0.5, 0.01*(0.5 - Math.random()), 0.01*(0.5 - Math.random()), 0.01*(0.5 - Math.random()));
			Minecraft.getInstance().particles.addEffect(fx2);
		}
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {

	}

}
