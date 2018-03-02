package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
	NBTTagCompound nbt;

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
	public void write(ByteBuf out) {
		NBTTagCompound nbt = (NBTTagCompound)ReflectionHelper.getPrivateValue(SPacketUpdateTileEntity.class, (SPacketUpdateTileEntity)tile.getUpdatePacket(), "field_148860_e");


		out.writeInt(((Entity)entity).world.provider.getDimension());
		out.writeInt(((Entity)entity).getEntityId());
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);

		PacketBuffer packetBuffer = new PacketBuffer(out);
		packetBuffer.writeCompoundTag(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readClient(ByteBuf in) {
		//DEBUG:
		World world;
		//world = DimensionManager.getWorld(in.readInt());
		in.readInt();
		world = Minecraft.getMinecraft().world;


		int entityId = in.readInt();
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		Entity ent = world.getEntityByID(entityId);

		NBTTagCompound nbt = null;

		try {
			nbt = new PacketBuffer(in).readCompoundTag();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.nbt = nbt;


		if(ent != null && ent instanceof INetworkEntity) {
			entity = (EntityRocket)ent;
			this.chunk = entity.storage;
		}
		else {
			//Error
		}
	}

	@Override
	public void read(ByteBuf in) {

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void executeClient(EntityPlayer thePlayer) {
		//Make sure the chunk is initialized before using it 
		//sanity check
		if(this.chunk != null) {
			TileEntity tile = this.chunk.getTileEntity(new BlockPos(x, y, z));
			tile.onDataPacket(Minecraft.getMinecraft().getConnection().getNetworkManager(), new SPacketUpdateTileEntity(new BlockPos(x, y, z), 0, nbt));
		}
	}

	@Override
	public void executeServer(EntityPlayerMP player) {

	}

}
