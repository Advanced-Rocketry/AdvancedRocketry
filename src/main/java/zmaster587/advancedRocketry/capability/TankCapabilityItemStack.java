package zmaster587.advancedRocketry.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;

public class TankCapabilityItemStack implements ICapabilityProvider {
	
	private ItemStack stack;
	private FluidHandlerItemStack fluidHandler;
	
	
	public TankCapabilityItemStack(@Nonnull ItemStack stack, int capacity) {
		fluidHandler = new FluidHandlerItemStack(stack, capacity);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
			return LazyOptional.of(() -> fluidHandler).cast();
		}
		return LazyOptional.empty();
	}
}
