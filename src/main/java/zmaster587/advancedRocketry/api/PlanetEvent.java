package zmaster587.advancedRocketry.api;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class PlanetEvent extends WorldEvent {

	public PlanetEvent(World world) {
		super(world);
	}
	
	
	public static class PlanetCreateEvent extends PlanetEvent {
		public PlanetCreateEvent(World world) {
			super(world);
		}
	}

}
