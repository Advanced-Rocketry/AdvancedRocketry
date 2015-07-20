package zmaster587.advancedRocketry.api;

import zmaster587.advancedRocketry.entity.EntityRocket;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;

public class RocketEvent  extends EntityEvent {
	public final World world;
	public RocketEvent(Entity entity) {
		super(entity);
		world = entity.worldObj;
	}

	public static class RocketLaunchEvent extends RocketEvent {
		public RocketLaunchEvent(EntityRocket entity) {
			super(entity);
		}
	}
	
	public static class RocketDeOrbitingEvent extends RocketEvent {
		public RocketDeOrbitingEvent(EntityRocket entity) {
			super(entity);
		}
	}
	
	public static class RocketLandedEvent extends RocketEvent {
		public RocketLandedEvent(EntityRocket entity) {
			super(entity);
		}
	}
	
	public static class RocketReachesOrbitEvent extends RocketEvent {
		public RocketReachesOrbitEvent(EntityRocket entity) {
			super(entity);
		}
	}
}
