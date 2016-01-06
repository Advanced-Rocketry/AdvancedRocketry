package zmaster587.advancedRocketry.api.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.api.network.PacketHandler;
import zmaster587.advancedRocketry.api.network.PacketOxygenState;

/**
 * Atmosphere type for vaccum (No air)
 * @author Zmaster
 */
public class AtmosphereVacuum extends AtmosphereType {

	public AtmosphereVacuum() {
		super(true, false);
	}

	@Override
	public void onTick(EntityLivingBase player) {
		if(player.worldObj.getTotalWorldTime() % 10  == 0 && !isImmune(player)) {
			if(!isBreathable()) {
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
				//TODO make helper to see if player is wearing a set
				//TODO add API #isPartOfSet(ItemStack) and #isSealed(ItemStack)
				helm != null && helm.getItem() instanceof ItemSpaceArmor &&
				chest != null && chest.getItem() instanceof ItemSpaceArmor &&
				leg != null && leg.getItem() instanceof ItemSpaceArmor &&
				feet != null && feet.getItem() instanceof ItemSpaceArmor &&
						//TODO add API #isAirTank(ItemStack)
						//TODO use Fluid Item interface ?
				((ItemSpaceArmor)AdvancedRocketryItems.itemSpaceSuit_Chest).decrementAir(chest, 1) > 0;
	}
}
