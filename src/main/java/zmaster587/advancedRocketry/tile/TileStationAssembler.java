package zmaster587.advancedRocketry.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemPackedStructure;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.util.EmbeddedInventory;

import java.util.LinkedList;
import java.util.List;

public class TileStationAssembler extends TileRocketAssemblingMachine implements IInventory {

	EmbeddedInventory inventory;
	Long storedId;
	public TileStationAssembler() {
		super();
		inventory = new EmbeddedInventory(4);
		status = ErrorCodes.EMPTY;
	}

	@Override
	public boolean canScan() {
		if(!super.canScan())
			return false;
		ItemStack stack = new ItemStack(AdvancedRocketryBlocks.blockLoader,1,1);

		if(inventory.getStackInSlot(0).isEmpty() || !stack.isItemEqual(inventory.getStackInSlot(0))) {
			status = ErrorCodes.NOSATELLITEHATCH;
			return false;
		}

		if(inventory.getStackInSlot(1).isEmpty() || AdvancedRocketryItems.itemSpaceStationChip != inventory.getStackInSlot(1).getItem()) {
			status = ErrorCodes.NOSATELLITECHIP;
			return false;
		}
		if( !inventory.getStackInSlot(2).isEmpty() || !inventory.getStackInSlot(3).isEmpty()) {
			status = ErrorCodes.OUTPUTBLOCKED;
			return false;
		}

		return true;
	}

	@Override
	public void scanRocket(World world, BlockPos pos2, AxisAlignedBB bb) {

		int actualMinX = (int)bb.maxX,
				actualMinY = (int)bb.maxY,
				actualMinZ = (int)bb.maxZ,
				actualMaxX = (int)bb.minX,
				actualMaxY = (int)bb.minY,
				actualMaxZ = (int)bb.minZ;


		for(int xCurr = (int)bb.minX; xCurr <= bb.maxX; xCurr++) {
			for(int zCurr = (int)bb.minZ; zCurr <= bb.maxZ; zCurr++) {
				for(int yCurr = (int)bb.minY; yCurr<= bb.maxY; yCurr++) {

					BlockPos posCurr = new BlockPos(xCurr, yCurr, zCurr);

					if(!world.isAirBlock(posCurr)) {
						if(xCurr < actualMinX)
							actualMinX = xCurr;
						if(yCurr < actualMinY)
							actualMinY = yCurr;
						if(zCurr < actualMinZ)
							actualMinZ = zCurr;
						if(xCurr > actualMaxX)
							actualMaxX = xCurr;
						if(yCurr > actualMaxY)
							actualMaxY = yCurr;
						if(zCurr > actualMaxZ)
							actualMaxZ = zCurr;
					}
				}
			}
		}

		status = ErrorCodes.SUCCESS_STATION;
	}


	@Override
	public void assembleRocket() {
		if(!world.isRemote) {
			if(bbCache == null)
				return;
			//Need to scan again b/c something may have changed
			scanRocket(world, pos, bbCache);

			if(status != ErrorCodes.SUCCESS_STATION)
				return;
			StorageChunk storageChunk;
			try {
				storageChunk = StorageChunk.cutWorldBB(world, bbCache);
			} catch( NegativeArraySizeException e) {
				return;
			}

			ItemStack outputStack;
			SpaceStationObject object = null;
			if(storedId == null) {
				object = new SpaceStationObject();
				SpaceObjectManager.getSpaceManager().registerSpaceObject(object, Constants.INVALID_PLANET);

				outputStack = new ItemStack(AdvancedRocketryItems.itemSpaceStation,1);
				ItemStationChip.setUUID(outputStack, object.getId());

			}
			else {
				outputStack = new ItemStack(AdvancedRocketryItems.itemSpaceStation,1);
				ItemStationChip.setUUID(outputStack, (int)(long)storedId);
			}

			((ItemPackedStructure)outputStack.getItem()).setStructure(outputStack, storageChunk);

			inventory.setInventorySlotContents(2, outputStack);

			if(storedId == null) {
				ItemStack stack = new ItemStack(AdvancedRocketryItems.itemSpaceStationChip,1);
				ItemStationChip.setUUID(stack,object.getId() );
				inventory.setInventorySlotContents(3, stack);
			}


			this.status = ErrorCodes.FINISHED;
			storedId = null;
			this.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);	
		}
	}

	@Override
	protected void updateText() {
		if(!world.isRemote) { 
			if(getRocketPadBounds(world, pos) == null)
				setStatus(ErrorCodes.INCOMPLETESTRCUTURE.ordinal());
			else if( ErrorCodes.INCOMPLETESTRCUTURE.equals(getStatus()))
				setStatus(ErrorCodes.UNSCANNED.ordinal());
		}
		
		errorText.setText(status.getErrorCode());
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModulePower(160, 30, this));

		modules.add(new ModuleProgress(149, 30, 2, verticalProgressBar, this));

		modules.add(new ModuleButton(5, 34, 0, LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.scan"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonScan));

		ModuleButton buttonBuild;
		modules.add(buttonBuild = new ModuleButton(5, 60, 1, LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.build"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		buttonBuild.setColor(0xFFFF2222);
		modules.add(errorText = new ModuleText(5, 22, "", 0xFFFFFF22));
		modules.add(new ModuleSync(4, this));

		updateText();

		modules.add(new ModuleSlotArray(90, 40, inventory, 0, 1));
		modules.add(new ModuleTexturedSlotArray(108, 40, inventory, 1, 2, TextureResources.idChip));

		modules.add(new ModuleSlotArray(90, 60, inventory, 2, 4));

		return modules;
	}


	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		
		boolean isScanningFlag = !isScanning() && canScan();
		
		super.useNetworkData(player, side, id, nbt);
		if(id == 1 && isScanningFlag) {
			inventory.decrStackSize(0, 1);

			storedId = (long)ItemStationChip.getUUID(inventory.getStackInSlot(1));
			if(storedId == 0) storedId = null;
			if(storedId == null)
				inventory.decrStackSize(1, 1);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		inventory.writeToNBT(nbt);
		if(storedId != null) {
			nbt.setLong("storedID", storedId);
		}
		return nbt;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		inventory.readFromNBT(nbt);
		if(nbt.hasKey("storedID")) {
			storedId = nbt.getLong("storedID");
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}


	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}


	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return inventory.decrStackSize(slot, amt);
	}


	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}


	@Override
	public String getName() {
		return "tile.stationBuilder.name";
	}


	@Override
	public boolean hasCustomName() {
		return false;
	}


	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}


	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return inventory.isUsableByPlayer(player);
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public void openInventory(EntityPlayer pos) {

	}


	@Override
	public void closeInventory(EntityPlayer pos) {

	}


	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public int getField(int id) {
		return inventory.getField(id);
	}

	@Override
	public void setField(int id, int value) {
		inventory.setField(id, value);
	}

	@Override
	public int getFieldCount() {
		return inventory.getFieldCount();
	}

	@Override
	public void clear() {
		inventory.clear();
	}
}
