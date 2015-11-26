package zmaster587.advancedRocketry.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;

public class AtmosphereType {

	public static final AtmosphereType AIR = new AtmosphereType(false);
	public static final AtmosphereType VACUUM = new AtmosphereType(false);
	
	static {
		AIR.isBreathable = true;
	}
	
	private boolean allowsCombustion;
	private boolean isBreathable;
	private boolean canTick;
	
	public AtmosphereType(boolean canTick) {
		allowsCombustion = false;
		isBreathable = false;
		this.canTick = canTick;
	}
	
	public boolean isBreathable() {
		return isBreathable;
	}

	public boolean canTick() {
		return !isBreathable() || canTick;
	}

	public boolean isImmune(EntityLiving entity) {
		return false;
	}
	
	public boolean allowsCombustion() {
		return allowsCombustion;
	}
	
	public void setIsBreathable(boolean isBreathable) {
		this.isBreathable = isBreathable;
	}
	
	public void setAllowsCombustion(boolean allowsCombustion) {
		this.allowsCombustion = allowsCombustion;
	}

	public void onTick(EntityLiving entity) {
		if(!isImmune(entity)) {
			if(!isBreathable()) {
				entity.attackEntityFrom(DamageSource.inWall, 1);
			}
		}
	}
}
