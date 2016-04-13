package zmaster587.advancedRocketry.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketHandler {
	public static final EnumMap<Side, FMLEmbeddedChannel> channels = Maps.newEnumMap(Side.class);


	public static void init() {
		if (!channels.isEmpty()) // avoid duplicate inits..
			return;

		Codec codec = new Codec();

		codec.addDiscriminator(0, PacketMachine.class);
		codec.addDiscriminator(1, PacketEntity.class);
		codec.addDiscriminator(2, PacketDimInfo.class);
		codec.addDiscriminator(3, PacketSatellite.class);
		codec.addDiscriminator(4, PacketStellarInfo.class);
		codec.addDiscriminator(5, PacketItemModifcation.class);
		codec.addDiscriminator(6, PacketOxygenState.class);
		codec.addDiscriminator(7, PacketStationUpdate.class);
		codec.addDiscriminator(8, PacketSpaceStationInfo.class);
		
		channels.putAll(NetworkRegistry.INSTANCE.newChannel("advancedRocketry", codec, new HandlerServer()));

		// add handlers
		if (FMLCommonHandler.instance().getSide().isClient())
		{
			// for the client
			FMLEmbeddedChannel channel = channels.get(Side.CLIENT);
			String codecName = channel.findChannelHandlerNameForType(Codec.class);
			channel.pipeline().addAfter(codecName, "ClientHandler", new HandlerClient());
		}
	}


	public static final void sendToServer(BasePacket packet) {
		channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channels.get(Side.CLIENT).writeOutbound(packet);
	}

	
	@SideOnly(Side.SERVER)
	public static final void sendToPlayersTrackingEntity(BasePacket packet, Entity entity) {

		for( EntityPlayer player : ((WorldServer)entity.worldObj).getEntityTracker().getTrackingPlayers(entity)) {

			channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
			channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
			channels.get(Side.SERVER).writeOutbound(packet);
		}
	}

	public static final void sendToAll(BasePacket packet) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		channels.get(Side.SERVER).writeOutbound(packet);
	}
	
	public static final void sendToPlayer(BasePacket packet, EntityPlayer player) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channels.get(Side.SERVER).writeOutbound(packet);
	}

	public static final void sendToDispatcher(BasePacket packet, NetworkManager netman) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DISPATCHER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(NetworkDispatcher.get(netman));
		channels.get(Side.SERVER).writeOutbound(packet);
	}
	
	public static final void sendToNearby(BasePacket packet,int dimId, int x, int y, int z, double dist) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new NetworkRegistry.TargetPoint(dimId, x, y, z,dist));
		channels.get(Side.SERVER).writeOutbound(packet);
	}

	private static final class Codec extends FMLIndexedMessageToMessageCodec<BasePacket> {

		@Override
		public void encodeInto(ChannelHandlerContext ctx, BasePacket msg,
				ByteBuf data) throws Exception {
			msg.write(data);
		}

		@Override
		public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, BasePacket packet) {


			switch (FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT:
				packet.readClient(data);
				//packet.executeClient((EntityPlayer)Minecraft.getMinecraft().thePlayer);
				break;
			case SERVER:
				INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
				packet.read(data);
				//packet.executeServer(((NetHandlerPlayServer) netHandler).playerEntity);
				break;
			}

		}
	}

	@Sharable
	@SideOnly(Side.CLIENT)
	private static final class HandlerClient extends SimpleChannelInboundHandler<BasePacket>
	{
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, BasePacket packet) throws Exception
		{
			Minecraft mc = Minecraft.getMinecraft();
			packet.executeClient(mc.thePlayer); //actionClient(mc.theWorld, );
		}
	}
	@Sharable
	private static final class HandlerServer extends SimpleChannelInboundHandler<BasePacket>
	{
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, BasePacket packet) throws Exception
		{
			if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			{
				// nothing on the client thread
				return;
			}
			EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
			packet.executeServer(player); //(player.worldObj, player);
		}
	}

}
