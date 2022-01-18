package zmaster587.advancedRocketry.common;

import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.network.PacketLaserGun;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.ZUtils;

public class CommonProxy {

	private static zmaster587.advancedRocketry.dimension.DimensionManager dimensionManagerServer = new zmaster587.advancedRocketry.dimension.DimensionManager();
	
	public void registerRenderers() { }

	public void registerEventHandlers() { }

	public void spawnParticle(BasicParticleType particle, World world, double x, double y, double z, double motionX, double motionY, double motionZ) { }

	public void registerKeyBindings() { }

	public IProfiler getProfiler() {
		return  ServerLifecycleHooks.getCurrentServer().getProfiler();
	}

	public void changeClientPlayerWorld(World world) { }

	public void fireFogBurst(ISpaceObject station) {
		PacketHandler.sendToNearby(new PacketStationUpdate(station, PacketStationUpdate.Type.SIGNAL_WHITE_BURST), DimensionManager.spaceId, station.getSpawnLocation().x, 128, station.getSpawnLocation().z, ARConfiguration.getCurrentConfig().stationSize.get());
	}

	public float calculateCelestialAngleSpaceStation() {
		return 0;
	}

	public long getWorldTimeUniversal() {
		try {
			
			return ZUtils.getWorld(Dimension.OVERWORLD.getRegistryName()).getGameTime();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void preinit() { }

	public void init() { }

	public void spawnLaser(Entity entity, Vector3d toPos) {
		PacketHandler.sendToPlayersTrackingEntity(new PacketLaserGun(entity, toPos), entity);
	}
	
	public void displayMessage(String msg, int time) { }

	public String getNameFromBiome(Biome biome) {
		return "";
	}

	public zmaster587.advancedRocketry.dimension.DimensionManager getDimensionManager() {
		return dimensionManagerServer;
	}

	public void initDeferredRegistries() { }
}
