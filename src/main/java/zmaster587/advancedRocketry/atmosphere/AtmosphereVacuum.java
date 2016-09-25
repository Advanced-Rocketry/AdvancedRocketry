package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.libVulpes.network.PacketHandler;

/**
 * Atmosphere type for vaccum (No air)
 * @author Zmaster
 */
public class AtmosphereVacuum extends AtmosphereType {

	
	public static int damageValue;
	
	public AtmosphereVacuum() {
		super(true, false, "vacuum");
	}

	@Override
	public void onTick(EntityLivingBase player) {
		if(player.worldObj.getTotalWorldTime() % 10  == 0 && !isImmune(player)) {
			if(!isImmune(player)) {
				player.attackEntityFrom(AtmosphereHandler.vacuumDamage, damageValue);
				if(player instanceof EntityPlayer)
					PacketHandler.sendToPlayer(new PacketOxygenState(), (EntityPlayer)player);
			}
		}
	}

	@Override
	public boolean isImmune(EntityLivingBase player) {

		//Checks if player is wearing spacesuit or anything that extends ItemSpaceArmor

		ItemStack feet = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		ItemStack leg = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		//TODO change over to use API #ISealedArmor
		return (player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode) 
				|| player.getRidingEntity() instanceof EntityRocketBase ||
				helm != null && helm.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)helm.getItem()).protectsFromSubstance(this) &&
				chest != null && chest.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)chest.getItem()).protectsFromSubstance(this) &&
				leg != null && leg.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)leg.getItem()).protectsFromSubstance(this) &&
				feet != null && feet.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)feet.getItem()).protectsFromSubstance(this) &&
				(chest.getItem() instanceof IFillableArmor) && ((IFillableArmor)AdvancedRocketryItems.itemSpaceSuit_Chest).decrementAir(chest, 1) > 0;
	}
}
