package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.entity.fx.InverseTrailFluid;
import zmaster587.libVulpes.network.BasePacket;

public class PacketFluidParticle extends BasePacket {

	BlockPos toPos;
	BlockPos fromPos;
	int time;
	int color;

	public PacketFluidParticle(BlockPos fromPos, BlockPos toPos, int time, int color) {
		this.toPos = toPos;
		this.fromPos = fromPos;
		this.time = time;
		this.color = color;
	}

	public PacketFluidParticle() {
	}

	@Override
	public void write(PacketBuffer out) {
		out.writeInt(toPos.getX());
		out.writeInt(toPos.getY());
		out.writeInt(toPos.getZ());
		out.writeInt(fromPos.getX());
		out.writeInt(fromPos.getY());
		out.writeInt(fromPos.getZ());
		out.writeInt(time);
		out.writeInt(color);
	}

	@Override
	public void readClient(PacketBuffer in) {
		toPos = new BlockPos(in.readInt(), in.readInt(), in.readInt());
		fromPos = new BlockPos(in.readInt(), in.readInt(), in.readInt());
		time = in.readInt();
		color = in.readInt();
	}

	@Override
	public void read(PacketBuffer in) {

	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void executeClient(PlayerEntity thePlayer) {

		if(Minecraft.getInstance().gameSettings.particles == ParticleStatus.ALL) {
			// Hacky, but we need to pass the color along
			InverseTrailFluid fx3 = new InverseTrailFluid(thePlayer.world, fromPos.getX() + 0.5, fromPos.getY() + 0.5, fromPos.getZ() + 0.5,toPos.getX() + 0.5, toPos.getY() + 0.5, toPos.getZ() + 0.5, color, time );
			fx3.selectSpriteWithAge(InverseTrailFluid.Factory.spriteSet2);
			Minecraft.getInstance().particles.addEffect(fx3);
		}

		int numIterations = Minecraft.getInstance().gameSettings.particles == ParticleStatus.ALL ? 5 : (Minecraft.getInstance().gameSettings.particles == ParticleStatus.DECREASED ? 3 : 0);
		
		for(int i = 0; i < numIterations;i++) {
			InverseTrailFluid fx2 = new InverseTrailFluid(thePlayer.world, fromPos.getX() + 0.1*(0.5 - Math.random()), fromPos.getY() + 0.1*(0.5 - Math.random()), fromPos.getZ() + 0.1*(0.5 - Math.random()), toPos.getX() + 0.5, toPos.getY() + 0.5, toPos.getZ() + 0.5, color, time);;
			fx2.selectSpriteWithAge(InverseTrailFluid.Factory.spriteSet2);
			Minecraft.getInstance().particles.addEffect(fx2);
		}
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {

	}

}
