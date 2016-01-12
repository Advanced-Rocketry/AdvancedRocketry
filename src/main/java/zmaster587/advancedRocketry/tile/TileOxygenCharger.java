package zmaster587.advancedRocketry.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.armor.ItemSpaceArmor;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleLiquidIndicator;
import zmaster587.advancedRocketry.inventory.modules.ModulePower;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;

public class TileOxygenCharger extends TileInventoriedRFConsumerTank implements IModularInventory {
	public TileOxygenCharger() {
		super(1000, 2, 16000);
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
		
		if(resource.getFluid().getID() == AdvancedRocketryFluids.fluidOxygen.getID())
			return super.fill(from, resource, doFill);
		return 0;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid.getID() == FluidRegistry.getFluidID(AdvancedRocketryFluids.fluidOxygen);
	}	

	@Override
	public int getPowerPerOperation() {
		return 1;
	}
	
	@Override
	public boolean canPerformFunction() {
		if(!worldObj.isRemote) {
			for( Object player : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1))) {
				ItemStack stack = ((EntityPlayer)player).getEquipmentInSlot(3);

				if(stack != null && stack.getItem() instanceof ItemSpaceArmor) {
					FluidStack fluidStack = this.drain(ForgeDirection.UNKNOWN, 1, false);
					
					
					
					if(((ItemSpaceArmor)stack.getItem()).getAirRemaining(stack) < ((ItemSpaceArmor)stack.getItem()).getMaxAir() &&
							fluidStack != null && fluidStack.getFluid().getID() == AdvancedRocketryFluids.fluidOxygen.getID() && fluidStack.amount > 0)  {
						this.drain(ForgeDirection.UNKNOWN, 1, true);
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public void performFunction() {
		for( Object player : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1))) {
			ItemStack stack = ((EntityPlayer)player).getEquipmentInSlot(3);

			if(stack != null && stack.getItem() instanceof ItemSpaceArmor) {
				((ItemSpaceArmor)stack.getItem()).increment(stack, 100);
			}
		}
	}

	@Override
	public List<ModuleBase> getModules() {
		ArrayList<ModuleBase> modules = new ArrayList<ModuleBase>();

		modules.add(new ModulePower(18, 20, this));
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
}
