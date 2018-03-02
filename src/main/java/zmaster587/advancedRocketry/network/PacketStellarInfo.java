package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.libVulpes.network.BasePacket;

import java.io.IOException;

public class PacketStellarInfo extends BasePacket {
	StellarBody star;
	int starId;

	public PacketStellarInfo() {}

	public PacketStellarInfo(int starId,StellarBody star) {
		this.star = star;
		this.starId = starId;
	}

	@Override
	public void write(ByteBuf out) {
		NBTTagCompound nbt = new NBTTagCompound();
		out.writeInt(starId);
		out.writeBoolean(star == null);


		if((star != null)) {
			star.writeToNBT(nbt);
			PacketBuffer packetBuffer = new PacketBuffer(out);
			packetBuffer.writeCompoundTag(nbt);
		}

	}

	@Override
	public void readClient(ByteBuf in) {
		PacketBuffer packetBuffer = new PacketBuffer(in);
		NBTTagCompound nbt;
		starId = in.readInt();

		if(in.readBoolean()) {
			if(DimensionManager.getInstance().isDimensionCreated(starId)) {
				DimensionManager.getInstance().removeStar(starId);
			}
		}
		else {
			//TODO: error handling
			try {
				nbt = packetBuffer.readCompoundTag();

			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			StellarBody star;

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
	public void read(ByteBuf in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {}

	@Override
	public void executeServer(EntityPlayerMP player) {}

}
