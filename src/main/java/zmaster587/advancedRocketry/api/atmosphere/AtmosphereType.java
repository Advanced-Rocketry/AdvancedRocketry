package zmaster587.advancedRocketry.api.atmosphere;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
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

	public boolean isImmune(EntityLivingBase player) {

		ItemStack helm = player.getEquipmentInSlot(1);
		ItemStack shirt = player.getEquipmentInSlot(2);
		ItemStack leg = player.getEquipmentInSlot(3);
		ItemStack feet = player.getEquipmentInSlot(4);

		return (player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode) ||
				helm != null && helm.getItem() instanceof ItemArmor && ((ItemArmor)helm.getItem()).getArmorMaterial() == AdvancedRocketryItems.spaceSuit &&
				shirt != null && shirt.getItem() instanceof ItemArmor && ((ItemArmor)shirt.getItem()).getArmorMaterial() == AdvancedRocketryItems.spaceSuit &&
				leg != null && leg.getItem() instanceof ItemArmor && ((ItemArmor)leg.getItem()).getArmorMaterial() == AdvancedRocketryItems.spaceSuit &&
				feet != null && feet.getItem() instanceof ItemArmor && ((ItemArmor)feet.getItem()).getArmorMaterial() == AdvancedRocketryItems.spaceSuit &&
				((ItemSpaceArmor)AdvancedRocketryItems.itemSpaceSuit_Chest).decrementAir(leg, 1) > 0;
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

	public void onTick(EntityLivingBase player) {
		if(player.worldObj.getTotalWorldTime() % 10  == 0 && !isImmune(player)) {
			if(!isBreathable()) {
				player.attackEntityFrom(AdvancedRocketry.vacuumDamage, 1);
				if(player instanceof EntityPlayer)
					PacketHandler.sendToPlayer(new PacketOxygenState(), (EntityPlayer)player);
			}
		}
	}
}
