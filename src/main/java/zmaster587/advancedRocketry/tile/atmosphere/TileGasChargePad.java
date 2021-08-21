package zmaster587.advancedRocketry.tile.atmosphere;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.armor.IFillableArmor;
import zmaster587.advancedRocketry.util.ItemAirUtils;
import zmaster587.libVulpes.api.IModularArmor;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.FluidUtils;
import zmaster587.libVulpes.util.IconResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TileGasChargePad extends TileInventoriedRFConsumerTank implements IModularInventory {
	public TileGasChargePad() {
		super(0, 2, 16000);
	}

	@Override
	@Nonnull
	public int[] getSlotsForFace(@Nullable EnumFacing side) {
		return new int[] {};
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return false;
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
		if(!world.isRemote) {
			for( EntityPlayer player : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos, pos.add(1,2,1)))) {
				ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

				if(!stack.isEmpty()) {
					IFillableArmor fillable = null;

					if(stack.getItem() instanceof IFillableArmor)
						fillable = (IFillableArmor)stack.getItem();
					else if(ItemAirUtils.INSTANCE.isStackValidAirContainer(stack))
						fillable = new ItemAirUtils.ItemAirWrapper(stack);
					
					//Check for O2 fill
					if(fillable != null ) {
						int amtFluid = fillable.getMaxAir(stack) - fillable.getAirRemaining(stack);
						FluidStack fluidStack = this.drain(amtFluid, false);

						if(amtFluid > 0 && fluidStack != null && FluidUtils.areFluidsSameType(fluidStack.getFluid(), AdvancedRocketryFluids.fluidOxygen) && fluidStack.amount > 0)  {
							FluidStack fstack = this.drain(amtFluid, true);
							this.markDirty();
							world.markChunkDirty(getPos(), this);
							fillable.increment(stack, fstack.amount);
							return true;
						}
					}
				}

				//Check for H2 fill (possibly merge with O2 fill
				//Fix conflict with O2 fill
				if(this.tank.getFluid() != null && !FluidUtils.areFluidsSameType(this.tank.getFluid().getFluid(), AdvancedRocketryFluids.fluidOxygen) && !stack.isEmpty() && stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);

					FluidStack fluidStack = this.drain(100, false);
					if(fluidStack != null) {
						for(int i = 0; i < inv.getSizeInventory(); i++) {

							if(!((IModularArmor)stack.getItem()).canBeExternallyModified(stack, i))
								continue;

							ItemStack module = inv.getStackInSlot(i);
							if(FluidUtils.containsFluid(module)) {
								int amtFilled = module.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, EnumFacing.UP).fill(fluidStack, true);
								if(amtFilled == 100) {
									this.drain(100, true);
									
									this.markDirty();
									world.markChunkDirty(getPos(), this);
									
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
		ArrayList<ModuleBase> modules = new ArrayList<>();

		modules.add(new ModuleSlotArray(50, 21, this, 0, 1));
		modules.add(new ModuleSlotArray(50, 57, this, 1, 2));
		if(world.isRemote)
			modules.add(new ModuleImage(49, 38, new IconResource(194, 0, 18, 18, CommonResources.genericBackground)));

		//modules.add(new ModulePower(18, 20, this));
		modules.add(new ModuleLiquidIndicator(32, 20, this));

		//modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));
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
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		super.setInventorySlotContents(slot, stack);
		while(useBucket(0, getStackInSlot(0)));
	}
	
	private boolean useBucket(int slot, @Nonnull ItemStack stack) {
		return FluidUtils.attemptDrainContainerIInv(inventory, tank, stack, 0, 1);
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
}
