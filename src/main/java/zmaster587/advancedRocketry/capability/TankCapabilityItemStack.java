package zmaster587.advancedRocketry.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class TankCapabilityItemStack implements ICapabilityProvider {
	
	ItemStack stack;
	FluidHandlerItemStack fluidHandler;
	
	
	public TankCapabilityItemStack(ItemStack stack, int capacity) {
		fluidHandler = new FluidHandlerItemStack(stack, capacity);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return (T) fluidHandler;
	}
}
