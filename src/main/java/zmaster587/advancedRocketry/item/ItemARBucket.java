package zmaster587.advancedRocketry.item;

import javax.annotation.Nullable;

import zmaster587.advancedRocketry.util.FluidHandlerBucketClone;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

public class ItemARBucket extends Item {

	FluidStack fluid;
    public ItemARBucket(Fluid fluid) {
    	
		this.fluid = new FluidStack(fluid, 1000);
	}

	@Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.NBTTagCompound nbt) {
		FluidHandlerItemStackSimple container = new FluidHandlerItemStackSimple(stack, 1000);
		container.fill(fluid, true);
        return new FluidHandlerBucketClone(stack, 1000, new ItemStack(Items.BUCKET));
    }
}
