package zmaster587.advancedRocketry.network;

import java.io.IOException;

import zmaster587.libVulpes.interfaces.INetworkEntity;
import zmaster587.libVulpes.util.INetworkMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

public class PacketItemModifcation extends BasePacket {

	NBTTagCompound nbt;

	byte packetId;
	int entityId;
	EntityPlayer entity;
	INetworkItem machine;

	public PacketItemModifcation() {
		nbt = new NBTTagCompound();
	};

	public PacketItemModifcation(INetworkItem machine, EntityPlayer entity, byte packetId) {
		this();
		this.machine = machine;
		this.entity = entity;
		this.packetId = packetId;
		this.entityId = entity.getEntityId();
	}


	public PacketItemModifcation(INetworkItem machine, EntityPlayer entity, byte packetId, NBTTagCompound nbt) {
		this(machine, entity, packetId);
		this.nbt = nbt;
	}

	@Override
	public void write(ByteBuf out) {
		PacketBuffer buffer = new PacketBuffer(out);

		write(buffer);
	}

	private void write(PacketBuffer out) {
		out.writeInt(entity.worldObj.provider.dimensionId);
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
		
		machine.writeDataToNetwork(out, packetId, entity.getHeldItem());
	}

	@Override
	public void read(ByteBuf in) {
		PacketBuffer buffer = new PacketBuffer(in);
		read(buffer, true);
	}

	public void read(PacketBuffer in, boolean server) {
		//DEBUG:
		World world;
		world = DimensionManager.getWorld(in.readInt());

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

		if(ent != null && ent instanceof EntityPlayer) {
			ItemStack itemStack = ((EntityPlayer)ent).getHeldItem();
			if(itemStack != null && itemStack.getItem() instanceof INetworkItem) {
				((INetworkItem)itemStack.getItem()).readDataFromNetwork(in, packetId, nbt, itemStack);
			}
		}
		else {
			//Error
		}
	}

	public void execute(EntityPlayer player, Side side) {
		
		if(player != null) {
			ItemStack itemStack = player.getHeldItem();
			if(itemStack != null && itemStack.getItem() instanceof INetworkItem) {
				((INetworkItem)itemStack.getItem()).useNetworkData(player, side, packetId, nbt, itemStack);
			}
		}
		else {
			//Error
		}
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
	@SideOnly(Side.CLIENT)
	public void readClient(ByteBuf in) {
		PacketBuffer buffer = new PacketBuffer(in);

		//DEBUG:
		World world;

		buffer.readInt();
		world = Minecraft.getMinecraft().theWorld;

		int entityId = buffer.readInt();
		packetId = buffer.readByte();

		Entity ent = world.getEntityByID(entityId);

		if(buffer.readBoolean()) {
			NBTTagCompound nbt = null;

			try {
				nbt = buffer.readNBTTagCompoundFromBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.nbt = nbt;
		}

		if(ent != null && ent instanceof EntityPlayer) {
			ItemStack itemStack = ((EntityPlayer)ent).getHeldItem();
			if(itemStack != null && itemStack.getItem() instanceof INetworkItem) {
				((INetworkItem)itemStack.getItem()).readDataFromNetwork(in, packetId, nbt, itemStack);
			}
		}
		else {
			//Error
		}
	}
}
