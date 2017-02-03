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

public class AtmosphereLowOxygen extends AtmosphereType {

	
	public AtmosphereLowOxygen(boolean canTick, boolean isBreathable,
			String name) {
		super(canTick, isBreathable, name);
	}

	@Override
	public String getDisplayMessage() {
		return "Warning: No Oxygen detected!";
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
		
		ItemStack chest = player.getEquipmentInSlot(3);
		ItemStack helm = player.getEquipmentInSlot(4);


		//TODO change over to use API #ISealedArmor
		return (player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode) 
				|| player.ridingEntity instanceof EntityRocketBase ||
				helm != null && (helm.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)helm.getItem()).protectsFromSubstance(this, helm, true) || AtmosphereVacuum.protectsFrom(helm, 1)) &&
				chest != null && (chest.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)chest.getItem()).protectsFromSubstance(this, chest, true) || AtmosphereVacuum.protectsFrom(chest, 2)) &&
				((chest.getItem() instanceof IFillableArmor) && ((IFillableArmor)AdvancedRocketryItems.itemSpaceSuit_Chest).decrementAir(chest, 1) > 0);
	}
}
