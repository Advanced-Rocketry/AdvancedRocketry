package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.network.PacketHandler;

public class AtmosphereNoOxygen extends AtmosphereNeedsSuit {

	public static boolean enableNausea = ARConfiguration.getCurrentConfig().enableNausea.get();

	public AtmosphereNoOxygen(boolean canTick, boolean isBreathable, boolean allowsCombustion,
                              String name) {
		super(canTick, isBreathable, allowsCombustion, name);
	}

	@Override
	public String getDisplayMessage() {
		return LibVulpes.proxy.getLocalizedString("msg.nooxygen");
	}
	
	@Override
	public void onTick(LivingEntity player) {
		if(player.world.getGameTime() % 10  == 0 && !isImmune(player)) {
			player.attackEntityFrom(AtmosphereHandler.lowOxygenDamage, 1);
			player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 40, 4));
			player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 40, 4));
			if(enableNausea) {
				player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 400, 1));
			}
			if(player instanceof PlayerEntity)
				PacketHandler.sendToPlayer(new PacketOxygenState(), (PlayerEntity)player);
		}
	}
	
	// True if only a helmet is needed
	protected boolean onlyNeedsMask()
	{
		return true;
	}
}
