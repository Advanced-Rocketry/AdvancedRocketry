package zmaster587.advancedRocketry.util;

import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidUtils {
	
	public static boolean containsFluid(ItemStack stack) {
		return stack != null && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
	}
	
	public static boolean containsFluid(ItemStack stack, Fluid fluid) {
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
			FluidStack fluidStack = fluidItem.getTankProperties()[0].getContents();
			if(fluidStack != null && fluidStack.getFluid() == fluid)
				return true;
		}
		
		return false;
	}
	
	public static int getFluidItemCapacity(ItemStack stack) {
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
				return  fluidItem.getTankProperties()[0].getCapacity();
		}
		return 0;
	}
	
	public static FluidStack getFluidForItem(ItemStack item) {
		if(!containsFluid(item))
			return null;
		IFluidHandlerItem fluidItem = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
		return fluidItem.getTankProperties()[0].getContents();
	}
	
	public static boolean attemptDrainContainerIInv(IInventory inv, IFluidTank tank, ItemStack stack, int inputSlot, int outputSlot) {
		
		if(containsFluid(stack)) {
			IFluidHandlerItem fluidItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP);
			FluidStack fluidStack;
			stack = stack.copy();
			stack.setCount(1);
			
			FluidStack itemFluidStack = getFluidForItem(stack);

			//Drain the tank into the item
			if(itemFluidStack == null && tank.getFluid() != null) {
				int amt = fluidItem.fill(tank.getFluid(), true);


				//If the container is full move it down and try again for a new one
				if(amt != 0 && getFluidItemCapacity(stack) == getFluidForItem(stack).amount) {


					if(inv.getStackInSlot(outputSlot) == null) {
						inv.setInventorySlotContents(outputSlot, stack);
					}
					else if(ItemStack.areItemStackTagsEqual(inv.getStackInSlot(outputSlot), stack) && inv.getStackInSlot(outputSlot).getItem().equals(stack.getItem()) && inv.getStackInSlot(outputSlot).getItemDamage() == stack.getItemDamage() && stack.getItem().getItemStackLimit(stack) < inv.getStackInSlot(outputSlot).getCount()) {
						inv.getStackInSlot(outputSlot).setCount(inv.getStackInSlot(outputSlot).getCount() + 1);

					}
					else
						return false;
					tank.drain(amt, true);
					inv.decrStackSize(inputSlot, 1);

					return true;
				}

			}
			else {
				fluidStack = fluidItem.drain(tank.getCapacity() - tank.getFluidAmount(), false);

				int amountDrained = tank.fill(fluidStack, true);
				fluidItem.drain(amountDrained, true);
				if (getFluidForItem(stack) == null || getFluidForItem(stack).amount == 0) {
					if(inv.getStackInSlot(outputSlot) == null) {
						inv.setInventorySlotContents(outputSlot, stack);
					}
					else if(ItemStack.areItemStackTagsEqual(inv.getStackInSlot(outputSlot), stack) && inv.getStackInSlot(outputSlot).getItem().equals(stack.getItem()) && inv.getStackInSlot(outputSlot).getItemDamage() == stack.getItemDamage() && stack.getItem().getItemStackLimit(stack) < inv.getStackInSlot(outputSlot).getCount()) {
						inv.getStackInSlot(outputSlot).setCount( inv.getStackInSlot(outputSlot).getCount() + 1 );

					}
					else
						return false;

					inv.decrStackSize(inputSlot, 1);

					return true;
				}
			}
		}
		return false;
	}
}
