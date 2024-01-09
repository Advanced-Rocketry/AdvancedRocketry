package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.util.HashedBlockPosition;

public class PacketBiomeIDChange extends BasePacket {

	Chunk chunk;
	int worldId, xPos, zPos;
	byte[] array;
	HashedBlockPosition pos;
	
	public PacketBiomeIDChange() {
		array = new byte[256];
		pos = new HashedBlockPosition(0, 0, 0);
	}
	
	public PacketBiomeIDChange(Chunk chunk, World world, HashedBlockPosition pos) {
		this.chunk = chunk;
		this.pos = pos;
		worldId = world.provider.getDimension();
	}
	
	@Override
	public void write(ByteBuf out) {
		out.writeInt(worldId);
		out.writeInt(chunk.x);
		out.writeInt(chunk.z);
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
		if(thePlayer.world.provider.getDimension() == worldId) {
			chunk = thePlayer.world.getChunk(xPos, zPos);
			if(chunk.isLoaded()) {
				chunk.setBiomeArray(array);
				BlockPos pos2 = pos.getBlockPos();// new BlockPos(chunk.xPosition << 4, 48, chunk.zPosition << 4);
				thePlayer.world.markBlockRangeForRenderUpdate(pos2, pos2.add(1, 64, 1));
				
				if(Minecraft.getMinecraft().gameSettings.particleSetting < 2)
					AdvancedRocketry.proxy.spawnParticle("smallLazer", thePlayer.world, pos.x, pos.y, pos.z, 0,0,0);
			}
		}
	}

	@Override
	public void executeServer(EntityPlayerMP player) {
		// How about no
		
	}

}
