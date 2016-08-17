package zmaster587.advancedRocketry.item.components;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.armor.IArmorComponent;
import zmaster587.advancedRocketry.util.InputSyncHandler;

public class ItemJetpack extends Item implements IArmorComponent {

	@Override
	public void onTick(World world, EntityPlayer player,
			ItemStack armorStack, IInventory inv, ItemStack componentStack) {

		if(InputSyncHandler.isSpaceDown(player)) {
			boolean hasFuel = false;
			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack fuelTank = inv.getStackInSlot(i);

				if(fuelTank != null && fuelTank.getItem() instanceof IFluidContainerItem) {
					FluidStack fluid = ((IFluidContainerItem)fuelTank.getItem()).drain(fuelTank, 1, false);
					if(fluid != null && fluid.getFluidID() == FluidRegistry.getFluidID(AdvancedRocketryFluids.fluidHydrogen)) {
						((IFluidContainerItem)fuelTank.getItem()).drain(fuelTank, 1, true);
						hasFuel = true;
						break;
					}
				}

			}

			if(hasFuel) {
				player.addVelocity(0, 0.1, 0);
				if(player.motionY > -0.3) {
					player.fallDistance = 0;
				}
			}
		}
	}

	@Override
	public boolean onComponentAdded(World world, ItemStack armorStack) {
		return true;
	}

	@Override
	public void onComponentRemoved(World world, ItemStack armorStack) {

	}

	@Override
	public void onArmorDamaged(EntityLivingBase entity, ItemStack armorStack,
			ItemStack componentStack, DamageSource source, int damage) {

	}

}
