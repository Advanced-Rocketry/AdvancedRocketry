package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.network.PacketHandler;

public class AtmosphereLowOxygen extends AtmosphereNeedsSuit {
	
	public AtmosphereLowOxygen(boolean canTick, boolean isBreathable, boolean allowsCombustion,
			String name) {
		super(canTick, isBreathable, allowsCombustion, name);
	}

	@Override
	public String getDisplayMessage() {
		return LibVulpes.proxy.getLocalizedString("msg.noOxygen");
	}
	
	@Override
	public void onTick(EntityLivingBase player) {
		if(player.worldObj.getTotalWorldTime() % 20  == 0 && !isImmune(player)) {
			player.attackEntityFrom(AtmosphereHandler.lowOxygenDamage, 1);
			player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 40, 2));
			player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 2));
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
