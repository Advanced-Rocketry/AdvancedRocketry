package zmaster587.advancedRocketry.common;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

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
		return MinecraftServer.getServer().theProfiler;
	}

	public void changeClientPlayerWorld(World world) {
		
	}

	public void fireFogBurst(ISpaceObject station) {
		PacketHandler.sendToNearby(new PacketStationUpdate(station, PacketStationUpdate.Type.SIGNAL_WHITE_BURST), Configuration.spaceDimId, station.getSpawnLocation().x, 128, station.getSpawnLocation().z, Configuration.stationSize);
	}
	
	public String getLocalizedString(String str) {
		return str;
	}
}
