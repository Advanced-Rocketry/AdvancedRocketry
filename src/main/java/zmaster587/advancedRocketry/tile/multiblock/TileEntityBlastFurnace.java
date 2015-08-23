package zmaster587.advancedRocketry.tile.multiblock;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.api.recipe.ITimedPoweredMachine;
import zmaster587.advancedRocketry.recipe.RecipesBlastFurnace;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.gui.IlimitedItemSlotEntity;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TileEntityMachine;
import zmaster587.libVulpes.tile.TileEntityPointer;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityBlastFurnace extends TileEntityMachine implements ISidedInventory, IMultiblock, IlimitedItemSlotEntity, ITimedPoweredMachine   {

	//offset from bottom left corner
	private static final int xSize = 5, ySize = 5, zSize = 5, xOffset = 2, yOffset = 1;
	private int progress, heat, maxHeat;
	private final int INV_SIZE = 24; // 6 slots for silica, 6 for coke/coal, 6 for output
	private int opTime = 0; //5 minutes (less for testings)6000
	private boolean complete;

	public TileEntityBlastFurnace() {
		progress = 0;
		maxHeat = 1;
		heat = 0;
		inv = new ItemStack[INV_SIZE];
		energy = new EnergyStorage(0);
		complete = false;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	public boolean hasMaster() {
		return complete;
	}

	public TileEntity getMasterBlock() {
		return this;
	}

	public void setComplete(int x, int y, int z) {

		Runnable run = new Runnable() {

			@Override
			public void run() {
				ForgeDirection inwardDir = RotatableBlock.getFront(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)).getOpposite();
				ForgeDirection perpendicular = inwardDir.getRotation(ForgeDirection.UP);

				int currX = xCoord - (perpendicular.offsetX * xOffset);
				int currY = yCoord - yOffset;
				int currZ = zCoord - (perpendicular.offsetZ * xOffset);

				if(!checkCap(currX, currY, currZ, perpendicular) || !checkCap(currX + inwardDir.offsetX*(zSize-1), currY, currZ + inwardDir.offsetZ*(zSize-1), perpendicular)) {
					return;
				}

				for(int i = 1; i + 1 < zSize; i++)
					if(!checkMid(currX + i*inwardDir.offsetX, currY, currZ + i*inwardDir.offsetZ, perpendicular)) {
						return;
					}


				assignCap(currX, currY, currZ, perpendicular);
				assignCap(currX + inwardDir.offsetX*(zSize-1), currY, currZ + inwardDir.offsetZ*(zSize-1), perpendicular);

				for(int i = 1; i + 1 < zSize; i++) {
					assignMid(currX + i*inwardDir.offsetX, currY, currZ + i*inwardDir.offsetZ, perpendicular);
				}

				complete = true;
			}

			private void assignMid(int currX, int currY, int currZ, ForgeDirection perpendicular) {
				for(int y = 0; y < ySize; y++) {
					//worldObj.getBlockId(currX, currY + y, currZ);

					worldObj.setBlockMetadataWithNotify(currX, currY + y, currZ, 1, 3);
					((TileEntityPointer)worldObj.getTileEntity(currX, currY + y, currZ)).setMasterBlock(xCoord, yCoord, zCoord);

					worldObj.setBlockMetadataWithNotify(currX + perpendicular.offsetX*(xSize-1), currY + y, currZ + perpendicular.offsetZ*(zSize-1),1,3);
					((TileEntityPointer)worldObj.getTileEntity(currX + perpendicular.offsetX*(xSize-1), currY + y, currZ + perpendicular.offsetZ*(zSize-1))).setMasterBlock(xCoord, yCoord, zCoord);

				}

				for(int x = 1; x + 1 < xSize; x++) {
					worldObj.setBlockMetadataWithNotify(currX + x*perpendicular.offsetX, currY, currZ + perpendicular.offsetZ*x,1,3);
					((TileEntityPointer)worldObj.getTileEntity(currX + x*perpendicular.offsetX, currY, currZ + perpendicular.offsetZ*x)).setMasterBlock(xCoord, yCoord, zCoord);

					worldObj.setBlockMetadataWithNotify(currX + x*perpendicular.offsetX, currY + ySize-1, currZ + perpendicular.offsetZ*x,1,3);
					((TileEntityPointer)worldObj.getTileEntity(currX + x*perpendicular.offsetX, currY + ySize-1, currZ + perpendicular.offsetZ*x)).setMasterBlock(xCoord, yCoord, zCoord);

				}
			}

			private void assignCap(int currX, int currY, int currZ, ForgeDirection perpendicular) {
				for(int x = 0; x < xSize; x++) {
					for(int y = 0; y < ySize; y++) {
						if(!(currX + (x*perpendicular.offsetX) == xCoord && currY + y == yCoord && currZ + (x*perpendicular.offsetZ) == zCoord)) {
							worldObj.setBlockMetadataWithNotify(currX + x*perpendicular.offsetX, currY + y, currZ + x*perpendicular.offsetZ,1,3);
							((TileEntityPointer)worldObj.getTileEntity(currX + x*perpendicular.offsetX, currY + y, currZ + x*perpendicular.offsetZ)).setMasterBlock(xCoord, yCoord, zCoord);
						}
					}
				}
			}

			private boolean checkMid(int currX, int currY, int currZ, ForgeDirection perpendicular) {
				for(int y = 0; y < ySize; y++) {
					if((worldObj.getBlock(currX, currY + y, currZ) != AdvRocketryBlocks.blockBlastBrick || worldObj.getBlockMetadata(currX, currY + y, currZ) != 0)  ||
							(worldObj.getBlock(currX + perpendicular.offsetX*(xSize-1), currY + y, currZ + perpendicular.offsetZ*(zSize-1))  != AdvRocketryBlocks.blockBlastBrick || worldObj.getBlockMetadata(currX + perpendicular.offsetX*(xSize-1), currY + y, currZ + perpendicular.offsetZ*(zSize-1)) != 0)) {
						return false;
					}

				}

				for(int x = 1; x + 1 < xSize; x++) {
					if((worldObj.getBlock(currX + x*perpendicular.offsetX, currY, currZ + perpendicular.offsetZ*x) != AdvRocketryBlocks.blockBlastBrick && worldObj.getBlockMetadata(currX + x*perpendicular.offsetX, currY, currZ + perpendicular.offsetZ*x) == 0) ||
							(worldObj.getBlock(currX + x*perpendicular.offsetX, currY + ySize-1, currZ + perpendicular.offsetZ*x) != AdvRocketryBlocks.blockBlastBrick || worldObj.getBlockMetadata(currX + x*perpendicular.offsetX, currY + ySize-1, currZ + perpendicular.offsetZ*x) != 0)) {
						return false;
					}
				}
				return true;
			}

			private boolean checkCap(int currX, int currY, int currZ, ForgeDirection perpendicular) {
				for(int x = 0; x < xSize; x++) {
					for(int y = 0; y < ySize; y++) {
						if(!(currX + (x*perpendicular.offsetX) == xCoord && currY + y == yCoord && currZ + (x*perpendicular.offsetZ) == zCoord) &&
								(worldObj.getBlock(currX + x*perpendicular.offsetX, currY + y, currZ + x*perpendicular.offsetZ) != AdvRocketryBlocks.blockBlastBrick || worldObj.getBlockMetadata(currX + x*perpendicular.offsetX, currY + y, currZ + x*perpendicular.offsetZ) != 0)) {
							return false;
						}
					}
				}
				return true;
			}
		};
		//if(!worldObj.isRemote)
		run.run();
		//new Thread(run).start();
	}

	public void setIncomplete() {

		Runnable run = new Runnable() {

			@Override
			public void run() {
				ForgeDirection inwardDir = RotatableBlock.getFront(worldObj.getBlockMetadata(xCoord, yCoord, zCoord)).getOpposite();
				ForgeDirection perpendicular = inwardDir.getRotation(ForgeDirection.UP);

				int currX = xCoord - (perpendicular.offsetX * xOffset);
				int currY = yCoord - yOffset;
				int currZ = zCoord - (perpendicular.offsetZ * xOffset);


				assignCap(currX, currY, currZ, perpendicular);
				assignCap(currX + inwardDir.offsetX*(zSize-1), currY, currZ + inwardDir.offsetZ*(zSize-1), perpendicular);

				for(int i = 1; i + 1 < zSize; i++) {
					assignMid(currX + i*inwardDir.offsetX, currY, currZ + i*inwardDir.offsetZ, perpendicular);
				}
			}

			private void assignMid(int currX, int currY, int currZ, ForgeDirection perpendicular) {
				for(int y = 0; y < ySize; y++) {
					if(worldObj.getBlock(currX, currY + y, currZ) == AdvRocketryBlocks.blockBlastBrick) {
						worldObj.setBlockMetadataWithNotify(currX, currY + y, currZ, 0, 3);
					}

					if(worldObj.getBlock(currX + perpendicular.offsetX*(xSize-1), currY + y, currZ + perpendicular.offsetZ*(zSize-1)) == AdvRocketryBlocks.blockBlastBrick)
						worldObj.setBlockMetadataWithNotify(currX + perpendicular.offsetX*(xSize-1), currY + y, currZ + perpendicular.offsetZ*(zSize-1),0,3);

				}

				for(int x = 1; x + 1 < xSize; x++) {
					if(worldObj.getBlock(currX + x*perpendicular.offsetX, currY, currZ + perpendicular.offsetZ*x) == AdvRocketryBlocks.blockBlastBrick)
						worldObj.setBlockMetadataWithNotify(currX + x*perpendicular.offsetX, currY, currZ + perpendicular.offsetZ*x,0,3);

					if(worldObj.getBlock(currX + x*perpendicular.offsetX, currY + ySize-1, currZ + perpendicular.offsetZ*x) == AdvRocketryBlocks.blockBlastBrick)
						worldObj.setBlockMetadataWithNotify(currX + x*perpendicular.offsetX, currY + ySize-1, currZ + perpendicular.offsetZ*x,0,3);

				}
			}

			private void assignCap(int currX, int currY, int currZ, ForgeDirection perpendicular) {
				for(int x = 0; x < xSize; x++) {
					for(int y = 0; y < ySize; y++) {
						if(worldObj.getBlock(currX + x*perpendicular.offsetX, currY + y, currZ + x*perpendicular.offsetZ) == AdvRocketryBlocks.blockBlastBrick) {
							worldObj.setBlockMetadataWithNotify(currX + x*perpendicular.offsetX, currY + y, currZ + x*perpendicular.offsetZ,0,3);
						}
					}
				}
			}
		};

		//new Thread(run).start();
		//if(!worldObj.isRemote)
		run.run();

		//Reset everyone destroy the current recipe
		complete = false;
		this.setRunning(false, this.worldObj);
		progress = 0;
		heat = 0;
		for(int i = 0; i < 6; i++)
			inv[i+18] = null;

		//TODO: debug:

	}

	@Override
	public void updateEntity() {
		if(isRunning()) {

			if(heat != 0)
				progress++;

			if(progress == opTime ) {
				for(int i = 0; i < 6; i++) {
					if(inv[i+18] != null) {
						if(inv[i+12] != null)
							incStackSize(i+12, inv[i+18].stackSize);
						else
							inv[i+12] = inv[i+18].copy();

						inv[i+18] = null;
					}
				}
				opTime = 0;
				onInventoryUpdate();
			}
			//Check again to see if recipe has fininshed
			if(isRunning()) {
				if(heat == 0) {
					for(int i = 0; i < 6; i++) {
						int fuelValue = 0;
						if(inv[i+6] != null && (fuelValue = RecipesBlastFurnace.getInstance().getFuelValue(inv[i+6])) != -1) {
							decrStackSize(i+6, 1);
							setHeatWithMax(fuelValue);
							break;
						}
					}
				}
			}
		}

		if(heat == 0 && (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 8) == 8)
			setRunning(false, this.worldObj);
		else if(heat > 0 && (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 8) == 0)
			setRunning(true, this.worldObj);

		if(heat > 0)
			heat--;
	}

	public void setHoldingSlot(int slot, ItemStack item) {
		inv[slot+18] = item;
	}

	public int getHeat() {return heat;}

	private void setHeatWithMax(int heatVal) {
		maxHeat = heat = heatVal + 1;
	}

	public int getScaledProgress(int value) {
		return (int)(value*progress/(float)opTime);
	}

	public int getMaxHeat() {return maxHeat;}

	public int getScaledHeat(int value) {
		return (int)(value*(heat/(float)maxHeat));
	}

	@SideOnly(Side.CLIENT)
	public void setHeat(int num) { heat = num;}

	@SideOnly(Side.CLIENT)
	public void setMaxHeat(int num) {maxHeat = num;}

	public int getProgress() { return progress; }
	@SideOnly(Side.CLIENT)
	public void setProgress(int num) { progress = num; }

	public boolean isRunning() {
		return opTime != 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setInteger("progress", progress);
		nbt.setInteger("Heat", heat);
		nbt.setBoolean("complete",complete);
		nbt.setInteger("maxHeat", maxHeat);
		nbt.setInteger("opTime", opTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		progress = nbt.getInteger("progress");
		heat = nbt.getInteger("Heat");
		maxHeat = nbt.getInteger("maxHeat");
		complete = nbt.getBoolean("complete");
		opTime = nbt.getInteger("opTime");
	}

	public void onInventoryUpdate() {
		if(!isRunning()) {

			ItemStack input = null;
			ItemStack output = null;
			for(int i = 0; i < 6; i++) {
				output = RecipesBlastFurnace.getInstance().getRecipeOutput(this, i, this.worldObj);
				if(output == null || inv[i+12] != null && inv[i+12].getMaxStackSize() - inv[i+12].stackSize < output.stackSize ) {
					//setRunning(false, this.worldObj);
					continue;

				}
				else {
					input = inv[i];
					break;
				}
			}

			if(output == null)
				return;


			if(heat == 0) {
				for(int g = 0; g < 6; g++) {
					int fuelValue = 0;
					if(inv[g+6] != null && (fuelValue = RecipesBlastFurnace.getInstance().getFuelValue(inv[g+6])) != -1) {
						setHeatWithMax(fuelValue);
						decrStackSize(g+6, 1);
						break;
					}
				}
			}
			if(heat == 0) {
				setRunning(false, this.worldObj);
				return;
			}

			setRunning(true, this.worldObj);

			opTime = RecipesBlastFurnace.getInstance().getRecipeTime(input);
			progress = 0;
			for(int i=0; i < 6; i++) {
				if((inv[i+12] == null || output.isItemEqual(inv[i+12])) && inv[i] != null && inv[i].isItemEqual(input)) {
					//int amt = 
					decrStackSize(i, 1);
					///RecipesBlastFurnace.getInstance().useResources(this, i, this.worldObj);
					//output.stackSize *= amt;
					inv[i+18] = output.copy();
				}
			}
		}
	}

	//ISidedInventory start
	@Override
	public String getInventoryName() {
		// TODO Auto-generated method stub
		return "blastFurnace";
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {

		if(slot < 6) {
			return RecipesBlastFurnace.getInstance().isValidInputStack(itemstack);
		}
		else if(slot < 12 && slot > 5) {
			return RecipesBlastFurnace.getInstance().isValidFuel(itemstack);
		}
		return false;
	}

	@Override
	public void setRecipeTime(int time) {}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if(hasMaster())
			return new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
		return null;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		ForgeDirection dir = ForgeDirection.getOrientation(j);
		if(i < 12 && isItemValidForSlot(i, itemstack))
			return true;
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		ForgeDirection dir = ForgeDirection.getOrientation(j);

		if(i > 11)
			return true;
		return false;
	}

	@Override
	public boolean isItemValidForLimitedSlot(int slot, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return isItemValidForSlot(slot, itemstack);
	}
	//ISidedInventory end
	//Energy start

	//Doesnt have power
	@Override
	public void removePower(int amt) {
		
	}

	@Override
	public int getPower() {
		return 0;
	}

	//Energy end
}
