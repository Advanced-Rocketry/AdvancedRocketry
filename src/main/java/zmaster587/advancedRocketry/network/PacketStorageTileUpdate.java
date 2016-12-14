package zmaster587.advancedRocketry.network;

import java.io.IOException;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.network.BasePacket;

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
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
	}

	@Override
	public void write(ByteBuf out) {
		NBTTagCompound nbt = (NBTTagCompound)ReflectionHelper.getPrivateValue(S35PacketUpdateTileEntity.class, (S35PacketUpdateTileEntity)tile.getDescriptionPacket(), "field_148860_e");


		out.writeInt(((Entity)entity).worldObj.provider.dimensionId);
		out.writeInt(((Entity)entity).getEntityId());
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);

		PacketBuffer packetBuffer = new PacketBuffer(out);
		try {
			packetBuffer.writeNBTTagCompoundToBuffer(nbt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readClient(ByteBuf in) {
		//DEBUG:
		World world;
		//world = DimensionManager.getWorld(in.readInt());
		in.readInt();
		world = Minecraft.getMinecraft().theWorld;


		int entityId = in.readInt();
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		Entity ent = world.getEntityByID(entityId);

		NBTTagCompound nbt = null;

		try {
			nbt = new PacketBuffer(in).readNBTTagCompoundFromBuffer();
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
			TileEntity tile = this.chunk.getTileEntity(x, y, z);
			tile.onDataPacket(Minecraft.getMinecraft().getNetHandler().getNetworkManager(), new S35PacketUpdateTileEntity(x, y, z, 0, nbt));
		}
	}

	@Override
	public void executeServer(EntityPlayerMP player) {

	}

}
