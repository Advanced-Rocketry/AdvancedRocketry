package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
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

		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		return (player instanceof EntityPlayer && ((EntityPlayer)player).capabilities.isCreativeMode) 
				|| player.getRidingEntity() instanceof EntityRocketBase ||
				helm != null && helm.hasCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, EnumFacing.DOWN) && helm.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, EnumFacing.DOWN).protectsFromSubstance(this, helm, true) &&
				chest != null && chest.hasCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, EnumFacing.DOWN) && chest.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, EnumFacing.DOWN).protectsFromSubstance(this, chest, true);
	}
}
