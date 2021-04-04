package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;

public class SatelliteSpyTelescope extends SatelliteBase {

	//TODO: write position to player file and teleport player then subscribe to onlogin
	@Override
	public String getInfo(World world) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Spy in the sky";
	}

	@Override
	public boolean performAction(PlayerEntity player, World world, BlockPos pos) {

		/*
		//cam.setPosition(player.posX, player.posY + 30, player.posZ);
		if(!world.isRemote) {			
			player.setPosition(-300, 80, 250);
			//MinecraftServer.getServer().getConfigurationManager().
			EntityCameraMP entityClientCameramp = new EntityCameraMP(MinecraftServer.getServer(), world, "Camera", new ItemInWorldManager(world));
			ServerWorld worldserver = ((ServerPlayerEntity)player).getServerForPlayer();

			entityClientCameramp.playerNetServerHandler = ((ServerPlayerEntity)player).playerNetServerHandler;
			entityClientCameramp.dimension = world.provider.dimensionId;
			entityClientCameramp.entityId = player.entityId;
			//      worldserver.theChunkProviderServer.loadChunk((int)entityplayermp1.posX >> 4, (int)entityplayermp1.posZ >> 4);
			((ServerPlayerEntity)player).playerNetServerHandler.playerEntity = entityClientCameramp;

			worldserver.getEntityTracker().removePlayerFromTrackers((ServerPlayerEntity) player);
			worldserver.getEntityTracker().removeEntityFromAllTrackingPlayers((ServerPlayerEntity) player);
			//((ServerPlayerEntity)player).getServerForPlayer().getPlayerManager().removePlayer((ServerPlayerEntity) player);
			//MinecraftServer.getServer().getWorld(player.dimension).removePlayerEntityDangerously(player);

			//MinecraftServer.getServer().getConfigurationManager().playerEntityList.remove(player);
			//worldserver.removePlayerEntityDangerously(player);
			worldserver.getPlayerManager().removePlayer((ServerPlayerEntity) player);


			//PacketDispatcher.sendPacketToPlayer(new Packet9Respawn(world.provider.dimensionId, (byte)world.difficultySetting, world.getWorldInfo().getTerrainType(), world.getHeight(), ((ServerPlayerEntity)player).theItemInWorldManager.getGameType()), (Player) player);
			((ServerPlayerEntity)player).playerNetServerHandler.setPlayerLocation(-300, 80, 200, 0, 87);
			//((ServerPlayerEntity)player).playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(world.provider.dimensionId, (byte)world.difficultySetting, world.getWorldInfo().getTerrainType(), world.getHeight(), ((ServerPlayerEntity)player).theItemInWorldManager.getGameType()));
			//((ServerPlayerEntity)player).playerNetServerHandler.sendPacketToPlayer(new Packet6SpawnPosition(0, 80,0)); //TODO: fix



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
	public void writeToNBT(CompoundNBT nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(CompoundNBT nbt) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canTick() {
		return false;
	}

	@Override
	public void tickEntity() {

	}

}
