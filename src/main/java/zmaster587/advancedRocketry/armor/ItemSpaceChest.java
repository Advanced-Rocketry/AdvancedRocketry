package zmaster587.advancedRocketry.armor;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.util.EmbeddedInventory;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.IconResource;

public class ItemSpaceChest extends ItemSpaceArmor implements IFillableArmor {

	public ItemSpaceChest(ArmorMaterial material, EntityEquipmentSlot component, int numModules) {
		super(material, component, numModules);
	}

	@Override
	public boolean isItemValidForSlot(ItemStack stack, int slot) {
		if(slot >= 2)
			return true;

		return stack != null && stack.getItem() instanceof IFluidContainerItem && 
				(((IFluidContainerItem)stack.getItem()).getFluid(stack) == null || ((IFluidContainerItem)stack.getItem()).getFluid(stack).getFluid() == AdvancedRocketryFluids.fluidOxygen);
	}

	@Override
	public boolean canBeExternallyModified(ItemStack armor, int slot) {
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
	public int getAirRemaining(ItemStack stack) {

		List<ItemStack> list = getComponents(stack);

		int airRemaining = 0;

		for(ItemStack component : list) {
			if(component.getItem() instanceof IFluidContainerItem) {
				IFluidContainerItem fluidItem = (IFluidContainerItem)component.getItem();
				FluidStack fluidStack = fluidItem.getFluid(component);
				if(fluidStack != null && FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.fluidOxygen))
					airRemaining += fluidStack.amount;
			}
		}

		return airRemaining;

		/*if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger("air");
		}
		else {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("air", 0);
			stack.setTagCompound(nbt);
			return getMaxAir();
		}*/
	}

	/**
	 * Sets the amount of air remaining in the suit (WARNING: DOES NOT BOUNDS CHECK!)
	 * @param stack the stack to operate on
	 * @param amt amount of air to set the suit to
	 */
	@Override
	public void setAirRemaining(ItemStack stack, int amt) {
		/*NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else {
			nbt = new NBTTagCompound();
		}
		nbt.setInteger("air", amt);
		stack.setTagCompound(nbt);*/
	}

	/**
	 * Decrements air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air extracted from the suit
	 */
	@Override
	public int decrementAir(ItemStack stack, int amt) {

		if(stack.hasTagCompound()) {
			EmbeddedInventory inv = new EmbeddedInventory(getNumSlots(stack));
			inv.readFromNBT(stack.getTagCompound());
			List<ItemStack> list = new LinkedList<ItemStack>();

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				if(inv.getStackInSlot(i) != null)
					list.add(inv.getStackInSlot(i));
			}
			int amtDrained = amt;
			for(ItemStack component : list) {
				if(component.getItem() instanceof IFluidContainerItem) {
					IFluidContainerItem fluidItem = (IFluidContainerItem)component.getItem();
					FluidStack fluidStack = fluidItem.getFluid(component);

					FluidStack fluidDrained = null;

					if(fluidStack != null && FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.fluidOxygen))
						fluidDrained = fluidItem.drain(component, amtDrained, true);

					if(fluidDrained != null)
						amtDrained -= fluidDrained.amount;

					if(amtDrained == 0)
						break;
				}
			}
			saveEmbeddedInventory(stack, inv);
			return amt - amtDrained;

		}
		return 0;

		/*NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else {
			nbt = new NBTTagCompound();
		}

		int prevAmt = nbt.getInteger("air");
		int newAmt = Math.max(prevAmt - amt,0);
		nbt.setInteger("air", newAmt);
		stack.setTagCompound(nbt);

		return prevAmt - newAmt;*/
	}

	/**
	 * Increments air in the suit by amt
	 * @param stack the item stack to operate on
	 * @param amt amount of air by which to decrement
	 * @return The amount of air inserted into the suit
	 */
	@Override
	public int increment(ItemStack stack, int amt) {

		if(stack.hasTagCompound()) {
			EmbeddedInventory inv = new EmbeddedInventory(getNumSlots(stack));
			inv.readFromNBT(stack.getTagCompound());
			List<ItemStack> list = new LinkedList<ItemStack>();

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				if(inv.getStackInSlot(i) != null && 
						(i < 2 || 
								(inv.getStackInSlot(i).getItem() instanceof IFluidContainerItem && 
										((IFluidContainerItem)inv.getStackInSlot(i).getItem()).getFluid(inv.getStackInSlot(i)) != null && 
												FluidUtils.areFluidsSameType(((IFluidContainerItem)inv.getStackInSlot(i).getItem()).getFluid(inv.getStackInSlot(i)).getFluid(), AdvancedRocketryFluids.fluidOxygen))))
					
					list.add(inv.getStackInSlot(i));
			}


			int amtDrained = amt;
			for(ItemStack component : list) {
				if(component.getItem() instanceof IFluidContainerItem) {
					IFluidContainerItem fluidItem = (IFluidContainerItem)component.getItem();
					FluidStack fluidStack = fluidItem.getFluid(component);

					if(fluidStack == null || FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.fluidOxygen))
						amtDrained -= fluidItem.fill(component, new FluidStack(AdvancedRocketryFluids.fluidOxygen, amtDrained), true);

					if(amtDrained == 0)
						break;
				}
			}

			saveEmbeddedInventory(stack, inv);

			return amt - amtDrained;
		}

		return 0;

		/*NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else {
			nbt = new NBTTagCompound();
		}

		int prevAmt = nbt.getInteger("air");
		int newAmt = Math.min(prevAmt + amt, getMaxAir());
		nbt.setInteger("air", newAmt);
		stack.setTagCompound(nbt);

		return newAmt - prevAmt;*/
	}

	/**
	 * @return the maximum amount of air allowed in this suit
	 */
	@Override
	public int getMaxAir(ItemStack stack) {

		if(stack.hasTagCompound()) {
			EmbeddedInventory inv = new EmbeddedInventory(getNumSlots(stack));
			inv.readFromNBT(stack.getTagCompound());
			List<ItemStack> list = new LinkedList<ItemStack>();

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				//Check the stack if its in an O2 slot or if it's a container with O2 in it
				if(inv.getStackInSlot(i) != null && 
						(i < 2 || 
								(inv.getStackInSlot(i).getItem() instanceof IFluidContainerItem && 
										((IFluidContainerItem)inv.getStackInSlot(i).getItem()).getFluid(inv.getStackInSlot(i)) != null && 
										FluidUtils.areFluidsSameType(((IFluidContainerItem)inv.getStackInSlot(i).getItem()).getFluid(inv.getStackInSlot(i)).getFluid(), AdvancedRocketryFluids.fluidOxygen))))
					list.add(inv.getStackInSlot(i));
			}
			
			int maxAir = 0;
			for(ItemStack component : list) {
				if(component.getItem() instanceof IFluidContainerItem) {
					IFluidContainerItem fluidItem = (IFluidContainerItem)component.getItem();
					FluidStack fluidStack = fluidItem.getFluid(component);

					if(fluidStack == null || fluidStack.getFluid() == null || FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.fluidOxygen))
						maxAir += fluidItem.getCapacity(component);
				}
			}


			return maxAir;
		}

		return 0;

		//return Configuration.spaceSuitOxygenTime*1200; //30 minutes;
	}

}
