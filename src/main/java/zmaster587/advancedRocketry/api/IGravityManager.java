package zmaster587.advancedRocketry.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public interface IGravityManager {
	
	/**
	 * @param entity entity to apply custom gravity on
	 * @param multiplier magnitude of the gravitational effects on the entity, 1 is earthlike
	 */
	public void setGravityMultiplier(Entity entity, double multiplier);
	
	/**
	 * Removes the specified entity from the list of entities with custom gravity effects and normal planetary gravity takes over
	 * @param entity entity to remove from custom gravity
	 */
	public void clearGravityEffect(Entity entity);
}
