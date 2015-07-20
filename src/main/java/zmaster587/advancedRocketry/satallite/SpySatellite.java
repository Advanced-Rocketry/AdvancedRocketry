package zmaster587.advancedRocketry.satallite;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import zmaster587.advancedRocketry.api.ISatallite;

public class SpySatellite implements ISatallite {
//TODO: write position to player file and teleport player then subscribe to onlogin
	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Spy in the sky";
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, int x,
			int y, int z) {

/*
		//cam.setPosition(player.posX, player.posY + 30, player.posZ);
		if(!world.isRemote) {			
			player.setPosition(-300, 80, 250);
			//MinecraftServer.getServer().getConfigurationManager().
			EntityCameraMP entityClientCameramp = new EntityCameraMP(MinecraftServer.getServer(), world, "Camera", new ItemInWorldManager(world));
			WorldServer worldserver = ((EntityPlayerMP)player).getServerForPlayer();

			entityClientCameramp.playerNetServerHandler = ((EntityPlayerMP)player).playerNetServerHandler;
			entityClientCameramp.dimension = world.provider.dimensionId;
			entityClientCameramp.entityId = player.entityId;
			//      worldserver.theChunkProviderServer.loadChunk((int)entityplayermp1.posX >> 4, (int)entityplayermp1.posZ >> 4);
			((EntityPlayerMP)player).playerNetServerHandler.playerEntity = entityClientCameramp;

			worldserver.getEntityTracker().removePlayerFromTrackers((EntityPlayerMP) player);
			worldserver.getEntityTracker().removeEntityFromAllTrackingPlayers((EntityPlayerMP) player);
			//((EntityPlayerMP)player).getServerForPlayer().getPlayerManager().removePlayer((EntityPlayerMP) player);
			//MinecraftServer.getServer().worldServerForDimension(player.dimension).removePlayerEntityDangerously(player);

			//MinecraftServer.getServer().getConfigurationManager().playerEntityList.remove(player);
			//worldserver.removePlayerEntityDangerously(player);
			worldserver.getPlayerManager().removePlayer((EntityPlayerMP) player);


			//PacketDispatcher.sendPacketToPlayer(new Packet9Respawn(world.provider.dimensionId, (byte)world.difficultySetting, world.getWorldInfo().getTerrainType(), world.getHeight(), ((EntityPlayerMP)player).theItemInWorldManager.getGameType()), (Player) player);
			((EntityPlayerMP)player).playerNetServerHandler.setPlayerLocation(-300, 80, 200, 0, 87);
			//((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(world.provider.dimensionId, (byte)world.difficultySetting, world.getWorldInfo().getTerrainType(), world.getHeight(), ((EntityPlayerMP)player).theItemInWorldManager.getGameType()));
			//((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(new Packet6SpawnPosition(0, 80,0)); //TODO: fix



			worldserver.getPlayerManager().addPlayer(entityClientCameramp);
			worldserver.getEntityTracker().addEntityToTracker(entityClientCameramp);
			worldserver.spawnEntityInWorld(entityClientCameramp);
			MinecraftServer.getServer().getConfigurationManager().playerEntityList.add(entityClientCameramp);

			MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).playerEntityList.add(entityClientCameramp);
		}
		else {


		}*/
		return false;
	}

	@Override
	public double failureChance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub

	}

}
