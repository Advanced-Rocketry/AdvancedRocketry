package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketOxygenState;

/**
 * Atmosphere type for vaccum (No air)
 * @author Zmaster
 */
public class AtmosphereVacuum extends AtmosphereType {

	public AtmosphereVacuum() {
		super(true, false, "vacuum");
	}

	@Override
	public void onTick(EntityLivingBase player) {
		if(player.worldObj.getTotalWorldTime() % 10  == 0 && !isImmune(player)) {
			if(!isImmune(player)) {
				player.attackEntityFrom(AtmosphereHandler.vacuumDamage, 1);
				if(player instanceof EntityPlayer)
					PacketHandler.sendToPlayer(new PacketOxygenState(), (EntityPlayer)player);
			}
		}
	}

	@Override
	public boolean isImmune(EntityLivingBase player) {

		//Checks if player is wearing spacesuit or anything that extends ItemSpaceArmor
		ItemStack feet = player.getEquipmentInSlot(1);
		ItemStack leg = player.getEquipmentInSlot(2);
		ItemStack chest = player.getEquipmentInSlot(3);
		ItemStack helm = player.getEquipmentInSlot(4);

		//TODO change over to use API #ISealedArmor
		return (player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode) 
				|| player.ridingEntity instanceof EntityRocketBase ||
				helm != null && helm.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)helm.getItem()).protectsFromSubstance(this) &&
				chest != null && chest.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)chest.getItem()).protectsFromSubstance(this) &&
				leg != null && leg.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)leg.getItem()).protectsFromSubstance(this) &&
				feet != null && feet.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)feet.getItem()).protectsFromSubstance(this) &&
				(chest.getItem() instanceof IFillableArmor) && ((IFillableArmor)AdvancedRocketryItems.itemSpaceSuit_Chest).decrementAir(chest, 1) > 0;
	}
}
