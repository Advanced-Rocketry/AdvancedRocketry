package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.network.PacketHandler;

public class AtmosphereSuperHighPressureNoOxygen extends AtmosphereNeedsSuit {

	public static boolean enableNausea = ARConfiguration.getCurrentConfig().enableNausea;

	public AtmosphereSuperHighPressureNoOxygen(boolean canTick, boolean isBreathable, boolean allowsCombustion,
                                               String name) {
		super(canTick, isBreathable, allowsCombustion, name);
	}
	

	@Override
	public String getDisplayMessage() {
		return LibVulpes.proxy.getLocalizedString("msg.noOxygen");
	}
	
	// Needs full pressure suit
	protected boolean onlyNeedsMask()
	{
		return false;
	}
	
	@Override
	public void onTick(LivingEntity player) {
		if(player.world.getGameTime() % 10  == 0 && !isImmune(player)) {
			player.attackEntityFrom(AtmosphereHandler.lowOxygenDamage, 1);
			player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 40, 4));
			player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 40, 4));
			//Removes the ability to jump. Nitrogen/other gas narcosis
			player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 40, 150));
			if(enableNausea) {
				player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 400, 2));
			}
			if(player instanceof PlayerEntity)
				PacketHandler.sendToPlayer(new PacketOxygenState(), (PlayerEntity)player);
		}
	}
}
