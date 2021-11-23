package zmaster587.advancedRocketry.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryParticleTypes;
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
			AdvancedRocketry.proxy.spawnParticle(AdvancedRocketryParticleTypes.oxygenCloudFx, thePlayer.world,  toPos.x + 0.5, toPos.y + 0.5, toPos.z + 0.5, 0, 0, 0);
		}

		int numIterations = Minecraft.getInstance().gameSettings.particles == ParticleStatus.ALL ? 5 : (Minecraft.getInstance().gameSettings.particles == ParticleStatus.DECREASED ? 3 : 1);
		
		for(int i = 0; i < numIterations;i++) {
			AdvancedRocketry.proxy.spawnParticle(AdvancedRocketryParticleTypes.oxygenTraceFx, thePlayer.world,  toPos.x + 0.5, toPos.y + 0.5, toPos.z + 0.5, 0.01*(0.5 - Math.random()), 0.01*(0.5 - Math.random()), 0.01*(0.5 - Math.random()));
		}
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {

	}

}
