package zmaster587.advancedRocketry.item;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import zmaster587.advancedRocketry.util.FluidHandlerBucketClone;

import javax.annotation.Nullable;

public class ItemARBucket extends Item {

	FluidStack fluid;
    public ItemARBucket(Properties props, Fluid fluid) {
    	super(props);
		this.fluid = new FluidStack(fluid, 1000);
	}

	@Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundNBT nbt) {
		FluidHandlerItemStackSimple container = new FluidHandlerItemStackSimple(stack, 1000);
		container.fill(fluid, FluidAction.EXECUTE);
        return new FluidHandlerBucketClone(stack, 1000, new ItemStack(Items.BUCKET));
    }
}
