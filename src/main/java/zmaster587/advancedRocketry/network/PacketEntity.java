package zmaster587.advancedRocketry.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import zmaster587.libVulpes.interfaces.INetworkEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.relauncher.Side;

public class PacketEntity extends BasePacket {

	INetworkEntity entity;

	NBTTagCompound nbt;

	byte packetId;

	public PacketEntity() {
		nbt = new NBTTagCompound();
	};

	public PacketEntity(INetworkEntity machine, byte packetId) {
		this();
		this.entity = machine;
		this.packetId = packetId;
	}


	public PacketEntity(INetworkEntity entity, byte packetId, NBTTagCompound nbt) {
		this(entity, packetId);
		this.nbt = nbt;
	}

	@Override
	public void write(ByteBuf out) {
		PacketBuffer buffer = new PacketBuffer(out);

		write(buffer);
	}

	private void write(PacketBuffer out) {
		out.writeInt(((Entity)entity).worldObj.provider.dimensionId);
		out.writeInt(entity.getEntityId());
		out.writeByte(packetId);

		out.writeBoolean(!nbt.hasNoTags());

		if(!nbt.hasNoTags()) {
			try {
				out.writeNBTTagCompoundToBuffer(nbt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		entity.writeDataToNetwork(out, packetId);
	}

	@Override
	public void read(ByteBuf in) {
		PacketBuffer buffer = new PacketBuffer(in);
		read(buffer, true);
	}

	public void read(PacketBuffer in, boolean server) {
		//DEBUG:
		World world;
		if(server)
			world = DimensionManager.getWorld(in.readInt());
		else {
			in.readInt();
			world = Minecraft.getMinecraft().theWorld;
		}
		
		int entityId = in.readInt();
		packetId = in.readByte();

		Entity ent = world.getEntityByID(entityId);

		if(in.readBoolean()) {
			NBTTagCompound nbt = null;
			
			try {
				nbt = in.readNBTTagCompoundFromBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.nbt = nbt;
		}
		
		if(ent != null && ent instanceof INetworkEntity) {
			entity = (INetworkEntity)ent;
			entity.readDataFromNetwork(in, packetId, nbt);
		}
		else {
			//Error
		}
	}

	public void execute(EntityPlayer player, Side side) {
		if(entity != null)
			entity.useNetworkData(player, side, packetId, nbt);
	}

	@Override
	public void executeServer(EntityPlayerMP player) {
		execute((EntityPlayer)player, Side.SERVER);
	}

	@Override
	public void executeClient(EntityPlayer player) {
		execute((EntityPlayer)player, Side.CLIENT);
	}

	@Override
	public void readClient(ByteBuf in) {
		PacketBuffer buffer = new PacketBuffer(in);
		read(buffer, false);
	}

}
