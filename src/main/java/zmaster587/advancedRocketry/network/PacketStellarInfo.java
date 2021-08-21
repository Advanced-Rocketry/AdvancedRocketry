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
	NBTTagCompound nbt;
	boolean removeStar;

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

		starId = in.readInt();
		removeStar = in.readBoolean();

		if(!removeStar) {
			try {
				nbt = packetBuffer.readCompoundTag();
			} catch (IOException e) {
				e.printStackTrace();
				nbt = null;
			}
		}
	}

	@Override
	public void read(ByteBuf in) {
		//Should never be read on the server!
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
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
	public void executeServer(EntityPlayerMP player) {}

}
