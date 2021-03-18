package zmaster587.advancedRocketry.tile.satellite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
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
	//Slot 0: Main satellite device
	//Slot 1 -> 6: Other functional pieces
	//Slot 7: output
	//Slot 8: chip printing slot
	//Slot 9: chip duping slot
	//Slot 10: temp holding space (Inaccessible)

	private static final byte primaryFunctionSlot = 0;
	private static final byte modularFunctionSlotStart = 1;
	private static final byte modularFunctionSlotEnd = 6;
	private static final byte outputSlot = 7;
	private static final byte chipSlot = 8;
	private static final byte chipCopySlot = 9;
	private static final byte holdingSlot = 10;
	private static final byte chassisSlot = 11;

	public TileSatelliteBuilder() {
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

		boolean hasPowerGeneration = false;
		//First make sure everything is a satellite part, and check to see if satellite has any power generation
		for(int i = primaryFunctionSlot; i <= modularFunctionSlotEnd; i++) {
			ItemStack stack = getStackInSlot(i);
			if (SatelliteRegistry.getSatelliteProperty(stack).getPowerGeneration() > 0 )
				hasPowerGeneration = true;
			if(!stack.isEmpty() && SatelliteRegistry.getSatelliteProperty(stack) == null)
				return false;
		}

		//Make sure critical parts exist and output is empty
		if(getStackInSlot(primaryFunctionSlot).isEmpty() || SatelliteRegistry.getSatelliteProperty(getStackInSlot(primaryFunctionSlot)).getSatelliteType() == null || !hasPowerGeneration)
			return false;
		if(!getStackInSlot(holdingSlot).isEmpty() || !getStackInSlot(outputSlot).isEmpty() )
			return false;

		String satType = SatelliteRegistry.getSatelliteProperty(getStackInSlot(primaryFunctionSlot)).getSatelliteType();
		SatelliteBase sat = SatelliteRegistry.getSatallite(satType);

		return sat.isAcceptableControllerItemStack(getStackInSlot(chipSlot));
	}

	/**
	 * Assumes everything is in the proper place in the inventory to construct the satellite
	 * If unsure check canAssembleSatellite() first
	 */
	public void assembleSatellite() {
		//Basic properties of the satellite
		int powerStorage = 0, powerGeneration = 0, maxData = 0;

		//Get the primary function from slot 0
		String satType = SatelliteRegistry.getSatelliteProperty(getStackInSlot(primaryFunctionSlot)).getSatelliteType();
		SatelliteBase sat = SatelliteRegistry.getSatallite(satType);
		
		if(!world.isRemote) {
			//Grab properties from the items in slots 1-6
			for (int currentSlotIndex = modularFunctionSlotStart; currentSlotIndex <= modularFunctionSlotEnd; currentSlotIndex++) {
				powerStorage += SatelliteRegistry.getSatelliteProperty(getStackInSlot(currentSlotIndex)).getPowerStorage();
				powerGeneration += SatelliteRegistry.getSatelliteProperty(getStackInSlot(currentSlotIndex)).getPowerGeneration();
				maxData += SatelliteRegistry.getSatelliteProperty(getStackInSlot(currentSlotIndex)).getMaxDataStorage();
			}

			//Set final satellite properties
			//720 here is the base power buffer, so the satellite has SOMETHING to run on
			SatelliteProperties properties = new SatelliteProperties(powerGeneration, powerStorage + 720, satType,maxData);
			properties.setId(DimensionManager.getInstance().getNextSatelliteId());

			//Create the output item
			ItemSatellite satItem = (ItemSatellite)AdvancedRocketryItems.itemSatellite;
			ItemStack output = getStackInSlot(chassisSlot);
			satItem.setSatellite(output, properties);
			setInventorySlotContents(chassisSlot, ItemStack.EMPTY);

			//Set the ID chip
			setInventorySlotContents(chipSlot, sat.getContollerItemStack(getStackInSlot(chipSlot), properties));
			//Move item to temporary holding slot
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
		boolean isStation = stack0.getItem() instanceof ItemStationChip && ItemStationChip.getUUID(stack0) != 0 && stack1.getItem() instanceof ItemStationChip;
		boolean isPlanet = (stack0.getItem() instanceof ItemPlanetIdentificationChip && stack1.getItem() instanceof ItemPlanetIdentificationChip);
		boolean isOreScanner = (stack0.getItem() instanceof ItemOreScanner && stack1.getItem() instanceof ItemOreScanner);
		return !isRunning() && getStackInSlot(outputSlot).isEmpty() && (isStation || stack0.hasTagCompound()) && 
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

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);


		onInventoryButtonPressed(id - 100);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModulePower(18, 20, getBatteries()));
		modules.add(new ModuleTexturedLimitedSlotArray(152, 10, this, 0, 1, TextureResources.functionComponent)); //Functional Piece
		modules.add(new ModuleTexturedLimitedSlotArray(116, 30, this, 1, 4, TextureResources.powercomponent));  //Generators
		modules.add(new ModuleTexturedLimitedSlotArray(116, 50, this, 4, 7, TextureResources.ioSlot));
		modules.add(new ModuleTexturedLimitedSlotArray(38, 16, this, chassisSlot, chassisSlot + 1, TextureResources.slotSatellite)); 	// Id chip
		modules.add(new ModuleOutputSlotArray(58, 36, this, 7, 8));   // Output
		modules.add(new ModuleTexturedSlotArray(58, 16, this, chipSlot, chipSlot + 1, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleTexturedSlotArray(82, 16, this, chipCopySlot, chipCopySlot+1, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleProgress(75, 36, 0, new ProgressBarImage(217,0, 17, 17, 234, 0, EnumFacing.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleButton(40, 56, 0, "Build", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		modules.add(new ModuleButton(173, 3, 1, "", this, TextureResources.buttonCopy, LibVulpes.proxy.getLocalizedString("msg.satbuilder.writesecondchip"), 24, 24));

		return modules;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(world.isRemote)
			PacketHandler.sendToServer(new PacketMachine(this, (byte)(buttonId + 100)) );

		if(buttonId == 0) {
			if(canAssembleSatellite())
				assembleSatellite();
		}
		else if(buttonId == 1)
			if(canCopy())
				copyChip();

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
	public boolean hasCustomName() {
		return inventory.hasCustomName();
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(pos) < 4192;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		inventory.openInventory(player);
	}

	@Override
	public void closeInventory(EntityPlayer player) {
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		inventory.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		inventory.readFromNBT(nbt);
	}

	@Override
	public String getName() {
		return null;
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
	
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
}