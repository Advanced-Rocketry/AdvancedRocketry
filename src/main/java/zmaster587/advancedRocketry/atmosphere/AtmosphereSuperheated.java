package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import zmaster587.libVulpes.LibVulpes;

public class AtmosphereSuperheated extends AtmosphereNeedsSuit {

	public AtmosphereSuperheated(boolean canTick, boolean isBreathable, boolean allowsCombustion,
                                 String name) {
		super(canTick, isBreathable, allowsCombustion, name);
	}
	

	@Override
	public String getDisplayMessage() {
		return LibVulpes.proxy.getLocalizedString("msg.tooHot");
	}
	
	// Needs full pressure suit
	protected boolean onlyNeedsMask()
	{
		return false;
	}
	
	@Override
	public void onTick(LivingEntity player) {
		if(player.world.getGameTime() % 20  == 0 && !isImmune(player)) {
			player.setFire(1);
			player.attackEntityFrom(AtmosphereHandler.heatDamage, 4);
			player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 40, 3));
		}
	}
}
