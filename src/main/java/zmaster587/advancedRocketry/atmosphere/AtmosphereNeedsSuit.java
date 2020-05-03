package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.integration.CompatibilityMgr;

public class AtmosphereNeedsSuit extends AtmosphereType {
	
	static Class powerSuitItem;
	
	public AtmosphereNeedsSuit(boolean canTick, boolean isBreathable, boolean allowsCombustion, String name) {
		super(canTick, isBreathable, allowsCombustion, name);
	}
	
	// True if only a helmet is needed
	protected boolean onlyNeedsMask()
	{
		return allowsCombustion();
	}
	
	@Override
	public boolean isImmune(EntityLivingBase player) {

		//Checks if player is wearing spacesuit or anything that extends ItemSpaceArmor
		ItemStack feet = player.getEquipmentInSlot(1);
		ItemStack leg = player.getEquipmentInSlot(2);
		ItemStack chest = player.getEquipmentInSlot(3);
		ItemStack helm = player.getEquipmentInSlot(4);

		// Note: protectsFrom(chest) is intentionally the last thing to check here.  This is because java will bail on the check early if others fail
		// this will prevent the O2 level in the chest from being needlessly decremented
		return (player instanceof EntityPlayer && ((((EntityPlayer)player).capabilities.isCreativeMode)))
				|| player.ridingEntity instanceof EntityRocketBase || player.ridingEntity instanceof EntityElevatorCapsule ||
				( ( (!onlyNeedsMask() && protectsFrom(leg) && protectsFrom(feet) ) || onlyNeedsMask() ) && protectsFrom(helm) && protectsFrom(chest) );
		}

	protected boolean protectsFrom(ItemStack stack) {

		if(stack == null)
			return false;
		
		//Check for enchantment
		boolean isEnchanted = false;
		NBTTagList enchList = stack.getEnchantmentTagList();
		if(enchList != null) {
			for(int i = 0 ; i < enchList.tagCount(); i++) {
				NBTTagCompound compound = enchList.getCompoundTagAt(i);
				isEnchanted = compound.getShort("id") == AdvancedRocketryAPI.enchantmentSpaceProtection.effectId;
				if(isEnchanted)
					break;
			}
		}
		
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
		
		
		return isEnchanted || (stack != null && (stack.getItem() instanceof IProtectiveArmor && ((IProtectiveArmor)stack.getItem()).protectsFromSubstance(this, stack, true)));
	}

}
