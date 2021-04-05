package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.network.PacketHandler;

public class AtmosphereSuperHighPressure extends AtmosphereNeedsSuit {
	
	public AtmosphereSuperHighPressure(boolean canTick, boolean isBreathable, boolean allowsCombustion,
			String name) {
		super(canTick, isBreathable, allowsCombustion, name);
	}
	

	@Override
	public String getDisplayMessage() {
		return LibVulpes.proxy.getLocalizedString("msg.muchtoodense");
	}
	
	// Needs full pressure suit
	protected boolean onlyNeedsMask()
	{
		return false;
	}
	
	@Override
	public void onTick(LivingEntity player) {
		if(player.world.getGameTime() % 20  == 0 && !isImmune(player)) {
			player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 40, 3));
			player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 40, 3));
			player.attackEntityFrom(AtmosphereHandler.oxygenToxicityDamage, 1);
			if(player instanceof PlayerEntity)
				PacketHandler.sendToPlayer(new PacketOxygenState(), (PlayerEntity)player);
		}
	}
}
