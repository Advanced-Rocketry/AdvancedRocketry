package zmaster587.advancedRocketry.atmosphere;

import cpw.mods.fml.common.registry.LanguageRegistry;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.entity.EntityLivingBase;

public class AtmosphereType implements IAtmosphere {

	public static final AtmosphereType AIR = new AtmosphereType(false, true, "air");
	public static final AtmosphereType VACUUM = new AtmosphereVacuum();

	static {
		AtmosphereRegister.getInstance().registerAtmosphere(AIR, AIR.getUnlocalizedName());
		AtmosphereRegister.getInstance().registerAtmosphere(VACUUM, VACUUM.getUnlocalizedName());
	}
	
	private boolean allowsCombustion;
	private boolean isBreathable;
	private boolean canTick;
	private String name;

	public AtmosphereType(boolean canTick, boolean isBreathable, String name) {
		this.allowsCombustion = isBreathable;
		this.isBreathable = isBreathable;
		this.canTick = canTick;
		this.name = name;
	}
	
	public AtmosphereType(boolean canTick, boolean isBreathable, boolean allowsCombustion, String name) {
		this(canTick, isBreathable, name);
		this.allowsCombustion = allowsCombustion;
	}

	/**
	 * Should the gas run a tick on every player in it?  Calls onTick(EntityLiving base)
	 * @return true if the atmosphere performs an action every tick
	 */
	public boolean canTick() {
		return canTick;
	}

	//TODO: check for all entities
	/**
	 * 
	 * @param player living entity inside this atmosphere we are ticking
	 * @return true if the atmosphere does not affect the entity in any way
	 */
	public boolean isImmune(EntityLivingBase player) {
		return isBreathable;
	}
	
	/**
	 * To be used to check if combustion can occur in this atmosphere, furnaces, torches, engines, etc could run this check
	 * @return true if the atmosphere is combustable
	 */
	public boolean allowsCombustion() {
		return allowsCombustion;
	}

	/**
	 * Sets the atmosphere to be breathable or not breathable
	 * @param isBreathable
	 */
	public void setIsBreathable(boolean isBreathable) {
		this.isBreathable = isBreathable;
	}

	/**
	 * Sets the atmosphere to allow combustion or not to allow combustion
	 * @param allowsCombustion
	 */
	public void setAllowsCombustion(boolean allowsCombustion) {
		this.allowsCombustion = allowsCombustion;
	}

	//TODO: tick for all entities
	/**
	 * If the canTick() returns true then then this is called every tick on EntityLivingBase objects located inside this atmosphere
	 * @param player entity being ticked
	 */
	public void onTick(EntityLivingBase player) {
	}

	@Override
	public String getUnlocalizedName() {
		return name;
	}
}
