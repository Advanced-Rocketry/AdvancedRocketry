package zmaster587.advancedRocketry.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public interface IAtmosphere {
	
	/**
	 * @param player living entity inside this atmosphere we are ticking
	 * @return true if the atmosphere does not affect the entity in any way
	 */
	boolean isImmune(LivingEntity player);
	
	/**
	 * Used by spawning and atmosphere ticking, this should probably not be used elsewhere, prefer isImmune(LivingEntity player) where performance allows
	 * @param entityClass class of the entity to check for immunity
	 * @return true if the entity class should ALWAYS be immune
	 */
	boolean isImmune(Class<? extends Entity> entityClass);
	
	/**
	 * To be used to check if combustion can occur in this atmosphere, furnaces, torches, engines, etc could run this check
	 * @return true if the atmosphere is combustable
	 */
	boolean allowsCombustion();
	
	/**
	 * Should the gas run a tick on every player in it?  Calls onTick(EntityLiving base)
	 * @return true if the atmosphere performs an action every tick
	 */
	boolean canTick();
	
	/**
	 * If the canTick() returns true then then this is called every tick on LivingEntity objects located inside this atmosphere
	 * @param player entity being ticked
	 */
	void onTick(LivingEntity player);
	
	/**
	 * @return unlocalized name of the gas
	 */
	String getUnlocalizedName();
	
	
	/**
	 * @return true if the atmosphere is normally breathable without a suit
	 */
	boolean isBreathable();
	
	/**
	 * @return unlocalized message to display when player is in the gas with no protection
	 */
	String getDisplayMessage();
}
