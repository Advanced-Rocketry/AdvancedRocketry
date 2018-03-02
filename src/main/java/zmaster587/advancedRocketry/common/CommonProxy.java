package zmaster587.advancedRocketry.common;

import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.network.PacketLaserGun;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
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
}
