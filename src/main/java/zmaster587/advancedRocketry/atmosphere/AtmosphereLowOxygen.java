package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.advancedRocketry.util.ItemAirUtils;
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

		return (player instanceof EntityPlayer && ((((EntityPlayer)player).capabilities.isCreativeMode) || ((EntityPlayer)player).isSpectator()))
				|| player.getRidingEntity() instanceof EntityRocketBase || player.getRidingEntity() instanceof EntityElevatorCapsule ||
				protectsFrom(helm) && protectsFrom(chest);
	}
	
	public boolean protectsFrom(ItemStack stack) {
		return (ItemAirUtils.INSTANCE.isStackValidAirContainer(stack) && new ItemAirUtils.ItemAirWrapper(stack).protectsFromSubstance(this, stack, true) ) || (stack != null && stack.hasCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null) &&
				stack.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null).protectsFromSubstance(this, stack, true));
	}
}
