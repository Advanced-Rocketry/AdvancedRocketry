package zmaster587.advancedRocketry.api.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.api.network.PacketHandler;
import zmaster587.advancedRocketry.api.network.PacketOxygenState;

public class AtmosphereVacuum extends AtmosphereType {

	public AtmosphereVacuum() {
		super(true, false);
	}

	public void onTick(EntityLivingBase player) {
		if(player.worldObj.getTotalWorldTime() % 10  == 0 && !isImmune(player)) {
			if(!isBreathable()) {
				player.attackEntityFrom(AtmosphereHandler.vacuumDamage, 1);
				if(player instanceof EntityPlayer)
					PacketHandler.sendToPlayer(new PacketOxygenState(), (EntityPlayer)player);
			}
		}
	}

	
	public boolean isImmune(EntityLivingBase player) {

		//Checks if player is wearing spacesuit or anything that extends ItemSpaceArmor
		ItemStack feet = player.getEquipmentInSlot(1);
		ItemStack leg = player.getEquipmentInSlot(2);
		ItemStack chest = player.getEquipmentInSlot(3);
		ItemStack helm = player.getEquipmentInSlot(4);

		return (player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode) 
				|| player.ridingEntity instanceof EntityRocketBase ||
				helm != null && helm.getItem() instanceof ItemSpaceArmor &&
				chest != null && chest.getItem() instanceof ItemSpaceArmor &&
				leg != null && leg.getItem() instanceof ItemSpaceArmor &&
				feet != null && feet.getItem() instanceof ItemSpaceArmor &&
				((ItemSpaceArmor)AdvancedRocketryItems.itemSpaceSuit_Chest).decrementAir(chest, 1) > 0;
	}
}
