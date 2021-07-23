package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;

public class PacketSatellite extends BasePacket {

	SatelliteBase machine;

	CompoundNBT nbt;

	byte packetId;

	public PacketSatellite() {
		nbt = new CompoundNBT();
	};

	public PacketSatellite(SatelliteBase machine) {
		this();
		this.machine = machine;
	}


	@Override
	public void write(PacketBuffer outline) {
		PacketBuffer packetBuffer = new PacketBuffer(outline);
		CompoundNBT nbt = new CompoundNBT();
		machine.writeToNBT(nbt);
		
		packetBuffer.writeCompoundTag(nbt);
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void readClient(PacketBuffer in) {
		
		PacketBuffer packetBuffer = new PacketBuffer(in);
		CompoundNBT nbt;
		nbt = packetBuffer.readCompoundTag();
		SatelliteBase satellite = SatelliteRegistry.createFromNBT(nbt);

		DimensionManager.getInstance().getDimensionProperties(satellite.getDimensionId().get()).addSatellite(satellite);
	}

	@Override
	public void read(PacketBuffer in) {
		//Should never happen
		
	}

	@Override
	public void executeClient(PlayerEntity player) {
	}
	
	@Override
	public void executeServer(ServerPlayerEntity player) {
	}

	public void execute(PlayerEntity player, Dist side) {
	}

}
