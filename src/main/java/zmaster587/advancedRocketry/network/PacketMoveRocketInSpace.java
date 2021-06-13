package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import zmaster587.advancedRocketry.util.SpacePosition;
import zmaster587.libVulpes.network.BasePacket;

public class PacketMoveRocketInSpace extends BasePacket {

	SpacePosition position;
	int dimId = 0, starId=0;
	
	boolean hasWorld;
	boolean hasStar;
	
	
	public PacketMoveRocketInSpace() {
	}

	public PacketMoveRocketInSpace(SpacePosition position) {
		this.position = position;
	}

	@Override
	public void write(ByteBuf out) {
		out.writeDouble(position.x);
		out.writeDouble(position.y);
		out.writeDouble(position.z);
		hasWorld = position.world == null;
		hasStar = position.star == null;
		
		out.writeBoolean(hasWorld);
		if(hasWorld)
			out.writeInt(position.world.getId());
		out.writeBoolean(hasStar);
		if(hasStar)
			out.writeInt(position.star.getId());
	}
	
	@Override
	public void readClient(ByteBuf in) {
	}
	
	@Override
	public void read(ByteBuf in) {
		position.x = in.readDouble();
		position.y = in.readDouble();
		position.z = in.readDouble();
		
		hasWorld = in.readBoolean();
		if(hasWorld)
			dimId = in.readInt();
		
		hasStar = in.readBoolean();
		if(hasStar)
			starId = in.readInt();
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
	}
	
	@Override
	public void executeServer(EntityPlayerMP player) 
	{
		
	}
}
