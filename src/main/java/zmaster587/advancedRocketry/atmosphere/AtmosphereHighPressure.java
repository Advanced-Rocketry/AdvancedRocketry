package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.libVulpes.network.PacketHandler;

public class AtmosphereHighPressure extends AtmosphereType{
	
	public AtmosphereHighPressure(boolean canTick, boolean isBreathable,
			String name) {
		super(canTick, isBreathable, name);
	}
	

	@Override
	public String getDisplayMessage() {
		return "Warning: Pressure too high!";
	}
	
	@Override
	public void onTick(EntityLivingBase player) {
		if(player.world.getTotalWorldTime() % 20  == 0 && !isImmune(player)) {
			if(!isImmune(player)) {
				player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 40, 3));
				player.addPotionEffect(new PotionEffect(Potion.getPotionById(4), 40, 3));
			}
		}
	}
	
	
	@Override
	public boolean isImmune(EntityLivingBase player) {

		//Checks if player is wearing spacesuit or anything that extends ItemSpaceArmor

		ItemStack feet = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		ItemStack leg = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS /*so hot you can fry an egg*/ );
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		return (player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode) 
				|| player.getRidingEntity() instanceof EntityRocketBase ||
				helm != null && helm.hasCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null) && helm.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null).protectsFromSubstance(this, helm, true) &&
				chest != null && chest.hasCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null) && chest.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null).protectsFromSubstance(this, chest, true) &&
				leg != null && leg.hasCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null) && leg.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null).protectsFromSubstance(this, leg, true) &&
				feet != null && feet.hasCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null) && feet.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null).protectsFromSubstance(this, feet, true);
	}
}
