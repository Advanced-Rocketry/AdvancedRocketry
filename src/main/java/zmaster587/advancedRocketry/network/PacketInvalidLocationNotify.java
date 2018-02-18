package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.entity.fx.FxLaserHeat;
import zmaster587.advancedRocketry.entity.fx.OxygenCloudFX;
import zmaster587.advancedRocketry.entity.fx.OxygenTraceFX;
import zmaster587.libVulpes.entity.fx.FxErrorBlock;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.util.HashedBlockPosition;

public class PacketInvalidLocationNotify extends BasePacket {

	HashedBlockPosition toPos;

	public PacketInvalidLocationNotify(HashedBlockPosition toPos) {
		this.toPos = toPos;
	}

	public PacketInvalidLocationNotify() {
	}

	@Override
	public void write(ByteBuf out) {
		out.writeInt(toPos.x);
		out.writeInt(toPos.y);
		out.writeInt(toPos.z);
	}

	@Override
	public void readClient(ByteBuf in) {
		toPos = new HashedBlockPosition(in.readInt(), in.readInt(), in.readInt());
	}

	@Override
	public void read(ByteBuf in) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer thePlayer) {

	FxErrorBlock fx3 = new FxErrorBlock(thePlayer.worldObj,  toPos.x, toPos.y, toPos.z );
	Minecraft.getMinecraft().effectRenderer.addEffect(fx3);
		
	}

	@Override
	public void executeServer(EntityPlayerMP player) {

	}

}
