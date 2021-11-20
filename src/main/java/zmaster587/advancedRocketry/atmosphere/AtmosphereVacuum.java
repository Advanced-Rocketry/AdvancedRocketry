package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.network.PacketHandler;

/**
 * Atmosphere type for vaccum (No air)
 * @author Zmaster
 */
public class AtmosphereVacuum extends AtmosphereNeedsSuit {

	public static boolean enableNausea = ARConfiguration.getCurrentConfig().enableNausea.get();

	public AtmosphereVacuum() {
		super(true, false, false, "vacuum");
	}

	@Override
	public void onTick(LivingEntity player) {
		if(player.world.getGameTime() % 10  == 0 && !isImmune(player)) {
			player.attackEntityFrom(AtmosphereHandler.vacuumDamage, 1);
			player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 40, 4));
			player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 40, 4));
			if(enableNausea) {
				player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100, 1));
			}
			if(player instanceof PlayerEntity)
				PacketHandler.sendToPlayer(new PacketOxygenState(), (PlayerEntity)player);
		}
	}

	@Override
	public String getDisplayMessage() {
		return LibVulpes.proxy.getLocalizedString("msg.nooxygen");
	}
}
