package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.advancedRocketry.util.ItemAirUtils;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.network.PacketHandler;

public class AtmosphereSuperHighPressure extends AtmosphereType{
	
	public AtmosphereSuperHighPressure(boolean canTick, boolean isBreathable,
			String name) {
		super(canTick, isBreathable, name);
	}
	

	@Override
	public String getDisplayMessage() {
		return LibVulpes.proxy.getLocalizedString("msg.muchTooDense");
	}
	
	@Override
	public void onTick(EntityLivingBase player) {
		if(player.world.getTotalWorldTime() % 20  == 0 && !isImmune(player)) {
			if(!isImmune(player)) {
				player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 40, 3));
				player.addPotionEffect(new PotionEffect(Potion.getPotionById(4), 40, 3));
				player.attackEntityFrom(AtmosphereHandler.oxygenToxicityDamage, 1);
				if(player instanceof EntityPlayer)
					PacketHandler.sendToPlayer(new PacketOxygenState(), (EntityPlayer)player);
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

		return (player instanceof EntityPlayer && ((((EntityPlayer)player).capabilities.isCreativeMode) || ((EntityPlayer)player).isSpectator()))
				|| player.getRidingEntity() instanceof EntityRocketBase || player.getRidingEntity() instanceof EntityElevatorCapsule ||
				protectsFrom(helm) && protectsFrom(leg) && protectsFrom(feet) && protectsFrom(chest);
	}
	
	public boolean protectsFrom(ItemStack stack) {
		return (ItemAirUtils.INSTANCE.isStackValidAirContainer(stack) && new ItemAirUtils.ItemAirWrapper(stack).protectsFromSubstance(this, stack, true) ) || (stack != null && stack.hasCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null) &&
				stack.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null).protectsFromSubstance(this, stack, true));
	}
}
