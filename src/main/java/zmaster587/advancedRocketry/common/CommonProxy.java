package zmaster587.advancedRocketry.common;

import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.libVulpes.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
		return FMLCommonHandler.instance().getMinecraftServerInstance().theProfiler;
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
}
