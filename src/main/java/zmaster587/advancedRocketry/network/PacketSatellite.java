package zmaster587.advancedRocketry.network;

import java.io.IOException;

import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.util.INetworkMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSatellite extends BasePacket {

	SatelliteBase machine;

	NBTTagCompound nbt;

	byte packetId;

	public PacketSatellite() {
		nbt = new NBTTagCompound();
	};

	public PacketSatellite(SatelliteBase machine) {
		this();
		this.machine = machine;
	}


	@Override
	public void write(ByteBuf outline) {
		PacketBuffer packetBuffer = new PacketBuffer(outline);
		NBTTagCompound nbt = new NBTTagCompound();
		machine.writeToNBT(nbt);
		
		packetBuffer.writeCompoundTag(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readClient(ByteBuf in) {
		
		PacketBuffer packetBuffer = new PacketBuffer(in);
		NBTTagCompound nbt;
		
		//TODO: error handling
		try {
			nbt = packetBuffer.readCompoundTag();
			SatelliteBase satellite = SatelliteRegistry.createFromNBT(nbt);
			
			zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(satellite.getDimensionId()).addSatallite(satellite);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void read(ByteBuf in) {
		//Should never happen
		
	}

	public void executeClient(EntityPlayer player) {
	}

	public void executeServer(EntityPlayerMP player) {
	}

	public void execute(EntityPlayer player, Side side) {
	}

}
