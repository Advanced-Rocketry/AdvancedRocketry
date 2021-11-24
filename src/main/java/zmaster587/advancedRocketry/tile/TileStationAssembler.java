package zmaster587.advancedRocketry.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemSpaceStationContainer;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.util.EmbeddedInventory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.List;

public class TileStationAssembler extends TileRocketAssembler implements IInventory {

	EmbeddedInventory inventory;
	ResourceLocation storedId;
	public TileStationAssembler() {
		super(AdvancedRocketryTileEntityType.TILE_STATION_BUILDER);
	
		inventory = new EmbeddedInventory(4);
		status = ErrorCodes.EMPTY;
		storedId = Constants.INVALID_PLANET;
	}
	
	@Override
	public boolean canScan() {
		if(!super.canScan())
			return false;
		ItemStack stack = new ItemStack(AdvancedRocketryBlocks.blockSatelliteBay,1);

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
			SpaceStationObject spaceStationObject = null;
			if(Constants.INVALID_PLANET.equals(storedId)) {
				spaceStationObject = new SpaceStationObject();
				SpaceObjectManager.getSpaceManager().registerSpaceObject(spaceStationObject, Constants.INVALID_PLANET);

				outputStack = new ItemStack(AdvancedRocketryItems.itemSpaceStationContainer,1);
				ItemStationChip.setUUID(outputStack, spaceStationObject.getId());

			}
			else {
				outputStack = new ItemStack(AdvancedRocketryItems.itemSpaceStationContainer,1);
				ItemStationChip.setUUID(outputStack, storedId);
			}

			((ItemSpaceStationContainer)outputStack.getItem()).setStructure(outputStack, storageChunk);

			inventory.setInventorySlotContents(2, outputStack);

			if(Constants.INVALID_PLANET.equals(storedId)) {
				ItemStack stack = new ItemStack(AdvancedRocketryItems.itemSpaceStationChip,1);
				ItemStationChip.setUUID(stack,spaceStationObject.getId() );
				inventory.setInventorySlotContents(3, stack);
			}


			this.status = ErrorCodes.FINISHED;
			storedId = Constants.INVALID_PLANET;
			inventory.decrStackSize(0, 1);
			inventory.decrStackSize(1, 1);
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
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<>();

		modules.add(new ModulePower(160, 30, this));

		modules.add(new ModuleProgress(149, 30, 2, verticalProgressBar, this));

		modules.add(new ModuleButton(5, 34, LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.scan"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonScan).setAdditionalData(0));

		ModuleButton buttonBuild;
		modules.add(buttonBuild = new ModuleButton(5, 60, LibVulpes.proxy.getLocalizedString("msg.rocketbuilder.build"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild).setAdditionalData(1));
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
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		
		boolean isScanningFlag = !isScanning() && canScan();
		
		super.useNetworkData(player, side, id, nbt);
		if(id == 1 && isScanningFlag) {
			storedId = ItemStationChip.getUUID(inventory.getStackInSlot(1));
			if(storedId == DimensionManager.overworldProperties.getId()) storedId = Constants.INVALID_PLANET;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		inventory.write(nbt);
		if(storedId != null) {
			nbt.putString("storedID", storedId.toString().split(":")[1]);
		}
		return nbt;
	}

	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		inventory.readFromNBT(nbt);
		if(nbt.contains("storedID")) {
			storedId = new ResourceLocation(Constants.modId, nbt.getString("storedID"));
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}


	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}


	@Override
	@Nonnull
	public ItemStack decrStackSize(int slot, int amt) {
		return inventory.decrStackSize(slot, amt);
	}


	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}


	@Override
	@ParametersAreNonnullByDefault
	public boolean isUsableByPlayer(PlayerEntity player) {
		return inventory.isUsableByPlayer(player);
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	@ParametersAreNonnullByDefault
	public void openInventory(PlayerEntity pos) {

	}


	@Override
	@ParametersAreNonnullByDefault
	public void closeInventory(PlayerEntity pos) {

	}


	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return inventory.isItemValidForSlot(slot, stack);
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public void clear() {
		inventory.clear();
	}
	
	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}
}
