package zmaster587.advancedRocketry.tile.Satellite;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.*;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.EmbeddedInventory;

import java.util.LinkedList;
import java.util.List;

public class TileSatelliteBuilder extends TileMultiPowerConsumer implements IModularInventory, IInventory, IButtonInventory {

	public static final Object[][][] structure = new Object[][][] {
		{{'c'}},
		{{'P'}}
	};

	EmbeddedInventory inventory;
	//Slot 0: functional Piece
	//Slot 1 -> 3: power
	//Slot 4 -> 7: data Storage
	//Slot 7: output
	//Slot 8: chip printing slot
	//Slot 9: chip duping slot
	//Slot 10: temp holding space (Inaccessible)

	private static final byte holdingSlot = 10;
	private static final byte outputSlot = 7;
	private static final byte chipSlot = 8;
	private static final byte chipCopySlot = 9;
	private static final byte chassisSlot = 11;

	public TileSatelliteBuilder() {
		super(AdvancedRocketryTileEntityType.TILE_SAT_BUILDER);
		inventory = new EmbeddedInventory(5);
		powerPerTick = 10;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	protected void processComplete() {
		super.processComplete();
		setInventorySlotContents(outputSlot, removeStackFromSlot(holdingSlot));
	}

	public boolean canAssembleSatellite() {

		if(getStackInSlot(chassisSlot).isEmpty())
			return false;

		//First make sure everything is a satellite part
		for(int i = 0; i < 7; i++) {
			ItemStack stack = getStackInSlot(i);
			if(!stack.isEmpty() && SatelliteRegistry.getSatelliteProperty(stack) == null)
				return false;
		}

		//Make sure critical parts exist and output is empty
		if(getStackInSlot(0).isEmpty() || !getStackInSlot(holdingSlot).isEmpty() || !getStackInSlot(outputSlot).isEmpty() || SatelliteRegistry.getSatelliteProperty(getStackInSlot(0)).getSatelliteType() == null)
			return false;

		String satType = SatelliteRegistry.getSatelliteProperty(getStackInSlot(0)).getSatelliteType();
		SatelliteBase sat = SatelliteRegistry.getSatallite(satType);

		//TODO: UNDEBUG if 0 power gen also return false
		return sat.isAcceptableControllerItemStack(getStackInSlot(chipSlot));
	}

	/**
	 * Assumes everything is in the proper place in the inventory to construct the satellite
	 * If unsure check canAssembleSatellite() first
	 */
	public void assembleSatellite() {
		int powerStorage = 0, powerGeneration = 0, maxData = 0;
		ItemStack stack = getStackInSlot(chassisSlot);
		ItemSatellite item = (ItemSatellite) stack.getItem();

		SatelliteProperties properties = item.getSatellite(stack);

		String satType = SatelliteRegistry.getSatelliteProperty(getStackInSlot(0)).getSatelliteType();
		SatelliteBase sat = SatelliteRegistry.getSatallite(satType);
		
		if(!world.isRemote) {
			//Set final satellite properties
			if(properties == null || properties.getSatelliteType().isEmpty()) {
				properties = new SatelliteProperties(powerGeneration, powerStorage, satType,maxData);
				properties.setId(DimensionManager.getInstance().getNextSatelliteId());
			}

			//Create the output item
			ItemSatellite satItem = (ItemSatellite)AdvancedRocketryItems.itemSatellite;
			ItemStack output = getStackInSlot(chassisSlot);
			satItem.setSatellite(output, properties);
			setInventorySlotContents(chassisSlot, ItemStack.EMPTY);

			//Set the ID chip
			setInventorySlotContents(chipSlot, sat.getContollerItemStack(getStackInSlot(chipSlot), properties));

			setInventorySlotContents(holdingSlot, output);
		}

		completionTime = 100;
	}

	private boolean canCopy() {
		ItemStack stack0 = getStackInSlot(chipSlot);
		ItemStack stack1 = getStackInSlot(chipCopySlot);
		getStackInSlot(outputSlot);

		boolean chipsExist = !stack0.isEmpty() && !stack1.isEmpty();
		if(!chipsExist)
			return false;
		boolean isSatellite = ((stack0.getItem() instanceof ItemSatellite || stack0.getItem() instanceof ItemSatelliteIdentificationChip) && stack1.getItem().equals(stack0.getItem()));
		boolean isStation = stack0.getItem() instanceof ItemStationChip && !ItemStationChip.getUUID(stack0).equals(DimensionManager.overworldProperties.getId()) && stack1.getItem() instanceof ItemStationChip;
		boolean isPlanet = (stack0.getItem() instanceof ItemPlanetIdentificationChip && stack1.getItem() instanceof ItemPlanetIdentificationChip);
		boolean isOreScanner = (stack0.getItem() instanceof ItemOreScanner && stack1.getItem() instanceof ItemOreScanner);
		return !isRunning() && getStackInSlot(outputSlot).isEmpty() && (isStation || stack0.hasTag()) && 
				(isSatellite  || isStation || isPlanet || isOreScanner);
	}

	private void copyChip() {

		ItemStack slot0 = getStackInSlot(chipSlot);
		ItemStack slot1 = getStackInSlot(chipCopySlot);

		if(slot0.getItem() instanceof ItemSatelliteIdentificationChip || slot0.getItem() instanceof ItemOreScanner || slot0.getItem() instanceof ItemPlanetIdentificationChip || slot0.getItem() instanceof ItemStationChip) {
			setInventorySlotContents(holdingSlot, getStackInSlot(chipSlot).copy());
		}
		else {
			ItemSatellite satelliteItem = (ItemSatellite)slot0.getItem();

			ItemSatelliteIdentificationChip itemIdChip = (ItemSatelliteIdentificationChip)slot1.getItem();

			itemIdChip.setSatellite(slot1, satelliteItem.getSatellite(slot0));
			setInventorySlotContents(holdingSlot, slot1);
		}
		decrStackSize(chipCopySlot, 1);
		completionTime = 100;

		this.markDirty();
	}

	private void doActionById(int buttonId)
	{
		if(buttonId == 0) {
			if(canAssembleSatellite())
				assembleSatellite();
		}
		else if(buttonId == 1)
			if(canCopy())
				copyChip();
	}
	
	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		super.useNetworkData(player, side, id, nbt);


		doActionById(id - 100);
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(new ModuleTexturedLimitedSlotArray(152, 10, this, 0, 1, TextureResources.functionComponent)); //Functional Piece
		modules.add(new ModuleTexturedLimitedSlotArray(116, 30, this, 1, 4, TextureResources.powercomponent));  //Generators
		modules.add(new ModuleTexturedLimitedSlotArray(116, 50, this, 4, 7, TextureResources.ioSlot));
		modules.add(new ModuleTexturedLimitedSlotArray(38, 16, this, chassisSlot, chassisSlot + 1, TextureResources.slotSatellite)); 	// Id chip
		modules.add(new ModuleOutputSlotArray(58, 36, this, 7, 8));   // Output
		modules.add(new ModuleTexturedSlotArray(58, 16, this, chipSlot, chipSlot + 1, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleTexturedSlotArray(82, 16, this, chipCopySlot, chipCopySlot+1, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleProgress(75, 36, 0, new ProgressBarImage(217,0, 17, 17, 234, 0, Direction.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleButton(40, 56, "Build", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild).setAdditionalData(0));
		modules.add(new ModuleButton(173, 3, "", this, TextureResources.buttonCopy, LibVulpes.proxy.getLocalizedString("msg.satbuilder.writesecondchip"), 24, 24).setAdditionalData(1));

		return modules;
	}

	@Override
	public void onInventoryButtonPressed(ModuleButton buttonId) {
		if(world.isRemote)
			PacketHandler.sendToServer(new PacketMachine(this, (byte)((int)buttonId.getAdditionalData() + 100)) );

		doActionById((int)buttonId.getAdditionalData());

	}

	@Override
	public String getMachineName() {
		return "tile.satelliteBuilder.name";
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				return inv.getStackInSlot(slot);
			}
			return ItemStack.EMPTY;
		}
		return inventory.getStackInSlot(slot - 7);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		if(slot < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				ItemStack stack = inv.decrStackSize(slot, amt);
				((ItemSatellite)chassis.getItem()).writeInvToNBT(chassis, inv);
				return stack;
			}

			return ItemStack.EMPTY;
		}

		return inventory.decrStackSize(slot - 7, amt);
	}


	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(slot < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				inv.setInventorySlotContents(slot, stack);
				((ItemSatellite)chassis.getItem()).writeInvToNBT(chassis, inv);
			}
			return;
		}
		inventory.setInventorySlotContents(slot-7, stack);
	}


	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return pos.distanceSq(new BlockPos(player.getPositionVec())) < 4192;
	}

	@Override
	public void openInventory(PlayerEntity player) {
		inventory.openInventory(player);
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		inventory.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(slot < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				return inv.isItemValidForSlot(slot, stack);
			}
			return false;
		}
		return inventory.isItemValidForSlot(slot - 7, stack);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		inventory.write(nbt);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		inventory.readFromNBT(nbt);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if(index < outputSlot) {
			ItemStack chassis = getStackInSlot(chassisSlot);

			if(!chassis.isEmpty() && chassis.getItem() instanceof ItemSatellite) {
				EmbeddedInventory inv = ((ItemSatellite)chassis.getItem()).readInvFromNBT(chassis);
				ItemStack stack = inv.removeStackFromSlot(index);
				((ItemSatellite)chassis.getItem()).writeInvToNBT(chassis,inv);
				return stack;
			}
			return ItemStack.EMPTY;
		}

		return inventory.removeStackFromSlot(index - 7);
	}

	@Override
	public void clear() {
		inventory.clear();
	}
	
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
}