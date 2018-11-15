package zmaster587.advancedRocketry.common;

import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.network.PacketAsteroidInfo;
import zmaster587.advancedRocketry.network.PacketConfigSync;
import zmaster587.advancedRocketry.network.PacketDimInfo;
import zmaster587.advancedRocketry.network.PacketLaserGun;
import zmaster587.advancedRocketry.network.PacketSpaceStationInfo;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.network.PacketStellarInfo;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.AsteroidSmall;
import zmaster587.libVulpes.network.PacketHandler;

public class CommonProxy {


	public void registerRenderers() {

	}

	public void registerEventHandlers() {

	}


	public void spawnParticle(String particle, World world, double x, double y,
			double z, double motionX, double motionY, double motionZ) {

	}

	public void registerKeyBindings() {

	}

	public Profiler getProfiler() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().profiler;
	}

	public void changeClientPlayerWorld(World world) {

	}

	public void fireFogBurst(ISpaceObject station) {
		PacketHandler.sendToNearby(new PacketStationUpdate(station, PacketStationUpdate.Type.SIGNAL_WHITE_BURST), Configuration.spaceDimId, station.getSpawnLocation().x, 128, station.getSpawnLocation().z, Configuration.stationSize);
	}



	public float calculateCelestialAngleSpaceStation() {
		return 0;
	}

	public long getWorldTimeUniversal(int id) {
		if(DimensionManager.getWorld(id) != null)
			return DimensionManager.getWorld(id).getTotalWorldTime();
		return 0;
	}

	public void preinit() {
		// TODO Auto-generated method stub

	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public void spawnLaser(Entity entity, Vec3d toPos) {
		PacketHandler.sendToPlayersTrackingEntity(new PacketLaserGun(entity, toPos), entity);
	}

	public void loadUILayout(
			net.minecraftforge.common.config.Configuration config) {
		// TODO Auto-generated method stub
		
	}

	public void saveUILayout(
			net.minecraftforge.common.config.Configuration configuration) {
		// TODO Auto-generated method stub
		
	}
	
	public void displayMessage(String msg, int time) {
		
	}

	public void preInitBlocks() {
		// TODO Auto-generated method stub
		
	}

	public void preInitItems() {
		// TODO Auto-generated method stub
		
	}
	
	public void loginEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayerMP playerMP = (EntityPlayerMP)event.player;
		net.minecraft.network.NetworkManager manager = playerMP.connection.netManager;
		
		//Send config first
		PacketHandler.sendToDispatcher(new PacketConfigSync(), manager);
		
		//Make sure stars are sent next
		for(int i : zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getStarIds()) {
			PacketHandler.sendToDispatcher(new PacketStellarInfo(i, zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getStar(i)), manager);
		}

		for(int i : zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getRegisteredDimensions()) {
			PacketHandler.sendToDispatcher(new PacketDimInfo(i, zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(i)), manager);
		}

		for(ISpaceObject obj : SpaceObjectManager.getSpaceManager().getSpaceObjects()) {
			PacketHandler.sendToDispatcher(new PacketSpaceStationInfo(obj.getId(), obj), manager);
		}

		PacketHandler.sendToDispatcher(new PacketDimInfo(0, zmaster587.advancedRocketry.dimension.DimensionManager.getInstance().getDimensionProperties(0)), manager);


		for(Entry<String, AsteroidSmall> ent : zmaster587.advancedRocketry.api.Configuration.asteroidTypes.entrySet())
		{
			PacketHandler.sendToDispatcher(new PacketAsteroidInfo(ent.getValue()), manager);
		}
	}

	public String getNameFromBiome(Biome biome) {
		return "";
	}
}
