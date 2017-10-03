package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.network.BasePacket;

public class PacketLaserGun extends BasePacket {

	Entity fromEntity;
	Vec3d toPos;
	int entityId;

	public PacketLaserGun(Entity fireFrom, Vec3d toPos) {
		this.fromEntity = fireFrom;
		this.toPos = toPos;
	}

	public PacketLaserGun() {
	}

	@Override
	public void write(ByteBuf out) {
		out.writeInt(fromEntity.getEntityId());
		out.writeFloat((float)toPos.x);
		out.writeFloat((float)toPos.y);
		out.writeFloat((float)toPos.z);
	}

	@Override
	public void readClient(ByteBuf in) {
		entityId = in.readInt();
		toPos = new Vec3d(in.readFloat(), in.readFloat(), in.readFloat());
	}

	@Override
	public void read(ByteBuf in) {

	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		Entity entity = thePlayer.world.getEntityByID(entityId);
		if(entity != null) {
			AdvancedRocketry.proxy.spawnLaser(entity, toPos);
		}
	}

	@Override
	public void executeServer(EntityPlayerMP player) {

	}

}
