package zmaster587.advancedRocketry.tile.oxygen;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryFluids;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.util.IBlobHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.util.AreaBlob;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleLiquidIndicator;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.tile.TileInventoriedRFConsumerTank;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.IAdjBlockUpdate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TileOxygenVent extends TileInventoriedRFConsumerTank implements IBlobHandler, IModularInventory, IAdjBlockUpdate {

	boolean isSealed;
	boolean firstRun;
	boolean hasFluid;
	int numScrubbers;
	List<TileCO2Scrubber> scrubbers;
	
	public TileOxygenVent() {
		super(1000,2, 1000);
		isSealed = true;
		firstRun = true;
		hasFluid = true;
		numScrubbers = 0;
		scrubbers = new LinkedList<TileCO2Scrubber>();
	}

	public TileOxygenVent(int energy, int invSize, int tankSize) {
		super(energy, invSize, tankSize);
		isSealed = false;
		firstRun = false;
		hasFluid = true;
		scrubbers = new LinkedList<TileCO2Scrubber>();
	}

	@Override
	public boolean canPerformFunction() {
		return AtmosphereHandler.hasAtmosphereHandler(this.worldObj.provider.dimensionId);
	}
	
	@Override
	public World getWorld() {
		return getWorldObj();
	}

	@Override
	public void onAdjacentBlockUpdated() {
		if(isSealed) {

			activateAdjblocks();
			scrubbers.clear();
			TileEntity[] tiles = new TileEntity[4];
			tiles[0] = worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
			tiles[1] = worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
			tiles[2] = worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
			tiles[3] = worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);

			for(TileEntity tile : tiles) {
				if(tile instanceof TileCO2Scrubber && worldObj.getBlock(tile.xCoord, tile.yCoord, tile.zCoord) == AdvancedRocketryBlocks.blockOxygenScrubber)
					scrubbers.add((TileCO2Scrubber)tile);
			}

		}
	}

	private void activateAdjblocks() {
		numScrubbers = 0;
		numScrubbers = toggleAdjBlock(this.xCoord + 1, this.yCoord, this.zCoord, true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(this.xCoord - 1, this.yCoord, this.zCoord, true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord + 1, true) ? numScrubbers + 1 : numScrubbers;
		numScrubbers = toggleAdjBlock(this.xCoord, this.yCoord, this.zCoord - 1, true) ? numScrubbers + 1 : numScrubbers;
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
			if(on && (meta & 8) == 0)
				worldObj.setBlockMetadataWithNotify(x, y, z, 8, 2);
			else if(!on && (meta & 8) == 8)
				worldObj.setBlockMetadataWithNotify(x, y, z, 0, 2);

			return true;
		}
		return false;
	}

	@Override
	public void invalidate() {
		super.invalidate();

		AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId);
		if(handler != null)
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
		
		/*NB: canPerformFunction returns false and must return true for perform function to execute
		 *  if there is no O2 handler, this is why we can safely call AtmosphereHandler.getOxygenHandler
		 * And not have to worry about an NPE being thrown
		 */
		
		//IF first tick then register the blob and check for scrubbers
		if(firstRun && !worldObj.isRemote) {
			AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).registerBlob(this, xCoord, yCoord, zCoord);

			onAdjacentBlockUpdated();
			//isSealed starts as true so we can accurately check for scrubbers, we now set it to false to force the tile to check for a seal on first run
			isSealed = false;
			firstRun = false;
		}

		if(!worldObj.isRemote) {

			if(isSealed && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
				AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).clearBlob(this);

				deactivateAdjblocks();

				isSealed = false;
			}
			else if(!isSealed && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
				AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).addBlock(this, new BlockPosition(this.xCoord, this.yCoord, this.zCoord));
				isSealed = true;

				activateAdjblocks();
			}

			if(isSealed) {

				//If scrubbers exist and the config allows then use the cartridge
				if(Configuration.scrubberRequiresCartrige){
					//TODO: could be optimized
					if(worldObj.getTotalWorldTime() % 20 == 0) {
						numScrubbers = 0;
						for(TileCO2Scrubber scrubber : scrubbers) {
							numScrubbers =  scrubber.useCharge() ? numScrubbers + 1 : numScrubbers;
						}
					}

				}

				int amtToDrain = (int) (AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).getBlobSize(this)*getGasUsageMultiplier());
				FluidStack drainedFluid = this.drain(ForgeDirection.UNKNOWN, amtToDrain, false);

				if( (drainedFluid != null && drainedFluid.amount >= amtToDrain) || amtToDrain == 0) {
					this.drain(ForgeDirection.UNKNOWN, amtToDrain, true);
					if(!hasFluid) {
						hasFluid = true;

						activateAdjblocks();

						AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).setAtmosphereType(this, AtmosphereType.AIR);
					}
				}
				else if(hasFluid){
					AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId).setAtmosphereType(this, DimensionManager.getInstance().getDimensionProperties(this.worldObj.provider.dimensionId).getAtmosphere());

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
			AtmosphereHandler handler = AtmosphereHandler.getOxygenHandler(this.worldObj.provider.dimensionId);
			if(handler != null)
				handler.clearBlob(this);

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
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		ArrayList<ModuleBase> modules = new ArrayList<ModuleBase>();

		modules.add(new ModulePower(18, 20, this));
		modules.add(new ModuleLiquidIndicator(32, 20, this));
		//modules.add(toggleSwitch = new ModuleToggleSwitch(160, 5, 0, "", this, TextureResources.buttonToggleImage, 11, 26, getMachineEnabled()));
		//TODO add itemStack slots for liqiuid
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