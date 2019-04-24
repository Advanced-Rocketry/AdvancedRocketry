package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.entity.fx.InverseTrailFluid;
import zmaster587.advancedRocketry.entity.fx.OxygenCloudFX;
import zmaster587.advancedRocketry.entity.fx.OxygenTraceFX;
import zmaster587.libVulpes.network.BasePacket;
import zmaster587.libVulpes.util.HashedBlockPosition;

public class PacketFluidParticle extends BasePacket {

	BlockPos toPos;
	BlockPos fromPos;
	int time;
	int color;

	public PacketFluidParticle(BlockPos fromPos, BlockPos toPos, int time, int color) {
		this.toPos = toPos;
		this.fromPos = fromPos;
		this.time = time;
		this.color = color;
	}

	public PacketFluidParticle() {
	}

	@Override
	public void write(ByteBuf out) {
		out.writeInt(toPos.getX());
		out.writeInt(toPos.getY());
		out.writeInt(toPos.getZ());
		out.writeInt(fromPos.getX());
		out.writeInt(fromPos.getY());
		out.writeInt(fromPos.getZ());
		out.writeInt(time);
		out.writeInt(color);
	}

	@Override
	public void readClient(ByteBuf in) {
		toPos = new BlockPos(in.readInt(), in.readInt(), in.readInt());
		fromPos = new BlockPos(in.readInt(), in.readInt(), in.readInt());
		time = in.readInt();
		color = in.readInt();
	}

	@Override
	public void read(ByteBuf in) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void executeClient(EntityPlayer thePlayer) {

		if(Minecraft.getMinecraft().gameSettings.particleSetting < 1) {
			InverseTrailFluid fx3 = new InverseTrailFluid(thePlayer.world, fromPos.getX() + 0.5, fromPos.getY() + 0.5, fromPos.getZ() + 0.5,toPos.getX() + 0.5, toPos.getY() + 0.5, toPos.getZ() + 0.5, color, time );
			Minecraft.getMinecraft().effectRenderer.addEffect(fx3);
		}

		int numIterations = Minecraft.getMinecraft().gameSettings.particleSetting < 1 ? 5 : (Minecraft.getMinecraft().gameSettings.particleSetting < 2 ? 3 : 0);
		
		for(int i = 0; i < numIterations;i++) {
			InverseTrailFluid fx2 = new InverseTrailFluid(thePlayer.world, fromPos.getX() + 0.1*(0.5 - Math.random()), fromPos.getY() + 0.1*(0.5 - Math.random()), fromPos.getZ() + 0.1*(0.5 - Math.random()), toPos.getX() + 0.5, toPos.getY() + 0.5, toPos.getZ() + 0.5, color, time);;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	@Override
	public void executeServer(EntityPlayerMP player) {

	}

}
