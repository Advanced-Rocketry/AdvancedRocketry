package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.libVulpes.network.PacketHandler;

/**
 * Atmosphere type for vaccum (No air)
 * @author Zmaster
 */
public class AtmosphereVacuum extends AtmosphereType {

	static Class powerSuitItem;
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
	public String getDisplayMessage() {
		return "Warning: No Oxygen detected!";
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
				helm != null && (helm.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)helm.getItem()).protectsFromSubstance(this, helm, true) || protectsFrom(helm, 1)) &&
				chest != null && (chest.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)chest.getItem()).protectsFromSubstance(this, chest, true) || protectsFrom(chest, 2)) &&
				leg != null && (leg.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)leg.getItem()).protectsFromSubstance(this, leg, true) || protectsFrom(leg, 3)) &&
				feet != null && (feet.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)feet.getItem()).protectsFromSubstance(this, feet, true) || protectsFrom(feet, 4)) &&
				(protectsFrom(chest, 2) || ((chest.getItem() instanceof IFillableArmor) && ((IFillableArmor)AdvancedRocketryItems.itemSpaceSuit_Chest).decrementAir(chest, 1) > 0));
	}
	
	public static boolean protectsFrom(ItemStack stack, int slot) {
		
		if(CompatibilityMgr.powerSuits) {
			if( powerSuitItem == null)
				try {
					powerSuitItem = Class.forName("net.machinemuse.powersuits.item.ItemPowerArmor");
				} catch (ClassNotFoundException e) {
					//Silently fail to prevent spam
					return false;
				}
			
			if(powerSuitItem.isInstance(stack.getItem()))
				return true;
		}
		return false;
	}
}
