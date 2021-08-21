package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.util.SpacePosition;
import zmaster587.libVulpes.network.BasePacket;

public class PacketMoveRocketInSpace extends BasePacket {

	SpacePosition position;
	ResourceLocation dimId = null, starId=null;
	
	boolean hasWorld;
	boolean hasStar;
	
	
	public PacketMoveRocketInSpace() {
	}

	public PacketMoveRocketInSpace(SpacePosition position) {
		this.position = position;
	}

	@Override
	public void write(PacketBuffer out) {
		out.writeDouble(position.x);
		out.writeDouble(position.y);
		out.writeDouble(position.z);
		hasWorld = position.world == null;
		hasStar = position.star == null;
		
		out.writeBoolean(hasWorld);
		if(hasWorld)
			out.writeResourceLocation(position.world.getId());
		out.writeBoolean(hasStar);
		if(hasStar)
			out.writeResourceLocation(position.star.getId());
	}
	
	@Override
	public void readClient(PacketBuffer in) {
	}
	
	@Override
	public void read(PacketBuffer in) {
		position.x = in.readDouble();
		position.y = in.readDouble();
		position.z = in.readDouble();
		
		hasWorld = in.readBoolean();
		if(hasWorld)
			dimId = in.readResourceLocation();
		
		hasStar = in.readBoolean();
		if(hasStar)
			starId = in.readResourceLocation();
	}

	@Override
	public void executeClient(PlayerEntity thePlayer) {
	}
	
	@Override
	public void executeServer(ServerPlayerEntity player) 
	{
		
	}
}
