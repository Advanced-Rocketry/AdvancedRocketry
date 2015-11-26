package zmaster587.advancedRocketry.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModuleLiquidIndicator;
import zmaster587.advancedRocketry.Inventory.modules.ModulePower;
import zmaster587.advancedRocketry.Inventory.modules.ModuleToggleSwitch;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.AtmosphereHandler;
import zmaster587.advancedRocketry.api.AtmosphereType;
import zmaster587.advancedRocketry.api.util.AreaBlob;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.util.IAdjBlockUpdate;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.BlockPosition;

public class TileOxygenVent extends TileInventoriedRFConsumerTank implements IBlobHandler, IModularInventory, IAdjBlockUpdate {

	boolean isSealed;
	boolean firstRun;
	boolean hasFluid;
	int numScrubbers;

	public TileOxygenVent() {
		super(1000,2, 1000);
		isSealed = false;
		firstRun = true;
		hasFluid = true;
		numScrubbers = 0;
	}

	public TileOxygenVent(int energy, int invSize, int tankSize) {
		super(energy, invSize, tankSize);
		isSealed = false;
		firstRun = false;
		hasFluid = true;
	}

	@Override
	public boolean canPerformFunction() {
		return true;
	}

	@Override
	public void onAdjacentBlockUpdated() {
		if(isSealed && hasFluid) {
			numScrubbers = 0;
			numScrubbers = toggleAdjBlock(this.xCoord + 1, this.yCoord, this.zCoord, true) ? numScrubbers + 1 : numScrubbers;
			numScrubbers = toggleAdjBlock(this.xCoord - 1, this.yCoord, this.zCoord, true) ? numScrubbers + 1 : numScrubbers;
			numScrubbers = toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord + 1, true) ? numScrubbers + 1 : numScrubbers;
			numScrubbers = toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord - 1, true) ? numScrubbers + 1 : numScrubbers;
		}
	}
	
	private void deactivateAdjblocks() {
		toggleAdjBlock(this.xCoord + 1, this.yCoord, this.zCoord, false);
		toggleAdjBlock(this.xCoord - 1, this.yCoord, this.zCoord, false);
		toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord + 1, false);
		toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord - 1, false);
	}

	private boolean toggleAdjBlock(int x, int y, int z, boolean on) {
		Block block = this.worldObj.getBlock(x,y,z);
		if(block == AdvancedRocketryBlocks.blockOxygenScrubber) {
			int meta = worldObj.getBlockMetadata(x,y,z);
			if(on && meta == 0)
				worldObj.setBlockMetadataWithNotify(x, y, z, 1, 2);
			else if(!on && meta == 1)
				worldObj.setBlockMetadataWithNotify(x, y, z, 0, 2);

			return true;
		}
		return false;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).unregisterBlob(this);
		deactivateAdjblocks();
	}

	@Override
	public int getPowerPerOperation() {
		return isSealed ? numScrubbers*10 + 1 : 0;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == AdvancedRocketryFluids.fluidOxygen && super.canFill(from, fluid);
	}

	@Override
	public void performFunction() {

		//IF first tick then register the blob and check for scrubbers
		if(firstRun && !worldObj.isRemote) {
			AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).registerBlob(this, xCoord, yCoord, zCoord);
			firstRun = false;
		}

		if(!worldObj.isRemote) {

			if(isSealed && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
				AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).getBlobFromHandler(this).clearBlob();

				deactivateAdjblocks();

				isSealed = false;
			}
			else if(!isSealed && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
				AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).getBlobFromHandler(this).addBlock(new BlockPosition(this.xCoord, this.yCoord, this.zCoord));
				isSealed = true;

				onAdjacentBlockUpdated();
			}

			if(isSealed) {
				int amtToDrain = (int) (AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).getBlobFromHandler(this).getBlobSize()*getGasUsageMultiplier());
				FluidStack drainedFluid = this.drain(ForgeDirection.UNKNOWN, amtToDrain, false);

				if( (drainedFluid != null && drainedFluid.amount >= amtToDrain) || amtToDrain == 0) {
					this.drain(ForgeDirection.UNKNOWN, amtToDrain, true);
					if(!hasFluid) {
						hasFluid = true;

						onAdjacentBlockUpdated();

						AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).getBlobFromHandler(this).setData(AtmosphereType.AIR);
					}
				}
				else if(hasFluid){
					AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).getBlobFromHandler(this).setData(DimensionManager.getInstance().getDimensionProperties(this.worldObj.provider.dimensionId).getAtmosphere());

					deactivateAdjblocks();

					hasFluid = false;
				}
			}
		}
	}

	public float getGasUsageMultiplier() {
		return Math.max(0.05f - numScrubbers*0.025f,0);
	}

	@Override
	public void notEnoughEnergyForFunction() {
		if(isSealed && !worldObj.isRemote) {
			AreaBlob blob = AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).getBlobFromHandler(this);
			blob.clearBlob();

			deactivateAdjblocks();

			isSealed = false;
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return new int[]{};
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean canBlobsOverlap(BlockPosition blockPosition, AreaBlob blob) {
		return false;
	}

	@Override
	public int getMaxBlobRadius() {
		return 32;
	}

	@Override
	public BlockPosition getRootPosition() {
		return new BlockPosition(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public List<ModuleBase> getModules() {
		ArrayList<ModuleBase> modules = new ArrayList<ModuleBase>();

		modules.add(new ModulePower(18, 20, this));
		modules.add(new ModuleLiquidIndicator(32, 20, this));
		//modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "OxygenVent";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public boolean canFormBlob() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
}