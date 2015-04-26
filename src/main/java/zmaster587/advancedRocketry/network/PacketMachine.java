package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import zmaster587.libVulpes.util.INetworkMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketMachine extends BasePacket {

	INetworkMachine machine;
	
	NBTTagCompound nbt;
	
	byte packetId;
	
	public PacketMachine() {
		nbt = new NBTTagCompound();
	};
	
	public PacketMachine(INetworkMachine machine, byte packetId) {
		this();
		this.machine = machine;
		this.packetId = packetId;
	}
	
	
	@Override
	public void write(ByteBuf outline) {
		outline.writeInt(((TileEntity)machine).getWorldObj().provider.dimensionId);
		outline.writeInt(((TileEntity)machine).xCoord);
		outline.writeInt(((TileEntity)machine).yCoord);
		outline.writeInt(((TileEntity)machine).zCoord);
		
		outline.writeByte(packetId);
		
		machine.writeDataToNetwork(outline, packetId);
		//ByteBuf buffer
		//outline.writeBytes(out.toByteArray());
		//outline.
		//outline. =  (ByteBuf)out.toByteArray();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void readClient(ByteBuf in) {
		//DEBUG:
		in.readInt();
		World world = Minecraft.getMinecraft().theWorld;
		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		packetId = in.readByte();
		
		TileEntity ent = world.getTileEntity(x, y, z);
		
		if(ent != null && ent instanceof INetworkMachine) {
			machine = (INetworkMachine)ent;
			machine.readDataFromNetwork(in, packetId, nbt);
		}
		else {
			//Error
		}
	}
	
	@Override
	public void read(ByteBuf in) {
		//DEBUG:
		int temp = in.readInt();
		World world = DimensionManager.getWorld(temp);
		
		int x = in.readInt();
		int y = in.readInt();
		int z = in.readInt();
		packetId = in.readByte();
		
		TileEntity ent = world.getTileEntity(x, y, z);
		
		if(ent != null && ent instanceof INetworkMachine) {
			machine = (INetworkMachine)ent;
			machine.readDataFromNetwork(in, packetId, nbt);
		}
		else {
			//Error
		}
	}

	public void executeClient(EntityPlayer player) {
		machine.useNetworkData(player, Side.CLIENT, packetId, nbt);
	}
	
	public void executeServer(EntityPlayerMP player) {
		machine.useNetworkData(player, Side.SERVER, packetId, nbt);
	}
	
	public void execute(EntityPlayer player, Side side) {
		machine.useNetworkData(player, side, packetId, nbt);
	}

}
