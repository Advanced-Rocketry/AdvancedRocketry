package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.util.BlockPosition;

public class PacketBiomeIDChange extends BasePacket {

	Chunk chunk;
	int worldId, xPos, zPos;
	byte array[];
	BlockPosition pos;
	
	public PacketBiomeIDChange() {
		array = new byte[256];
		pos = new BlockPosition(0, 0, 0);
	}
	
	public PacketBiomeIDChange(Chunk chunk, World world, BlockPosition pos) {
		this.chunk = chunk;
		this.pos = pos;
		worldId = world.provider.dimensionId;
	}
	
	@Override
	public void write(ByteBuf out) {
		out.writeInt(worldId);
		out.writeInt(chunk.xPosition);
		out.writeInt(chunk.zPosition);
		out.writeInt(pos.x);
		out.writeShort(pos.y);
		out.writeInt(pos.z);
		out.writeBytes(chunk.getBiomeArray());
	}

	@Override
	public void readClient(ByteBuf in) {
		worldId = in.readInt();
		xPos = in.readInt();
		zPos = in.readInt();
		
		pos.x = in.readInt();
		pos.y = in.readShort();
		pos.z = in.readInt();
		
		in.readBytes(array);
	}

	@Override
	public void read(ByteBuf in) {
		// One way transmission
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		if(thePlayer.worldObj.provider.dimensionId == worldId) {
			chunk = thePlayer.worldObj.getChunkFromChunkCoords(xPos, zPos);
			if(chunk.isChunkLoaded) {
				chunk.setBiomeArray(array);
				AdvancedRocketry.proxy.spawnParticle("smallLazer", thePlayer.worldObj, pos.x, pos.y, pos.z, 0,0,0);
			}
		}
	}

	@Override
	public void executeServer(EntityPlayerMP player) {
		// How about no
		
	}

}
