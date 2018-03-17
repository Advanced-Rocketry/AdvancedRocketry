package zmaster587.advancedRocketry.tile.oxygen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.armor.ItemSpaceChest;
import zmaster587.advancedRocketry.util.ItemAirUtils;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleLiquidIndicator;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.IconResource;

public class TileOxygenCharger extends TileInventoriedRFConsumerTank implements IModularInventory {
	public TileOxygenCharger() {
		super(0, 2, 16000);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] {};
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {

		if(canFill(resource.getFluid()))
			return super.fill(resource, doFill);
		return 0;
	}

	@Override
	public boolean canFill(Fluid fluid) {
		return FluidUtils.areFluidsSameType(fluid, AdvancedRocketryFluids.fluidOxygen) || FluidUtils.areFluidsSameType(fluid, AdvancedRocketryFluids.fluidHydrogen);
	}	

	@Override
	public int getPowerPerOperation() {
		return 0;
	}

	@Override
	public boolean canPerformFunction() {
		if(!worldObj.isRemote) {
			for( Object player : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos, pos.add(1,2,1)))) {
				ItemStack stack = ((EntityPlayer)player).getItemStackFromSlot(EntityEquipmentSlot.CHEST);
				if(stack != null) {
					IFillableArmor fillable = null;

					if(stack.getItem() instanceof IFillableArmor)
						fillable = (IFillableArmor)stack.getItem();
					else if(ItemAirUtils.INSTANCE.isStackValidAirContainer(stack))
						fillable = new ItemAirUtils.ItemAirWrapper(stack);
					
					//Check for O2 fill
					if(fillable != null ) {
						FluidStack fluidStack = this.drain(100, false);

						if(fillable.getAirRemaining(stack) < fillable.getMaxAir(stack) &&
								fluidStack != null && FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.fluidOxygen) && fluidStack.amount > 0)  {
							this.drain(1, true);

							this.markDirty();
							worldObj.markChunkDirty(getPos(), this);
							fillable.increment(stack, 100);
							return true;
						}
					}
				}

				//Check for H2 fill (possibly merge with O2 fill
				//Fix conflict with O2 fill
				if(this.tank.getFluid() != null && !FluidUtils.areFluidsSameType(this.tank.getFluid().getFluid(), AdvancedRocketryFluids.fluidOxygen) && stack != null && stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);

					FluidStack fluidStack = this.drain(100, false);
					if(fluidStack != null) {
						for(int i = 0; i < inv.getSizeInventory(); i++) {

							if(!((IModularArmor)stack.getItem()).canBeExternallyModified(stack, i))
								continue;

							ItemStack module = inv.getStackInSlot(i);
							if(module != null && module.getItem() instanceof IFluidContainerItem) {
								int amtFilled = ((IFluidContainerItem)module.getItem()).fill(module, fluidStack, true);
								if(amtFilled == 100) {
									this.drain(100, true);

									((IModularArmor)stack.getItem()).saveModuleInventory(stack, inv);

									return true;
								}
							}
						}
					}
				}

				return false;
			}
		}
		return false;
	}

	@Override
	public void performFunction() {

	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		ArrayList<ModuleBase> modules = new ArrayList<ModuleBase>();

		modules.add(new ModuleSlotArray(50, 21, this, 0, 1));
		modules.add(new ModuleSlotArray(50, 57, this, 1, 2));
		if(worldObj.isRemote)
			modules.add(new ModuleImage(49, 38, new IconResource(194, 0, 18, 18, CommonResources.genericBackground)));

		//modules.add(new ModulePower(18, 20, this));
		modules.add(new ModuleLiquidIndicator(32, 20, this));

		//modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));
		//TODO add itemStack slots for liqiuid
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "tile.oxygenCharger.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		while(useBucket(0, getStackInSlot(0)));
	}

	//Yes i was lazy
	//TODO: make better
	private boolean useBucket( int slot, ItemStack stack) {


		/*if(stack != null && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {

			ItemStack stackCpy = stack.copy();
			IFluidHandler fluidCapability = stackCpy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

			boolean hasFluid = false;
			for(IFluidTankProperties props : fluidCapability.getTankProperties()) {
				if(props.getContents() != null) {
					hasFluid = true;
					break;
				}
			}
			if(hasFluid) {
				if(slot == 0 && tank.getCapacity() != tank.getFluidAmount()) {
					//Do a real drain on the item copy to get the resultant item
					if(tank.getFluid() != null)
						fluidCapability.drain(new FluidStack(tank.getFluid().getFluid(), tank.getCapacity() - tank.getFluidAmount()) , true);
					else
						fluidCapability.drain(tank.getCapacity() - tank.getFluidAmount(), true);

					if(getStackInSlot(1) == null)
						inventory.setInventorySlotContents(1, stackCpy);
					else if(ItemStack.areItemStackTagsEqual(getStackInSlot(1), stackCpy) && getStackInSlot(1).getItem().equals(stackCpy.getItem()) && getStackInSlot(1).getItemDamage() == stackCpy.getItemDamage() && stackCpy.getItem().getItemStackLimit(stackCpy) < getStackInSlot(1).stackSize) {
						getStackInSlot(1).stackSize++;
					}
					else
						return false;

					fluidCapability = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
					//Don't drain the real thing by mistake
					if(tank.getFluid() != null)
						tank.fill(fluidCapability.drain(new FluidStack(tank.getFluid().getFluid(), tank.getCapacity() - tank.getFluidAmount()), false), true);
					else
						tank.fill(fluidCapability.drain(tank.getCapacity() - tank.getFluidAmount(), false), true);
					decrStackSize(0, 1);
					return true;
				}
			}
			else {
				if(slot == 0 && tank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
					//Do a real drain on the item copy to get the resultant item
					fluidCapability.fill(tank.getFluid(), true);

					if(getStackInSlot(1) == null)
						inventory.setInventorySlotContents(1, stackCpy);
					else if(ItemStack.areItemStackTagsEqual(getStackInSlot(1), stackCpy) && getStackInSlot(1).getItem().equals(stackCpy.getItem()) && getStackInSlot(1).getItemDamage() == stackCpy.getItemDamage() && stackCpy.getItem().getItemStackLimit(stackCpy) < getStackInSlot(1).stackSize) {
						getStackInSlot(1).stackSize++;
					}
					else
						return false;

					fluidCapability = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
					//Don't drain the real thing by mistake
					tank.drain(fluidCapability.fill(tank.getFluid(), false), true);
					decrStackSize(0, 1);
					return true;
				}
			}
		}*/

		if(FluidContainerRegistry.isFilledContainer(stack)) {
			if(slot == 0 && tank.getFluidAmount() + FluidContainerRegistry.getContainerCapacity(stack) <= tank.getCapacity()) {
				ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(stack);

				if(emptyContainer != null && getStackInSlot(1) == null || (emptyContainer.isItemEqual(getStackInSlot(1)) && getStackInSlot(1).stackSize < getStackInSlot(1).getMaxStackSize())) {
					tank.fill(FluidContainerRegistry.getFluidForFilledItem(stack), true);

					if(getStackInSlot(1) == null)
						inventory.setInventorySlotContents(1, emptyContainer);
					else
						getStackInSlot(1).stackSize++;
					decrStackSize(0, 1);
					return true;
				}
			}
		}
		else if(FluidContainerRegistry.isContainer(stack)) {
			if(slot == 0 && tank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
				ItemStack fullContainer = FluidContainerRegistry.fillFluidContainer(tank.drain(FluidContainerRegistry.BUCKET_VOLUME, false), stack);


				if(fullContainer != null && (getStackInSlot(1) == null || (fullContainer.isItemEqual(getStackInSlot(1)) && getStackInSlot(1).stackSize < getStackInSlot(1).getMaxStackSize())) ) {
					tank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);

					if(getStackInSlot(1) == null)
						inventory.setInventorySlotContents(1, fullContainer);
					else
						getStackInSlot(1).stackSize++;
					decrStackSize(0, 1);
					return true;
				}
			}
		}
		else if(stack != null && stack.getItem() instanceof IFluidContainerItem) {
			IFluidContainerItem fluidItem = ((IFluidContainerItem)stack.getItem());
			FluidStack fluidStack;
			stack = stack.copy();
			stack.stackSize = 1;

			//Drain the tank into the item
			if(fluidItem.getFluid(stack) == null && tank.getFluid() != null) {
				int amt = fluidItem.fill(stack, tank.getFluid(), true);


				//If the container is full move it down and try again for a new one
				if(amt != 0 && fluidItem.getCapacity(stack) == fluidItem.getFluid(stack).amount) {


					if(getStackInSlot(1) == null) {
						inventory.setInventorySlotContents(1, stack);
					}
					else if(ItemStack.areItemStackTagsEqual(getStackInSlot(1), stack) && getStackInSlot(1).getItem().equals(stack.getItem()) && getStackInSlot(1).getItemDamage() == stack.getItemDamage() && stack.getItem().getItemStackLimit(stack) < getStackInSlot(1).stackSize) {
						getStackInSlot(1).stackSize++;

					}
					else
						return false;
					tank.drain(amt, true);
					decrStackSize(0, 1);

					return true;
				}

			}
			else {
				fluidStack = fluidItem.drain(stack, tank.getCapacity() - tank.getFluidAmount(), false);

				int amountDrained = tank.fill(fluidStack, true);
				fluidItem.drain(stack, amountDrained, true);
				if (fluidItem.getFluid(stack) == null || fluidItem.getFluid(stack).amount == 0) {
					if(getStackInSlot(1) == null) {
						inventory.setInventorySlotContents(1, stack);
					}
					else if(ItemStack.areItemStackTagsEqual(getStackInSlot(1), stack) && getStackInSlot(1).getItem().equals(stack.getItem()) && getStackInSlot(1).getItemDamage() == stack.getItemDamage() && stack.getItem().getItemStackLimit(stack) < getStackInSlot(1).stackSize) {
						getStackInSlot(1).stackSize++;

					}
					else
						return false;

					decrStackSize(0, 1);

					return true;
				}
			}
		}
		return false;
	}
}
