package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.network.BasePacket;

public class PacketLaserGun extends BasePacket {

	Entity fromEntity;
	Vector3d toPos;
	int entityId;

	public PacketLaserGun(Entity fireFrom, Vector3d toPos) {
		this.fromEntity = fireFrom;
		this.toPos = toPos;
	}

	public PacketLaserGun() {
	}

	@Override
	public void write(PacketBuffer out) {
		out.writeInt(fromEntity.getEntityId());
		out.writeFloat((float)toPos.x);
		out.writeFloat((float)toPos.y);
		out.writeFloat((float)toPos.z);
	}

	@Override
	public void readClient(PacketBuffer in) {
		entityId = in.readInt();
		toPos = new Vector3d(in.readFloat(), in.readFloat(), in.readFloat());
	}

	@Override
	public void read(PacketBuffer in) {

	}

	@Override
	public void executeClient(PlayerEntity thePlayer) {
		Entity entity = thePlayer.world.getEntityByID(entityId);
		if(entity != null) {
			AdvancedRocketry.proxy.spawnLaser(entity, toPos);
		}
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {

	}

}
