package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;

public class PacketStorageTileUpdate extends BasePacket {


	EntityRocket entity;
	StorageChunk chunk;
	int x,y,z;
	TileEntity tile;
	CompoundNBT nbt;

	public PacketStorageTileUpdate() {

	}

	public PacketStorageTileUpdate(Entity entity, StorageChunk chunk, TileEntity tile) {
		this.entity = (EntityRocket)entity;
		this.chunk = chunk;
		this.tile = tile;
		x = tile.getPos().getX();
		y = tile.getPos().getY();
		z = tile.getPos().getZ();
	}

	@Override
	public void write(PacketBuffer out) {
		CompoundNBT nbt = tile.getUpdatePacket().getNbtCompound();
		
		//this.writeWorld(out, ((Entity)entity).world);
		out.writeInt(((Entity)entity).getEntityId());
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);

		PacketBuffer packetBuffer = new PacketBuffer(out);
		packetBuffer.writeCompoundTag(nbt);
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void readClient(PacketBuffer in) {
		//DEBUG:
		World world;
		//world = DimensionManager.getWorld(in.readInt());
		//this.readWorld(in);
		world = Minecraft.getInstance().world;


		int entityId = in.readInt();
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		Entity ent = world.getEntityByID(entityId);

		CompoundNBT nbt = null;

		nbt = new PacketBuffer(in).readCompoundTag();

		this.nbt = nbt;


		if(ent instanceof INetworkEntity) {
			entity = (EntityRocket)ent;
			this.chunk = entity.storage;
		}
		else {
			//Error
		}
	}

	@Override
	public void read(PacketBuffer in) {

	}

	@OnlyIn(value=Dist.CLIENT)
	@Override
	public void executeClient(PlayerEntity thePlayer) {
		//Make sure the chunk is initialized before using it 
		//sanity check
		if(this.chunk != null) {
			TileEntity tile = this.chunk.getTileEntity(new BlockPos(x, y, z));
			tile.onDataPacket(Minecraft.getInstance().getConnection().getNetworkManager(), new SUpdateTileEntityPacket(new BlockPos(x, y, z), 0, nbt));
		}
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {

	}

}
