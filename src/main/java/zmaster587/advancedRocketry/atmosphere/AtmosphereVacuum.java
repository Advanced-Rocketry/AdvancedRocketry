package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.network.PacketOxygenState;
import zmaster587.advancedRocketry.util.ItemAirUtils;
import zmaster587.libVulpes.network.PacketHandler;

/**
 * Atmosphere type for vaccum (No air)
 * @author Zmaster
 */
public class AtmosphereVacuum extends AtmosphereType {


	public static int damageValue;

	public AtmosphereVacuum() {
		super(true, false, "vacuum");
	}

	@Override
	public void onTick(EntityLivingBase player) {
		if(player.world.getTotalWorldTime() % 10  == 0 && !isImmune(player)) {
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

		ItemStack feet = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		ItemStack leg = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS /*so hot you can fry an egg*/ );
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		return (player instanceof EntityPlayer && ((((EntityPlayer)player).capabilities.isCreativeMode) || ((EntityPlayer)player).isSpectator()))
				|| player.getRidingEntity() instanceof EntityRocketBase || player.getRidingEntity() instanceof EntityElevatorCapsule ||
				protectsFrom(helm) && protectsFrom(leg) && protectsFrom(feet) && protectsFrom(chest);
		}

	public boolean protectsFrom(ItemStack stack) {
		return (ItemAirUtils.INSTANCE.isStackValidAirContainer(stack) && new ItemAirUtils.ItemAirWrapper(stack).protectsFromSubstance(this, stack, true) ) || (!stack.isEmpty() && stack.hasCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null) &&
				stack.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null).protectsFromSubstance(this, stack, true));
	}
}
