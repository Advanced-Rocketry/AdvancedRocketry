package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.util.Vector3F;

public class PacketLaserGun extends BasePacket {

	Entity fromEntity;
	Vec3 toPos;
	int entityId;

	public PacketLaserGun(Entity fireFrom, Vec3 toPos) {
		this.fromEntity = fireFrom;
		this.toPos = toPos;
	}

	public PacketLaserGun() {
	}

	@Override
	public void write(ByteBuf out) {
		out.writeInt(fromEntity.getEntityId());
		out.writeFloat((float)(double)toPos.xCoord);
		out.writeFloat((float)(double)toPos.yCoord);
		out.writeFloat((float)(double)toPos.zCoord);
	}

	@Override
	public void readClient(ByteBuf in) {
		entityId = in.readInt();
		toPos = Vec3.createVectorHelper((double)in.readFloat(), (double)in.readFloat(), (double)in.readFloat());
	}

	@Override
	public void read(ByteBuf in) {

	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		Entity entity = thePlayer.worldObj.getEntityByID(entityId);
		if(entity != null) {
			AdvancedRocketry.proxy.spawnLaser(entity, toPos);
		}
	}

	@Override
	public void executeServer(EntityPlayerMP player) {

	}

}
