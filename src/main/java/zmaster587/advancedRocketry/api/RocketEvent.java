package zmaster587.advancedRocketry.api;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;

public class RocketEvent extends EntityEvent {
	public final World world;
	public RocketEvent(Entity entity) {
		super(entity);
		world = entity.worldObj;
	}

	public static class RocketLaunchEvent extends RocketEvent {
		public RocketLaunchEvent(Entity entity) {
			super(entity);
		}
	}
	
	public static class RocketDeOrbitingEvent extends RocketEvent {
		public RocketDeOrbitingEvent(Entity entity) {
			super(entity);
		}
	}
	
	public static class RocketLandedEvent extends RocketEvent {
		public RocketLandedEvent(Entity entity) {
			super(entity);
		}
	}
	
	public static class RocketReachesOrbitEvent extends RocketEvent {
		public RocketReachesOrbitEvent(Entity entity) {
			super(entity);
		}
	}
}
