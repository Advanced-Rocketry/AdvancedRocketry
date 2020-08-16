package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.libVulpes.entity.fx.FxErrorBlock;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.util.HashedBlockPosition;

public class PacketInvalidLocationNotify extends BasePacket {

	HashedBlockPosition toPos;

	public PacketInvalidLocationNotify(HashedBlockPosition toPos) {
		this.toPos = toPos;
	}

	public PacketInvalidLocationNotify() {
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

	FxErrorBlock fx3 = new FxErrorBlock((ClientWorld) thePlayer.world,  toPos.x, toPos.y, toPos.z );
	Minecraft.getInstance().particles.addEffect(fx3);
		
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {

	}

}
