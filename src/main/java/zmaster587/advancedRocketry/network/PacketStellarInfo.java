package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;

public class PacketStellarInfo extends BasePacket {
	StellarBody star;
	ResourceLocation starId;
	CompoundNBT nbt;
	boolean removeStar;

	public PacketStellarInfo() {}

	public PacketStellarInfo(ResourceLocation starId,StellarBody star) {
		this.star = star;
		this.starId = starId;
	}

	@Override
	public void write(PacketBuffer out) {
		CompoundNBT nbt = new CompoundNBT();
		out.writeResourceLocation(starId);
		out.writeBoolean(star == null);


		if((star != null)) {
			star.writeToNBT(nbt);
			PacketBuffer packetBuffer = new PacketBuffer(out);
			packetBuffer.writeCompoundTag(nbt);
		}

	}

	@Override
	public void readClient(PacketBuffer in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);

		starId = in.readResourceLocation();
		removeStar = in.readBoolean();

		if(!removeStar) {
<<<<<<< HEAD
			nbt = packetBuffer.readCompoundTag();
=======
			try {
				nbt = packetBuffer.readCompoundTag();
			} catch (IOException e) {
				e.printStackTrace();
				nbt = null;
			}
>>>>>>> origin/feature/nuclearthermalrockets
		}
	}

	@Override
	public void read(PacketBuffer in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(PlayerEntity thePlayer) {
		StellarBody star;
		
		if(removeStar) {
			if(DimensionManager.getInstance().isDimensionCreated(starId)) {
				DimensionManager.getInstance().removeStar(starId);
			}
		}
		else if(nbt != null) {
			if((star = DimensionManager.getInstance().getStar(starId)) != null) {
				star.readFromNBT(nbt);
			} else {
				star = new StellarBody();
				star.readFromNBT(nbt);
				DimensionManager.getInstance().addStar(star);
			}
		}
	}

	@Override
	public void executeServer(ServerPlayerEntity player) {}

}
