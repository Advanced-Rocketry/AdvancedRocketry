package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.network.PacketHandler;

public class AtmosphereNoOxygen extends AtmosphereNeedsSuit {

	public static boolean enableNausea = ARConfiguration.getCurrentConfig().enableNausea;

	public AtmosphereNoOxygen(boolean canTick, boolean isBreathable, boolean allowsCombustion,
                              String name) {
		super(canTick, isBreathable, allowsCombustion, name);
	}

	@Override
	public String getDisplayMessage() {
		return LibVulpes.proxy.getLocalizedString("msg.noOxygen");
	}
	
	@Override
	public void onTick(EntityLivingBase player) {
		if(player.world.getTotalWorldTime() % 10  == 0 && !isImmune(player)) {
			player.attackEntityFrom(AtmosphereHandler.lowOxygenDamage, 1);
			player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 40, 4));
			player.addPotionEffect(new PotionEffect(Potion.getPotionById(4), 40, 4));
			if(enableNausea) {
				player.addPotionEffect(new PotionEffect(Potion.getPotionById(9), 400, 1));
			}
			if(player instanceof EntityPlayer)
				PacketHandler.sendToPlayer(new PacketOxygenState(), (EntityPlayer)player);
		}
	}
	
	// True if only a helmet is needed
	protected boolean onlyNeedsMask()
	{
		return true;
	}
}
