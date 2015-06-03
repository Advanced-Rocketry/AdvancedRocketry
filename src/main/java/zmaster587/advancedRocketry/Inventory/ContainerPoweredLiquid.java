package zmaster587.advancedRocketry.Inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import zmaster587.libVulpes.api.IUniversalEnergy;

public abstract class ContainerPoweredLiquid extends ContainerPowered {


	int liquidAmtId[];
	int liquidUUID[];
	
	//Prev values, used so the server knows when to send a changes
	int prevLiquidAmt[];
	int prevLiquidUUID[];
	int numTanks;
	IFluidHandler tile;
	private static final int invalidFluid = -1;


	public ContainerPoweredLiquid(InventoryPlayer playerInv, IFluidHandler tile) {
		super(playerInv, (IUniversalEnergy)tile);
		this.tile = tile;

		numTanks = tile.getTankInfo(ForgeDirection.UNKNOWN).length;

		liquidAmtId = new int[numTanks];
		liquidUUID = new int[numTanks];
		prevLiquidAmt = new int[numTanks];
		prevLiquidUUID = new int[numTanks];

		for(int i = 0; i < numTanks; i++) {
			liquidAmtId[i] = nextSlot();
			liquidUUID[i] = nextSlot();
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafter) {
		super.addCraftingToCrafters(crafter);

		FluidTankInfo info[] = tile.getTankInfo(ForgeDirection.UNKNOWN);

		for(int i = 0; i < numTanks; i++) {
			if(info[i].fluid == null) {
				prevLiquidAmt[i] = 0;
				prevLiquidUUID[i] = invalidFluid;
			}
			else {
				prevLiquidAmt[i] = info[i].fluid.amount;
				prevLiquidUUID[i] = info[i].fluid.fluidID;
			}
			crafter.sendProgressBarUpdate(this, liquidAmtId[i], prevLiquidAmt[i]);
			crafter.sendProgressBarUpdate(this, liquidUUID[i], prevLiquidUUID[i]);
		}

	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		FluidTankInfo info[] = tile.getTankInfo(ForgeDirection.UNKNOWN);
		
		for(int i = 0; i < numTanks; i++) {


			if(info[i].fluid != null) {
				
				if(prevLiquidUUID[i] != info[i].fluid.fluidID) {
					prevLiquidUUID[i] = info[i].fluid.fluidID;
					for (int j = 0; j < this.crafters.size(); ++j) {
						
						((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, liquidUUID[i], prevLiquidUUID[i]);
					}
				}
				
				if(prevLiquidAmt[i] != info[i].fluid.amount) {
					prevLiquidAmt[i] = info[i].fluid.amount;
					
					for (int j = 0; j < this.crafters.size(); ++j) {
						
						((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, liquidAmtId[i], prevLiquidAmt[i]);
					}
				}
				
			}
			else {
				if(prevLiquidUUID[i] != invalidFluid) {
					
					prevLiquidUUID[i] = invalidFluid;
					for (int j = 0; j < this.crafters.size(); ++j) {
						((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, liquidUUID[i], prevLiquidUUID[i]);
					}
					prevLiquidAmt[i] = 0;
				}
			}
		}
	}
	
	@Override
	public void updateProgressBar(int slot, int value) {
		super.updateProgressBar(slot, value);
		FluidTankInfo info[] = tile.getTankInfo(ForgeDirection.UNKNOWN);
		
		for(int i = 0; i < numTanks; i++) {
			
			if(slot == liquidUUID[i]) {
				if(info[i].fluid == null && value != invalidFluid) {
					tile.fill(ForgeDirection.UNKNOWN, new FluidStack(FluidRegistry.getFluid(value), 1), true);
				}
				else if(value == invalidFluid) {
					tile.drain(ForgeDirection.UNKNOWN, info[i].capacity, true);
				}
				else if(info[i].fluid != null && value != info[i].fluid.fluidID) { //Empty the tank then fill it back up with new resource
					FluidStack stack = tile.drain(ForgeDirection.UNKNOWN, info[i].capacity, true);
					stack.fluidID = value;
					tile.fill(ForgeDirection.UNKNOWN, stack, true);
				}
			}
			else if(slot == liquidAmtId[i] && info[i].fluid != null) {
				int difference = value - info[i].fluid.amount;
				
				if(difference > 0) {
					tile.fill(ForgeDirection.UNKNOWN, new FluidStack(info[i].fluid.getFluid(), difference), true);
				}
				else
					tile.drain(ForgeDirection.UNKNOWN, -difference, true);
			}
		}
	}
}
