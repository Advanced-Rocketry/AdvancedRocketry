package zmaster587.advancedRocketry.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry.FuelType;

import javax.annotation.Nonnull;

public class RocketFluidTank extends FluidTank {

	private int baseFuelRate;
	private int fuelRate;
	private FuelType type;

	public RocketFluidTank(int capacity, int baseFuelRate, @Nonnull FuelType type) {
		super(capacity);
		this.baseFuelRate = baseFuelRate;
		this.type = type;
	}

	//Actual _proper_ NBT save implementation that doesn't lose capacity... why this didn't deal with dynamic tank sizes I dunno
	public RocketFluidTank readFromNBT(CompoundNBT nbt) {
		FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
		setFluid(fluid);
		capacity = nbt.getInt("capacity");
		baseFuelRate = nbt.getInt("basefuelrate");
		fuelRate = nbt.getInt("fuelrate");
		type = FuelType.valueOf(nbt.getString("type"));
		return this;
	}

	public CompoundNBT writeToNBT(CompoundNBT nbt) {
		fluid.writeToNBT(nbt);
        nbt.putInt("capacity", capacity);
		nbt.putInt("basefuelrate", baseFuelRate);
		nbt.putInt("fuelrate", fuelRate);
		nbt.putString("type", type.name());
		return nbt;
	}

	public int getBaseFuelRate() { return baseFuelRate; }
	public void setFuelRate(float multiplier) { fuelRate = (int)(baseFuelRate * multiplier); }
	public int getFuelRate() { return fuelRate; }

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return isFluidValid(stack);
	}

	public boolean isFluidValid(FluidStack stack) {
		return validator.test(stack) && FuelRegistry.instance.isFuel(type, stack.getFluid());
	}

}
