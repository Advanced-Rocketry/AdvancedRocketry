package zmaster587.advancedRocketry.tile.Satellite;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.item.ItemOreScanner;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.item.ItemSatellite;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleOutputSlotArray;
import zmaster587.libVulpes.inventory.modules.ModulePower;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleTexturedSlotArray;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.EmbeddedInventory;

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

	public TileSatelliteBuilder() {
		inventory = new EmbeddedInventory(11);
		powerPerTick = 10;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	protected void processComplete() {
		super.processComplete();
		setInventorySlotContents(outputSlot, inventory.removeStackFromSlot(holdingSlot));
	}

	public boolean canAssembleSatellite() {

		//First make sure everything is a satellite part
		for(int i = 0; i < 7; i++) {
			ItemStack stack = getStackInSlot(i);
			if(stack != null && SatelliteRegistry.getSatelliteProperty(stack) == null)
				return false;
		}

		//Make sure critical parts exist and output is empty
		if(inventory.getStackInSlot(0) == null || inventory.getStackInSlot(holdingSlot) != null || inventory.getStackInSlot(outputSlot) != null || SatelliteRegistry.getSatelliteProperty(inventory.getStackInSlot(0)).getSatelliteType() == null)
			return false;

		String satType = SatelliteRegistry.getSatelliteProperty(getStackInSlot(0)).getSatelliteType();
		SatelliteBase sat = SatelliteRegistry.getSatallite(satType);
		
		//TODO: UNDEBUG if 0 power gen also return false
		return sat.isAcceptableControllerItemStack(inventory.getStackInSlot(chipSlot));
	}

	/**
	 * Assumes everything is in the proper place in the inventory to construct the satellite
	 * If unsure check canAssembleSatellite() first
	 */
	public void assembleSatellite() {
		int powerStorage = 0, powerGeneration = 0, maxData = 0;
		SatelliteProperties properties;

		String satType = SatelliteRegistry.getSatelliteProperty(inventory.getStackInSlot(0)).getSatelliteType();
		SatelliteBase sat = SatelliteRegistry.getSatallite(satType);
		for(int i = 0; i < 7; i++) {
			ItemStack stack = getStackInSlot(i);
			if(stack != null) {
				properties = SatelliteRegistry.getSatelliteProperty(stack);

				if(!sat.acceptsItemInConstruction(stack))
					continue;
				
				powerStorage += properties.getPowerStorage();
				powerGeneration += properties.getPowerGeneration();
				maxData += properties.getMaxDataStorage();

				decrStackSize(i, 1);
			}
		}
		if(!worldObj.isRemote) {
			//Set final satellite properties
			properties = new SatelliteProperties(powerGeneration, powerStorage, satType,maxData);
			properties.setId(DimensionManager.getInstance().getNextSatelliteId());

			//Create the output item
			ItemSatellite satItem = (ItemSatellite)AdvancedRocketryItems.itemSatellite;
			ItemStack output = new ItemStack(satItem);
			satItem.setSatellite(output, properties);

			//Set the ID chip
			inventory.setInventorySlotContents(chipSlot, sat.getContollerItemStack(inventory.getStackInSlot(chipSlot), properties));

			inventory.setInventorySlotContents(holdingSlot, output);
		}

		completionTime = 100;
	}

	private boolean canCopy() {
		ItemStack stack0 = getStackInSlot(chipSlot);
		ItemStack stack1 = getStackInSlot(chipCopySlot);
		getStackInSlot(outputSlot);

		boolean chipsExist = stack0 != null && stack1 != null;
		if(!chipsExist)
			return false;
		boolean isSatellite = ((stack0.getItem() instanceof ItemSatellite || stack0.getItem() instanceof ItemSatelliteIdentificationChip) && stack1.getItem().equals(stack0.getItem()));
		boolean isStation = stack0.getItem() instanceof ItemStationChip && stack0.getItemDamage() != 0 && stack1.getItem() instanceof ItemStationChip;
		boolean isPlanet = (stack0.getItem() instanceof ItemPlanetIdentificationChip && stack1.getItem() instanceof ItemPlanetIdentificationChip);
		boolean isOreScanner = (stack0.getItem() instanceof ItemOreScanner && stack1.getItem() instanceof ItemOreScanner);
		return !isRunning() && getStackInSlot(outputSlot) == null && (isStation || stack0.hasTagCompound()) && 
				(isSatellite  || isStation || isPlanet || isOreScanner);
	}

	private void copyChip() {

		ItemStack slot0 = getStackInSlot(chipSlot);
		ItemStack slot1 = getStackInSlot(chipCopySlot);

		if(slot0.getItem() instanceof ItemSatelliteIdentificationChip || slot0.getItem() instanceof ItemOreScanner || slot0.getItem() instanceof ItemPlanetIdentificationChip || slot0.getItem() instanceof ItemStationChip) {
			inventory.setInventorySlotContents(holdingSlot, getStackInSlot(chipSlot).copy());
		}
		else {
			ItemSatellite satelliteItem = (ItemSatellite)slot0.getItem();

			ItemSatelliteIdentificationChip itemIdChip = (ItemSatelliteIdentificationChip)slot1.getItem();

			itemIdChip.setSatellite(slot1, satelliteItem.getSatellite(slot0));
			inventory.setInventorySlotContents(holdingSlot, slot1);
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
		modules.add(new ModuleTexturedSlotArray(152, 10, inventory, 0, 1, TextureResources.functionComponent)); //Functional Piece
		modules.add(new ModuleTexturedSlotArray(116, 30, inventory, 1, 4, TextureResources.powercomponent));  //Generators
		modules.add(new ModuleTexturedSlotArray(116, 50, inventory, 4, 7, TextureResources.ioSlot));
		modules.add(new ModuleOutputSlotArray(58, 36, inventory, 7, 8));   // Output
		modules.add(new ModuleTexturedSlotArray(58, 16, inventory, chipSlot, chipSlot + 1, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleTexturedSlotArray(82, 16, inventory, chipCopySlot, chipCopySlot+1, TextureResources.idChip)); 	// Id chip
		modules.add(new ModuleProgress(75, 36, 0, new ProgressBarImage(217,0, 17, 17, 234, 0, EnumFacing.DOWN, TextureResources.progressBars), this));
		modules.add(new ModuleButton(40, 56, 0, "Build", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		modules.add(new ModuleButton(173, 3, 1, "", this, TextureResources.buttonCopy, "Write to Secondary Chip", 24, 24));

		return modules;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(worldObj.isRemote)
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
	public boolean hasCustomName() {
		return inventory.hasCustomName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
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
		return inventory.isItemValidForSlot(slot, stack);
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