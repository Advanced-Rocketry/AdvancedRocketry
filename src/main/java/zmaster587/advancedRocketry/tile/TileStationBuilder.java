package zmaster587.advancedRocketry.tile;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemPackedStructure;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleSlotArray;
import zmaster587.libVulpes.inventory.modules.ModuleSync;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.inventory.modules.ModuleTexturedSlotArray;
import zmaster587.libVulpes.util.EmbeddedInventory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class TileStationBuilder extends TileRocketBuilder implements IInventory {

	EmbeddedInventory inventory;
	Long storedId;
	public TileStationBuilder() {
		super();
		inventory = new EmbeddedInventory(4);
		status = ErrorCodes.EMPTY;
	}

	@Override
	public boolean canScan() {
		ItemStack stack = new ItemStack(AdvancedRocketryBlocks.blockLoader,1,1);

		if(inventory.getStackInSlot(0) == null || !stack.isItemEqual(inventory.getStackInSlot(0))) {
			status = ErrorCodes.NOSATELLITEHATCH;
			return false;
		}

		if(inventory.getStackInSlot(1) == null || AdvancedRocketryItems.itemSpaceStationChip != inventory.getStackInSlot(1).getItem()) {
			status = ErrorCodes.NOSATELLITECHIP;
			return false;
		}
		if( inventory.getStackInSlot(2) != null || inventory.getStackInSlot(3) != null) {
			status = ErrorCodes.OUTPUTBLOCKED;
			return false;
		}

		return super.canScan();
	}

	@Override
	public void scanRocket(World world, int x, int y, int z, AxisAlignedBB bb) {

		int actualMinX = (int)bb.maxX,
				actualMinY = (int)bb.maxY,
				actualMinZ = (int)bb.maxZ,
				actualMaxX = (int)bb.minX,
				actualMaxY = (int)bb.minY,
				actualMaxZ = (int)bb.minZ;


		for(int xCurr = (int)bb.minX; xCurr <= bb.maxX; xCurr++) {
			for(int zCurr = (int)bb.minZ; zCurr <= bb.maxZ; zCurr++) {
				for(int yCurr = (int)bb.minY; yCurr<= bb.maxY; yCurr++) {

					Block block = world.getBlock(xCurr, yCurr, zCurr);

					if(!block.isAir(world, xCurr, yCurr, zCurr)) {
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
		if(!worldObj.isRemote) {
			if(bbCache == null)
				return;
			//Need to scan again b/c something may have changed
			scanRocket(worldObj, xCoord, yCoord, zCoord, bbCache);

			if(status != ErrorCodes.SUCCESS_STATION)
				return;

			StorageChunk storageChunk = StorageChunk.cutWorldBB(worldObj, bbCache);
			ItemStack outputStack;
			SpaceObject object = null;
			if(storedId == null) {
				object = new SpaceObject();
				SpaceObjectManager.getSpaceManager().registerSpaceObject(object, -1);

				outputStack = new ItemStack(AdvancedRocketryItems.itemSpaceStation,1, object.getId());
				
			}
			else
				outputStack = new ItemStack(AdvancedRocketryItems.itemSpaceStation,1, (int)(long)storedId);
			
			((ItemPackedStructure)outputStack.getItem()).setStructure(outputStack, storageChunk);
			inventory.setInventorySlotContents(2, outputStack);


			if(storedId == null)
				inventory.setInventorySlotContents(3, new ItemStack(AdvancedRocketryItems.itemSpaceStationChip,1,object.getId()));

			this.status = ErrorCodes.FINISHED;
			storedId = null;

			this.markDirty();
			this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	protected void updateText() {
		errorText.setText(status.getErrorCode());
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModulePower(160, 30, this));

		modules.add(new ModuleProgress(149, 30, 2, verticalProgressBar, this));

		modules.add(new ModuleButton(5, 34, 0, "Scan", this,  zmaster587.libVulpes.inventory.TextureResources.buttonScan));

		ModuleButton buttonBuild;
		modules.add(buttonBuild = new ModuleButton(5, 60, 1, "Build", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		buttonBuild.setColor(0xFFFF2222);
		modules.add(errorText = new ModuleText(5, 24, "", 0xFFFFFF22));
		modules.add(new ModuleSync(4, this));

		updateText();

		modules.add(new ModuleSlotArray(90, 20, inventory, 0, 1));
		modules.add(new ModuleTexturedSlotArray(108, 20, inventory, 1, 2, TextureResources.idChip));

		modules.add(new ModuleSlotArray(90, 60, inventory, 2, 4));

		return modules;
	}


	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id == 1 && canScan()) {
			inventory.decrStackSize(0, 1);
			storedId = (long)ItemStationChip.getUUID(inventory.getStackInSlot(1));
			if(storedId == 0) storedId = null;
			if(storedId == null)
				inventory.decrStackSize(1, 1);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		inventory.writeToNBT(nbt);
		if(storedId != null) {
			nbt.setLong("storedID", storedId);
		}
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
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}


	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}


	@Override
	public String getInventoryName() {
		return "tile.stationBuilder.name";
	}


	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}


	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}


	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}


	@Override
	public void openInventory() {

	}


	@Override
	public void closeInventory() {

	}


	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}
}
