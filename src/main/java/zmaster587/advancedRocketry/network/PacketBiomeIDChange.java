package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.util.HashedBlockPosition;
import zmaster587.libVulpes.util.ZUtils;

public class PacketBiomeIDChange extends BasePacket {

	Chunk chunk;
	ResourceLocation worldId;
	int xPos, zPos;
	int array[];
	HashedBlockPosition pos;
	
	public PacketBiomeIDChange() {
		array = new int[256];
		pos = new HashedBlockPosition(0, 0, 0);
	}
	
	public PacketBiomeIDChange(Chunk chunk, World world, HashedBlockPosition pos) {
		this.chunk = chunk;
		this.pos = pos;
		worldId = ZUtils.getDimensionIdentifier(world);
	}
	
	@Override
	public void write(PacketBuffer out) {
		out.writeResourceLocation(worldId);
		out.writeInt(chunk.getPos().x);
		out.writeInt(chunk.getPos().z);
		out.writeInt(pos.x);
		out.writeShort(pos.y);
		out.writeInt(pos.z);
		out.writeVarIntArray(chunk.getBiomes().getBiomeIds());
	}

	@Override
	public void readClient(PacketBuffer in) {
		worldId = in.readResourceLocation();
		xPos = in.readInt();
		zPos = in.readInt();
		
		pos.x = in.readInt();
		pos.y = in.readShort();
		pos.z = in.readInt();
		
		array = in.readVarIntArray();
	}

	@Override
	public void read(PacketBuffer in) {
		// One way transmission
	}

	@Override
	public void executeClient(PlayerEntity thePlayer) {
		if(ZUtils.getDimensionIdentifier(thePlayer.world) == worldId) {
			if( thePlayer.getEntityWorld().getChunkProvider().isChunkLoaded(new ChunkPos(xPos, zPos))) {
				chunk = thePlayer.world.getChunk(xPos, zPos);
				chunk.setBiomeArray(array);
				BlockPos pos2 = pos.getBlockPos();// new BlockPos(chunk.xPosition << 4, 48, chunk.zPosition << 4);
				//((ClientWorld)thePlayer.world).markChunkDirty(pos, unusedTileEntity); markBlockRangeForRenderUpdate(pos2.getX(), pos2.getY(), pos2.getZ(),pos3.getX(), pos3.getY(), pos3.getZ());
				
				if(Minecraft.getInstance().gameSettings.particles  == ParticleStatus.ALL)
					AdvancedRocketry.proxy.spawnParticle("smallLazer", thePlayer.world, pos.x, pos.y, pos.z, 0,0,0);
			}
		}
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {
		// How about no
		
	}

}
