package zmaster587.advancedRocketry.api;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Events related to rockets here
 *
 */
public class RocketEvent extends EntityEvent {
	public final World world;
	public RocketEvent(Entity entity) {
		super(entity);
		world = entity.world;
	}

	/**
	 * Fired when the rocket is launched on the Minecraft Forge EVENT_BUS
	 */
	public static class RocketLaunchEvent extends RocketEvent {
		public RocketLaunchEvent(Entity entity) {
			super(entity);
		}
	}
	
	/**
	 * Fired when the rocket is launched on the Minecraft Forge EVENT_BUS
	 * Cancelling the event aborts the launch
	 */
	@Cancelable
	public static class RocketPreLaunchEvent extends RocketEvent {
		public RocketPreLaunchEvent(Entity entity) {
			super(entity);
		}
	}
	
	/**
	 * Fired before the rocket is finished teleporting to the destination world on the Minecraft Forge EVENT_BUS
	 */
	public static class RocketDeOrbitingEvent extends RocketEvent {
		public RocketDeOrbitingEvent(Entity entity) {
			super(entity);
		}
	}
	
	/**
	 * Fired when the rocket touches the ground on the Minecraft Forge EVENT_BUS
	 */
	public static class RocketLandedEvent extends RocketEvent {
		public RocketLandedEvent(Entity entity) {
			super(entity);
		}
	}
	
	/**
	 * Fired right before the rocket is dismantled on the Minecraft Forge EVENT_BUS
	 */
	public static class RocketDismantleEvent extends RocketEvent {
		public RocketDismantleEvent(Entity entity) {
			super(entity);
		}
	}
	
	/**
	 * Fired when the rocket is about to be teleported to its destination on the Minecraft Forge EVENT_BUS
	 */
	public static class RocketReachesOrbitEvent extends RocketEvent {
		public RocketReachesOrbitEvent(Entity entity) {
			super(entity);
		}
	}
}
