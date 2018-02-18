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

public class PacketAirParticle extends BasePacket {

	HashedBlockPosition toPos;

	public PacketAirParticle(HashedBlockPosition toPos) {
		this.toPos = toPos;
	}

	public PacketAirParticle() {
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

		if(Minecraft.getMinecraft().gameSettings.particleSetting < 1) {
			OxygenCloudFX fx3 = new OxygenCloudFX(thePlayer.worldObj,  toPos.x + 0.5, toPos.y + 0.5, toPos.z + 0.5, 0, 0, 0);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx3);
		}

		int numIterations = Minecraft.getMinecraft().gameSettings.particleSetting < 1 ? 5 : (Minecraft.getMinecraft().gameSettings.particleSetting < 2 ? 3 : 1);
		
		for(int i = 0; i < numIterations;i++) {
			OxygenTraceFX fx2 = new OxygenTraceFX(thePlayer.worldObj,  toPos.x + 0.5, toPos.y + 0.5, toPos.z + 0.5, 0.01*(0.5 - Math.random()), 0.01*(0.5 - Math.random()), 0.01*(0.5 - Math.random()));
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	@Override
	public void executeServer(EntityPlayerMP player) {

	}

}
