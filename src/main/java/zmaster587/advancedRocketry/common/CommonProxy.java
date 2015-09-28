package zmaster587.advancedRocketry.common;

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

	public String getLocalizedString(String str) {
		return str;
	}
}
