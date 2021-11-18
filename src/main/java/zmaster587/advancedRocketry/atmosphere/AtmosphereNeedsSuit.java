package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.armor.IProtectiveArmor;
import zmaster587.advancedRocketry.api.capability.CapabilitySpaceArmor;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.util.ItemAirUtils;

import javax.annotation.Nonnull;

public class AtmosphereNeedsSuit extends AtmosphereType {
	
	public AtmosphereNeedsSuit(boolean canTick, boolean isBreathable, boolean allowsCombustion, String name) {
		super(canTick, isBreathable, allowsCombustion, name);
	}
	
	// True if only a helmet is needed
	protected boolean onlyNeedsMask()
	{
		return allowsCombustion();
	}
	
	@Override
	public boolean isImmune(LivingEntity player) {

		
		//Checks if player is wearing spacesuit or anything that extends ItemSpaceArmor

		ItemStack feet = player.getItemStackFromSlot(EquipmentSlotType.FEET);
		ItemStack leg = player.getItemStackFromSlot(EquipmentSlotType.LEGS /*so hot you can fry an egg*/ );
		ItemStack chest = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
		ItemStack helm = player.getItemStackFromSlot(EquipmentSlotType.HEAD);

		// Note: protectsFrom(chest) is intentionally the last thing to check here.  This is because java will bail on the check early if others fail
		// this will prevent the O2 level in the chest from being needlessly decremented
		return (player instanceof PlayerEntity && ((((PlayerEntity)player).isCreative()) || player.isSpectator()))
				|| player.getRidingEntity() instanceof EntityRocketBase || player.getRidingEntity() instanceof EntityElevatorCapsule ||
				( ( (!onlyNeedsMask() && protectsFrom(leg) && protectsFrom(feet) ) || onlyNeedsMask() ) && protectsFrom(helm) && protectsFrom(chest) );
		}

	protected boolean protectsFrom(@Nonnull ItemStack stack) {
		boolean armorProtec = false;
		if(!stack.isEmpty())
		{
			LazyOptional<IProtectiveArmor> cap = stack.getCapability(CapabilitySpaceArmor.PROTECTIVEARMOR, null);
			armorProtec = cap.isPresent() && cap.orElse(null).protectsFromSubstance(this, stack, true);
		}
		
		return (ItemAirUtils.INSTANCE.isStackValidAirContainer(stack) && new ItemAirUtils.ItemAirWrapper(stack).protectsFromSubstance(this, stack, true) ) || armorProtec;
	}

}
