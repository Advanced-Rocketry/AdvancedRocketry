package zmaster587.advancedRocketry.tile.oxygen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.armor.ItemSpaceChest;
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
	public int[] getAccessibleSlotsFromSide(int slots) {
		return new int[] {};
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {

		if(resource.getFluid().getUnlocalizedName().contains("oxygen") ||
				resource.getFluidID() == AdvancedRocketryFluids.fluidHydrogen.getID())
			return super.fill(from, resource, doFill);
		return 0;
	}
	
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid.getUnlocalizedName().contains("oxygen") || fluid.getID() == FluidRegistry.getFluidID(AdvancedRocketryFluids.fluidHydrogen);
	}	

	@Override
	public int getPowerPerOperation() {
		return 0;
	}

	@Override
	public boolean canPerformFunction() {
		if(!worldObj.isRemote) {
			for( Object player : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1))) {
				ItemStack stack = ((EntityPlayer)player).getEquipmentInSlot(3);

				//Check for O2 fill
				if(stack != null && stack.getItem() instanceof ItemSpaceChest) {
					FluidStack fluidStack = this.drain(ForgeDirection.UNKNOWN, 1, false);

					if(((ItemSpaceChest)stack.getItem()).getAirRemaining(stack) < ((ItemSpaceChest)stack.getItem()).getMaxAir(stack) &&
							fluidStack != null && fluidStack.getFluid().getUnlocalizedName().contains("oxygen") && fluidStack.amount > 0)  {
						this.drain(ForgeDirection.UNKNOWN, ((ItemSpaceChest)stack.getItem()).increment(stack, 1), true);
						
						return true;
					}
				}

				//Check for H2 fill (possibly merge with O2 fill
				//Fix conflict with O2 fill
				if(this.tank.getFluid() != null && this.tank.getFluid().getFluid() != AdvancedRocketryFluids.fluidOxygen && stack != null && stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);

					FluidStack fluidStack = this.drain(ForgeDirection.UNKNOWN, 100, false);
					if(fluidStack != null) {
						for(int i = 0; i < inv.getSizeInventory(); i++) {
							
							if(!((IModularArmor)stack.getItem()).canBeExternallyModified(stack, i))
								continue;
							
							ItemStack module = inv.getStackInSlot(i);
							if(module != null && module.getItem() instanceof IFluidContainerItem) {
								int amtFilled = ((IFluidContainerItem)module.getItem()).fill(module, fluidStack, true);
								if(amtFilled == 100) {
									this.drain(ForgeDirection.UNKNOWN, 100, true);
									
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
