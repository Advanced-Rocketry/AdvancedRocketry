package zmaster587.advancedRocketry.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.IconResource;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class ItemSpaceChest extends ItemSpaceArmor implements IFillableArmor {

	public ItemSpaceChest(Item.Properties props, ArmorMaterial material, EquipmentSlotType component, int numModules) {
		super(props, material, component, numModules);
	}

	@Override
	public boolean isItemValidForSlot(@Nonnull ItemStack stack, int slot) {
		if(slot >= 2)
			return true;

		FluidStack fstack;
		
		LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
		
		if(stack.isEmpty() || !cap.isPresent())
			return false;
		
		fstack = cap.orElse(null).getFluidInTank(0);
		
		return (fstack.isEmpty() || FluidUtils.areFluidsSameType(fstack.getFluid(), AdvancedRocketryFluids.oxygenFlowing.get()));
	}

	@Override
	public boolean canBeExternallyModified(@Nonnull ItemStack armor, int slot) {
		return slot >= 2;
	}

	@Override
	public IconResource getResourceForSlot(int slot) {
		if(slot < 2)
			return TextureResources.slotO2;
		return null;
	}

	/**
	 * gets the amount of air remaining in the suit.
	 * @param stack stack from which to get an amount of air
	 * @return the amount of air in the stack
	 */
	@Override
	public int getAirRemaining(@Nonnull ItemStack stack) {

		List<ItemStack> list = getComponents(stack);

		int airRemaining = 0;

		for(ItemStack component : list) {
			if(FluidUtils.containsFluid(component, AdvancedRocketryFluids.oxygenStill.get())) {
				airRemaining += FluidUtils.getFluidForItem(component).getAmount();
			}
		}

		return airRemaining;

		/*if(stack.hasTag()) {
			return stack.getTag().getInt("air");
		}
		else {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("air", 0);
			stack.setTag(nbt);
			return getMaxAir();
		}*/
	}

	/**
	 * Sets the amount of air remaining in the suit (WARNING: DOES NOT BOUNDS CHECK!)
	 * @param stack the stack to operate on
	 * @param amt amount of air to set the suit to
	 */
	@Override
	public void setAirRemaining(@Nonnull ItemStack stack, int amt) {
		/*CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
		}
		else {
			nbt = new CompoundNBT();
		}
		nbt.putInt("air", amt);
		stack.setTag(nbt);*/
	}

	/**
	 * Decrements air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air extracted from the suit
	 */
	@Override
	public int decrementAir(@Nonnull ItemStack stack, int amt) {

		if(stack.hasTag()) {
			EmbeddedInventory inv = new EmbeddedInventory(getNumSlots(stack));
			inv.readFromNBT(stack.getTag());
			List<ItemStack> list = new LinkedList<>();

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				if(!inv.getStackInSlot(i).isEmpty())
					list.add(inv.getStackInSlot(i));
			}
			int amtDrained = amt;
			for(ItemStack component : list) {
				
				LazyOptional<IFluidHandlerItem> cap = component.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
				
				if(cap.isPresent()) {
					IFluidHandlerItem fluidItem = cap.orElse(null);
					FluidStack fluidStack = FluidUtils.getFluidForItem(component);

					FluidStack fluidDrained = null;

					if(fluidStack != null && FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.oxygenStill.get()))
						fluidDrained = fluidItem.drain(amtDrained, FluidAction.EXECUTE);

					if(fluidDrained != null)
						amtDrained -= fluidDrained.getAmount();

					if(amtDrained == 0)
						break;
				}
			}
			saveEmbeddedInventory(stack, inv);
			return amt - amtDrained;

		}
		return 0;

		/*CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
		}
		else {
			nbt = new CompoundNBT();
		}

		int prevAmt = nbt.getInt("air");
		int newAmt = Math.max(prevAmt - amt,0);
		nbt.putInt("air", newAmt);
		stack.setTag(nbt);

		return prevAmt - newAmt;*/
	}

	/**
	 * Increments air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air inserted into the suit
	 */
	@Override
	public int increment(@Nonnull ItemStack stack, int amt) {

		if(stack.hasTag()) {
			EmbeddedInventory inv = new EmbeddedInventory(getNumSlots(stack));
			inv.readFromNBT(stack.getTag());
			List<ItemStack> list = new LinkedList<>();

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				if(!inv.getStackInSlot(i).isEmpty()) {
					
					LazyOptional<IFluidHandlerItem> cap = inv.getStackInSlot(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
					
					if( i < 2) {
						final int count = i;
						cap.ifPresent(value -> list.add(inv.getStackInSlot(count)));
					}
					else if(FluidUtils.containsFluid(inv.getStackInSlot(i))) {
						
						FluidStack fstack = FluidUtils.getFluidForItem(inv.getStackInSlot(i));
						if(fstack != null && FluidUtils.areFluidsSameType(fstack.getFluid(), AdvancedRocketryFluids.oxygenStill.get()))
							list.add(inv.getStackInSlot(i));
					}
					
				}
			}


			int amtDrained = amt;
			//At this point the list contains ONLY capable items
			for(ItemStack component : list) {
				 	
					IFluidHandlerItem fHandler = component.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);
					FluidStack fluidStack = fHandler.getFluidInTank(0);

					if(fluidStack.isEmpty() || FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.oxygenStill.get()))
						amtDrained -= fHandler.fill(new FluidStack(AdvancedRocketryFluids.oxygenStill.get(), amtDrained), FluidAction.EXECUTE);

					if(amtDrained == 0)
						break;
				
			}

			saveEmbeddedInventory(stack, inv);

			return amt - amtDrained;
		}

		return 0;

		/*CompoundNBT nbt;
		if(stack.hasTag()) {
			nbt = stack.getTag();
		}
		else {
			nbt = new CompoundNBT();
		}

		int prevAmt = nbt.getInt("air");
		int newAmt = Math.min(prevAmt + amt, getMaxAir());
		nbt.putInt("air", newAmt);
		stack.setTag(nbt);

		return newAmt - prevAmt;*/
	}

	/**
	 * @return the maximum amount of air allowed in this suit
	 */
	@Override
	public int getMaxAir(@Nonnull ItemStack stack) {

		if(stack.hasTag()) {
			EmbeddedInventory inv = new EmbeddedInventory(getNumSlots(stack));
			inv.readFromNBT(stack.getTag());
			List<ItemStack> list = new LinkedList<>();

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				if(!inv.getStackInSlot(i).isEmpty()) {
					
					LazyOptional<IFluidHandlerItem> cap = inv.getStackInSlot(i).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
					
					if( i < 2) {
						final int count = i;
						cap.ifPresent(value -> list.add(inv.getStackInSlot(count)));
					}
					else if(cap.isPresent()) {
						
						FluidStack fstack = FluidUtils.getFluidForItem(inv.getStackInSlot(i));
						if(fstack != null && FluidUtils.areFluidsSameType(fstack.getFluid(), AdvancedRocketryFluids.oxygenStill.get()))
							list.add(inv.getStackInSlot(i));
					}
					
				}
			}
			
			int maxAir = 0;
			for(ItemStack component : list) {
					LazyOptional<IFluidHandlerItem> cap = component.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
					
					IFluidHandlerItem fHandler = cap.orElse(null);
					FluidStack fluidStack = fHandler.getFluidInTank(0);

					if(fluidStack.isEmpty() || FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.oxygenStill.get()))
						maxAir += fHandler.getTankCapacity(0);
			}


			return maxAir;
		}

		return 0;

		//return Configuration.spaceSuitOxygenTime*1200; //30 minutes;
	}
	
	@Override
	public boolean protectsFromSubstance(@Nonnull IAtmosphere atmosphere, @Nonnull ItemStack stack, boolean commitProtection) {
		
		if(!super.protectsFromSubstance(atmosphere, stack, commitProtection))
			return false;
		
		// Assume for now that the space suit has a built in O2 extractor and can magically handle pressure
		if(atmosphere.allowsCombustion())
			return true;
		
		// If the atmosphere allows for combustion, it probably has O2, TODO: atmosphere with non O2 oxidizers
		boolean commitAndDecrement = commitProtection && ((IFillableArmor)AdvancedRocketryItems.itemSpaceSuitChestpiece).decrementAir(stack, 1) > 0;
		boolean noncommitAndHasAir = !commitProtection && ((IFillableArmor)AdvancedRocketryItems.itemSpaceSuitChestpiece).getAirRemaining(stack) > 0;
		return noncommitAndHasAir || commitAndDecrement;
	}
}
