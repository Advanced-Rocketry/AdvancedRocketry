package zmaster587.advancedRocketry.api;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

/**
 * Contains events fired when something happens with a planet
 *
 */
public class PlanetEvent extends WorldEvent {

	public PlanetEvent(World world) {
		super(world);
	}
	
	
	/**
	 * Fired when a new planet is created
	 */
	public static class PlanetCreateEvent extends PlanetEvent {
		public PlanetCreateEvent(World world) {
			super(world);
		}
	}

}
